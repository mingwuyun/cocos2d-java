package com.cocos2dj.module.typefactory;

import com.cocos2dj.s2d.Node;

/**
 * PoolListener.java
 * <br>PoolNodeEvent
 * <p>
 * 
 * @author Copyright (c) 2016 xu jun
 */
public interface PoolListener {
	
	public static enum PoolEvent {
		/**awake*/Pop,
		/**sleep*/Push,
	}
	
	/**
	 * SObject事件，该事件不能自定义触发，由SObject触发
	 * 
	 * @param eventType
	 * @param obj
	 * @return 是否删除（返回true移除该监听）
	 */
	public boolean onObjectEvent(PoolEvent eventType, Node obj);
}
