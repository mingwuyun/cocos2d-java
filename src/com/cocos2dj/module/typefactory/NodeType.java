package com.cocos2dj.module.typefactory;

import com.badlogic.gdx.utils.GdxRuntimeException;

/**ʵ������<p>
 * 
 * ���ƾ����һ��C2Actor�����Ӧ�Ѹ�C2ActorType(û���ֶ�����Ĭ��ΪNULL����)
 * 
 * @author xu jun
 * Copyright (c) 2014. All rights reserved. */
public class NodeType {
	
	/**ʵ������*/
	public enum InstanceType {
		/**����ģʽ ��ǰ����ֻ�����һ��ʵ��*/ SINGLETON,		
		/**�ӷ������ �����ɻ��ն��� ����ʱ�̶���������*/ ADDING_POOL,
		/**��ͨ����� �����ɻ��ն��� ����ʱ��������*/ NORMAL_POOL
	}
	
	/**���͵����ڹ��� */
	SSceneFactory factory;
	/**���͵����� */
	String typeName = "null";
	/**ʵ��λ�ã�ID��*/
	int instanceID = -1;
	/**ʵ���Ĵ������� */
	InstanceType instanceType = InstanceType.SINGLETON;
	/**ʵ�������Ͷ��� */
	Class<? extends SObject> clazz;
	/**��������������� */
	Class<?>[] initClasses;
	/**��������������� */
	Object[] initArgs;
	/**������������ */
	int poolInitCount = 2, poolAddCount = 2;
	SObject parent;
	/**�����͵Ĵ��� */
//	C2ActorProxy actorProxy;
	
	
//	public final C2ActorType setProxy(C2ActorProxy proxy) {
//		actorProxy = proxy;
//		return this;
//	}
	
	public String getTypeName() {
		return typeName;
	}
	
	public final NodeType setParent(SObject parent) {
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
	
	public final NodeType setClass(Class<? extends SObject> clazz) {
		this.clazz = clazz;
		return this;
	}
	
	public final NodeType setArgs(Class<?>[] params, Object[] args) {
		this.initClasses = params;
		this.initArgs = args;
		return this;
	}
	
	
	
	/**��ȡ��ʵ�壨��Ա�����͵�ʵ�� ��ȡʧ�ܻ��׳��쳣<p>
	 * 
	 * @return  ʵ������ */
	public final SObject getInstance() {
		SObject a = factory.getObject(this);
		if(a == null) {
			throw new GdxRuntimeException("getInstance fail: " + this);
		}
		return a;
	}
	
	/**为对象池中的所有对象添加监听 */
	public final void addObserver(SObjectObserver obv) {
		factory.getPool(this).addObserver(obv);
	}
	
	public final void removeObserver(SObjectObserver observer) {
		factory.getPool(this).removeObserver(observer);
	}
	
	public final void clearObserver() {
		factory.getPool(this).clearObserver();
	}
	
//	/**��ȡ���͵Ĵ��� */
//	public final C2ActorProxy getActorProxy() {
//		return actorProxy;
//	}
	
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("typeName: "); sb.append(typeName);
		sb.append(" instanceID: "); sb.append(instanceID);
		return sb.toString();
	}
}
