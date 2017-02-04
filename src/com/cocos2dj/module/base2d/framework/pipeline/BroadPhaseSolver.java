package com.cocos2dj.module.base2d.framework.pipeline;

import com.cocos2dj.module.base2d.framework.PhysicsObject;
import com.cocos2dj.module.base2d.framework.PhysicsScene;
import com.cocos2dj.module.base2d.framework.common.TempArray;
import com.cocos2dj.module.base2d.jbox2d.BroadPhase;

/**粗检测管线<p>
 * 
 * @author xujun
 * Copyright (c) 2015. All rights reserved. */
public class BroadPhaseSolver {
	
	public final void solve(final PhysicsScene scene) {
		final BroadPhase broadPhase = scene._getBroadPhase();
		//detect 物体不维护broadPhase，直接查询
		final TempArray<PhysicsObject> detectObjects = scene._getDetectObjects();
		for(int i = 0, n = detectObjects.size(); i < n; ++i) {
			final PhysicsObject temp = detectObjects.get(i);
			scene._setCurrectDetectObject(temp);
//			System.out.println(temp.sweepAABB);
			broadPhase.query(scene, temp.getSweepAABB());
		}
		
		//dynamicTree
		broadPhase.updatePairs(scene);
	}
	
}
