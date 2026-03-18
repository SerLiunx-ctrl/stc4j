package com.serliunx.stc4j.state.machine;

import com.serliunx.stc4j.state.handler.StateHandlerWrapper;
import com.serliunx.stc4j.util.Pair;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

/**
 * 并发型状态机的默认实现。
 * 状态索引的切换使用 CAS，而状态列表结构变化（如 reserve）由独立的读写锁协调。
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.4
 * @since 2026/3/16
 */
public class DefaultConcurrentStateMachine<S> extends AbstractStateMachine<S> implements ConcurrentStateMachine<S> {

    /**
     * 当前状态索引。
     */
    private final AtomicInteger index = new AtomicInteger(0);
    private final ReentrantReadWriteLock structuralLock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.ReadLock structuralReadLock = structuralLock.readLock();
    private final ReentrantReadWriteLock.WriteLock structuralWriteLock = structuralLock.writeLock();

    public DefaultConcurrentStateMachine(List<S> stateList,
                                  Map<S, List<StateHandlerWrapper<S>>> entryHandlers,
                                  Map<S, List<StateHandlerWrapper<S>>> leaveHandlers,
                                  Map<Pair<S, S>, List<StateHandlerWrapper<S>>> exchangeHandlers,
                                  Map<Object, List<Consumer<StateMachine<S>>>> eventRegistries,
                                  Executor executor,
                                  Boolean async,
                                  S initialState
    ) {
        super(stateList, new StateMachineContext<>(entryHandlers, leaveHandlers, exchangeHandlers, eventRegistries, executor, async, initialState));

        final int initialIndex = indexOf(context.initialState);
        if (initialIndex != -1) {
            setDefault(initialIndex);
            updateCurrentIndex(initialIndex);
        }
    }

    @Override
    public boolean compareAndSet(S expectedValue, S newValue) {
        return compareAndSet(expectedValue, newValue, true);
    }

    @Override
    public boolean compareAndSet(S expectedValue, S newValue, boolean invokeHandlers) {
        Transition<S> transition;
        try {
            structuralReadLock.lock();
            int current = indexOf(expectedValue);
            int newIndex = indexOf(newValue);
            if (current == -1 || newIndex == -1)
                return false;

            if (!index.compareAndSet(current, newIndex))
                return false;

            transition = new Transition<>(get(current), get(newIndex));
        } finally {
            structuralReadLock.unlock();
        }

        if (invokeHandlers && !transition.noop()) {
            invokeHandlers(transition.fromState, transition.toState);
        }

        return true;
    }

    @Override
    public void reserve() {
        try {
            structuralWriteLock.lock();
            super.reserve();
        } finally {
            structuralWriteLock.unlock();
        }
    }

    /**
     * 使用 CAS 将当前状态重置为默认状态。
     *
     * @param invokeHandlers 是否触发状态处理器
     */
    @Override
    public void reset(boolean invokeHandlers) {
        Transition<S> transition;
        try {
            structuralReadLock.lock();
            if (isDefault())
                return;
            transition = exchangeToTarget(getDefault());
        } finally {
            structuralReadLock.unlock();
        }

        if (transition != null && invokeHandlers) {
            invokeHandlers(transition.fromState, transition.toState);
        }
    }

    @Override
    public boolean switchTo(S state, boolean invokeHandlers) {
        Transition<S> transition;
        try {
            structuralReadLock.lock();
            int i = indexOf(state);
            if (i == -1 || i == index.get()) {
                return false;
            }
            transition = exchangeToTarget(i);
            if (transition == null) {
                return false;
            }
        } finally {
            structuralReadLock.unlock();
        }

        if (invokeHandlers) {
            invokeHandlers(transition.fromState, transition.toState);
        }
        return true;
    }

    @Override
    public S switchPrevAndGet(boolean invokeHandlers) {
        Transition<S> transition;
        try {
            structuralReadLock.lock();
            transition = exchangeToPrev();
        } finally {
            structuralReadLock.unlock();
        }

        if (invokeHandlers) {
            invokeHandlers(transition.fromState, transition.toState);
        }
        return transition.toState;
    }

