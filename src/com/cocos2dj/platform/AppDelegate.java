package com.cocos2dj.platform;

/**
 * AppDelegate.java
 * <p>
 * 
 * @author Copyright (c) 2017 xu jun
 */
public interface AppDelegate {
	
	/**
	 * Cocos引擎初始化之前调用
	 * 可以对cocos2dj引擎进行参数设置，注意引擎此时不能使用，应该只设置参数
	 * 此时gdx初始化完毕 
	 */
	public void initConfiguration();
	
    /**
    @brief    Implement Director and Scene init code here.
    @return true    Initialize success, app continue.
    @return false   Initialize failed, app terminate.
    */
    public boolean applicationDidFinishLaunching();

    /**
    @brief  Called when the application moves to the background
    @param  the pointer of the application
    */
    public void applicationDidEnterBackground();

    /**
    @brief  Called when the application reenters the foreground
    @param  the pointer of the application
    */
    public void applicationWillEnterForeground();
    
}
