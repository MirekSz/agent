
package com.apec.timeout;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.AgentBuilder.Transformer.ForAdvice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.loading.ClassInjector;
import net.bytebuddy.matcher.ElementMatchers;

public class TimeoutAgent {

	public static final String AGENT = "apec.timeout.agent";
	public static final String AGENT_TIMEOUT = "apec.socket.timeout.val";
	public static final String TESTING_ENV = "streamsoft.testing.env";
	public static String GLOBAL_TIMEOUT = "60000";

	public static void premain(final String arguments, final Instrumentation instrumentation) throws Exception {
		File temp = Files.createTempDirectory("apec-timeout-agent").toFile();

		deleteOldDirs(temp);

		ClassInjector.UsingInstrumentation.of(temp, ClassInjector.UsingInstrumentation.Target.BOOTSTRAP, instrumentation)
				.inject(Collections.singletonMap(new TypeDescription.ForLoadedType(SocketInputStreamSocketReadAdvice.class),
						ClassFileLocator.ForClassLoader.read(SocketInputStreamSocketReadAdvice.class).resolve()));

		ForAdvice advice = new AgentBuilder.Transformer.ForAdvice()// .include(SocketInputStreamSocketReadAdvice.class.getClassLoader())
				.advice(ElementMatchers.named("socketRead"), SocketInputStreamSocketReadAdvice.class.getName());

		if (System.getProperty(AGENT_TIMEOUT) == null) {
			System.setProperty(AGENT_TIMEOUT, GLOBAL_TIMEOUT);
		}
		if (System.getProperty(TESTING_ENV) != null) {
			System.setProperty("sun.net.client.defaultConnectTimeout", "1");
			System.setProperty("sun.net.client.defaultReadTimeout", "1");
			advice = new AgentBuilder.Transformer.ForAdvice().include(TestingSocketInputStreamSocketReadAdvice.class.getClassLoader())
					.advice(ElementMatchers.named("socketRead"), TestingSocketInputStreamSocketReadAdvice.class.getName());
		}

		System.setProperty(AGENT, "true");
		System.setProperty("sun.net.client.defaultConnectTimeout", GLOBAL_TIMEOUT + "");
		System.setProperty("sun.net.client.defaultReadTimeout", GLOBAL_TIMEOUT + "");

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
		File temp = Files.createTempDirectory("apec-timeout-agent").toFile();
		deleteOldDirs(temp);
	}

	private static void deleteOldDirs(final File temp) throws IOException {
		Stream<Path> list = Files.list(temp.getParentFile().toPath());
		list.filter(d -> d.toString().contains("apec-timeout-agent")).filter(d -> !d.toFile().equals(temp)).map(Path::toFile)
				.forEach(File::delete);
	}

}