    @Override
    public S getAndSwitchPrev(boolean invokeHandlers) {
        Transition<S> transition;
        try {
            structuralReadLock.lock();
            transition = exchangeToPrev();
        } finally {
            structuralReadLock.unlock();
        }

        if (invokeHandlers) {
            invokeHandlers(transition.fromState, transition.toState);
        }
        return transition.fromState;
    }

    @Override
    public void switchPrev(boolean invokeHandlers) {
        Transition<S> transition;
        try {
            structuralReadLock.lock();
            transition = exchangeToPrev();
        } finally {
            structuralReadLock.unlock();
        }

        if (invokeHandlers) {
            invokeHandlers(transition.fromState, transition.toState);
        }
    }

    @Override
    public S switchNextAndGet(boolean invokeHandlers) {
        Transition<S> transition;
        try {
            structuralReadLock.lock();
            transition = exchangeToNext();
        } finally {
            structuralReadLock.unlock();
        }

        if (invokeHandlers) {
            invokeHandlers(transition.fromState, transition.toState);
        }
        return transition.toState;
    }

    @Override
    public S getAndSwitchNext(boolean invokeHandlers) {
        Transition<S> transition;
        try {
            structuralReadLock.lock();
            transition = exchangeToNext();
        } finally {
            structuralReadLock.unlock();
        }

        if (invokeHandlers) {
            invokeHandlers(transition.fromState, transition.toState);
        }
        return transition.fromState;
    }

    @Override
    public void switchNext(boolean invokeHandlers) {
        Transition<S> transition;
        try {
            structuralReadLock.lock();
            transition = exchangeToNext();
        } finally {
            structuralReadLock.unlock();
        }

        if (invokeHandlers) {
            invokeHandlers(transition.fromState, transition.toState);
        }
    }

    @Override
    public S current() {
        try {
            structuralReadLock.lock();
            return get(index.get());
        } finally {
            structuralReadLock.unlock();
        }
    }

    @Override
    protected void updateCurrentIndex(int newIndex) {
        this.index.set(newIndex);
    }

    /**
     * 当前是否处于默认状态。
     *
     * @return 当前索引等于默认索引时返回 {@code true}
     */
    protected boolean isDefault() {
        return index.get() == getDefault();
    }

    /**
     * 使用 CAS 将当前索引切换到上一个状态，直到成功为止。
     *
     * @return 切换结果
     */
    protected Transition<S> exchangeToPrev() {
        final int size = size();
        int currentValue;
        int newValue;
        do {
            currentValue = index.get();
            newValue = currentValue == 0 ? size - 1 : currentValue - 1;
        } while (!index.compareAndSet(currentValue, newValue));
        return new Transition<>(get(currentValue), get(newValue));
    }

    /**
     * 使用 CAS 将当前索引切换到下一个状态，直到成功为止。
     *
     * @return 切换结果
     */
    protected Transition<S> exchangeToNext() {
        final int size = size();
        int currentValue;
        int newValue;
        do {
            currentValue = index.get();
            newValue = currentValue == size - 1 ? 0 : currentValue + 1;
        } while (!index.compareAndSet(currentValue, newValue));
        return new Transition<>(get(currentValue), get(newValue));
    }

    /**
     * 使用 CAS 将当前索引切换到目标索引，直到成功为止。
     *
     * @param target 目标索引
     * @return 切换结果
     */
    protected Transition<S> exchangeToTarget(int target) {
        int currentValue;
        while (true) {
            currentValue = index.get();
            if (currentValue == target) {
                return null;
            }
            if (index.compareAndSet(currentValue, target)) {
                return new Transition<>(get(currentValue), get(target));
            }
        }
    }

    protected static final class Transition<S> {

        private final S fromState;
        private final S toState;

        private Transition(S fromState, S toState) {
            this.fromState = fromState;
            this.toState = toState;
        }

        private boolean noop() {
            return Objects.equals(fromState, toState);
        }
    }
}
