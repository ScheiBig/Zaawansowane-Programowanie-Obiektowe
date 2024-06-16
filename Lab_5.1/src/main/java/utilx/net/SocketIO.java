package utilx.net;

import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.Socket;

public class SocketIO {

	private @Nullable InputStream in;
	private @Nullable OutputStream out;

	public SocketIO(Socket socket)
	throws IOException {
		this.out = socket.getOutputStream();
		this.in = socket.getInputStream();
	}

	public record ObjectStreamIO(ObjectOutputStream out, ObjectInputStream in) {}

	public ObjectStreamIO objectStreams()
	throws IOException {
		var streams = new ObjectStreamIO(
				new ObjectOutputStream(this.out),
				new ObjectInputStream(this.in)
		);

		this.out = null;
		this.in = null;

		return streams;
	}
}
