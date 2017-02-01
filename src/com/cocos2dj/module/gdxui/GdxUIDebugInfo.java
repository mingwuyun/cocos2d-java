package com.cocos2dj.module.gdxui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;

/**
 * 通常的debug信息处理<p>
 * 
 * 包括：fps，heap等信息；出现在左下角; 如果有需要也可以多创建几个处理不同等信息，
 * 自行设定位置即可<p>
 * 
 * 可以通过addDebugListener来更换不同debug输出
 * 例如:
 * <pre>
 * SUIDebugInfo debug = new SUIDebugInfo();
 * FPSDebugListener fpsDebug = new FPSDebugListener() {
 * 	public String onOutput() {
 * 		return Gdx.graphics.getFPS();
 * 	}
 * }
 * </pre>
 * @author xu jun
 * Copyright (c) 2016. All rights reserved. 
 */
public class GdxUIDebugInfo extends GdxUIStage {
	
	//static>>
	public static interface DebugListener {
		/**返回输出debug信息 —— 返回null不输出*/
		public String onOutput();
	}
	//static<<
	
	
	//fields>>
	Array<DebugListener> outputs = new Array<>();
	Label	label;
	//fields<<
	
	
	
	//methods>>
	public Label getLabel() {
		return label;
	}
	
	public void addDebugListener(DebugListener listener) {
		outputs.add(listener);
	}
	
	public void removeDebugListener(DebugListener listener) {
		outputs.removeValue(listener, true);
	}
	
	public void removeDebugListener(int index) {
		outputs.removeIndex(index);
	}
	
	public void clearDebugListener() {
		outputs.clear();
	}
	//methods<<
	
	
	
	@Override
	protected void onInitUI() {
		Skin skin = GdxUISkin.instance().getDeafult();
		label = new Label("", skin);
		label.setOrigin(0, 0);
		label.setPosition(10, 10);
		addUI(label);
		skin = null;
	}

	@Override
	protected void onDestroyUI() {
		
	}

	@Override
	protected void onChanged(Event e, Actor actor) {
		
	}
	
	@Override
	protected void onUpdateUI(int dt) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0, n = outputs.size; i < n; ++i) {
			String out = outputs.get(i).onOutput();
			//输出然后换行
			if(out != null) {
				sb.append(out).append('\n');
			}
		}
		String out = sb.toString();
		label.setText(out);
		label.setPosition(10, 10 + label.getPrefHeight()/2f);
		out = null;
		sb = null;
	}

	
	//class>>
	public static final DebugListener DebugFPS = new DebugListener() {
		@Override
		public String onOutput() {
			return "fps = " + Gdx.graphics.getFramesPerSecond();
			// + " | deltaTime = " + Gdx.graphics.getDeltaTime();
		}
	};
	public static final DebugListener DebugHeap = new DebugListener() {
		@Override
		public String onOutput() {
			return "heap = " + Gdx.app.getJavaHeap() + " / " + Runtime.getRuntime().totalMemory();
		}
	};
//	public static final Debug
	//class<<



	
}

