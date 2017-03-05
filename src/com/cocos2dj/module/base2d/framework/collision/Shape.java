package com.cocos2dj.module.base2d.framework.collision;

import com.badlogic.gdx.math.Vector2;
import com.cocos2dj.module.base2d.framework.PhysicsObject;
import com.cocos2dj.module.base2d.framework.common.AABB;

/**
 * shape<p>
 * 
 * 形状对象 由physicsobject创建
 * shape的形状只包含本地坐标信息 <br>
 * 具体的世界坐标位置包含在关联的physicsObject中
 * 
 * @author xujun
 * Copyright (c) 2015-2016. All rights reserved.
 */
public abstract class Shape {
	
	public static final int ID_TILE = 2;
	public static final int ID_AABB = 3;
	public static final int ID_POLYGON = 5;
	public static final int ID_CIRCLE = 7;
	
	/**中心点坐标 */
	public float centerX, centerY;
	
	/**包围形状的最小AABB*/
	public final AABB aabb;
	
	/**shape类型 AABB=3 polygon=5 circle=7*/
	public final int shapeTypeID;
	
	/**关联的physicsObject对象*/
	protected PhysicsObject physicsObject;
	
	/**同一个PhysicsObject关联的下一个shape对象*/
	public Shape next;
	
	private Object userData;
	
	/**行(y)*/
	protected int row = -1;
	/**列(x)*/
	protected int col = -1;

	protected float rowY = Float.NaN;
	protected float colX = Float.NaN;
	
	/**如果该对象是tile中的子形状返回true */
	public boolean isInTile() {
		return row != -1;
	}
	
	public Object getUserData() {
		return this.userData;
	}
	
	public void setUserData(Object userData) {
		this.userData = userData;
	}
	
	public float getRowY() {
		return this.rowY;
	}
	
	public float getColX() {
		return this.colX;
	}
	
	/**获取行(y)*/
	public int getRow() {
		return row;
	}
	
	/**获取列(x)*/
	public int getCol() {
		return col;
	}
	
	public Shape(int shapeID){
		shapeTypeID=shapeID;
		aabb=new AABB();
	}

	/**获取形状关联的physicsObject所在的位置
	 * @return position*/
	public final Vector2 getPosition(){
		return physicsObject.getPosition();
	}
	
	/**设置card2d物理对象
	 * @param obj*/
	public void setPhysicsObject(PhysicsObject obj){
		this.physicsObject=obj;
	}
	
	/**获取Card2D物理对象
	 * @return card2DPhysicsObject*/
	public PhysicsObject getPhysicsObject(){
		return physicsObject;
	}
	
	/**将ShapeAABB转换为在世界坐标中的AABB
	 * 结果存在argAABB中
	 * @param argAABB 
	 * @param position */
	public void computeAABB(final AABB argAABB, final Vector2 position){
		//由于不涉及旋转的物理模拟所以直接加上position即可
		//因此这个不是抽象方法
		argAABB.lowerBound.x=aabb.lowerBound.x+position.x;
		argAABB.lowerBound.y=aabb.lowerBound.y+position.y;
		argAABB.upperBound.x=aabb.upperBound.x+position.x;
		argAABB.upperBound.y=aabb.upperBound.y+position.y;
	}
	
	
	
	/**计算形状包围AABB 在初始化或更新形状操作后调用此方法生成包围AABB
	 * 这个方法最终结果存在基类shape的aabb*/
	public abstract void computeShapeAABB();
	/**计算中心点 */
	public abstract void computeShapeCenter();
	
	
	
	/**旋转shape的操作
	 * <b>注意当场景锁未释放时（调用scene中的checkSceneLock查看）
	 * 不可调用该方法</b>
	 * @param angle 旋转的角度 单位是rad */
	public abstract void rotate(final float rad);
	
	/**将形状旋转至指定的弧度（初始为0）
	 * @param angle 旋转的角度 单位是rad */
	public abstract void setRotate(final float rad);
	
	/**@return 当前的形状相比原始形状的旋转角度（rad）*/
	public abstract float getRotate();
	
	
	
	/**平移shape的位置
	 * <b>注意当场景锁未释放时（调用scene中的checkSceneLock查看）
	 * 不可调用该方法</b>
	 * @param x
	 * @param y */
	public abstract void trans(final float x,final float y);
	
	/**查询指定点是否在该shape中
	 * @param x   测试点的x坐标
	 * @param y   测试点的y坐标
	 * @return <code>true</code> 点在shape中
	 * <code>false</code> 点不在shape中*/
	public abstract boolean checkPoint(final float x,final float y);
	
	/**绕(0,0)逆时针转90度 */
	public abstract void rotate90();
	
	/**绕(0,0)逆时针转180度 */
	public abstract void rotate180();
	
	/**绕(0,0)逆时针转270度 */
	public abstract void rotate270();
	
	/**设置形状为指定尺寸的矩形<br>
	 * 可用于AABB以及Polygon形状
	 * @param x 左下角x坐标
	 * @param y 左下角y坐标
	 * @param width 矩形的宽
	 * @param height 矩形的高 */
	public abstract void resetShapeAsRectangle(final float x, final float y, 
			final float width, final float height);
	
	/**设置形状为指定的圆形
	 * 此方法前两个参数保留 
	 * @param x
	 * @param y
	 * @param radious */
	public abstract void resetShapeAsCircle(final float x, final float y,final float radious);
	
	/**设置形状为指定的多边形
	 * 仅使用与polygon形状 
	 * @param points 各个点的坐标 顺序为逆时针 */
	public abstract void resetShapeAsPolygon(Vector2[] points);
}
