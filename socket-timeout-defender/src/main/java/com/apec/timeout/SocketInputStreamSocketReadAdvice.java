
package com.apec.timeout;

import net.bytebuddy.asm.Advice;

public class SocketInputStreamSocketReadAdvice {

	private static final int NOT_SET = 1;

	/**
	 * @see SocketInputStream@socketRead
	 * @param obj
	 * @param method
	 * @param m
	 */
	@Advice.OnMethodEnter
	static void before(@Advice.This final Object obj, @Advice.Origin final String method,
			@Advice.Argument(readOnly = false, value = 4) int timeout) {
		if (timeout < NOT_SET) {
			timeout = TimeoutAgent.GLOBAL_TIMEOUT;
		}
	}

}
