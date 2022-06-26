package com.qingtianblog.task;

import com.qingtianblog.util.ThreadPoolUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.FutureTask;
import java.util.function.Supplier;

import static com.qingtianblog.task.SpeedTimeInspect.warp;

/**
 * @author Guank
 * @version 1.0
 * @description: 任务执行管理器
 * @date 2022/6/14 8:18
 */
@Slf4j
public class TaskManager {

    private TaskManager(){}

    private static final ThreadPoolTaskExecutor poolTaskExecutor = ThreadPoolUtils.getInstance();


    /**
     * 执行任务
     * @param task 任务
     * @param title 标题
     */
    public static void doTask(ITask task, String title) {
        poolTaskExecutor.execute(() -> warp((Supplier<Void>) () -> {
            try {
                task.doTask();
            }catch (Exception e) {
                log.error("TaskManager doTask execute error",e.getMessage(),e);
            }
            return null;
        },title));
    }


    /**
     * 带有返回值的 task
     * @param task
     * @param title
     * @return
     * @param <T>
     */
    public static <T> FutureTask<T> doFutureTask(final IFutureTask<T> task, String title) {
        FutureTask<T> futureTask = new FutureTask<>(() -> warp((task::doTask), title));
        poolTaskExecutor.execute(futureTask);
        return futureTask;
    }

}
