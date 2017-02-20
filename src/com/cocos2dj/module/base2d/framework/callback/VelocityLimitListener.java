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
	
}
