package com.cocos2dj.module.base2d.framework;

import com.badlogic.gdx.math.Vector2;
import com.cocos2dj.module.base2d.framework.common.TimeInfo;
import com.cocos2dj.module.base2d.framework.common.V2;
/**
 * DynamicPhsicsyObject<p>
 * 
 * 物理对象的标准实现。可以设置物体的速度，加速度等控制对象的移动并可以由Card2DPhysics
 * 对其进行碰撞的侦测。<p>
 * 
 * Card2D Physics主要的应用方向并不是物理仿真所以没有引入“力”的概念。
 * 
 * @author xujun
 * Copyright (c) 2012-2015. All rights reserved.
 */
public final class PhysicsObjectDynamic implements IPhysicsObject {
	
	/**动态物体位置*/
	public final Vector2 position = new Vector2();
	/**速度*/
	public final Vector2 velocity = new Vector2();
	/**加速度*/
	public final Vector2 accelerate = new Vector2();
	/**碰撞等级*/
	private int collisionLevel;
	/**速度衰减率*/
//	private float damping = 1;
	//缓存数据
	public final Vector2 prevPosition = new Vector2();
	private static final Vector2 pool = new Vector2();
	
	
	public PhysicsObjectDynamic(){}

	
	public float getDamping() {
		return 1f;
	}

	public void setDamping(float damping) {
//		this.damping = damping;
	}

	public int getCollisionLevel() {
		return collisionLevel;
	}
	
	public void setCollisionLevel(final int collisionLevel){
		this.collisionLevel=collisionLevel;
	}

	public final void setPosition(final float x,final float y) {
		prevPosition.set(position);
		position.set(x, y);
	}
	
	public final Vector2 getPosition() {
		return position;
	}

	public final Vector2 getPrevPosition() {
		return this.prevPosition;
	}

	/**移动物理对象
	 * 这个方法会设置prePosition<br>
	 * @return */
	public final float move(final TimeInfo time, final Vector2 argVector2) {
		argVector2.set(velocity.x * time.ratio, velocity.y * time.ratio);
		prevPosition.set(position);
		position.add(argVector2);
		return 0f;
		
	}
	
	public final void initAdvance() {
		position.set(prevPosition);
	}

	public final void modifierPosition() {}

	public final void updateVelocity(final TimeInfo time){
		this.velocity.add(
				this.accelerate.x*time.ratio,
				this.accelerate.y*time.ratio);
//		velocity.mulLocal(damping);
	}
	
	/*(non-Javadoc)
	 * @see com.card2dphysics.module.IPhysicsObject#modifierPosition(com.card2dphysics.system.Vector2)*/
	public final void modifierPosition(final Vector2 MTD) {
		position.add(MTD);
//		if(keepSpeed){
//			return;
//		}
		if(V2.normalize(MTD) == 0){
			return;
		}
		
		pool.set(MTD.y, -MTD.x);
		
		//如果修正方向与速度方向相同则不修正速度
		if(V2.cross(velocity, pool)<0) return;
		
		final float f = V2.dot(velocity, pool);
		velocity.set(pool.scl(f));
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
		return 1f;
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
}