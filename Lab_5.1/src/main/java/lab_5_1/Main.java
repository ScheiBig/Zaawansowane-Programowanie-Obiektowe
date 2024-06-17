package lab_5_1;


public class Main {

	public static void main(String[] args) {
		if (args.length == 0) {
			System.err.println("Not provided app mode");
		} else {
			switch (args[0]) {
				case "client" -> MainClient.mainClient();
				case "server" -> MainServer.mainServer();
				default -> System.err.println("Unknown app mode: " + args[0]);
			}
		}
	}
}
