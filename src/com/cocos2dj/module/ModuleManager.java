package com.cocos2dj.module;

import com.badlogic.gdx.utils.Array;
import com.cocos2dj.macros.CCLog;
import com.cocos2dj.protocol.IScene;

/**
 * ModuleManager.java
 * <p>
 * 
 * 模块管理以scene为单位，sceneManager负责模块的初始化和销毁其余需要模块自己实现 
 * 不提供remove方法，scene周期内，模块固定
 * 
 * @author Copyright (c) 2015-2017 xu jun 
 * */
public class ModuleManager {
	
	final IScene _scene;
	final Array<Module> modules = new Array<Module>();
	boolean running = false;
	Object config;
	
	public ModuleManager(IScene scene) {
		_scene = scene;
	}
	
	public final void setRunning(boolean running) {
		this.running = running;
	}
	
	
	public Module findModule(String moduleName) {
		for(int i = 0; i < modules.size; ++i) {
			Module module = modules.get(i);
			if(module.moduleName.equals(moduleName)) {
				return module;
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Module> T getModule(String moduleName) {
		for(int i = 0; i < modules.size; ++i) {
			Module module = modules.get(i);
			if(module.moduleName.equals(moduleName)) {
				return (T) module;
			}
		}
		return null;
	}
	
	/**
	 * <pre>
	 * Gdxui = scene.getModule<'Gdxui'>(Gdxui.class);
	 * </pre>
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends Module> T getModule(Class<T> clazz) {
		for(int i = 0; i < modules.size; ++i) {
			Module module = modules.get(i);
			if(module.getClass() == clazz) {
				return (T) module;
			}
		}
		return null;
	}
	
	/**检测模块是否可以安装 不能重复添加*/
	private boolean checkModule(Module module) {
		for(int i = 0; i < modules.size; ++i) {
			Module temp = modules.get(i);
			if(module.moduleType.equals(temp.moduleType)) {
				//同样类型模块只能添加一个
				CCLog.engine("ModuleManager", "module has same type : " + temp.moduleType);
				return false;
			}
		}
		
		
		if(modules.contains(module, true)) {
			CCLog.engine("ModuleManager", "module already exists");
			return false;
		}
		return true;
	}
	
	public final void addModule(Module module, Object config) {
		if(!checkModule(module)) {return;}
		this.config = config;
		modules.add(module);
		if(running) {
			module._onEnter(_scene, this.config);
		}
	}
	
	public final void onSceneEnter() {
		running = true;
		for(int i = 0; i < modules.size; ++i) {
			modules.get(i)._onEnter(_scene, config);
		}
	}
	
	public final void onSceneEnterAfter() {
		for(int i = 0; i < modules.size; ++i) {
			modules.get(i).onAfterEnter(_scene);
		}
	}
	
	public final void onSceneExit() {
		running = false;
		for(int i = 0; i < modules.size; ++i) {
			modules.get(i)._onExit(_scene);
		}
		modules.clear();
	}
}
