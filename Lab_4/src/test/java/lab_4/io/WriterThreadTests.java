package lab_4.io;

import lab_4.concurrent.Semaphore;
import lab_4.concurrent.locks.TwoWayMonitor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;

class WriterThreadTests {

	Semaphore sem;
	TwoWayMonitor monitor;
	final Character[] buffer = new Character[1];
	BufferedWriter output_w;
	String input;
	String out;
	Thread writerThread;

	@Test
	void Test_WriterThread_writing_to_file()
	throws InterruptedException {
		WriterThread.noGui = true;

		try {
			sem = new Semaphore(3);
			monitor = new TwoWayMonitor();
			input = "hello world";

			Assertions.assertDoesNotThrow(() -> {
				output_w = Files.newBufferedWriter(Path.of(Resources.output.toURI()));
			});

			Assertions.assertDoesNotThrow(() -> sem.acquire());

			Assertions.assertDoesNotThrow(() -> {
				writerThread = new WriterThread(output_w, null, buffer, monitor, sem);
				writerThread.start();
			});


			try {
				for (char c : input.toCharArray()) {
					monitor.lock();
					try {
						while (this.buffer[0] != null) {
							monitor.read.await();
						}
						this.buffer[0] = c;
//						System.out.println("-> " + this.buffer[0]);
						this.monitor.write.signal();
					} finally {
						monitor.unlock();
					}
				}
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}

			sem.release();
			writerThread.join();


			Assertions.assertDoesNotThrow(() -> {
				out = Files.readString(Path.of(Resources.output.toURI()));
			});

			Assertions.assertEquals(
					input,
					out
			);
		} finally {
		}
	}
}