package com.serliunx.stc4j.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 键值对定义
 *
 * @author <a href="mailto:root@serliunx.com">SerLiunx</a>
 * @since 2025/8/30
 * @param <L> 左值
 * @param <R> 右值
 */
public interface Pair<L, R> {

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
     * 设置左值
     *
     * @param left  左值
     */
    void sl(L left);

    /**
     * 设置右值
     *
     * @param right 右值
     */
    void sr(R right);

    /**
     * 转换为Map, 左值为Key, 右值为Value
     *
     * @return  转换后的Map, 默认为HashMap
     */
    Map<L, R> map();

    /**
     * 快速创建一个键值对
     *
     * @param l     左值
     * @param r     右值
     * @return      键值对
     * @param <L>   左值类型
     * @param <R>   右值类型
     */
    static <L, R> Pair<L, R> of(L l, R r) {
        return new DefaultImpl<>(l, r);
    }

    /**
     * 将Map中的键值对提取出来
     *
     * @param map   源Map
     * @return      键值对列表
     * @param <L>   左值类型
     * @param <R>   右值类型
     */
    static <L, R> List<Pair<L, R>> extract(Map<L, R> map) {
        return map.entrySet()
                .stream()
                .map(e -> new DefaultImpl<>(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    /**
     * 键值对默认实现
     *
     * @param <L>   左值类型
     * @param <R>   右值类型
     */
    class DefaultImpl<L, R>  implements Pair<L, R> {

        private L left;
        private R right;

        public DefaultImpl(L left, R right) {
            this.left = left;
            this.right = right;
        }

        public DefaultImpl() {}

        @Override
        public L l() {
            return left;
        }

        @Override
        public R r() {
            return right;
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
        public Map<L, R> map() {
            HashMap<L, R> map = new HashMap<>();
            map.put(this.left, this.right);
            return map;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass())
                return false;
            DefaultImpl<?, ?> np = (DefaultImpl<?, ?>) o;
            return Objects.equals(left, np.left) && Objects.equals(right, np.right);
        }

        @Override
        public int hashCode() {
            return Objects.hash(left, right);
        }

        @Override
        public String toString() {
            return left.toString() + "="  + right.toString();
        }
    }
}
