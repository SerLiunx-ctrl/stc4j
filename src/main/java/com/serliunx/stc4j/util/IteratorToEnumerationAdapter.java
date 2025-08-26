package com.serliunx.stc4j.util;

import java.util.Enumeration;
import java.util.Iterator;

/**
 * Enumeration适配普通迭代器
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.0
 * @since 2025/8/26
 */
public final class IteratorToEnumerationAdapter<T> implements Enumeration<T> {

    private final Iterator<T> it;

    public IteratorToEnumerationAdapter(Iterator<T> it) {
        this.it = it;
    }

    @Override
    public boolean hasMoreElements() {
        return it.hasNext();
    }

    @Override
    public T nextElement() {
        return it.next();
    }
}
