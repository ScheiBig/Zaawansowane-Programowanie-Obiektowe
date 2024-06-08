package lab_4.result;

/**
 * Represents an operation that does not return a result, that can error.
 *
 * <p> This is a {@linkplain java.util.function functional interface}
 * whose functional method is {@link #run()}.
 *
 */
@FunctionalInterface
public interface ThrowingRunnable {

	/**
	 * Runs this operation.
	 * @throws Throwable An error
	 */
	void run() throws Throwable;
}
