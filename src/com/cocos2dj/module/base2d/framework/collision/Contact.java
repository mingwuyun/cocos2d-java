package com.cocos2dj.module.base2d.framework.collision;

import com.badlogic.gdx.math.Vector2;
import com.cocos2dj.module.base2d.framework.PhysicsObject;
import com.cocos2dj.module.base2d.framework.callback.ContactListener;
import com.cocos2dj.module.base2d.framework.common.AABB;
import com.cocos2dj.module.base2d.framework.common.TimeInfo;

import static com.cocos2dj.module.base2d.framework.collision.CollideAlgorithms.*;

import java.util.ArrayList;

/**
 * Contact<p>
 * 
 * 两个代理AABB靠近时如果满足求解条件会创建Contact.调用handle对contact对象识别并
 * 调用相对应的方法求解碰撞并报告<p>
 * 
 * 新版实现了contact缓存，并修正了程序冗余结构。
 * 
 * @author xujun
 * Copyright (c) 2015-2016. All rights reserved.
 */

public final class Contact {
	
	private static final int FLAG_CONTACT_REMOVE  = 0x02;
	private static final int FLAG_AABB_TEST = 0x01;
	public final static int TYPE_2D = 0;
	
	/**
	 * 用来传递MTD数据的pool
	 * 同时用于记录3D检测时的第一个MTD
	 * */
	private static final Vector2 pool1 = new Vector2();
	/**
	 * 用来传递tile检测时，瓦片范围的最小值和最大值
	 */
	private static final Vector2 pool2 = new Vector2();
//	/**
//	 * 用于缓存contact是否需要MTD的状态
//	 */
//	private static boolean pool_useMTD = false;
//	/**
//	 * MTD向量是否是反向（修正时需要使用）
//	 */
//	private static boolean pool_useInv = false;
//	
//	/**
//	 * 接触类型通过判断两个对象的类型在contact初始化时确定
//	 */
//	private int contactType = 0;
	
	private int stateFlag;
	/**contact状态标志*/
	private boolean contacted;
	private boolean contacting;
	/**接触*/
	public PhysicsObject o1;
	public PhysicsObject o2;
	/***/
	public final ContactAttach nodeA = new ContactAttach();
	public final ContactAttach nodeB = new ContactAttach();
	/**用于分离的MTD向量*/
	public final Vector2 MTD;
	
	/**链表相关contact*/
	Contact next;
	Contact prev;
	
	/**列表使用的next指针 */
	public Contact list_next;
	/**列表使用的prev指针 */
	public Contact list_prev;
	
	public final ContactCollisionData contactData = new ContactCollisionData();

	private static final AABB poolAABB = new AABB();
	
	//TODO 构建contact缓存
	private static final ArrayList<Object> stacks = new ArrayList<Object>();
	/*
	 * 缓存对象结构：
	 * shape1, shape2, Float(mtd x), Float(mtd y) 
	 * 
	 * shape 暂时只能这样了，提供一个shape，但是不能判断shape之前状态
	 * 现在需要在报告中添加shape
	 */
	static final void pushToTemp(Shape s1, Shape s2, float mtdX, float mtdY) {
		stacks.add(s1);
		stacks.add(s2);
		stacks.add(mtdX);
		stacks.add(mtdY);
	}
	
	
	
	Contact(PhysicsObject o1, PhysicsObject o2) {
		MTD = new Vector2();
		this.o1 = o1;
		this.o2 = o2;
	}

	public Contact() {
		MTD = new Vector2();
	}
	
	
	/**设置contact的contacted状态
	 * @param contacted <code>true</code> 前一step已发生碰撞
	 * <code>false</code> 前一step未发生碰撞*/
	public final void setContacted(boolean contacted){
		this.contacted=contacted;
	}
	
	/**获取contact的contacted状态
	 * @return<code>true</code> 前一step已发生碰撞
	 * <code>false</code> 前一step未发生碰撞*/
	public final boolean isContacted(){
		return this.contacted;
	}
	
	/**设置cintact的contacting状态
	 * @param contacting <code>true</code> step中发生碰撞
	 * <code>false</code>step中未发生碰撞*/
	public final void setContacting(boolean contacting){
		this.contacting=contacting;
	}
	
