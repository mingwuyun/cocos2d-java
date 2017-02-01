package com.cocos2dj.utils;

/**
 * ����ع�������<p>
 * 
 * ʹ�÷�������:
 * <pre>
 * ObjectPoolBuilder<Object> builder = new ObjectPoolBuilder<>();
 * builder.setClass(Object.class)
 * 			.setInitCount(3)
 * 			.setAddCount(2)
 * 			.createNormalPool();
 * </pre>
 * @author xu jun
 *			2016-8-2
 */
public class ObjectPoolBuilder<T> {
	
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
	
	/**�������Զ���أ�Ĭ���Ƽ��� */
	public ObjectPoolLinear<T> create() {
		return createAddingPool();
	}
	
	/**������ͨ����أ���������2����*/
	public ObjectPool<T> createNormalPool() {
		return new ObjectPool<>(sClass, initCount, params, args);
	}
	
	/**�������Զ���أ���������ָ��������*/
	public ObjectPoolLinear<T> createAddingPool() {
		return new ObjectPoolLinear<>(sClass, initCount, addCount, params, args);
	}
	//methods<<
}
