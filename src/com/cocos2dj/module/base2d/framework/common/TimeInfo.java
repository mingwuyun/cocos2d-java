package com.cocos2dj.module.base2d.framework.common;

/**
 * 仿真所需的时间信息
 */
public class TimeInfo {
	
	/**模拟时间间隔单位为ms 建议选择32ms（对应1/30）或16ms（对应1/60）*/
	public float dt = 32f;			
	
	/**引擎输入的模拟时长 */
	public float realDt;
	
	/**时间执行比率(实际时间/模拟时间间隔)*/
	public float ratio = 1;
	
	/**迭代次数*/
	public int iteration = 2;
	
	/**迭代次数的倒数*/
	public float inv_iteration = 1f / iteration;
	
	
	/**设置迭代次数
	 * 用这个方法会更新inv_iteration*/
	public void setIteration(final int iteration){
		this.iteration = iteration;
		this.inv_iteration = 1f / this.iteration;
	}
	
	/**更新时间信息的ratio信息
	 * @param timeDelay 距离上一次执行的时间间隔(ms)*/
	public final void update(final float timeDelay){
		realDt = timeDelay;
		ratio = timeDelay/dt;
	}
}
