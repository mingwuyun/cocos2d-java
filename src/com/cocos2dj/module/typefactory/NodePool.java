package com.cocos2dj.module.typefactory;

import com.badlogic.gdx.utils.Array;
import com.cocos2dj.s2d.Node;

/**
 * 对象池<p>
 * 如果不加typePool参数则默认的对象池实现为AddingObjectPool（扩容是增加指定数量）
 * 
 * 增加了监听相关接口
 * 
 * @author xu jun
 * Copyright (c) 2012-2016. All rights reserved. 
 * 				2016-8-4
 */
public class NodePool<T extends Node> {

	public static final int NORMAL_POOL = 0;
	public static final int ADDING_POOL = 1;
	
	private int id = -1;
	private String name;
	private IObjectPool<T> pool;
	private final SObject parent;
	private final Array<SObjectObserver>	observers = new Array<>();	//观察者
	
	
	public void addObserver(SObjectObserver observer) {
		observers.add(observer);
	}
	
	public void removeObserver(SObjectObserver observer) {
		observers.removeValue(observer, true);
	}
	
	public void clearObserver() {
		observers.clear();
	}
	
	final void observers_onPop(T t) {
		for(int i = observers.size - 1; i >= 0; --i) {
			final SObjectObserver obv = observers.get(i);
			final boolean ret = obv.onObjectEvent(SObjectEvent.event_pop, t);
			if(ret) {
				observers.removeIndex(i);
			}
		}
	}
	