	/**获取cintact的contacting状态
	 * @return <code>true</code> step中发生碰撞
	 * <code>false</code>step中未发生碰撞*/
	public final boolean isContacting(){
		return this.contacting;
	}
	
	public final void setRemove(){
		stateFlag|=Contact.FLAG_CONTACT_REMOVE;
	}
	
	public final void clearRemove(){
		stateFlag&=~Contact.FLAG_CONTACT_REMOVE;
	}
	
	public final boolean isWillRemove(){
		return (stateFlag&Contact.FLAG_CONTACT_REMOVE)==Contact.FLAG_CONTACT_REMOVE;
	}
	
	public final void setAABBTest(){
		stateFlag |= Contact.FLAG_AABB_TEST;
	}
	
	public final void clearAABBTest(){
		stateFlag &= ~Contact.FLAG_AABB_TEST;
	}
	
	public final boolean isAABBTest(){
		return (stateFlag&Contact.FLAG_AABB_TEST) == Contact.FLAG_AABB_TEST;
	}
	
	public final void clearFlag() {
		this.stateFlag = 0;
	}
	
	public final PhysicsObject getPhysicsObject1(){
		return o1;
	}
	
	public final PhysicsObject getPhysicsObject2(){
		return o2;
	}
	
	public final void setPhysicsObject(final PhysicsObject o1,final PhysicsObject o2){
		this.o1 = o1;
		this.o2 = o2;
	}
	
	public final void swap(){
		final PhysicsObject temp = o1;
		o2 = o1;
		o2 = temp;
	}

	

	/**清除推进标志*/
	public final void clearAdvanceFlag(){
		o1.clearAdvanceFlag();
		o2.clearAdvanceFlag();
	}
	
