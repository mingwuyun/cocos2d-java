package com.cocos2dj.basic;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.cocos2dj.base.Director;
import com.cocos2dj.macros.CCLog;

/** 
 * Engine.java 
 * <p>
 * 
 * @author Copyright (c) 2012-2017 xu jun
 */
public final class Engine {
	
	public static enum EngineMode {
		/**单线程模式 */ 
		SingleThread,
		/**双线程模式 逻辑线程-gl线程 （测试后，好像并没有变快?）*/ 
		DoubleThread,
	}
	private static EngineMode _engineMode = null; //EngineMode.DoubleThread;
	public static EngineMode getEngineMode() {
		return _engineMode;
	}
	public static void setEngineMode(EngineMode engineMode) {
		if(_engineMode == null) {
			_engineMode = engineMode;
		} else {
			throw new IllegalStateException("engineMode cannot change ! now is :" + _engineMode);
		}
	}
	
	public static void setSingleThreadMode() {
		setEngineMode(EngineMode.SingleThread);
	}
	public static void setDoubleThreadMode() {
		setEngineMode(EngineMode.DoubleThread);
	}
	
	final SConfig config = new SConfig();
	
	/**应用的状态 */
	enum AppState {
		START, RUNNING, END
	}
	
	///////////////////////////////////
	/**创建一个Card2DEngine实例 */
	public static final Engine newEngine(BaseGame card2dAppListener) {
		if(_instance == null){
			if(_engineMode == null) {
				if(Gdx.app.getType() == ApplicationType.Desktop) {
					_engineMode = EngineMode.SingleThread;		//桌面版本经过测试，backend已经实现了渲染线程分离，因此默认使用single模式
				} else {
					_engineMode = EngineMode.DoubleThread;		
				}
			}
			
			CCLog.engine("Engine", ">>>>>>>>>>>>>>>>>> engine run mode : " + _engineMode);
			
			_instance = new Engine();
			_instance.game = card2dAppListener;
			_instance.director = Director.getInstance();
			_instance.baseScheduler = BaseScheduler.instance();
		}
		return _instance;
	}
	
	/**card2D引擎的刷新率由libgdx负责 */
	private Engine() {
		engineLock = new EngineLock(true);
	}
	
	private static Engine _instance;
	public static Engine instance() {
		return _instance;
	}
	/////////////////////////////////
	
	
	private AppState appState = AppState.START;
	private EngineLock engineLock;
	BaseInput	baseInput;
	/**首个运行场景名称 */
//	private String firstSceneName;		
//	private IScene firstScene;			//首个运行场景
	BaseGame game;
	private volatile boolean endFlag;
	
	/**是否读取新场景的标志*/
//	private boolean loadScene;
	private volatile boolean renderThreadFlag = true;
	private Director 		director;
	private BaseScheduler 	baseScheduler;
	
	
	/**查询当前线程是否是渲染线程 */
	public final boolean isGLThread() {
		if(_engineMode == EngineMode.SingleThread) {
			return true;
		}
		return renderThreadFlag;
	}
	
	private static final Array<IDisposable> disposableList = 
			new Array<IDisposable>(4);
	
	
	public boolean isAppRunning() {
		return appState == AppState.RUNNING;
	}
	
	/**注册一个可释放对象，这个对象将在引擎完全退出时调用释放方法  */
	public static final void registerDisposable(IDisposable disposable) {
		disposableList.add(disposable);
		BaseLog.debug("disposableList", "add  count = " + disposableList.size);
	}
	
	/**接触释放对象的注册 */
	public static final void unregisterDisposable(IDisposable dispose) {
		disposableList.removeValue(dispose, true);
	}
	
	
	
	/**逻辑线程 */
	private Thread thread = new Thread("card2DMainThread"){
		public void run() {
			while(true){
				if(endFlag) {
					interrupt();
					break;
				}
				update((int)BaseCoreTimer.update());
			}
			BaseLog.debug("card2dx", "engine dispose");
		}
	};
	
	/**修正由于暂停没有导致的lastTime错误的问题 */
	final void updateLastTime(){
		BaseCoreTimer.updateLastTime();
	}
	
	/**要求改变场景 */
	final void requireChangeScene() {
//		loadScene = true;
	}
	
	private void _update(int delta) {
		// 更新 本地计时器时间
		BaseTimer.updateEngineTime(delta);
		//主循环交给director完成
		director.mainLoop(delta);
		baseInput.update();
		baseScheduler.updateMain(delta);
	}
	
	private void _render() {
		director.clearRendererState();
		//在图像进程处理绘制相关，由engine主动调用drawScene
		baseScheduler.updateRenderBefore((int)BaseCoreTimer.delta);
		director.drawScene();
		baseScheduler.updateRenderAfter((int)BaseCoreTimer.delta);
	}
	
	/**更新逻辑 */
	final void update(final int delta) {
		
		engineLock.lock();
		try {
			renderThreadFlag = false;
			
			_update(delta);
			
			engineLock.notifyCanDraw();
			try {
				engineLock.waitUntilCanUpdate();
			} catch (InterruptedException e) {
				
			}
		} finally {
			engineLock.unlock();
		}
	}
	
	/**更新图像 */
	final void render() {
		switch(_engineMode) {
		case DoubleThread:
			engineLock.lock();
			try {
				renderThreadFlag = true;
				
				_render();
				
				engineLock.notifyCanUpdate();
				try {
					engineLock.waitUntilCanDraw();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} finally {
				engineLock.unlock();
			}
			break;
			
		case SingleThread:
			_update((int)BaseCoreTimer.update());
			_render();
			break;
		}
		
	}
	
	
	
	
	public final EngineLock getEngineLock(){
		return engineLock;
	}
	
	
	
	
	/**初始化引擎*/
	public final void initializeEngine() {
		baseInput = BaseInput.instance();
	}
	
	/**启动引擎
	 * 运行状态的切换交由drawThread控制——当oncreated中完成
	 * 场景载入后切换为 运行状态*/
	public void startEngine() {
		updateLastTime();
		appState = AppState.RUNNING;
		
		if(_engineMode == EngineMode.DoubleThread) {
			if(!thread.isAlive()) {
				thread.start();
			}
		}
	}
	
	/**暂停引擎*/
	public void pauseEngine(){
//		thread.interrupt();
	}
	
	/**激活引擎*/
	public void activeEngine(){
		updateLastTime();
	}
	
	/**停止引擎 释放资源*/
	public void endEngine(){
		appState = AppState.END;
		
		//释放所有注册的可丢弃对象
		for(int i = 0; i < disposableList.size; ++i) {
			BaseLog.debug("disposableList", "dispose  count = " + disposableList.size);
			disposableList.get(i).dispose();
		}
		disposableList.clear();
		
		endFlag = true;
		
		if(_engineMode == EngineMode.DoubleThread) {
			thread.interrupt();
			//释放引擎
			thread = null;
		}

		_engineMode = null;
		_instance = null;
	}
}
