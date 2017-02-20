package com.cocos2dj.platform.android;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.cocos2dj.basic.BaseGame;
import com.cocos2dj.platform.AppDelegate;

/**
 * ApplicationStartup.java
 * <p>
 * 
 * android版本启动函数
 * <pre>
 *
 * </pre>
 * 
 * @author Copyright (c) 2017 xu jun
 */
public class ApplicationStartup {

	static AndroidApplicationConfiguration	configuration;

	public static final AndroidApplicationConfiguration getConfiguration() {
		if(configuration == null) {
			configuration = new AndroidApplicationConfiguration();
		}
		return configuration;
	}

	public static final void start(AppDelegate appDelegate, AndroidApplication application) {
		application.initialize(new BaseGame(appDelegate), getConfiguration());
	}
}
