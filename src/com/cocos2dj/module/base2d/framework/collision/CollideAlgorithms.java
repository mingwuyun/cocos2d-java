package com.cocos2dj.module.base2d.framework.collision;

import com.badlogic.gdx.math.Vector2;
import com.cocos2dj.module.base2d.framework.common.MathUtils;
import com.cocos2dj.module.base2d.framework.common.V2;

/**碰撞检测算法<p>
 * 
 * 修改了FloatPair的实现从new改为了单例模式 ,改变了变量的获取方法。
 * #data:
 * 		2012.11.23 改变了AABB的检测代码采用本地坐标检测<br>
 * 入口的方法只有Intersect 其余方法均为辅助<p>
 * 
 * 新增了形状圆 <p>
 * @author xujun
 * Copyright (c) 2012-2013. All rights reserved. */
public final class CollideAlgorithms {
	
	/**最大边的数量*/
	private static final int MAX_VERTICES = 12;

	private static final Vector2[] poolVector2Array;
	private static final Vector2[] poolVector2Interval;
	
	private static FloatPair pair = null;
	/**轴X单位向量 使用后应初始化*/
	public static final Vector2 axisX = new Vector2(1,0);
	/**轴Y单位向量 使用后应初始化*/
	public static final Vector2 axisY = new Vector2(0,1);
	
	//pools 基本都用在圆与多边形的测试上
	private static final Vector2 pool1 = new Vector2();
	private static final Vector2 pool2 = new Vector2();
	private static final Vector2 pool3 = new Vector2();
	private static final Vector2 pool4 = new Vector2();
	private static final Vector2 pool5 = new Vector2();
	
	static
	{
		poolVector2Array = new Vector2[MAX_VERTICES];
		for(int i = 0; i < MAX_VERTICES; ++i){
			poolVector2Array[i] = new Vector2();
		}
		poolVector2Interval = new Vector2[MAX_VERTICES];
		for(int i = 0; i < MAX_VERTICES; ++i){
			poolVector2Interval[i] = new Vector2();
		}
	}
	
//	private static final Vector2 pool2=new Vector2();
	//private static final Vector2 pool3=new Vector2();
	
	
	//TODO 多边形之间的检测相关方法
	/**计算多边形p在axis轴上的投影并返回最大最小值
	 * 注意axis为单位向量
	 * @param axis 
	 * @param p
	 * @param position 形状p的位置
	 * @return FloatPair */
	public static final FloatPair CalculateInterval(final Vector2 axis, final Polygon p,
			final Vector2 position) {
		
		final Vector2[] points = p.getPoints();
//		a.x * b.x + a.y * b.y
//		float d = Vector2.dot(axis, points[0]);
		float d = axis.x * (points[0].x + position.x) + axis.y * (points[0].y + position.y);
		float min = d;
		float max = d;

	    final int length = points.length;
	    for(int i = 0; i < length; i++){
//	    	d=Vector2.dot(axis, points[i]);
	    	d = axis.x * (points[i].x + position.x) + axis.y * (points[i].y + position.y);
	    	if(d < min) {
	    		min = d;
	    	} else if(d > max) {
	    		max = d;
	    	}
		} 
	    return FloatPair.getFloatPair(min, max);
	}
	
	/**测试在Axis轴上的A、B是否分离<br>
	 * @param Axis
	 * @param A
	 * @param B
	 * @param positionA A的位置
	 * @param positionB B的位置
	 * @return <code>true</code> 分离
	 * <code>false</code> 未分离*/
	public static final boolean axisSeparatePolygons(final Vector2 Axis, final Polygon A, final Polygon B,
			final Vector2 positionA, final Vector2 positionB) {
		float mina, maxa;
		float minb, maxb;
		
		pair=CalculateInterval(Axis, A, positionA);
		mina=pair.min;
		maxa=pair.max;
		
		pair=CalculateInterval(Axis, B, positionB); 
		minb=pair.min;
		maxb=pair.max;
		
		if (mina > maxb || minb > maxa) 
			return true; 
		// find the interval overlap
//		float d0 = maxa - minb; 
//		float d1 = maxb - mina; 
//		float depth = (d0 < d1)? d0 : d1;
		//修剪轴
		// convert the separation axis into a push vector (re-normalise
		// the axis and multiply by interval overlap)
//		float axisLengthSquared = Vector2.dot(Axis, Axis);
//		Axis.mulThis(depth / axisLengthSquared);
		return false; 
	}
	
