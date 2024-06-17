package lab_5_1.net.client;

import lab_5_1.net.Msg;
import org.jetbrains.annotations.Nullable;
import utilx.concurrent.locks.Monitor;
import utilx.net.SocketIO;

import java.io.IOException;
import java.util.Scanner;

public class Client_WriteThread
		extends Thread
{

	private final SocketIO.ObjectStreamIO socketIO;
	private final Monitor outLock;
	private @Nullable String username;
	private @Nullable String pendingUsername;

	public Client_WriteThread(
			SocketIO.ObjectStreamIO socketIO,
			Monitor outLock
	) {
		this.socketIO = socketIO;
		this.outLock = outLock;

		this.username = null;
	}

	public void initUsername() {
		this.username = this.pendingUsername;
	}

	@Override
	public void run() {
		// Client_ReadThread is responsible for cleanup
		//noinspection resource
		var push = socketIO.out();
		try (
				var scanner = new Scanner(System.in);
		) {
			String[] input;
			loop:
			while (true) {
				input = scanner.nextLine()
						.split(" ", 2);
				if (input.length == 0 || input[0].isBlank()) {
					continue;
				}

				switch (input[0]) {
					case Msg.Register.Cmd -> {
						if (this.username != null) {
							this.outLock.lock();
							try {
								System.err.println("Already registered");
							} finally {
								this.outLock.unlock();
							}
							continue;
						}
						if (input.length != 2) {
							this.outLock.lock();
							try {
								System.err.println("""
										Username required.
										Type `HELP REGISTER` for details""");
							} finally {
								this.outLock.unlock();
							}
							continue;
						}
						this.pendingUsername = input[1];
						push.writeObject(new Msg.Register(this.pendingUsername));
					}
					case Msg.ListUsers.Cmd -> {
						push.writeObject(new Msg.ListUsers());
					}
					case Msg.All.Cmd -> {
						if (this.username == null) {
							this.outLock.lock();
							try {
								System.err.println("You need to register first");
							} finally {
								this.outLock.unlock();
							}
							continue;
						}
						if (input.length != 2) {
							this.outLock.lock();
							try {
								System.err.println("""
										Message required.
										Type `HELP ALL` for details.""");
							} finally {
								this.outLock.unlock();
							}
							continue;
						}
						push.writeObject(new Msg.All(input[1]));
					}
					case Msg.Exit.Cmd -> {
						push.writeObject(new Msg.Exit(this.username));
						break loop;
					}
					case Msg.Help.Cmd -> {
						this.outLock.lock();
						try {
							if (input.length == 1) {
								System.out.println(Msg.Help.help());
							} else {
								switch (input[1]) {
									case Msg.Register.Cmd ->
											System.out.println(Msg.Register.help());
									case Msg.ListUsers.Cmd ->
											System.out.println(Msg.ListUsers.help());
									case Msg.All.Cmd -> System.out.println(Msg.All.help());
									case Msg.SendTo.Cmd -> System.out.println(Msg.SendTo.help());
									case Msg.Exit.Cmd -> System.out.println(Msg.Exit.help());
									case Msg.Help.Cmd -> System.out.println(Msg.Help.help());
									default -> System.err.println(Msg.UnknownCommand);
								}
							}
						} finally {
							this.outLock.unlock();
						}
					}
					default -> {
						if (this.username == null) {
							this.outLock.lock();
							try {
								System.err.println("You need to register first");
							} finally {
								this.outLock.unlock();
							}
							continue;
						}
						if (input.length != 2) {
							this.outLock.lock();
							try {
								System.err.println("""
										Message required.
										Type `HELP SEND_TO` for details.""");
							} finally {
								this.outLock.unlock();
							}
							continue;
						}
						push.writeObject(new Msg.SendTo(input[0], input[1]));
					}

				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
