package com.cocos2dj.module.base2d.framework.callback;

import com.cocos2dj.module.base2d.framework.PhysicsObject;
import com.cocos2dj.module.base2d.jbox2d.BroadPhase;
import com.cocos2dj.module.base2d.jbox2d.TreeCallback;

/**
 * 查询回调函数（返回查询的物理对象个数以及引用）
 * @author xu jun
 * Copyright (c) 2015. All rights reserved.
 */
public class MyTreeCallback implements TreeCallback {
	private BroadPhase broadPhase;
	private PhysicsObject[] results;
	private int queryCount;
	
	/**@return 最终查询到的物理对象个数*/
	public final int getQueryCount(){
		return queryCount;
	}
	
	public final void init(PhysicsObject[] results, BroadPhase broadPhase){
		queryCount = 0;
		this.results = results;
		this.broadPhase = broadPhase;
	}
	
	@Override
	public boolean treeCallback(int proxyId) {
		PhysicsObject temp = (PhysicsObject) broadPhase.getUserData(proxyId);
		if(!temp.sleep && !temp.checkRemoveFlag()){
			results[queryCount++] = temp;
		}
		init(null, null);
		return queryCount < results.length;
	}
	
}