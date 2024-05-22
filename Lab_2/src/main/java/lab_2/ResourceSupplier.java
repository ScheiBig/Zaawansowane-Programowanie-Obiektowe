package lab_2;

import java.io.IOException;

@FunctionalInterface
public interface ResourceSupplier<T> {

	T get()
	throws IOException;
}
