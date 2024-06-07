package lab_3.util;

/**
 * Represents a supplier of results, that can error.
 *
 * <p>There is no requirement that a new or distinct result be returned each
 * time the supplier is invoked.
 *
 * <p>This is a <code>functional interface</code>
 * whose functional method is {@link #get()}.
 *
 * @param <T> the type of results supplied by this supplier
 *
 * @since 1.8
 */
@FunctionalInterface
public interface ThrowingSupplier<T> {

	/**
	 * Gets a result, or throws an error.
	 * @return A result
	 * @throws Throwable An error
	 */
	T get() throws Throwable;
}
