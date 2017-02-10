package com.cocos2dj.module.base2d.framework;

import java.util.Iterator;
import java.util.LinkedList;

import com.cocos2dj.module.base2d.framework.callback.ContactListener;
import com.cocos2dj.module.base2d.framework.collision.Contact;
import com.cocos2dj.module.base2d.framework.collision.ContactAttach;
import com.cocos2dj.module.base2d.framework.collision.ContactList;
import com.cocos2dj.module.base2d.framework.collision.ContactPool;
import com.cocos2dj.module.base2d.framework.collision.Shape;
import com.cocos2dj.module.base2d.framework.common.AABB;
import com.cocos2dj.module.base2d.framework.common.TempArray;
import com.cocos2dj.module.base2d.jbox2d.BroadPhase;
import com.cocos2dj.module.base2d.jbox2d.PairCallback;
import com.cocos2dj.module.base2d.jbox2d.TreeCallback;

/**C2PhysicsScene<p>
 * 
 * 物理场景 用于存放管理物理对象<br>
 * 
 * @author xujun
 * Copyright (c) 2015. All rights reserved. */
public class PhysicsScene implements PairCallback, TreeCallback {
	
	private ContactListener listener;
	private boolean attachSystem;
	private boolean sceneLock;
	private BroadPhase broadPhase;
	
	final TempArray<PhysicsObject> moveObjects;
	final TempArray<PhysicsObject> staticObjects;
	final TempArray<PhysicsObject> detectObjects;
//	final TempArray<PhysicsObject> groupDetectObjects;
	final ContactList contacts;
	/*
	 * 由于在调用addPhysObject方法时可能遇到 该scene未被物理引擎载入的情况
	 * （此时attachSystem标记为false）
	 * 这样无法创建broadPhase
	 * 所以使用该缓存，当为载入时所有的物理对象添加到这个缓存中
	 * 当scene被引擎读入时再向layers添加
	 */
	final LinkedList<PhysicsObject> tempPhysObjects;
	boolean staticObjectsNeedRemove = false;
	boolean moveObjectsNeedRemove = false;
	boolean detectObjectNeedRemove = false;
//	boolean groupDetectObjectNeedRemove = false;
	
	private PhysicsObject temp_currDetect;
	
	
	public PhysicsScene() {
//		listener = new ContactAdapter();
		detectObjects = new TempArray<PhysicsObject>();
		moveObjects = new TempArray<PhysicsObject>();
		staticObjects = new TempArray<PhysicsObject>();
		contacts = new ContactList();
		tempPhysObjects = new LinkedList<PhysicsObject>();
//		groupDetectObjects = new TempArray<PhysicsObject>();
	}
	
	
	public void _setCurrectDetectObject(PhysicsObject obj) {
		this.temp_currDetect = obj;
	}
	
	public final TempArray<PhysicsObject> _getMoveObjects() {
		return moveObjects;
	}
	
	public final TempArray<PhysicsObject> _getStaticObjects() {
		return staticObjects;
	}
	
	public final TempArray<PhysicsObject> _getDetectObjects() {
		return detectObjects;
	}
	
	public final BroadPhase _getBroadPhase() {
		return broadPhase;
	}
	
	public final Contact _getContacts() {
		return contacts.first();
	}
	
	
	static final AABB poolAABB = new AABB();
	/**计算扫掠矩形并创建代理（添加时必须手动计算sweepAABB）*/
	private void createProxy(final PhysicsObject obj) {
		
		Shape shape = obj.getShapeList();
		
		shape.computeAABB(obj.sweepAABB, obj.getPosition());
		shape = shape.next;
		
		while(shape != null){
			//计算下一个连接形状移动前后的AABB
			shape.computeAABB(poolAABB, obj.getPosition());
			obj.sweepAABB.combine(poolAABB);
			shape = shape.next;
		}
		
//		obj.sweepAABB.lowerBound.addLocal(obj.getPosition());
//		obj.sweepAABB.upperBound.addLocal(obj.getPosition());
		obj.setProxyID(broadPhase.createProxy(obj.getSweepAABB(), obj));
//		System.out.println("aabb = " + obj.getSweepAABB());
//		obj.setProxyID(broadPhase.createProxy(obj.getShapeAABB(), obj));
	}
	
