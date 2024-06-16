package utilx.concurrent;

/**
 * Extended {@link java.util.concurrent.Semaphore Semaphore}, that remembers initial permit count.
 *
 * @see java.util.concurrent.Semaphore
 */
public class Semaphore extends java.util.concurrent.Semaphore {

	private final int permitStorage;

	/**
	 * @return Initial count of permits - one that was provided in class constructor.
	 */
	public int permitStorage() {
		return this.permitStorage;
	}

	/**
	 * Creates new, non-fair semaphore.
	 * @param permits Number of initial permits - non-positive number requires releases before
	 *                   any acquire can succeed.
	 */
	public Semaphore(int permits) {
		super(permits);
		this.permitStorage = permits;
	}


	/**
	 * Creates new semaphore.
	 * @param fair Whether semaphore should be fair (FIFO access to permits).
	 * @param permits Number of initial permits - non-positive number requires releases before
	 *                   any acquire can succeed.
	 */
	public Semaphore(
			int permits,
			boolean fair
	) {
		super(permits, fair);
		this.permitStorage = permits;
	}
}
