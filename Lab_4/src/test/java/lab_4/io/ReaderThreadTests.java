package lab_4.io;

import lab_4.Config;
import lab_4.concurrent.Semaphore;
import lab_4.concurrent.locks.TwoWayMonitor;
import lab_4.result.Res;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

class ReaderThreadTests {

	Semaphore sem;
	TwoWayMonitor mon;
	final Character[] buffer = new Character[1];
	BufferedReader test1_txt;
	BufferedReader test2_txt;
	BufferedReader test3_txt;
	String test1_output;
	String test2_output;
	String test3_output;
	LinkedList<Character> output;

	@Test
	void Test_ReaderThreads_ballet() {
		ReaderThread.noGui = true;

		try {
			sem = new Semaphore(3);
			mon = new TwoWayMonitor();

			// Assign readers
			Assertions.assertDoesNotThrow(() -> {
				test1_txt =
						new BufferedReader(new InputStreamReader(Resources.test1.openStream()));
			});
			Assertions.assertDoesNotThrow(() -> {
				test2_txt =
						new BufferedReader(new InputStreamReader(Resources.test2.openStream()));
			});
			Assertions.assertDoesNotThrow(() -> {
				test3_txt =
						new BufferedReader(new InputStreamReader(Resources.test3.openStream()));
			});

			// Pull file contents for comparison
			Assertions.assertDoesNotThrow(() -> {
				test1_output = Files.readString(Path.of(Resources.test1.toURI()));
			});
			Assertions.assertDoesNotThrow(() -> {
				test2_output = Files.readString(Path.of(Resources.test2.toURI()));
			});
			Assertions.assertDoesNotThrow(() -> {
				test3_output = Files.readString(Path.of(Resources.test3.toURI()));
			});

			output = new LinkedList<>();

			// Let ReaderThreads into dancefloor
			Assertions.assertDoesNotThrow(() -> {
				new ReaderThread(test1_txt, null, buffer, mon, sem).start();
			});
			Assertions.assertDoesNotThrow(() -> {
				new ReaderThread(test2_txt, null, buffer, mon, sem).start();
			});
			Assertions.assertDoesNotThrow(() -> {
				new ReaderThread(test3_txt, null, buffer, mon, sem).start();
			});

			// Join the dance, process inputs
			try {
				outer: while(true) {
					mon.lock();
					try {
						while (this.buffer[0] == null) {
							if (!mon.write.await(
									Config.AssumeThreadDead_ms,
									TimeUnit.MILLISECONDS
							) && sem.tryAcquire(
									3,
									Config.AssumeThreadDead_ms,
									TimeUnit.MILLISECONDS
							)) {
								break outer;
							}
						}
//						System.out.println("<- " + this.buffer[0]);
						output.addLast(this.buffer[0]);
						this.buffer[0] = null;
						mon.read.signal();
					} finally {
						mon.unlock();
					}
				}
			} catch (InterruptedException e){
				throw new RuntimeException(e);
			}

			// After-flight checks
			var str_output = output.stream()
					.map(Object::toString)
					.collect(Collectors.joining());
			var str_chars = str_output.replaceAll("\\s", "");
			var str_lineCnt = str_output.split("\n").length;

			var test1_chars = test1_output.replaceAll("\\s", "");
			var test2_chars = test2_output.replaceAll("\\s", "");
			var test3_chars = test3_output.replaceAll("\\s", "");
			var test1_lineCnt = test1_output.split("\n").length;
			var test2_lineCnt = test2_output.split("\n").length;
			var test3_lineCnt = test3_output.split("\n").length;

			Assertions.assertEquals(test1_lineCnt + test2_lineCnt + test3_lineCnt, str_lineCnt);

			Assertions.assertEquals(
					test1_chars.length() + test2_chars.length() + test3_chars.length(),
					str_chars.length()
			);

			Assertions.assertEquals(test1_chars, str_chars.replaceAll("[a-zA-Z]", ""));

			Assertions.assertEquals(test2_chars, str_chars.replaceAll("[0-9A-Z]", ""));

			Assertions.assertEquals(test3_chars, str_chars.replaceAll("[0-9a-z]", ""));

		} finally {
			Res.from(() -> test1_txt.close());
			Res.from(() -> test2_txt.close());
			Res.from(() -> test3_txt.close());
		}
	}
}
