
package com.apec.timeout;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.AgentBuilder.Transformer.ForAdvice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.loading.ClassInjector;
import net.bytebuddy.matcher.ElementMatchers;

public class TimeoutAgent {

	public static final String AGENT = "apec.timeout.agent";
	public static final String AGENT_TIMEOUT = "apec.socket.timeout.val";
	public static final String TESTING_ENV = "apec.testing.env";
	public static String GLOBAL_TIMEOUT = "60000";

	public static void premain(final String arguments, final Instrumentation instrumentation) throws Exception {
		File temp = createTmpDir();

		ClassInjector.UsingInstrumentation.of(temp, ClassInjector.UsingInstrumentation.Target.BOOTSTRAP, instrumentation)
				.inject(Collections.singletonMap(new TypeDescription.ForLoadedType(SocketInputStreamSocketReadAdvice.class),
						ClassFileLocator.ForClassLoader.read(SocketInputStreamSocketReadAdvice.class).resolve()));

		ForAdvice advice = new AgentBuilder.Transformer.ForAdvice()// .include(SocketInputStreamSocketReadAdvice.class.getClassLoader())
				.advice(ElementMatchers.named("socketRead"), SocketInputStreamSocketReadAdvice.class.getName());

		if (System.getProperty(AGENT_TIMEOUT) == null) {
			System.setProperty(AGENT_TIMEOUT, GLOBAL_TIMEOUT);
		}

		System.setProperty(AGENT, "true");
		System.setProperty("sun.net.client.defaultConnectTimeout", System.getProperty(AGENT_TIMEOUT));
		System.setProperty("sun.net.client.defaultReadTimeout", System.getProperty(AGENT_TIMEOUT));

		if (System.getProperty(TESTING_ENV) != null) {
			System.setProperty("sun.net.client.defaultConnectTimeout", "1");
			System.setProperty("sun.net.client.defaultReadTimeout", "1");
			System.setProperty(AGENT_TIMEOUT, "1");
			advice = new AgentBuilder.Transformer.ForAdvice()// .include(SocketInputStreamSocketReadAdvice.class.getClassLoader())
					.advice(ElementMatchers.named("socketRead"), TestingSocketInputStreamSocketReadAdvice.class.getName());
		}

		System.out.println(" ____ ____ ____ ____ ");
		System.out.println("||A |||P |||E |||C || ");
		System.out.println("||__|||__|||__|||__||");
		System.out.println("|/__\\|/__\\|/__\\|/__\\|");
		System.out
				.println(">>> Timeout: " + TimeUnit.MILLISECONDS.toSeconds(Integer.valueOf(System.getProperty(AGENT_TIMEOUT))) + "s  <<<");
		System.out.println();

		new AgentBuilder.Default().enableBootstrapInjection(instrumentation, temp).ignore(ElementMatchers.nameStartsWith("net.bytebuddy."))
				.type(ElementMatchers.nameEndsWith("SocketInputStream")).transform(advice).installOn(instrumentation);

	}

	public static void main(final String[] args) throws Exception {
		File temp = new File(new File(System.getProperty("java.io.tmpdir")).getAbsolutePath() + File.separator + "apec-timeout-agent");
		if (temp.exists()) {
			File[] listFiles = temp.listFiles();
			for (File file : listFiles) {
				file.delete();
			}
		}
		temp.mkdir();
	}

	private static File createTmpDir() throws IOException {
		File temp = new File(new File(System.getProperty("java.io.tmpdir")).getAbsolutePath() + File.separator + "apec-timeout-agent");
		if (temp.exists()) {
			File[] listFiles = temp.listFiles();
			for (File file : listFiles) {
				file.delete();
			}
		}
		temp.mkdir();
		return temp;
	}

}
