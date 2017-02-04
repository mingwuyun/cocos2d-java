package com.cocos2dj.module.base2d.jbox2d;

import com.badlogic.gdx.math.Vector2;

public class RaycastResult {
	public float lambda = 0.0f;
	public final Vector2 normal = new Vector2();
	
	public RaycastResult set(RaycastResult argOther){
		lambda = argOther.lambda;
		normal.set( argOther.normal);
		return this;
	}
}
