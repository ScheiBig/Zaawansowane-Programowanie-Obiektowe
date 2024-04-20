package lab_1.pesel;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.util.concurrent.atomic.AtomicReference;


/**
 * Klasa definiuje testy parametryzowane konkretnych przykładów błędów,
 * w większości mapujących bezpośrednio na konkretne wyjątki,
 * dziedziczące z klasy {@link PeselException}
 * <p>
 * Wartości testowe zostały wygenerowane z pomocą skryptu
 *     <code>src/test/resources/lab_1/pesel/generate_test_data.js</code>
 */
public class PeselVerifierTests_Batch {

	@ParameterizedTest
	@CsvFileSource(resources = "length_check.csv")
	void Test_pesel_length_batch_tests(
			String input,
			boolean isValid
	) {
		Assertions.assertEquals(
				isValid,
				PeselVerifier.isPeselValid(input)
		);
		if (isValid) {
			Assertions.assertDoesNotThrow(() -> {
				PeselVerifier.parsePeselData(input);
			});
		} else {
			Assertions.assertThrows(
					PeselException.LengthException.class,
					() -> {
						PeselVerifier.parsePeselData(input);
					}
			);
		}
	}

	@ParameterizedTest
	@CsvFileSource(resources = "charset_check.csv")
	void Test_pesel_charset_batch_tests(
			String input,
			boolean isValid
	) {
		Assertions.assertEquals(
				isValid,
				PeselVerifier.isPeselValid(input)
		);
		if (isValid) {
			Assertions.assertDoesNotThrow(() -> {
				PeselVerifier.parsePeselData(input);
			});
		} else {
			Assertions.assertThrows(
					PeselException.IllegalCharactersException.class,
					() -> {
						PeselVerifier.parsePeselData(input);
					}
			);
		}
	}

	@ParameterizedTest
	@CsvFileSource(resources = "month_check.csv")
	void Test_pesel_month_batch_tests(
			String input,
			boolean isValid
	) {
		Assertions.assertEquals(
				isValid,
				PeselVerifier.isPeselValid(input)
		);
		if (isValid) {
			Assertions.assertDoesNotThrow(() -> {
				PeselVerifier.parsePeselData(input);
			});
		} else {
			Assertions.assertThrows(
					PeselException.MonthException.class,
					() -> {
						PeselVerifier.parsePeselData(input);
					}
			);
		}
	}

	@ParameterizedTest
	@CsvFileSource(resources = "day_check.csv")
	void Test_pesel_day_batch_tests(
			String input,
			boolean isValid
	) {
		Assertions.assertEquals(
				isValid,
				PeselVerifier.isPeselValid(input)
		);
		if (isValid) {
			Assertions.assertDoesNotThrow(() -> {
				PeselVerifier.parsePeselData(input);
			});
		} else {
			Assertions.assertThrows(
					PeselException.DayException.class,
					() -> {
						PeselVerifier.parsePeselData(input);
					}
			);
		}
	}

	@ParameterizedTest
	@CsvFileSource(resources = "sum_check.csv")
	void Test_pesel_sum_batch_tests(
			String input,
			boolean isValid
	) {
		Assertions.assertEquals(
				isValid,
				PeselVerifier.isPeselValid(input)
		);
		if (isValid) {
			Assertions.assertDoesNotThrow(() -> {
				PeselVerifier.parsePeselData(input);
			});
		} else {
			Assertions.assertThrows(
					PeselException.ControlSumException.class,
					() -> {
						PeselVerifier.parsePeselData(input);
					}
			);
		}
	}

	@ParameterizedTest
	@CsvFileSource(resources = "parser_check.csv")
	void Test_pesel_parser_batch_tests(
			String input,
			String birthDate,
			Gender gender
	) {
		var format = PeselVerifier.format;

		Assertions.assertTrue(PeselVerifier.isPeselValid(input));

		AtomicReference<PeselData> pd = new AtomicReference<>();
		Assertions.assertDoesNotThrow(() -> {
			pd.set(PeselVerifier.parsePeselData(input));
		});

		Assertions.assertEquals(
				birthDate,
				format.format(pd.get()
						.birthDate())
		);
		Assertions.assertEquals(
				gender,
				pd.get()
						.gender()
		);
	}
}
