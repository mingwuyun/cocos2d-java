package com.cocos2dj.module.base2d.jbox2d.pooling;

import com.cocos2dj.module.base2d.framework.common.AABB;

public class TLAABB extends ThreadLocal<AABB> {
	protected AABB initialValue(){
		return new AABB();
	}
}
