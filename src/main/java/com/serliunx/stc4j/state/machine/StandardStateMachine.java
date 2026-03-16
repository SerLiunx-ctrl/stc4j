package com.serliunx.stc4j.state.machine;

import com.serliunx.stc4j.state.handler.StateHandlerWrapper;
import com.serliunx.stc4j.util.Pair;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

/**
 * 状态机的标准实现
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.4
 * @since 2026/3/16
 */
public class StandardStateMachine<S> extends AbstractStateMachine<S> implements StateMachine<S> {

	/**
	 * 默认的构造函数
	 *
	 * @param entryHandlers 	进入事件处理器集合
	 * @param leaveHandlers 	离开事件处理器集合
	 * @param exchangeHandlers	交换事件处理器集合
	 * @param executor 			异步执行器
	 * @param async 			是否异步执行
	 */
	StandardStateMachine(List<S> stateList,
						 Map<S, List<StateHandlerWrapper<S>>> entryHandlers,
						 Map<S, List<StateHandlerWrapper<S>>> leaveHandlers,
						 Map<Pair<S, S>, List<StateHandlerWrapper<S>>> exchangeHandlers,
						 Map<Object, List<Consumer<StateMachine<S>>>> eventRegistries,
						 Executor executor,
						 Boolean async,
						 S initialState
	) {
		super(stateList, new StateMachineContext<>(entryHandlers, leaveHandlers, exchangeHandlers, eventRegistries,
				executor, async, initialState));

		final int initialIndex = indexOf(context.initialState);
		if (initialIndex != -1) {
			setDefault(initialIndex);
			updateCurrentIndex(initialIndex);
		}
	}
}