	/**检测两个多边形的相交
	 * @param A
	 * @param B
	 * @return <code>true</code> 相交
	 * <code>false</code> 不相交*/
	public static final boolean Intersect(final Polygon A, final Polygon B,
			final Vector2 positionA, final Vector2 positionB){ 
		for(int i=A.getPoints().length-1; i>=0;i--){
			pool1.set(-A.getPoints()[i].y, A.getPoints()[i].x);
			if (axisSeparatePolygons(pool1, A, B, positionA, positionB))
				return false;
		}
		
		for(int i=B.getPoints().length-1; i>=0;i--){
			pool1.set(-B.getPoints()[i].y, B.getPoints()[i].x);
			if (axisSeparatePolygons(pool1, A, B, positionA, positionB))
				return false;
		}
		return true;
	} 
	
	/**测试在Axis轴上的A、B是否分离  并 将轴用于MTD测试
	 * @param Axis
	 * @param A
	 * @param B
	 * @param positionA
	 * @param positionB
	 * @return <code>true</code> 分离
	 * <code>false</code> 未分离 */
	public static final boolean axisSeparatePolygonsMTD(final Vector2 Axis, final Polygon A, final Polygon B,
			final Vector2 positionA, final Vector2 positionB) {
		pair=CalculateInterval(Axis, A, positionA);
		final float mina=pair.min;
		final float maxa=pair.max;
		
		pair=CalculateInterval(Axis, B, positionB); 
		final float minb=pair.min;
		final float maxb=pair.max;
		
		if (mina > maxb || minb > maxa) 
			return true; 
		
		 // find the interval overlap 
		final float d0 = maxa - minb; 
		final float d1 = maxb - mina; 
		final float depth = (d0 < d1)? d0 : d1;
		
		// convert the separation axis into a push vector (re-normalise
		// the axis and multiply by interval overlap) 
		float axisLengthSquared = V2.dot(Axis, Axis);
		Axis.scl(depth / axisLengthSquared);
		return false; 
	}
	
	/** 搜寻MTD<br>
	 * 从所给推进向量中求出MTD
	 * @param pushVectors
	 * @param numVectors
	 * @return MTD*/
	public static final Vector2 FindMTD(final Vector2[] pushVectors, final int numVectors) {
		Vector2 MTD = pushVectors[0]; 
		float mind2 = V2.dot(pushVectors[0],pushVectors[0]);
		for(int i=1; i<numVectors; ++i) {
			float d2 = V2.dot(pushVectors[i], pushVectors[i]);
			if (d2 < mind2) {
				mind2 = d2; 
				MTD = pushVectors[i]; 
			}
		}
		return MTD; 
	}
	
	/**相交测试并求出MTD向量   结果存放在参数MTD中
	 * 沿MTD向量就可以讲物体分开
	 * @param A
	 * @param B
	 * @param MTD
	 * @return <code>true</code> 相交
	 * <code>false</code> 不相交 */
	public static final boolean Intersect(final Polygon A, final Polygon B, 
			final Vector2 positionA, final Vector2 positionB, final Vector2 MTD) { 
		Vector2[] axis=poolVector2Array;   
		// max of 10 vertices per polygon
		Vector2 tempE=pool1;
		Vector2 tempN=null;
		final Vector2[] pointsA=A.getPoints();
		final Vector2[] pointsB=B.getPoints();
		
		int numAxis = 0; 
		
		//用这个不用单独求0的情况
		for(int j=pointsA.length-1, i=0; i<pointsA.length;
				j=i, ++i) {
			tempE.set(pointsA[i].x-pointsA[j].x,pointsA[i].y-pointsA[j].y);
			tempN=axis[numAxis++].set(-tempE.y, tempE.x);
			if (axisSeparatePolygonsMTD(tempN, A, B, positionA, positionB)) 
				return false; 
		}
		
		for(int j=pointsB.length-1, i=0; i<pointsB.length;
				j=i, ++i) {
			tempE.set(pointsB[i].x-pointsB[j].x,pointsB[i].y-pointsB[j].y);
			tempN=axis[numAxis++].set(-tempE.y, tempE.x);
			if (axisSeparatePolygonsMTD(tempN, A, B, positionA, positionB)) 
				return false; 
		}
		
		//从所有的分离向量中查找最近的作为MTD   
		MTD.set(FindMTD(axis, numAxis)); 
		
		// makes sure the push vector is pushing A away from B 
//		Vector2 D = posiyionA.sub(B.getPosition());
		pool1.set(positionA.x-positionB.x, positionA.y-positionB.y);
		if (V2.dot(pool1, MTD)<0) {
			V2.negate(MTD);
		}
		
		return true; 
	}
	
