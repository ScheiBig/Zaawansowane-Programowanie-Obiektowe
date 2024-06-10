package lab_4.io;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;

public final class Resources {
	public static final URL test1 = Thread.currentThread()
			.getContextClassLoader()
			.getResource("test1.txt");
	public static final URL test2 = Thread.currentThread()
			.getContextClassLoader()
			.getResource("test2.txt");
	public static final URL test3 = Thread.currentThread()
			.getContextClassLoader()
			.getResource("test3.txt");

	public static final URL output;
	static {
		try {
			output = Path.of("src/test/resources/output.txt").toUri().toURL();
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
}
