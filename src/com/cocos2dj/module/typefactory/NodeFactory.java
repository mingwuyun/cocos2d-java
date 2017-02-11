package com.cocos2dj.module.typefactory;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;

import com.badlogic.gdx.utils.Array;
import com.cocos2dj.macros.CCLog;
import com.cocos2dj.module.typefactory.NodeType.InstanceType;
import com.cocos2dj.protocol.IFunctionOneArg;
import com.cocos2dj.s2d.Node;
import com.cocos2dj.s2d.Scene;

/**实体工厂<p>
 * 
 * 调用 {@link #putActorType(C2ActorType)} ,如果想在放入Type的途中获取对象实体
 * 可以使用 {@link #flush_createInstances(C2GameScene)} 强制创建当前输入实例<p>
 * 
 * 最后由系统在 scene的create方法的最后调用 {@link #end_createInstance(C2GameScene)} 
 * 强制刷新，将所有的类型实例化
 * 
 * @author Copyright (c) 2015 xu jun
 * */
public class NodeFactory {
	
	/**字符对象以及实体类型的映射关系 */
	private final HashMap<String, NodeType> actorTypeMap = new HashMap<String, NodeType>();
	/**暂时缓存Type类型的数组 */
	private Array<NodeType> tempArray = new Array<NodeType>();
	/**存储所有的单例类型实例*/
	private final Array<Node> singletonInstances = new Array<Node>();
	/**存储所有的对象池类型实例*/
	private final Array<NodePool<? extends Node>> poolInstances = 
			new Array<NodePool<? extends Node>>();
	
	
	static final String TAG = "NodeFactory";
	
	
	public final int getSingletonCount() {
		return singletonInstances.size;
	}
	
	/**遍历处理单例object对象 */
	public final void handleObject(IFunctionOneArg<Node> handle) {
		for(int i = 0; i < singletonInstances.size; ++i) {
			handle.callback(singletonInstances.get(i));
		}
	}
	
	public final int getPoolCount() {
		return poolInstances.size;
	}
	
	/**遍历处理pool对象 */
	@SuppressWarnings("rawtypes")
	public final void handleObjectPool(IFunctionOneArg<NodePool> handle) {
		for(int i = 0; i < poolInstances.size; ++i) {
			handle.callback(poolInstances.get(i));
		}
	}
	
	public final int getTypeSize() {
		return actorTypeMap.size();
	}
	
	/**遍历并处理Type对象 */
	public final void handleActorType(IFunctionOneArg<NodeType> handle) {
		Iterator<java.util.Map.Entry<String, NodeType>> it = actorTypeMap.entrySet().iterator();
		while(it.hasNext()) {
			NodeType t = it.next().getValue();
			handle.callback(t);
		}
	}
	
	public final NodeFactory putActorType(NodeType type) {
		return putActorType(type.typeName, type);
	}
	
	public final NodeFactory putActorType(String key, NodeType type) {
		if(actorTypeMap.containsKey(key) || key == null) {
			CCLog.engine(TAG, "key error: key = " + key);
			return this;
		}
		type.typeName = key;
		actorTypeMap.put(key, type);
		type.factory = this;
		tempArray = tempArray == null ? new Array<NodeType>() : tempArray;
		tempArray.add(type);
		return this;
	}
	
	public final NodeType getActorType(String key) {
		return actorTypeMap.get(key);
	}
	
	public final NodeFactory flush_createInstances(final Scene scene) {
		for(int i = 0; i < tempArray.size; ++i) {
			final NodeType type = tempArray.get(i);
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
	
	public final void end_createInstance(final Scene scene) {
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
	
	
	private Node createObject(final NodeType type, final Scene scene) {
		Node temp = null;
		try {
			if(type.initClasses != null)
				temp = type.clazz.getConstructor(type.initClasses).newInstance(type.initArgs);
			else
				temp = type.clazz.newInstance();
			temp._setNodeType(type);
			type.parent.addChild(temp);
			temp.onSleep();
			CCLog.engine(TAG, "create object : " + type);
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
	
	private NodePool<? extends Node> createObjectPool(final NodeType type, final Scene scene) {
		int poolType;
		if(type.instanceType != InstanceType.ADDING_POOL)
			poolType = 0;
		else
			poolType = 1;
		
		CCLog.engine(TAG, "create pool : " + type);
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		NodePool<? extends Node> pool = new NodePool(
				type.parent,
				poolType,
				type.clazz,
				type.poolInitCount,
				type.poolAddCount,
				type.initClasses,
				type.initArgs) {
			public void onCreate(Node o) {
				if(type.proxyClazz != null) {
					try {
						o.setNodeProxy(type.proxyClazz.newInstance());
					} catch (InstantiationException | IllegalAccessException e) {
						e.printStackTrace();
					}
				}
				o._setNodeType(type);
			}
		};
		
		if(type.poolListener != null) {
			pool.addPoolListener(type.poolListener);
		}
		return pool;
	}
	
	/**按键值获取实体实例 */
	public final Node getObject(final String name) {
		NodeType t = actorTypeMap.get(name);
		if(t == null) return null;
		return getObject(t);
	}
	
	/**获取对象池 */
	@SuppressWarnings("rawtypes")
	final NodePool getPool(NodeType at) {
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
	final Node getObject(NodeType at) {
		switch(at.instanceType) {
		case SINGLETON: 
			{	
				final int id = at.instanceID;
				if(id >= 0) {	//手动唤醒
					Node ret = singletonInstances.get(id);
					ret.onAwake();
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
	
	/**清除factory中所有被管理对象 不要手动调用 */
	public final void clear() {
		for(Node o : singletonInstances) {
			o.removeFromParent();
		}
		for(NodePool<?> pool : poolInstances) {
			pool.dispose();
		}
	}
}
