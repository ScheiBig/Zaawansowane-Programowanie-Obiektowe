package lab_2;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.stream.Stream;

public class Main {

	/**
	 * {@link URL} do lokalizacji przetwarzanego pliku.
	 */
	public static URL english_200MB_txt = Thread.currentThread()
			.getContextClassLoader()
			.getResource("lab_2/english.200MB.txt");

	public static void main(String[] args)
	throws URISyntaxException, IOException {

		System.out.println("Setting up...");
		var start = System.currentTimeMillis();
		var fws = new FileWordStreamer(Main.english_200MB_txt);

		var msSummary = new MistraGriesSummaries<>(
				(ResourceSupplier<Stream<String>>) fws::stream,
				100
		);

		System.out.println("Calculating summaries...");
		var summaries = msSummary.calculated().summaries();

		System.out.println();
		summaries.subList(0, 6).forEach(ss -> {
			System.out.println(ss.key() + ": " + ss.occurrenceCount());
		});

		var time = System.currentTimeMillis() - start;
		System.out.println();
		System.out.println("Took " + time / 1000 + "s " + time % 1000 + "ms");
	}
}
