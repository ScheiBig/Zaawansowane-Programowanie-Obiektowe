package utilx.result;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Standard implementation of {@link Result} type.
 * @param <V> Type of value
 * @param <E> Type of error
 */
public class Res<V, E extends Throwable> implements Result<V, E> {

	private final V val;
	private final E err;

	public @Nullable V val() {
		return val;
	}

	public @Nullable E err() {
		return err;
	}

	private Res(
			@Nullable V value,
			@Nullable E error
	) {
		this.val = value;
		this.err = error;
	}

	/**
	 * Creates new instance of result.
	 * <p>
	 * <code>value</code> and <code>error</code> must be mutually exclusive in their mutability.
	 * @param value Value to wrap
	 * @param error Error to wrap
	 * @return New result
	 * @param <V> Type of value
	 * @param <E> Type of error
	 * @throws NullPointerException If both <code>value</code> and <code>error</code> are
	 * <code>null</code> or not-<code>null</code>
	 */
	@Contract("null, null -> fail; !null, !null -> fail")
	public static <V, E extends Throwable> Result<V, E> of(
			@Nullable V value,
			@Nullable E error
	)
	throws NullPointerException {
		if ((value == null) == (error == null)) {
			throw new NullPointerException("When creating instance of `Res` result, exclusively " +
					"either value or error should be not-null");
		}
		return new Res<>(value, error);
	}

	/**
	 * Creates successful result, that holds not-<code>null</code> value.
	 * @param value Value to be wrapped; must be not-null
	 * @return New result
	 * @param <V> Type of value
	 */
	public static <V> Result<V, Throwable> success(V value) {
		return new Res<>(value, null);
	}

	/**
	 * Creates successful result, that holds not-<code>null</code> error
	 * @param error Error to be wrapped; must be not-null
	 * @return New result
	 * @param <T> Type of Error
	 */
	public static <T extends Throwable> Result<Object, T> failure(T error) {
		return new Res<>(null, error);
	}

	/**
	 * Creates result, that wraps return values / errors produced by <code>call</code>.
	 * <p>
	 * Rules of {@link Res#of Res::of} apply to the <code>call</code>.
	 * @param call Code that returns / throws
	 * @return New result
	 * @param <V> Type of value
	 */
	public static <V> Result<V, Throwable> from(
			ThrowingSupplier<V> call
	) {
		try {
			return success(call.get());
		} catch (Throwable e) {
			return new Res<>(null, e);
		}
	}

	/**
	 * Creates result, that wraps errors produced by <code>call</code>.
	 * <p>
	 * Rules of {@link Res#of Res::of} apply to the <code>call</code>.
	 * @param call Code that throws
	 * @return New result
	 */
	public static Result<Void, Throwable> from(
			ThrowingRunnable call
	) {
		try {
			call.run();
			return new Res<>(null, null);
		} catch (Throwable e) {
			return new Res<>(null, e);
		}
	}

	/**
	 * Implementation of {@link Result} type, that can hold valid empty values as {@link Optional}s.
	 * @param <V> Type of value
	 * @param <E> Type of error
	 */
	public static class op<V, E extends Throwable>
			implements Result<Optional<V>, E>
	{
		private final Optional<V> val;
		private final E err;

		public @Nullable Optional<V> val() {
			return val;
		}

		public @Nullable E err() {
			return err;
		}

		private op(
				@Nullable V value,
				@Nullable E error
		) {
			this.val = Optional.ofNullable(value);
			this.err = error;
		}

		private op(
				@Nullable Optional<V> value,
				@Nullable E error
		) {
			this.val = value;
			this.err = error;
		}
		/**
		 * Creates new instance of result.
		 * <p>
		 * Both <code>value</code> and <code>error</code> can be <code>null</code>, as this class
		 * accepts empty values, however it is still illegal for both of them, to be
		 * not-<code>null</code>.
		 * mutability
		 * @param value Value (possibly empty) to wrap
		 * @param error Error to wrap
		 * @return New result
		 * @param <V> Type of value
		 * @param <E> Type of error
		 * @throws NullPointerException If both <code>value</code> and <code>error</code> are
		 * not-<code>null</code>
		 */
		@Contract("!null, !null -> fail")
		public static <V, E extends Throwable> Result<Optional<V>, E> of(
				@Nullable V value,
				@Nullable E error
		) {
			if ((value != null) && (error != null)) {
				throw new NullPointerException("When creating instance of `Res` result, exclusively " +
						"either value or error should be not-null");
			}
			return new op<>(value, error);
		}
		/**
		 * Creates successful result, that holds <code>null</code>able value.
		 * @param value Value to be wrapped; can be null
		 * @return New result
		 * @param <V> Type of value
		 */
		public static <V> Result<Optional<V>, Throwable> success(@Nullable V value) {
			return new op<>(value, null);
		}

		/**
		 * Creates successful result, that holds not-<code>null</code> error
		 * @param error Error to be wrapped; must be not-null
		 * @return New result
		 * @param <T> Type of Error
		 */
		public static <T extends Throwable> Result<Optional<Void>, T> failure(T error) {
			return new op<>(null, error);
		}

		/**
		 * Creates result, that wraps return values / errors produced by <code>call</code>.
		 * <p>
		 * Rules of {@link op#of Res::op::of} apply to the <code>call</code>.
		 * @param call Code that returns / throws
		 * @return New result
		 * @param <V> Type of value
		 */
		public static <V> Result<Optional<V>, Throwable> from(
				ThrowingSupplier<V> call
		) {
			try {
				return success(call.get());
			} catch (Throwable e) {
				return new op<>(null, e);
			}
		}

		/**
		 * Creates result, that wraps errors produced by <code>call</code>.
		 * <p>
		 * Rules of {@link Res#of Res::of} apply to the <code>call</code>.
		 * @param call Code that throws
		 * @return New result
		 */
		public static Result<Optional<Void>, Throwable> from(
				ThrowingRunnable call
		) {
			try {
				call.run();
				return success(null);
			} catch (Throwable e) {
				return new op<>(null, e);
			}
		}
	}
}

