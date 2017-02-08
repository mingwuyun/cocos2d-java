package com.cocos2dj.protocol;

import com.cocos2dj.s2d.Node;

/**
 * Component.java
 * <p>
 * 
 * @author Copyright (c) 2017 xu jun
 */
public interface IComponent {

	/*
	 * 以下方法按照协议实现
	 */
	public void setOwner(Node owner);
	
	public Node getOwner();
	
	public boolean isEnabled();
	
	public void setEnabled(boolean enabled);
	
	public String getName();
	public void setName(String name);
	
//	public final void setOwner(Node owner) {this._owner = owner;}
//	public final Node getOwner() {return _owner;}
//	public boolean isEnabled() {return _enabled;}
//	public void setEnabled(boolean enabled) {_enabled = enabled;}
//	public String getName() {return _name;}
//	public void setName(String name) {_name = name;}
//	protected Node 		_owner;
//	protected String 	_name;
//	protected boolean	_enabled;
	
	
	public void update(float delta);
	public void onEnter();
	public void onExit();
	public void onAdd();
	public void onRemove();

	//pools
	public void onSleep();
	public void onAwake();
}
