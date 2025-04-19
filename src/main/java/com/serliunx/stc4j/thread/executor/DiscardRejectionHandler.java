package com.serliunx.stc4j.thread.executor;

/**
 * 拒绝策略之：丢弃
 *
 * <p>
 * 任务被拒绝时直接丢弃任务, 什么都不做。 思路源自{@link java.util.concurrent.ThreadPoolExecutor.DiscardPolicy}
 * <p>
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.0
 * @since 2025/4/18
 */
public final class DiscardRejectionHandler implements TaskRejectionHandler {

    /**
     * 单例实现
     */
    private static final DiscardRejectionHandler INSTANCE = new DiscardRejectionHandler();

    /**
     * 获取实例
     */
    public static DiscardRejectionHandler instance() {
        return INSTANCE;
    }

    @Override
    public void reject(Runnable task, ReusableThreadExecutor rte) {
        // 什么都不做
    }
}
