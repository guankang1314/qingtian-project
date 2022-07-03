package com.qingtianblog.scheduled;

import com.alibaba.fastjson.JSON;
import com.qingtianblog.constant.ErrorMessageEnum;
import com.qingtianblog.constant.RedisQueueKey;
import com.qingtianblog.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RList;
import org.redisson.api.RLock;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Guank
 * @version 1.0
 * @description: 定时任务 搬运延迟任务进入 readyQueue
 * @date 2022/7/3 17:11
 */
@Slf4j
@Component
public class CarryJobScheduled {

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 定时任务搬运 job 信息 每秒进行扫描
     */
    @Scheduled(cron = "*/1 * * * * *")
    public void carryJobToQueue() {
        log.info("------------------------- carryJobToQueue ------------------------------");
        RLock lock = redissonClient.getLock(RedisQueueKey.CARRY_THREAD_LOCK);
        try {
            boolean lockFlag = lock.tryLock(RedisQueueKey.LOCK_WAIT_TIME, RedisQueueKey.LOCK_RELEASE_TIME, TimeUnit.SECONDS);
            if (!lockFlag) {
                throw new BusinessException(ErrorMessageEnum.ACQUIRE_LOCK_FAIL);
            }
            RScoredSortedSet<Object> delayBucket = redissonClient.getScoredSortedSet(RedisQueueKey.RD_ZSET_BUCKET_PRE);
            long now = System.currentTimeMillis();
            // 找出时间区间内的任务
            Collection<Object> jobCollection = delayBucket.valueRange(0, false, now, true);
            List<String> jobList = jobCollection.stream().map(String::valueOf).collect(Collectors.toList());
            RList<Object> readyQueue = redissonClient.getList(RedisQueueKey.RD_LIST_TOPIC_PRE);
            readyQueue.addAll(jobList);
            log.info("carry job to ready queue successful : [{}] , Time is : [{}]", JSON.toJSONString(jobList), new Date());
            // 在 bucket 删除任务
            delayBucket.removeAllAsync(jobList);
        }catch (Exception e) {
            log.error("carry job to queue error",e);
        }finally {
            if (null != lock) {
                lock.unlock();
            }
        }
    }
}
