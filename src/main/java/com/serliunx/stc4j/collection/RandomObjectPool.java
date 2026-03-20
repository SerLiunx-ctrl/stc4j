package com.serliunx.stc4j.collection;

import java.util.Collection;
import java.util.Random;

/**
 * 随机对象池
 * <p>
 * 默认会等概率从对象池中抽取一个和多个对象
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.5
 * @since 2026/3/19
 */
public interface RandomObjectPool<E> extends Collection<E> {

    /**
     * 随机获取一个对象
     * <p>
     * 采用默认随机策略{@link Random}
     *
     * @return 对象
     */
    E get();

    /**
     * 根据指定的随机器来随机抽取对象
     *
     * @param random 随机器
     * @return 对象
     */
    E get(Random random);
}
