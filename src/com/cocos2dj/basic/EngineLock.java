package com.cocos2dj.basic;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**协调双线程的锁*/
public class EngineLock extends ReentrantLock {
	
	private static final long serialVersionUID = 671220941302523934L;
	
	final Condition mDrawingCondition = this.newCondition();
	final AtomicBoolean mDrawing = new AtomicBoolean(false);


	public EngineLock(final boolean pFair) {
		super(pFair);
	}
	
	/**唤醒绘制线程
	 * @see java.util.concurrent.locks.Condition.signalAll()*/
	public final void notifyCanDraw() {
		this.mDrawing.set(true);
		this.mDrawingCondition.signalAll();
	}

	/**唤醒逻辑线程*/
	public final void notifyCanUpdate() {
		this.mDrawing.set(false);
		this.mDrawingCondition.signalAll();
	}

	/**通知绘制线程等待*/
	public final void waitUntilCanDraw() throws InterruptedException {
		while(!this.mDrawing.get()) {
			this.mDrawingCondition.await();
		}
	}

	/**通知逻辑线程等待*/
	public final void waitUntilCanUpdate() throws InterruptedException {
		while(this.mDrawing.get()) {
			this.mDrawingCondition.await();
		}
	}
}