package com.cocos2dj.module.base2d.framework.callback;

import com.badlogic.gdx.math.Vector2;

/**
 * VelocityLimitListener.java
 * <>p
 * 用于限制最大速度
 * 
 * @author xu jun
 */
public interface VelocityLimitListener {
	
	public void onVelocity(Vector2 velocity);
	
	public static final VelocityLimitListener NULL = new VelocityLimitListener() {
		@Override
		public void onVelocity(Vector2 velocity) {}
	};
	
	/**
	 * 创建限定双方向速度的限制器
	 * 
	 * @param limitX (limitX >= 0)
	 * @param limitY (limitY >= 0)
	 * @return
	 */
	public static VelocityLimitListener create(final float limitX, final float limitY) {
		return new VelocityLimitListener() {
			@Override
			public void onVelocity(Vector2 velocity) {
				if(velocity.y > limitY) {
					velocity.y = limitY;
				} else if(velocity.y < -limitY) {
					velocity.y = -limitY;
				}
				
				if(velocity.x > limitX) {
					velocity.x = limitX;
				} else if(velocity.x < -limitX) {
					velocity.x = -limitX;
				}
			}
		};
	}
}
