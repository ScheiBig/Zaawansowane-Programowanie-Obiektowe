package lab_5_1.net.server;

import lab_5_1.concurrent.locks.Monitor;
import lab_5_1.net.Msg;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.BlockingDeque;

public class ReadThread
		extends Thread
{

	private @Nullable String username;
	private final Monitor usernameSignal;
	private final Socket socket;
	private final Map<String, BlockingDeque<Msg>> userMessageQueues;
	private final Monitor queueEmptyStatus;

	public ReadThread(
			Socket socket,
			Map<String, BlockingDeque<Msg>> userMessageQueues,
			Monitor queueEmptyStatus
	) {
		this.username = null;
		this.socket = socket;
		this.userMessageQueues = userMessageQueues;
		this.queueEmptyStatus = queueEmptyStatus;
		usernameSignal = new Monitor();
	}


	@Override
	public void run() {
		try (
				var push = new ObjectOutputStream(socket.getOutputStream());
		) {
			this.usernameSignal.lock();
			try {
				if (this.username == null) {
					this.usernameSignal.await();
				}
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			} finally {
				this.usernameSignal.unlock();
			}
			var queue = userMessageQueues.get(this.username);
			while (true) {
				var msg = queue.takeFirst();
				if (msg instanceof Msg.SendTo) {
					push.writeObject(msg);
					System.out.println(this.getLogHead() + " sending message :: " + msg);
				} else if (msg instanceof Msg.Exit) {
					System.out.println(this.getLogHead() + " queue emptied, signaling exit");
					this.queueEmptyStatus.lock();
					try {
						this.queueEmptyStatus.signal();
					} finally {
						this.queueEmptyStatus.unlock();
					}
					break;
				} else {
					System.err.println(this.getLogHead() + " unknown command :: " + msg);
				}
			}
		} catch (InterruptedException | IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void initUsername(String username) {
		this.usernameSignal.lock();
		try {
			this.username = username;
			this.usernameSignal.signal();
		} finally {
			this.usernameSignal.unlock();
		}
	}

	private String getLogHead() {
		return "[Server::Read # " + this.username + " @ " + this.socket.getInetAddress()
				.getHostAddress() + ":" + this.socket.getPort() + "]";

	}
}
