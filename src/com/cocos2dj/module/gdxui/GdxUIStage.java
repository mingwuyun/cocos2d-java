package com.cocos2dj.module.gdxui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.cocos2dj.basic.BaseUpdater;
import com.cocos2dj.basic.Engine;

//import com.stormframework.util.SLog;

/**
 * UI版面<p>
 * 
 * 更新内容：支持java热加载操作 推荐使用下面的方式类进行事件注册
 * 
 * <pre>
 * registerEvent("ev_open", buttonOpen);
 * 
 * 	onChanged(Event e, Actor a) {
 * 		switch(a.getName()) {
 * 			case "ev_open":
 * 				...
 * 		}
 * }
 * </pre>
 * @author xu jun
 * 
 * Copyright (c) 2016. All rights reserved. 
 */
public abstract class GdxUIStage implements EventListener {
	
	//fields>>
	protected String		stageKey;
	private Group			subRoot = new Group();
	private Stage			parent = null;
	private GdxUIManager 	uiManager = null;
	boolean					hotSwapFlag = false;		//hotswap局限性较大，不要滥用
	//fields<<
	
	
	//getset>>
	/**设置是否开启热加载模式 默认不开启 */
	public final void setHotSwapFlag(boolean enable) {hotSwapFlag = enable;}
	public final Stage getStage() {return parent;}
	public final float getUIWidth() {return uiManager.getUIWidth();}
	public final float getUIHeight() {return uiManager.getUIHeight();}
	public final float getUICenterX() {return uiManager.getUIWidth()/2f;}
	public final float getUICenterY() {return uiManager.getUIHeight()/2f;}
	public final void setScale(float scaleX, float scaleY) {subRoot.setScale(scaleX, scaleY);}
	public final void setScale(float scale) {subRoot.setScale(scale);}
	public final void setScaleX(float scaleX) {subRoot.setScaleX(scaleX);}
	public final void setScaleY(float scaleY) {subRoot.setScaleY(scaleY);}
	
	public final void setStageKey(String key) {
		this.stageKey = key;
	}
	
	public final String getStageKey() {
		return stageKey;
	}
	//getset<<
	
	
	//func>>
	final void initUI() {
		//可能涉及到纹理创建等操作 因此修正到gl线程中执行
		if(Engine.instance().isGLThread()) {
			onInitUI();
		} else {
			new BaseUpdater() {
				protected boolean onUpdate(float dt) {
					onInitUI();
					return true;
				}
				protected void onEnd() {}
			}.attachScheduleToRenderBefore();
		}
	}
	
	final void updateUI(int dt) {
		if(!isHide()) {
			onUpdateUI(dt);
		}
	}
	//func<<
	
	
	//methods>>
	public final GdxUIManager getUIManager() {
		return uiManager;
	}
	
	public final void remove() {
		uiManager.removeUIStage(this);
	}
	
	public final void setPosition(float x, float y) {
		this.subRoot.setPosition(x, y);
	}
	
	public final void hide() {
		this.subRoot.setVisible(false);
		onHideUI();
	}
	
	public final void show() {
		this.subRoot.setVisible(true);
		onShowUI();
	}
	
	/**是否隐藏 */
	public final boolean isHide() {
		return !subRoot.isVisible();
	}
	
	public void addUI(Actor actor) {
		subRoot.addActor(actor);
	}
	
	
	/**返回该stage是否已经添加到场景中 */
	public final boolean isInScene() {
		return parent != null;
	}
	
	/**添加stage到场景  */
	protected void addToStage(GdxUIManager uiManager) {
		if(this.uiManager == null) {
			this.uiManager = uiManager;
//			SLog.error("SUIStage.addToScene", "添加UIStage失败 stage已添加 --- parent = " + parent);
			initUI();
		}
		this.parent = uiManager.getModule().getStage();
		this.parent.addActor(subRoot);
	}
	
	/**移除stage */
	protected void removeFromStage() {
		onDestroyUI();
		subRoot.remove();
		parent = null;
		subRoot = null;
	}

	/**注册输入监听<p>
	 * 为了兼容热加载，推荐使用该函数处理输入监听不要使用内部类 */
	protected void registerEvent(String key, Actor actor) {
		actor.setName(key);
//		actor.clearListeners();
		actor.addListener(this);
	}
	//methods<<
	
	
	public boolean handle (Event event) {
		if (!(event instanceof ChangeEvent)) return false;
		onChanged(event, event.getTarget());
		return false;
	}
	
	
	//abstract>>
	protected abstract void onInitUI();
	protected void onHideUI() {};
	protected void onShowUI() {};
	protected abstract void onDestroyUI();
	protected abstract void onUpdateUI(int dt);
	/**监听事件响应*/
	protected abstract void onChanged(Event e, Actor actor);
	//abstract<<
}