	/**查看该场景是否已装载到系统中
	 * @return true 已经装载
	 * false 未装载*/
	public boolean isAttachSystem(){
		return this.attachSystem;
	}
	
	/**设置attachsystem属性
	 * @param attachSystem	true 已经装载
	 * false 未装载*/
	public void setAttachSystem(boolean attachSystem){
		this.attachSystem = attachSystem;
	}
	
	/**检查场景锁 
	 * 当调用step是会锁住场景不可进行添加/删除物体以及改变物体的操作
	 * @return true = 场景物体不可动
	 * false = 场景物体可动 */
	public boolean isSceneLock() {
		return sceneLock;
	}
	
	/**设置场景锁 
	 * 当调用step是会锁住场景不可进行添加/删除物体的操作
	 * 不要自行调用该方法
	 * @param sceneLock */
	public void setSceneLock(boolean sceneLock) {
		this.sceneLock = sceneLock;
	}
	
	public final void setContactListener(ContactListener listener){
		this.listener = listener;
	}
	
	public final ContactListener getContactListener() {
		return listener;
	}
	
	/**
	 * 将scene添加到physics系统中的初始化动作
	 * @param broadPhase
	 */
	public final void _initPhysicsScene(BroadPhase broadPhase) {
		this.broadPhase = broadPhase;
		handleTempObjects();
	}
	
	/**处理缓存的物理对象 <p>
	 * 
	 * 该方法在添加scene时调用（具体在Simulator的initPhysScene中调用） */
	private final void handleTempObjects() {
		if(!tempPhysObjects.isEmpty()) {
			Iterator<PhysicsObject> it = tempPhysObjects.iterator();
			while(it.hasNext()) {
				_add(it.next());
			}
			//遍历完直接清空即可
			tempPhysObjects.clear();
		}
	}
	
	
	/**向场景添加对象 在添加前应该保证physicsObject以初始化完毕
	 * @param obj
	 * @return <code>true 成功添加  false 由于场景加锁添加失败*/
//	public final boolean addPhysicsObject(final PhysicsObject obj) {
//		return addPhysicsObject(obj, obj.getLayerID());
//	}
	public final void remove(final PhysicsObject obj) {
		//设置移除标记 
		obj._clearFlag();
		obj.setRemoveFlag();
		
		switch(obj.getPhysicsObjectType()) {
		case Detect:
			detectObjectNeedRemove = true;
		case Dynamic:
		case Move:
			moveObjectsNeedRemove = true;
			break;
		case Static:
			staticObjectsNeedRemove = true;
			break;
		}
		
		obj.setAccelerate(0, 0);
		obj.setVelocity(0, 0);
	}
	
	public final boolean _add(final PhysicsObject obj){
//		if(obj.scene == this) {
//			throw new RuntimeException(" ----- PhysObject已添加到scene : " + " 不能重复添加对象");
//		}
		
		obj.scene = this;
		obj.clearRemoveFlag();
		switch(obj.getPhysicsObjectType()) {
		case Dynamic:
				createProxy(obj);
		case Move:
				moveObjects.add(obj);
				break;
		case Static:
				createProxy(obj);
				staticObjects.add(obj);
				break;
		case Detect:
				moveObjects.add(obj);
				detectObjects.add(obj);
				break;
		}
		return true;
	}
	
	/**向场景添加对象 在添加前应该保证physicsObject以初始化完毕
	 * @param obj
	 * @return <code>true 成功添加  false 由于场景加锁添加失败*/
	public final boolean add(final PhysicsObject obj) {
		if(obj.scene == this) {
			System.out.println("重复添加 -------add(PhysicsObject obj)");
			return false;
		}
		if(obj.checkRemoveFlag() && !obj.isRemoved()) {
			//正在移除中，不操作
			return false;
		}
		
		if(this.attachSystem && !this.sceneLock) {
			_add(obj);
		}
		else {
			tempPhysObjects.add(obj);
			return false;
		}
		return true;
	}
	


