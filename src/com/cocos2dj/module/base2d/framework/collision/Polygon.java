package com.cocos2dj.module.base2d.framework.collision;

import com.badlogic.gdx.math.Vector2;
import com.cocos2dj.module.base2d.framework.PhysicsObject;
import com.cocos2dj.module.base2d.framework.common.M22;
import com.cocos2dj.module.base2d.framework.common.MathUtils;

/**
 * Polygon<p>
 * 多边形对象    建议只创建三角形
 * 
 * @author xu jun
 * Copyright (c) 2012-2013. All rights reserved.
 */

public final class Polygon extends Shape {

	/**多边形点集合*/
	private Vector2[] points;
	private static final M22 m = new M22();
	/**当前形状的角度*/
	private float angle;

	
	public Polygon(){
		super(Shape.ID_POLYGON);
	}
	
	public Polygon(PhysicsObject obj){
		super(Shape.ID_POLYGON);
		this.physicsObject=obj;
	}

	
	
	/** 获取现在的点的坐标(Array)*/
	public final Vector2[] getPoints(){
		return points;
	}

	
	public void computeShapeAABB() {
		float right=Float.MIN_VALUE;
		float left=Float.MAX_VALUE;
		float top=Float.MIN_VALUE;
		float bottom=Float.MAX_VALUE;
		
		for(Vector2 v:points){
			right=v.x > right ? v.x : right;
			left=v.x < left ? v.x : left;
			top=v.y > top ? v.y : top;
			bottom=v.y < bottom ? v.y : bottom;
		}
		
		aabb.lowerBound.set(left,bottom);
		aabb.upperBound.set(right,top);
	}
	
	public void computeShapeCenter() {
		final int len = points.length;
		float tempX = 0, tempY = 0;
		for(int i = 0; i < len; ++i) {
			tempX += points[i].x;
			tempY += points[i].y;
		}
		centerX = tempX / len;
		centerY = tempY / len;
	}
	
	
	
	/**设置多边形边界    更新AABB
	 * 使用本地坐标 <br>
	 * <b>这个方法直接将points设置为points而不是复制数据</b>
	 * @param points Vector2[]*/
	public final void setPoints(final Vector2...points){
		this.points = points;
		computeShapeAABB();
		computeShapeCenter();
	}
	
	/**设置多边形边界   更新AABB
	 * 使用本地坐标
	 * <b>逆时针方向</b>
	 * @param points float[]*/
	public final void setPoints(final float...points){
		int pointCount=points.length/2;
		this.points=new Vector2[pointCount];
		for(int i=0; i<pointCount; ++i){
			this.points[i]=new Vector2(points[2*i],points[2*i+1]);
		}
		computeShapeAABB();
		computeShapeCenter();
	}
	
	/**指定四边的范围创建一个矩形  更新AABB
	 * 使用的是本地坐标(x0,y0)-(x1,y1)
	 * @param x0
	 * @param y0
	 * @param x1
	 * @param y1
	 * @return Polygon */
	public final void setAsBox(final float x0, final float y0, final float x1,final float y1){
		setPoints(new Vector2(x0,y0),new Vector2(x1,y0),
				new Vector2(x1,y1),new Vector2(x0,y1));
		computeShapeAABB();
		computeShapeCenter();
	}
	
	/**指定半宽与半长创建一个矩形  更新AABB
	 * @param halfWidth
	 * @param halfHeight*/
	public final void setAsBox(float halfWidth, float halfHeight){
		setPoints(new Vector2(-halfWidth,-halfHeight),new Vector2(halfWidth,-halfHeight),
				new Vector2(halfWidth,halfHeight),new Vector2(-halfWidth,halfHeight));
		computeShapeAABB();
		computeShapeCenter();
	}

	/**绕(0,0)旋转angle 变更形状   更新AABB(逆时针)
	 * @param angle 单位是弧度 */
	public final void rotate(final float angle){
		this.angle += angle;
		this.angle = MathUtils.reduceAngle(this.angle);
		m.set(angle);
		for(Vector2 v: points){
			M22.mulOut(m, v, v);
//			M22.m
//			v.set(m.mul(v));
		}
		computeShapeAABB();
		computeShapeCenter();
	}
	
	/**绕(0,0)旋转angle 变更形状   更新AABB(逆时针)
	 * @param angle 单位是弧度 */
	public void setRotate(float rad) {
		this.angle %= MathUtils.TWOPI;
		rad %= MathUtils.TWOPI;
		m.set(rad-angle);
		for(Vector2 v: points){
			M22.mulOut(m, v, v);
//			M22.m
//			v.set(m.mul(v));
		}
		computeShapeAABB();
		computeShapeCenter();
	}

	public float getRotate() {
		return angle;
	}
	
	/**平移 变更形状	更新AABB
	 * @param x
	 * @param y */
	public final void trans(final float x,final float y){
		for(Vector2 v: points){
			v.add(x, y);
		}
		computeShapeAABB();
		computeShapeCenter();
	}
	
	/**平移 变更形状	更新AABB
	 * @param tran */
	public final void trans(final Vector2 tran){
		for(Vector2 v: points){
			v.add(tran);
		}
		computeShapeAABB();
		computeShapeCenter();
	}

	private static Vector2 temp=new Vector2();
	public boolean checkPoint(float x, float y) {
		return ShapeAlgorithms.PointInConvexPolygon(temp.set(x, y), points);
//		return false;
	}

	public void rotate90(boolean clockwise) {
		
	}

	public void rotate90() {
		for(Vector2 v:points){
			v.set(-v.y, v.x);  
			//多边形的points顺序可以乱所以区别于aabb的相关方法
		}
		computeShapeAABB();
		computeShapeCenter();
	}

	public void rotate180() {
		for(Vector2 v:points){
			v.set(-v.x, -v.y);  
		}
		computeShapeAABB();
		computeShapeCenter();
	}

	public void rotate270() {
		for(Vector2 v:points){
			v.set(v.y, -v.x);  
		}
		computeShapeAABB();
		computeShapeCenter();
	}

	public void resetShapeAsRectangle(float x, float y, float width,
			float height) {
		setPoints(new Vector2(x,y),new Vector2(x+width,y),
				new Vector2(x+width,y+height),new Vector2(x,y+height));
		computeShapeAABB();
		computeShapeCenter();
	}

	public void resetShapeAsPolygon(Vector2[] points) {
		this.points=points;
		computeShapeAABB();
		computeShapeCenter();
	}

	public void resetShapeAsCircle(float x, float y, float radious) {
		
	}
}