	/**
	 * public static final int ID_TILE = 2;
	public static final int ID_AABB = 3;
	public static final int ID_POLYGON = 5;
	public static final int ID_CIRCLE = 7;
	 * 2D空间的形状检测
	 * @param s1
	 * @param s2
	 */
	private void testShape(Shape s1, Shape s2) {
		int shapesID = 0;
		
		while(s1 != null){
//			s2 = o2.getShapeList();
			while(s2 != null){
				//处理形状相交
				shapesID = s1.shapeTypeID * s2.shapeTypeID;
				
				if(o1.isSensor()||o2.isSensor()||
						o1.getContactFilter().testPass(o2.getContactFilter())) {
					//仅测试碰撞不修正
					switch(shapesID) {
					case 25:
						PlaygonTestPolygon(s1, s2, o1.getPosition(), o2.getPosition());
						break;
					case 9:
						AABBTestAABB(s1, s2, o1.getPosition(), o2.getPosition());
						break;
					case 15:
						PolygonTestAABB(s1, s2, o1.getPosition(), o2.getPosition());
						break;
					case 49:
						CircleTestCircle(s1, s2, o1.getPosition(), o2.getPosition());
						break;
					case 21:
						CircleTestAABB(s1, s2, o1.getPosition(), o2.getPosition());
						break;
					case 35:
						CircleTestPolygon(s1, s2, o1.getPosition(), o2.getPosition());
						break;
					case 6:
						TileTestAABB(s1, s2, o1, o2);
						break;
					case 10:
						TileTestPolygon(s1, s2, o1, o2);
						break;
					case 14:
						break;
					}
				}else{
					//碰撞并修正位置
					switch(shapesID) {
					case 25:
						PlaygonTestPolygonMTD(s1, s2, o1.getPosition(), o2.getPosition());
						break;
					case 9:
						AABBTestAABBMTD(s1, s2, o1.getPosition(), o2.getPosition());
						break;
					case 15:
						PolygonTestAABBMTD(s1, s2, o1.getPosition(), o2.getPosition());
						break;
					case 49:
						CircleTestCircleMTD(s1, s2, o1.getPosition(), o2.getPosition());
						break;
					case 21:
						CircleTestAABBMTD(s1, s2, o1.getPosition(), o2.getPosition());
						break;
					case 35:
						CircleTestPolygonMTD(s1, s2, o1.getPosition(), o2.getPosition());
						break;
					case 6:
						TileTestAABBMTD(s1, s2, o1, o2);
						break;
					case 10:
						TileTestPolygonMTD(s1, s2, o1, o2);
						break;
					}
				}
				s2 = s2.next;
			}
			s1 = s1.next;
		}
	}
	
	
	/**处理接触<br>
	 * final为true是最后一次迭代
	 * @param finalTest 
	 * @param time
	 * @param listener */
	public final void handle(boolean finalTest,final TimeInfo time,
			final ContactListener listener) {
		//迭代物体位置	防止同一时间迭代多次物体
		if(!o1.checkAdvanceFlag()) {
			o1.advance(time);
			o1.setAdvanceFlag();
		}
		if(!o2.checkAdvanceFlag()){
			o2.advance(time);
			o2.setAdvanceFlag();
		}
		
		contactData.retFriction = o1.friction * o2.friction;
		contactData.retStaticFriction = o1.staticFriction * o2.staticFriction;
		
		//随时取消碰撞
		if(listener.cancelContact(o1, o2)) {
			return;
		}

		testShape(o1.getShapeList(), o2.getShapeList());
		
		//在最后迭代时处理contactListener
		if(finalTest){
			if(!contacted && contacting){
				if(stacks.isEmpty()) {
					listener.contactCreated(o1, o2, MTD, contactData);
				}
				else {
					for(int i = 0, n = stacks.size(); i < n; i+=4) {
						Shape s1 = (Shape)stacks.get(i);
						Shape s2 = (Shape)stacks.get(i+1);
						float mtd_x = (float) stacks.get(i+2);
						float mtd_y = (float) stacks.get(i+3);
						contactData.shape1 = s1;
						contactData.shape2 = s2;
						listener.contactCreated(o1, o2, MTD.set(mtd_x, mtd_y), contactData);
					}
					stacks.clear();
				}
			}
			else if(contacted && contacting){
				if(stacks.isEmpty()) {
					listener.contactPersisted(o1, o2, MTD, contactData);
				}
				else {
					for(int i = 0, n = stacks.size(); i < n; i+=4) {
						Shape s1 = (Shape)stacks.get(i);
						Shape s2 = (Shape)stacks.get(i+1);
						float mtd_x = (float) stacks.get(i+2);
						float mtd_y = (float) stacks.get(i+3);
						contactData.shape1 = s1;
						contactData.shape2 = s2;
//						System.out.println("s1 = " + s1.row + " " + s1.col);
//						System.out.println("s2 = " + s2.row + " " + s2.col);
						listener.contactPersisted(o1, o2, MTD.set(mtd_x, mtd_y), contactData);
					}
					stacks.clear();
				}
			}
			else if(contacted && !contacting){
				if(stacks.isEmpty()) {
					listener.contactDestroyed(o1, o2, MTD, contactData);
				}
				else {
					for(int i = 0, n = stacks.size(); i < n; i+=4) {
						Shape s1 = (Shape)stacks.get(i);
						Shape s2 = (Shape)stacks.get(i+1);
						float mtd_x = (float) stacks.get(i+2);
						float mtd_y = (float) stacks.get(i+3);
						contactData.shape1 = s1;
						contactData.shape2 = s2;
						listener.contactDestroyed(o1, o2, MTD.set(mtd_x, mtd_y), contactData);
					}
					stacks.clear();
				}
			}
			
			contacted = contacting;
			contacting = false;
			
			//调用监听器
			o1.listener.onUpdatePosition(o1);
			o2.listener.onUpdatePosition(o2);
			
		}
	}
	
	/*
	 * 相交处理：设置contacting标志位为true
	 * 不相交处理：设置contacting标志位false（不处理）
	 * 在handle最后判断：
	 * contacted为true，contacting为false 表示接触结束
	 * contacted为false，contacting为true 表示接触开始
	 * contacted为true，contacting为true 表示接触持续
	 * 最后将contacting值付给contacted
	 * contacting设置为false
	 */
	
