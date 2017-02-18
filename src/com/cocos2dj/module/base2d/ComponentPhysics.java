package com.cocos2dj.module.base2d;

import com.badlogic.gdx.math.Vector2;
import com.cocos2dj.macros.CC;
import com.cocos2dj.macros.CCLog;
import com.cocos2dj.module.base2d.framework.PhysicsObject;
import com.cocos2dj.module.base2d.framework.PhysicsObjectType;
import com.cocos2dj.module.base2d.framework.callback.UpdateListener;
import com.cocos2dj.module.base2d.framework.collision.ContactCollisionData;
import com.cocos2dj.protocol.IComponent;
import com.cocos2dj.s2d.Node;

/**
 * ComponentPhysics.java
 * <br>CancelContactCallback
 * <br>ContactCreatedCallback
 * <br>ContactPersistedCallback
 * <br>ContactDestroyedCallback
 * <br>ContactCallback
 * <p>
 * 
 * 
 * @author Copyright(c) 2017 xu jun
 */
public class ComponentPhysics extends PhysicsObject implements IComponent, UpdateListener {
	
	public ComponentPhysics() {
		super();
	}
	
	public ComponentPhysics(PhysicsObjectType type) {
		super(type);
	}
	
	public ComponentPhysics(boolean physicsModifer) {
		super();
		_physicsMidifer = physicsModifer;
	}
	
	public ComponentPhysics(PhysicsObjectType type, boolean physicsModifer) {
		super(type);
		_physicsMidifer = physicsModifer;
	}
	
	public final void setOwner(Node owner) {this._owner = owner;}
	public final Node getOwner() {return _owner;}
	public boolean isEnabled() {return _enabled;}
	public void setEnabled(boolean enabled) {_enabled = enabled;}
	public String getName() {return _name;}
	public void setName(String name) {_name = name;}
	
	protected Node 		_owner;
	protected String 	_name;
	protected boolean	_enabled;
	boolean				_physicsMidifer = true;		//是否允许物理引擎修正位置

	
	public ComponentPhysics bindNode(Node node) {
		node.addComponent(this);
		return this;
	}
	
	public void setPhysicsModifer(boolean enable) {
		_physicsMidifer = enable;
	}
	
	//override>>
	@Override
	public void update(float delta) {}

	@Override
	public void onEnter() {
		
	}

	@Override
	public void onExit() {
		
	}
	
	@Override
	public void onSleep() {
		sleep = true;
	}

	@Override
	public void onAwake() {
		sleep = false;
	}

	@Override
	public void onAdd() {
		if(isRemoved() && !checkRemoveFlag()) {
			ModuleBase2d module = CC.GetRunningSceneModule(ModuleBase2d.class);
			if(module != null) {
				module.getCurrentPhysicsScene().add(this);
			} else {
				CCLog.error("ComponentPhysics", "this scene not found physics module");
			}
		}
		
		setUserData(_owner);
		
		if(_physicsMidifer) {		//物理修正
			setPositionUpdateListener(this);
		}
	}

	@Override
	public void onRemove() {
		removeSelf();
		setUserData(null);
		listener = nullUpdateListener;
	}
	
	/*
	 * base2d并不是刚体模拟物理引擎，
	 * rotation不做同步
	 */
	//node更新物理对象位置，physics主动更新
	public void updateObject() {
		final Node parent = _owner.getParent();
		//转换到world坐标中设置
		if(parent != null) {
			Vector2 temp = parent.convertToWorldSpace(_owner.getPosition());
			this.setPosition(temp.x, temp.y);
//			System.out.println("update set node pos >>>" + temp);
		} else {
			Vector2 temp = _owner.getPosition();
			this.setPosition(temp.x, temp.y);
		}
	}
	
	//物理对象更新node位置
	@Override
	public final void onUpdatePosition(PhysicsObject o) {
		//需要转换为world坐标再设置更新
		final Node parent = _owner.getParent();
		if(parent != null) {
			_owner.setPosition(parent.convertToNodeSpace(o.getPosition()));
		} else {
			_owner.setPosition(o.getPosition());
		}
//		_owner._setPhysicsCallFlag();
	}
	
	
	/**将要发生碰撞时调用
	 * @return false 碰撞继续 true取消碰撞 */
	public final boolean cancelContact(PhysicsObject o) {
		return _cancelContactCallback.cancelContact(this, (ComponentPhysics) o);
	}
	
