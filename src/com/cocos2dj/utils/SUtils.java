package com.stormframework.util;

import java.lang.reflect.Field;

/**
 * 引擎常用工具<p>
 * 
 * @author xu jun
 */
public class SUtils {
	
	/**获取所有public权限的fields */
	public static String getFieldsStr(Object o) {
		return getFieldsStr(o, true);
	}
	
	/**获取该对象的fields*/
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