	/**处理碰撞结果*/
	private final void handleResult(){
//		System.out.println("o1 = " + o1.getPosition() + "o1 is" + o1);
//		System.out.println("o2 = " + o2.getPosition() + "o2 is" + o2);
//		System.out.println("handleResult:: MTD = " + MTD);
		if(o1.isStaticObject()||o2.isStaticObject()){
//			System.out.println("handleResult:: MTD = " + MTD);
			o1.modifierPosition(pool1.set(MTD.x, MTD.y), contactData);
			o2.modifierPosition(pool1.set(-MTD.x, -MTD.y), contactData);
//			System.out.println("o1 = " + o1.getPosition());
//			System.out.println("o2 = " + o2.getPosition());
		}
		else {
			final int l1 = o1.getContactLevel();
			final int l2 = o2.getContactLevel();
			if(l1 == l2) {
				o1.modifierPosition(pool1.set(.5f*MTD.x, .5f*MTD.y), contactData);
				o2.modifierPosition(pool1.set(.5f*-MTD.x, .5f*-MTD.y), contactData);
			}
			else if(l1 < l2) {
				o1.modifierPosition(pool1.set(MTD.x, MTD.y), contactData);
				o2.modifierPosition(pool1.set(0,0), contactData);
			}
			else {
				o1.modifierPosition(pool1.set(0,0), contactData);
				o2.modifierPosition(pool1.set(-MTD.x, -MTD.y), contactData);
			}
		}	
	}
	
	private final void handleResultInv() {
		if(o1.isStaticObject()||o2.isStaticObject()){
			o1.modifierPosition(pool1.set(-MTD.x, -MTD.y), contactData);
			o2.modifierPosition(pool1.set(MTD.x, MTD.y), contactData);
		}
		else {
			final int l1=o1.getContactLevel();
			final int l2=o2.getContactLevel();
			if(l1 == l2){
				o1.modifierPosition(pool1.set(.5f*-MTD.x, .5f*-MTD.y), contactData);
				o2.modifierPosition(pool1.set(.5f*MTD.x, .5f*MTD.y), contactData);
			}
			else if(l1 < l2){
				o1.modifierPosition(pool1.set(-MTD.x, -MTD.y), contactData);
				o2.modifierPosition(pool1.set(0,0), contactData);
			}
			else{
				o1.modifierPosition(pool1.set(0,0), contactData);
				o2.modifierPosition(pool1.set(MTD.x, MTD.y), contactData);
			}
		}	
	}
	
	/**检测两个多边形的相交
	 * @param s1
	 * @param s2
	 * @param positionA
	 * @param positionB */
	private final void PlaygonTestPolygon(final Shape s1,final Shape s2,
			final Vector2 positionA, final Vector2 positionB){
		if(Intersect((Polygon)s1, (Polygon)s2, positionA, positionB)){
			contacting = true;
		}
	}
	
	/**检测两个多边形相交并计算MTD
	 * @param o1
	 * @param o2 */
	private final void PlaygonTestPolygonMTD(final Shape s1,final Shape s2, 
			final Vector2 positionA, final Vector2 positionB){
		if(Intersect((Polygon)s1, (Polygon)s2, positionA, positionB, MTD)){
			contacting = true;
			handleResult();
		}
	}

	private final void AABBTestAABB(final Shape s1,final Shape s2,
			final Vector2 positionA,final Vector2 positionB){
		if(Intersect((AABBShape)s1, (AABBShape)s2, positionA, positionB)){
			contacting = true;
		}
	}
	
	private final void AABBTestAABBMTD(final Shape s1,final Shape s2,
			final Vector2 positionA, final Vector2 positionB){
		if(Intersect((AABBShape)s1, (AABBShape)s2, positionA, positionB, MTD)){
			contacting = true;
			handleResult();
		}
	}
	
	/**如果不涉及AABB修正可以不管AABB与polygon的顺序
	 * @param o1
	 * @param o2  */
	private final void PolygonTestAABB(final Shape s1,final Shape s2,
			final Vector2 positionA, final Vector2 positionB){
		if(s1.shapeTypeID == Shape.ID_POLYGON){
			if(Intersect((Polygon)s1, (AABBShape)s2, positionA, positionB)){
				contacting = true;
			}
		}else{
			if(Intersect((Polygon)s2, (AABBShape)s1 ,positionB, positionA)){
				contacting = true;
			}
		}
	}
	
