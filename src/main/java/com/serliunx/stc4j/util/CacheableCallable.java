package com.serliunx.stc4j.util;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 可缓存的 {@link Callable}。
 * <p>
 * 缓存计算结果，过期后重新执行，并刷新新的过期时间。
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.5
 * @since 2026/3/18
 */
public class CacheableCallable<V> implements Callable<V> {

    /**
     * 实际执行逻辑。
     */
    private final Callable<V> delegate;
    /**
     * 缓存存活时长，单位为纳秒。
     */
    private final long ttlNanos;

    /**
     * 当前缓存值。
     */
    private volatile V value = null;
    /**
     * 当前缓存的过期时间点。
     */
    private volatile long expiresAt;

    public CacheableCallable(Callable<V> delegate, long ttl, TimeUnit ttlUnit) {
        Assert.isTrue(ttl > 0, "timeout must be greater than 0 !");
        Assert.notNull(delegate, "delegate must not be null!");
        this.delegate = delegate;
        this.ttlNanos = ttlUnit.toNanos(ttl);
        this.expiresAt = System.nanoTime() + ttlNanos;
    }

    @Override
    public V call() throws Exception {
        final long now = System.nanoTime();
        if (value == null || now > this.expiresAt) {
            value = delegate.call();
            expiresAt = System.nanoTime() + ttlNanos;
            return value;
        }
        return value;
    }

    /**
     * 构建可缓存的 {@link Callable}。
     *
     * @param delegate 实际需要执行的 {@link Callable}
     * @param ttl 存活时间
     * @param ttlUnit 存活时间单位
     * @param <V> 值类型
     * @return 可缓存的 {@link Callable}
     */
    public static <V> Callable<V> build(Callable<V> delegate, long ttl, TimeUnit ttlUnit) {
        return new CacheableCallable<>(delegate, ttl, ttlUnit);
    }
}
