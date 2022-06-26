package com.qingtianblog.task;

/**
 * @author Guank
 * @version 1.0
 * @description: 任务接口
 * @date 2022/6/12 8:58
 */
public interface IFutureTask<T> {

    /**
     * 执行任务
     */
    T doTask();
}
