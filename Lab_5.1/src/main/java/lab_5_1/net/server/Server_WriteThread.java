package lab_5_1.net.server;

import lab_5_1.net.Msg;
import org.jetbrains.annotations.Nullable;
import utilx.concurrent.locks.Monitor;
import utilx.net.IP4Address;
import utilx.net.SocketIO;

import java.io.IOException;
import java.net.SocketException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.Consumer;

public class Server_WriteThread
		extends Thread
{

	private @Nullable String username;
	private final SocketIO.ObjectStreamIO socketIO;
	private final IP4Address socketAddress;
	private final Map<String, BlockingDeque<Msg>> userMessageQueues;
	private final Monitor usersLock;
	private final Monitor queueEmptyStatus;
	private final Consumer<String> usernameInitializer;
	private final Runnable readInterrupt;

	public Server_WriteThread(
			SocketIO.ObjectStreamIO socketIO,
			IP4Address socketAddress,
			Map<String, BlockingDeque<Msg>> userMessageQueues,
			Monitor queueEmptyStatus,
			Monitor usersLock,
			Consumer<String> usernameInitializer,
			Runnable readInterrupt
	) {
		this.username = null;
		this.socketIO = socketIO;
		this.socketAddress = socketAddress;
		this.userMessageQueues = userMessageQueues;
		this.queueEmptyStatus = queueEmptyStatus;
		this.usersLock = usersLock;
		this.usernameInitializer = usernameInitializer;
		this.readInterrupt = readInterrupt;

		this.setName(this.getLogHead());
	}

	@Override
	public void run() {
		try (
				var push = socketIO.out();
				var pull = socketIO.in();
		) {
			/*
			 * Main processing loop of client communicates
			 */
			Object obj;
			loop:
			while (true) {
				switch (obj = pull.readObject()) {
					/*
					 *  Register message initializes client on server:
					 *  - if client already is registered => error,
					 *  - if name is blank, or contains non-alphanum => error,
					 *  - if client username already taken => error,
					 *  - if client username is used as command name => error,
					 * otherwise - success.
					 */
					case Msg.Register msg -> {
						System.out.println(this.getLogHead() + " " + msg);
						if (this.username != null) {
							push.writeObject(msg.cmd + " # " + Msg.AlreadyRegistered);
							System.out.println(this.getLogHead() +
									" already registered as: " +
									this.username);
							break;
						}
						if (Msg.Commands.contains(msg.username) ||
								msg.username.isBlank() ||
								!msg.username.replaceAll("[a-zA-Z0-9]", "")
										.isEmpty()) {
							push.writeObject(msg.cmd + " # " + Msg.UsernameInvalid);
							System.out.println(this.getLogHead() +
									" invalid username: " +
									msg.username);
							break;
						}
						this.usersLock.lock();
						try {
							if (this.userMessageQueues.containsKey(msg.username)) {
								push.writeObject(msg.cmd + " # " + Msg.UsernameTaken);
								System.out.println(this.getLogHead() +
										" already taken username:" +
										" " +
										msg.username);
								break;
							}
							var queue = new LinkedBlockingDeque<Msg>(32);
							this.userMessageQueues.put(msg.username, queue);
							this.username = msg.username;
							push.writeObject(msg.cmd + " # " + Msg.Success);
							this.usernameInitializer.accept(this.username);
							System.out.println(this.getLogHead() +
									" successful register: " +
									this.username);
							this.setName(this.getLogHead());
						} finally {
							this.usersLock.unlock();
						}
					}

					/*
					 * List user message immediately responds with list of registered names.
					 */
					case Msg.ListUsers msg -> {
						System.out.println(this.getLogHead() + " " + msg);
						push.writeObject(Set.copyOf(this.userMessageQueues.keySet()));
					}

					/*
					 * Send message to all users in chat. Will fail if none are present.
					 */
					case Msg.All msg -> {
						System.out.println(this.getLogHead() + " " + msg);
						if (this.username == null) {
							push.writeObject(msg.cmd + " # " + Msg.NotRegistered);
							System.out.println(this.getLogHead() + " user not registered");
							break;
						}
						this.usersLock.lock();
						try {
							var others = this.userMessageQueues.entrySet()
									.stream()
									.filter(e -> !e.getKey()
											.equals(this.username))
									.map(Map.Entry::getValue)
									.toList();
							if (others.isEmpty()) {
								push.writeObject(msg.cmd + " # " + Msg.NoUsers);
								System.out.println(this.getLogHead() + " no other users");
								break;
							}
							for (var q : others) {
								if (!(q.peekLast() instanceof Msg.Exit)) {
									q.putLast(new Msg.SendTo(this.username, msg.message));
								}
							}
							push.writeObject(msg.cmd + " # " + Msg.Success);
							System.out.println(this.getLogHead() + " successful broadcast");
						} finally {
							this.usersLock.unlock();
						}
					}

					/*
					 * Send message to specified user in chat. Will fail if user is not present.
					 */
					case Msg.SendTo msg -> {
						System.out.println(this.getLogHead() + " " + msg);
						if (this.username == null) {
							push.writeObject(msg.cmd + " # " + Msg.NotRegistered);
							System.out.println(this.getLogHead() + " user not registered");
							break;
						}
						this.usersLock.lock();
						try {
							var user = this.userMessageQueues.get(msg.username);
							if (user == null || user.peekLast() instanceof Msg.Exit) {
								push.writeObject(msg.cmd + " # " + Msg.NoSuchUser);
								System.out.println(this.getLogHead() +
										" no such user: " +
										msg.username);
								break;
							}
							user.putLast(new Msg.SendTo(this.username, msg.message));
							push.writeObject(msg.cmd + " # " + Msg.Success);
							System.out.println(this.getLogHead() + " successful send");
						} finally {
							this.usersLock.unlock();
						}
					}

					/*
					 * Exit message returns status code, that indicates whether cleanup was
					 * necessary for client:
					 * - unregistered client => false,
					 * - registered client => true,
					 * additionally, for registered client, thread waits until queue is emptied;
					 * to do so, it places Exit message into queue - reader upon receiving exit
					 * message immediately clears rest of queue, signals cleanup readiness and
					 * shuts down.
					 */
					case Msg.Exit msg -> {
						System.out.println(this.getLogHead() + " " + msg);
						if (this.username == null) {
							push.writeObject(false);
							System.out.println(this.getLogHead() +
									" cleanup unnecessary # " +
									msg.username);
						} else {
							System.out.println(this.getLogHead() +
									" cleanup necessary # " +
									msg.username);
							this.queueEmptyStatus.lock();
							try {
								if (!this.userMessageQueues.get(this.username)
										.isEmpty()) {
									System.out.println(this.getLogHead() +
											" cleanup awaiting " +
											"empty message queue # " +
											msg.username);
									this.userMessageQueues.get(this.username)
											.putLast(msg);
									this.queueEmptyStatus.await();
								} else {
									System.out.println(this.getLogHead() +
											" message queue empty, signaling exit # " +
											msg.username);
									this.userMessageQueues.get(this.username)
											.putLast(msg);
								}
								this.usersLock.lock();
								try {
									this.userMessageQueues.remove(this.username);
								} finally {
									this.usersLock.unlock();
								}
							} catch (InterruptedException e) {
								throw new RuntimeException(e);
							} finally {
								this.queueEmptyStatus.unlock();
							}
							push.writeObject(true);
							System.out.println(this.getLogHead() +
									" cleanup successful # " +
									msg.username);
						}
						break loop;
					}

					default -> {
						System.err.println(this.getLogHead() + " unknown command :: " + obj);
						push.writeObject(" # " + Msg.UnknownCommand);
					}
				}
			}
		} catch (SocketException e) {
			this.usersLock.lock();
			try {
				if (this.username != null) {
					this.userMessageQueues.remove(this.username);
				}
				this.readInterrupt.run();
			} finally {
				this.usersLock.unlock();
			}

		} catch (IOException | ClassNotFoundException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private String getLogHead() {
		return "[Server::Write # " +
				this.username +
				" @ " +
				this.socketAddress.address()
						.getHostAddress() +
				":" +
				this.socketAddress.port() +
				"]";

	}
}
