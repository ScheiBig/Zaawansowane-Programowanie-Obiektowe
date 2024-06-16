package lab_5_1.net.server;

import utilx.concurrent.locks.Monitor;
import lab_5_1.net.Msg;
import org.jetbrains.annotations.Nullable;
import utilx.net.IP4Address;
import utilx.net.SocketIO;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.concurrent.BlockingDeque;

public class ReadThread
		extends Thread
{

	private @Nullable String username;
	private final Monitor usernameSignal;
	private final SocketIO.ObjectStreamIO socketIO;
	private final IP4Address socketAddress;
	private final Map<String, BlockingDeque<Msg>> userMessageQueues;
	private final Monitor queueEmptyStatus;

	public ReadThread(
			SocketIO.ObjectStreamIO socketIO,
			IP4Address socketAddress,
			Map<String, BlockingDeque<Msg>> userMessageQueues,
			Monitor queueEmptyStatus
	) {
		this.username = null;
		this.socketIO = socketIO;
		this.socketAddress = socketAddress;
		this.userMessageQueues = userMessageQueues;
		this.queueEmptyStatus = queueEmptyStatus;
		this.usernameSignal = new Monitor();

		this.setName(this.getLogHead());
	}


	@Override
	public void run() {
		try (
				var push = this.socketIO.out();
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
		return "[Server::Read # " + this.username + " @ " + this.socketAddress.address()
				.getHostAddress() + ":" + this.socketAddress.port() + "]";

	}
}
