package com.cocos2dj.module.base2d.framework;

import com.cocos2dj.module.base2d.framework.callback.ContactListener;
import com.cocos2dj.module.base2d.framework.callback.DefaultContactListener;
import com.cocos2dj.module.base2d.framework.collision.ContactPool;
import com.cocos2dj.module.base2d.framework.common.TimeInfo;
import com.cocos2dj.module.base2d.framework.pipeline.BroadPhaseSolver;
import com.cocos2dj.module.base2d.framework.pipeline.ContactSolver;
import com.cocos2dj.module.base2d.framework.pipeline.PosSolver;
import com.cocos2dj.module.base2d.jbox2d.BroadPhase;
import com.cocos2dj.module.base2d.jbox2d.DefaultBroadPhaseBuffer;
import com.cocos2dj.module.base2d.jbox2d.DynamicTreeFlatNodes;
/** Simulator<br>
 * 
 * 仿真器负责执行物理模拟 {@link #step(TimeInfo, C2PhysScene)}<p>
 * 
 * 原本分离出了碰撞管线对象，但仔细看看代码量少的可怜所以取消整个pipeline架构
 * 直接由这个Simulator处理
 * 
 * 其他方法用于内存管理以及数据状态查看<p>
 * 
 * @author xujun
 * Copyright (c) 2015. All rights reserved. */
public final class Simulator {

//	public BroadPhase createBroad(C2PhysLayer layer) {
//		return BroadPhasePool.getBroadPhasePool().get(layer.broadPhaseType);
//	}
	
	/**碰撞管线 */
//	PipelinePhys2D pipeline= new PipelinePhys2D();
	
	
	/**初始化仿真器 */
	final void initSimulator() {
		defaultContactListener = new DefaultContactListener();
		broadPhase = new DefaultBroadPhaseBuffer(new DynamicTreeFlatNodes());
		posSolver = new PosSolver();
		broadPhaseSolver = new BroadPhaseSolver();
		contactSolver = new ContactSolver();
	}
	
	ContactListener defaultContactListener;
	ContactListener contactListener;
	BroadPhase broadPhase;
	PosSolver posSolver;
	BroadPhaseSolver broadPhaseSolver;
	ContactSolver contactSolver;
	
	
	/**初始化物理场景<p>
	 * 
	 * 必须调用该方法处理scene后才能在step中调用该scene.
	 * 该方法会对scene中的layer进行分析并分配空间结构数据 <p>
	 * 
	 * @param scene */
	public final void loadPhysicsScene(final PhysicsScene scene){
		if(scene.getContactListener() != null) {
			this.contactListener = scene.getContactListener();
		}
		else {
			this.contactListener = defaultContactListener;
			scene.setContactListener(defaultContactListener);
		}
		
		scene._initPhysicsScene(broadPhase);
	}
	
	
	/**仿真器工作<br>
	 * 需要的参数是时间比率rate,处理物体迭代器
	 * @param time
	 * @param scene */
	public final void step(final TimeInfo time, final PhysicsScene scene){	
//		_time = System.currentTimeMillis();
//		try {
		scene.setSceneLock(true);
		
		scene.updateSceneObjects();
		posSolver.solve(time, scene);
		broadPhaseSolver.solve(scene);
		contactSolver.solve(contactListener, time, scene);
		
		scene.setSceneLock(false);
		
//		System.out.println("use ms = " + (System.currentTimeMillis() - _time));
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//			System.err.println(scene);
//		}
//		if(i++ > 50) {
//			System.out.println(scene);
//			i = 0;
//		}
	}
//	long _time;
//	int i = 0;
	
	/**仿真器相关对象的清理 <p>
	 * 
	 * ContactPool <br>
	 * BroadPhasePool <br> */
	public void reset() {
		ContactPool.getContactPool().reset();
		broadPhase.reset();
	}
	
	
	public void release(){
//		contacts.clear();
//		broadPhase = null;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("sumulator status------------------- \n");
		sb.append("[motorPool] \n").append("null");
		sb.append("[contactPool] \n").append(ContactPool.getContactPool());
//		sb.append("[broadPhasePool] \n").append(BroadPhasePool.getBroadPhasePool());
		return sb.toString();
	}
	
	
//	public void debug(){
//		ContactPool.getContactPool().showState();
//		System.out.println("from PhysicsSystem.Simulator : ");
//		System.out.println("contact count: "+contacts.size());
//		System.out.println("proxy count: "+broadPhase.getProxyCount());
//	}
}