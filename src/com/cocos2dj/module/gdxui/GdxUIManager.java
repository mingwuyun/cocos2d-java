package com.cocos2dj.module.gdxui;

import com.badlogic.gdx.utils.Array;

/**
 * 场景内部等UI管理器<p>
 * 
 * 使用Array来管理UIStage
 * 
 * @author xu jun
 * Copyright (c) 2016. All rights reserved. 
 */
public final class GdxUIManager {
	
	static final String TAG = "GdxUIManager";
	
	
	//fields>>
	final Array<GdxUIStage>		stages = new Array<>();
	final ModuleGdxUI			module;		//从module中获取scene等所需信息
	//fields<<
	
	
	GdxUIManager(ModuleGdxUI module) {
		this.module = module;
	}
	
	
	//methods>>
	public ModuleGdxUI getModule() {
		return module;
	}
	
	public final GdxUIStage addUIStage(GdxUIStage stage, boolean show) {
		return this.addUIStage(stage, -1, show);
	}
	
	/**
	 * @param stage
	 * @param show
	 */
	public final GdxUIStage addUIStage(GdxUIStage stage, int index, boolean show) {
//		if(stage.hotSwapFlag) {
//			initHotSwap();
//			SLog.debug(TAG, "init with hotswap mode : " + stage.getClass().getName());
//			try {
//				stage = (GdxUIStage) hotSwapClassManager.getClass(stage.getClass().getName()).newInstance();
//			} catch (InstantiationException | IllegalAccessException e) {
//				e.printStackTrace();
//			} 
//		}
		stage.addToStage(this);
		if(index < 0) {
			stages.add(stage);
		} else {
			stages.insert(index, stage);
		}
		if(show) {
			stage.show();
		}
		else {
			stage.hide();
		}
		return stage;
	}
	
	
	public final GdxUIStage popUIStage() {
		GdxUIStage s = stages.pop();
		s.removeFromStage();
		return s;
	}
	
	public final int getUIStageCount() {
		return stages.size;
	}
	
	public final GdxUIStage getStage(int index) {
		return stages.get(index);
	}
	
	public final GdxUIStage getUIStage(Class<? extends GdxUIStage> clazz) {
		for(int i = 0; i < stages.size; ++i) {
			GdxUIStage stage = stages.get(i);
			if(stage.getClass() == clazz) {
				return stage;
			}
		}
		return null;
	}
	
	public final GdxUIStage getUIStage(String key) {
		for(int i = 0; i < stages.size; ++i) {
			GdxUIStage stage = stages.get(i);
			if(key.equals(stage.stageKey)) {
				return stage;
			}
		}
		return null;
	}
	
	/**指定下标 移除 uiStage（从场景销毁）*/
	public final GdxUIStage removeUIStage(int index) {
		GdxUIStage s = stages.removeIndex(index);
		s.removeFromStage();
		return s;
	}
	
	/**指定名称 移除 uiStage（从场景销毁）*/
	public final GdxUIStage removeUIStage(String key) {
		for(int i = stages.size - 1; i >= 0; --i) {
			GdxUIStage stage = stages.get(i);
			if(key.equals(stage.stageKey)) {
				
				stages.removeIndex(i);
				return stage;
			}
		}
		return null;
	}
	
	/**指定名称 移除 uiStage（从场景销毁）*/
	public final GdxUIStage removeUIStage(GdxUIStage stage) {
		if(stages.removeValue(stage, true)) {
			stage.removeFromStage();
			return stage;
		}
		return null;
	}
	
	public final void updateUIManager(int dt) {
		for(GdxUIStage stage : stages) {
			stage.updateUI(dt);
		}
	}
	
	
	
	public final void destroyUIManager() {
		for(int i = 0; i < stages.size; ++i) {
			final GdxUIStage stage = stages.get(i);
			stage.onDestroyUI();
			stage.removeFromStage();
		}
		stages.clear();
//		destroyHotSwap();
	}
	//methods<<


	public float getUIWidth() {
		return GdxUIConfig.instance().uiDefaultWidth;
	}

	public float getUIHeight() {
		return GdxUIConfig.instance().uiDefaultHeight;
	}
	

	
	//TODO 热加载扩展 ----------------------------------------
//	HotSwapClassManager hotSwapClassManager;
//	boolean leftShiftDown = false;
//	boolean TabDown = false;
//	InputAdapter 	inputHandle = new InputAdapter() {
//		public boolean keyDown(int keycode) {
//			if(keycode == Keys.SHIFT_LEFT) {
//				leftShiftDown = true;
//			} else if(keycode == Keys.TAB) {
//				TabDown = true;
//			}
//			if(leftShiftDown && TabDown) {
//				sweepStages();
//			}
//			return false;
//		}
//		
//		public boolean keyUp(int keycode) {
//			if(keycode == Keys.SHIFT_LEFT) {
//				leftShiftDown = false;
//			} else if(keycode == Keys.TAB) {
//				TabDown = false;
//			}
//			return false;
//		}
//	};
//	/**注册热加载快捷键监听 默认 left shift ＋ Q*/
//	public void registerHotSwapInputListener() {
//		SInput.instance().addProcessor(inputHandle);
//	}
//	
//	
//	private void reloadStage(GdxUIStage stage, int index, String className) {
//		boolean show = !stage.isHide();
//		removeUIStage(index);
//		GdxUIStage newStage = null;
//		try {
//			newStage = (GdxUIStage) hotSwapClassManager.getClass(className).newInstance();
//		} catch (InstantiationException e) {
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			e.printStackTrace();
//		}
//		addUIStage(newStage, index, show);
//	}
//	
//	/**
//	 * 手动开启热加载
//	 */
//	public final void initHotSwap() {
//		if(hotSwapClassManager == null) {
//			hotSwapClassManager = new HotSwapClassManager();
//		}
//	}
//	
//	/**一个class可以有多个实现 需要遍历查找潜在对象 */
//	public final void reloadStages(StructClassInfo info) {
//		for(int i = 0; i < stages.size; ++i) {
//			GdxUIStage s = stages.get(i);
//			String sName = s.getClass().getName();
//			if(sName.equals(info.className)) {
//				SLog.debug(TAG, "found changed stage : [" + i + "] " + sName);
//				reloadStage(s, i, sName);
//			}
//		}
//	}
//	
//	public final void sweepStages() {
//		if(hotSwapClassManager == null) {
//			SLog.error(TAG, "hot swap cannot use");
//			return;
//		}
//		Array<StructClassInfo> ret = hotSwapClassManager.sweepFromObjects(stages);
//		if(ret.size == 0) {
//			SLog.debug(TAG, "no class file change ");
//			return;
//		}
//		
//		for(StructClassInfo info : ret) {
//			reloadStages(info);
//		}
//		
//		ret.clear();
//		ret = null;
//	}
//	
//	public final void destroyHotSwap() {
//		if(hotSwapClassManager == null) {
//			return;
//		}
//		hotSwapClassManager.clear();
//		hotSwapClassManager = null;
//	}
}