	/**多边形与AABB形状的MTD碰撞
	 * @param s1
	 * @param s2 
	 * @param positionA
	 * @param positionB */
	private final void PolygonTestAABBMTD(final Shape s1,final Shape s2,
			final Vector2 positionA, final Vector2 positionB){
		//判断两个形状相交
		if(s1.shapeTypeID == Shape.ID_POLYGON){
			if(Intersect((Polygon)s1, (AABBShape)s2, positionA, positionB, MTD)){
				contacting = true;
				handleResult();
			}
		} else {
			if(Intersect((Polygon)s2,(AABBShape)s1, positionB, positionA, MTD)){
				contacting = true;
				this.handleResultInv();
			}
		}
	}
	
	/**仅检测圆与圆的重叠
	 * @param s1
	 * @param s2
	 * @param positionA
	 * @param positionB */
	private final void CircleTestCircle(final Shape s1,final Shape s2,
			final Vector2 positionA, final Vector2 positionB){
		if(Intersect((Circle)s1, (Circle)s2, positionA, positionB)){
			contacting = true;
		}
	}
	
	/**检测圆与圆的碰撞并修正
	 * @param s1
	 * @param s2
	 * @param positionA
	 * @param positionB */
	private final void CircleTestCircleMTD(final Shape s1,final Shape s2,
			final Vector2 positionA, final Vector2 positionB){
		if(Intersect((Circle)s1, (Circle)s2, positionA, positionB, MTD)){
			contacting = true;
			this.handleResult();
		}
	}
	
	private final void CircleTestAABBMTD(final Shape s1, final Shape s2, final Vector2 positionA, final Vector2 positionB) {
		if(s1.shapeTypeID == Shape.ID_CIRCLE){
			if(Intersect((Circle)s1, (AABBShape)s2, positionA, positionB, MTD)){
				contacting = true;
				this.handleResult();	
			}
		}
		else{
			if(Intersect((Circle)s2, (AABBShape)s1, positionB, positionA, MTD)){
				contacting = true;
				this.handleResultInv();
			}
		}
	}

	private final void CircleTestAABB(final Shape s1, final Shape s2, final Vector2 positionA, final Vector2 positionB) {
		if(s1.shapeTypeID == Shape.ID_CIRCLE){
			if(Intersect((Circle)s1, (AABBShape)s2, positionA, positionB)){
				contacting = true;
			}
		}
		else{
			if(Intersect((Circle)s2, (AABBShape)s1, positionB, positionA)){
				contacting = true;
			}
		}
	}
	
	/**这个方法还有待验证
	 * @param s1
	 * @param s2
	 * @param positionA
	 * @param positionB
	 */
	private final void CircleTestPolygon(final Shape s1, final Shape s2, final Vector2 positionA, final Vector2 positionB) {
		if(s1.shapeTypeID == Shape.ID_CIRCLE){
			if(Intersect((Circle)s1, (Polygon)s2, positionA, positionB)){
				contacting = true;
			}
		}
		else{
			if(Intersect((Circle)s2, (Polygon)s1, positionB, positionA)){
				contacting = true;
			}
		}
	}

	private final void CircleTestPolygonMTD(final Shape s1, final Shape s2, final Vector2 positionA,
			final Vector2 positionB) {
		if(s1.shapeTypeID == Shape.ID_CIRCLE){
			if(Intersect((Circle)s1, (Polygon)s2, positionA, positionB, MTD)){
				contacting = true;
				this.handleResult();	
			}
		}
		else{
			if(Intersect((Circle)s2, (Polygon)s1, positionB, positionA, MTD)){
				contacting = true;
				this.handleResultInv();
			}
		}
	}
	
