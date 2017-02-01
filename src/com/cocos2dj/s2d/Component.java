package com.cocos2dj.s2d;

/**
 * Component.java
 * <p>
 * 
 * @author Copyright (c) 2017 xu jun
 */
public class Component {
	
	public void init() {
		
	}

	public final void setOwner(Node owner) {
		this._owner = owner;
	}
	public final Node getOwner() {
		return _owner;
	}
	
	public boolean isEnabled() {return _enabled;}
	public void setEnabled(boolean enabled) {
		_enabled = enabled;
	}
	
	public String getName() {return _name;}
	public void setName(String name) {
		_name = name;
	}
	
	public void update(float delta) {}
//	public void serialize();
	public void onEnter() {}
	public void onExit() {}
	public void onAdd() {}
	public void onRemove() {}
	
	
	//fields>>
	protected Node 		_owner;
	protected String 	_name;
	protected boolean	_enabled;
	//fields<<
}
