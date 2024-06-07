package lab_3;

import lab_3.student.MainStudent;

import java.util.Arrays;
import java.util.Optional;
import java.util.Scanner;

public class Main {


	public static void main(String[] args) {
		try {
			if (args.length >= 1) {
				switch (args[0]) {
					case "calc":
						MainCalc.mainCalc(Arrays.copyOfRange(args, 1, args.length - 1));
						return;
					case "student":
						MainStudent.mainStudent(Arrays.copyOfRange(args, 1, args.length - 1));
						return;
					default:
						throw new IllegalArgumentException("Unknown program type");
				}
			} else {
				var sc = new Scanner(System.in);
				System.out.println("Select program type ['calc' / 'student']:");
				switch (sc.nextLine()) {
					case "calc":
						MainCalc.mainCalc(null);
						return;
					case "student":
						MainStudent.mainStudent(null);
						return;
					default:
						throw new IllegalArgumentException("Unknown program type");
				}
			}
		} catch (Throwable e) {
			System.err.println(e.getMessage());
			Optional.ofNullable(e.getCause())
					.ifPresent(er -> System.err.println(er.getMessage()));
		}
	}
}
