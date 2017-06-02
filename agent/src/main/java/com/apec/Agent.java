package com.apec;

import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.Instrumentation;
import java.util.Properties;

public class Agent {
	public static void premain(String args, Instrumentation instrumentation) {
		System.out.println(" _________ ____ ____ ____ ____ ");
		System.out.println("||       |||A |||P |||E |||C || " + getVer());
		System.out.println("||_______|||__|||__|||__|||__||");
		System.out.println("|/_______\\|/__\\|/__\\|/__\\|/__\\|");
		System.out.println(" ____ ____ ____ ____ ____ _________ ____ ____ ____ ____ ____ ____ ____ ");
		System.out.println("||A |||g |||e |||n |||t |||       |||E |||n |||a |||b |||l |||e |||d ||");
		System.out.println("||__|||__|||__|||__|||__|||_______|||__|||__|||__|||__|||__|||__|||__||");
		System.out.println("|/__\\|/__\\|/__\\|/__\\|/__\\|/_______\\|/__\\|/__\\|/__\\|/__\\|/__\\|/__\\|/__\\|");
		ClassTransformer transformer = new ClassTransformer();
		instrumentation.addTransformer(transformer, true);
	}

	private static String getVer() {
		InputStream resourceAsStream = Agent.class.getClassLoader().getResourceAsStream("ver.txt");
		Properties properties = new Properties();
		try {
			properties.load(resourceAsStream);
			return properties.getProperty("build.date");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	public static void main(String[] args) {

		System.out.println(" _________ ____ ____ ____ ____ ");
		System.out.println("||       |||A |||P |||E |||C || " + getVer());
		System.out.println("||_______|||__|||__|||__|||__||");
		System.out.println("|/_______\\|/__\\|/__\\|/__\\|/__\\|");
		System.out.println(" ____ ____ ____ ____ ____ _________ ____ ____ ____ ____ ____ ____ ____ ");
		System.out.println("||A |||g |||e |||n |||t |||       |||E |||n |||a |||b |||l |||e |||d ||");
		System.out.println("||__|||__|||__|||__|||__|||_______|||__|||__|||__|||__|||__|||__|||__||");
		System.out.println("|/__\\|/__\\|/__\\|/__\\|/__\\|/_______\\|/__\\|/__\\|/__\\|/__\\|/__\\|/__\\|/__\\|");
	}
}
