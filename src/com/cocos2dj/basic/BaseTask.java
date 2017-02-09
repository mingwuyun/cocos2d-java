package com.cocos2dj.basic;

/**
 * BaseTask.java
 * <p>
 * 
 * 推荐的使用方法:
 * <pre>
 * new BaseTask(()->{
 * //do something
 * }.attachSchedule(); //添加到main（cocos）线程执行
 * 
 * new BaseTask(()->{
 * //do something
 * }.attachScheduleToRender(); //添加到render（gl）线程执行 
 * </pre>
 * 
 * @author Copyright (c) 2016 xu jun
 */
public class BaseTask extends BaseUpdater {
	
	private Runnable 	runnable;
	private int 		frameDelay = -1;
	
	public static BaseTask create(Runnable runnable) {
		return new BaseTask(runnable);
	}
	
	public BaseTask(Runnable runnable) {
		this(runnable, -1);
	}
	
	public BaseTask(Runnable runnable, int frameDelay) {
		this.runnable = runnable;
		this.frameDelay = frameDelay;
	}
	
	@Override
	protected final boolean onUpdate(float dt) {
		if(frameDelay-- <= 0) {
			runnable.run();
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected final void onEnd() {
		
	}

}

