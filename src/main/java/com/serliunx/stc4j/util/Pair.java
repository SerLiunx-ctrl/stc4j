package com.serliunx.stc4j.util;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 键值对定义; 简易的键值对实现, 避免使用复杂的Map.
 *
 * <p>
 *     对左右两边的值没有强制要求, 可以都为空; 适用于需要表达多个映射关系的场景中.
 * </p>
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.0
 * @since 2025/8/27
 */
public interface Pair<L, R> {

    /**
     * 设置左边的值
     *
     * @param left  左值
     */
    void sl(L left);

    /**
     * 设置右边的值
     *
     * @param right 右值
     */
    void sr(R right);

    /**
     * 获取左值
     *
     * @return  左值
     */
    L l();

    /**
     * 获取右值
     *
     * @return  右值
     */
    R r();

    /**
     * 快速构建一个键值对
     *
     * @param left  左值
     * @param right 右值
     * @return  新的键值对
     * @param <L>   左值类型
     * @param <R>   右值类型
     */
    static <L, R> Pair<L, R> of(L left, R right) {
        return new DefaultImpl<>(left, right);
    }

    /**
     * 将Map中的键值对提取为普通键值对列表
     *
     * @param source    原Map
     * @return          键值对列表
     * @param <L>       左值类型
     * @param <R>       右值类型
     */
    static <L, R> List<Pair<L, R>> extract(Map<L, R> source) {
        return source.entrySet()
                .stream()
                .map(entry -> new DefaultImpl<>(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    class DefaultImpl<L, R> implements Pair<L, R> {

        private L left;
        private R right;

        public DefaultImpl(L left, R right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public void sl(L left) {
            this.left = left;
        }

        @Override
        public void sr(R right) {
            this.right = right;
        }

        @Override
        public L l() {
            return left;
        }

        @Override
        public R r() {
            return right;
        }
    }
}
