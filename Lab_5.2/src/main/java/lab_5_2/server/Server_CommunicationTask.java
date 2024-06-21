package lab_5_2.server;

import javafx.concurrent.Task;
import lab_5_2.Config;
import org.jetbrains.annotations.Nullable;
import utilx.result.Res;
import utilx.thread.IOTask;

import java.io.EOFException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.function.Consumer;

@SuppressWarnings("Convert2MethodRef")
public class Server_CommunicationTask extends Task<Server_CommunicationTask.Result> {

	private final Socket socket;
	private final Consumer<Double> messageConsumer;

	public Server_CommunicationTask(
			Socket socket,
			Consumer<Double> messageConsumer
	) {
		this.socket = socket;
		this.messageConsumer = messageConsumer;
	}

	@Override
	protected Result call()
	throws InterruptedException {
		var getIn_task = new IOTask<utilx.result.Result<ObjectInputStream, Throwable>>(this.socket) {
			@Override
			public utilx.result.Result<ObjectInputStream, Throwable> call(Object[] args) {
				return Res.from(() -> new ObjectInputStream(((Socket)args[0]).getInputStream()));
			}
		};
		getIn_task.start();
		getIn_task.join(Config.ServerIOTimeout_ms);

		if (getIn_task.isAlive()) {
			getIn_task.interrupt();
			Res.from(() -> this.socket.close());
			return Result.TimeoutError;
		}

		var in_res = getIn_task.result();
		if (in_res.err() != null) {
			Res.from(() -> this.socket.close());
			return Result.GIS_IOError.withError(in_res.err());
		}

		var in = in_res.val();
		while(true) {
			var msg_res = Res.from(() -> in.readObject());
			if (msg_res.err() != null) {
				Res.from(() -> this.socket.close());
				if (msg_res.err() instanceof EOFException) {
					return Result.RO_DisconnectWarning;
				}
				return Result.RO_IOError.withError(msg_res.err());
			}

			this.messageConsumer.accept((Double) msg_res.val());
		}
	}

	public enum Result {
		GIS_IOError("Błąd I/O podczas otwierania komunikacji z klientem"),
		TimeoutError("Przekroczono limit czasu podczas otwierania komunikacji z klientem"),
		RO_DisconnectWarning("Klient zakończył połączenie"),
		RO_IOError("Błąd I/O podczas próby odebrania instrukcji sterujących od klienta");


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