	//TODO AABB形状之间检测相关方法
	/**测试X轴向上的两个AABB是否分离	
	 * <br>true为分离  axis为x轴的单位向量 必须传入(1,0)<br>
	 * <b>使用两个pool</b>
	 * @param Axis
	 * @param A
	 * @param B
	 * @param positionA
	 * @param positionB
	 * @return*/
	public static final boolean axisSeparateAABBMTDX(final Vector2 axis,
			final AABBShape A, final AABBShape B,
			final Vector2 positionA, final Vector2 positionB) {
		
		final float mina=positionA.x+A.getPoints()[0].x;
		final float maxa=positionA.x+A.getPoints()[2].x;
		final float minb=positionB.x+B.getPoints()[0].x;
		final float maxb=positionB.x+B.getPoints()[2].x;

		
		if (mina > maxb || minb > maxa) 
			return true; 
		
		 // find the interval overlap 
		final float d0 = maxa - minb; 
		final float d1 = maxb - mina; 
		final float depth = (d0 < d1)? d0 : d1;
		// convert the separation axis into a push vector (re-normalise
		// the axis and multiply by interval overlap) 
		axis.scl(depth);
		return false; 
	}
	
	/**测试Y轴向上的两个AABB是否分离
	 * <br>true为分离
	 * axis为x轴的单位向量 必须传入(0,1)
	 * @param Axis
	 * @param A
	 * @param B
	 * @param positionB 
	 * @param positionA 
	 * @return */
	public static final boolean axisSeparateAABBMTDY(final Vector2 axis,final AABBShape A, final AABBShape B,
			final Vector2 positionA, final Vector2 positionB) {
		final Vector2[] pointsA=A.getPoints();
		final Vector2[] pointsB=B.getPoints();
		
		final float mina=positionA.y+pointsA[0].y;
		final float maxa=positionA.y+pointsA[2].y;
		final float minb=positionB.y+pointsB[0].y;
		final float maxb=positionB.y+pointsB[2].y;

		
		if (mina > maxb || minb > maxa) 
			return true; 
		
		 // find the interval overlap 
		final float d0 = maxa - minb; 
		final float d1 = maxb - mina; 
		final float depth = (d0 < d1)? d0 : d1;
		
		// convert the separation axis into a push vector (re-normalise
		// the axis and multiply by interval overlap) 
		axis.scl(depth);
		return false; 
	}
	
	/**相交测试并求出MTD向量
	 * 沿MTD向量就可以讲物体分开
	 * @param A				形状A
	 * @param B 			形状B
	 * @param positionA		形状A位置
	 * @param positionB		形状B位置
	 * @param MTD			MTD向量
	 * @return boolean true为相交false不相交*/
	public static final boolean Intersect(final AABBShape A,final AABBShape B,
			final Vector2 positionA, final Vector2 positionB, final Vector2 MTD){
		//初始化轴向量
		axisX.set(1, 0);
		axisY.set(0, 1);
		
		if(axisSeparateAABBMTDX(axisX, A, B, positionA, positionB)){
			return false;
		}
		if(axisSeparateAABBMTDY(axisY, A, B, positionA, positionB)){
			return false;
		}
		
		if(axisX.x>axisY.y){
			MTD.set(axisY);
		}else{
			MTD.set(axisX);
		}
		
//		System.out.println("MTD "+MTD);

		// makes sure the push vector is pushing A away from B 
//		pool1.set(positionA.x-positionB.x, positionA.y-positionB.y);
		pool1.set(positionA.x + A.centerX - positionB.x - B.centerX, positionA.y + A.centerY - positionB.y - B.centerY);
		if (V2.dot(pool1, MTD) < 0) {
			V2.negate(MTD);
		}
		return true;
	}
	
	/**相交测试测试两个AABB之间的相交
	 * @param A				形状A
	 * @param B 			形状B
	 * @param positionA		形状A位置
	 * @param positionB		形状B位置
	 * @return */
	public static final boolean Intersect(final AABBShape A,final AABBShape B,
			final Vector2 positionA, final Vector2 positionB){
		//向量A-向量B
//		final Vector2 v=A.getPosition().sub(B.getPosition());
//		final Vector2 v=positionA.sub(positionB);
		pool1.set(positionA.x-positionB.x, positionA.y-positionB.y);
		if (A.getPoints()[0].x+pool1.x > B.getPoints()[2].x|| B.getPoints()[0].x > pool1.x+A.getPoints()[2].x) 
			return false; 
		if (A.getPoints()[0].y+pool1.y> B.getPoints()[2].y || B.getPoints()[0].y > pool1.y+A.getPoints()[2].y) 
			return false; 
		
		return true;
	}
	
	
	
