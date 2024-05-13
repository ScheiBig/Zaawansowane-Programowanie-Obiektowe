package lab_1.pesel;

/**
 * Klasa definiująca płeć. Posiada metody parsujące płeć w kodowaniu PESEL
 */
public enum Gender {
	FEMALE, MALE;

	public static Gender of(int peselDigit) {
		if (peselDigit % 2 == 0) {
			return FEMALE;
		} else {
			return MALE;
		}
	}

	public static Gender of(char peselCharacter) {
		return of(Character.digit(peselCharacter, 10));
	}
}
