package lab_5_1.net.server;


import lab_5_1.Config;
import lab_5_1.concurrent.locks.Monitor;
import lab_5_1.net.Msg;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class Bundle_1__WriteThreadTests {

	private Map<String, BlockingDeque<Msg>> userMessageQueues;
	private Monitor usersLock;
	private Map<String, Monitor> userQueueStatuses;
	private Socket clientSocket;
	private Socket serverSocket;
	private WriteThread serverWriter;

	private static final String ClientName = "User";
	private static final String TestUserName = "Test";

	private ObjectOutputStream client_push;
	private ObjectInputStream client_pull;

	@BeforeEach
	void setUp()
	throws IOException, InterruptedException {

		var server = new ServerSocket(Config.Port);
		var _serverSocket = new Socket[1];
		var th = new Thread(() -> {
			try {
				_serverSocket[0] = server.accept();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
		th.start();
		clientSocket = new Socket("localhost", Config.Port);
		client_push = new ObjectOutputStream(clientSocket.getOutputStream());
		th.join();
		serverSocket = _serverSocket[0];
		userMessageQueues = new HashMap<>();
		userQueueStatuses = new HashMap<>();
		userQueueStatuses.put(ClientName, new Monitor());
		userQueueStatuses.put(TestUserName, new Monitor());
		userMessageQueues.put(TestUserName, new LinkedBlockingDeque<>());
		usersLock = new Monitor();
		serverWriter = new WriteThread(
				serverSocket,
				userMessageQueues,
				userQueueStatuses.get(ClientName),
				usersLock,
				(s) -> {System.out.println("[Read Thread Mockup] Initialized username " + s);}
		);
		serverWriter.start();
		serverWriter.setUncaughtExceptionHandler((t, e) -> {});
		client_pull = new ObjectInputStream(clientSocket.getInputStream());

		server.close();
	}

	@AfterEach
	void tearDown()
	throws IOException {
		serverWriter.interrupt();
		serverSocket.close();
		clientSocket.close();
	}

	@Order(1)
	@Test
	void Test_unregistered_user_errors()
	throws IOException, ClassNotFoundException {

		client_push.writeObject(new Msg.SendTo(TestUserName, "Hello"));
		var obj = client_pull.readObject();
		Assertions.assertInstanceOf(Msg.NotRegistered.getClass(), obj);
		var msg = (String) obj;
		Assertions.assertEquals(Msg.NotRegistered, msg);

		client_push.writeObject(new Msg.All("Hello"));
		obj = client_pull.readObject();
		Assertions.assertInstanceOf(Msg.NotRegistered.getClass(), obj);
		msg = (String) obj;
		Assertions.assertEquals(Msg.NotRegistered, msg);
	}

	@Order(2)
	@Test
	void Test_listing_users()
	throws IOException, ClassNotFoundException {

		client_push.writeObject(new Msg.ListUsers());
		var obj = client_pull.readObject();
		Assertions.assertInstanceOf(Set.class, obj);
		@SuppressWarnings("unchecked") var msg = (Set<String>) obj;
		Assertions.assertEquals(Set.of(TestUserName), msg);
	}

	@Order(3)
	@Test
	void Test_exit_without_registration()
	throws IOException, ClassNotFoundException {

		client_push.writeObject(new Msg.Exit(null));
		var obj = client_pull.readObject();
		Assertions.assertInstanceOf(Boolean.class, obj);
		var msg = (Boolean) obj;
		Assertions.assertEquals(false, msg);
	}

	@Order(4)
	@Test
	void Test_registration_invalid_name_errors()
	throws IOException, ClassNotFoundException {

		var invalidNames = List.of("", "a a", "b\nc", "       ", "\n\r\t", "...,,");

		for (var name : invalidNames) {
			client_push.writeObject(new Msg.Register(name));
			var obj = client_pull.readObject();
			Assertions.assertInstanceOf(String.class, obj);
			var msg = (String) obj;
			Assertions.assertEquals(Msg.UsernameInvalid, msg);

		}
	}

	@Order(5)
	@Test
	void Test_registration_reserved_name_errors()
	throws IOException, ClassNotFoundException, NoSuchFieldException, IllegalAccessException {

		var invalidNames = Msg.class.getClasses();

		for (var in : invalidNames) {
			var name = in.getField("Cmd")
					.get(null)
					.toString();

			client_push.writeObject(new Msg.Register(name));
			var obj = client_pull.readObject();
			Assertions.assertInstanceOf(String.class, obj);
			var msg = (String) obj;
			Assertions.assertEquals(Msg.UsernameInvalid, msg);
		}
	}

	@Order(6)
	@Test
	void Test_registration_taken_name_errors()
	throws IOException, ClassNotFoundException {

		client_push.writeObject(new Msg.Register(TestUserName));
		var obj = client_pull.readObject();
		Assertions.assertInstanceOf(String.class, obj);
		var msg = (String) obj;
		Assertions.assertEquals(Msg.UsernameTaken, msg);
	}

	void register()
	throws IOException, ClassNotFoundException {

		client_push.writeObject(new Msg.Register(ClientName));
		var obj = client_pull.readObject();
		Assertions.assertInstanceOf(String.class, obj);
		var msg = (String) obj;
		Assertions.assertEquals(Msg.Success, msg);
	}

	@Order(7)
	@Test
	void Test_registration()
	throws IOException, ClassNotFoundException {
		register();
		usersLock.lock();
		try {
			Assertions.assertTrue(userMessageQueues.containsKey(ClientName));
		} finally {
			usersLock.unlock();
		}
	}

	@Order(8)
	@Test
	void Test_second_registration_errors()
	throws IOException, ClassNotFoundException {
		register();
		client_push.writeObject(new Msg.Register(ClientName));
		var obj = client_pull.readObject();
		Assertions.assertInstanceOf(String.class, obj);
		var msg = (String) obj;
		Assertions.assertEquals(Msg.AlreadyRegistered, msg);
	}

	@Order(9)
	@Test
	void Test_exit_with_registration_and_no_messages()
	throws IOException, ClassNotFoundException, InterruptedException {
		register();

		client_push.writeObject(new Msg.Exit(ClientName));
		var obj = client_pull.readObject();
		Assertions.assertInstanceOf(Boolean.class, obj);
		var msg = (Boolean) obj;
		Assertions.assertEquals(true, msg);
		Assertions.assertFalse(userMessageQueues.containsKey(ClientName));
	}


	@Order(10)
	@Test
	void Test_exit_with_registration_and_messages()
	throws IOException, ClassNotFoundException, InterruptedException {
		register();

		var lock = userQueueStatuses.get(ClientName);
		var queue = userMessageQueues.get(ClientName);

		lock.lock();
		try {
			queue.put(new Msg.SendTo(TestUserName, "Hello"));
		} finally {
			lock.unlock();
		}
		client_push.writeObject(new Msg.Exit(ClientName));
		Thread.sleep(50); // Wait until serverWriter has chance to reada
		lock.lock();
		try {
			Assertions.assertIterableEquals(List.of(
					new Msg.SendTo(TestUserName, "Hello"),
					new Msg.Exit(ClientName)
			), queue);
			queue.clear();
			lock.signal();
		} finally {
			lock.unlock();
		}

		var obj = client_pull.readObject();
		Assertions.assertInstanceOf(Boolean.class, obj);
		var msg = (Boolean) obj;
		Assertions.assertEquals(true, msg);

		usersLock.lock();
		try {
			Assertions.assertFalse(userMessageQueues.containsKey(ClientName));
		} finally {
			usersLock.unlock();
		}
	}

	@Order(11)
	@Test
	void Test_broadcast_without_users_fails()
	throws IOException, ClassNotFoundException {
		userMessageQueues.remove(TestUserName);
		register();

		client_push.writeObject(new Msg.All("Hello"));
		var obj = client_pull.readObject();
		Assertions.assertInstanceOf(String.class, obj);
		var msg = (String) obj;
		Assertions.assertEquals(Msg.NoUsers, msg);
	}

	@Order(12)
	@Test
	void Test_broadcasting()
	throws IOException, ClassNotFoundException {
		register();

		client_push.writeObject(new Msg.All("Hello"));
		var obj = client_pull.readObject();
		Assertions.assertInstanceOf(String.class, obj);
		var msg = (String) obj;
		Assertions.assertEquals(Msg.Success, msg);

		Assertions.assertTrue(userMessageQueues.get(ClientName)
				.isEmpty());

		Assertions.assertIterableEquals(
				List.of(new Msg.SendTo(ClientName, "Hello")),
				userMessageQueues.get(TestUserName)
		);
	}

	@Order(13)
	@Test
	void Test_send_to_missing_user_fails()
	throws IOException, ClassNotFoundException {
		register();

		client_push.writeObject(new Msg.SendTo("Hello", "World"));
		var obj = client_pull.readObject();
		Assertions.assertInstanceOf(String.class, obj);
		var msg = (String) obj;
		Assertions.assertEquals(Msg.NoSuchUser, msg);
	}

	@Order(14)
	@Test
	void Test_sending()
	throws IOException, ClassNotFoundException {
		register();


		client_push.writeObject(new Msg.SendTo(TestUserName, "Hi"));
		var obj = client_pull.readObject();
		Assertions.assertInstanceOf(String.class, obj);
		var msg = (String) obj;
		Assertions.assertEquals(Msg.Success, msg);

		Assertions.assertIterableEquals(
				List.of(new Msg.SendTo(ClientName, "Hi")),
				userMessageQueues.get(TestUserName)
		);
	}

	@Order(15)
	@Test
	void Test_send_to_exiting_user_fails()
	throws IOException, ClassNotFoundException, InterruptedException {
		register();
		userMessageQueues.get(TestUserName)
				.put(new Msg.Exit(TestUserName));

		client_push.writeObject(new Msg.SendTo(TestUserName, "Hi"));
		var obj = client_pull.readObject();
		Assertions.assertInstanceOf(String.class, obj);
		var msg = (String) obj;
		Assertions.assertEquals(Msg.NoSuchUser, msg);
	}

	@Order(16)
	@Test
	void Test_unknown_command_errors()
	throws IOException, ClassNotFoundException {
		register();

		client_push.writeObject(420);
		var obj = client_pull.readObject();
		Assertions.assertInstanceOf(String.class, obj);
		var msg = (String) obj;
		Assertions.assertEquals(Msg.UnknownCommand, msg);

	}
}