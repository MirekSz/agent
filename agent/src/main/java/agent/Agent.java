package agent;
import java.lang.instrument.Instrumentation;

public class Agent {
	public static void premain(String args, Instrumentation instrumentation) {
		System.out.println("$$$$$$$$$$ TRANSOFRMER ENABLED $$$$$$$$$");
		ClassLogger transformer = new ClassLogger();
		instrumentation.addTransformer(transformer, true);
	}
}
