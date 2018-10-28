import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.SubscriptionEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;

public class PusherCLient implements Runnable {

	@Override
	public void run() {
		PusherOptions options = new PusherOptions().setCluster("eu");
		com.pusher.client.Pusher pusher = new com.pusher.client.Pusher("72dcaea876d66fba5d7c", options);

		pusher.connect(new ConnectionEventListener() {
			@Override
			public void onConnectionStateChange(ConnectionStateChange change) {
				System.out
						.println("State changed to " + change.getCurrentState() + " from " + change.getPreviousState());
			}

			@Override
			public void onError(String message, String code, Exception e) {
				System.out.println("There was a problem connecting!");
			}
		}, ConnectionState.ALL);

		// Subscribe to a channel
		Channel channel = pusher.subscribe("my-channel");

		// Bind to listen for events called "my-event" sent to "my-channel"
		channel.bind("my-event", new SubscriptionEventListener() {
			@Override
			public void onEvent(String channel, String event, String data) {
				System.out.println("Received event with data: " + data);
			}
		});

		// Disconnect from the service (or become disconnected my network
		// conditions)
		pusher.disconnect();

		// Reconnect, with all channel subscriptions and event bindings
		// automatically recreated
		pusher.connect();

	}

}
