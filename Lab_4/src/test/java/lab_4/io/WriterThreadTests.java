package lab_4.io;

import lab_4.concurrent.locks.Monitor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.Semaphore;

class WriterThreadTests {

	Semaphore sem;
	Monitor monitor;
	final Character[] buffer = new Character[1];
	BufferedWriter output_w;
	String input;
	String out;
	Thread writerThread;

	@Test
	void Test_WriterThread_writing_to_file()
	throws InterruptedException {
		try {
			sem = new Semaphore(3);
			monitor = new Monitor();
			input = "hello world";

			Assertions.assertDoesNotThrow(() -> {
				output_w = Files.newBufferedWriter(Path.of(Resources.output.toURI()));
			});

			Assertions.assertDoesNotThrow(() -> sem.acquire());

			Assertions.assertDoesNotThrow(() -> {
				writerThread = new WriterThread(output_w, buffer, monitor, sem);
				writerThread.start();
			});

//			try {
//				for (char c : input.toCharArray()) {
//					synchronized (this.buffer) {
//						while (true) {
//							if (!Objects.isNull(this.buffer[0])) {
//								this.buffer.wait();
//							} else {
//								break;
//							}
//						}
//						this.buffer[0] = c;
//						this.buffer.notify();
//						System.err.println("Character emission: " + c);
//					}
//				}
//				sem.release();
//				synchronized (this.buffer) {
//					this.buffer.notify();
//				}
//			} catch (InterruptedException e) {
//				throw new RuntimeException(e);
//			}


			try {
				for (char c : input.toCharArray()) {
					monitor.lock();
					try {
						while (this.buffer[0] != null) {
							monitor.await();
						}
						this.buffer[0] = c;
						System.out.println("-> " + this.buffer[0]);
						this.monitor.signal();
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