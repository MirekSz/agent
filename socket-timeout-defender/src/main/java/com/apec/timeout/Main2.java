
package com.apec.timeout;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class Main2 {

	public static void main(final String[] args) throws Exception {
		System.out.println("#################################");
		HttpURLConnection urlConnection = (HttpURLConnection) new URL("http://www.google.com").openConnection();
		System.out.println(urlConnection.getRequestMethod());
		URL oracle = new URL("https://www.oracle.com/");
		URLConnection yc = oracle.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
		System.out.println(in.readLine());
		in.close();
	}
}
