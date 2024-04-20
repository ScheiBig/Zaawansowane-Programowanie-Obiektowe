package lab_1;

public class Main {

	public static void main(String[] args) {
		var nc = new NumberConverter();

		System.out.println(nc.romanToArabic("I"));
		System.out.println(nc.arabicToRoman(1));
	}
}