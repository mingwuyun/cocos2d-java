package com.cocos2dj.utils;

import java.lang.reflect.Field;

import com.badlogic.gdx.math.Vector2;

/**
 * MessUtils.java
 * <p>
 * 
 * 各种乱七八糟的功能集合
 * 
 * @author Copyright(c) 2016-2017 xu jun
 */
public class MessUtils {
	
	public static float[] pointsToFloats(Vector2...verts) {
		float[] ps = new float[verts.length];
    	for(int i = 0; i < verts.length; ++i) {
    		ps[i * 2] = verts[i].x;
    		ps[i * 2 + 1] = verts[i].y;
    	}
    	return ps;
	}
	
	/** */
	public static String getFieldsStr(Object o) {
		return getFieldsStr(o, true);
	}
	
	/***/
	public static String getFieldsStr(Object o, boolean onlyPublic) {
		StringBuilder sb = new StringBuilder();
		Field[] fs;
		if(onlyPublic) {
			fs = o.getClass().getFields();
		}
		else {
			fs = o.getClass().getDeclaredFields();
		}
		for(int i = 0; i < fs.length; ++i) {
			String name = fs[i].getName();
			Object v = null;
			try {
				v = fs[i].get(o);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			sb.append(name + ":").append(v).append(",");
		}
		return sb.toString();
	}
}
