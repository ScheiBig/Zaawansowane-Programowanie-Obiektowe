package lab_4.concurrent.locks;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class TwoWayMonitor extends ReentrantLock {

	public final Condition read;
	public final Condition write;

	private final ReentrantLock lock;

	public TwoWayMonitor() {
		this.lock = new ReentrantLock(true);
		this.read = this.lock.newCondition();
		this.write = this.lock.newCondition();
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
		return lock.toString();
	}
}
