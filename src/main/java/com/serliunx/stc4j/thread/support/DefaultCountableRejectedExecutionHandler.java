package com.serliunx.stc4j.thread.support;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 附带计数的拒绝策略默认实现(丢弃任务并计数)
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.0
 * @since 2025/4/16
 */
public final class DefaultCountableRejectedExecutionHandler implements CountableRejectedExecutionHandler {

    /**
     * 计数器
     */
    private final AtomicLong counter = new AtomicLong(0);

    /**
     * 最后一次被拒绝的任务
     */
    private volatile Runnable last = null;

    @Override
    public long getCount() {
        return counter.get();
    }

    @Override
    public Runnable getLastRejectedTask() {
        return last;
    }

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        last = r;
        counter.incrementAndGet();
    }
}
