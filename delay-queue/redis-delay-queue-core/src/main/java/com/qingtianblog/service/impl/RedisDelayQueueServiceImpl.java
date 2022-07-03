package com.qingtianblog.service.impl;

import com.qingtianblog.constant.ErrorMessageEnum;
import com.qingtianblog.constant.RedisQueueKey;
import com.qingtianblog.exception.BusinessException;
import com.qingtianblog.model.Job;
import com.qingtianblog.model.JobDie;
import com.qingtianblog.service.RedisDelayQueueService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author Guank
 * @version 1.0
 * @description: 提供任务的增加和删除
 * @date 2022/7/3 15:33
 */
@Service
@Slf4j
public class RedisDelayQueueServiceImpl implements RedisDelayQueueService {


    /**
     * 分布式锁
     */
    @Autowired
    private RedissonClient redissonClient;

    /**
     * 添加延迟任务
     * @param job
     */
    @Override
    public void addJob(Job job) {
        RLock lock = redissonClient.getLock(RedisQueueKey.ADD_JOB_LOCK + job.getJobId());
        try {
            boolean lockFlag = lock.tryLock(RedisQueueKey.LOCK_WAIT_TIME, RedisQueueKey.LOCK_RELEASE_TIME, TimeUnit.SECONDS);
            if (!lockFlag) {
                throw new BusinessException(ErrorMessageEnum.ACQUIRE_LOCK_FAIL);
            }
            String topicId = RedisQueueKey.getTopicId(job.getTopic(), job.getJobId());

            // 将 job 添加到 JobPool 中
            RMap<String, Job> jobPool = redissonClient.getMap(RedisQueueKey.JOB_POOL_KEY);
            if (jobPool.containsKey(topicId)) {
                throw new BusinessException(ErrorMessageEnum.JOB_ALREADY_EXIST);
            }

            jobPool.put(topicId, job);

            //将 job 放入 delayBucket 中
            RScoredSortedSet<Object> delayBucket = redissonClient.getScoredSortedSet(RedisQueueKey.RD_ZSET_BUCKET_PRE);
            delayBucket.add(job.getDelay(),topicId);
            log.info("add job successful : [{}]",topicId);
        }catch (Exception e) {
            log.error("add job error ",e);
        }finally {
            if (null != lock) {
                lock.unlock();
            }
        }
    }


    /**
     * 删除延迟任务
     * @param jobDie
     */
    @Override
    public void deleteJob(JobDie jobDie) {
        RLock lock = redissonClient.getLock(RedisQueueKey.DELETE_JOB_LOCK + jobDie.getJobId());
        try {
            boolean lockFlag = lock.tryLock(RedisQueueKey.LOCK_WAIT_TIME, RedisQueueKey.LOCK_RELEASE_TIME, TimeUnit.SECONDS);
            if (!lockFlag) {
                throw new BusinessException(ErrorMessageEnum.ACQUIRE_LOCK_FAIL);
            }
            String topicId = RedisQueueKey.getTopicId(jobDie.getTopic(), jobDie.getJobId());
            RMap<String, Job>  jobPool = redissonClient.getMap(RedisQueueKey.JOB_POOL_KEY);
            jobPool.remove(topicId);

            RScoredSortedSet<Object> delayBucket = redissonClient.getScoredSortedSet(RedisQueueKey.RD_ZSET_BUCKET_PRE);
            delayBucket.remove(topicId);
            log.info("delete job successful : [{}]",topicId);
        }catch (Exception e) {
            log.error("delete job error : [{}]",jobDie.getTopic() + jobDie.getJobId());
        }finally {
            if (null != lock) {
                lock.unlock();
            }
        }
    }
}
