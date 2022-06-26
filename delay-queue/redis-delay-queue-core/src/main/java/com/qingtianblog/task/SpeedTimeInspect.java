package com.qingtianblog.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

import java.util.function.Supplier;

/**
 * @author Guank
 * @version 1.0
 * @description: 记录任务的运行时间
 * @date 2022/6/12 8:59
 */
@Slf4j
public class SpeedTimeInspect {

    private SpeedTimeInspect() {}

    /**
     * 执行任务并打印时间
     * @param supplier 执行方法
     * @param title 标题
     * @return <T> 返回值类型
     * @param <T> 返回值类型
     */
    public static<T> T warp(Supplier<T> supplier, String title) {
        log.info("speed_time_log : [{}], start to run",title);
        StopWatch watch = new StopWatch();
        watch.start();
        T res = supplier.get();
        watch.stop();
        log.info("speed_time_log : [{}], completed in {} ms" , title,watch.getTotalTimeMillis());
        return res;
    }
}
