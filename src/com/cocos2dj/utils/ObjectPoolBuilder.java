package com.cocos2dj.utils;

/**
 * ObjectPoolBuilder.java
 * <p>
 * 包装对象池的创建
 * <pre>
 * //ObjectPoolBuilder<Object> builder = new ObjectPoolBuilder<>();
 * ObjectPoolBuilder<Object> builder = ObjectPoolBuilder.startBuilder();
 * builder.setClass(Object.class)
 * 			.setInitCount(3)
 * 			.setAddCount(2)
 * 			.createNormalPool();
 * </pre>
 * @author Copyright (c) 2016 xujun
 */
public class ObjectPoolBuilder<T> {
	
	/**开始构建 */
	public static <T> ObjectPoolBuilder<T> startBuilder() {
		return new ObjectPoolBuilder<T>();
	}
	
	//fields>>
	private Class<T> 	sClass;
	private Class<?>[] 	params;
	private Object[] 	args;
	
	private int 		initCount = 2;
	private int 		addCount = 1;
	//fields<<
	
	//methods>>
	public ObjectPoolBuilder<T> setClass(Class<T> clazz) {
		this.sClass = clazz;
		return this;
	}
	
	public ObjectPoolBuilder<T> setParamsClass(Class<?>...params) {
		this.params = params;
		return this;
	}
	
	public ObjectPoolBuilder<T> setParams(Object...args) {
		this.args = args;
		return this;
	}
	
	public ObjectPoolBuilder<T> setInitCount(int initCount) {
		this.initCount = initCount;
		return this;
	}
	
	public ObjectPoolBuilder<T> setAddCount(int addCount) {
		this.addCount = addCount;
		return this;
	}
	
	/**
	 * 默认创建方法 
	 * @see #createAddingPool()
	 * */
	public ObjectPoolLinear<T> create() {
		return createAddingPool();
	}
	
	/**
	 * 创建指数增长对象池
	 * <br> newCount = currCount * 2
	 */
	public ObjectPool<T> createNormalPool() {
		return new ObjectPool<>(sClass, initCount, params, args);
	}
	
	/**创建线性增长对象池<br>
	 * newCount = currCount + addCount */
	public ObjectPoolLinear<T> createAddingPool() {
		return new ObjectPoolLinear<>(sClass, initCount, addCount, params, args);
	}
	//methods<<
}