	private void innerDestroyContact(Contact c) {
		if (c.nodeA.prev != null) {c.nodeA.prev.next = c.nodeA.next;}
		if (c.nodeA.next != null) {c.nodeA.next.prev = c.nodeA.prev;}
		if (c.nodeA == c.o1.getContactList()) {c.o1._setContactList(c.nodeA.next);}
		
		// Remove from body 2
		if (c.nodeB.prev != null) {c.nodeB.prev.next = c.nodeB.next;}
		if (c.nodeB.next != null) {c.nodeB.next.prev = c.nodeB.prev;}
		if (c.nodeB == c.o2.getContactList()) {c.o2._setContactList(c.nodeB.next);}
		
		c.contactData.clear();
		c.clearRemove();
	}

	
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("move: \n").append(moveObjects).append('\n');
		sb.append("static: \n").append(staticObjects).append('\n');
		sb.append("detect: \n").append(detectObjects).append('\n');
//		sb.append("motor: \n").append("null").append('\n');
		sb.append("contacts: \n").append(contacts.toSimpleString()).append('\n');
		sb.append("broadPhase \n proxyCount = ").append(broadPhase.getProxyCount()).append(
				"  height = ").append(broadPhase.getTreeHeight()).append('\n');
		
		return sb.toString();
	}

	
	/**清理场景相关数据 方便gc工作 */
	public final void release() {
		//清空contact对象并且
		Contact c = contacts.first();
		while(c != null) {
			innerDestroyContact(c);
			c = c.list_next;
		}
		contacts.clear();
		
		//清空physObject对象
		moveObjects.clear();
		staticObjects.clear();
		detectObjects.clear();
//		staticObjectList.clear();
	}
	
	/**销毁该 PhysObject对象及其关联内容(删除相关的contact)<p>
	 * 
	 * <b>该方法值为关联contact设置“清除标记” 实际清除工作在管线中遍历时进行</b>
	 * @param o */
	final void destroyPhysObject(final PhysicsObject obj) {
//		obj.unattachAllPhysicsObject();
		obj.clearRemoveFlag();
		
		if(obj.getProxy() != PhysicsObject.NULL_PROXY) {
			broadPhase.destroyProxy(obj.getProxy());
		}
		
		ContactAttach ca = obj.getContactList();
		while(ca != null){
			//为所有的contact设置清理标志
			ca.contact.setRemove();
			ca = ca.next;
		}
		
		obj.scene = null;
	}
	
	public final void updateSceneObjects() {
		//delete objects
//		System.out.println("states = " + moveObjectsNeedRemove + " " + detectObjectNeedRemove);
		if(detectObjectNeedRemove) {
			updateObjectsArray(false, detectObjects);
			detectObjectNeedRemove = false;
		}
		
		if(moveObjectsNeedRemove) {
			updateObjectsArray(true, moveObjects);
			moveObjectsNeedRemove = false;
		}
		
		if(staticObjectsNeedRemove) {
			updateObjectsArray(true, staticObjects);
			staticObjectsNeedRemove = false;
		}

		
		//handle temp
		handleTempObjects();
	}
	
	final void updateObjectsArray(boolean end, final TempArray<PhysicsObject> targetArray) {
		final Object[] elements = targetArray.beginRemove();
		
//		System.out.println("dfhadsfhdsiufydsunfdshfidshfladhflashflashflaf");
		for(int i = 0, n = targetArray.size(); i < n; ++i) {
			PhysicsObject o = (PhysicsObject)elements[i];
			if(!o.checkRemoveFlag()) {
				targetArray.keepElement(i);
			}
			else {
				if(end) {
					destroyPhysObject(o);
				}
			}
		}
		targetArray.endRemove();
	}
	
	public final void _createContact(
			final PhysicsObject o1, final PhysicsObject o2, final boolean aabbTest) {
		//判断o1与o2中已有该碰撞记录
		ContactAttach c = o1.getContactList();
		while(c != null) {
			if(c.other == o2){
				//接触已创建
				return;
			}
			c = c.next;
		}
		
		//创建一个接触
		final Contact contact = ContactPool.getContactPool().get();
		
		if(aabbTest) {
			contact.setAABBTest();		//detect物体
		}
		
		contact.setPhysicsObject(o1, o2);
		
		//contct connnect o1
		contact.nodeA.contact = contact;
		contact.nodeA.other = o2;
		
		contact.nodeA.prev = null;
		contact.nodeA.next = o1.getContactList();
		if(o1.getContactList() != null) {
			o1.getContactList().prev = contact.nodeA;
		}
		o1._setContactList(contact.nodeA);
		
		//contact connect o2
		contact.nodeB.contact = contact;
		contact.nodeB.other = o1;
		
		contact.nodeB.prev = null;
		contact.nodeB.next = o2.getContactList();
		if(o2.getContactList() != null){
			o2.getContactList().prev = contact.nodeB;
		}
		o2._setContactList(contact.nodeB);

		contacts.addContact(contact);
	}

	/**删除contact 该方法由PipeSolverContact对象在移除contact时调用<br>
	 * 
	 * 这个方法只是将contact与physicsObject分离而不会从contacts中删除
	 * 删除contact需要在迭代中调用remove方法
	 * @param 接触监听 
	 * @param c */
	public final void _destroyContact(final Contact c) {
		final PhysicsObject o1 = c.getPhysicsObject1();
		final PhysicsObject o2 = c.getPhysicsObject2();
		
		//
		contacts.removeContact(c);
		c.clearFlag();
		
		//处理contactListener
		if(c.isContacted()){
			listener.contactDestroyed(o1, o2, c.MTD, c.contactData);
		}
		
		// Remove from body 1
		if (c.nodeA.prev != null) {
			c.nodeA.prev.next = c.nodeA.next;
		}
		
		if (c.nodeA.next != null) {
			c.nodeA.next.prev = c.nodeA.prev;
		}
		
		if (c.nodeA == o1.getContactList()) {
			o1._setContactList(c.nodeA.next);
		}
		
		// Remove from body 2
		if (c.nodeB.prev != null) {
			c.nodeB.prev.next = c.nodeB.next;
		}
		
		if (c.nodeB.next != null) {
			c.nodeB.next.prev = c.nodeB.prev;
		}
		
		if (c.nodeB == o2.getContactList()) {
			o2._setContactList(c.nodeB.next);
		}
		
		//这个方法中做了contact状态的处理工作
		ContactPool.getContactPool().cycle(c);
		
	}
	
	/**
	 * 突然想到将addPair放到这里岂不是更好？
	 */
	@Override
	public final void addPair(final Object userDataA, final Object userDataB) {
		final PhysicsObject o1 = ((PhysicsObject)userDataA);
		final PhysicsObject o2 = ((PhysicsObject)userDataB);

		//睡眠物体过滤
		if(o1.isSleep()||o2.isSleep()){
			return;
		}
		//静态物体不添加接触
		if(o1.isStaticObject() && o2.isStaticObject()){
			return;
		}
		//碰撞过滤
		if(!o1.getContactFilter().testFilter(o2.getContactFilter())){
			return;
		}
		
		_createContact(o1, o2, false);
		//碰撞过滤
		//TODO 所有的contactListener都通过Scene获取
//		if(scene.getContactListener().cancelContant(o1, o2)) {
//			return;
//		}
//		System.out.println("cancel after");
	}

	@Override
	public boolean treeCallback(int proxyId) {
//		broadPhase.getUserData(proxyId);
//		final int queryID = temp_currDetect.getProxy();
//		if(queryID == proxyId) {
//			return true;
//		}
//		if (proxyId < queryID) {
//			addPair(broadPhase.getUserData(proxyId), this.temp_currDetect);
//		}
//		else {
//		addPair(this.temp_currDetect, broadPhase.getUserData(proxyId));
		final PhysicsObject o1 = temp_currDetect;
		final PhysicsObject o2 = ((PhysicsObject)broadPhase.getUserData(proxyId));

		//睡眠物体过滤
		if(o1.isSleep()||o2.isSleep()){
			return true;
		}
		//静态物体不添加接触
		if(o1.isStaticObject() && o2.isStaticObject()){
			return true;
		}
		//碰撞过滤
		if(!o1.getContactFilter().testFilter(o2.getContactFilter())){
			return true;
		}
		
		_createContact(o1, o2, true);
		return true;
	}
	
}