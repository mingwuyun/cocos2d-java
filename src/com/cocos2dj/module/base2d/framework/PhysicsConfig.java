package com.cocos2dj.module.base2d.framework;

/**物理相关配置 */
public final class PhysicsConfig {
	
	/**模拟时间间隔单位为ms 建议选择32ms（对应1/30）或16ms（对应1/60）*/
	public float dt = 32f;			
	
	/**碰撞迭代求解次数*/
	public int iteration = 2;
	
	public float LimitX = 1000;
	
	public float LimitY = 1000;
	
}
