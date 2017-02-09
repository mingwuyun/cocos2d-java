package com.cocos2dj.module;

import com.cocos2dj.protocol.IScene;

/**
 * Module.java
 * <p>
 * 
 * 模块修改：<br>
 * 模块准备结合 Scheduler 来使用<br>
 * 以scene为单位，直接关联scheduler驱动逻辑。例如：<br>
 * 
 * 为场景添加box2d物理引擎
 * <pre>
 * //init
 * World world = new World();
 * Scheduler s = Director.getInstance().getScheduler();
 * s.mainSchedulePerFrame((t)->{
 * 	world.step(t, )...
 * }, scene, -2, false);
 * 
 * //destroy
 * world.destroy();
 * </pre>
 * 
 * schedule的释放会通过 scheduler.unscheduleForTarget(currScene)
 * 实现。
 * 
 * @author Copyright (c) 2017 xu jun
 */
public abstract class Module {
	/**模块名称 */
	public final String moduleName;
	/**模块类型 */
	public final String moduleType;
	
	protected boolean initFlag = false;
	
	protected IScene _currentScene;
	
	/**获取当前模块关联的scene */
	public IScene getCurrentScene() {return _currentScene;}
	
	public Module(String moduleName, String moduleType) {
		this.moduleName = moduleName;
		this.moduleType = moduleType;
	}
	
	public void init() {
		
	}
	
	public void _onEnter(IScene scene, Object config) {
		this._currentScene = scene;
		if(!initFlag) {
			initFlag = true;
			onEnter(scene, config);
		}
	}
	
	public void _onExit(IScene scene) {
		if(initFlag) {
			initFlag = false;
			onExit(scene);
		}
		this._currentScene = null;
	}
	
	/**场景enter调用完毕后执行 */
	public void onAfterEnter(IScene scene) {} 
	
	public abstract void onEnter(IScene scene, Object config);
	
	public abstract void onExit(IScene scene);
	
}

