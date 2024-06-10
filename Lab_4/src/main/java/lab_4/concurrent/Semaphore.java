package lab_4.concurrent;

public class Semaphore extends java.util.concurrent.Semaphore {

	private final int permitStorage;

	public int permitStorage() {
		return this.permitStorage;
	}

	public Semaphore(int permits) {
		super(permits);
		this.permitStorage = permits;
	}

	public Semaphore(
			int permits,
			boolean fair
	) {
		super(permits, fair);
		this.permitStorage = permits;
	}
}
