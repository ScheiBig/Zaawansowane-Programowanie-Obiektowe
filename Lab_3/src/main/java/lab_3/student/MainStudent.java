package lab_3.student;

public class MainStudent {
	public static void mainStudent(String[] args) {
		var sStud = new StudentSimple();

		System.out.println("Default student with inherited simple toString():");
		System.out.println(sStud);


		var stud = new Student();
		System.out.println("Default student with inherited parameterized toString():");
		System.out.println(stud);

		var otherStud = new Student(
				"John",
				"Doe",
				"789123",
				new float[] {3f, 3.5f, 4f},
				"Master",
				false
		);
		System.out.println("Other student with inherited parameterized toString():");
		System.out.println(otherStud);

		System.out.println("Test of equality between students:");
		System.out.println("Equals = " + stud.equals(otherStud));

		System.out.println("Test of hashCodes of students:");
		System.out.println("First  = " + Integer.toHexString(stud.hashCode()));
		System.out.println("Second = " + Integer.toHexString(otherStud.hashCode()));
	}
}
