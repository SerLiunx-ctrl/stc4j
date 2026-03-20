package com.serliunx.stc4j.collection;

import com.serliunx.stc4j.util.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * 带权重的随机对象池默认实现。
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.5
 * @since 2026/3/20
 */
public class DefaultWeightedRandomObjectPool<E> implements WeightedRandomObjectPool<E> {

    private final List<Entry<E>> entries = new ArrayList<>();
    private int totalWeight = 0;

    @Override
    public E get() {
        return get(new Random(System.currentTimeMillis()));
    }

    @Override
    public E get(Random random) {
        Assert.notNull(random);
        Assert.isFalse(entries.isEmpty(), "object pool must not be empty!");

        final int hit = random.nextInt(totalWeight);
        int current = 0;
        for (Entry<E> entry : entries) {
            current += entry.weight;
            if (hit < current) {
                return entry.object;
            }
        }

        throw new IllegalStateException("Unable to locate weighted object.");
    }

    @Override
    public boolean add(E object, int weight) {
        Assert.isTrue(weight > 0, "weight must be greater than 0!");
        entries.add(new Entry<>(object, weight));
        totalWeight += weight;
        return true;
    }

    @Override
    public int getWeight(E object) {
        int weight = 0;
        for (Entry<E> entry : entries) {
            if (Objects.equals(entry.object, object)) {
                weight += entry.weight;
            }
        }
        return weight;
    }

    @Override
    public int totalWeight() {
        return totalWeight;
    }

    @Override
    public int size() {
        return entries.size();
    }

    @Override
    public boolean isEmpty() {
        return entries.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        for (Entry<E> entry : entries) {
            if (Objects.equals(entry.object, o)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Iterator<E> iterator() {
        final Iterator<Entry<E>> delegate = entries.iterator();
        return new Iterator<E>() {
            private Entry<E> current;

            @Override
            public boolean hasNext() {
                return delegate.hasNext();
            }

            @Override
            public E next() {
                current = delegate.next();
                return current.object;
            }

            @Override
            public void remove() {
                delegate.remove();
                totalWeight -= current.weight;
            }
        };
    }

    @Override
    public Object[] toArray() {
        Object[] array = new Object[entries.size()];
        for (int i = 0; i < entries.size(); i++) {
            array[i] = entries.get(i).object;
        }
        return array;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        final int size = entries.size();
        T[] array = a.length >= size ? a : (T[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);
        for (int i = 0; i < size; i++) {
            array[i] = (T) entries.get(i).object;
        }
        if (array.length > size) {
            array[size] = null;
        }
        return array;
    }

    @Override
    public boolean add(E e) {
        return add(e, 1);
    }

    @Override
    public boolean remove(Object o) {
        Iterator<Entry<E>> iterator = entries.iterator();
        while (iterator.hasNext()) {
            Entry<E> entry = iterator.next();
            if (Objects.equals(entry.object, o)) {
                iterator.remove();
                totalWeight -= entry.weight;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object object : c) {
            if (!contains(object)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean changed = false;
        for (E object : c) {
            changed |= add(object);
        }
        return changed;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean changed = false;
        Iterator<Entry<E>> iterator = entries.iterator();
        while (iterator.hasNext()) {
            Entry<E> entry = iterator.next();
            if (c.contains(entry.object)) {
                iterator.remove();
                totalWeight -= entry.weight;
                changed = true;
            }
        }
        return changed;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean changed = false;
        Iterator<Entry<E>> iterator = entries.iterator();
        while (iterator.hasNext()) {
            Entry<E> entry = iterator.next();
            if (!c.contains(entry.object)) {
                iterator.remove();
                totalWeight -= entry.weight;
                changed = true;
            }
        }
        return changed;
    }

    @Override
    public void clear() {
        entries.clear();
        totalWeight = 0;
    }

    private static final class Entry<E> {

        private final E object;
        private final int weight;

        private Entry(E object, int weight) {
            this.object = object;
            this.weight = weight;
        }
    }
}
