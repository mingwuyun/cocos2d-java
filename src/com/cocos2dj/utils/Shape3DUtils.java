package com.cocos2dj.utils;


/**
 * 3D图形相交算法<p>
 * 
 * @author xu jun
 * Copyright (c) 2016. All rights reserved. 
 */
public class Shape3DUtils {
	
//	//pool>>
//	static final Vector3	pool1 = new Vector3();
//	static final Vector3	pool2 = new Vector3();
//	static final Vector3 	pool3 = new Vector3();
	//pool<<
	
	
//	/**
//	 * 检测box是否在frustum中<p>
//	 * 有微小误差，尽量使用fatBox
//	 * 
//	 * @param frustum
//	 * @param box
//	 * @return
//	 */
//	public static final boolean IntersectFrustumAABB(final Frustum frustum, final BoundingBox box) {
//		final float minX = box.min.x;
//		final float minY = box.min.y;
//		final float minZ = box.min.z;
//		final float maxX = box.max.x;
//		final float maxY = box.max.y;
//		final float maxZ = box.max.z;
//		
//		do{
//			if(frustum.pointInFrustum(minX, minY, minZ)) break;
//			if(frustum.pointInFrustum(maxX, minY, minZ)) break;
//			if(frustum.pointInFrustum(maxX, maxY, minZ)) break;
//			if(frustum.pointInFrustum(minX, maxY, minZ)) break;
//			if(frustum.pointInFrustum(minX, minY, maxZ)) break;
//			if(frustum.pointInFrustum(maxX, minY, maxZ)) break;
//			if(frustum.pointInFrustum(maxX, maxY, maxZ)) break;
//			if(frustum.pointInFrustum(minX, maxY, maxZ)) break;
//			
//			//最后检测一下线段(可能视锥包含AABB中)
//			final Vector3[] ps = frustum.planePoints;
//			if(Shape3DUtils.IntersectRayAABB_A2B(ps[0], ps[4], minX, minY, minZ, maxX, maxY, maxZ, null) > 0) {break;}
//			if(Shape3DUtils.IntersectRayAABB_A2B(ps[1], ps[5], minX, minY, minZ, maxX, maxY, maxZ, null) > 0) {break;}
//			if(Shape3DUtils.IntersectRayAABB_A2B(ps[2], ps[6], minX, minY, minZ, maxX, maxY, maxZ, null) > 0) {break;}
//			if(Shape3DUtils.IntersectRayAABB_A2B(ps[3], ps[7], minX, minY, minZ, maxX, maxY, maxZ, null) > 0) {break;}
//			
//			return false;
//		} while(false);
//		
//		return true;
//	}
//	
//	public static final float IntersectRayAABB(final Vector3 positionA, final Vector3 direct, 
//			final Vector3 aabbMin, final Vector3 aabbMax, final Vector3 ret) {
//		return IntersectRayAABB(positionA, direct, 
//				aabbMin.x, aabbMin.y, aabbMin.z, 
//				aabbMax.x, aabbMax.y, aabbMax.z, ret);
//	}
//	
//	public static final float IntersectRayAABB_A2B(final Vector3 positionA, final Vector3 positionB, 
//			final float minX, final float minY, final float minZ,
//			final float maxX, final float maxY, final float maxZ, 
//			final Vector3 ret) {
//		pool3.set(positionB).sub(positionA);
//		return IntersectRayAABB(positionA, pool3, minX, minY, minZ, maxX, maxY, maxZ, ret);
//	}
//	
//	public static final float IntersectRayAABB(final Vector3 positionA, final Vector3 direct, 
//			final float minX, final float minY, final float minZ,
//			final float maxX, final float maxY, final float maxZ, 
//			final Vector3 ret) {
//		
//		float 	tmin = 0f;
//		float 	tmax = Float.MAX_VALUE;
//		
//		pool1.set(minX, minY, minZ);
//		pool2.set(maxX, maxY, maxZ);
//		
//		final Vector3	min = pool1;
//		final Vector3 max = pool2;
//		
//		
//		if(Math.abs(direct.x) < PhySetting.EPSILON) {
//			if(positionA.x < min.x || positionA.x > max.x) {return 0f;}
//		}
//		else {
//			float ood = 1f / direct.x;
//			float t1 = (min.x - positionA.x) * ood;
//			float t2 = (max.x - positionA.x) * ood;
//			
//			if(t1 > t2) {float tmp = t2;t2 = t1;t1 = tmp;}
//			
//			if(t1 > tmin) {tmin = t1;}
//			if(t2 < tmax) {tmax = t2;}
//			
//			if(tmin > tmax) {return 0f;}
//			
//		}
//		if(Math.abs(direct.y) <  PhySetting.EPSILON) {
//			if(positionA.y < min.y || positionA.y > max.y) {return 0f;}
//		}
//		else {
//			float ood = 1f / direct.y;
//			float t1 = (min.y - positionA.y) * ood;
//			float t2 = (max.y - positionA.y) * ood;
//			
//			if(t1 > t2) {float tmp = t2;t2 = t1;t1 = tmp;}
//			
//			if(t1 > tmin) {tmin = t1;}
//			if(t2 < tmax) {tmax = t2;}
//			
//			if(tmin > tmax) {return 0f;}
//		}
//		if(Math.abs(direct.z) <  PhySetting.EPSILON) {
//			if(positionA.z < min.z || positionA.z > max.z) {return 0f;}
//		}
//		else {
//			float ood = 1f / direct.z;
//			float t1 = (min.z - positionA.z) * ood;
//			float t2 = (max.z - positionA.z) * ood;
//			
//			if(t1 > t2) {float tmp = t2;t2 = t1;t1 = tmp;}
//			
//			if(t1 > tmin) {tmin = t1;}
//			if(t2 < tmax) {tmax = t2;}
//			
//			if(tmin > tmax) {return 0f;}
//		}
//		
//		if(ret != null) {
//			//q = p + d * tmin
//			pool2.set(positionA).add(pool1.set(direct).scl(tmin));
//			ret.set(pool2);
//		}
//		return 1f;
//	}

}
