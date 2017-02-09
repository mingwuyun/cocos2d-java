package com.cocos2dj.module.typefactory;

import com.cocos2dj.basic.BaseTask;
import com.cocos2dj.basic.Engine;
import com.cocos2dj.macros.CCLog;
import com.cocos2dj.module.Module;
import com.cocos2dj.protocol.IScene;
import com.cocos2dj.s2d.Scene;

/**
 * ModuleTypeFactory.java
 * <p>
 * 
 * @author Copyright(c) 2017 xu jun
 */
public class ModuleTypeFactory extends Module {

	public static final String ModuleId = "TypeFactory";
	public static final String ModuleType = "NodeFactory";
	
	public ModuleTypeFactory() {
		super(ModuleId, ModuleType);
	}

	@Override
	public void onEnter(IScene scene, Object config) {
		if(currentScene == null) {
			nodeFactory = new NodeFactory();
			currentScene = (Scene) scene;
			
		} else {
			CCLog.engine("ModuleTypeFactory", "currentScene already exists!");
		}
	}
	
	public void onAfterEnter(IScene scene) {
		if(nodeFactory != null) {
			nodeFactory.end_createInstance(currentScene);
		}
	}

	@Override
	public void onExit(IScene scene) {
		currentScene = null;
		nodeFactory.clear();
		nodeFactory = null;
	}
	
	/**
	 * autoFlush = true
	 * @see  #addNodeType(NodeType, boolean)
	 */
	public void addNodeType(NodeType type) {
		addNodeType(type, true);
	}
	
	/**
	 * 添加 节点类型对象
	 * @param type
	 * @param autoFlush 是否自动刷新
	 */
	public void addNodeType(NodeType type, boolean autoFlush) {
		nodeFactory.putActorType(type);
		if(autoFlush) {
			if(Engine.instance().isGLThread()) {
				nodeFactory.flush_createInstances(currentScene);
			} else {
				BaseTask.create(new Runnable() {
					@Override
					public void run() {
						nodeFactory.flush_createInstances(currentScene);
					}
				}).attachScheduleToRenderBefore();
			}
		}
	}
	
	public NodeType findNodeType(String key) {
		return nodeFactory.getActorType(key);
	}
	
	
	Scene			currentScene;
	NodeFactory		nodeFactory;
}
