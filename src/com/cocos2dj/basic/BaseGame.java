package com.cocos2dj.basic;

import com.badlogic.gdx.ApplicationListener;
import com.cocos2dj.platform.AppDelegate;

/**
 * BaseGame.java
 * <p>
 * 
 * @author Copyright (c) 2015-2017 xu jun
 */
public class BaseGame implements ApplicationListener {
	
	final AppDelegate appDelegate;
	
	public BaseGame(AppDelegate appDelegate) {
		this.appDelegate = appDelegate;
	}
	
	
	public void create() {
		appDelegate.initConfiguration();
		
		Engine.newEngine(this);
//		EngineSetting();
		Engine.instance().initializeEngine();
//		gameInitialize();
		Engine.instance().startEngine();
//		gameStart();
		appDelegate.applicationDidFinishLaunching();
	}
	
	public void dispose () {
//		gameEnd();
		Engine.instance().endEngine();
	}

	public void pause () {
		appDelegate.applicationDidEnterBackground();
		Engine.instance().pauseEngine();
	}

	public void resume () {
		appDelegate.applicationWillEnterForeground();
		Engine.instance().activeEngine();
	}

	public void render () {
		Engine.instance().render();
	}

	public void resize (int width, int height) {
		
	}
}
