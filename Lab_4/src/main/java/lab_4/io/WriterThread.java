package lab_4.io;

import lab_4.Main;
import lab_4.concurrent.locks.Monitor;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class WriterThread
		extends Thread
{
	private final BufferedWriter file;
	private final Character[] buffer;
	private final Monitor mon;
	private final Semaphore sem;

	public WriterThread(
			BufferedWriter file,
			Character[] buffer,
			Monitor mon,
			Semaphore sem
	) {
		this.file = file;
		this.buffer = buffer;
		this.mon = mon;
		this.sem = sem;
		this.setName("WriterThread :: " + this.getName());
	}

	@Override
	public void run() {
		try {
			outer:
			while (true) {
				mon.lock();
				try {
					while (this.buffer[0] == null) {
						if (!mon.await(
								Main.ASSUME_THREAD_DEAD__MS,
								TimeUnit.MILLISECONDS
						) && sem.tryAcquire(
								3,
								Main.ASSUME_THREAD_DEAD__MS,
								TimeUnit.MILLISECONDS
						)) {
							break outer;
						}
					}
					System.out.println("<- " + this.buffer[0]);
					file.write(this.buffer[0]);
					this.buffer[0] = null;
					mon.signal();
				} finally {
					mon.unlock();
				}
			}
			file.flush();
		} catch (InterruptedException | IOException e) {
			throw new RuntimeException(e);
		}
	}
}
