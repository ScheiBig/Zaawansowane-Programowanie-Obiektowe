package lab_4.io;

import lab_4.concurrent.Semaphore;
import lab_4.concurrent.locks.Monitor;
import lab_4.concurrent.locks.TwoWayMonitor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;

public class ThreadTests {

	@Test
	void Test_Collaboration_is_working() {
		WriterThread.noGui = true;
		ReaderThread.noGui = true;

		try {
			var wg = new Semaphore(3, true);
			var mon = new TwoWayMonitor();
			var bo = new Semaphore(1, true);
			var buffer = new Character[1];

			var strings = new String[]{
					"Hello darkness my old friend\n",
					"Just say it so, hey hi hello\n",
					"What is love, baby don't hurt me\n"
			};

			var out = new StringWriter();
			var rth = new ArrayList<Thread>();

			for (var s : strings) {
				rth.add(new ReaderThread(
						new BufferedReader(new StringReader(s)),
						"",
						buffer,
						mon,
						wg,
						bo,
						BufferingMode.ByLine
				));
			}

			rth.forEach(Thread::start);

			var th = new WriterThread(new BufferedWriter(out), "", buffer, mon, wg);
			th.start();

			th.join();
			for (var thread : rth) {
				thread.join();
			}

			var str = out.toString();

			Assertions.assertEquals(String.join("", strings)
					.length(), str.length());

		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
