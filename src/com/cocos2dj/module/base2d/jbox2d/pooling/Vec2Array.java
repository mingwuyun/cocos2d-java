package com.cocos2dj.module.base2d.jbox2d.pooling;

import com.badlogic.gdx.math.Vector2;



public class Vec2Array extends DynamicTLArray<Vector2> {

	@Override
	protected Vector2[] getInitializedArray(int argLength) {
		final Vector2[] ray = new Vector2[argLength];
		for(int i=0; i<ray.length; i++){
			ray[i] = new Vector2();
		}
		return ray;
	}

}
