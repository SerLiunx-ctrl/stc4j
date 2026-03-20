package com.serliunx.stc4j.collection;

import com.serliunx.stc4j.util.Assert;

import java.util.*;

/**
 * 随机对象池默认实现
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.5
 * @since 2026/3/19
 */
public class DefaultRandomObjectPool<E> implements RandomObjectPool<E> {

    private final List<E> objects = new ArrayList<>();

    @Override
    public E get() {
        return get(new Random(System.currentTimeMillis()));
    }

    @Override
    public E get(Random random) {
        Assert.notNull(random);
        return objects.get(random.nextInt(objects.size()));
    }

    @Override
    public int size() {
        return objects.size();
    }

    @Override
    public boolean isEmpty() {
        return objects.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return objects.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return objects.iterator();
    }

    @Override
    public Object[] toArray() {
        return objects.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return objects.toArray(a);
    }

    @Override
    public boolean add(E e) {
        return objects.add(e);
    }

    @Override
    public boolean remove(Object o) {
        return objects.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return new HashSet<>(objects).containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return objects.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return objects.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return objects.retainAll(c);
    }

    @Override
    public void clear() {
        objects.clear();
    }
}
