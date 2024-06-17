package lab_5_1.net.client;

import lab_5_1.net.Msg;
import org.jetbrains.annotations.Nullable;
import utilx.net.SocketIO;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;

public class Client_WriteThread
		extends Thread
{

	private final SocketIO.ObjectStreamIO socketIO;
	private final AtomicReference<@Nullable String> username = new AtomicReference<>();

	public Client_WriteThread(SocketIO.ObjectStreamIO socketIO) {
		this.socketIO = socketIO;

		this.username.set(null);
	}

	public void initUsername(String username) {
		this.username.set(username);
	}

	@Override
	public void run() {
		try (
				var push = socketIO.out();
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
						if (this.username.get() != null) {
							System.err.println("Already registered");
							continue;
						}
						if (input.length != 2) {
							System.err.println("""
									Username required.
									Type `HELP REGISTER` for details""");
							continue;
						}
						push.writeObject(new Msg.Register(input[1]));
					}
					case Msg.ListUsers.Cmd -> {
						push.writeObject(new Msg.ListUsers());
					}
					case Msg.All.Cmd -> {
						if (this.username.get() == null) {
							System.err.println("You need to register first");
							continue;
						}
						if (input.length != 2) {
							System.err.println("""
									Message required.
									Type `HELP ALL` for details.""");
							continue;
						}
						push.writeObject(new Msg.All(input[1]));
					}
					case Msg.Exit.Cmd -> {
						push.writeObject(new Msg.Exit(this.username.get()));
						break loop;
					}
					case Msg.Help.Cmd -> {
						if (input.length == 1) {
							System.out.println(Msg.Help.help());
						} else {
							switch (input[1]) {
								case Msg.Register.Cmd -> System.out.println(Msg.Register.help());
								case Msg.ListUsers.Cmd -> System.out.println(Msg.ListUsers.help());
								case Msg.All.Cmd -> System.out.println(Msg.All.help());
								case Msg.SendTo.Cmd -> System.out.println(Msg.SendTo.help());
								case Msg.Exit.Cmd -> System.out.println(Msg.Exit.help());
								case Msg.Help.Cmd -> System.out.println(Msg.Help.help());
								default -> System.err.println(Msg.UnknownCommand);
							}
						}
					}
					default -> {
						if (this.username.get() == null) {
							System.err.println("You need to register first");
							continue;
						}
						if (input.length != 2) {
							System.err.println("""
									Message required.
									Type `HELP SEND_TO` for details.""");
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