	//TODO AABB与多边形之间检测的相关方法
	/**相交测试并求出MTD向量
	 * 沿MTD向量就可以讲物体分开
	 * @param A
	 * @param B
	 * @param positionA
	 * @param positionB
	 * @param MTD
	 * @return <code>true</code> 形状相交 
	 * <code>false</code> 形状不相交*/
	public static final boolean Intersect(final Polygon A,final AABBShape B,
			final Vector2 positionA, final Vector2 positionB, final Vector2 MTD){
		axisX.set(1, 0);
		axisY.set(0, 1);
		
		final Vector2[] pointsA=A.getPoints();
		Vector2[] axis = poolVector2Array;   
		// max of 6 vertices per polygon
		Vector2 tempE = pool1;
		Vector2 tempN = null;
		int numAxis = 0; 
		
		//用这个不用单独求0的情况
		for(int j=pointsA.length-1, i=0; i<pointsA.length;
				j=i, i++) {
//			tempE=A.getPoints()[i].sub(A.getPoints()[j]); 
			tempE.set(pointsA[i].x-pointsA[j].x, pointsA[i].y-pointsA[j].y);
			tempN=axis[numAxis++].set(-tempE.y, tempE.x);
			if (axisSeparatePolygonsMTD(tempN, A, B, positionA, positionB)) {
//				System.out.println("B = " + B);
				return false; 
			}
		}

		//对x以及y轴的简化测试
		if (axisSeparatePolygonsMTDX(axisX, A, B, positionA, positionB)) {
			return false; 
		}
		if (axisSeparatePolygonsMTDY(axisY, A, B, positionA, positionB)) { 
			return false; 
		}
		
		axis[numAxis++].set(axisX);
		axis[numAxis++].set(axisY);
		
//		for(int i = 0; i < numAxis; ++i) {
//			System.out.println("axis["+i+"] " + axis[i]);
//		}
		
		//从所有的分离向量中查找最近的作为MTD   
		MTD.set(FindMTD(axis, numAxis)); 
		
//		System.out.println("MTD = " + MTD);
		
		// makes sure the push vector is pushing A away from B 
//		pool1.set(positionA.x - positionB.x, positionA.y - positionB.y);
		pool1.set(positionA.x + A.centerX - positionB.x - B.centerX, positionA.y + A.centerY - positionB.y - B.centerY);
//		positionA.sub(positionB);
		if (V2.dot(pool1, MTD)<0) {
			V2.negate(MTD);
		}
		return true;
	}
	
	/**相交测试 
	 * 多边形与AABB
	 * @param A
	 * @param B
	 * @param MTD
	 * @return  */
	public static final boolean Intersect(final Polygon A,final AABBShape B,
			final Vector2 positionA, final Vector2 positionB){
		axisX.set(1,0);
		axisY.set(0,1);
		Vector2 tempE=null;
		//用这个不用单独求0的情况
		for(int j=A.getPoints().length-1, i=0; i<A.getPoints().length;
				j=i, i++) {
			tempE=A.getPoints()[i].sub(A.getPoints()[j]); 
			if (axisSeparatePolygonsMTD(new Vector2(-tempE.y, tempE.x), A, B, positionA, positionB)) 
				return false; 
		}
		if (axisSeparatePolygonsMTDX(axisX, A, B, positionA, positionB)) 
			return false; 
		if (axisSeparatePolygonsMTDY(axisY, A, B, positionA, positionB)) 
			return false; 
		return true;
	}
	
	/**测试在Axis轴上的A、B是否分离
	 * 用于MTD测试的方法
	 * <br>true为分离
	 * @param Axis
	 * @param A
	 * @param B
	 * @return */
	public static final boolean axisSeparatePolygonsMTD(final Vector2 Axis, final Polygon A, final AABBShape B,
			final Vector2 positionA, final Vector2 positionB ) {
		pair=CalculateInterval(Axis, A, positionA);
		final float mina=pair.min;
		final float maxa=pair.max;
		
		pair=CalculateInterval(Axis, B, positionB);
		final float minb=pair.min;
		final float maxb=pair.max;
		
		if (mina > maxb || minb > maxa) 
			return true; 
		
		 // find the interval overlap 
		final float d0 = maxa - minb; 
		final float d1 = maxb - mina; 
		final float depth = (d0 < d1)? d0 : d1;
		
		// convert the separation axis into a push vector (re-normalise
		// the axis and multiply by interval overlap) 
		float axisLengthSquared = V2.dot(Axis, Axis);
		Axis.scl(depth / axisLengthSquared);
		return false; 
	}
	
	/**测试在X轴上的A、B是否分离
	 * 用于MTD测试的方法
	 * Axis必须为(1,0)
	 * @param Axis
	 * @param A
	 * @param B
	 * @return <code>true</code> A与B分离
	 * <code>false</code> A与B重合 */
	public static final boolean axisSeparatePolygonsMTDX(final Vector2 Axis, final Polygon A, final AABBShape B,
			final Vector2 positionA, final Vector2 positionB ) {
		pair=CalculateInterval(Axis, A, positionA);
		final float mina=pair.min;
		final float maxa=pair.max;
		
		final float minb=B.getPoints()[0].x+positionB.x;
		final float maxb=B.getPoints()[2].x+positionB.x;
		
		if (mina > maxb || minb > maxa) 
			return true; 
		
		 // find the interval overlap 
		final float d0 = maxa - minb; 
		final float d1 = maxb - mina; 
		final float depth = (d0 < d1)? d0 : d1;
		
		//直接求出即可
		Axis.scl(depth);
		return false; 
	}
	
