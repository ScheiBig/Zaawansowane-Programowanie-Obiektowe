package lab_5_2.client;

import javafx.concurrent.Task;
import lab_5_2.Config;
import org.jetbrains.annotations.Nullable;
import utilx.result.Res;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class Client_SocketConnectTask
		extends Task<Client_SocketConnectTask.Result>
{

	private final InetSocketAddress address;
	private final int timeout_ms;

	public Client_SocketConnectTask(
			String host,
			int port,
			int timeout_ms
	) {
		this.address = new InetSocketAddress(host, port);
		this.timeout_ms = timeout_ms;
	}

	@Override
	protected Result call() {
		// Simulate network latency
		var sleep_res = Res.from(() -> Thread.sleep(Config.SimulateLatency_ms));
		if (sleep_res.err() != null) {
			return Result.CancellationError.withError(sleep_res.err());
		}

		var socket = new Socket();

		var connect_res = Res.from(() -> socket.connect(this.address, this.timeout_ms));
		if (connect_res.err() != null) {
			if (connect_res.err() instanceof SocketTimeoutException) {
				return Result.TimeoutError.withError(connect_res.err());
			}
			if (connect_res.err() instanceof IOException) {
				return Result.IOError.withError(connect_res.err());
			}
			return null;
		}

		return Result.Success.withSocket(socket);
	}


	public enum Result {
		Success("Pomyślnie połączono z serwerem"),
		IOError("Błąd I/O podczas próby połączenia z serwerem"),
		TimeoutError("Przekroczono limit czasu podczas próby połączenia z serwerem"),
		CancellationError("Przerwano próbę połączenia z serwerem");


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
