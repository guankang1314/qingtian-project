package com.qingtianblog.util;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Guank
 * @version 1.0
 * @description: ThreadPoolUtils
 * @date 2022/6/14 8:01
 */
public class ThreadPoolUtils {

    private static volatile ThreadPoolTaskExecutor singleton;

    private ThreadPoolUtils (){

    }

    public static ThreadPoolTaskExecutor getInstance() {
        if (null == singleton) {
            synchronized (ThreadPoolUtils.class) {
                if (null == singleton) {
                    singleton = new ThreadPoolTaskExecutor();
                    singleton.setCorePoolSize(10);
                    singleton.setMaxPoolSize(20);
                    singleton.setQueueCapacity(20);
                    singleton.setKeepAliveSeconds(60);
                    singleton.setThreadNamePrefix("thread-pool");
                    singleton.setWaitForTasksToCompleteOnShutdown(true);
                    singleton.setAwaitTerminationSeconds(60);
                    singleton.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
                    singleton.initialize();
                }
            }
        }
        return singleton;
    }

}
