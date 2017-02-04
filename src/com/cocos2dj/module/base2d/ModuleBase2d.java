package com.cocos2dj.module.base2d;

import com.cocos2dj.module.Module;
import com.cocos2dj.module.base2d.framework.Base2D;
import com.cocos2dj.protocol.IScene;

/**
 * ModuleBase2d.java
 * <p>
 * 
 * Base2D物理引擎模块， 基于速度。 该引擎善于模拟受控物体 <p>
 * 
 * @author Copyright(c) 2017 xujun
 */
public class ModuleBase2d extends Module {
	
	public static final String ModuleId = "Base2D";
	public static final String ModuleType = "Physics2D";

	public ModuleBase2d(String moduleName, String moduleType) {
		super(moduleName, moduleType);
	}

	
	@Override
	public void onEnter(IScene scene, Object config) {
//		Base2D
	}

	@Override
	public void onExit(IScene scene) {
//		Base2D.instance().destroyScene();
	}
	
	
	//fields>>
	
	//fields<<
}
