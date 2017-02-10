package com.cocos2dj.module.base2d.framework;

import com.badlogic.gdx.math.Vector2;
import com.cocos2dj.module.base2d.framework.collision.ContactCollisionData;
import com.cocos2dj.module.base2d.framework.common.TimeInfo;

/**
 * 物理对象接口<p>
 * 
 * 不要使用任何get方法进行修改
 * 
 * @author xujun
 * Copyright (c) 2015. All rights reserved.
 */
public interface IPhysicsObject {
	
	static final Vector2 ZERO = new Vector2();
	
	/**设置physicsObject的位置<br>
	 * 同时会更新prevPositon数据
	 * 在实现此方法时需注意
	 * @param x
	 * @param y */
	public void setPosition(final float x, final float y);
	
	/** 获取对象位置(只读不要修改 修改要用set方法)
	 * @return*/
	public Vector2 getPosition();

	/**获取对象前一个位置(只读不要修改 修改要用set方法)
	 * @return*/
	public Vector2 getPrevPosition();
	
	/**获取速度衰减率*/
	public float getDamping(); 
	
	public void setCollisionLevel(int level);
	
	/**获取碰撞等级*/
	public int getCollisionLevel();
	
	/**
	 * 移动物理对象并且将移动向量记录在argVector2中(经过time处理后的向量)<p>
	 * 返回的值是Z轴上的分量
	 * 
	 * @param time
	 * @param argVector2
	 * @return zClip 
	 */
	public float move(final TimeInfo time,final Vector2 argVector2, PhysicsObject obj);
	
	/**修正物理对象的位置 */
	public void modifierPosition();
	
//	public void 
	/**按照MTD修正物理对象位置(xy平面)
	 * {@link #modifierPositionXZ(Vector2)}
	 * @param MTD 
	 * @param data 碰撞信息 */
	public void modifierPosition(Vector2 MTD, ContactCollisionData data);
	
	/**更新速度
	 * @param time */
	public void updateVelocity(final TimeInfo time);
	
	/**设置碰撞数据 */
//	public void setContactData();
	
	/**根据给定的向量设置速度(复制数据)
	 * @param velocity */
	public void setVelocity(final Vector2 velocity);
	
	public void setVelocityX(final float x);
	
	public void setVelocityY(final float y);
	
	public void setVelocity(float x,float y);
	
	/**根据给定的向量设置速度(复制数据)
	 * @param velocity */
	public void setAccelerate(final Vector2 velocity);
	
	public void setAccelerateX(final float x);
	
	public void setAccelerateY(final float y);
	
	public void setAccelerate(float x,float y);
	
	/**@return velocity 速度向量引用*/
	public Vector2 getVelocity();
	
	/**@return accelerate 加速度向量引用*/
	public Vector2 getAccelerate();
	
	/**设置速度衰减
	 * @param damping */
	public void setVelocityDamping(float damping);
	
	/**获取速度衰减
	 * @return */
	public float getVelocityDamping();
	
	/**初始化advance的数据<br>用于contact的handle方法中 
	 * 将position重新设置为prevPosition*/
	public void initAdvance();
}