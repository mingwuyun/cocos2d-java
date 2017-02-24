package com.cocos2dj.macros;

public class CCLog {
//	
	public static boolean CC_ENGINE_LOG_ENABLE = true;
	public static final void engine(String tag, String msg) {
		if (CC_ENGINE_LOG_ENABLE) {
			System.out.println("["+tag+"] " + msg);
		}
	}
	
	public static final void debug(Class<?> clazz, String msg) {
//		 simpleName.substring(simpleName.lastIndexOf(".")+1);
		debug(clazz.getSimpleName(), msg);
	}
	
	public static final void debug(String tag, String msg) {
		System.out.println("["+tag+"] " + msg);
	}
	
	public static final void error(String tag, String msg) {
		System.err.println("["+tag+"] " + msg);
	}
}

//CC.LOG(ddd)
//CC.Sprite.create()
//CC.ReadJson
