package com.cocos2dj.module.base2d.framework.collision;

import com.badlogic.gdx.math.Vector2;
import com.cocos2dj.module.base2d.framework.common.MathUtils;
import com.cocos2dj.module.base2d.framework.common.V2;

/**
 * 形状相关算法<br>
 * <b>这个类的相关方法未测试完毕
 * @version 1.0
 * @project Card2DPhysicsEngine
 * @author xu jun
 * @date 2012.7-2012.12
 */
public final class ShapeAlgorithms {
	
	/**pool*/
	private static final Vector2 pool1 = new Vector2();
	private static final Vector2 pool2 = new Vector2();
	
	/**检测pointA pointB pointC 三点是否为逆时针排列<br>
	 * 顺序为pointA->pointB->pointC->pointA<br>
	 * 当三点共线时返回的是true<br>
	 * <b>use: pool1 pool2<b>
	 * @param pointA
	 * @param pointB
	 * @param pointC
	 * @return true = pointA pointB pointC逆时针排列
	 * 		   false = pointA pointB pointC顺时针排列  */
	public static final boolean TriangleIsCCW(final Vector2 pointA,
			final Vector2 pointB, final Vector2 pointC){
//		final Vector2 ab = pointB.sub(pointA); 
//		final Vector2 ac = pointC.sub(pointA);
		pool1.set(pointB.x-pointA.x, pointB.y-pointA.y);
		pool2.set(pointC.x-pointA.x, pointC.y-pointA.y);
		return V2.cross(pool1, pool2) >= 0;
	}
	
	
	/**测试 点p 是否在 多边形v 中<br>
	 * v是多边形的点集   该点集应按逆时针顺序排列<br>
	 * 点在边界上返回true<br>
	 * @param p
	 * @param v
	 * @return true = 点在多边形内
	 * 		   false = 点在多边形外 */
	public static final boolean PointInConvexPolygon(final Vector2 p,final Vector2[] v){
		int low=0;
		int high=v.length;
		
		do{
			int mid=(low+high)/2;
			if(TriangleIsCCW(v[0],v[mid],p))
				low=mid;
			else
				high=mid;
		}while(low+1<high);
		
		if(low==0||high==v.length)
			return false;
		
		return TriangleIsCCW(v[low],v[high],p);
	}
	
	/**求点p到线段ab所处直线的最短平方距离
	 * <br><b>use: pool1 pool2</b>
	 * @param p 测试点
	 * @param a 线段起始点
	 * @param b 线段终点
	 * @return 最短平方距离 */
	public static final float ClosetDistanceSquarePtSegment(final Vector2 p,final Vector2 a,final Vector2 b){
//		Vector2 ab=b.sub(a);
		pool1.set(b.x-a.x, b.y-a.y);
		pool1.nor();
		pool2.set(p.x-a.x, p.y-a.y);
//		return Math.abs(Vector2.cross(p.sub(a), pool1));
		return MathUtils.abs(V2.cross(pool2, pool1));
	}
	
	/**求点p到线段ab的最短距离
	 * <br><b>use: pool1 pool2</b>
	 * @param p 测试点
	 * @param a 线段起始点
	 * @param b 线段终点
	 * @return 最短距离 */
	public static final float ClosetDistancePtSegment(final Vector2 p,final Vector2 a,final Vector2 b){
		final float s=ClosetDistanceSquarePtSegment(p, a, b);
		return (float) MathUtils.sqrt(s);
	}
	
	/**求 点p 到 多边形的最短距离
	 * v按逆时针顺序排列
	 * @param p
	 * @param v
	 * @return 最短距离 */
	public static final float ClosestDistancePtPolygon(final Vector2 p,final Vector2[] v){
		int low=0;
		int high=v.length;
		int length=high-1;
		
		do{
			
			int mid=(low+high)/2;
			if(TriangleIsCCW(v[0],v[mid],p))
				low=mid;
			else
				high=mid;
		}while(low+1<high);
		
		if(low==0){
			Vector2 a = v[0].sub(v[length]);
			Vector2 b = v[0].sub(v[1]);
			Vector2 c = v[1].sub(v[2]);
			
			Vector2 temp=p.sub(v[0]);
			float s0 = V2.dot(a, temp);
			float t0 = V2.dot(b, temp);
			
			if(s0>=0&&t0>=0)
				return temp.len();
			
			temp=p.sub(v[1]);
			float s1 = V2.dot(V2.negate(a), temp);
			float t1 = V2.dot(c, temp);
			
			if(s1>=0&&t1>=0)
				return temp.len();
			
			if(t0<=0&&s1<=0)
				return ClosetDistancePtSegment(p,v[0],v[1]);
			if(s0<=0)
				return ClosetDistancePtSegment(p,v[length],v[0]);
			if(t1<=0)
				return ClosetDistancePtSegment(p,v[2],v[1]);
		}
		
		if(high==v.length){
			Vector2 a = v[length-1].sub(v[length-2]);
			Vector2 b = v[length-1].sub(v[length]);
			Vector2 c = v[0].sub(v[length]);
			
			Vector2 temp=p.sub(v[length-1]);
			float s0 = V2.dot(a, temp);
			float t0 = V2.dot(b, temp);
			
			if(s0>=0&&t0>=0)
				return temp.len();
			
			temp=p.sub(v[length]);
			float s1 = V2.dot(V2.negate(a), temp);
			float t1 = V2.dot(c, temp);
			
			if(s1>=0&&t1>=0)
				return temp.len();
			
			if(t0<=0&&s1<=0)
				return ClosetDistancePtSegment(p,v[length-1],v[length]);
			if(s0<=0)
				return ClosetDistancePtSegment(p,v[length-2],v[length-1]);
			if(t1<=0)
				return ClosetDistancePtSegment(p,v[length],v[0]);
		}
		
		//a、b、c、为相邻的三个属于多边形的边
		Vector2 a = v[low].sub(v[low-1]);
		Vector2 b = v[low].sub(v[high]);
		Vector2 c = v[high].sub(v[high+1==length?0:high+1]);
		
		//测试p在测试边上的投影是否超出最小范围
		Vector2 temp = p.sub(v[low]);
		
		float s0 = V2.dot(temp, a);
		float t0 = V2.dot(temp, b);
		if(s0>=0 && t0>=0)
			return temp.len();
		
		//测试p在测试边上的投影是否超出最大范围
		temp = p.sub(v[high]);
		float s1 = V2.dot(temp, V2.negate(b));
		float t1 = V2.dot(temp, c);
		if(s1>=0&&t1>=0)
			return temp.len();
		
		
		if(t0<=0&&s1<=0)
			return ClosetDistancePtSegment(p,v[low],v[high]);
		if(s0<=0)
			return ClosetDistancePtSegment(p,v[low-1],v[low]);
		if(t1<=0)
			return ClosetDistancePtSegment(p,v[high],v[high+1]);

		return 0;
	}
	
