package lab_5_1;

import lab_5_1.net.client.Client;

public class MainClient {
	public static void mainClient() {
		System.out.println("Launching client...");
		new Client(Config.Host, Config.Port).run();
		System.out.println("See you again ;)");
	}
}
