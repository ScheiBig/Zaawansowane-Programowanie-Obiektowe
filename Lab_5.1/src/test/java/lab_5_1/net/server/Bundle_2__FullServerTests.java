package lab_5_1.net.server;

import lab_5_1.Config;
import lab_5_1.net.Msg;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Bundle_2__FullServerTests {

	private Thread serverThread;

	private static final String ClientUserName = "User";
	private static final String TestUserName = "Test";
	private Socket clientSocket;
	private ObjectOutputStream client_push;
	private ObjectInputStream client_pull;
	private Socket testSocket;
	private ObjectOutputStream test_push;
	private ObjectInputStream test_pull;

	@BeforeEach
	void setUp()
	throws IOException {
		serverThread = new Server(Config.Port);
		serverThread.start();

		clientSocket = new Socket("localhost", Config.Port);
		client_push = new ObjectOutputStream(clientSocket.getOutputStream());
		client_pull = new ObjectInputStream(clientSocket.getInputStream());
		testSocket = new Socket("localhost", Config.Port);
		test_push = new ObjectOutputStream(testSocket.getOutputStream());
		test_pull = new ObjectInputStream(testSocket.getInputStream());
	}

	@Order(1)
	@Test
	void Test_sending_messages()
	throws IOException, ClassNotFoundException {

		/*
		 * Register Test
		 */
		test_push.writeObject(new Msg.Register(TestUserName));
		var obj = test_pull.readObject();
		Assertions.assertInstanceOf(String.class, obj);
		var msg = (String) obj;
		Assertions.assertEquals(Msg.Register.Cmd + " # " + Msg.Success, msg);

		/*
		 * Register User
		 */
		client_push.writeObject(new Msg.Register(ClientUserName));
		obj = client_pull.readObject();
		Assertions.assertInstanceOf(String.class, obj);
		msg = (String) obj;
		Assertions.assertEquals(Msg.Register.Cmd + " # " + Msg.Success, msg);

		/*
		 * Send message from User to Test
		 */
		client_push.writeObject(new Msg.SendTo(TestUserName, "Hello Test"));
		obj = client_pull.readObject();
		Assertions.assertInstanceOf(String.class, obj);
		msg = (String) obj;
		Assertions.assertEquals(Msg.SendTo.Cmd + " # " + Msg.Success, msg);
		// Test should receive message
		obj = test_pull.readObject();
		Assertions.assertInstanceOf(Msg.SendTo.class, obj);
		var msgObj = (Msg.SendTo) obj;
		Assertions.assertEquals(new Msg.SendTo(ClientUserName, "Hello Test"), msgObj);

		/*
		 * Send broadcast from Test
		 */
		test_push.writeObject(new Msg.All("Hello Every-nyan"));
		obj = test_pull.readObject();
		Assertions.assertInstanceOf(String.class, obj);
		msg = (String) obj;
		Assertions.assertEquals(Msg.Success, msg);
		// User should receive message
		obj = client_pull.readObject();
		Assertions.assertInstanceOf(Msg.SendTo.class, obj);
		msgObj = (Msg.SendTo) obj;
		Assertions.assertEquals(new Msg.SendTo(TestUserName, "Hello Every-nyan"), msgObj);
		// Test should receive nothing
		Assertions.assertEquals(0, test_pull.available());
	}
}
