package lab_4.concurrent.locks;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Monitor
		extends ReentrantLock
		implements Condition, AutoCloseable
{

	private final ReentrantLock lock;
	private final Condition cond;

	public Monitor() {
		this(new ReentrantLock(true));
	}

	public Monitor(
			ReentrantLock lock
	) {
		this.lock = lock;
		this.cond = lock.newCondition();
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

	public Monitor synchronize() {
		this.lock();
		return this;
	}

	@Override
	public void close() {
		this.unlock();
	}
}
