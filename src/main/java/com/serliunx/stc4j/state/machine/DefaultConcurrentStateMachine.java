package com.serliunx.stc4j.state.machine;

import com.serliunx.stc4j.state.handler.StateHandlerWrapper;
import com.serliunx.stc4j.util.Pair;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * 并发型状态机的默认实现, 内置的状态序列切换使用CAS实现.
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.4
 * @since 2026/3/16
 */
public class DefaultConcurrentStateMachine<S> extends AbstractStateMachine<S> implements ConcurrentStateMachine<S> {

    /**
     * 当前状态
     */
    private final AtomicInteger index = new AtomicInteger(0);

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
        int current = indexOf(expectedValue);
        int newIndex = indexOf(newValue);
        if (current == -1 || newIndex == -1)
            return false;

        boolean result = index.compareAndSet(current, newIndex);
        if (result && invokeHandlers && current != newIndex) {
            invokeHandlers(get(current), get(newIndex));
        }

        return result;
    }

    /**
     * 使用CAS不断尝试将当前状态重置回默认值(0)
     *
     * @param invokeHandlers    是否唤醒状态处理器
     */
    @Override
    public void reset(boolean invokeHandlers) {
        if (isDefault())
            return;
        Transition transition = exchangeToTarget(getDefault());
        if (transition != null && invokeHandlers) {
            invokeHandlers(get(transition.fromIndex), get(transition.toIndex));
        }
    }

    @Override
    public boolean switchTo(S state, boolean invokeHandlers) {
        int i = indexOf(state);
        if (i == -1 ||
                i == index.get()) {
            return false;
        }
        Transition transition = exchangeToTarget(i);
        if (transition == null) {
            return false;
        }
        if (invokeHandlers) {
            invokeHandlers(get(transition.fromIndex), get(transition.toIndex));
        }
        return true;
    }

    @Override
    public S switchPrevAndGet(boolean invokeHandlers) {
        Transition transition = exchangeToPrev();
        if (invokeHandlers) {
            invokeHandlers(get(transition.fromIndex), get(transition.toIndex));
        }
        return get(transition.toIndex);
    }

    @Override
    public S getAndSwitchPrev(boolean invokeHandlers) {
        Transition transition = exchangeToPrev();
        if (invokeHandlers) {
            invokeHandlers(get(transition.fromIndex), get(transition.toIndex));
        }
        return get(transition.fromIndex);
    }

    @Override
    public void switchPrev(boolean invokeHandlers) {
        Transition transition = exchangeToPrev();
        if (invokeHandlers) {
            invokeHandlers(get(transition.fromIndex), get(transition.toIndex));
        }
    }

    @Override
    public S switchNextAndGet(boolean invokeHandlers) {
        Transition transition = exchangeToNext();
        if (invokeHandlers) {
            invokeHandlers(get(transition.fromIndex), get(transition.toIndex));
        }
        return get(transition.toIndex);
    }

    @Override
    public S getAndSwitchNext(boolean invokeHandlers) {
        Transition transition = exchangeToNext();
        if (invokeHandlers) {
            invokeHandlers(get(transition.fromIndex), get(transition.toIndex));
        }
        return get(transition.fromIndex);
    }

    @Override
    public void switchNext(boolean invokeHandlers) {
        Transition transition = exchangeToNext();
        if (invokeHandlers) {
            invokeHandlers(get(transition.fromIndex), get(transition.toIndex));
        }
    }

    @Override
    public S current() {
        return get(index.get());
    }

    @Override
    protected void updateCurrentIndex(int newIndex) {
        this.index.set(newIndex);
    }

    /**
     * 是否为默认状态
     *
     * @return 默认状态时返回真, 否则返回假.
     */
    protected boolean isDefault() {
        return index.get() == getDefault();
    }

    /**
     * 移动下标至上一个状态
     * <p>
     *     使用CAS一直尝试, 直到成功
     * </p>
     */
    protected Transition exchangeToPrev() {
        final int size = size();
        int currentValue;
        int newValue;
        do {
            currentValue = index.get();
            newValue = currentValue == 0 ? size - 1 : currentValue - 1;
        } while (!index.compareAndSet(currentValue, newValue));
        return new Transition(currentValue, newValue);
    }

    /**
     * 移动下标至下一个状态
     * <p>
     *     使用CAS一直尝试, 直到成功
     * </p>
     */
    protected Transition exchangeToNext() {
        final int size = size();
        int currentValue;
        int newValue;
        do {
            currentValue = index.get();
            newValue = currentValue == size - 1 ? 0 : currentValue + 1;
        } while (!index.compareAndSet(currentValue, newValue));
        return new Transition(currentValue, newValue);
    }

    /**
     * 切换到指定状态值
     * <p>
     *     使用CAS一直尝试, 直到成功
     * </p>
     *
     * @param target    目标值
     */
    protected Transition exchangeToTarget(int target) {
        int currentValue;
        while (true) {
            currentValue = index.get();
            if (currentValue == target) {
                return null;
            }
            if (index.compareAndSet(currentValue, target)) {
                return new Transition(currentValue, target);
            }
        }
    }

    protected static final class Transition {

        private final int fromIndex;
        private final int toIndex;

        private Transition(int fromIndex, int toIndex) {
            this.fromIndex = fromIndex;
            this.toIndex = toIndex;
        }
    }
}
