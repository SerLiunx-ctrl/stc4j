package com.serliunx.stc4j.thread.executor;

import java.util.concurrent.ExecutorService;

/**
 * 可重复使用的单线程的线程池
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.0
 * @since 2025/4/17
 */
public interface ReusableThreadExecutor extends ExecutorService {

    /**
     * 获取内置线程
     *
     * @return  内置运行任务的线程
     */
    Thread getThread();

    /**
     * 获取线程池状态
     *
     * @return  状态
     */
    int getStatus();

    /**
     * 获取当前线程池已执行的任务数量
     *
     * @return  已执行的任务数量
     */
    long getTasksExecuted();
}
