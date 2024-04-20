package lab_1.pesel;

import java.util.Date;

/**
 * Rekord danych zakodowanych w identyfikatorze PESEL
 */
public record PeselData(Date birthDate, Gender gender) {}