	/**测试在Y轴上的A、B是否分离
	 * 用于MTD测试的方法
	 * Axis必须为(0,1)
	 * <br>true为分离
	 * @param Axis
	 * @param A
	 * @param B
	 * @return <code>true</code> A与B分离
	 * <code>false</code> A与B重合*/
	public static final boolean axisSeparatePolygonsMTDY(final Vector2 Axis, final Polygon A, final AABBShape B,
			final Vector2 positionA, final Vector2 positionB) {
		pair=CalculateInterval(Axis, A, positionA);
		final float mina=pair.min;
		final float maxa=pair.max;
		
		final float minb=B.getPoints()[0].y+positionB.y;
		final float maxb=B.getPoints()[2].y+positionB.y;
		
		if (mina > maxb || minb > maxa) 
			return true; 
		
		 // find the interval overlap 
		final float d0 = maxa - minb; 
		final float d1 = maxb - mina; 
		final float depth = (d0 < d1)? d0 : d1;

		Axis.scl(depth);
		return false; 
	}
	
	/**计算AABB p在axis轴上的投影并返回最大最小值
	 * 注意axis为单位向量
	 * @param axis 
	 * @param p
	 * @param position
	 * @return <code>true</code> A与B分离
	 * <code>false</code> A与B重合*/
	public static final FloatPair CalculateInterval(final Vector2 axis, final AABBShape p, final Vector2 position) {
		final Vector2[] points = p.getPoints();
		float d = axis.x * (points[0].x + position.x) + axis.y * (points[0].y + position.y);
		float min = d;
		float max = d;
		
	    for(int i = 0; i < 4; i++){
//	    	d=V2.dot(axis, temp[i]);
	    	d = axis.x * (points[i].x + position.x) + axis.y * (points[i].y + position.y);
	    	if(d < min) {
	    		min = d;
	    	} else if(d > max) {
	    		max = d;
	    	}
		} 
	    return FloatPair.getFloatPair(min, max);
	}
	
	/**测试在Axis轴上的A、B是否分离
	 * 用于MTD测试的方法
	 * <br>true为分离
	 * @param Axis
	 * @param A
	 * @param B
	 * @return */
	public static final boolean axisSeparatePolygons(final Vector2 Axis, final Polygon A, final AABBShape B,
			final Vector2 positionA, final Vector2 positionB) {
		pair = CalculateInterval(Axis, A, positionA);
		final float mina = pair.min;
		final float maxa = pair.max;
		
		pair = CalculateInterval(Axis, B, positionB);
		final float minb = pair.min;
		final float maxb = pair.max;
		
		if (mina > maxb || minb > maxa) 
			return true; 
		return false; 
	}
	
	/**测试在X轴上的A、B是否分离
	 * 用于MTD测试的方法
	 * Axis必须为(1,0)
	 * @param Axis
	 * @param A
	 * @param B
	 * @param positionA
	 * @param positionB
	 * @return <code>true</code> 在x*/
	public static final boolean axisSeparatePolygonsX(final Vector2 Axis, final Polygon A, final AABBShape B,
			final Vector2 positionA, final Vector2 positionB) {
		pair=CalculateInterval(Axis, A, positionA);
		final float mina=pair.min;
		final float maxa=pair.max;
		
		final float minb=B.getPoints()[0].x+positionB.x;
		final float maxb=B.getPoints()[2].x+positionB.x;
		
		if (mina > maxb || minb > maxa) 
			return true; 
		return false; 
	}
	
	/**测试在Y轴上的A、B是否分离
	 * 用于MTD测试的方法
	 * Axis必须为(0,1)
	 * <br>true为分离
	 * @param Axis
	 * @param A
	 * @param B
	 * @param positionA
	 * @param positionB
	 * @return */
	public static final boolean axisSeparatePolygonsY(final Vector2 Axis, final Polygon A, final AABBShape B,
			final Vector2 positionA, final Vector2 positionB) {
		pair=CalculateInterval(Axis, A, positionA);
		final float mina=pair.min;
		final float maxa=pair.max;
		
		final float minb=B.getPoints()[0].y+positionB.y;
		final float maxb=B.getPoints()[2].y+positionB.y;
		
		if (mina > maxb || minb > maxa) 
			return true; 
		return false; 
	}
	
	
	
