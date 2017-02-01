package com.cocos2dj.basic;

import com.badlogic.gdx.math.MathUtils;

/**
 * Storm Engine��ʱ��  �ö�ʱ�����ܵ�����״̬��Ӱ�죬��ͣ�����߳�ʱ��������ֹͣ <p>
 * 
 * �ö�ʱ�����ò�ѯ�ķ�ʽ���С���C2Timer�������������У�ֻ�е����ض��Ĳ�ѯ״̬��
 * ���·�����Ż��������ʱ�����ж��Ƿ񵽶�ʱʱ�䡣�������Ϊ��Ҫ������������߳�<p>
 * 
 * ͨ������{@link #overTime()}��������ȡ��ʱ��Ϣ��
 * ���ע����TimeListener�����ͨ������ {@link #updateTime()}���������¶�ʱ��״̬��<p>
 * 
 * �趨һ��ʱ�䷶Χ��������շ��ط�Χ�ڵ�ʱ����¼�ʱ
 * 
 * ��ʱ���Ĺ�����ʽ�У�
 * <li>UNIFORM_DELAY_LOOP: ��ָ���ļ����ʱ
 * <li>ORDER_DELAY_LOOP����ָ����һ������ʱ
 * <li>ONCE��ֻ��ʱһ�Σ�Ȼ����ִͣ��
 * 
 * @author xu jun
 * Copyright (c) 2015. All rights reserved.
 */
public class BaseRandTimer extends BaseTimer {
	
	private long start, end;
	
	public BaseRandTimer(){}
	
	public BaseRandTimer(long start, long end) {
		this.setIntervalRange(start, end);
		this.restart();
	}
	
	
	public void setIntervalRange(long start, long end) {
		this.start = start;
		this.end = end;
	}
	
//	public SRandTimer(int delay){
//		super(new int[]{delay});
//	}
//	
//	public SRandTimer(int delay, boolean running) {
//		super(ORDER_DELAY_LOOP, running, new int[]{delay});
//	}
//	
//	public SRandTimer(int...delays){
//		super(ORDER_DELAY_LOOP, delays);
//	}
//	
//	public SRandTimer(int type, int...delays){
//		super(type, true, delays);
//	}
//	
//	public SRandTimer(int type, boolean running, int...delays){
//		super(type, running, delays);
//	}
	
	@Override
	public boolean updateTimer(){
		if(!running){
			return false;
		}
		final long currentTime = BaseTimer.getEngineTime();
		if(currentTime > nextTime){
//			nextInterval();
			//�������
			long interval = MathUtils.random(start, end);
//			currentiInterval = MathUtils.random(intervals.length - 1);
			nextTime = currentTime + interval; // + intervals[currentiInterval];
			return true;
		}
		return false;
	}
	
	@Override
	/**ʹnextTimeָ����ȷ��ʱ�� */
	public final void updateNextTime() {
		long interval = MathUtils.random(start, end);
		nextTime = BaseTimer.getEngineTime() + interval; // + intervals[currentiInterval];		
//		nextTime = System.currentTimeMillis() + intervals[currentiInterval];
	}
}

