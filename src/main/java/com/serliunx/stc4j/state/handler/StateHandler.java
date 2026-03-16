package com.serliunx.stc4j.state.handler;

/**
 * 状态处理器
 * <p>
 * 定义状态进入、离开及切换时执行的逻辑
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.4
 * @since 2026/3/16
 */
@FunctionalInterface
public interface StateHandler<S> {

	/**
	 * 处理
	 *
	 * @param params 参数
	 */
	void handle(StateHandlerProcessParams<S> params);
}