	//TODO 关于圆与圆的计算
	/**圆与圆的相交测试
	 * @param A
	 * @param B
	 * @param positionA
	 * @param positionB
	 * @return <code>true</code> 形状相交 
	 * <code>false</code> 形状不相交 */
	public static final boolean Intersect(final Circle A,final Circle B,
			final Vector2 positionA,final Vector2 positionB){
		pool1.set(positionA.x+A.circleCenter.x-positionB.x-B.circleCenter.x,
				positionA.y+A.circleCenter.y-positionB.y-B.circleCenter.y);
		final float dist2=V2.dot(pool1, pool1);
		float radiusSum=A.getRadius()+B.getRadius();
		return dist2 <= radiusSum*radiusSum;
	}
	
	/**圆与圆的MTD相交测试
	 * @param A
	 * @param B
	 * @param positionA
	 * @param positionB
	 * @param MTD
	 * @return <code>true</code> 形状相交 
	 * <code>false</code> 形状不相交 */
	public static final boolean Intersect(final Circle A,final Circle B,
			final Vector2 positionA,final Vector2 positionB,final Vector2 MTD){
		
		//pool1为MTD的方向 pool2保存pool1的向量
		pool1.set(positionA.x+A.circleCenter.x-positionB.x-B.circleCenter.x,
				positionA.y+A.circleCenter.y-positionB.y-B.circleCenter.y);
		pool2.set(pool1);
		
		//求出向量的模并单位化向量
		final float length = V2.normalize(pool1);
		
		//计算两圆的刺入深度
		final float depth=A.getRadius()+B.getRadius()-length;
		
		if(depth >= 0){
			MTD.set(pool1);
			MTD.scl(depth);
			// MTD方向应该是由positionB指向positionA
			if (V2.dot(pool1, MTD)<0) {
				V2.negate(MTD);
			}
			return true;
		}
		
		return false;
	}
	
	
	
	//TODO 关于圆与AABB的计算
	/** 圆与AABB之间的相交测试
	 * @param A
	 * @param B
	 * @param positionA
	 * @param positionB
	 * @return <code>true</code> 形状相交 
	 * <code>false</code> 形状不相交 */
	public static final boolean Intersect(final Circle A,final AABBShape B,
			final Vector2 positionA,final Vector2 positionB){
		final Vector2[] points=B.getPoints();
		float depth=0;
		final float dx=positionA.x+A.circleCenter.x-positionB.x;
		final float dy=positionA.y+A.circleCenter.y-positionB.y;
		
		//判断A相对B的所处范围
		if(dx>points[2].x){
			if(dy>points[2].y)	pool1.set(points[2]);
			else if(dy<points[1].y)	pool1.set(points[1]);
			else{
				depth=A.getRadius()+points[2].x-dx;
				if(depth>0) return true;
				else return false;
			}
		}
		
		else if(dx<points[3].x){
			if(dy>points[3].y)	pool1.set(points[3]);
			else if(dy<points[0].y)	pool1.set(points[0]);
			else{
				depth=A.getRadius()-points[0].x+dx;
				if(depth>0)return true;
				else return false;
			}
		}
		
		else{
			if(dy>0){
				depth=A.getRadius()+points[2].y-dy;
				if(depth>0) return true;
				else return false;
			}
			
			else{
				depth=A.getRadius()-points[0].y+dy;
				if(depth>0) return true;
				else return false;
			}
		}

		//最后的判断
		final float tx=dx-pool1.x;final float ty=dy-pool1.y;
		final float dist2=tx*tx+ty*ty;
		if(A.getRadius()*A.getRadius()>dist2) return true;
		return false;
	}
	
