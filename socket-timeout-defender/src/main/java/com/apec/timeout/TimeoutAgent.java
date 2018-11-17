
package com.apec.timeout;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.AgentBuilder.Transformer.ForAdvice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeDescription.ForLoadedType;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.loading.ClassInjector;
import net.bytebuddy.matcher.ElementMatchers;

public class TimeoutAgent {

	public static final String AGENT = "apec.timeout.agent";
	public static final String AGENT_TIMEOUT = "apec.socket.timeout.val";
	public static final String AGENT_DEBUG = "apec.socket.timeout.debug";
	public static final String TESTING_ENV = "apec.testing.env";
	public static String GLOBAL_TIMEOUT = "60000";

	public static void premain(final String arguments, final Instrumentation instrumentation) throws Exception {
		File temp = createTmpDir();

		Map<ForLoadedType, byte[]> map = new HashMap<>();
		map.put(new TypeDescription.ForLoadedType(SocketInputStreamSocketReadAdvice.class),
				ClassFileLocator.ForClassLoader.read(SocketInputStreamSocketReadAdvice.class).resolve());
		map.put(new TypeDescription.ForLoadedType(TestingSocketInputStreamSocketReadAdvice.class),
				ClassFileLocator.ForClassLoader.read(TestingSocketInputStreamSocketReadAdvice.class).resolve());
		map.put(new TypeDescription.ForLoadedType(DebuggingSocketInputStreamSocketReadAdvice.class),
				ClassFileLocator.ForClassLoader.read(DebuggingSocketInputStreamSocketReadAdvice.class).resolve());

		ClassInjector.UsingInstrumentation.of(temp, ClassInjector.UsingInstrumentation.Target.BOOTSTRAP, instrumentation).inject(map);

		ForAdvice advice = new AgentBuilder.Transformer.ForAdvice()// .include(SocketInputStreamSocketReadAdvice.class.getClassLoader())
				.advice(ElementMatchers.named("socketRead"), SocketInputStreamSocketReadAdvice.class.getName());

		if (System.getProperty(AGENT_TIMEOUT) == null) {
			System.setProperty(AGENT_TIMEOUT, GLOBAL_TIMEOUT);
		}
		if (System.getProperty(AGENT_DEBUG) != null) {
			advice = new AgentBuilder.Transformer.ForAdvice().advice(ElementMatchers.named("socketRead"),
					DebuggingSocketInputStreamSocketReadAdvice.class.getName());
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
		System.out.println(">>> Global  ");
		System.out.println(">>> Socket ");
		System.out
				.println(">>> Timeout: " + TimeUnit.MILLISECONDS.toSeconds(Integer.valueOf(System.getProperty(AGENT_TIMEOUT))) + "s  <<<");
		System.out.println();

		new AgentBuilder.Default().enableBootstrapInjection(instrumentation, temp).ignore(ElementMatchers.nameStartsWith("net.bytebuddy."))
				.type(ElementMatchers.nameEndsWith("SocketInputStream")).transform(advice).installOn(instrumentation);

	}

	public static void main(final String[] args) throws Exception {
		System.out.println(" ____ ____ ____ ____ ");
		System.out.println("||A |||P |||E |||C || ");
		System.out.println("||__|||__|||__|||__||");
		System.out.println("|/__\\|/__\\|/__\\|/__\\|");
		System.out.println(">>> Global  ");
		System.out.println(">>> Socket ");
		System.out.println(">>> Timeout: " + 60 + "s  <<<");
		System.out.println();
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
