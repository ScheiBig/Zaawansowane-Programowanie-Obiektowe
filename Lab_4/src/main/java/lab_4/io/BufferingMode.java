package lab_4.io;

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
