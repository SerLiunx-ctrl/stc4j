package com.serliunx.stc4j.thread.executor;

import java.util.Collection;
import java.util.concurrent.*;

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

    @Override
    <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException;

    @Override
    <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException,
            ExecutionException, TimeoutException;
}