	final void observers_onPush(T t) {
		for(int i = observers.size - 1; i >= 0; --i) {
			final SObjectObserver obv = observers.get(i);
			final boolean ret = obv.onObjectEvent(SObjectEvent.event_push, t);
			if(ret) {
				observers.removeIndex(i);
			}
		}
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public int getPoolID() {
		return id;
	}
	
	//ID的设置只能由Pools进行 所以这个为包访问权限
	final void setPoolID(int id) {
		this.id = id;
	}
	
	/**对象池为AddingObjectPool
	 * @param clazz 放入池中对象的类 
	 * @param argInitSize 初始对象的数量 与每次扩容的数量（poolType=1）*/
	public NodePool(SObject parent, Class<T> clazz, int initSize) {
		this(parent, 1, clazz, initSize);
	}
	
	/**@param poolType 池的类型，与扩容方式有关  <code>0=扩两倍 1=增加固定数量</code>
	 * @param clazz 放入池中对象的类 
	 * @param argInitSize 初始对象的数量 与每次扩容的数量（poolType=1）*/
	public NodePool(SObject parent, int poolType, Class<T> clazz, int initSize) {
		this(parent, poolType, clazz, initSize, initSize);
	}
	/**@param poolType 池的类型，与扩容方式有关  <code>0=扩两倍 1=增加固定数量</code>
	 * @param clazz 放入池中对象的类 
	 * @param argInitSize 初始对象的数量 
	 * @param argAddCount 如果选用addingpool则为每次扩容增加的容量否则无效 */
//	public SObjectPool(int poolType, Class<T> clazz, int argInitSize, int argAddCount){
//		this(SceneManager.getCurrentScene(), poolType, clazz, argInitSize, argAddCount);
//	}
	
	/**@param argScene 对象池所在的scene  默认是当前的场景
	 * @param poolType 池的类型，与扩容方式有关  <code>0=扩两倍 1=增加固定数量</code>
	 * @param clazz 放入池中对象的类 
	 * @param argInitSize 初始对象的数量
	 * @param argAddCount 如果选用addingpool则为每次扩容增加的容量否则无效 */
	public NodePool(SObject parent, int poolType, Class<T> clazz, int argInitSize, int argAddCount) {
		this(parent, poolType, clazz, argInitSize, argAddCount, null, null);
	}
	
	/**@param clazz 放入池中对象的类 
	 * @param poolType 池的类型，与扩容方式有关  <code>0=扩两倍 1=增加固定数量</code>
	 * @param argInitSize 初始对象的数量 以及扩容的增加数量
	 * 假设初始数量为10,如果类型为0则扩容2次后为10*2*2=40,而类型为1则扩容两次后为10+10+10=30.
	 * @param argParam 构造函数的参数类型
	 * @param argArgs 构造函数的参数*/
	public NodePool(SObject parent, Class<T> clazz, int poolType, int argInitSize, Class<?>[] argParam, Object[] argArgs){
		this(parent ,poolType,
				clazz,argInitSize,argInitSize,argParam,argArgs);
	}
	
//	/**@param clazz 放入池中对象的类 
//	 * @param poolType 池的类型，与扩容方式有关  <code>0=扩两倍 1=增加固定数量</code>
//	 * @param argInitSize 
//	 * @param argAddCount
//	 * @param argParam 构造函数的参数类型
//	 * @param argArgs 构造函数的参数*/
//	public SObjectPool(int poolType, Class<T> clazz, int argInitSize, int argAddCount, Class<?>[] argParam, Object[] argArgs){
//		this(SceneManager.getCurrentScene(),poolType,
//				clazz,argInitSize,argAddCount,argParam,argArgs);
//	}
	
	/**@param argScene 对象池所在的scene  默认是当前的场景
	 * @param poolType 池的类型，与扩容方式有关  <code>0=扩两倍 1=增加固定数量</code>
	 * @param clazz 放入池中对象的类 
	 * @param argInitSize 初始对象的数量
	 * @param argAddCount 如果选用addingpool则为每次扩容增加的容量否则无效
	 * @param argParam 构造函数的参数类型
	 * @param argArgs 构造函数的参数 */
	public NodePool(SObject argparent, int poolType, Class<T> clazz, int argInitSize, int argAddCount,
			Class<?>[] argParam, final Object[] argArgs) {
		/*System.out.println("create pool :");
		System.out.println("type:"+poolType);
		System.out.println("class name:"+clazz.getName());
		if(argParam!=null)
			for(int i=0;i<argParam.length;++i){
				System.out.println("[param arg]"+i+" : "+argParam[i].getName()+" | "+argArgs.getClass().getName());
			}
		else System.out.println("no param ");*/
		
//		scene = argScene;
		
		this.parent = argparent;
		
		switch(poolType){
		case NORMAL_POOL:
			pool = new ObjectPool<T>(clazz, argInitSize, argParam, argArgs) {
				public void onCreated(T t){
//					scene.add(t);
//					t.created();
					t._setObjectPool(NodePool.this);	//先设置为pool对象
					parent.addChild(t);
					onCreate(t);
					t._onSleep();
				}
				
				public void onPop(T t){
					t._setInPool(false);
					t._onAwake();
				}
				
				public void onPush(T t){
					t._setInPool(true);
//					t._onSleep();	//引入延迟回收机制所以不调用这个方法了
				}
			};
			break;
		case ADDING_POOL:
			pool = new ObjectPoolLinear<T>(clazz, argInitSize, argAddCount, argParam, argArgs){
				public void onCreated(T t){
//					scene.addEntity(t);
//					t.created();
					t._setObjectPool(NodePool.this);
					parent.addChild(t);
					onCreate(t);
//					SLog.debug("OBJECTPOOL", "create it");
					t._onSleep();
				}
				
				public void onPop(T t){
					t._setInPool(false);
					t._onAwake();
				}
				
				public void onPush(T t){
					t._setInPool(true);
//					t._onSleep();	//引入延迟回收机制所以不调用这个方法了
				}
			};
			break;
		}
		
		name = clazz.getName();
		SPools.addObjectPool(this);
	}
	
	/**为了在C2EntityPool层面进行初始化操作设置的方法 */
	public void onCreate(T t) {
		
	}
	
	/**从池中获取一个对象<br>
	 * 该方法执行时会调用t的onGet()方法  
	 * @return T */
	public final T pop() {
		final T t = pool.pop();
		t._setInPool(false);
		observers_onPop(t);
		return t;
	}
	
	/**向池中放入一个对象（回收）<br>
	 * 该方法执行时会调用t的onCycle()方法
	 * @param t */
	public final void push(T t) {
		if(t.isInPool()) return;
		t._setInPool(true);
		observers_onPush(t);
		pool.push(t);
	}
	
	/**从池中获取一个对象<br>
	 * 该方法不调用t的onGet()方法
	 * @return T */
	public final T get() {
		final T t = pool.get();
		t._setInPool(false);
		return t;
	}
	
	/**回收全部对象 */
	public final void recycleAll() {
		for(int i = 0, n = pool.getAll().length; i < n; ++i ) {
			T t = pool.getAll()[i];
			t.pushBack();
			observers_onPush(t);
		}
	}
	
	/**获取所有元素 可以进行整体操作 */
	public final T[] getAll() {
		return pool.getAll();
	}
	
	/**回收所有池中对象（为其调用remove方法）清理存储空间*/
	public final void dispose() {
		for(int i = 0, n = pool.getAll().length; i < n; ++i ) {
			T t = pool.getAll()[i];
//			t.onCycle();
//			t.remove();
			t._onSleep();
			t.destroySelf();
		}
		clearObserver();
		pool.dispose();
		SPools.removeObjectPool(this, false); //防止反复调用
	}
	
	
	/**展示内容：名称/当前尺寸/堆栈指针位置 <br>
	 * 池类型/类名称/构造函数参数类型/ */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("pool id : "); sb.append(id); sb.append('\n');
		sb.append(pool);
		return sb.toString();
	}
	
	
	
}
