
package com.apec.timeout;

import net.bytebuddy.asm.Advice;

public class DebuggingSocketInputStreamSocketReadAdvice {

	public static final String AGENT_TIMEOUT = "apec.socket.timeout.val";

	private static final int NOT_SET = 1;
	public static int TIMEOUT;
	static {
		TIMEOUT = Integer.valueOf(System.getProperty(AGENT_TIMEOUT));
	}

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
			timeout = TIMEOUT;
			StackTraceElement[] stackTrace = new Exception().getStackTrace();
			int ii = 0;
			StringBuilder sb = new StringBuilder(1000);
			sb.append("INFINITY socket timeout");
			for (StackTraceElement stackTraceElement : stackTrace) {
				sb.append(stackTraceElement.toString() + "\n");
				ii++;
				if (ii == 20) {
					break;
				}
			}
			System.err.println(sb);
		}
	}

}
