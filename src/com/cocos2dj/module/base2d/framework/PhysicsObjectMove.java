package com.cocos2dj.module.base2d.framework;

import com.badlogic.gdx.math.Vector2;
import com.cocos2dj.module.base2d.framework.collision.ContactCollisionData;
import com.cocos2dj.module.base2d.framework.common.TimeInfo;

/**MovePhsicsyObject<p>
 * 
 * 物理对象的标准实现。可以设置物体的速度，加速度等控制对象的移动,
 * 但是不可对其进行碰撞检测 <p>
 * 
 * 这个对象并不会添加到physics的碰撞系统中<br>
 * 
 * @author xujun
 * Copyright (c) 2012-2013. All rights reserved.
 */
public final class PhysicsObjectMove implements IPhysicsObject {

	/**动态物体位置*/
	public final Vector2 position;
	
	/**速度*/
	public final Vector2 velocity;
	
	/**加速度*/
	public final Vector2 accelerate;
	
	/**速度衰减率*/
//	private float damping = 1;
	
	//缓存数据
	/**前一个位置*/
	public final Vector2 prevPosition;
	
	public PhysicsObjectMove(){
		prevPosition = new Vector2();
		position = new Vector2();
		velocity = new Vector2();
		accelerate = new Vector2();
	}

	public float getDamping() {
		return 1;
	}

	public void setDamping(float damping) {
//		this.damping = damping;
	}

	public int getCollisionLevel() {
		return 0;
	}

	public final void setPosition(final float x,final float y) {
		prevPosition.set(position);
		position.set(x, y);
	}
	
	public final Vector2 getPosition() {
		return position;
	}

	public Vector2 getPrevPosition() {
		return this.prevPosition;
	}

	/**移动物理对象
	 * 这个方法会设置prePosition<br>*/
	public final float move(final TimeInfo time, final Vector2 argVector2, PhysicsObject obj) {
//		final Vector2 vector2 = velocity.scl(time.ratio);
		argVector2.set(velocity).scl(time.ratio);
		prevPosition.set(position);
		obj.updatePositionGenerators(time, argVector2);
		position.add(argVector2);
		return 0f;
	}
	
	public final void initAdvance(){
//		position.set(prevPosition);
	}

	public final void modifierPosition() {
		
	}

	public final void updateVelocity(final TimeInfo time){
		this.velocity.add(
				this.accelerate.x*time.ratio,
				this.accelerate.y*time.ratio);
//		velocity.mulLocal(damping);
	}
	
	public final void modifierPosition(final Vector2 MTD,  final ContactCollisionData data) {
		
	}

	public final void setVelocity(final Vector2 velocity) {
		this.velocity.set(velocity);
	}

	public void setVelocityX(float x) {
		velocity.x=x;
	}

	public void setVelocityY(final float y) {
		velocity.y=y;
	}

	public void setVelocity(final float x, final float y) {
		this.velocity.set(x, y);
	}

	public Vector2 getVelocity() {
		return this.velocity;
	}

	public void setVelocityDamping(float damping) {
//		this.damping=damping;
	}

	public float getVelocityDamping() {
		return 1;
	}

	public void setAccelerate(final Vector2 accelerate) {
		this.accelerate.set(accelerate);
	}

	public void setAccelerateX(final float x) {
		this.accelerate.x=x;
	}

	public void setAccelerateY(float y) {
		this.accelerate.y=y;
	}

	public void setAccelerate(float x, float y) {
		this.accelerate.set(x, y);
	}

	public Vector2 getAccelerate() {
		return accelerate;
	}

	@Override
	public void setCollisionLevel(int level) {
	}
}