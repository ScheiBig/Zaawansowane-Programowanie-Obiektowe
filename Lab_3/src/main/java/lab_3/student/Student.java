package lab_3.student;


import lab_3.reflection.IgnoreEquals;
import lab_3.reflection.ToString;

@DefaultStudent(name = "John",
		surname = "Doe",
		indexNumber = "123456",
		grades = {4f, 4.5f, 5f, 4f},
		degree = "Bachelor",
		fullTime = true)
public class Student
		extends GeneratedToStringEquals {

	public String name;

	@ToString(order = 10, name = "Nazwisko")
	public String surname;

	@IgnoreEquals
	@ToString(order = 1, name = "Numer indeksu")
	public String indexNumber;

	@IgnoreEquals
	@ToString(order = 1337, name = "Oceny")
	public float[] grades;

	@IgnoreEquals
	public String degree;

	@IgnoreEquals
	public boolean fullTime;

	public Student() {
		var cls = this.getClass();
		var def = cls.getAnnotation(DefaultStudent.class);
		if (def == null) {
			return;
		}
		this.name = def.name();
		this.surname = def.surname();
		this.indexNumber = def.indexNumber();
		this.grades = def.grades();
		this.degree = def.degree();
		this.fullTime = def.fullTime();
	}

	public Student(
			String name,
			String surname,
			String indexNumber,
			float[] grades,
			String degree,
			boolean fullTime
	) {
		this.name = name;
		this.surname = surname;
		this.indexNumber = indexNumber;
		this.grades = grades;
		this.degree = degree;
		this.fullTime = fullTime;
	}
}
