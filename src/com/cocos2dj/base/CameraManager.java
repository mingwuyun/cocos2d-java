package com.cocos2dj.base;

import com.cocos2dj.protocol.ICamera;

/**
 * CameraManager.java
 * <p>
 * 
 * 相机管理 s2d组件不应该被其他组件引用
 * 因此camera系统添加两个辅助类型
 * CameraManager和ICamera；用来保存全局状态／代理Camera
 * 
 * @author Copyright (c) 2017 xu jun
 *
 */
public final class CameraManager {
	
//	public 
	public static ICamera _visitingCamera;
	
}
