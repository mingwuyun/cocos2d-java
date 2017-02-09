package com.cocos2dj.module.typefactory;

import com.cocos2dj.base.Director;
import com.cocos2dj.s2d.Node;
import com.cocos2dj.s2d.Scene;

/**
 * SmartType.java
 * <p>
 * 
 * typeFactory辅助对象，根据放入 {@link NodeFactory} 中Type的键值获取对应的node对象
 * 第一次会从module中获取，之后缓存nodeType，可以快速取得目标node
 * <br>
 * <pre>
 * SmartType type = SmartType.create("TargetNode1");
 * Node target = type.getNode();
 * NodeProxy = target.getNodeProxy();
 * ...
 * target.pushBack();
 * </pre>
 * 
 * @author Copyright(c) 2017 xujun
 */
public class SmartType {
	
	private String 		key;
	private NodeType 	type;
	
	public static SmartType create(String key) {
		return new SmartType(key);
	}
	
	public SmartType(String key) {
		this.key = key;
	}
	
	public final Node getObject() {
		if(type == null) {
			Scene scene = (Scene) Director.getInstance().getRunningScene();
			ModuleTypeFactory factory = (ModuleTypeFactory) scene.getModule(ModuleTypeFactory.class);
			type = factory.findNodeType(key);
		}
		return type.getInstance();
	}
}
