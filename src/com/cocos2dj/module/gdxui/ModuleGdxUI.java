package com.cocos2dj.module.gdxui;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.cocos2dj.base.Director;
import com.cocos2dj.base.Scheduler;
import com.cocos2dj.basic.BaseInput;
import com.cocos2dj.basic.BaseUpdater;
import com.cocos2dj.macros.CC;
import com.cocos2dj.macros.CCLog;
import com.cocos2dj.module.Module;
import com.cocos2dj.protocol.IScene;
import com.cocos2dj.protocol.IUpdater;

/**
 * ModuleGdxUI.java
 * <p>
 * 使用方法：
 * <pre>
 * scene.addModule(new ModuleGdxUI());
 * //gdxui = scene.createModule<'ModuleGdxUI>(ModuleGdxUI.class);
 * </pre>
 * @author Copyright (c) 2017 xu jun
 */
public class ModuleGdxUI extends Module {
	
	////////////////////////////////////////
	public static Drawable loadImageToAsDrawable(String filePath) {
		return new TextureRegionDrawable(CC.LoadImage(filePath).createTextureRegion());
	}
	
	/**获取ui配置实例*/
	public static GdxUIConfig getUIConfig() {
		return GdxUIConfig.instance();
	}
	
	/**创建debug输出实例 */
	public static GdxUIDebugInfo createDebugInfo() {
		return new GdxUIDebugInfo();
	}
	
	private GdxUIConsole _console;
	/**
	 * 创建控制台
	 * @return 控制台实例
	 */
	public GdxUIConsole createConsole() {
		if(_console == null) {
			_console = new GdxUIConsole();
			addUIStage(_console, false);
		}
		return _console;
	}
	/**获取控制台 */
	public GdxUIConsole getConsole() {
		return _console;
	}
	
	///////////////////////////////////////
	
	
	
	public static final String ModuleId = "GdxUI";
	public static final String ModuleType = "UI";
	
	public static ModuleGdxUI create() {
		return new ModuleGdxUI();
	}
	
	
	public ModuleGdxUI() {
		super(ModuleId, ModuleType);
	}

	private Group			_defaultGroup;
	private Stage 			_stage;	
	private GdxUIManager 	_manager;
	private IUpdater renderFunc = new IUpdater() {
		@Override
		public boolean update(float dt) {
			_manager.updateUIManager((int) dt);
			_stage.act(dt);
			_stage.draw();
			return false;
		}
	};
	private BaseUpdater renderFuncHandler;
	

	//methods>>
	/**清理默认ui层 */
	public void clearUIDefault() {
		if(_defaultGroup != null) {
			_defaultGroup.clear();
		}
	}
	
	/**向默认ui层添加actor对象 */
	public void addUIDefault(Actor actor) {
		if(_defaultGroup == null) {
			_defaultGroup = new Group();
			//stage 如果已创建就直接添加
			if(_stage != null) {
				_stage.addActor(_defaultGroup);
			}
		}
		_defaultGroup.addActor(actor);
	}
	
	public Stage getStage() {
		return _stage;
	}
	
	public final GdxUIStage addUIStage(GdxUIStage stage, boolean show) {
//		System.out.println(_manager);
		return _manager.addUIStage(stage, show);
	}
	
	public final GdxUIStage addUIStage(GdxUIStage stage, int index, boolean show) {
		return _manager.addUIStage(stage, index, show);
	}
	
	
	public final GdxUIStage popUIStage() {
		return _manager.popUIStage();
	}
	
	public final int getUIStageCount() {
		return _manager.getUIStageCount();
	}
	
	public final GdxUIStage getStage(int index) {
		return _manager.getStage(index);
	}
	
	public final GdxUIStage getUIStage(Class<? extends GdxUIStage> clazz) {
		return _manager.getUIStage(clazz);
	}
	
	public final GdxUIStage getUIStage(String key) {
		return _manager.getUIStage(key);
	}
	
	/**指定下标 移除 uiStage（从场景销毁）*/
	public final GdxUIStage removeUIStage(int index) {
		return _manager.removeUIStage(index);
	}
	
	/**指定名称 移除 uiStage（从场景销毁）*/
	public final GdxUIStage removeUIStage(String key) {
		return _manager.removeUIStage(key);
	}
	
	/**指定名称 移除 uiStage（从场景销毁）*/
	public final GdxUIStage removeUIStage(GdxUIStage stage) {
		return _manager.removeUIStage(stage);
	}
	
	/**设置debug模式 */
	public void setDebugMode(boolean debug) {
		_stage.setDebugAll(debug);
	}
	
	public OrthographicCamera getUICamera() {
		return (OrthographicCamera) _stage.getCamera();
	}
	//methods<<
	
	
	
	//override>>
	@Override
	public void onEnter(IScene scene, Object config) {
		CCLog.engine(ModuleId, "onEnter");
		
		GdxUIConfig _config = (GdxUIConfig) config;
		if(_config == null) {
			//默认采用从director获取的设置
			_config = GdxUIConfig.instance();
			_config.uiDefaultWidth = Director.getInstance().getVisibleSize().width;
			_config.uiDefaultHeight = Director.getInstance().getVisibleSize().height;
		}
		
		_stage = new Stage();
//		_stage.s
		
		//default存在就添加
		if(_defaultGroup != null) {
			if(_defaultGroup.getStage() != _stage) {
				_stage.addActor(_defaultGroup);
			}
		}
		
		_manager = new GdxUIManager(this);
		
		//应用配置
		_stage.getCamera().viewportWidth = _config.uiDefaultWidth;
		_stage.getCamera().viewportHeight = _config.uiDefaultHeight;
		_stage.getCamera().position.set(
				_stage.getCamera().viewportWidth/2f, 
				_stage.getCamera().viewportHeight/2f, 0);
		
		_stage.getViewport().setWorldWidth(_config.uiDefaultWidth);
		_stage.getViewport().setWorldHeight(_config.uiDefaultHeight);
		
		BaseInput.instance().addInputProcessor(_stage);
		Scheduler _scheduler = Director.getInstance().getScheduler();
		//visit 渲染完再处理这个
		renderFuncHandler = _scheduler.renderAfterSchedulePerFrame(renderFunc, 0, false);
		
		_stage.setDebugAll(false);
	}

	@Override
	public void onExit(IScene scene) {
		CCLog.engine(ModuleId, "onExit");
		if(_stage != null) {
			BaseInput.instance().removeInputProcessor(_stage);
			_stage.dispose();
			_stage = null;
		}
		if(renderFuncHandler != null) {
			renderFuncHandler.removeSelf();
			renderFuncHandler = null;
		}
	}
	//override<<

}
