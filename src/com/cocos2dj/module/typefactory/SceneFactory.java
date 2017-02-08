package com.cocos2dj.module.typefactory;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;

import com.badlogic.gdx.utils.Array;

/**实体工厂<p>
 * 
 * 调用 {@link #putActorType(C2ActorType)} ,如果想在放入Type的途中获取对象实体
 * 可以使用 {@link #flush_createInstances(C2GameScene)} 强制创建当前输入实例<p>
 * 
 * 最后由系统在 scene的create方法的最后调用 {@link #end_createInstance(C2GameScene)} 
 * 强制刷新，将所有的类型实例化
 * 
 * 暂时把代理抠了
 * @author xu jun
 * Copyright (c) 2015. All rights reserved. */
public class SceneFactory {
	
	/**字符对象以及实体类型的映射关系 */
	private final HashMap<String, SObjectType> actorTypeMap = new HashMap<String, SObjectType>();
	/**暂时缓存Type类型的数组 */
	private Array<SObjectType> tempArray = new Array<SObjectType>();
	/**存储所有的单例类型实例*/
	private final Array<SObject> singletonInstances = new Array<SObject>();
	/**存储所有的对象池类型实例*/
	private final Array<SObjectPool<? extends SObject>> poolInstances = 
			new Array<SObjectPool<? extends SObject>>();
	/**存放实体代理 非必须信息暂时保留 */
//	private final Array<C2ActorProxy> proxies = new Array<C2ActorProxy>();
	
	
	static final String TAG = "SSceneFactory";
	
	
	public final int getSingletonCount() {
		return singletonInstances.size;
	}
	
	/**遍历处理单例object对象 */
	public final void handleObject(IHandle<SObject> handle) {
		for(int i = 0; i < singletonInstances.size; ++i) {
			handle.onHandle(singletonInstances.get(i));
		}
	}
	
	public final int getPoolCount() {
		return poolInstances.size;
	}
	
	/**遍历处理pool对象 */
	@SuppressWarnings("rawtypes")
	public final void handleObjectPool(IHandle<SObjectPool> handle) {
		for(int i = 0; i < poolInstances.size; ++i) {
			handle.onHandle(poolInstances.get(i));
		}
	}
	
	public final int getTypeSize() {
		return actorTypeMap.size();
	}
	
	/**遍历并处理Type对象 */
	public final void handleActorType(IHandle<SObjectType> handle) {
		Iterator<java.util.Map.Entry<String, SObjectType>> it = actorTypeMap.entrySet().iterator();
		while(it.hasNext()) {
			SObjectType t = it.next().getValue();
			handle.onHandle(t);
		}
	}
	
	public final SSceneFactory putActorType(SObjectType type) {
		return putActorType(type.typeName, type);
	}
	
	public final SSceneFactory putActorType(String key, SObjectType type) {
		if(actorTypeMap.containsKey(key) || key == null) {
			SLog.error("world", TAG, "key error: key = " + key);
			return this;
		}
		type.typeName = key;
		actorTypeMap.put(key, type);
		type.factory = this;
		tempArray = tempArray == null ? new Array<SObjectType>() : tempArray;
		tempArray.add(type);
		return this;
	}
	
	public final SObjectType getActorType(String key) {
		return actorTypeMap.get(key);
	}
	
	public final SSceneFactory flush_createInstances(final SScene scene) {
		for(int i = 0; i < tempArray.size; ++i) {
			final SObjectType type = tempArray.get(i);
			switch(type.instanceType) {
			case SINGLETON:
				type.instanceID = singletonInstances.size;
				singletonInstances.add(createObject(type, scene));
				break;
			case ADDING_POOL: case NORMAL_POOL:
				type.instanceID = poolInstances.size;
				poolInstances.add(createObjectPool(type, scene));
				break;
			}
		}
		tempArray.clear();
		return this;
	}
	
	public final void end_createInstance(final SScene scene) {
		flush_createInstances(scene);
		singletonInstances.ensureCapacity(0);
		poolInstances.ensureCapacity(0);
		tempArray = null;
		
		end_createProxy();
	}
	
	
	final void end_createProxy() {
//		for(int i = 0; i < proxies.size; ++i) {
//			C2ActorProxy p = proxies.get(i);
//			p.buildProxy(this);
//		}
//		proxies.ensureCapacity(0);
	}
	
	
	private SObject createObject(final SObjectType type, final SScene scene) {
		SObject temp = null;
		try {
			if(type.initClasses != null)
				temp = type.clazz.getConstructor(type.initClasses).newInstance(type.initArgs);
			else
				temp = type.clazz.newInstance();
			
			temp.setObjectType(type);
			type.parent.addChild(temp);
			temp._onSleep();
			
			SLog.debug("world", TAG, "create object : " + type);
			
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return temp;
	}
	
	private SObjectPool<? extends SObject> createObjectPool(final SObjectType type, 
			final SScene scene) {
		int poolType;
		if(type.instanceType != InstanceType.ADDING_POOL)
			poolType = 0;
		else
			poolType = 1;
		
		SLog.debug("world", TAG, "create pool : " + type);
//		System.out.println("create pool -----   class = " + type.clazz);
		
//		System.out.println(type.);
		@SuppressWarnings({ "rawtypes", "unchecked" })
		SObjectPool<? extends SObject> pool = new SObjectPool(
				type.parent,
				poolType,
				type.clazz,
				type.poolInitCount,
				type.poolAddCount,
				type.initClasses,
				type.initArgs) {
			public void onCreate(SObject o) {
				o.setObjectType(type);
			}
		};
		
		return pool;
	}
	
	/**按键值获取实体实例 */
	public final SObject getObject(final String name) {
		SObjectType t = actorTypeMap.get(name);
		if(t == null) return null;
		return getObject(t);
	}
	
	/**获取对象池 */
	@SuppressWarnings("rawtypes")
	final SObjectPool getPool(SObjectType at) {
		if(at.instanceType == InstanceType.ADDING_POOL || at.instanceType == InstanceType.NORMAL_POOL) {
			final int id = at.instanceID;
			if(id >= 0) return poolInstances.get(id);
		}
		return null;
	}
	
	/**按指定的实体（或演员Actor）类型获取实例
	 * 
	 * @param at 实体类型(ActorType)
	 * @return 实体的实例 获取失败时返回null */
	final SObject getObject(SObjectType at) {
		switch(at.instanceType) {
		case SINGLETON: 
			{	
				final int id = at.instanceID;
				if(id >= 0) {	//手动唤醒
					SObject ret = singletonInstances.get(id);
					ret._onAwake();
					return ret;
				}
			}
			break;
		case ADDING_POOL: case NORMAL_POOL:
			{
				final int id = at.instanceID;
				if(id >= 0) return poolInstances.get(id).pop();
			}
			break;
		}
		return null;
	}
	
	final void clear() {
		for(SObject o : singletonInstances) {
			o.destroySelf();
		}
		for(SObjectPool<?> pool : poolInstances) {
			pool.dispose();
		}
	}
}