	/**AABB 和 tile类型的碰撞检测 */
	private final boolean TileTestAABBMTD(final Shape s1, final Shape s2, final PhysicsObject o1,
			final PhysicsObject o2) {
//		contactData.s
		final Vector2 positionA = o1.getPosition();
		final Vector2 positionB = o2.getPosition();
//		System.out.println("tile Test AABB ------------------- ");
		
		if(s1.shapeTypeID == Shape.ID_TILE){
			TileShape tileShape = (TileShape) s1;
			//pool2 = positionB - positionA
			pool2.set(positionB); pool2.sub(positionA);
			//poolAABB = shape2AABB + pool2 
			poolAABB.set(o2.getShapeAABB()); poolAABB.lowerBound.add(pool2); poolAABB.upperBound.add(pool2);
				
//			System.out.println("testAABB = " + poolAABB);
				
			//计算出范围
			tileShape.getMinMaxCol(poolAABB, pool2);
//			System.out.println("cols = " + pool2);
			final int minCol = (int) pool2.x;
			final int maxCol = (int) pool2.y;
			tileShape.getMinMaxRow(poolAABB, pool2);
//			System.out.println("rows = " + pool2);
			final int minRow = (int) pool2.x;
			final int maxRow = (int) pool2.y;
				
			for(int col = minCol; col <= maxCol; ++col) {
				for(int row = minRow; row <= maxRow; ++row) {
//					System.out.println("cols = " + col + "rows = " + row);
					final Shape shape = tileShape.getShape(row, col);
					if(shape == null) continue;
						
					//TODO tile暂时全部都是AABB
					AABBShape aabb = (AABBShape) shape;
					pool2.set(positionA).add(aabb.colX, aabb.rowY);
					if(Intersect(aabb, (AABBShape)s2, pool2, positionB, MTD)){
						pushToTemp(aabb, s2, MTD.x, MTD.y);
						contacting = true;
						handleResult();
					}
//					AABBTestAABBMTD(aabb, s2, pool2, positionB);
					
//					switch(s2.shapeTypeID) {
//					case Shape.ID_AABB:
//						AABBTestAABBMTD(aabb, s2, positionA, positionB);
//						break;
//					case Shape.ID_POLYGON:
//						PolygonTestAABBMTD(s2, aabb, positionB, positionA);
//						break;
//					case Shape.ID_CIRCLE:
//						CircleTestAABB(s2, aabb, positionB, positionA);
//						break;
//					}
				}
			}
		}
		else {
			TileShape tileShape = (TileShape) s2;
			//pool2 = positionA - positionB
			pool2.set(positionA); pool2.sub(positionB);
			//poolAABB = shape2AABB + pool2 
			poolAABB.set(o1.getShapeAABB()); poolAABB.lowerBound.add(pool2); poolAABB.upperBound.add(pool2);			
			
			//计算出范围
			tileShape.getMinMaxCol(poolAABB, pool2);
//			System.out.println("cols = " + pool2);
			final int minCol = (int) pool2.x;
			final int maxCol = (int) pool2.y;
			tileShape.getMinMaxRow(poolAABB, pool2);
//			System.out.println("rows = " + pool2);
			final int minRow = (int) pool2.x;
			final int maxRow = (int) pool2.y;
			
			for(int col = minCol; col <= maxCol; ++col) {
				for(int row = minRow; row <= maxRow; ++row) {
//					System.out.println("cols = " + col + "rows = " + row);
					final Shape shape = tileShape.getShape(row, col);
					if(shape == null) continue;
					
					//TODO tile暂时全部都是AABB
					AABBShape aabb = (AABBShape) shape;
					pool2.set(positionB);
					pool2.add(aabb.colX, aabb.rowY);
					if(Intersect(aabb, (AABBShape)s2, pool2, positionB, MTD)){
						pushToTemp(s1, aabb, MTD.x, MTD.y);
						contacting = true;
						handleResult();
					}
//					AABBTestAABBMTD(s1, aabb, positionA, pool2);
				}
			}
		}
		return false;
	}
	
