package lab_3.student;

import lab_3.reflection.ParameterizedEquals;
import lab_3.reflection.ParameterizedToString;

public class GeneratedToStringEquals
		implements ParameterizedToString,
		ParameterizedEquals
{

	@Override
	public String toString() {
		return parametrizedToString();
	}

	@Override
	public boolean equals(Object obj) {
		return parameterizedEquals(obj);
	}

	@Override
	public int hashCode() {
		return parameterizedHashCode();
	}
}
