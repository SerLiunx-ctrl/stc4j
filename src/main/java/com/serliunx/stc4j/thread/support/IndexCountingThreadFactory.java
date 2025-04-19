package com.serliunx.stc4j.thread.support;

import java.util.concurrent.ThreadFactory;

/**
 * 下标计数的线程工厂, 如: common-task-%s
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.0
 * @since 2025/4/16
 */
public interface IndexCountingThreadFactory extends ThreadFactory {

    /**
     * 获取名称模板
     *
     * @return  名称模板
     */
    String getPattern();

    /**
     * 获取下一个线程序号
     *
     * @return  下一个线程的序号
     */
    int getNextIndex();
}
