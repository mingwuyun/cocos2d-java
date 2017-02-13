package com.cocos2dj.module.visui;

import com.cocos2dj.module.Module;
import com.cocos2dj.module.gdxui.ModuleGdxUI;
import com.cocos2dj.protocol.IScene;
import com.cocos2dj.s2d.Scene;
import com.kotcrab.vis.ui.VisUI;

/**
 * ModuleVisUI.java
 * <p>
 * 
 * 添加逻辑：如果存在ModuleGdxUI则只添加本组件；
 * 如果不存在ModuleGdxUI会同GdxUI一起添加<br>
 * 
 * 使用方法：
 * <pre>
 * scene.addModule(new ModuleVisUI());
 * //gdxui = scene.createModule<'ModuleVisUI>(ModuleVisUI.class);
 * </pre>
 * @author Copyright (c) 2017 xu jun
 */
public class ModuleVisUI extends Module implements VisUIHelper {

	public ModuleVisUI() {
		super(ModuleId, ModuleType);
	}

	public static final String ModuleId = "VisUI";
	public static final String ModuleType = "UIExt";
	
	
	
	@Override
	public void onEnter(IScene iscene, Object config) {
		Scene scene = (Scene) iscene;
		_gdxui = (ModuleGdxUI) scene.getModule(ModuleGdxUI.class);
		if(_gdxui == null) {
			//gdx ui 不存在自动添加
			_gdxui = scene.createModule(ModuleGdxUI.class);
		}
		if(!VisUI.isLoaded()) {
			VisUI.load();	
		}
		VisUI.getSizes();
	}
	
	@Override
	public void onExit(IScene scene) {
		
	}
	
	
	//methods>>
	public static void disposeVisUI() {
		VisUI.dispose();
	}
	
	
	//methods<<
	
	public final ModuleGdxUI getGdxUI() {return _gdxui;}
	
	//fields>>
	ModuleGdxUI 	_gdxui;
}
