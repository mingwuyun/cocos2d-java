package com.cocos2dj.module.base2d.framework;

import com.badlogic.gdx.math.Vector2;
import com.cocos2dj.module.base2d.framework.common.TimeInfo;

/**
 * PhysicsGenerator.java
 * <p>
 * 
 * @author copyright(c) 2017 xujun
 */
public abstract class PhysicsGenerator {

	/**
	 * generator 帧更新
	 * @param time
	 * @param target
	 * @return
	 */
	public abstract boolean onUpdate(PhysicsObject target, TimeInfo time);
	
	/**
	 * 用于修正速度
	 * @param targetVelocity
	 */
	public abstract void onUpdateVelocity(PhysicsObject target, TimeInfo time, Vector2 targetVelocity);
	
	/**
	 * 修正位移偏移向量
	 * @param positionDelta
	 */
	public abstract void onUpdatePosition(PhysicsObject target, TimeInfo time, Vector2 positionDelta);
	
	
	protected boolean				endFlag;
	public void stop() {
		endFlag = true;
	}
	public boolean isStop() { return endFlag;}
	
	
	
	public static final PhysicsGenerator TEST = new PhysicsGenerator() {

		@Override
		public boolean onUpdate(PhysicsObject target, TimeInfo time) {
			System.out.println("TestGenerator: onUpdate ");
			return false;
		}

		@Override
		public void onUpdateVelocity(PhysicsObject target, TimeInfo time, Vector2 targetVelocity) {
			System.out.println("TestGenerator: onUpdate Velocity");
		}

		@Override
		public void onUpdatePosition(PhysicsObject target, TimeInfo time, Vector2 positionDelta) {
			System.out.println("TestGenerator: onUpdate Position");
		} 
		
	};
}
