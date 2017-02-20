package com.cocos2dj.platform.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.cocos2dj.basic.BaseGame;
import com.cocos2dj.platform.AppDelegate;

/**
 * ApplicationStartup.java
 * <p>
 * 
 * 桌面版本启动函数
 * <pre>
 * public static void main(String[] args) {
 * 	LwjglApplicationConfiguration config =  ApplicationStartup.getConfiguration();
 * 	//config.width = 1136; config.height = 640;
 * 	ApplicationStartup.start(new MyAppDelegate());
 * }
 * </pre>
 * 
 * @author Copyright (c) 2017 xu jun
 */
public class ApplicationStartup {
	
	static LwjglApplicationConfiguration 	configuration;
	static LwjglApplication					application;
	
	
	public static final LwjglApplicationConfiguration getConfiguration() {
		if(configuration == null) {
			configuration = new LwjglApplicationConfiguration();
		}
		return configuration;
	}
	
	public static final void start(AppDelegate appDelegate) {
		application = new LwjglApplication(
				new BaseGame(appDelegate),
				getConfiguration()
		);
	}
}
