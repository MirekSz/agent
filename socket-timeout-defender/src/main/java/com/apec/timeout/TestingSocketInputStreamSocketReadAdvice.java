
package com.apec.timeout;

import net.bytebuddy.asm.Advice;

public class TestingSocketInputStreamSocketReadAdvice {

	/**
	 * @see SocketInputStream@socketRead
	 * @param obj
	 * @param method
	 * @param m
	 */
	@Advice.OnMethodEnter
	static void before(@Advice.This final Object obj, @Advice.Origin final String method,
			@Advice.Argument(readOnly = false, value = 4) int timeout) {
		System.out.println("timeout " + timeout);
		timeout = 1;
	}

}
