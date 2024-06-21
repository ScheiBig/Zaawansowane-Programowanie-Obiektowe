package utilx.thread;

public abstract class IOTask<T> extends Thread {

	private final Object[] args;
	private T result;
	public T result() {
		return this.result;
	}

	public IOTask(Object... args) {
		this.args = args;
		this.setName("IOTask__" + this.getName());
	}

	public abstract T call(Object[] args);

	@Override
	public final void run() {
		this.result = this.call(this.args);
	}
}
