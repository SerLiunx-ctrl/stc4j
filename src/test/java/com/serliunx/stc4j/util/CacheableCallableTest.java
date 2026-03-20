package com.serliunx.stc4j.util;

import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

/**
 * {@link CacheableCallable} 单元测试
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.5
 * @since 2026/3/18
 */
public class CacheableCallableTest {

    @Test
    public void testBuildRejectsInvalidArguments() {
        assertThrows(IllegalArgumentException.class, () -> CacheableCallable.build(null, 1, TimeUnit.SECONDS));
        assertThrows(IllegalArgumentException.class, () -> CacheableCallable.build(() -> "ok", 0, TimeUnit.SECONDS));
    }

    @Test
    public void testCallReturnsCachedValueWithinTimeToLive() throws Exception {
        AtomicInteger invokeCount = new AtomicInteger();
        Callable<String> callable = CacheableCallable.build(() -> "value-" + invokeCount.incrementAndGet(),
                1, TimeUnit.SECONDS);

        assertEquals("value-1", callable.call());
        assertEquals("value-1", callable.call());
        assertEquals("value-1", callable.call());
        assertEquals(1, invokeCount.get());
    }

    @Test
    public void testCallRefreshesExpiredValueAndCachesItAgain() throws Exception {
        AtomicInteger invokeCount = new AtomicInteger();
        Callable<String> callable = CacheableCallable.build(() -> "value-" + invokeCount.incrementAndGet(),
                80, TimeUnit.MILLISECONDS);

        assertEquals("value-1", callable.call());

        TimeUnit.MILLISECONDS.sleep(120);

        assertEquals("value-2", callable.call());
        assertEquals("value-2", callable.call());
        assertEquals(2, invokeCount.get());
    }
}