	private boolean TileTestPolygonMTD(final Shape s1, final Shape s2, final PhysicsObject o1,
			final PhysicsObject o2) {
		final Vector2 positionA = o1.getPosition();
		final Vector2 positionB = o2.getPosition();
		
		if(s1.shapeTypeID == Shape.ID_TILE){
			TileShape tileShape = (TileShape) s1;
			//pool2 = positionB - positionA
			pool2.set(positionB); pool2.sub(positionA);
			//poolAABB = shape2AABB + pool2 
			poolAABB.set(o2.getShapeAABB()); poolAABB.lowerBound.add(pool2); poolAABB.upperBound.add(pool2);
				
//			System.out.println("testAABB = " + poolAABB);
				
			//计算出范围
			tileShape.getMinMaxCol(poolAABB, pool2);
//			System.out.println("cols = " + pool2);
			final int minCol = (int) pool2.x;
			final int maxCol = (int) pool2.y;
			tileShape.getMinMaxRow(poolAABB, pool2);
//			System.out.println("rows = " + pool2);
			final int minRow = (int) pool2.x;
			final int maxRow = (int) pool2.y;
				
			for(int col = minCol; col <= maxCol; ++col) {
				for(int row = minRow; row <= maxRow; ++row) {
//					System.out.println("cols = " + col + "rows = " + row);
					final Shape shape = tileShape.getShape(row, col);
					if(shape == null) continue;
						
					//TODO tile暂时全部都是AABB
					AABBShape aabb = (AABBShape) shape;
					pool2.set(positionA).add(aabb.colX, aabb.rowY);
					if(Intersect((Polygon)s2, aabb, positionB, pool2, MTD)){
						pushToTemp(aabb, s2, MTD.x, MTD.y);
						contacting = true;
						handleResultInv();
					}
//					PolygonTestAABBMTD(aabb, s2, pool2, positionB);
				}
			}
		}
		else {
			TileShape tileShape = (TileShape) s2;
			//pool2 = positionA - positionB
			pool2.set(positionA); pool2.sub(positionB);
			//poolAABB = shape2AABB + pool2 
			poolAABB.set(o1.getShapeAABB()); poolAABB.lowerBound.add(pool2); poolAABB.upperBound.add(pool2);			
			
			//计算出范围
			tileShape.getMinMaxCol(poolAABB, pool2);
//			System.out.println("cols = " + pool2);
			final int minCol = (int) pool2.x;
			final int maxCol = (int) pool2.y;
			tileShape.getMinMaxRow(poolAABB, pool2);
//			System.out.println("rows = " + pool2);
			final int minRow = (int) pool2.x;
			final int maxRow = (int) pool2.y;
			
			for(int col = minCol; col <= maxCol; ++col) {
				for(int row = minRow; row <= maxRow; ++row) {
//					System.out.println("cols = " + col + "rows = " + row);
					final Shape shape = tileShape.getShape(row, col);
					if(shape == null) continue;
					
					//TODO tile暂时全部都是AABB
					AABBShape aabb = (AABBShape) shape;
					pool2.set(positionB).add(aabb.colX, aabb.rowY);
					if(Intersect((Polygon)s1, aabb, positionA, pool2, MTD)){
						pushToTemp(s1, aabb, MTD.x, MTD.y);
						contacting = true;
						handleResult();
					}
//					PolygonTestAABBMTD(s1, aabb, positionA, pool2);
				}
			}
		}
		return false;		
	}
	
	private final boolean TileTestAABB(final Shape s1, final Shape s2, final PhysicsObject o1,
			final PhysicsObject o2) {
		final Vector2 positionA = o1.getPosition();
		final Vector2 positionB = o2.getPosition();
		
		if(s1.shapeTypeID == Shape.ID_TILE){
			TileShape tileShape = (TileShape) s1;
			//pool2 = positionB - positionA
			pool2.set(positionB); pool2.sub(positionA);
			//poolAABB = shape2AABB + pool2 
			poolAABB.set(o2.getShapeAABB()); poolAABB.lowerBound.add(pool2); poolAABB.upperBound.add(pool2);
				
			//计算出范围
			tileShape.getMinMaxCol(poolAABB, pool2);
//			System.out.println("cols = " + pool2);
			final int minCol = (int) pool2.x;
			final int maxCol = (int) pool2.y;
			tileShape.getMinMaxRow(poolAABB, pool2);
//			System.out.println("rows = " + pool2);
			final int minRow = (int) pool2.x;
			final int maxRow = (int) pool2.y;
				
			for(int col = minCol; col <= maxCol; ++col) {
				for(int row = minRow; row <= maxRow; ++row) {
					final Shape shape = tileShape.getShape(row, col);
					if(shape == null) continue;
						
					//TODO tile暂时全部都是AABB
					AABBShape aabb = (AABBShape) shape;
					pool2.set(positionA).add(aabb.colX, aabb.rowY);
//					AABBTestAABB(aabb, s2, pool2, positionB);
					if(Intersect(aabb, (AABBShape)s2, pool2, positionB)){
						pushToTemp(aabb, s2, 0, 0);
						contacting = true;
					}
				}
			}
		}
		else {
			TileShape tileShape = (TileShape) s2;
			pool2.set(positionA); pool2.sub(positionB);
			poolAABB.set(o1.getShapeAABB()); poolAABB.lowerBound.add(pool2); poolAABB.upperBound.add(pool2);			
			
			//计算出范围
			tileShape.getMinMaxCol(poolAABB, pool2);
			final int minCol = (int) pool2.x;
			final int maxCol = (int) pool2.y;
			tileShape.getMinMaxRow(poolAABB, pool2);
			final int minRow = (int) pool2.x;
			final int maxRow = (int) pool2.y;
			
			for(int col = minCol; col <= maxCol; ++col) {
				for(int row = minRow; row <= maxRow; ++row) {
					final Shape shape = tileShape.getShape(row, col);
					if(shape == null) continue;
					
					//TODO tile暂时全部都是AABB
					AABBShape aabb = (AABBShape) shape;
					pool2.set(positionB).add(aabb.colX, aabb.rowY);
//					AABBTestAABB(s1, aabb, positionA, pool2);
					if(Intersect((AABBShape)s1, aabb, positionA, pool2)){
						pushToTemp(s1, aabb, 0, 0);
						contacting = true;
					}
				}
			}
		}
		return false;
	}
	
