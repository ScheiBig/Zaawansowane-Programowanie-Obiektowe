package lab_3.student;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DefaultStudent {
	String name() default "";
	String surname() default "";
	String indexNumber() default "";
	float[] grades() default {};
	String degree() default "";
	boolean fullTime() default false;
}
