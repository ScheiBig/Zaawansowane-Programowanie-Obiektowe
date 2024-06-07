package lab_3.reflection;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.stream.Collectors;

public interface SimpleToString {

	default String simpleToString() {
		var cls = this.getClass();
		var str = Arrays.stream(cls.getDeclaredMethods())
				.filter(m -> m.getName()
						.matches("^(get|is)[a-zA-Z0-9_]*"))
				.map(m -> {
					var name = m.getName()
							.replaceFirst("^(get|is)", "");
					name = Character.toLowerCase(name.charAt(0)) + name.substring(1);

					Object value;
					try {
						value = m.invoke(this, null);
					} catch (IllegalAccessException | InvocationTargetException e) {
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
