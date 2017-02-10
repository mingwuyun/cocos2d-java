package com.cocos2dj.module.base2d.framework.collision;

/**
 * 碰撞数据<p>
 * 
 * @author Copyright (c) 2015-2017. xujun 
 */
public class ContactCollisionData {
//	public V2 point;
//	public float depth;
//	public boolean isXZ = false;
	
	public Shape 	shape1;
	
	public Shape 	shape2;
	/**动摩擦系数（衰减计算） */
	public float 	retFriction = 0f;
	/**静摩擦系数（修正计算） */
	public float 	retStaticFriction = 0f;
	
	public void clear() {
		shape1 = null;
		shape2 = null;
	}
}
