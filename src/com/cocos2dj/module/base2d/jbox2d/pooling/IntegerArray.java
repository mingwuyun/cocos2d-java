package com.cocos2dj.module.base2d.jbox2d.pooling;


public class IntegerArray extends DynamicTLArray<Integer> {
	@Override
	protected final Integer[] getInitializedArray(int argLength) {
		return new Integer[argLength];
	}
}
