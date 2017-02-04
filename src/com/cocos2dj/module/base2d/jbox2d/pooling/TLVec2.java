package com.cocos2dj.module.base2d.jbox2d.pooling;

import com.badlogic.gdx.math.Vector2;


public class TLVec2 extends ThreadLocal<Vector2> {
	protected Vector2 initialValue(){
		return new Vector2();
	}
}
