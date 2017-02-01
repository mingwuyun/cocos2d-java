package com.cocos2dj.basic;

/**
 * Card2D Engine��ʱ��  �ö�ʱ�����ܵ�����״̬��Ӱ�죬��ͣ�����߳�ʱ��������ֹͣ <p>
 * 
 * �ö�ʱ�����ò�ѯ�ķ�ʽ���С���C2Timer�������������У�ֻ�е����ض��Ĳ�ѯ״̬��
 * ���·�����Ż��������ʱ�����ж��Ƿ񵽶�ʱʱ�䡣�������Ϊ��Ҫ������������߳�<p>
 * 
 * ͨ������{@link #overTime()}��������ȡ��ʱ��Ϣ��
 * ���ע����TimeListener�����ͨ������ {@link #updateTime()}���������¶�ʱ��״̬��<p>
 * 
 * ��ʱ���Ĺ�����ʽ�У�
 * <li>UNIFORM_DELAY_LOOP: ��ָ���ļ����ʱ
 * <li>ORDER_DELAY_LOOP����ָ����һ������ʱ
 * <li>ONCE��ֻ��ʱһ�Σ�Ȼ����ִͣ��
 * 
 * @author xu jun
 * Copyright (c) 2015. All rights reserved.
 */
public class BaseSystemTimer extends BaseTimer {
	
	public BaseSystemTimer(){}
	
	public BaseSystemTimer(int delay){
		super(new int[]{delay});
	}
	
	public BaseSystemTimer(int delay, boolean running) {
		super(ORDER_DELAY_LOOP, running, new int[]{delay});
	}
	
	public BaseSystemTimer(int...delays){
		super(ORDER_DELAY_LOOP, delays);
	}
	
	public BaseSystemTimer(int type, int...delays){
		super(type, true, delays);
	}
	
	public BaseSystemTimer(int type, boolean running, int...delays){
		super(type, running, delays);
	}

	
	@Override
	public boolean updateTimer(){
		if(!running){
			return false;
		}
		final long currentTime = System.currentTimeMillis();
		if(currentTime > nextTime){
			nextInterval();
			nextTime = currentTime + intervals[currentiInterval];
			return true;
		}
		return false;
	}
	
	@Override
	/**ʹnextTimeָ����ȷ��ʱ�� */
	public final void updateNextTime() {
		nextTime = System.currentTimeMillis() + intervals[currentiInterval];
	}
}

