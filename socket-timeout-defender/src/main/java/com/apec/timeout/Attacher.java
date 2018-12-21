
package com.apec.timeout;

import java.io.File;

import net.bytebuddy.agent.ByteBuddyAgent;

public class Attacher {

	public static long getPID() {
		String processName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
		if (processName != null && processName.length() > 0) {
			try {
				return Long.parseLong(processName.split("@")[0]);
			} catch (Exception e) {
				return 0;
			}
		}

		return 0;
	}

	public static void main(final String[] args) {
		System.out.println("Premain" + getPID());
		HeaderUtility headerUtility = new HeaderUtility();
		HeaderUtility.isPriorityCall(null);
		File file;
		try {

			file = (new File(new File("").getAbsolutePath() + "\\target\\socket-timeout-defender.jar"));
			ByteBuddyAgent.attach(file, getPID() + "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		HeaderUtility.isPriorityCall(null);

	}
}
