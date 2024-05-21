package lab_2;

import java.net.URL;

public class Main {

	/**
	 * {@link URL} do lokalizacji przetwarzanego pliku.
	 */
	public static URL english_200MB_txt = Thread.currentThread()
			.getContextClassLoader()
			.getResource("lab_2/english.200MB.txt");

	public static void main(String[] args) {
	}
}
