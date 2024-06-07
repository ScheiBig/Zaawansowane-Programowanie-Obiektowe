package lab_3.reflection;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public interface ParameterizedEquals {

	private List<Field> fields() {
		var cls = this.getClass();
		return Arrays.stream(cls.getDeclaredFields())
				.filter(f -> !f.isAnnotationPresent(IgnoreEquals.class))
				.toList();
	}

	default boolean parameterizedEquals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}

		ParameterizedEquals peq = (ParameterizedEquals) obj;
		var result = true;
		for (var field : fields()) {
			field.setAccessible(true);
			if (field.getClass()
					.isArray()) {
				try {
					result = result &&
							switch (field.getClass()
									.getComponentType()
									.getName()) {
								case "byte" -> Arrays.equals((byte[]) field.get(this),
										(byte[]) field.get(peq)
								);
								case "short" -> Arrays.equals((short[]) field.get(this),
										(short[]) field.get(peq)
								);
								case "int" -> Arrays.equals((int[]) field.get(this),
										(int[]) field.get(peq)
								);
								case "long" -> Arrays.equals((long[]) field.get(this),
										(long[]) field.get(peq)
								);
								case "float" -> Arrays.equals((float[]) field.get(this),
										(float[]) field.get(peq)
								);
								case "double" -> Arrays.equals((double[]) field.get(this),
										(double[]) field.get(peq)
								);
								case "boolean" -> Arrays.equals((boolean[]) field.get(this),
										(boolean[]) field.get(peq)
								);
								case "char" -> Arrays.equals((char[]) field.get(this),
										(char[]) field.get(peq)
								);
								default -> Arrays.equals((Object[]) field.get(this),
										(Object[]) field.get(peq)
								);
							};
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			} else {
				try {
					result = result && field.get(this).equals(field.get(peq));
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}
		}
		return result;
	}

	default int parameterizedHashCode() {
		return Objects.hash(fields().stream()
				.map(f -> {
					f.setAccessible(true);
					try {
						return f.get(this);
					} catch (IllegalAccessException e) {
						throw new RuntimeException(e);
					}
				})
				.toArray());
	}
}
