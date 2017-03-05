package com.cocos2dj.module.base2d.framework.collision;

import com.badlogic.gdx.math.Vector2;
import com.cocos2dj.module.base2d.framework.PhysicsObject;
import com.cocos2dj.module.base2d.framework.common.AABB;
import com.cocos2dj.module.base2d.framework.common.V2;

/**
 * AABBShape<p>
 * Box形状（即AABB）对物理要求不高的游戏中建议使用的物理对象<br>
 * 将一个polygon设置为AABB可以减少物理计算的时间<br>
 * points使用的坐标是本地坐标<br>
 * aabb使用的是世界坐标
 * 
 * @author xu jun
 * Copyright (c) 2012-2013. All rights reserved.
 */
public final class AABBShape extends Shape {
	
	/**aabb的点集*/
	private final Vector2[] points;
	//本来准备与包围AABB共用一个aabb.但是最终为了维护与多边形之间碰撞
	//检测的统一最终还是选用点集来实现
	

	public AABBShape(){
		super(Shape.ID_AABB);
		points=new Vector2[]{new Vector2(),new Vector2(),new Vector2(),new Vector2()};
	}
	
	public AABBShape(final PhysicsObject obj) {
		this();
		this.physicsObject = obj;
	}
	
	
	
	public final float getWidth(){
		return points[2].x-points[0].x;
	}
	
	public final float getHeight(){
		return points[2].y-points[0].y;
	}
	
	/**设置AABBShape的范围（按最小最大值设置）
	 * 坐标为相对于物理对象位置的坐标<br>
	 * 在调用后自动更新包围AABB*/
	public void setAABBShape(final float x0,final float y0,
			final float x1,final float y1){
		points[0].set(x0, y0);
		points[1].set(x1, y0);
		points[2].set(x1, y1);
		points[3].set(x0, y1);
		this.computeShapeAABB();
		computeShapeCenter();
	}
	
	/**设置AABBShape的半长宽.其中position为该AABB的中点<br>
	 * 在调用后自动更新包围AABB
	 * @param halfWidth 半宽
	 * @param halfHeight 半长 */
	public void setAABBShape(final float halfWidth,final float halfHeight){
		points[0].set(-halfWidth, -halfHeight);
		points[1].set(halfWidth, -halfHeight);
		points[2].set(halfWidth, halfHeight);
		points[3].set(-halfWidth, halfHeight);
		this.computeShapeAABB();
		computeShapeCenter();
	}
	
	/**由一个AABB生成一个AABBShape
	 * 坐标为相对物理对象坐标<br>
	 * 在调用后自动更新包围AABB*/
	public void setAABBShape(final AABB aabb){
		points[0].set(aabb.lowerBound);
		points[1].set(aabb.upperBound.x, aabb.lowerBound.y);
		points[2].set(aabb.upperBound);
		points[3].set(aabb.lowerBound.x, aabb.upperBound.y);
		this.computeShapeAABB();
		computeShapeCenter();
	}
	
	/*(non-Javadoc)
	 * @see com.card2dphysics.module.shapes.Shape#computeShapeAABB()*/
	public void computeShapeAABB() {
		//这个AABB直接用了设置的AABB
		this.aabb.lowerBound.set(points[0]);
		this.aabb.upperBound.set(points[2]);
	}
	
	public void computeShapeCenter() {
		centerX = (points[0].x + points[2].x) / 2;
		centerY = (points[0].y + points[2].y) / 2;
	}
	
	
	
	
	/**获取aabb图形的位置信息
	 * @return Vector2[] points*/
	public final Vector2[] getPoints(){
		return points;
	}

	public void rotate(float angle) {
		
	}
	
	/**旋转90*/
	public void rotate90(boolean clockwise){
		V2.swap(aabb.lowerBound);
		V2.swap(aabb.upperBound);
		points[0].set(aabb.lowerBound);
		points[1].set(aabb.upperBound.x, aabb.lowerBound.y);
		points[2].set(aabb.upperBound);
		points[3].set(aabb.lowerBound.x, aabb.upperBound.y);
		this.computeShapeAABB();
		computeShapeCenter();
	}

	public void trans(float x, float y) {
		for(Vector2 v:points){
			v.add(x,y);
		}
		this.computeShapeAABB();
		computeShapeCenter();
	}

	public boolean checkPoint(final float x, final float y) {
		return points[0].x < x && points[2].x > x 
				&&  points[0].y < y &&  points[2].y > y;
//		return false;
	}

	public void rotate90() {
		points[0].set(-aabb.upperBound.y, aabb.lowerBound.x);
		points[1].set(-aabb.lowerBound.y, aabb.lowerBound.x);
		points[2].set(-aabb.lowerBound.y, aabb.upperBound.x);
		points[3].set(-aabb.upperBound.y, aabb.upperBound.x);
		computeShapeAABB();
		computeShapeCenter();
	}

	public void rotate180() {
		points[0].set(-aabb.upperBound.x, -aabb.upperBound.y);
		points[1].set(-aabb.lowerBound.x, -aabb.upperBound.y);
		points[2].set(-aabb.lowerBound.x, -aabb.lowerBound.y);
		points[3].set(-aabb.upperBound.x, -aabb.lowerBound.y);
		computeShapeAABB();
		computeShapeCenter();
	}

	public void rotate270() {
		points[0].set(aabb.lowerBound.y, -aabb.upperBound.x);
		points[1].set(aabb.upperBound.y, -aabb.upperBound.x);
		points[2].set(aabb.upperBound.y, -aabb.lowerBound.x);
		points[3].set(aabb.lowerBound.y, -aabb.lowerBound.x);
		computeShapeAABB();
		computeShapeCenter();
	}

	public void resetShapeAsRectangle(float x, float y, float width,
			float height) {
		points[0].set(x, y);
		points[1].set(x+width, y);
		points[2].set(x+width, y+height);
		points[3].set(x, y+height);
		this.computeShapeAABB();
		computeShapeCenter();
	}

	public void resetShapeAsPolygon(Vector2[] points) {
		
	}

	public void resetShapeAsCircle(float x, float y, float radious) {
		
	}

	public void setRotate(float rad) {
		
	}

	public float getRotate() {
		return 0;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[AABB] : ").append("{");
		for(Vector2 p : points) {
			sb.append(p).append(',');
		}
		sb.append("}");
		return sb.toString();
	}
}