	private boolean TileTestPolygon(final Shape s1, final Shape s2, final PhysicsObject o1,
			final PhysicsObject o2) {
		final Vector2 positionA = o1.getPosition();
		final Vector2 positionB = o2.getPosition();
		
		if(s1.shapeTypeID == Shape.ID_TILE){
			TileShape tileShape = (TileShape) s1;
			//pool2 = positionB - positionA
			pool2.set(positionB); pool2.sub(positionA);
			//poolAABB = shape2AABB + pool2 
			poolAABB.set(o2.getShapeAABB()); poolAABB.lowerBound.add(pool2); poolAABB.upperBound.add(pool2);
								
			//计算出范围
			tileShape.getMinMaxCol(poolAABB, pool2);
			final int minCol = (int) pool2.x;
			final int maxCol = (int) pool2.y;
			tileShape.getMinMaxRow(poolAABB, pool2);
			final int minRow = (int) pool2.x;
			final int maxRow = (int) pool2.y;
				
			for(int col = minCol; col <= maxCol; ++col) {
				for(int row = minRow; row <= maxRow; ++row) {
					final Shape shape = tileShape.getShape(row, col);
					if(shape == null) continue;
						
					//TODO tile暂时全部都是AABB
					AABBShape aabb = (AABBShape) shape;
					pool2.set(positionA).add(aabb.colX, aabb.rowY);
//					PolygonTestAABB(aabb, s2, pool2, positionB);
					if(Intersect((Polygon)s2, aabb, positionB, pool2)){
						pushToTemp(aabb, s2, 0, 0);
						contacting = true;
					}
				}
			}
		}
		else {
			TileShape tileShape = (TileShape) s2;
			//pool2 = positionA - positionB
			pool2.set(positionA); pool2.sub(positionB);
			//poolAABB = shape2AABB + pool2 
			poolAABB.set(o1.getShapeAABB()); poolAABB.lowerBound.add(pool2); poolAABB.upperBound.add(pool2);			
			
			//计算出范围
			tileShape.getMinMaxCol(poolAABB, pool2);
			final int minCol = (int) pool2.x;
			final int maxCol = (int) pool2.y;
			tileShape.getMinMaxRow(poolAABB, pool2);
			final int minRow = (int) pool2.x;
			final int maxRow = (int) pool2.y;
			
			for(int col = minCol; col <= maxCol; ++col) {
				for(int row = minRow; row <= maxRow; ++row) {
					final Shape shape = tileShape.getShape(row, col);
					if(shape == null) continue;
					
					//TODO tile暂时全部都是AABB
					AABBShape aabb = (AABBShape) shape;
					pool2.set(positionB).add(aabb.colX, aabb.rowY);
//					PolygonTestAABB(s1, aabb, positionA, pool2);
					if(Intersect((Polygon)s2, aabb, positionA, pool2)){
						pushToTemp(s1, aabb, 0, 0);
						contacting = true;
					}
				}
			}
		}
		return false;		
	}
}