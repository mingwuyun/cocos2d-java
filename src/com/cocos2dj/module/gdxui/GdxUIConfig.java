package com.cocos2dj.module.gdxui;

import com.cocos2dj.base.Director;

/**
 * UI的设置
 * 
 * @author xu jun
 * Copyright (c) 2016. All rights reserved.
 */
public class GdxUIConfig {
	
	private static GdxUIConfig _instance;
	private GdxUIConfig() {}
	public static GdxUIConfig instance() {
		if(_instance == null) {
			_instance = new GdxUIConfig();
		}
		return _instance;
	}
	
	//常规配置
	public float uiDefaultWidth = 800;
	public float uiDefaultHeight = 450;
	public String uiDefaultSkinPath = null;//"sfd/uiskin.json";
	public int uiDefaultBatchVertex = 500;		//uiBatch定点数量
	//控制台配置
	public int consoleMaxOutputLine = 8;			//最大缓存行数
//	public float getScaleX() {
		
//	}
//	public float g
	
	
	
	public GdxUIConfig setFromDirector() {
		uiDefaultWidth = Director.getInstance().getVisibleSize().width;
		uiDefaultHeight = Director.getInstance().getVisibleSize().height;
		return this;
	}
	
	public GdxUIConfig setDefaultUIScreen(float w, float h) {
		uiDefaultWidth = w;
		uiDefaultHeight = h;
		return this;
	}
	public GdxUIConfig setDefaultUISkin(String skinPath) {
		uiDefaultSkinPath = skinPath;
		return this;
	}
	public GdxUIConfig setDefaultBatchVertex(int count) {
		uiDefaultBatchVertex = count;
		return this;
	}
	public GdxUIConfig setConsoleMaxOutput(int count) {
		consoleMaxOutputLine = count;
		return this;
	}
}
