package com.serliunx.stc4j.thread.executor;

/**
 * 拒绝策略之：调用者运行
 *
 * <p>
 * 任务被拒绝时由提交任务的线程去执行被拒绝的任务, 思路源自{@link java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy}
 * <p>
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.0
 * @since 2025/4/18
 */
public final class CallerRunsRejectionHandler implements TaskRejectionHandler {

    /**
     * 单例实现
     */
    private static final CallerRunsRejectionHandler INSTANCE = new CallerRunsRejectionHandler();

    /**
     * 获取实例
     */
    public static CallerRunsRejectionHandler instance() {
        return INSTANCE;
    }

    @Override
    public void reject(Runnable task, ReusableThreadExecutor rte) {
        task.run();
    }
}
