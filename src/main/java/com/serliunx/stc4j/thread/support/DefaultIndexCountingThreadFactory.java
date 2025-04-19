package com.serliunx.stc4j.thread.support;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 下标计数的线程工厂的默认实现
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.0
 * @since 2025/4/16
 */
public final class DefaultIndexCountingThreadFactory implements IndexCountingThreadFactory {

    /**
     * 名称模板, 如: common-task-%s
     */
    private final String namePattern;
    /**
     * 线程序号
     */
    private final AtomicInteger index;

    /**
     * 全参构造器
     *
     * @param namePattern   名称模板
     * @param startIndex    下标起始值
     */
    public DefaultIndexCountingThreadFactory(String namePattern, int startIndex) {
        this.namePattern = namePattern;
        this.index = new AtomicInteger(startIndex);
    }

    @Override
    public String getPattern() {
        return namePattern;
    }

    @Override
    public int getNextIndex() {
        return index.get();
    }

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r, String.format(namePattern, index.getAndIncrement()));
    }
}