	/**求 点p 到 多边形的最短平方距离
	 * v按逆时针顺序排列
	 * @param p
	 * @param v
	 * @return 最短平方距离 */
	public static final float ClosestDistanceSquarePtPolygon(Vector2 p,Vector2[] v){
		int low=0;
		int high=v.length;
		int length=high-1;
		
		do{
			
			int mid=(low+high)/2;
			if(TriangleIsCCW(v[0],v[mid],p))
				low=mid;
			else
				high=mid;
		}while(low+1<high);
		
		if(low==0){
			Vector2 a = v[0].sub(v[length]);
			Vector2 b = v[0].sub(v[1]);
			Vector2 c = v[1].sub(v[2]);
			
			Vector2 temp=p.sub(v[0]);
			float s0 = V2.dot(a, temp);
			float t0 = V2.dot(b, temp);
			
			if(s0>=0&&t0>=0)
				return temp.len2();
			
			temp=p.sub(v[1]);
			float s1 = V2.dot(V2.negate(a), temp);
			float t1 = V2.dot(c, temp);
			
			if(s1>=0&&t1>=0)
				return temp.len2();
			
			if(t0<=0&&s1<=0)
				return ClosetDistanceSquarePtSegment(p,v[0],v[1]);
			if(s0<=0)
				return ClosetDistanceSquarePtSegment(p,v[length],v[0]);
			if(t1<=0)
				return ClosetDistanceSquarePtSegment(p,v[2],v[1]);
		}
		
		if(high==v.length){
			Vector2 a = v[length-1].sub(v[length-2]);
			Vector2 b = v[length-1].sub(v[length]);
			Vector2 c = v[0].sub(v[length]);
			
			Vector2 temp=p.sub(v[length-1]);
			float s0 = V2.dot(a, temp);
			float t0 = V2.dot(b, temp);
			
			if(s0>=0&&t0>=0)
				return temp.len2();
			
			temp=p.sub(v[length]);
			float s1 = V2.dot(V2.negate(a), temp);
			float t1 = V2.dot(c, temp);
			
			if(s1>=0&&t1>=0)
				return temp.len2();
			
			if(t0<=0&&s1<=0)
				return ClosetDistanceSquarePtSegment(p,v[length-1],v[length]);
			if(s0<=0)
				return ClosetDistanceSquarePtSegment(p,v[length-2],v[length-1]);
			if(t1<=0)
				return ClosetDistanceSquarePtSegment(p,v[length],v[0]);
		}
		
		Vector2 a = v[low].sub(v[low-1]);
		Vector2 b = v[low].sub(v[high]);
		Vector2 c = v[high].sub(v[high+1==length?0:high+1]);
		
		Vector2 temp = p.sub(v[low]);
		
		float s0 = V2.dot(temp, a);
		float t0 = V2.dot(temp, b);
		if(s0>=0&&t0>=0)
			return temp.len2();
		
		temp = p.sub(v[high]);
		float s1 = V2.dot(temp, V2.negate(b));
		float t1 = V2.dot(temp, c);
		if(s1>=0&&t1>=0)
			return temp.len2();
		
		
		if(t0<=0&&s1<=0)
			return ClosetDistanceSquarePtSegment(p,v[low],v[high]);
		if(s0<=0)
			return ClosetDistanceSquarePtSegment(p,v[low-1],v[low]);
		if(t1<=0)
			return ClosetDistanceSquarePtSegment(p,v[high],v[high+1]);

		return 0;
	}
}