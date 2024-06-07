package lab_3.reflection;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Collectors;

public interface ParameterizedToString {

	default String parametrizedToString() {
		var cls = this.getClass();
		var str = Arrays.stream(cls.getDeclaredFields())
				.filter(f -> f.isAnnotationPresent(ToString.class))
				.sorted(Comparator.comparing(f -> f.getAnnotation(ToString.class)
						.order()))
				.map(f -> {
					var name = f.getName();
					var alias = f.getAnnotation(ToString.class)
							.name();
					if (!alias.isEmpty()) {
						name = alias;
					}

					Object value;
					f.setAccessible(true);
					try {
						value = f.get(this);
					} catch (IllegalAccessException e) {
						throw new RuntimeException(e);
					}
					String stringValue;
					if (value.getClass()
							.isArray()) {
						stringValue = switch (value.getClass()
								.getComponentType()
								.getName()) {
							case "byte" -> Arrays.toString((byte[]) value);
							case "short" -> Arrays.toString((short[]) value);
							case "int" -> Arrays.toString((int[]) value);
							case "long" -> Arrays.toString((long[]) value);
							case "float" -> Arrays.toString((float[]) value);
							case "double" -> Arrays.toString((double[]) value);
							case "boolean" -> Arrays.toString((boolean[]) value);
							case "char" -> Arrays.toString((char[]) value);
							default -> Arrays.toString((Object[]) value);
						};
					} else {
						stringValue = value.toString();
					}
					return name + " = " + stringValue;
				})
				.collect(Collectors.joining(", "));
		return cls.getName() + "{ " + str + " }";
	}
}
