package lab_5_1.net.client;

import lab_5_1.net.Msg;
import utilx.concurrent.locks.Monitor;
import utilx.net.SocketIO;

import java.io.IOException;
import java.util.Set;

public class Client_ReadThread
		extends Thread
{

	private final SocketIO.ObjectStreamIO socketIO;
	private final Runnable usernameInitializer;
	private final Monitor outLock;

	public Client_ReadThread(
			SocketIO.ObjectStreamIO socketIO,
			Runnable usernameInitializer,
			Monitor outLock
	) {
		this.socketIO = socketIO;
		this.usernameInitializer = usernameInitializer;
		this.outLock = outLock;
	}

	@Override
	public void run() {
		try (
				var pull = socketIO.in();
		) {
			Object resp;
			loop:
			while (true) {
				switch (resp = pull.readObject()) {
					case String str -> {
						var tokens = str.split(" # ", 2);
						if (tokens.length != 2) {
							this.outLock.lock();
							try {
								System.err.println("~SERVER~: " + str);
							} finally {
								this.outLock.unlock();
							}
							break;
						}
						if (tokens[0].isBlank()) {
							this.outLock.lock();
							try {
								System.err.println("~SERVER~: " + tokens[1]);
							} finally {
								this.outLock.unlock();
							}
							break;
						}
						if (tokens[1].equals(Msg.Success) && tokens[0].equals(Msg.Register.Cmd)) {
							this.outLock.lock();
							try {
								System.out.println("~SERVER~: Successfully registered.");
							} finally {
								this.outLock.unlock();
							}
							if (tokens[0].startsWith(Msg.Register.Cmd) &&
									tokens[1].equals(Msg.Success)) {
								this.usernameInitializer.run();
							}
							break;
						}
						if (!tokens[1].equals(Msg.Success)) {
							System.err.println("~SERVER~: " + tokens[1]);
							System.err.println("\t@ " + tokens[0]);
						}
					}

					case Msg.SendTo msg -> {
						this.outLock.lock();
						try {
							System.out.println(msg.username + ": " + msg.message);
						} finally {
							this.outLock.unlock();
						}
					}

					// Cannot pattern match generics, because Java decided to be
					// TOO BACKWARDS COMPATIBLE
					//noinspection rawtypes
					case Set users -> {
						this.outLock.lock();
						try {
							if (users.isEmpty()) {
								System.out.println("~AVAILABLE USERS : nobody~");
							} else {
								System.out.println("~AVAILABLE USERS~: [");
								for (var user : users) {
									System.out.println("\t" + user);
								}
								System.out.println("]");
							}
						} finally {
							this.outLock.unlock();
						}
					}

					case Boolean bool -> {
						this.outLock.lock();
						try {
							if (bool) {
								System.out.println("~SERVER~: Cleanup completed. Goodbye!");
							} else {
								System.out.println("~SERVER~: Cleanup unnecessary. Goodbye!");
							}
						} finally {
							this.outLock.unlock();
						}
						break loop;
					}

					default -> {
						System.err.println("~UNKNOWN RESPONSE~: " + resp);
					}
				}
			}
		} catch (IOException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}
