package lab_5_1;

import lab_5_1.net.server.Server;

import java.io.IOException;

public class MainServer {
	public static void mainServer() {
		System.out.println("Launching server...");
		try {
			new Server(Config.Port).start();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		System.out.println("Press [Ctrl] + [C] to kill server");
		/*
		 * getch() loop instead of Server#join - so OS does not think, app is unresponsive
		 */

		//noinspection InfiniteLoopStatement
		while (true) {
			try {
				//noinspection ResultOfMethodCallIgnored
				System.in.read();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
