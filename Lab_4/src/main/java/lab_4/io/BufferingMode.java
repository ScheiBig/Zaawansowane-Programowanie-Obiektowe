package lab_4.io;

/**
 * Specifies mode of buffering.
 * <p>
 * Buffer size doesn't change, but how long particular thread locks
 * said buffer does.
 */
public enum BufferingMode {
	ByChar("Po jednym znaku"),
	ByWord("Po jednym słowie"),
	ByLine("Po jednej linii");

	private final String str;

	BufferingMode(String str) {this.str = str;}

	@Override
	public String toString() {
		return this.str;
	}
}
