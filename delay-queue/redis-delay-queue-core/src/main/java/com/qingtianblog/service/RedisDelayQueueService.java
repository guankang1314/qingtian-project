package com.qingtianblog.service;

import com.qingtianblog.model.Job;
import com.qingtianblog.model.JobDie;

/**
 * @author Guank
 * @version 1.0
 * @description: 提供任务的 增加和删除操作
 * @date 2022/7/3 15:14
 */
public interface RedisDelayQueueService {

    /**
     * 添加延迟任务
     * @param job
     */
    void addJob(Job job);

    /**
     * 删除任务
     * @param jobDie
     */
    void deleteJob(JobDie jobDie);
}
