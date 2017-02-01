package com.cocos2dj.basic;

import com.badlogic.gdx.math.MathUtils;

/**
 * STimer.java <p>
 * 
 * StormFramework中的计时器。该定时器采用查询的方式进行——STimer本身并不主动运行，只有调用特定的查询状态或
 * 更新方法后才会根据现在时刻来判断是否到定时时间。可以理解为需要自行添加运行线程<p>
 * 
 * 通过调用{@link #overTime()}方法来获取定时信息。
 * 如果注册了TimeListener则可以通过调用 {@link #updateTime()}方法来更新定时器状态。<p>
 * 
 * <pre>
 * STimer timer = new CCTimer(100);//100ms call
 * ...
 * void update() {
 * 	if(timer.overTime()) {
 * 		//do work
 * 	}
 * }
 * </pre>
 * 
 * 计时器的工作方式有：
 * <li>UNIFORM_DELAY_LOOP: 按指定的间隔计时
 * <li>ORDER_DELAY_LOOP：按指定的一组间隔计时
 * <li>ONCE：只计时一次，然后暂停执行
 * 
 * @author Copyright (c) 2015 xu jun
 */
public class BaseTimer {

//	Timer t;
	public static final int UNIFORM_DELAY_LOOP = 0;
	public static final int ORDER_DELAY_LOOP = 1;
	public static final int ONCE = 2;
	public static final int RANDOM = 3;
	
	private static long innerTime;
	private static boolean pauseFlag;

	
	
	public static final long getEngineTime() {
		return innerTime;
	}
	
	public static final void updateEngineTime(final int delay){
		if(pauseFlag){
			return;
		}
		innerTime += delay;
	}
	
	public static final void pauseEngineTime(boolean pause){
		pauseFlag = pause;
	}
	
	
	
	protected long nextTime = 0;
	private int type;
	protected boolean running;
	protected int currentiInterval;
	protected int[] intervals;
	
	
	
	public BaseTimer(){}
	
	public BaseTimer(int intervals){
		this(new int[]{intervals});
	}
	
	public BaseTimer(int interval, boolean running) {
		this(ORDER_DELAY_LOOP, running, new int[]{interval});
	}
	
	public BaseTimer(int...intervals){
		this(ORDER_DELAY_LOOP, intervals);
	}
	
	public BaseTimer(int type, int...intervals){
		this(type, true, intervals);
	}
	
	public BaseTimer(int type, boolean running, int...intervals){
		this.type = type;
		this.intervals = intervals;
		this.running = running;
		if(running) {
			updateNextTime();
		}
	}
	
	
	
	final void nextInterval(){
		switch(type) {
		case ORDER_DELAY_LOOP:
			if(++currentiInterval >= intervals.length){
				currentiInterval = 0;
			}
			break;
		case RANDOM:
			currentiInterval = MathUtils.random(intervals.length - 1);
			break;
		}
	}
	
	/**放缩时间*/
	public void scaleInterval(float scale) {
		for(int i = 0; i < intervals.length; ++i) {
			intervals[i] *= scale;
		}
	}
	
	public int getCurrentDelayIndex() {
		return currentiInterval;
	}

	public int getInterval() {
		return this.intervals[0];
	}
	
	public void setCurrentDelayIndex(int index) {
		if(type == UNIFORM_DELAY_LOOP){
			this.currentiInterval = 0;
			return;
		}
		this.currentiInterval = index;
	}

	public void setIntervals(int[] intervals) {
		this.intervals = intervals;
		type=ORDER_DELAY_LOOP;
	}
	
	public void setInterval(int interval) {
		this.intervals = new int[]{interval};
	}
	
	public void changeInterval(int position, int value){
		intervals[position] = value;
	}
	
	public final void changeDelay(final int interval){
		intervals[0] = interval;
//		nextTime = innerTime + delay;
	}

	/**@return 计时器当前工作方式
	 * <li>UNIFORM_DELAY_LOOP: 按指定的间隔计时
	 * <li>ORDER_DELAY_LOOP：按指定的一组间隔计时
	 * <li>ONCE：只计时一次，然后暂停执行 */
	public int getTimerType() {
		return type;
	}
	
	/**设置计时器工作方式
	 * @param type 
	 * <li>UNIFORM_DELAY_LOOP: 按指定的间隔计时
	 * <li>ORDER_DELAY_LOOP：按指定的一组间隔计时
	 * <li>ONCE：只计时一次，然后暂停执行 */
	public void setTimerType(int type) {
		this.type = type;
	}
	
	public final boolean isPause(){
		return !running;
	}
	
	public void pause(){
		running = false;
	}
	
	public void start(){
		updateNextTime();
		running = true;
	}
	
	/**重置定时器,会将延迟设置为delays[0],并设置暂停运行
	 * 需要调用 {@link #run()}方法继续运行 */
	public void reset(){
		currentiInterval = 0;
		running = false;
	}
	
	/**重启计时器 */
	public void restart(){
		currentiInterval = 0;
		running = true;
		updateNextTime();
	}
	
	public boolean updateTimer(){
		if(!running){
			return false;
		}
		final long currentTime = innerTime;
		if(currentTime > nextTime){
			nextInterval();
			nextTime = currentTime + intervals[currentiInterval];
			return true;
		}
		return false;
	}
	
	public boolean overTime() {
		return updateTimer();
	}
	
	public final boolean updateTimer(final long currentTime){
		if(!running){
			return false;
		}
		if(currentTime > nextTime){
			nextInterval();
			nextTime = currentTime + intervals[currentiInterval];
			return true;
		}
		return false;
	}
	
	/**使nextTime指向正确的时间 */
	public void updateNextTime() {
		nextTime = innerTime + intervals[currentiInterval];
	}
	
	/**使nextTime指向正确的时间 */
	public final void updateNextTime(final long currentTime){
		nextTime = currentTime + intervals[currentiInterval];
	}
	
	/**获取距离目标时间点的差值 该结果不会小于0 */
	public final long getDeltaTime() {
		final long ret = nextTime - innerTime;
		return ret >= 0 ? ret : 0;
	}
	
	/**获取nextTime属性 */
	public final long getNextTime() {
		return nextTime;
	}
}

