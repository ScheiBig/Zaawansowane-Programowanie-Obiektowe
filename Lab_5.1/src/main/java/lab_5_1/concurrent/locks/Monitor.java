package lab_5_1.concurrent.locks;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class can be used as a drop-in replacement for built-in monitors (that are used with
 * <code>synchronized</code>, {@link Object#wait}, {@link Object#notify},
 * {@link Object#notifyAll()}, as it implements both {@link ReentrantLock} and {@link Condition}.
 * <p>
 * Underneath, a fair <code>ReentrantLock</code> is used, as well as single instance of
 * <code>Condition</code> created with {@link ReentrantLock#newCondition()}.
 *
 * @see ReentrantLock
 * @see Condition
 */
public class Monitor
		extends ReentrantLock
		implements Condition
{

	private final ReentrantLock lock;
	private final Condition cond;

	/**
	 * Creates new <code>Monitor</code> instance.
	 */
	public Monitor() {
		this.lock = new ReentrantLock(true);
		this.cond = this.lock.newCondition();
	}

	@Override
	public void lock() {
		lock.lock();
	}

	@Override
	public void lockInterruptibly()
	throws InterruptedException {
		lock.lockInterruptibly();
	}

	@Override
	public boolean tryLock() {
		return lock.tryLock();
	}

	@Override
	public boolean tryLock(
			long timeout,
			TimeUnit unit
	)
	throws InterruptedException {
		return lock.tryLock(timeout, unit);
	}

	@Override
	public void unlock() {
		lock.unlock();
	}

	@Override
	public Condition newCondition() {
		return lock.newCondition();
	}

	@Override
	public int getHoldCount() {
		return lock.getHoldCount();
	}

	@Override
	public boolean isHeldByCurrentThread() {
		return lock.isHeldByCurrentThread();
	}

	@Override
	public boolean isLocked() {
		return lock.isLocked();
	}

	@Override
	public boolean hasWaiters(Condition condition) {
		return lock.hasWaiters(condition);
	}

	@Override
	public int getWaitQueueLength(Condition condition) {
		return lock.getWaitQueueLength(condition);
	}

	@Override
	public String toString() {
		return lock.toString() + "\n" + cond.toString();
	}

	@Override
	public void await()
	throws InterruptedException {
		cond.await();
	}

	@Override
	public void awaitUninterruptibly() {
		cond.awaitUninterruptibly();
	}

	@Override
	public long awaitNanos(long nanosTimeout)
	throws InterruptedException {
		return cond.awaitNanos(nanosTimeout);
	}

	@Override
	public boolean await(
			long time,
			TimeUnit unit
	)
	throws InterruptedException {
		return cond.await(time, unit);
	}

	@Override
	public boolean awaitUntil(@NotNull Date deadline)
	throws InterruptedException {
		return cond.awaitUntil(deadline);
	}

	@Override
	public void signal() {
		cond.signal();
	}

	@Override
	public void signalAll() {
		cond.signalAll();
	}
}
