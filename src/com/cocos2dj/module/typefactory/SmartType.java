package com.cocos2dj.module.typefactory;

import com.cocos2dj.base.Director;
import com.cocos2dj.s2d.Node;
import com.cocos2dj.s2d.Scene;

public class SmartType {
	
	private String 		key;
	private NodeType 	type;
	
	public SmartType(String key) {
		this.key = key;
	}
	
	public final Node getObject() {
		if(type == null) {
			Scene scene = (Scene) Director.getInstance().getRunningScene();
			ModuleTypeFactory factory = (ModuleTypeFactory) scene.getModule(ModuleTypeFactory.class);
			
//			type = currScene.getObjectType(key);
//			type = ObjectTypeMgr.instance().getObjectType(key);
		}
		return type.getInstance();
	}
}
