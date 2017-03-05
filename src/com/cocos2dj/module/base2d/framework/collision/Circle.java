package com.cocos2dj.module.base2d.framework.collision;

import com.badlogic.gdx.math.Vector2;
import com.cocos2dj.module.base2d.framework.PhysicsObject;
import com.cocos2dj.module.base2d.framework.common.M22;
import com.cocos2dj.module.base2d.framework.common.MathUtils;

/** 
 * Circle <p>
 * 
 * @author xu jun
 * Copyright (c) 2012-2013. All rights reserved.
 */

public class Circle extends Shape {

	/**半径*/
	private float radius;
	/**旋转角度(rad)*/
	private float angle;
	private static final M22 m = new M22();
	/**圆心位置*/
	public final Vector2 circleCenter;

	
	public Circle(){
		super(Shape.ID_CIRCLE);
		circleCenter=new Vector2();
	}
	
	public Circle(final PhysicsObject obj) {
		this();
		this.physicsObject=obj;
	}

	
	/**获取圆的半径
	 * @return radius */
	public final float getRadius(){
		return radius;
	}

	/** 获取圆的位置
	 * @return position */
	public final Vector2 getCircleCenter(){
		return circleCenter;
	}
	
	public void computeShapeAABB() {
//		aabb.lowerBound.set(-radius, -radius);
//		aabb.upperBound.set(radius, radius);
		aabb.lowerBound.set(circleCenter.x-radius, circleCenter.y-radius);
		aabb.upperBound.set(circleCenter.x+radius, circleCenter.y+radius);
	}
	
	//不用计算
	public void computeShapeCenter() {
		
	}
		
	/**设置圆的半径  更新AABB
	 * @param radius */
	public final void setCircleRadius(final float radius){
		this.radius=radius;
		computeShapeAABB();
	}
	
	public final void setCircle(final float radius, final float x, final float y){
		this.radius=radius;
		circleCenter.set(x, y);
		computeShapeAABB();
	}

	/**绕(0,0)旋转angle 变更形状   更新AABB(逆时针)
	 * @param angle 单位是弧度 */
	public final void rotate(final float angle){
		this.angle += angle;
		this.angle %= MathUtils.TWOPI;
		m.set(angle);
	    M22.mulOut(m, circleCenter, circleCenter);
		computeShapeAABB();
	}
	
	/**绕(0,0)旋转angle 变更形状   更新AABB(逆时针)
	 * @param angle 单位是弧度 */
	public void setRotate(float rad) {
		this.angle %= MathUtils.TWOPI;
		rad %= MathUtils.TWOPI;
		m.set(rad-angle);
		M22.mulOut(m, circleCenter, circleCenter);
		computeShapeAABB();
	}

	public float getRotate() {
		return angle;
	}

	public void rotate90() {
		circleCenter.set(-circleCenter.y, circleCenter.x);  
		computeShapeAABB();
	}

	public void rotate180() {
		circleCenter.set(-circleCenter.x, -circleCenter.y);  
		computeShapeAABB();
	}

	public void rotate270() {
		circleCenter.set(circleCenter.y, -circleCenter.x);  
		computeShapeAABB();
	}

	public void trans(float x, float y) {
		circleCenter.add(x, y);
	}

	public boolean checkPoint(float x, float y) {
		final float dx = x;//x-this.x;
		final float dy = y;//-this.y;
		return(dx*dx + dy*dy <= radius * radius);
	}

	public void rotate90(boolean clockwise) {
		// TODO Auto-generated method stub
		
	}

	public void resetShapeAsRectangle(float x, float y, float width,
			float height) {
	}

	public void resetShapeAsPolygon(Vector2[] points) {
		
	}

	public void resetShapeAsCircle(float x, float y, float radious) {
		this.radius=radious;
		computeShapeAABB();
	}
}