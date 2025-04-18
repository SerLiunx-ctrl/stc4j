package com.serliunx.stc4j.thread;

import com.serliunx.stc4j.thread.executor.DefaultReusableThreadExecutor;
import com.serliunx.stc4j.thread.executor.DiscardRejectionHandler;
import com.serliunx.stc4j.thread.executor.ReusableThreadExecutor;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
                Thread::new, new DiscardRejectionHandler());

        rte.execute(() -> {
            throw new RuntimeException();
        });

        TimeUnit.SECONDS.sleep(60);
    }
}
