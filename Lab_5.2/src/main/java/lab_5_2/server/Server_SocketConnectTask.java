package lab_5_2.server;

import javafx.concurrent.Task;
import org.jetbrains.annotations.Nullable;
import utilx.result.Res;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@SuppressWarnings("Convert2MethodRef")
public class Server_SocketConnectTask
		extends Task<Server_SocketConnectTask.Result>
{

	private final int port;

	public Server_SocketConnectTask(
			int port
	) {
		this.port = port;
	}

	@Override
	protected Result call() {

		var serverSocket_res = Res.from(() -> new ServerSocket(port));
		if (serverSocket_res.err() != null) {
			if (serverSocket_res.err() instanceof SecurityException) {
				return Result.SS_SecurityError.withError(serverSocket_res.err());
			}
			if (serverSocket_res.err() instanceof IOException) {
				return Result.SS_IOError.withError(serverSocket_res.err());
			}
			return null;
		}

		//noinspection resource
		var serverSocket = serverSocket_res.val();

		var socket_res = Res.from(() -> serverSocket.accept());
		if (socket_res.err() != null) {
			Res.from(() -> serverSocket.close());
			if (socket_res.err() instanceof SecurityException) {
				return Result.CS_SecurityError.withError(socket_res.err());
			}
			if (socket_res.err() instanceof IOException) {
				return Result.CS_IOError.withError(socket_res.err());
			}
			return null;
		}

		Res.from(() -> serverSocket.close());
		return Result.Success.withSocket(socket_res.val());
	}

	public enum Result {
		Success("Pomyślnie otrzymano połączenie od klienta"),
		SS_IOError("Błąd I/O podczas otwierania możliwości połączenia z serwerem"),
		SS_SecurityError(
				"Błąd dostępów lub zabezpieczeń podczas otwierania możliwości połączenia z " +
						"serwerem"),
		CS_IOError("Błąd I/O podczas oczekiwania na połączenie przez klienta"),
		CS_SecurityError(
				"Błąd dostępów lub zabezpieczeń podczas oczekiwania na połączenie przez klienta");


		private final String repr;
		public @Nullable Socket socket;
		public @Nullable Throwable error;

		Result(String repr) {
			this.repr = repr;
		}

		@Override
		public String toString() {
			return this.repr;
		}

		public Result withSocket(Socket socket) {
			this.socket = socket;
			return this;
		}

		public Result withError(Throwable error) {
			this.error = error;
			return this;
		}
	}
}
