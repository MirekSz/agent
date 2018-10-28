import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.media.sse.EventListener;
import org.glassfish.jersey.media.sse.EventSource;
import org.glassfish.jersey.media.sse.InboundEvent;
import org.glassfish.jersey.media.sse.SseFeature;

public class SSEClient {

	public static void main(String[] args) throws Throwable {
		Client client = ClientBuilder.newBuilder().register(SseFeature.class).register(LoggingFeature.class)
				.property(LoggingFeature.LOGGING_FEATURE_LOGGER_LEVEL, "FINEST").build();
		// HttpAuthenticationFeature feature =
		// HttpAuthenticationFeature.basic("actuator", "!#%asdActuator~1@12");
		// Client client =
		// ClientBuilder.newBuilder().register(feature).register(SseFeature.class)
		// .register(new Authenticator("actuator",
		// "!#%asdActuator~1@12")).build();
		WebTarget target = client.target("http://localhost:8080/altkom/auctions/stream");
		// .property(HttpAuthenticationFeature.HTTP_AUTHENTICATION_BASIC_USERNAME,
		// "actuator")
		// .property(HttpAuthenticationFeature.HTTP_AUTHENTICATION_BASIC_PASSWORD,
		// "!#%asdActuator~1@12");
		EventSource eventSource = EventSource.target(target).reconnectingEvery(10, TimeUnit.SECONDS).build();
		EventListener listener = new EventListener() {
			@Override
			public void onEvent(InboundEvent inboundEvent) {
				System.out.println(inboundEvent);
				System.out.println(inboundEvent.getName() + "; " + inboundEvent.readData(String.class));
			}
		};
		eventSource.register(listener);
		eventSource.open();
		// EventSource eventSource = new EventSource(target) {
		// @Override
		// public void onEvent(InboundEvent inboundEvent) {
		// System.out.println(inboundEvent);
		// }
		// };

		// EventInput e = null;
		// while (true) {
		// Thread.sleep(1000);
		// if (e == null || e.isClosed()) {
		// // (re)connect
		// e = target.request().get(EventInput.class);
		//
		// e.setChunkType("text/event-stream");
		// }
		//
		// final InboundEvent inboundEvent = e.read();
		// if (inboundEvent == null) {
		// break;
		// } else {
		// String data = new String(inboundEvent.getRawData());
		// // do something here - notify observers, parse json etc
		// }
		//
		// }

		System.out.println("Connected to SSE source...");
		try {
			Thread.sleep(550000);
		} catch (InterruptedException ie) {
			System.err.println("Exception: " + ie.getMessage());
		}
		eventSource.close();
		System.out.println("Closed connection to SSE source");

	}

}
