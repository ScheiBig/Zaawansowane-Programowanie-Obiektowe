package lab_5_1.net.client;

import lab_5_1.net.Msg;
import utilx.concurrent.locks.Monitor;
import utilx.net.SocketIO;

import java.io.IOException;
import java.net.Socket;

public class Client
		implements Runnable
{

	private final String host;
	private final int port;
	private final Monitor outLock;

	public Client(
			String host,
			int port
	) {
		this.host = host;
		this.port = port;
		this.outLock = new Monitor();
	}

	@Override
	public void run() {
		try {
			var socket = new Socket(this.host, this.port);
			System.out.println("~CLIENT~: Connected to server @ " + this.host + ":" + this.port);
			System.out.println("""
					\tType `LIST_USERS` to get list of other char users.
					\tType `REGISTER` <username> to register.
					\tType `HELP` to see available commands.""");
			var socketIO = new SocketIO(socket).objectStreams();
			var write = new Client_WriteThread(socketIO, this.outLock);
			write.start();
			var read = new Client_ReadThread(socketIO, write::initUsername, this.outLock);
			read.start();

			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				try {
					if (!socket.isClosed()) {
						socketIO.out()
								.writeObject(new Msg.Exit(null));
					}
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}));

			write.join();
			read.join();
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(e);
		}

	}
}
