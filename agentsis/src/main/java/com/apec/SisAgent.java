
package com.apec;

import java.lang.instrument.Instrumentation;

public class SisAgent {

	public static void premain(final String args, final Instrumentation instrumentation) {
		System.out.println(" _________ ____ agentsis ____ ____ ____ ");
		SisClassTransformer transformer = new SisClassTransformer();
		instrumentation.addTransformer(transformer, true);
	}
}
