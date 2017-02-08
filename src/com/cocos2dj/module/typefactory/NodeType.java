package com.cocos2dj.module.typefactory;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.cocos2dj.protocol.INodeType;
import com.cocos2dj.s2d.Node;

/**
 * NodeType.java
 * <p>
 * 
 * 一个NodeType对象管理一类node<pre>
 * 
 * 
 * Node node = type.getInstance();	//获取实例
 * node.pushBack();					//归还实例
 * </pre>
 * @author Copyright (c) 2014. xu jun 
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
	
//	/**为对象池中的所有对象添加监听 */
//	public final void addObserver(SObjectObserver obv) {
//		factory.getPool(this).addObserver(obv);
//	}
//	
//	public final void removeObserver(SObjectObserver observer) {
//		factory.getPool(this).removeObserver(observer);
//	}
//	
//	public final void clearObserver() {
//		factory.getPool(this).clearObserver();
//	}
	
	
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
	Node 					parent;
}
