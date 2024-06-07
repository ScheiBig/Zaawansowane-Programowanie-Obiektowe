package lab_3.student;


@DefaultStudent(name = "John",
		surname = "Doe",
		indexNumber = "123456",
		grades = {4f, 4.5f, 5f, 4f},
		degree = "Bachelor",
		fullTime = true)
public class StudentSimple
{
	private String name;

	private String surname;

	private String indexNumber;

	private float[] grades;

	private String degree;

	private boolean fullTime;

	public StudentSimple() {
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

	public StudentSimple(
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getIndexNumber() {
		return indexNumber;
	}

	public void setIndexNumber(String indexNumber) {
		this.indexNumber = indexNumber;
	}

	public float[] getGrades() {
		return grades;
	}

	public void setGrades(float[] grades) {
		this.grades = grades;
	}

	public String getDegree() {
		return degree;
	}

	public void setDegree(String degree) {
		this.degree = degree;
	}

	public boolean isFullTime() {
		return fullTime;
	}

	public void setFullTime(boolean fullTime) {
		this.fullTime = fullTime;
	}

}
