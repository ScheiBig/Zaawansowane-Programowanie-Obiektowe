package lab_5_1.net.server;

import lab_5_1.net.Msg;
import utilx.concurrent.locks.Monitor;
import utilx.net.IP4Address;
import utilx.net.SocketIO;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingDeque;

public class Server
		extends Thread
{

	private final int port;
	private final Map<String, BlockingDeque<Msg>> userMessageQueues;
	private final Monitor usersLock;

	public Server(int port)
	throws IOException {
		userMessageQueues = new HashMap<>();
		this.port = port;
		usersLock = new Monitor();
	}

	@Override
	public void run() {
		try (var socketServer = new ServerSocket(this.port)) {
			System.out.println("[Server]: Accepting client connections @ localhost:" + this.port);
			while (true) {
				var socket = socketServer.accept();
				System.out.println("[Server]: Accepted client from @ " +
						socket.getInetAddress()
								.getHostAddress() +
						":" +
						socket.getPort());
				var queueEmptyStatus = new Monitor();
				var socketIO = new SocketIO(socket).objectStreams();
				var socketAddress = new IP4Address(socket);
				var rt = new Server_ReadThread(
						socketIO,
						socketAddress,
						this.userMessageQueues,
						queueEmptyStatus
				);
				rt.start();
				new Server_WriteThread(
						socketIO,
						socketAddress,
						this.userMessageQueues,
						queueEmptyStatus,
						this.usersLock,
						rt::initUsername
				).start();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
