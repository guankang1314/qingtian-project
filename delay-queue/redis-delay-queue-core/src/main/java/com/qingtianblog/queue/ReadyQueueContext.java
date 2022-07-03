package com.qingtianblog.queue;

import com.alibaba.fastjson.JSON;
import com.qingtianblog.constant.RedisQueueKey;
import com.qingtianblog.constant.RetryStrategyEnum;
import com.qingtianblog.model.Job;
import com.qingtianblog.service.ConsumerService;
import com.qingtianblog.task.TaskManager;
import com.qingtianblog.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * @author Guank
 * @version 1.0
 * @description: Ready Queue 上下文 Queue 中存放了准备好消费的任务 topicId 开始消费任务
 * @date 2022/7/3 17:33
 */
@Slf4j
@Component
public class ReadyQueueContext {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private ConsumerService consumerService;

    /**
     * 初始化时开启 TOPIC 消费线程
     */
    @PostConstruct
    public void startTopicConsumer() {
        TaskManager.doTask(this::runTopicThreads, "开启TOPIC消费线程");
    }

    /**
     * 消费 readyQueue 中准备好的任务 catch 所有可能出现的异常 不让打断
     */
    private void runTopicThreads() {
        while (true) {
            RLock lock = null;
            try {
                lock = redissonClient.getLock(RedisQueueKey.CONSUMER_TOPIC_LOCK);
            }catch (Exception e) {
                log.error("runTopicThreads getLock error",e);
            }
            try {
                if (null == lock) {
                    continue;
                }
                // 分布式锁时间比Blpop阻塞时间多1S，避免出现释放锁的时候，锁已经超时释放，unlock报错
                boolean lockFlag = lock.tryLock(RedisQueueKey.LOCK_WAIT_TIME, RedisQueueKey.LOCK_RELEASE_TIME, TimeUnit.SECONDS);
                if (!lockFlag) {
                    continue;
                }

                // 获取 readyQueue 中待消费的数据
                RBlockingQueue<String> queue = redissonClient.getBlockingQueue(RedisQueueKey.RD_LIST_TOPIC_PRE);
                String topicId = queue.poll(60, TimeUnit.SECONDS);
                if (StringUtils.isEmpty(topicId)) {
                    continue;
                }

                // 根据 topicId 获取任务信息
                RMap<String, Job> jobPool = redissonClient.getMap(RedisQueueKey.JOB_POOL_KEY);
                Job job = jobPool.get(topicId);

                // 消费任务
                FutureTask<Boolean> taskResult = TaskManager.doFutureTask(() ->
                                consumerService.consumeMessage(job.getUrl(), job.getBody()),
                        job.getTopic() + "-- consume job id --" + job.getJobId());
                if (taskResult.get()) {
                    //消费成功 删除信息 打印日志
                    log.info("delay task consume successful : [{}]", JSON.toJSONString(job));
                    jobPool.remove(topicId);
                }else {
                    //消费失败 根据重试策略 加入 delayBucket
                    int retrySum = job.getRetry() + 1;

                    // 重试册数大于 5 不再重试 此时应该有处理措施记录
                    if (retrySum > RetryStrategyEnum.RETRY_FIVE.getRetry()) {
                        jobPool.remove(topicId);
                        continue;
                    }
                    job.setRetry(retrySum);
                    // 设置下一次触发时间
                    long nextTime = job.getDelay() + RetryStrategyEnum.getDelayTime(job.getRetry()) * 1000;
                    log.info("task : [{}] next retry time is : [{}]",topicId, DateUtil.long2Str(nextTime));
                    // 放入 delayBucket 中
                    RScoredSortedSet<Object> delayBucket = redissonClient.getScoredSortedSet(RedisQueueKey.RD_ZSET_BUCKET_PRE);
                    delayBucket.add(nextTime,topicId);
                    // 更新任务重试次数
                    jobPool.put(topicId,job);
                }
            }catch (Exception e) {
                log.error("runTopicThreads error",e);
            }finally {
                if (null != lock) {
                    try {
                        lock.unlock();
                    }catch (Exception e) {
                        log.error("runTopicThreads unlock error",e);
                    }
                }
            }
        }
    }
}
