package com.serliunx.stc4j.thread.executor;

import com.serliunx.stc4j.thread.support.DefaultIndexCountingThreadFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

/**
 * 可重复使用的单线程的线程池默认实现
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.0
 * @since 2025/4/17
 */
public class DefaultReusableThreadExecutor implements ReusableThreadExecutor {

    // ====== 构造参数 ======

    /**
     * 任务队列
     */
    private final BlockingQueue<Runnable> queue;
    /**
     * 线程工厂
     */
    private final ThreadFactory threadFactory;
    /**
     * 拒绝策略
     */
    private final TaskRejectionHandler rejectionHandler;

    /**
     * 任务执行数量
     */
    private long tasksExecuted = 0;

    /**
     * 线程池状态
     */
    private final AtomicInteger status = new AtomicInteger(STATUS_NEW);
    /**
     * 线程池锁
     */
    private final Lock mainLock = new ReentrantLock();

    /**
     * 状态：初始化
     *
     * <li> 线程池初始化中, 还未尝试从队列中提取任务
     */
    private static final int STATUS_NEW = 0;
    /**
     * 状态：空闲中
     *
     * <li> 线程池初始化完毕, 已经开始从队列中提取任务, 但还未有可用的任务
     */
    private static final int STATUS_IDLE = 1;
    /**
     * 状态：运行中
     *
     * <li> 正在运行任务
     */
    private static final int STATUS_RUNNING = 2;
    /**
     * 状态：关闭
     *
     * <li> 线程池调用了 {@link #shutdown()} 之后, 但还未完全关闭, 正在执行剩余的任务
     * <li> 此时不再接收新的任务
     */
    private static final int STATUS_SHUTDOWN = 3;
    /**
     * 状态：终止
     *
     * <li> 关闭且所有任务已执行完毕
     * <li> 此时不再接收新的任务
     */
    private static final int STATUS_TERMINATED = 4;

    public DefaultReusableThreadExecutor(BlockingQueue<Runnable> queue,
                                         ThreadFactory threadFactory,
                                         TaskRejectionHandler rejectionHandler) {
        this.queue = queue;
        this.threadFactory = threadFactory;
        this.rejectionHandler = rejectionHandler;

        // 初始化
        start();
    }

    public DefaultReusableThreadExecutor(BlockingQueue<Runnable> queue) {
        this(queue, new DefaultIndexCountingThreadFactory("single-thread-pool-executor", 1),
                AbortRejectionHandler.instance());
    }

    /**
     * 执行任务的线程
     */
    private volatile Thread thread;

    @Override
    public Thread getThread() {
        return ReadonlyThread.wrap(thread);
    }

    @Override
    public int getStatus() {
        return status.get();
    }

    @Override
    public long getTasksExecuted() {
        return tasksExecuted;
    }

    @Override
    public void shutdown() {
        if (getStatus() >= STATUS_SHUTDOWN)
            return;
        status.set(STATUS_SHUTDOWN);
    }

    @Override
    public List<Runnable> shutdownNow() {
        if (isTerminated()) {
            return Collections.emptyList();
        }
        try {
            mainLock.lock();
            if (isTerminated()) {
                return Collections.emptyList();
            }
            thread.interrupt();
            List<Runnable> tasks = new ArrayList<>();
            queue.drainTo(tasks);
            return tasks;
        } finally {
            mainLock.unlock();
        }
    }

    @Override
    public boolean isShutdown() {
        return status.get() >= STATUS_SHUTDOWN;
    }

    @Override
    public boolean isTerminated() {
        return status.get() >= STATUS_TERMINATED;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        // TODO
        return false;
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        FutureTask<T> futureTask = new FutureTask<>(task);
        execute(futureTask);
        return futureTask;
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return submit(new CallableAdapter<>(task, result));
    }

