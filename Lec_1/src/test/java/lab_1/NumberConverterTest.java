package lab_1;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Map;
import java.util.logging.Logger;

class NumberConverterTest {

	private static final Logger LOG = Logger.getLogger(NumberConverterTest.class.getName());
	private NumberConverter nc;


	@BeforeEach
	void setUp() {
		LOG.info("Tests begin");
		nc = new NumberConverter();
	}

	@AfterEach
	void tearDown() {
		LOG.info("Tests are over");
	}

	@Test
	void romanToArabic() {
		Assertions.assertEquals(
				5,
				nc.romanToArabic("V")
		);
		Assertions.assertEquals(
				11,
				nc.romanToArabic("XI")
		);
		Assertions.assertEquals(
				1025,
				nc.romanToArabic("MXXV")
		);
	}

	@Test
	void arabicToRoman() {
		Assertions.assertEquals(
				"VII",
				nc.arabicToRoman(7)
		);
		Assertions.assertEquals(
				"MCMXCV",
				nc.arabicToRoman(1995)
		);
		Assertions.assertEquals(
				"MMX",
				nc.arabicToRoman(2010)
		);
	}

	@ParameterizedTest
	@CsvSource(value = {"'VII',7", "'MCMXCV',1995", "'MMX',2010"})
	void arabicToRoman(
			String expected,
			int input
	) {
		Assertions.assertEquals(
				expected,
				nc.arabicToRoman(input)
		);
	}
}