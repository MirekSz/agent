public class PusherExample {

	public static void main(String[] args) throws Exception {
		for (int i = 1; i < 100; i++) {
			new Thread(new PusherCLient()).start();
		}
		Thread.sleep(100000);
		// Pusher pushers = new Pusher("633478", "72dcaea876d66fba5d7c",
		// "1a7c2f8b97972d3cbb98");
		// pushers.setCluster("eu");
		// pushers.setEncrypted(true);
		//
		// pushers.trigger("my-channel", "my-event",
		// Collections.singletonMap("message", "hello world"));
		// Thread.sleep(10000);

	}

}
