package com.cocos2dj.module.gdxui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.cocos2dj.basic.BaseTask;
import com.cocos2dj.basic.Engine;
import com.cocos2dj.basic.IDisposable;
import com.cocos2dj.platform.FileUtils;

/**
 * UI皮肤管理<p>
 * 
 * @author xu jun
 * Copyright (c) 2016. All rights reserved. 
 */
public class GdxUISkin implements IDisposable {
	
	private GdxUISkin() {}
	private static GdxUISkin _instance;
	public static GdxUISkin instance() {
		if(_instance == null) {
			_instance = new GdxUISkin();
			Engine.registerDisposable(_instance);
		}
		return _instance;
	}
	static class StructSkin {
		String  first;
		Skin	second;
	}

	
	private Skin		defaultSkin;
	private Array<StructSkin>	skins = new Array<StructSkin>(3);
	
	public Skin getDeafult() {
		if(defaultSkin == null) {
			if(GdxUIConfig.instance().uiDefaultSkinPath == null) {
				//android
				if(FileUtils.getInstance().isFileExist("sfd/uiskin.json")) {
					defaultSkin = new Skin(Gdx.files.internal("sfd/uiskin.json"));
				} else {
					defaultSkin = new Skin(Gdx.files.classpath("com/cocos2dj/module/gdxui/sfd/uiskin.json"));
				}
//				Gdx.files.classpath("com/cocos2dj/module/gdxui/sfd/uiskin.json");
			} else {
				defaultSkin = new Skin(Gdx.files.internal(GdxUIConfig.instance().uiDefaultSkinPath));
			}
		}
		return defaultSkin;
	}
	
	public Skin getSkin(String path) {
		for(int i = 0; i < skins.size; ++i) {
			StructSkin ss = skins.get(i);
			if(path.equals(ss.first)) {
				return ss.second;
			}
		}
		return null;
	}
	
	public Skin loadSkin(String path) {
		Skin skin = getSkin(path);
		if(skin == null) {
			skin = new Skin(Gdx.files.internal(path));
			StructSkin ss = new StructSkin();
			ss.first = path;
			ss.second = skin;
			skins.add(ss);
		}
		return skin;
	}
	
	public void clearSkins() {
		for(int i = 0; i < skins.size; ++i) {
			skins.get(i).second.dispose();
		}
		skins.clear();
	}
	
	@Override
	public void dispose() {
		if(defaultSkin != null) {
			if(Engine.instance().isGLThread()) {
				defaultSkin.dispose();
				defaultSkin = null;
			} else {
				new BaseTask(new Runnable() {
					@Override
					public void run() {
						defaultSkin.dispose();
						defaultSkin = null;
					}
				}).attachScheduleToRenderBefore();
			}
		}
		for(int i = 0; i < skins.size; ++i) {
			skins.get(i).second.dispose();
		}
		skins.clear();

		_instance = null;
	}
}