	/**当接触创建时调用 */
	public final void contactCreated(PhysicsObject o, Vector2 MTD, ContactCollisionData data) {
		_contactCreatedCallback.onContactCreated(this, (ComponentPhysics) o, MTD, data);
	}
	/**当接触持续存在时调用 */
	public final void contactPersisted(PhysicsObject o, Vector2 MTD, ContactCollisionData data) {
		_contactPersistedCallback.onContactPersisted(this, (ComponentPhysics) o, MTD, data);
	}
	/**当接触撤销后调用 */
	public final void contactDestroyed(PhysicsObject o, Vector2 MTD, ContactCollisionData data) {
		_contactDestroyedCallback.onContactDestroyed(this, (ComponentPhysics) o, MTD, data);
	}
	
	///////////////////////////////
	public static interface CancelContactCallback {
		public boolean cancelContact(ComponentPhysics self, ComponentPhysics other);
		public static final CancelContactCallback NULL = new CancelContactCallback() {
			public boolean cancelContact(ComponentPhysics self, ComponentPhysics other) {
				return false;
			}
		};
	}
	
	public static interface ContactCreatedCallback {
		public void onContactCreated(ComponentPhysics self, ComponentPhysics other, Vector2 MTD, ContactCollisionData data);
		public static final ContactCreatedCallback NULL = new ContactCreatedCallback() {
			public void onContactCreated(ComponentPhysics self, ComponentPhysics other, Vector2 MTD, ContactCollisionData data) {
			}
		};
	}
	public static interface ContactPersistedCallback {
		public void onContactPersisted(ComponentPhysics self, ComponentPhysics other, Vector2 MTD, ContactCollisionData data);
		public static final ContactPersistedCallback NULL = new ContactPersistedCallback() {
			public void onContactPersisted(ComponentPhysics self, ComponentPhysics other, Vector2 MTD, ContactCollisionData data) {
			}
		};
	}
	public static interface ContactDestroyedCallback {
		public void onContactDestroyed(ComponentPhysics self, ComponentPhysics other, Vector2 MTD, ContactCollisionData data);
		public static final ContactDestroyedCallback NULL = new ContactDestroyedCallback() {
			public void onContactDestroyed(ComponentPhysics self, ComponentPhysics other, Vector2 MTD, ContactCollisionData data) {
			}
		};
	}
	
	public static interface ContactCallback extends ContactCreatedCallback, ContactPersistedCallback, ContactDestroyedCallback {}
	
	CancelContactCallback 			_cancelContactCallback = CancelContactCallback.NULL;
	ContactCreatedCallback			_contactCreatedCallback = ContactCreatedCallback.NULL;
	ContactPersistedCallback		_contactPersistedCallback = ContactPersistedCallback.NULL;
	ContactDestroyedCallback		_contactDestroyedCallback = ContactDestroyedCallback.NULL;
	
	
	public void setCancelContactCallback(CancelContactCallback callback) {
		_cancelContactCallback = callback != null ? callback : CancelContactCallback.NULL; 
	}
	public void setContactCreatedCallback(ContactCreatedCallback callback) {
		_contactCreatedCallback = callback != null ? callback : ContactCreatedCallback.NULL; 
	}
	public void setContactPersistedCallback(ContactPersistedCallback callback) {
		_contactPersistedCallback = callback != null ? callback : ContactPersistedCallback.NULL; 
	}
	public void setContactDestroyedCallback(ContactDestroyedCallback callback) {
		_contactDestroyedCallback = callback != null ? callback : ContactDestroyedCallback.NULL; 
	}
	
	public void setContactCallback(ContactCallback callback) {
		_contactCreatedCallback = callback;
		_contactPersistedCallback = callback;
		_contactDestroyedCallback = callback;
	}
}
