package lab_4.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Semaphore;

public class ReaderThread
		extends Thread
{

	private final BufferedReader file;
	private final Character[] buffer;
	private final Semaphore sem;


	public ReaderThread(
			BufferedReader file,
			Character[] buffer,
			Semaphore sem
	)
	throws InterruptedException {
		this.file = file;
		this.buffer = buffer;
		this.sem = sem;

		this.sem.acquire();
	}

	@Override
	public void run() {
		this.runByChar();
		this.sem.release();
	}

	private void runByChar() {
		try {
			for (int c = file.read(); c != -1; c = file.read()) {
				synchronized (this.buffer) {
					while (true) {
						if (!Objects.isNull(this.buffer[0])) {
							this.buffer.wait();
						} else {
							break;
						}
					}
					this.buffer[0] = (char) c;
					this.buffer.notify();
				}
			}
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
