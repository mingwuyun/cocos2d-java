package com.cocos2dj.module.typefactory;

import com.badlogic.gdx.utils.Array;
import com.cocos2dj.module.typefactory.PoolListener.PoolEvent;
import com.cocos2dj.protocol.INode;
import com.cocos2dj.protocol.INodePool;
import com.cocos2dj.s2d.Node;
import com.cocos2dj.utils.IObjectPool;
import com.cocos2dj.utils.ObjectPool;
import com.cocos2dj.utils.ObjectPoolLinear;

/**
 * 对象池<p>
 * 如果不加typePool参数则默认的对象池实现为AddingObjectPool（扩容是增加指定数量）
 * 
 * 增加了监听相关接口
 * 
 * @author Copyright (c) 2012-2016. xujun 
 */
public class NodePool<T extends Node> implements INodePool {

	public static final int NORMAL_POOL = 0;
	public static final int ADDING_POOL = 1;
	
	private int 			id = -1;
	private String 			name;
	private IObjectPool<T> 	pool;
	private final Node 		parent;
	private final Array<PoolListener>	observers = new Array<>();	//观察者
	
	
	public void addPoolListener(PoolListener observer) {
		observers.add(observer);
	}
	
	public void removePoolListener(PoolListener observer) {
		observers.removeValue(observer, true);
	}
//	
	public void clearObserver() {
		observers.clear();
	}
	
	final void observers_onPop(T t) {
		for(int i = observers.size - 1; i >= 0; --i) {
			final PoolListener obv = observers.get(i);
			final boolean ret = obv.onObjectEvent(PoolEvent.Pop, t);
			if(ret) {
				observers.removeIndex(i);
			}
		}
	}
	
	final void observers_onPush(T t) {
		for(int i = observers.size - 1; i >= 0; --i) {
			final PoolListener obv = observers.get(i);
			final boolean ret = obv.onObjectEvent(PoolEvent.Push, t);
			if(ret) {
				observers.removeIndex(i);
			}
		}
	}
	
	public void setName(String name) {this.name = name;}
	public String getName() {return name;}
	public int getPoolID() {return id;}
	
	//ID的设置只能由Pools进行 所以这个为包访问权限
	final void setPoolID(int id) {this.id = id;}
	
	/**对象池为AddingObjectPool
	 * @param clazz 放入池中对象的类 
	 * @param argInitSize 初始对象的数量 与每次扩容的数量（poolType=1）*/
	public NodePool(Node parent, Class<T> clazz, int initSize) {
		this(parent, 1, clazz, initSize);
	}
	
	/**@param poolType 池的类型，与扩容方式有关  <code>0=扩两倍 1=增加固定数量</code>
	 * @param clazz 放入池中对象的类 
	 * @param argInitSize 初始对象的数量 与每次扩容的数量（poolType=1）*/
	public NodePool(Node parent, int poolType, Class<T> clazz, int initSize) {
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
	public NodePool(Node parent, int poolType, Class<T> clazz, int argInitSize, int argAddCount) {
		this(parent, poolType, clazz, argInitSize, argAddCount, null, null);
	}
	
	/**@param clazz 放入池中对象的类 
	 * @param poolType 池的类型，与扩容方式有关  <code>0=扩两倍 1=增加固定数量</code>
	 * @param argInitSize 初始对象的数量 以及扩容的增加数量
	 * 假设初始数量为10,如果类型为0则扩容2次后为10*2*2=40,而类型为1则扩容两次后为10+10+10=30.
	 * @param argParam 构造函数的参数类型
	 * @param argArgs 构造函数的参数*/
	public NodePool(Node parent, Class<T> clazz, int poolType, int argInitSize, Class<?>[] argParam, Object[] argArgs){
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
	public NodePool(Node argparent, int poolType, Class<T> clazz, int argInitSize, int argAddCount,
			Class<?>[] argParam, final Object[] argArgs) {
		this.parent = argparent;
		if(parent == null) {
			throw new IllegalArgumentException("parent argment is null! ");
		}
		switch(poolType) {
		case NORMAL_POOL:
			pool = new ObjectPool<T>(clazz, argInitSize, argParam, argArgs) {
				public void onCreated(T t){
					t._setNodePool(NodePool.this);	//先设置为pool对象
					t._setInPool(true);
					onCreate(t);
					
					parent.addChild(t);
					t.onSleep();
				}
				public void onPop(T t){
					t._setInPool(false);
					t.onAwake();
				}
				public void onPush(T t){
					t._setInPool(true);
					t.onSleep();
				}
			};
			break;
		case ADDING_POOL:
			pool = new ObjectPoolLinear<T>(clazz, argInitSize, argAddCount, argParam, argArgs){
				public void onCreated(T t){
					t._setNodePool(NodePool.this);
					t._setInPool(true);
					onCreate(t);
					
					parent.addChild(t);
					t.onSleep();
				}
				
				public void onPop(T t){
					t._setInPool(false);
					t.onAwake();
				}
				
				public void onPush(T t){
					t._setInPool(true);
					t.onSleep();
				}
			};
			break;
		}
		
		name = clazz.getName();
		NodePools.addObjectPool(this);
	}
	
	/**为了在Pool层面进行初始化操作设置的方法 */
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
			t.onSleep();
			t.removeFromParent();
		}
		clearObserver();
		pool.dispose();
		NodePools.removeObjectPool(this, false); //防止反复调用
	}
	
	
	/**展示内容：名称/当前尺寸/堆栈指针位置 <br>
	 * 池类型/类名称/构造函数参数类型/ */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("pool id : "); sb.append(id); sb.append('\n');
		sb.append(pool);
		return sb.toString();
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public final void pushPoolNode(INode node) {
		push((T) node);
	}
}
