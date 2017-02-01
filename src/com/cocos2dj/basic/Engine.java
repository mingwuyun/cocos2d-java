package com.cocos2dj.basic;

import com.badlogic.gdx.utils.Array;
import com.cocos2dj.base.Director;

/** 
 * Engine.java 
 * <p>
 * 
 * @author Copyright (c) 2012-2017 xu jun
 */
public class Engine {
	
	final SConfig config = new SConfig();
	
	/**应用的状态 */
	enum AppState {
		START, RUNNING, END
	}
	
	///////////////////////////////////
	/**创建一个Card2DEngine实例 */
	public static final Engine newEngine(BaseGame card2dAppListener) {
		if(_instance == null){
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
	
	/**更新逻辑 */
	final void update(final int delta) {
		
		engineLock.lock();
		try {
			renderThreadFlag = false;
			
			// 更新 本地计时器时间
			BaseTimer.updateEngineTime(delta);
			
//			BaseScheduler.instance().updateFirst(delta);
			
//			final IScene scene = SceneManager.getCurrentScene();
//			scene.update(delta);
//			System.out.println("update >>>>>>>>>>>");
//			BaseScheduler.instance().updateMain(delta);
//			this.moudleManager.updateModuleMainThread(delta);
			
			//主循环交给director完成
			//调用在逻辑线程，不要进行图像操作
			director.mainLoop(delta);
			baseInput.update();
			baseScheduler.updateMain(delta);
			
			
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
		engineLock.lock();
		try {
			renderThreadFlag = true;
			
			director.clearRendererState();
			//在图像进程处理绘制相关，由engine主动调用drawScene
			baseScheduler.updateRenderBefore((int)BaseCoreTimer.delta);
			director.drawScene();
			baseScheduler.updateRenderAfter((int)BaseCoreTimer.delta);
			
//			if(loadScene){
//				loadScene = false;
//				SceneManager.getSceneManager()._changeScene();
//				return;
//			}
//			
//			this.moudleManager.updateModuleRenderThread(BaseCoreTimer.delta);
//			
//			final IScene scene = SceneManager.getCurrentScene();
//			scene.render();
//			
//			BaseScheduler.instance().updateRender(BaseCoreTimer.delta);
			
			engineLock.notifyCanUpdate();
			try {
				engineLock.waitUntilCanDraw();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} finally {
			engineLock.unlock();
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
		if(!thread.isAlive())
			thread.start();
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
		thread.interrupt();
		
		//释放引擎
		thread = null;
		_instance = null;
	}
}
