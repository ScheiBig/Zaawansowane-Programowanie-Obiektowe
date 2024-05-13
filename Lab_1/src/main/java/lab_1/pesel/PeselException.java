package lab_1.pesel;

/**
 * Klasa bazowa wyjątków rzucanych przez parser identyfikatorów PESEL
 * <p>
 * Zawarte wewnątrz jej podklasy zwracane są w konkretnych przypadkach błędów
 */
public abstract class PeselException
		extends IllegalArgumentException
{

	private PeselException(String s) {
		super(s);
	}

	public static class NullException
			extends PeselException
	{
		public NullException() {
			super("Pesel is not provided");
		}
	}

	public static class LengthException
			extends PeselException
	{
		public LengthException(int length) {
			super("Pesel length is not 11 characters: length is " + length);
		}
	}

	public static class IllegalCharactersException
			extends PeselException
	{
		public IllegalCharactersException(
				char character,
				int index
		) {
			super("Pesel contains characters that are not numbers: character is " + character +
					" at " + index);
		}
	}

	public static class MonthException
			extends PeselException
	{
		public MonthException(int month) {
			super("Pesel month part is out of range 1..12: month is " + month);
		}
	}

	public static class DayException
			extends PeselException
	{
		public DayException(int day) {
			super("Pesel day part is out of range for given month: day is " + day);
		}
	}

	public static class ControlSumException
			extends PeselException
	{
		public ControlSumException(
				int expected,
				int actual
		) {
			super("Pesel control sum is not valid: sum is " + actual + " should be " + expected);
		}
	}
}
