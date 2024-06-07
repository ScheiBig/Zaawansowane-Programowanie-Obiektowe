package lab_3.student;

import lab_3.reflection.SimpleToString;

public abstract class ObjectToString implements SimpleToString {

	@Override
	public String toString() {
		return simpleToString();
	}
}
