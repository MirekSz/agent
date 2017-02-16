package agent;

import java.lang.reflect.Method;

public class Delegator {
	private static boolean INITIALIZED = false;
	private static Method start;
	private static Method stop;

	private static void init() {
		try {
			Class<?> forName = Class.forName("agent.Informer");
			start = forName.getDeclaredMethod("start", String.class);
			stop = forName.getDeclaredMethod("stop", String.class);
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}

	public static void start(String name) {
		if (!INITIALIZED) {
			init();
			INITIALIZED = true;
		}
		try {
			start.invoke(null, name);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void stop(String name) {
		try {
			stop.invoke(null, name);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
