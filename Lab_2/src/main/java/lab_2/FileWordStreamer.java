package lab_2;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Otacza plik przekazany w ścieżce w {@link Stream strumień}, który zwraca słowa większe niż 3
 * litery (słowa przekonwertowane do jedynie małych liter).
 * <p>
 * Jako znaki rozdzielające słowa, traktowane jest wszystko oprócz liter.
 */
public class FileWordStreamer {

	private final @NotNull Path filePath;

	private FileWordStreamer raw;

	/**
	 * Wersja klasy, która pomija filtrowanie.
	 */
	public @NotNull FileWordStreamer raw() {
		if (raw == null) {
			this.raw = new Raw(this.filePath.toUri());
		}
		return this.raw;
	}

	public FileWordStreamer(@NotNull URI fileLocation) {
		this.filePath = Paths.get(fileLocation);
	}
	public FileWordStreamer(@NotNull URL fileLocation)
	throws URISyntaxException {
		this.filePath = Paths.get(fileLocation.toURI());
	}

	/**
	 * Tworzy nową instancję strumienia.
	 */
	public @Nls @NotNull Stream<String> stream()
	throws IOException {
		return Files.lines(this.filePath)
				.flatMap(line -> Arrays.stream(line.split("[^a-zA-Z]+")))
				.filter(word -> word.length() >= 3)
				.map(String::toLowerCase);
	}


	private class Raw extends FileWordStreamer {

		public Raw(@NotNull URI fileLocation) {
			super(fileLocation);
		}

		@Override
		public @Nls @NotNull Stream<String> stream()
		throws IOException {
			return Files.lines(FileWordStreamer.this.filePath)
					.flatMap(line -> Arrays.stream(line.split(" ")))
					.map(String::toLowerCase);
		}
	}
}

