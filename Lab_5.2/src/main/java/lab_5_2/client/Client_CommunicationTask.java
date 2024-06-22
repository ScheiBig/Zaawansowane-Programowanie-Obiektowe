package lab_5_2.client;

import javafx.concurrent.Task;
import lab_5_2.Config;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import utilx.result.Res;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("Convert2MethodRef")
public class Client_CommunicationTask
		extends Task<Client_CommunicationTask.Result>
{

	private final Socket socket;
	private final ObjectOutputStream out;
	private final long debounce_ms;
	private final BlockingDeque<Double> messageQueue;
	private long lastMessage_timestamp;

	public Client_CommunicationTask(
			Socket socket,
			long debounce_ms
	)
	throws IOException {
		this.socket = socket;
		this.out = new ObjectOutputStream(socket.getOutputStream());
		this.debounce_ms = debounce_ms;
		this.messageQueue = new LinkedBlockingDeque<>();
	}

	public boolean add(Double sliderValue) {
		if (System.currentTimeMillis() - this.lastMessage_timestamp < this.debounce_ms) {
			return false;
		}
		this.lastMessage_timestamp = System.currentTimeMillis();
		return this.messageQueue.add(sliderValue);
	}

	@Override
	protected Result call() {
		while (true) {
			var msg_res = Res.from(() -> this.messageQueue.poll(Config.ClientPollTimeout_ms, TimeUnit.MILLISECONDS));
			if (msg_res.err() != null) {
				Res.from(() -> this.socket.close());
				return Result.CancellationError.withError(msg_res.err());
	 		}

			var send_res = Res.from(() -> this.out.writeObject(msg_res.val()));
			if (send_res.err() != null) {
				Res.from(() -> this.socket.close());
				return Result.IOError.withError(send_res.err());

			}
		}
	}

	public enum Result {
		IOError("Błąd I/O podczas próby wysłania instrukcji sterujących do serwera"),
		CancellationError("Przerwano komunikację w trakcie oczekiwania na zmiany w sterowaniu");


		private final String repr;
		public @Nullable Throwable error;

		Result(String repr) {
			this.repr = repr;
		}

		@Override
		public String toString() {
			return this.repr;
		}

		public Result withError(Throwable error) {
			this.error = error;
			return this;
		}
	}
}
