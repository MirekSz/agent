
package com.apec.timeout;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;

public class BugFixAgent {

	public static void agentmain(final String arg, final Instrumentation inst) throws Exception {
		// only if header utility is on the class path; otherwise,
		// a class can be found within any class loader by iterating
		// over the return value of Instrumentation::getAllLoadedClasses
		Class<?> headerUtility = Class.forName("com.apec.timeout.HeaderUtility");

		// copy the contents of typo.fix into a byte array
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (InputStream input = BugFixAgent.class.getResourceAsStream("/typo.fix")) {
			byte[] buffer = new byte[1024];
			int length;
			while ((length = input.read(buffer)) != -1) {
				output.write(buffer, 0, length);
			}
		}

		// Apply the redefinition
		inst.redefineClasses(new ClassDefinition(headerUtility, output.toByteArray()));
	}
}
