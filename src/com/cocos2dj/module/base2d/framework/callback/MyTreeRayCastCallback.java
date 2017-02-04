package com.cocos2dj.module.base2d.framework.callback;

import com.cocos2dj.module.base2d.framework.PhysicsObject;
import com.cocos2dj.module.base2d.jbox2d.BroadPhase;
import com.cocos2dj.module.base2d.jbox2d.RayCastInput;
import com.cocos2dj.module.base2d.jbox2d.TreeRayCastCallback;
public class MyTreeRayCastCallback implements TreeRayCastCallback {
	private BroadPhase broadPhase;
	private PhysicsObject[] results;
	private int queryCount;
	
	/**@return 最终查询到的物理对象个数*/
	public int getQueryCount(){
		return queryCount;
	}
	
	public final void initRayCast(PhysicsObject[] results, BroadPhase broadPhase){
//	public void setResults(PhysicsObject[] results){
		queryCount = 0;
		this.results = results;
		this.broadPhase = broadPhase;
	}
	
	@Override
	public float raycastCallback(RayCastInput input, int nodeId) {
		//返回0则停止测试
		//返回小于0的数继续测试
		//返回大于0的数更改范围继续测试
		PhysicsObject temp = (PhysicsObject) broadPhase.getUserData(nodeId);
		if(!temp.sleep && !temp.checkRemoveFlag()){
			results[queryCount++] = temp;
		}

		if(queryCount < results.length) {
			return -1;
		}
		else {
			initRayCast(null, null);
			return 0;
		}
	}

//	@Override
//	public float raycastCallback(RayCastInput input, int nodeId) {
//		// TODO Auto-generated method stub
//		return 0;
//	}

}