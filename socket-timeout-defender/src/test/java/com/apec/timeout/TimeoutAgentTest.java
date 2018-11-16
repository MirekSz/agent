
package com.apec.timeout;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

// run
// -javaagent:F:\git\agent\socket-timeout-defender\target\socket-timeout-defender.jar -Dstreamsoft.testing.env=true
public class TimeoutAgentTest {

	public static void main(final String[] args) throws Exception {
		shouldThrowSocketTimeoutExceptionByGlobalTimeout();
		shouldThrowSocketTimeoutExceptionBySocketInstrumentation();
	}

	public static void shouldThrowSocketTimeoutExceptionByGlobalTimeout() throws Exception {
		// given

		// when
		Throwable throwable = catchThrowable(TimeoutAgentTest::callSiteByURLConnection);

		// then
		assertThat(throwable).isInstanceOf(SocketTimeoutException.class);
	}

	public static void shouldThrowSocketTimeoutExceptionBySocketInstrumentation() throws Exception {
		// given

		// when
		Throwable throwable = catchThrowable(TimeoutAgentTest::callSiteByJSOUP);

		// then
		assertThat(throwable).isInstanceOf(SocketTimeoutException.class);
	}

	public static void callSiteByURLConnection() throws Exception {
		HttpURLConnection urlConnection = (HttpURLConnection) new URL("http://www.google.com").openConnection();
		System.out.println(urlConnection.getRequestMethod());
		URL oracle = new URL("https://www.oracle.com/");
		URLConnection yc = oracle.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
		System.out.println(in.readLine());
		in.close();
	}

	public static void callSiteByJSOUP() throws Exception {

		Document doc = Jsoup.connect("https://www.oracle.com/").get();
		doc.body().text();
	}
}
