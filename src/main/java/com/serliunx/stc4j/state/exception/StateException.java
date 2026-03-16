package com.serliunx.stc4j.state.exception;

/**
 * 状态机异常
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.4
 * @since 2026/3/16
 */
public class StateException extends RuntimeException {

	public StateException() {
	}

	public StateException(String message) {
		super(message);
	}

	public StateException(String message, Throwable cause) {
		super(message, cause);
	}

	public StateException(Throwable cause) {
		super(cause);
	}

	public StateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
