package lab_1.pesel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PeselVerifier {

	public static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * Zwraca <code>true</code>, jeśli PESEL jest poprawny
	 */
	public static boolean isPeselValid(String pesel) {
		try {
			parsePeselData(pesel);
			return true;
		} catch (PeselException e) {
			return false;
		}
	}

	/**
	 * Zwraca rekord z datą urodzenia i płcią zakodowanymi w identyfikatorze PESEL
	 * <p>
	 * Rzuca wyjątek z rodziny {@link PeselException} w przypadku błędów parsowania
	 */
	public static PeselData parsePeselData(String pesel)
	throws
			PeselException {

		validateLength(pesel);
		validateCharSet(pesel);
		var yearMonth = validateMonth(pesel);
		var year = Integer.parseInt(
				pesel.substring(
						0,
						2
				),
				10
		);

		year += switch (yearMonth.yearOffser) {
			case 8 -> 1800;
			case 0 -> 1900;
			case 2 -> 2000;
			case 4 -> 2100;
			case 6 -> 2200;
			default -> throw new IllegalStateException();
		};

		format.setLenient(false);

		Date date;
		var day = Integer.parseInt(
				pesel.substring(
						4,
						6
				),
				10
		);

		try {
			date = format.parse(year + "-" + yearMonth.month + "-" + day);
		} catch (ParseException e) {
			throw new PeselException.DayException(day);
		}

		Gender gender = Gender.of(pesel.charAt(9));

		int controlSum = calculateCotrolSum(pesel);
		int expectedSum = Character.digit(
				pesel.charAt(10),
				10
		);

		if (controlSum != expectedSum) {
			throw new PeselException.ControlSumException(
					expectedSum,
					controlSum
			);
		}

		return new PeselData(
				date,
				gender
		);
	}

	private static int calculateCotrolSum(String pesel) {
		int[] weights = new int[]{
				1, 3, 7, 9, 1, 3, 7, 9, 1, 3
		};

		int sum = 0;

		for (int i = 0; i < pesel.length() - 1; i++) {
			sum += weights[i] * Character.digit(
					pesel.charAt(i),
					10
			);
		}

		return 10 - sum % 10;
	}

	private static void validateLength(String pesel)
	throws
			PeselException.LengthException {
		try {
			if (pesel.length() != 11) {
				throw new PeselException.LengthException(pesel.length());
			}
		} catch (NullPointerException e) {
			throw new PeselException.NullException();
		}
	}

	private static void validateCharSet(String pesel)
	throws
			PeselException.IllegalCharactersException {
		int i = 0;
		for (char c : pesel.toCharArray()) {
			if (!Character.isDigit(c)) {
				throw new PeselException.IllegalCharactersException(
						c,
						i
				);
			}
			i++;
		}
	}

	private record YearMonth(int yearOffser, int month) {}

	private static YearMonth validateMonth(String pesel)
	throws
			PeselException.MonthException {
		String str = pesel.substring(
				2,
				4
		);
		int offset = Character.digit(
				str.charAt(0),
				10
		);
		int month = Character.digit(
				str.charAt(1),
				10
		);

		if (offset % 2 != 0) {
			month += 10;
			offset -= 1;
		}

		if (month == 0 || month > 12) {
			throw new PeselException.MonthException(month);
		}

		return new YearMonth(
				offset,
				month
		);
	}
}
