package com.serliunx.stc4j.state.external;

import com.serliunx.stc4j.state.machine.StateEventRegistry;
import com.serliunx.stc4j.state.machine.StateMachine;
import com.serliunx.stc4j.state.handler.StateHandler;
import com.serliunx.stc4j.state.handler.StateHandlerWrapper;

import java.util.List;

/**
 * 可变的、灵活的状态机, 支持在运行的过程中动态的增减状态及状态切换的事件
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.4
 * @since 2026/3/16
 * @deprecated 不再使用.
 */
@Deprecated
public interface FlexibleStateMachine<S> extends StateMachine<S>, StateEventRegistry<S> {

    /**
     * 获取指定状态下所有的离开事件处理器.
     * <p>
     * 通过 {@link StateEventRegistry#whenLeave(Object, StateHandler)} 等方法注册.
     *
     * @param state 状态
     * @return  所有与指定状态相关的离开事件处理器
     */
    List<StateHandlerWrapper<S>> allLeaveHandlers(S state);

    /**
     * 获取指定状态下所有的进入事件处理器.
     * <p>
     * 通过 {@link StateEventRegistry#whenEntry(Object, StateHandler)} 等方法注册.
     *
     * @param state 状态
     * @return  所有与指定状态相关的进入事件处理器
     */
    List<StateHandlerWrapper<S>> allEntryHandlers(S state);

    /**
     * 获取指定状态下所有的交换事件处理器.
     * <p>
     * 通过 {@link StateEventRegistry#exchange(Object, Object, StateHandler)} 等方法注册.
     *
     * @param from  源状态
     * @param to    目标状态
     * @return  所有与指定状态相关的交换事件处理器
     */
    List<StateHandlerWrapper<S>> allExchangeHandlers(S from, S to);
}
