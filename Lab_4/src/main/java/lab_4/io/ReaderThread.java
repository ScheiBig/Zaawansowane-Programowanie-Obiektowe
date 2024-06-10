package lab_4.io;

import lab_4.concurrent.locks.Monitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Semaphore;

public class ReaderThread
		extends Thread
{

	private final BufferedReader file;
	private final Character[] buffer;
	private final Monitor monitor;
	private final Semaphore sem;


	public ReaderThread(
			BufferedReader file,
			Character[] buffer,
			Monitor monitor,
			Semaphore sem
	)
	throws InterruptedException {
		this.file = file;
		this.buffer = buffer;
		this.monitor = monitor;
		this.sem = sem;

		this.sem.acquire();
		this.setName("ReaderThread :: " + this.getName());
	}

	@Override
	public void run() {
		this.runByChar();
		this.sem.release();
	}

	private void runByChar() {
		try {
			for (int c = file.read(); c != -1; c = file.read()) {
				monitor.lock();
				try {
					while (this.buffer[0] != null) {
						monitor.await();
					}
					this.buffer[0] = (char) c;
//					System.out.println("-> " + this.buffer[0]);
					this.monitor.signal();
				} finally {
					monitor.unlock();
				}
			}
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private void runByLine() {

	}
}
