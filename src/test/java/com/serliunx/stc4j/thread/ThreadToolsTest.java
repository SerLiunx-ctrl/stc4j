package com.serliunx.stc4j.thread;

import com.serliunx.stc4j.thread.executor.DefaultReusableThreadExecutor;
import com.serliunx.stc4j.thread.executor.DiscardRejectionHandler;
import com.serliunx.stc4j.thread.executor.ReusableThreadExecutor;
import com.serliunx.stc4j.thread.support.CountableRejectedExecutionHandler;
import com.serliunx.stc4j.thread.support.DefaultCountableRejectedExecutionHandler;
import com.serliunx.stc4j.thread.support.DefaultIndexCountingThreadFactory;
import com.serliunx.stc4j.thread.support.IndexCountingThreadFactory;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * 线程相关扩展测试
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.0
 * @since 2025/4/16
 */
public class ThreadToolsTest {

    private static final Logger log = LoggerFactory.getLogger(ThreadToolsTest.class);

    /**
     * 测试之 {@link CountableRejectedExecutionHandler}
     */
    @Test
    public void testCountableRejectedExecutionHandler() {
        CountableRejectedExecutionHandler handler = new DefaultCountableRejectedExecutionHandler();
    }

    /**
     * 测试之 {@link IndexCountingThreadFactory}
     */
    @Test
    public void testIndexCountingThreadFactory() {
        IndexCountingThreadFactory indexCountingThreadFactory =
                new DefaultIndexCountingThreadFactory("task-thread-%s", 1);

        System.out.println(indexCountingThreadFactory.getNextIndex());
        System.out.println(indexCountingThreadFactory.getPattern());

        ThreadPoolExecutor tpe = new ThreadPoolExecutor(1, 1,
                1000, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1), indexCountingThreadFactory);

        tpe.submit(() -> {
            log.info("1");
        });
    }

    /**
     * 测试之 {@link ReusableThreadExecutor}
     */
    @Test
    public void testReusableThreadExecutor() throws Exception {
        ReusableThreadExecutor rte = new DefaultReusableThreadExecutor(new ArrayBlockingQueue<>(16),
                new DefaultIndexCountingThreadFactory("task-thread-%s", 1), DiscardRejectionHandler.instance());

        rte.execute(() -> {
            log.info("hello~");
        });

        TimeUnit.SECONDS.sleep(1);
        System.out.println(rte);

        TimeUnit.SECONDS.sleep(1);
    }

    @Test
    public void testReusableThreadExecutorAwaitTerminationAfterShutdown() throws Exception {
        ReusableThreadExecutor rte = new DefaultReusableThreadExecutor(new ArrayBlockingQueue<>(16),
                new DefaultIndexCountingThreadFactory("await-thread-%s", 1), DiscardRejectionHandler.instance());

        rte.shutdown();

        assertTrue(rte.awaitTermination(1, TimeUnit.SECONDS));
        assertTrue(rte.isTerminated());
    }

    @Test
    public void testReusableThreadExecutorAwaitTerminationTimeout() throws Exception {
        ReusableThreadExecutor rte = new DefaultReusableThreadExecutor(new ArrayBlockingQueue<>(16),
                new DefaultIndexCountingThreadFactory("await-timeout-thread-%s", 1), DiscardRejectionHandler.instance());

        assertFalse(rte.awaitTermination(10, TimeUnit.MILLISECONDS));

        rte.shutdownNow();
        assertTrue(rte.awaitTermination(1, TimeUnit.SECONDS));
    }

    @Test
    public void testReusableThreadExecutorShutdownDoesNotInterruptRunningTask() throws Exception {
        ReusableThreadExecutor rte = new DefaultReusableThreadExecutor(new ArrayBlockingQueue<>(16),
                new DefaultIndexCountingThreadFactory("await-running-thread-%s", 1), DiscardRejectionHandler.instance());
        CountDownLatch started = new CountDownLatch(1);
        AtomicBoolean interrupted = new AtomicBoolean(false);

        rte.execute(() -> {
            started.countDown();
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                interrupted.set(true);
                Thread.currentThread().interrupt();
            }
        });

        assertTrue(started.await(1, TimeUnit.SECONDS));
        rte.shutdown();

        assertTrue(rte.awaitTermination(1, TimeUnit.SECONDS));
        assertFalse(interrupted.get());
    }
}
