package com.serliunx.stc4j.thread.executor;

/**
 * 单线程线程池任务提交时的拒绝策略
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.0
 * @since 2025/4/18
 */
@FunctionalInterface
public interface TaskRejectionHandler {

    /**
     * 拒绝任务
     *
     * @param task  任务对象
     * @param rte   单线程的线程池
     */
    void reject(Runnable task, ReusableThreadExecutor rte);
}
