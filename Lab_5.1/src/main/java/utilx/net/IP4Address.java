package utilx.net;

import java.net.InetAddress;
import java.net.Socket;

public record IP4Address(InetAddress address, int port) {

	public IP4Address(Socket socket) {
		this(socket.getInetAddress(), socket.getPort());
	}
}
