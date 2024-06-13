package lab_4;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Config {
	public static final List<URL> Files;
	public static final URL Result;

	static {
		try {
			Files = new ArrayList<>();
			Arrays.stream(new File("src/main/resources/in").listFiles())
					.map(File::toURI)
					.map(uri -> {
						try {
							return uri.toURL();
						} catch (MalformedURLException e) {
							throw new RuntimeException(e);
						}
					})
					.forEach(Config.Files::add);
			Result = Path.of("src/main/resources/wynik.txt")
					.toUri()
					.toURL();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static final long AssumeThreadDead_ms = 25;
	public static final long Bounce_ms = 10;
}
