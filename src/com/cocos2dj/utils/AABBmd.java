package com.stormframework.util;

/**
 * 轴对齐包围盒
 * @说明	采用最"小值-直径"方式储存
 */

public class AABBmd {
	//===========================================================//
	//                         Fields                            //
	//===========================================================//
	public float minX;
	public float minY;
	public float dW;
	public float dH;
	//===========================================================//
	//                      Constructors                         //
	//===========================================================//
	/*************************************************************
	 * @param minX	最小值X
	 * @param minY  最小值Y
	 * @param dW    X方向直径
	 * @param dH    Y方向直径
	 */
	public AABBmd(float minX, float minY, float dW, float dH){
	/*************************************************************/
		this.minX=minX;
		this.minY=minY;
		this.dH=dH;
		this.dW=dW;
	}
	//===========================================================//
	//                         Methods                           //
	//===========================================================//
	/*************************************************************
	 * 更新位置(minX,minY)
	 * @param minX
	 * @param minY
	 */
	public final void updateAABB(float minX, float minY){
	/*************************************************************/
		this.minX=minX;
		this.minY=minY;
	}
	
	/*************************************************************
	 * 更新位置与范围(minX,minY,dW,dH)
	 * @param minX
	 * @param minY
	 * @param dW
	 * @param dH
	 */
	public final void updateAABB(float minX, float minY, float dW, float dH){
	/*************************************************************/
		this.minX=minX;
		this.minY=minY;
		this.dH=dH;
		this.dW=dW;
	}
	
	/*************************************************************
	 * 重设范围(dW,dH)
	 * @param dW
	 * @param dH
	 */
	public final void reSetAABB(float dW, float dH){
	/*************************************************************/
		this.dH=dH;
		this.dW=dW;
	}
	
	/*************************************************************
	 * 检测与另一AABB的相交
	 * @param 	another  待检测AABB
	 * @return	true 相交
	 */
	public final boolean testAABB(AABBmd another){
	/*************************************************************/
		float t;
		if((t=minX-another.minX)>another.dW||-t>dW)
			return false;
		if((t=minY-another.minY)>another.dH||-t>dH)
		    return false;
		return true;
	}
	
	/*************************************************************
	 * 检测AABB的正确性
	 * @return  true 该AABB无效
	 */
	public final boolean isInvalid(){
	/*************************************************************/
		return dW<0||dH<0;
	}
	
	/*************************************************************
	 * 测试该AABB是否在另一个AABB中
	 * @param  another
	 * @return true  another包含该AABB
	 * 		   flase another没有包含该AABB	
	 */
	public final boolean testInAABB(AABBmd another){
	/*************************************************************/
		return (minX>=another.minX&&minX+dW<=another.minX+another.dW
				&&minY>=another.minY&&minY+dH<=another.minY+another.dH);
	}
	
	/*************************************************************
	 * 检查AABB a 是否在  AABB b中
	 * @param a  被包含AABB
	 * @param b  包含AABB
	 * @return   true  b包含a
	 * 		     flase b没有包含a
	 */
	public static final boolean testAABBInAABB(AABBmd a, AABBmd b){
	/*************************************************************/
		return (a.minX>=b.minX&&a.minX+a.dW<=b.minX+b.dW
				&&a.minY>=b.minY&&a.minY+a.dH<=b.minY+b.dH);
	}
	
	/*************************************************************
	 * 检测两个AABB的相交
	 * @param a  测试AABB a
	 * @param b  测试AABB b
	 * @return   ture 相交
	 */
	public static final boolean testAABBAABB(AABBmd a,AABBmd b){
	/*************************************************************/
		float t;
		if((t=a.minX-b.minX)>b.dW||-t>a.dW)
			return false;
		if((t=a.minY-b.minY)>b.dH||-t>a.dH)
		    return false;
		return true;
	}
	
	/*************************************************************
	 * 检测AABB的正确性
	 * @return  true 该AABB无效
	 */
	public static final boolean isInvalid(AABBmd a){
	/*************************************************************/
		return a.dW<0||a.dH<0;
	}
    //===========================================================//
	//                Inner and Anonymous Classes                //
	//===========================================================//
}

