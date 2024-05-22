package lab_2;

import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

class MistraGriesSummariesTest {

	FileWordStreamer fws;
	MistraGriesSummaries<String> msSummary;

	@Test
	void Test_summary_is_working() {

		Assertions.assertDoesNotThrow(() -> {
			fws = new FileWordStreamer(Main.english_200MB_txt);
		});

		Assertions.assertDoesNotThrow(() -> {
			msSummary = new MistraGriesSummaries<>(
					(ResourceSupplier<Stream<String>>) fws::stream,
					100
			);
		});

		var summaries = msSummary.calculated().summaries();

		var actualSummaries = summaries.subList(0, 6)
				.stream()
				.map(ss -> ss.key() + ": " + ss.occurrenceCount())
				.toList();

		var expectedSummaries = List.of(
				"the: 2427360",
				"and: 1323210",
				"that: 457507",
				"was: 405046",
				"his: 364337",
				"with: 313077"
		);

		Assertions.assertLinesMatch(expectedSummaries, actualSummaries);
	}
}