	/** 圆与AABB之间的MTD相交测试
	 * @param A
	 * @param B
	 * @param positionA
	 * @param positionB
	 * @param MTD
	 * @return <code>true</code> 形状相交 
	 * <code>false</code> 形状不相交 */
	public static final boolean Intersect(final Circle A,final AABBShape B,
			final Vector2 positionA,final Vector2 positionB,final Vector2 MTD){
		final Vector2[] points=B.getPoints();
		float depth=0;
		final float dx=positionA.x+A.circleCenter.x-positionB.x;
		final float dy=positionA.y+A.circleCenter.y-positionB.y;
		
//		System.out.println("x "+dx+" y "+dy);
//		for(int i=0;i<4;i++){
//			System.out.println(i+" "+points[i]);
//		}
		//判断A相对B的所处范围
		if(dx>points[2].x){
			
			if(dy>points[2].y) {
				pool1.set(points[2]);
			}
			else if(dy<points[1].y)	{
				pool1.set(points[1]);
			}
			else {
				depth=A.getRadius()+points[2].x-dx;
				if(depth>0){
					MTD.set(depth, 0);
					return true;
				}
				else return false;
			}
		}
		
		
		else if(dx<points[3].x){
			
			if(dy>points[3].y)	pool1.set(points[3]);
			else if(dy<points[0].y)	pool1.set(points[0]);
			else{
				depth=A.getRadius()-points[0].x+dx;
				if(depth>0){
					MTD.set(-depth, 0);
					return true;
				}
				else return false;
			}
			
		}
		
		
		else{
			if(dy>0){
				depth=A.getRadius()+points[2].y-dy;
				if(depth>0){
					MTD.set(0, depth);
					return true;
				}
				else return false;
			}
			
			else{
				depth=A.getRadius()-points[0].y+dy;
				if(depth>0){
					MTD.set(0, -depth);
					return true;
				}
				else return false;
			}
			
		}
		
		//求出MTD向量
		MTD.set(dx-pool1.x, dy-pool1.y);
		float length= V2.normalize(MTD); //MTD.normalize();
		depth=A.getRadius()-length;
		if(depth>0){
			MTD.scl(depth);
			return true;
		}
		
		return false;
	}
	
	
	//TODO 关于圆与多边形的计算
	/** 圆与多边形之间的相交测试
	 * @param A 圆
	 * @param B 多边形（逆时针）
	 * @param positionA
	 * @param positionB
	 * @return <code>true</code> 形状相交 
	 * <code>false</code> 形状不相交 */
	public static final boolean Intersect(final Circle A,final Polygon B,
			final Vector2 positionA,final Vector2 positionB){
		//其中pool1存放的是A相对于B的坐标
		final Vector2[] points=B.getPoints();
//		pool1.set(positionA.x-positionB.x, positionA.y-positionB.y);
		pool1.set(positionA.x+A.circleCenter.x-positionB.x,
				positionA.y+A.circleCenter.y-positionB.y);
		
		//判断positionB的所在区间
		//这里决定采用一些ShapeAlgorithm的算法
		int low=0;
		int high=points.length;
		final int length=high-1;
		
		//找出positionA相对于多边形B的的所在侧
		do{
			int mid=(low+high)/2;
			if(ShapeAlgorithms.TriangleIsCCW(points[0],points[mid],pool1))
				low=mid;
			else
				high=mid;
		}while(low+1<high);
		
		Vector2 a,b,c;		
		final int index0=low-1<0?length:low-1;
		final int index1=low;
		final int index2=high%points.length;
		final int index3=(high+1)%points.length;
		
		a = pool2.set(points[index1].x-points[index0].x, points[index1].y-points[index0].y);
		b = pool3.set(points[index2].x-points[index1].x, points[index2].y-points[index1].y);
		c = pool4.set(points[index3].x-points[index2].x, points[index3].y-points[index2].y);
		
		pool5.set(pool1.x-points[index1].x, pool1.y-points[index1].y);
		final float s0 = V2.dot(a, pool5);
		final float t0 = V2.dot(b, pool5);
			
		if(s0>=0&&t0<=0){
			//说明pool5为最近点
			float dist2=V2.dot(pool5, pool5);
			if(A.getRadius()*A.getRadius()>dist2) return true;
			else return false;
		}
	
		pool5.set(pool1.x-points[index2].x, pool1.y-points[index2].y);
//		float s1 = Vector2.dot(a.negate(), pool5);
		final float s1 = V2.dot(b, pool5);
		final float t1 = V2.dot(c, pool5);
			
		if(s1>=0&&t1<=0){
			//说明pool5为最近点
			float dist2=V2.dot(pool5, pool5);
			if(A.getRadius()*A.getRadius()>dist2) return true;
			else return false;
		}
			
		else if(t0 > 0 && s1 < 0){
			pool5.set(pool1.x-points[index1].x, pool1.y-points[index1].y);
			
			V2.negate(b);
//			b.nor();
			float dist=MathUtils.abs(V2.cross(pool5, b));
	
			float depth=A.getRadius()-dist;
			
			if(depth>0) return true;
			else return false;
		}
		
		else if(s0 < 0){
			pool5.set(pool1.x-points[index0].x, pool1.y-points[index0].y);
			V2.normalize(a);
			float dist=MathUtils.abs(V2.cross(pool5, a));
	
			float depth=A.getRadius()-dist;
			
			if(depth>0) return true;
			else return false;
		}
		
		else if(t1 > 0){
			pool5.set(pool1.x-points[index2].x, pool1.y-points[index2].y);
			V2.normalize(c);
			float dist=MathUtils.abs(V2.cross(pool5, c));
			float depth=A.getRadius()-dist;
			
			if(depth>0) return true;
			else return false;
		}
		return false;
	}
	
