package com.serliunx.stc4j.thread.support;

import com.serliunx.stc4j.thread.executor.ReusableThreadExecutor;
import com.serliunx.stc4j.thread.executor.TaskRejectionHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 合并的拒绝策略, 解决本框架自带的线程池和JDK中的线程池拒绝策略的参数不一致问题。
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.0
 * @since 2025/4/21
 * @see RejectedExecutionHandler
 * @see TaskRejectionHandler
 * @see ReusableThreadExecutor
 */
@FunctionalInterface
public interface MergedRejectedExecutionHandler extends RejectedExecutionHandler, TaskRejectionHandler {

    @Override
    default void reject(Runnable task, ReusableThreadExecutor rte) {
        mergedRejectedExecution(task, rte);
    }

    @Override
    default void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        mergedRejectedExecution(r, executor);
    }

    /**
     * 新的拒绝策略逻辑, 以参数中二者的共有父接口 {@link ExecutorService} 为准
     *
     * @param r     任务
     * @param es    线程池
     */
    void mergedRejectedExecution(Runnable r, ExecutorService es);
}
