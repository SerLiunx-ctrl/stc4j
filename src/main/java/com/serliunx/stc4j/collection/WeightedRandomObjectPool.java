package com.serliunx.stc4j.collection;

/**
 * 带权重的随机对象池。
 * <p>
 * 每个对象在池中都有一个权重，随机获取时按权重比例抽取。
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.5
 * @since 2026/3/20
 */
public interface WeightedRandomObjectPool<E> extends RandomObjectPool<E> {

    /**
     * 以指定权重加入一个对象。
     *
     * @param object 对象
     * @param weight 权重，必须大于 0
     * @return 加入成功时返回真, 否则返回假
     */
    boolean add(E object, int weight);

    /**
     * 获取指定对象在池中的总权重。
     *
     * @param object 对象
     * @return 总权重，不存在时返回 0
     */
    int getWeight(E object);

    /**
     * 获取当前池中的总权重。
     *
     * @return 总权重
     */
    int totalWeight();
}
