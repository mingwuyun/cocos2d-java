package com.cocos2dj.basic;

import java.util.HashMap;

import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * ����<p>
 * 
 * ����ϵͳ��������һ����Engine������ϵͳ���ã�
 * ��һ����ͨ����̬������ȡ����Ϸ����<p>
 * 
 * @author xu jun
 * Copyright (c) 2015-2016. All rights reserved. */
public class SConfig implements IDisposable {
	
	//object�������ã��������õ���
	private final HashMap<Class<?>, Object> configMap = new HashMap<Class<?>, Object>(); 
	
	//���ò�����㴴�� 
	SConfig() {}
	
	public final void putConfig(Object conf) {
		this.putConfig(conf.getClass(), conf);
	}
	
	public final void putConfig(Class<?> clazz, Object conf) {
		Object o = configMap.put(clazz, conf);
		if(o != null) {
			throw new GdxRuntimeException("�������Ѿ����� :" + clazz);
		}
	}
	
	public final void removeConfig(Class<?> clazz) {
		Object o = configMap.remove(clazz);
		if(o == null) {
			BaseLog.error("SConfig", "���ò����� clazz = " + clazz);
//			throw new GdxRuntimeException("���ò����� :" + clazz);
		}
	}
	
	public final Object getConfig(Class<?> clazz) {
		return configMap.get(clazz);
	}
	
	/**��Ϣϵͳ����Ų������� */
	public static final int MSG_MAX_ARG_COUNT = 8;
	
	@Override
	public void dispose() {
		gameConfig = null;
	}
	
	//�����Ƕ��ⲿ�Ľӿ�
	//static>>
	private static SConfig gameConfig;
	public static SConfig instance() {
		if(gameConfig == null) {
			gameConfig = new SConfig();
			Engine.registerDisposable(gameConfig);
		}
		return gameConfig;
	}
	//static<<

	
}
