package lab_3.util;

import java.util.Optional;

/**
 * Wraps generic result of calculation, that either produces value, or error.
 * <p>
 * By contract, it is expected that:
 * <p><code>
 * (this.val() == null) != (this.err() == null)
 * </code>
 * so that, R{@link Result} exclusively wraps either value, or error. For Legal, empty values,
 * {@link Optional}s should be used.
 *
 * @param <V> Type of wrapped value
 * @param <E> Type of wrapped error
 */
public interface Result<V, E extends Throwable> {

	/**
	 * Returns value.
	 */
	V val();

	/**
	 * Returns error.
	 */
	E err();
}
