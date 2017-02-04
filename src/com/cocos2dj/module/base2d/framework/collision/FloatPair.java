package com.cocos2dj.module.base2d.framework.collision;
/**
 * 2015
 * 改为了单例模式以充分利用该资源
 */
public final class FloatPair {
	
	private static FloatPair pair = new FloatPair();
	public float min;
	public float max;
	
//	private FloatPair(final float min, final float max){
//		this.min = min;
//		this.max = max;
//	}

	public final void setValue(final float min,final float max){
		this.min = min;
		this.max  =max;
	}
	
	/**
	 * 销毁floatPair
	 */
	public static final void release() {
		pair = null;
	}
	
	/**
	 * 获取内含有min与max两个float的floatPair
	 * @param min
	 * @param max
	 */
	static final FloatPair getFloatPair(final float min,final float max){
//		if(pair == null){
//			pair = new FloatPair(min, max);
//		}else {
		pair.setValue(min, max);
//		}
		return pair;
	}
}