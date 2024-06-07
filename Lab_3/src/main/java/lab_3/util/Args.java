package lab_3.util;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Scanner;

public class Args {

	public static Iterator<String> of(@NotNull String[] args) {
		return Arrays.asList(args)
				.iterator();
	}

	public static Iterator<String> stdin() {
		var scanner = new Scanner(System.in);

		return new Iterator<>() {
			@Override
			public boolean hasNext() {
				return true;
			}

			@Override
			public String next() {
				return scanner.next();
			}
		};
	}
}
