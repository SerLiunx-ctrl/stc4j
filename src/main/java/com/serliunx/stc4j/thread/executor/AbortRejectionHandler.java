package com.serliunx.stc4j.thread.executor;

import com.serliunx.stc4j.thread.support.MergedRejectedExecutionHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

/**
 * 拒绝策略之：抛出异常
 *
 * <p>
 * 任务被拒绝时抛出异常, 思路源自{@link java.util.concurrent.ThreadPoolExecutor.AbortPolicy}
 * <p>
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.0
 * @since 2025/4/18
 */
public final class AbortRejectionHandler implements MergedRejectedExecutionHandler {

    /**
     * 单例实现
     */
    private static final AbortRejectionHandler INSTANCE = new AbortRejectionHandler();

    /**
     * 获取实例
     */
    public static AbortRejectionHandler instance() {
        return INSTANCE;
    }

    @Override
    public void mergedRejectedExecution(Runnable r, ExecutorService es) {
        throw new RejectedExecutionException(r.toString() + " in " + es.toString());
    }
}
