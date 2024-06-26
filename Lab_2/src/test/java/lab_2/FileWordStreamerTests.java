package lab_2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

class FileWordStreamerTests {

	static FileWordStreamer fws;
	Stream<String> stream; // <- Wyrzucone do zmiennej klasy, żeby Lambdy nie krzyczały

	@BeforeAll
	static void setupFileWordStreamer() {
		Assertions.assertDoesNotThrow(() -> {
			fws = new FileWordStreamer(Main.english_200MB_txt.toURI());
		});
	}

	@Test
	void Test_file_word_count_pre_filtering() {

		Assertions.assertDoesNotThrow(() -> {
			stream = fws.raw().stream();
		});

		Assertions.assertEquals(
				39_177_225,
				stream.count()
		);

		stream.close();
	}

	@Test
	void Test_file_word_count_post_filtering() {
		Assertions.assertDoesNotThrow(() -> {
			stream = fws.stream();
		});

		Assertions.assertEquals(
				29_250_532,
				stream.count()
		);

		stream.close();
	}
}