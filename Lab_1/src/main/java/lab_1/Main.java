package lab_1;

import lab_1.pesel.PeselData;
import lab_1.pesel.PeselException;
import lab_1.pesel.PeselVerifier;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class Main {
	public static void main(String[] args) {
		if (args.length >= 1) {
			System.out.println("[[" + args[0] + "]]");
			mainArgs(args[0]);
		} else {
			mainStd();
		}
	}

	private static void mainArgs(String pesel) {
		try {
			PeselData pd = PeselVerifier.parsePeselData(pesel);

			System.out.println("Data urodzenia: " + PeselVerifier.format.format(pd.birthDate()));
			System.out.println("Płeć: " + pd.gender());
		} catch (PeselException e) {
			System.err.println(e.getMessage());
		}
	}

	private static void mainStd() {
		try {
			var in = new BufferedReader(new InputStreamReader(System.in));

			System.out.print("Podaj nr PESEL: ");
			String pesel = in.readLine();

			PeselData pd = PeselVerifier.parsePeselData(pesel);

			System.out.println("Data urodzenia: " + PeselVerifier.format.format(pd.birthDate()));
			System.out.println("Płeć: " + pd.gender());
		} catch (PeselException | IOException e) {
			System.err.println(e.getMessage());
		}
	}
}