    @Override
    public Future<?> submit(Runnable task) {
        return submit(task, null);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        if (tasks == null) {
            throw new NullPointerException();
        }

        List<Future<T>> futures = new ArrayList<>(tasks.size());
        try {
            for (Callable<T> t : tasks) {
                RunnableFuture<T> f = new FutureTask<>(t);
                futures.add(f);
                execute(f);
            }

            for (Future<T> f : futures) {
                if (!f.isDone()) {
                    try {
                        f.get();
                    } catch (CancellationException | ExecutionException ignore) {}
                }
            }
            return futures;
        } catch (Throwable t) {
            futures.forEach(f -> f.cancel(true));
            throw t;
        }
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
            throws InterruptedException {

        if (tasks == null) {
            throw new NullPointerException();
        }

        final long nanos = unit.toNanos(timeout);
        final long deadline = System.nanoTime() + nanos;
        List<Future<T>> futures = new ArrayList<>(tasks.size());
        int j = 0;
        timedOut: try {
            for (Callable<T> t : tasks) {
                futures.add(new FutureTask<>(t));
            }

            final int size = futures.size();

            for (int i = 0; i < size; i++) {
                if (((i == 0) ? nanos : deadline - System.nanoTime()) <= 0L) {
                    break timedOut;
                }
                execute((Runnable)futures.get(i));
            }

            for (; j < size; j++) {
                Future<T> f = futures.get(j);
                if (!f.isDone()) {
                    try {
                        f.get(deadline - System.nanoTime(), NANOSECONDS);
                    } catch (CancellationException | ExecutionException ignore) {
                      // 什么都不做
                    } catch (TimeoutException timedOut) {
                        break timedOut;
                    }
                }
            }
            return futures;
        } catch (Throwable t) {
            futures.forEach(f -> f.cancel(true));
            throw t;
        }

        // 取消剩余的任务
        for (int size = futures.size(); j < size; j++) {
            futures.get(j).cancel(true);
        }
        return futures;
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        try {
            return doInvokeAny(tasks, false, 0);
        } catch (TimeoutException cannotHappen) {
            throw new RuntimeException(cannotHappen);
        }
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws
            InterruptedException, ExecutionException, TimeoutException {
        return doInvokeAny(tasks, true, unit.toNanos(timeout));
    }

    @Override
    public void execute(Runnable command) {
        if (command == null) {
            throw new NullPointerException();
        }
        if (isShutdown() ||
                !queue.offer(command)) {
            rejectionHandler.reject(command, this);
        }
    }

    @Override
    public String toString() {
        try {
            mainLock.lock();
			return "[DefaultReusableThreadExecutor, queueSize=" + queue.size() +
                    ", tasksExecuted=" + tasksExecuted + ", status=" + status.get() + "]";
        } finally {
            mainLock.unlock();
        }
    }

    /**
     * 任务开始执行前的回调
     *
     * @param t     任务线程
     * @param task  任务对象
     */
    protected void beforeExecute(Thread t, Runnable task) {}

    /**
     * 任务结束后的回调
     *
     * @param task  任务对象
     * @param t     异常信息(没有异常时, 值为空)
     */
    protected void afterExecute(Runnable task, Throwable t) {}

    /**
     * 启动线程
     */
    private void start() {
        if (isShutdown()) {
            return;
        }
        Runnable mainLogic = () -> {
            boolean completedAbruptly = false;
            try {
                Runnable task;
                status.set(STATUS_IDLE);
                while ((task = getTask()) != null &&
                        (!isTerminated())) {
                    try {
                        status.set(STATUS_RUNNING);
                        beforeExecute(thread, task);
                        task.run();
                        afterExecute(task, null);
                    } catch (Throwable t) {
                        completedAbruptly = true;
                        afterExecute(task, t);
                        throw t;
                    } finally {
                        if (getStatus() < STATUS_SHUTDOWN) {
                            status.set(STATUS_IDLE);
                        }
                        tasksExecuted++;
                    }

                    if (isShutdown() && queue.isEmpty()) {
                        break;
                    }
                }
            } finally {
                if (completedAbruptly) {
                    start();
                } else {
                    status.set(STATUS_TERMINATED);
                }
            }
        };

        // 运行线程
        thread = threadFactory.newThread(mainLogic);
        thread.start();
    }

    /**
     * 从队列中获取任务
     */
    private Runnable getTask() {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            return null;
        }
    }

    /**
     * 仅完成一个任务就返回, 源自 {@link ThreadPoolExecutor} 中的逻辑
     */
    private <T> T doInvokeAny(Collection<? extends Callable<T>> tasks,
                              boolean timed, long nanos)
            throws InterruptedException, ExecutionException, TimeoutException {
        if (tasks == null)
            throw new NullPointerException();
        int ntasks = tasks.size();
        if (ntasks == 0)
            throw new IllegalArgumentException();
        ArrayList<Future<T>> futures = new ArrayList<>(ntasks);
        ExecutorCompletionService<T> ecs =
                new ExecutorCompletionService<T>(this);

        try {
            ExecutionException ee = null;
            final long deadline = timed ? System.nanoTime() + nanos : 0L;
            Iterator<? extends Callable<T>> it = tasks.iterator();

            futures.add(ecs.submit(it.next()));
            --ntasks;
            int active = 1;

            for (;;) {
                Future<T> f = ecs.poll();
                if (f == null) {
                    if (ntasks > 0) {
                        --ntasks;
                        futures.add(ecs.submit(it.next()));
                        ++active;
                    }
                    else if (active == 0)
                        break;
                    else if (timed) {
                        f = ecs.poll(nanos, NANOSECONDS);
                        if (f == null)
                            throw new TimeoutException();
                        nanos = deadline - System.nanoTime();
                    }
                    else
                        f = ecs.take();
                }
                if (f != null) {
                    --active;
                    try {
                        return f.get();
                    } catch (ExecutionException eex) {
                        ee = eex;
                    } catch (RuntimeException rex) {
                        ee = new ExecutionException(rex);
                    }
                }
            }

            if (ee == null)
                ee = new ExecutionException(new RuntimeException());
            throw ee;

        } finally {
            futures.forEach(f -> f.cancel(true));
        }
    }

    private static final class CallableAdapter<T> implements Callable<T> {

        private final Runnable r;
        private final T t;

        public CallableAdapter(Runnable r, T t) {
            this.r = r;
            this.t = t;
        }

        @Override
        public T call() throws Exception {
            r.run();
            return t;
        }
    }

    /**
     * 线程类封装之只读线程
     *
     * <li> 仅允许查看关键信息, 不允许额外操作
     */
    private static final class ReadonlyThread extends Thread {

        @Override
        public void start() {
            throw new UnsupportedOperationException("Readonly thread is not supported");
        }

        @Override
        public void run() {
            throw new UnsupportedOperationException("Readonly thread is not supported");
        }

        @Override
        public void interrupt() {
            throw new UnsupportedOperationException("Readonly thread is not supported");
        }

        @Override
        public boolean isInterrupted() {
            return super.isInterrupted();
        }

        @Override
        public void setContextClassLoader(ClassLoader cl) {
            throw new UnsupportedOperationException("Readonly thread is not supported");
        }

        @Override
        public UncaughtExceptionHandler getUncaughtExceptionHandler() {
            return super.getUncaughtExceptionHandler();
        }

        @Override
        public void setUncaughtExceptionHandler(UncaughtExceptionHandler ueh) {
            throw new UnsupportedOperationException("Readonly thread is not supported");
        }

        /**
         * 将一个线程封装成只读线程
         *
         * @param original  原始线程
         * @return  封装后的只读线程
         */
        static ReadonlyThread wrap(Thread original) {
            if (original instanceof ReadonlyThread) {
                return (ReadonlyThread) original;
            }

            return null;
        }
    }
}
