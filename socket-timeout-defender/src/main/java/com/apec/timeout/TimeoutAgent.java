
package com.apec.timeout;

import java.lang.instrument.Instrumentation;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.AgentBuilder.Transformer.ForAdvice;
import net.bytebuddy.matcher.ElementMatchers;

public class TimeoutAgent {

	public static final String AGENT = "streamsoft.timeout.agent";
	public static final String TESTING_ENV = "streamsoft.testing.env";
	public static final int GLOBAL_TIMEOUT = 600000;

	public static void premain(final String arguments, final Instrumentation instrumentation) throws Exception {
		ForAdvice advice = new AgentBuilder.Transformer.ForAdvice().include(SocketInputStreamSocketReadAdvice.class.getClassLoader())
				.advice(ElementMatchers.named("socketRead"), SocketInputStreamSocketReadAdvice.class.getName());

		System.setProperty(AGENT, "true");
		System.setProperty("sun.net.client.defaultConnectTimeout", GLOBAL_TIMEOUT + "");
		System.setProperty("sun.net.client.defaultReadTimeout", GLOBAL_TIMEOUT + "");

		if (System.getProperty(TESTING_ENV) != null) {
			System.setProperty("sun.net.client.defaultConnectTimeout", "1");
			System.setProperty("sun.net.client.defaultReadTimeout", "1");
			advice = new AgentBuilder.Transformer.ForAdvice().include(TestingSocketInputStreamSocketReadAdvice.class.getClassLoader())
					.advice(ElementMatchers.named("socketRead"), TestingSocketInputStreamSocketReadAdvice.class.getName());
		}

		new AgentBuilder.Default().ignore(ElementMatchers.nameStartsWith("net.bytebuddy."))
				.type(ElementMatchers.nameEndsWith("SocketInputStream")).transform(advice).installOn(instrumentation);

	}

}
