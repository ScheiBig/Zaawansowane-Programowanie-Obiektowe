package lab_1.pesel;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Klasa definiuje pojedyncze testy konkretnych przykładów błędów,
 * w większości mapujących bezpośrednio na konkretne wyjątki,
 * dziedziczące z klasy {@link PeselException}
 * <p>
 * Wartości testowe są ręcznie dobrane
 */
class PeselVerifierTests_SingleInstance {

	@Test
	void Test_pesel_is_null() {

		String pesel = null;

		Assertions.assertFalse(PeselVerifier.isPeselValid(pesel));

		Assertions.assertThrows(
				PeselException.NullException.class,
				() -> PeselVerifier.parsePeselData(pesel)
		);
	}

	@Test
	void Test_pesel_is_blank() {

		String pesel = "";

		Assertions.assertFalse(PeselVerifier.isPeselValid(pesel));

		Assertions.assertThrows(
				PeselException.LengthException.class,
				() -> PeselVerifier.parsePeselData(pesel)
		);
	}

	@Test
	void Test_pesel_is_too_short() {

		String pesel = "12345";

		Assertions.assertFalse(PeselVerifier.isPeselValid(pesel));

		Assertions.assertThrows(
				PeselException.LengthException.class,
				() -> PeselVerifier.parsePeselData(pesel)
		);
	}

	@Test
	void Test_pesel_is_too_long() {

		String pesel = "831025453581";

		Assertions.assertFalse(PeselVerifier.isPeselValid(pesel));

		Assertions.assertThrows(
				PeselException.LengthException.class,
				() -> PeselVerifier.parsePeselData(pesel)
		);
	}

	@Test
	void Test_pesel_bad_charset() {

		String pesel = "831025453xd";

		Assertions.assertFalse(PeselVerifier.isPeselValid(pesel));

		Assertions.assertThrows(
				PeselException.IllegalCharactersException.class,
				() -> PeselVerifier.parsePeselData(pesel)
		);
	}

	@Test
	void Test_pesel_month_is_zero() {

		String pesel = "99400221138";

		Assertions.assertFalse(PeselVerifier.isPeselValid(pesel));

		Assertions.assertThrows(
				PeselException.MonthException.class,
				() -> PeselVerifier.parsePeselData(pesel)
		);
	}

	@Test
	void Test_pesel_month_is_too_big() {

		String pesel = "99130174887";

		Assertions.assertFalse(PeselVerifier.isPeselValid(pesel));

		Assertions.assertThrows(
				PeselException.MonthException.class,
				() -> PeselVerifier.parsePeselData(pesel)
		);
	}

	@Test
	void Test_pesel_day_is_zero() {

		String pesel = "80040092277";

		Assertions.assertFalse(PeselVerifier.isPeselValid(pesel));

		Assertions.assertThrows(
				PeselException.DayException.class,
				() -> PeselVerifier.parsePeselData(pesel)
		);
	}

	@Test
	void Test_pesel_day_is_too_big() {

		String pesel = "63033351944";

		Assertions.assertFalse(PeselVerifier.isPeselValid(pesel));

		Assertions.assertThrows(
				PeselException.DayException.class,
				() -> PeselVerifier.parsePeselData(pesel)
		);
	}

	@Test
	void Test_pesel_incorrect_control_sum() {

		String pesel = "99030174883";

		Assertions.assertFalse(PeselVerifier.isPeselValid(pesel));

		Assertions.assertThrows(
				PeselException.ControlSumException.class,
				() -> PeselVerifier.parsePeselData(pesel)
		);
	}

	@Test
	void Test_correct_pesel_numbers() {
		var format = PeselVerifier.format;
		AtomicReference<PeselData> pd = new AtomicReference<>();
		AtomicReference<String> pesel = new AtomicReference<>();

		pesel.set("61061581563");
		Assertions.assertTrue(PeselVerifier.isPeselValid(pesel.get()));

		Assertions.assertDoesNotThrow(() -> {
			pd.set(PeselVerifier.parsePeselData(pesel.get()));
		});
		Assertions.assertEquals(
				"1961-06-15",
				format.format(pd.get().birthDate())
		);
		Assertions.assertEquals(
				Gender.FEMALE,
				pd.get().gender()
		);


		pesel.set("00473013194");
		Assertions.assertTrue(PeselVerifier.isPeselValid(pesel.get()));

		Assertions.assertDoesNotThrow(() -> {
			pd.set(PeselVerifier.parsePeselData(pesel.get()));
		});
		Assertions.assertEquals(
				"2100-07-30",
				format.format(pd.get().birthDate())
		);
		Assertions.assertEquals(
				Gender.MALE,
				pd.get().gender()
		);

		pesel.set("69921212349");
		Assertions.assertTrue(PeselVerifier.isPeselValid(pesel.get()));

		Assertions.assertDoesNotThrow(() -> {
			pd.set(PeselVerifier.parsePeselData(pesel.get()));
		});
		Assertions.assertEquals(
				"1869-12-12",
				format.format(pd.get().birthDate())
		);
		Assertions.assertEquals(
				Gender.FEMALE,
				pd.get().gender()
		);


		pesel.set("24222956655");
		Assertions.assertTrue(PeselVerifier.isPeselValid(pesel.get()));

		Assertions.assertDoesNotThrow(() -> {
			pd.set(PeselVerifier.parsePeselData(pesel.get()));
		});
		Assertions.assertEquals(
				"2024-02-29",
				format.format(pd.get().birthDate())
		);
		Assertions.assertEquals(
				Gender.MALE,
				pd.get().gender()
		);


		pesel.set("42692137695");
		Assertions.assertTrue(PeselVerifier.isPeselValid(pesel.get()));

		Assertions.assertDoesNotThrow(() -> {
			pd.set(PeselVerifier.parsePeselData(pesel.get()));
		});
		Assertions.assertEquals(
				"2242-09-21",
				format.format(pd.get().birthDate())
		);
		Assertions.assertEquals(
				Gender.MALE,
				pd.get().gender()
		);
	}
}