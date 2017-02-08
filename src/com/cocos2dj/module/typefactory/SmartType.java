package com.cocos2dj.module.typefactory;

import com.cocos2dj.s2d.Node;

public class SmartType {
	
	private String 	key;
	private Node 	type;
	
	public SmartType(String key) {
		this.key = key;
	}
	
	public final Node getObject() {
		if(type == null) {
			type = currScene.getObjectType(key);
//			type = ObjectTypeMgr.instance().getObjectType(key);
		}
		return type.getInstance();
	}
}
