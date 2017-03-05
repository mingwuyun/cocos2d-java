package com.cocos2dj.module.base2d.framework.collision;

import com.badlogic.gdx.math.Vector2;
import com.cocos2dj.module.base2d.framework.PhysicsObject;
import com.cocos2dj.module.base2d.framework.common.AABB;
import com.cocos2dj.module.base2d.framework.common.V2;

/**
 * TiledShape 瓦片形状<p>
 * 
 * 使用shape存储瓦片对象——这样的好处在于可以突破tile的限制，
 * 不仅仅是矩形瓦片，可以做出多边形瓦片的效果
 * 
 * points使用的坐标是本地坐标<br>
 * aabb使用的是世界坐标
 * 
 * @author xu jun
 * Copyright (c) 2016. All rights reserved.
 */
public class TileShape extends Shape {
	
//	public static final TILE
	
	private float cellWidth;
	private float cellHeight;
	private float x, y;
	private Shape[][] tiles;
	/**y*/
	private int rows;
	/**x*/
	private int cols;
	
	
	/**获取所有的tiles对象 
	 * 不建议通过这里做修改 */
	public final Shape[][] getTiles() {
		return this.tiles;
	}
	
	public void createTiles(final int rows, final int cols, float cellWidth, float cellHeight) {
		this.cellWidth = cellWidth;
		this.cellHeight = cellHeight;
		
		this.rows = rows;
		this.cols = cols;
		tiles = new Shape[rows][];
		for(int i = 0; i < rows; ++i) {
			tiles[i] = new Shape[cols];
		}
	}
	
	public float getCellWidth() {
		return this.cellWidth;
	}
	
	public float getCellHeight() {
		return this.cellHeight;
	}
	
	/**获取列(x)*/
	public int getCols() {
		return this.col;
	}
	
	/**获取行(y)*/
	public int getRows() {
		return this.row;
	}
	
//	public int valid
//	public float getWidth() {
//		return aabb.
//	}
	
	/**删除所有的元素 */
	public void destroyAll() {
		for(int i = 0; i < rows; ++i) {
			for(int j = 0; j < cols; ++j) {
				destroyShapeAt(i, j);
			}
		}
	}
	
	/** 获取指定位置的shape */
	public Shape getShapeAt(int row, int col) {
		return tiles[row][col];
	}
	
	/**删除指定位置的形状 
	 * @param row 行（y）
	 * @param col 列（x）*/
	public boolean destroyShapeAt(int row, int col) {
		Shape s = tiles[row][col];
		if(s == null) {
			return false;
		}
		s.col = -1;
		s.row = -1;
		s.colX = Float.NaN;
		s.rowY = Float.NaN;
		tiles[row][col] = null;
		return true;
	}
	
	/**在指定位置创建AABBshape 
	 * @param row 行（y）
	 * @param col 列（x）*/
	public void createAABBShapeAt(int row, int col) {
		AABBShape ab = new AABBShape(this.physicsObject);
		final float mx = x + col * cellWidth;
		final float my = y + row * cellHeight;
		ab.setAABBShape(0, 0, cellWidth, cellHeight);
		tiles[row][col] = ab;
		ab.col = col;
		ab.row = row;
		ab.colX = mx;
		ab.rowY = my;
		computeShapeAABB();
	}
	
	/**返回指定行列的shape
	 * @param row 行（y）
	 * @param col 列（x）
	 * @return 如果没有设定返回null */
	public Shape getShape(int row, int col) {
		return tiles[row][col];
	}
	
	/** 返回指定行列的位置存储到pos中
	 * @param row 行（y）
	 * @param col 列（x）
	 * @param pos
	 */
	public void getPosition(int row, int col, final Vector2 pos) {
		final float x = this.x + col * cellWidth;
		final float y = this.y + row * cellHeight;
		pos.set(x, y);
	}
	
	/**获取行的最小-最大范围（y）
	 * @param aabb
	 * @param ret */
	public void getMinMaxRow(final AABB aabb, final Vector2 ret) {
		 int minY = (int) ((aabb.lowerBound.y - y) / cellHeight);
		 int maxY = (int) ((aabb.upperBound.y - y) / cellHeight);
		if(minY < 0) minY = 0;
		if(minY >= rows) minY = rows - 1;
		if(maxY < 0) maxY = 0;
		if(maxY >= rows) maxY = rows - 1;
		ret.set(minY, maxY);
	}
	