	/** 圆与多边形之间的MTD相交测试
	 * @param A 圆
	 * @param B 多边形（逆时针）
	 * @param positionA
	 * @param positionB
	 * @param MTD
	 * @return <code>true</code> 形状相交 
	 * <code>false</code> 形状不相交 */
	public static final boolean Intersect(final Circle A,final Polygon B,
			final Vector2 positionA,final Vector2 positionB,final Vector2 MTD){
		//其中pool1存放的是A相对于B的坐标
		final Vector2[] points=B.getPoints();
//		pool1.set(positionA.x-positionB.x, positionA.y-positionB.y);
		pool1.set(positionA.x+A.circleCenter.x-positionB.x,
				positionA.y+A.circleCenter.y-positionB.y);
		//判断positionB的所在区间
		//这里决定采用一些ShapeAlgorithm的算法
		int low=0;
		int high=points.length;
		final int length=high-1;
		
		//找出positionA相对于多边形B的的所在侧
		do{
			int mid=(low+high)/2;
			if(ShapeAlgorithms.TriangleIsCCW(points[0],points[mid],pool1))
				low=mid;
			else
				high=mid;
		}while(low+1<high);
		
		Vector2 a,b,c;		
		final int index0=low-1<0?length:low-1;
		final int index1=low;
		final int index2=high%points.length;
		final int index3=(high+1)%points.length;
		
		a = pool2.set(points[index1].x-points[index0].x, points[index1].y-points[index0].y);
		b = pool3.set(points[index2].x-points[index1].x, points[index2].y-points[index1].y);
		c = pool4.set(points[index3].x-points[index2].x, points[index3].y-points[index2].y);
		
//		Vector2 temp=pool1.sub(points[0]);
		pool5.set(pool1.x-points[index1].x, pool1.y-points[index1].y);
		final float s0 = V2.dot(a, pool5);
		final float t0 = V2.dot(b, pool5);
			
		if(s0>=0&&t0<=0){
			//说明pool5为最近点
			float l= V2.normalize(pool5);
			float depth=A.getRadius()-l;
			
			if(depth>0){
				MTD.set(pool5);
				MTD.scl(depth);
				if(V2.dot(pool1, MTD)<0){
					V2.negate(MTD);
				}
				return true;
			}
			else return false;
		}
	
//		temp=p.sub(v[1]);
		pool5.set(pool1.x-points[index2].x, pool1.y-points[index2].y);
//		float s1 = Vector2.dot(a.negate(), pool5);
		final float s1 = V2.dot(b, pool5);
		final float t1 = V2.dot(c, pool5);
			
		if(s1>=0&&t1<=0){
			//说明pool5为最近点
			float l= V2.normalize(pool5);
			float depth=A.getRadius()-l;
			
			if(depth>0){
				MTD.set(pool5);
				MTD.scl(depth);
				if(V2.dot(pool1, MTD)<0){
					V2.negate(MTD);
				}
				return true;
			}
			else return false;
		}
			
		else if(t0 > 0 && s1 < 0){
			//return ClosetDistanceSquarePtSegment(p,v[low],v[high]);
			pool5.set(pool1.x-points[index1].x, pool1.y-points[index1].y);
			V2.normalize(b);
			float dist=MathUtils.abs(V2.cross(pool5, b));
//			float dist=C2Math.sqrt(dist2);
	
			float depth=A.getRadius()-dist;
			
			if(depth>0){
				MTD.set(-b.y,b.x);
				MTD.scl(depth);
				if(V2.dot(pool1, MTD)<0){
					V2.negate(MTD);
				}
				return true;
			}
			else return false;
		}
		
		else if(s0 < 0){
			//return ClosetDistanceSquarePtSegment(p,v[low-1],v[low]);
			pool5.set(pool1.x-points[index0].x, pool1.y-points[index0].y);
			V2.normalize(a);
			float dist=MathUtils.abs(V2.cross(pool5, a));
//			float dist=C2Math.sqrt(dist2);
	
			float depth=A.getRadius()-dist;
			
			if(depth>0){
				MTD.set(-a.y,a.x);
				MTD.scl(depth);
				if(V2.dot(pool1, MTD)<0){
					V2.negate(MTD);
				}
				return true;
			}
			else return false;
		}
		
		else if(t1 > 0){
			//return ClosetDistanceSquarePtSegment(p,v[high],v[high+1]);
			pool5.set(pool1.x-points[index2].x, pool1.y-points[index2].y);
			V2.normalize(c);
			float dist=MathUtils.abs(V2.cross(pool5, c));
//			float dist=C2Math.sqrt(dist2);
	
			float depth=A.getRadius()-dist;
			
			if(depth>0){
				MTD.set(-c.y,c.x);
				MTD.scl(depth);
				if(V2.dot(pool1, MTD)<0){
					V2.negate(MTD);
				}
				return true;
			}
			else return false;
		}
		
		return false;
	}
}

