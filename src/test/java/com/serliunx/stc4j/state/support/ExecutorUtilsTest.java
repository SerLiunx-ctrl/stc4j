package com.serliunx.stc4j.state.support;

import org.junit.Test;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * 单元测试 {@link ExecutorUtils}.
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @since 2026/3/16
 */
public class ExecutorUtilsTest {

    @Test
    public void testAdaptiveThreadPoolUsesExpectedSizingAndHandler() {
        RejectedExecutionHandler handler = (r, executor) -> {};
        ThreadPoolExecutor executor = (ThreadPoolExecutor) ExecutorUtils.adaptiveThreadPool(handler);
        int processors = Runtime.getRuntime().availableProcessors();

        try {
            assertEquals(processors * 2, executor.getCorePoolSize());
            assertEquals(processors * 4, executor.getMaximumPoolSize());
            assertSame(handler, executor.getRejectedExecutionHandler());
            assertTrue(executor.getQueue().remainingCapacity() <= processors * 8);
        } finally {
            executor.shutdownNow();
        }
    }
}
