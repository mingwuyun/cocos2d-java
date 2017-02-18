package com.cocos2dj.module.base2d.framework.pipeline;

import com.badlogic.gdx.math.Vector2;
import com.cocos2dj.module.base2d.framework.PhysicsObject;
import com.cocos2dj.module.base2d.framework.PhysicsScene;
import com.cocos2dj.module.base2d.framework.common.TempArray;
import com.cocos2dj.module.base2d.framework.common.TimeInfo;
import com.cocos2dj.module.base2d.jbox2d.BroadPhase;

/**位置处理管线<p>
 * 
 * @author xujun
 * Copyright (c) 2015. All rights reserved. */
public class PosSolver {
	
	public final void solve(final TimeInfo time, final PhysicsScene scene) {
		final TempArray<PhysicsObject> dynamicList = scene._getMoveObjects();
		final int n = dynamicList.size();
		final BroadPhase broadPhase = scene._getBroadPhase();
		
		for(int i = 0; i < n; ++i) {
			final PhysicsObject temp = dynamicList.get(i);
			
			if(temp.isSleep()) {
				continue;
			}
			
			//更新速度
			temp.updateObject();
			temp.updateVelocity(time);
			
		
			switch(temp.getPhysicsObjectType()) {
			case Detect: {
					temp.sweep(time);
				}
				break;
			case Dynamic: {
					final Vector2 distance = temp.sweep(time);
					broadPhase.moveProxy(temp.getProxy(), temp.getSweepAABB(), distance);
				}
				break;
			case Move: {
					temp.move(time);
					temp.listener.onUpdatePosition(temp);
				}
				break;
			case Static: 
				break;
			}
		}
	}
}
