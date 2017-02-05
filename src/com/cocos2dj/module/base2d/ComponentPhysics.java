package com.cocos2dj.module.base2d;

import com.badlogic.gdx.math.Vector2;
import com.cocos2dj.module.base2d.framework.PhysicsObject;
import com.cocos2dj.module.base2d.framework.PhysicsObjectType;
import com.cocos2dj.module.base2d.framework.callback.UpdateListener;
import com.cocos2dj.protocol.IComponent;
import com.cocos2dj.protocol.INode;
import com.cocos2dj.s2d.Node;

/**
 * ComponentPhysics.java
 * <p>
 * 
 * @author Copyright(c) 2017 xu jun
 */
public class ComponentPhysics extends PhysicsObject implements IComponent, INode.OnTransformCallback, UpdateListener {

	public ComponentPhysics() {
		super();
	}
	
	public ComponentPhysics(PhysicsObjectType type) {
		super(type);
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
	
	@Override
	public void update(float delta) {
		
	}

	@Override
	public void onEnter() {
		sleep = false;
	}

	@Override
	public void onExit() {
		sleep = true;
	}

	@Override
	public void onAdd() {
		_owner.setOnTransformCallback(this);
		setUserData(this);
		listener = this;
	}

	@Override
	public void onRemove() {
		_owner.setOnTransformCallback(null);
		setUserData(null);
		listener = null;
	}
	
//	public void setPosition(float x, float y) {
//		_owner.setPosition(x, y);
//		super.setPosition(positionX, positionY);
////		super.setpositionX();
//	}
	
	//node更新物理对象位置
	@Override
	public final void onTransform(INode n) {
		final Node parent = _owner.getParent();
		//转换到world坐标中设置
		if(parent != null) {
			Vector2 temp = parent.convertToWorldSpace(_owner.getPosition());
			this.setPosition(temp.x, temp.y);
//			System.out.println("update fuck 2 " + temp);
			
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
		_owner._setPhysicsCallFlag();
	}
}
