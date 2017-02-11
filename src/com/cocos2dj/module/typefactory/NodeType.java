package com.cocos2dj.module.typefactory;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.cocos2dj.protocol.INode;
import com.cocos2dj.protocol.INode.NodeProxy;
import com.cocos2dj.protocol.INodeType;
import com.cocos2dj.s2d.Node;

/**
 * NodeType.java
 * <p>
 * 
 * 一个NodeType对象管理一类node 有两种用法：<pre>
 * //use node class
 * public TargetNode extends Node {
 * 	...
 * }
 * NodeType type = NodeType.create("testName").setParent(scene).setClass(TargetNode.class);
 * 
 * //use node proxy (good way)
 * public TargetNodeProxy implements NodeProxy {
 * 	...
 * }
 * NodeType type = NodeType.create("testName").setParent(scene).setNodeProxy(TargetNodeProxy.class);
 * 
 * Node node = type.getInstance();	//获取实例
 * node.pushBack();			//归还实例
 * </pre>
 * @author Copyright (c) 2014-2017. xu jun 
 * */
public class NodeType implements INodeType {
	
	public enum InstanceType {
		/**单例类型 该type在一个scene中只会存在一个*/ 
		SINGLETON,		
		/**对象池类型 数量不够时线性增加 */ 
		ADDING_POOL,
		/**对象池类型 数量不够时指数增加 */ 
		NORMAL_POOL
	}
	
	
	public static NodeType create() {
		return new NodeType();
	}
	
	public static NodeType create(String name) {
		NodeType type = new NodeType();
		type.setName(name);
		return type;
	}
	
	public String getTypeName() {return typeName;}
	
	public final NodeType setParent(Node parent) { 
		this.parent = parent;
		return this;
	}
	
	public final NodeType setName(String name) {
		this.typeName = name;
		return this;
	}
	
	public final NodeType setInstanceType(InstanceType type) {
		this.instanceType = type;
		return this;
	}
	
	public final NodeType setInitCount(int initCount, int addCount) {
		this.poolInitCount = initCount;
		this.poolAddCount = addCount;
		return this;
	}
	
	public final NodeType setClass(Class<? extends Node> clazz) {
		this.clazz = clazz;
		return this;
	}
	
	public final NodeType setArgs(Class<?>[] params, Object[] args) {
		this.initClasses = params;
		this.initArgs = args;
		return this;
	}
	
	public final NodeType setNodeProxy(Class<? extends NodeProxy> proxyClazz) {
		this.proxyClazz = proxyClazz;
		return this;
	}
	
	public final NodeType setPoolListener(PoolListener listener) {
		this.poolListener = listener;
		return this;
	}
	
	
	
	/**
	 * 获取该type对应的node实例 <p>
	 * 
	 * 调用node.pushBack() 归还对象
	 * @return Node */
	public final Node getInstance() {
		Node a = factory.getObject(this);
		if(a == null) {
			throw new GdxRuntimeException("getInstance fail: " + this);
		}
		return a;
	}
	
	/**为对象池中的所有对象添加监听 */
	public final PoolListener addPoolListener(PoolListener obv) {
		factory.getPool(this).addPoolListener(obv);
		return obv;
	}
	
	public final void removePoolListener(PoolListener observer) {
		factory.getPool(this).removePoolListener(observer);
	}
	
	public final void clearPoolListener() {
		factory.getPool(this).clearObserver();
	}
	
	
	@Override
	public void pushSingletonNode(INode node) {
		
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("typeName: "); sb.append(typeName);
		sb.append(" instanceID: "); sb.append(instanceID);
		return sb.toString();
	}

	
	//fields>>
	NodeFactory 			factory;
	String 					typeName = "null";
	int 					instanceID = -1;
	InstanceType 			instanceType = InstanceType.SINGLETON;
	Class<? extends Node> 	clazz = Node.class;
	Class<?>[] 				initClasses;
	Object[] 				initArgs;
	int 					poolInitCount = 2;
	int 					poolAddCount = 2;
	PoolListener			poolListener;
	Node 					parent;
	
	Class<? extends NodeProxy>		proxyClazz;
}
