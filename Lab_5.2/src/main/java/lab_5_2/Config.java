package lab_5_2;

public final class Config {

	public static final String Host = "localhost";
	public static final int Port = 42069;
	public static final int ClientConnectTimeout_ms = 10_000;
	public static final int ClientPollTimeout_ms = 1_000;
	public static final long SimulateLatency_ms = 2_000;
	public static final boolean ShowErrors = false;
	public static final int ServerIOTimeout_ms = 5_000;
	public static final int CommunicationDebounce_ms = 100;

	public static String prettyPrint(Throwable error) {
		if (error == null) {
			return "";
		}

		var className = error.getClass()
				.getName();

		var message = error.getMessage();

		return "[[" + className + "]] " + message;
	}

	public static void debugPrintError(Throwable error) {
		if (error == null) {
			return;
		}
		if (!ShowErrors) {
			return;
		}
		error.printStackTrace();
	}
}
