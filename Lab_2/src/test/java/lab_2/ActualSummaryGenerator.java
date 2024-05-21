package lab_2;

import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ActualSummaryGenerator {

	@Test
	void generate()
	throws URISyntaxException, IOException {
		var stream = new FileWordStreamer(Main.english_200MB_txt).stream();

		var summaries = stream.collect(Collectors.groupingBy(
				Function.identity(),
				Collectors.counting()
		)).entrySet()
				.stream()
				.map(e -> new MistraGriesSummaries.Summary<>(
					e.getKey(), e.getValue() / 29_250_532.0, e.getValue()
				))
				.sorted(Comparator.comparingDouble(MistraGriesSummaries.Summary::occurrenceFrequency))
				.map(s -> "%s, %5.3f, %d".formatted(s.key(), s.occurrenceFrequency(), s.occurrenceCount()))
				.toList();

		Files.write(Path.of("src/test/resources/lab_2/summary.csv"), summaries.reversed(),
				StandardCharsets.UTF_8);

	}
}
