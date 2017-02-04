package com.cocos2dj.module.base2d.framework.collision;

/**碰撞数据<p>
 * 
 * @author xujun
 * Copyright (c) 2015-2016. All rights reserved. */
public class ContactCollisionData {
	
//	/**碰撞点 */
//	public V2 point;
//	
//	/**相交深度 */
//	public float depth;

	public boolean isXZ = false;
	
	/**应该提供碰撞的两个shape */
	public Shape shape1;
	
	public Shape shape2;

	public void clear() {
		shape1 = null;
		shape2 = null;
	}
}
