package com.cocos2dj.module.base2d.framework;

import com.badlogic.gdx.math.Vector2;
import com.cocos2dj.module.base2d.framework.collision.ContactCollisionData;
import com.cocos2dj.module.base2d.framework.collision.Shape;
import com.cocos2dj.module.base2d.framework.common.AABB;
import com.cocos2dj.module.base2d.framework.common.TimeInfo;
/**
 * StaticPhysicsObject<p>
 * 
 * Card2D物理对象的静态物体实现。静态物体不会在物理世界中进行移动，删除静态物体对象与删除
 * 非静态物体对象的位置不一样，尽量不要删除静态对象。<p>
 * 
 * 由于静态物体涉及的操作不多，所以大部分方法都是空实现
 * @author xujun
 * Copyright (c) 2012-2013. All rights reserved.
 */
public final class PhysicsObjectStatic implements IPhysicsObject {
	
	/**位置*/
	public final Vector2 position;
	final PhysicsObject obj;
	
	public PhysicsObjectStatic(PhysicsObject obj){
		this.obj = obj;
		this.position = new Vector2();
	}

	private void updateSweepAABB() {
		Shape shape = obj.getShapeList();
		shape.computeAABB(obj.sweepAABB, getPosition());
		final AABB poolAABB = new AABB();
		
		shape = shape.next;
		while(shape!=null){
			//计算下一个连接形状移动前后的AABB
			shape.computeAABB(poolAABB, getPosition());
			/*重新计算AABB的扫掠AABB*/
			obj.sweepAABB.combine(poolAABB);
			shape = shape.next;
		}
	}
	
	public final void setPosition(float x, float y) {
		position.set(x, y);
		updateSweepAABB();
		if(obj.getProxy() > -1) {
			Base2D.instance()._getBroadPhase().moveProxy(
					obj.getProxy(), obj.getSweepAABB(), new Vector2(0, 0));
		}
	}

	public Vector2 getPosition() {
		return position;
	}

	public Vector2 getPrevPosition() {
		return position;
	}

	public float move(TimeInfo time, Vector2 argVector2, PhysicsObject obj) {
		return 0f;
	}

	public void setVelocity(Vector2 velocity) {
		
	}

	public void setVelocityX(float x) {
		
	}

	public void setVelocityY(float y) {
		
	}

	public void setVelocity(float x, float y) {
		
	}
	
	/**这里仅保证不出错，*/
	public Vector2 getVelocity() {
		System.err.println("Static Physics Object Cannot move");
		return ZERO;
	}

	public void setVelocityDamping(float damping) {
		
	}

	public float getVelocityDamping() {
		return 0;
	}

	public void initAdvance() {
		
	}

	public void modifierPosition() {
		
	}

	public void modifierPosition(Vector2 MTD, ContactCollisionData data) {
		
	}

	public float getDamping() {
		return 0;
	}

	public int getCollisionLevel() {
		return 0;
	}

	public void updateVelocity(TimeInfo time) {
		
	}

	public void setAccelerate(Vector2 velocity) {
		
	}

	public void setAccelerateX(float x) {
		
	}

	public void setAccelerateY(float y) {
		
	}

	public void setAccelerate(float x, float y) {
		
	}
	@Override
	public void setCollisionLevel(int level) {
		
	}

	@Override
	public Vector2 getAccelerate() {
		return ZERO;
	}
}