	/**获取列的最小-最大范围（x）
	 * @param aabb
	 * @param ret */
	public void getMinMaxCol(final AABB aabb, final Vector2 ret) {
		 int minX = (int) ((aabb.lowerBound.x - x) / cellWidth);
		 int maxX = (int) ((aabb.upperBound.x - x) / cellWidth);
		if(minX < 0) minX = 0;
		if(minX >= cols) minX = cols - 1;
		if(maxX < 0) maxX = 0;
		if(maxX >= cols) maxX = cols - 1;
		ret.set(minX, maxX);
	}
	
	/**aabb的点集*/
//	private final Vector2[] points;
	//本来准备与包围AABB共用一个aabb.但是最终为了维护与多边形之间碰撞
	//检测的统一最终还是选用点集来实现
	

	public TileShape(){
		super(Shape.ID_TILE);
//		points=new Vector2[]{new Vector2(),new Vector2(),new Vector2(),new Vector2()};
	}
	
	public TileShape(final PhysicsObject obj) {
		this();
		this.physicsObject = obj;
	}
	
	
	
//	public final float getWidth(){
//		return points[2].x-points[0].x;
//	}
//	
//	public final float getHeight(){
//		return points[2].y-points[0].y;
//	}
	
	/**设置AABBShape的范围（按最小最大值设置）
	 * 坐标为相对于物理对象位置的坐标<br>
	 * 在调用后自动更新包围AABB*/
	public void setAABBShape(final float x0,final float y0,
			final float x1,final float y1){
//		points[0].set(x0, y0);
//		points[1].set(x1, y0);
//		points[2].set(x1, y1);
//		points[3].set(x0, y1);
		this.computeShapeAABB();
	}
	
	/**设置AABBShape的半长宽.其中position为该AABB的中点<br>
	 * 在调用后自动更新包围AABB
	 * @param halfWidth 半宽
	 * @param halfHeight 半长 */
	public void setAABBShape(final float halfWidth,final float halfHeight){
//		points[0].set(-halfWidth, -halfHeight);
//		points[1].set(halfWidth, -halfHeight);
//		points[2].set(halfWidth, halfHeight);
//		points[3].set(-halfWidth, halfHeight);
		this.computeShapeAABB();
	}
	
	/**由一个AABB生成一个AABBShape
	 * 坐标为相对物理对象坐标<br>
	 * 在调用后自动更新包围AABB*/
	public void setAABBShape(final AABB aabb){
//		points[0].set(aabb.lowerBound);
//		points[1].set(aabb.upperBound.x, aabb.lowerBound.y);
//		points[2].set(aabb.upperBound);
//		points[3].set(aabb.lowerBound.x, aabb.upperBound.y);
		this.computeShapeAABB();
	}
	
	/*(non-Javadoc)
	 * @see com.card2dphysics.module.shapes.Shape#computeShapeAABB()*/
	public void computeShapeAABB() {
		//这个AABB直接用了设置的AABB
		this.aabb.lowerBound.set(x, y);
		this.aabb.upperBound.set(x + cellWidth * cols, y + cellHeight * rows);
	}
	
	//不用计算
	public void computeShapeCenter() {
		
	}
	
	/**获取aabb图形的位置信息
	 * @return Vector2[] points*/
	public final Vector2[] getPoints(){
//		return points;
		return null;
	}

	public void rotate(float angle) {
		
	}
	
	/**旋转90*/
	public void rotate90(boolean clockwise){
		V2.swap(aabb.lowerBound);
		V2.swap(aabb.upperBound);
		this.computeShapeAABB();
	}

	public void trans(float x, float y) {
		this.computeShapeAABB();
	}

	public boolean checkPoint(final float x, final float y) {
//		return points[0].x < x && points[2].x > x 
//				&&  points[0].y < y &&  points[2].y > y;
		return false;
	}

	public void rotate90() {
		
	}

	public void rotate180() {
		
	}

	public void rotate270() {
		
	}

	public void resetShapeAsRectangle(float x, float y, float width, float height) {
//		points[0].set(x, y);
//		points[1].set(x+width, y);
//		points[2].set(x+width, y+height);
//		points[3].set(x, y+height);
		this.computeShapeAABB();
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
}