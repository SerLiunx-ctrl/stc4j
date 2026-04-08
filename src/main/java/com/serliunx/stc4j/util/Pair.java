package com.serliunx.stc4j.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    L left();

    /**
     * 获取右值
     *
     * @return  右值
     */
    R right();

    /**
     * 设置左值
     *
     * @param left  左值
     */
    void setLeft(L left);

    /**
     * 设置右值
     *
     * @param right 右值
     */
    void setRight(R right);

    /**
     * 流式操作键值对
     *
     * @return 流
     */
    default Stream<Pair<L, R>> stream() {
        return Stream.<Pair<L, R>>builder()
                .add(this)
                .build();
    }

    /**
     * 转换为Map, 左值为Key, 右值为Value
     *
     * @return  转换后的Map, 默认为HashMap
     */
    default Map<L, R> map() {
        return map(HashMap::new);
    }

    /**
     * 转换为指定类型的Map, 左值为Key, 右值为Value
     *
     * @param supplier  Map
     * @return  转换后的Map
     */
    default Map<L, R> map(Supplier<Map<L, R>> supplier) {
        L left = left();
        R right = right();
        Map<L, R> map = supplier.get();
        map.put(left, right);
        return map;
    }

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
     * 快速创建一个不可变的键值对
     * <p>
     * 该实现无法修改创建好的键值对, 修改类方法会抛出{@link UnsupportedOperationException}
     *
     * @param l     左值
     * @param r     右值
     * @return      键值对
     * @param <L>   左值类型
     * @param <R>   右值类型
     */
    static <L, R> Pair<L, R> ofImmutable(L l, R r) {
        return new ImmutablePair<>(l, r);
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
        return extract(map, false);
    }

    /**
     * 将Map中的键值对提取出来
     * <p>
     * 指定了不可变的键值对时, 无法修改键值对{@link ImmutablePair}
     *
     * @param map   源Map
     * @param immutable 是否不可变
     * @return      键值对列表
     * @param <L>   左值类型
     * @param <R>   右值类型
     */
    static <L, R> List<Pair<L, R>> extract(Map<L, R> map, boolean immutable) {
        return map.entrySet()
                .stream()
                .map(e -> {
                    if (immutable) {
                        return ofImmutable(e.getKey(), e.getValue());
                    } else {
                        return of(e.getKey(), e.getValue());
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * 不可变的键值对默认实现
     *
     * @param <L> 左值
     * @param <R> 右值
     */
    final class ImmutablePair<L, R> extends DefaultImpl<L, R> {

        public ImmutablePair(L left, R right) {
            super(left, right);
        }

        @Override
        public void setLeft(L left) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setRight(R right) {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * 键值对默认实现
     *
     * @param <L>   左值类型
     * @param <R>   右值类型
     */
    class DefaultImpl<L, R> implements Pair<L, R> {

        private L left;
        private R right;

        public DefaultImpl(L left, R right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public L left() {
            return left;
        }

        @Override
        public R right() {
            return right;
        }

        @Override
        public void setLeft(L left) {
            this.left = left;
        }

        @Override
        public void setRight(R right) {
            this.right = right;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Pair)) {
                return false;
            }

            Pair<?, ?> np = (Pair<?, ?>) o;
            return Objects.equals(left, np.left()) &&
                    Objects.equals(right, np.right());
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
