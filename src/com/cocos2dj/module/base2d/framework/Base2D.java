package com.cocos2dj.module.base2d.framework;

import com.badlogic.gdx.math.Vector2;
import com.cocos2dj.module.base2d.framework.callback.MyTreeCallback;
import com.cocos2dj.module.base2d.framework.callback.MyTreeRayCastCallback;
import com.cocos2dj.module.base2d.framework.callback.VelocityLimitListener;
import com.cocos2dj.module.base2d.framework.collision.FloatPair;
import com.cocos2dj.module.base2d.framework.common.AABB;
import com.cocos2dj.module.base2d.framework.common.TimeInfo;
import com.cocos2dj.module.base2d.jbox2d.BroadPhase;
import com.cocos2dj.module.base2d.jbox2d.RayCastInput;
/**
 * Base2D<p>
 * 
 * card2D引擎的物理系统部分。系统构建于jbox2D的broadPhase算法之上，并对其内容进行了一些修改。
 * 系统采用纯Java实现 本版替换了原先的扫掠排序法改为了对应的动态树。<p>
 * 
 * 此物理系统变化的地方有：<ul>
 * <li>对于简单图形碰撞反应的支持.card2DPhysics使用了AABB加速碰撞计算。使其可以运用到
 * 一般的游戏当中充当物理模块（如RPG和一些对物理要切不高的ACT）取消了物理上旋转。
 * 不过可以实现"伪旋转"（未完成）——强制更新图形信息，模拟旋转。AABBShape不可执行
 * 角度的计算，使物理计算耗时相比JBox2D的复杂算法小的多。
 * <li>支持的形状有AABBShape、Polygon、Circle 其中<b>Polygon最大边数为6</b> 在使用时需注意。
 * <li>碰撞检测与分离的算法该变，采用取MTD实现分离计算。当为传感器模式时不进行
 * MTD向量的计算 
 * <li>碰撞支持无弹性碰撞——即接触后不进行动力计算 方便某些方面的应用
 * <li>多个形状可以附着在一个对象上 可以组合出复杂的形状
 * </ul>
 * <p>
 * 
 * 本系统的主要功能不是“物理仿真”，所以系统中没有提供“力”的概念。
 * Card2DPhysics的重点在于对物理碰撞的侦测以及对对象的控制上。在这个系统中可以方便的对物体实施
 * 一些“反物理”的控制。但为此付出的代价是Card2DPhysics并不适用强调物理真实性的情况。
 * 
 * @author xu jun
 * Copyright (c) 2015. All rights reserved.
 */
public class Base2D {
	
	/**
	 * 创建限定双方向速度的限制器
	 * 
	 * @param limitX (limitX >= 0)
	 * @param limitY (limitY >= 0)
	 * @return
	 */
	public static VelocityLimitListener create(final float limitX, final float limitY) {
		return new VelocityLimitListener() {
			@Override
			public void onVelocity(Vector2 velocity) {
				if(velocity.y > limitY) {
					velocity.y = limitY;
				} else if(velocity.y < -limitY) {
					velocity.y = -limitY;
				}
				
				if(velocity.x > limitX) {
					velocity.x = limitX;
				} else if(velocity.x < -limitX) {
					velocity.x = -limitX;
				}
			}
		};
	}
	
	/**支持的最大多边形数*/
	public static final int MAX_POINT = 8;	
	/**单个物理对象最大支持的generator数量 */
	public static final int MAX_GENERATOR = 12;
	/**system*/
	private static Base2D instance;
	
	
	/**仿真器用来处理物体的移动*/
	private Simulator simulator;
	private PhysicsScene currentScene;
	/**时间信息*/
	private final TimeInfo time = new TimeInfo();
	private static PhysicsConfig config = new PhysicsConfig();
	
	private static VelocityLimitListener limitListener = VelocityLimitListener.NULL;
	public static final void setDefaultVelocityLimitListener(VelocityLimitListener listener) {
		limitListener = (listener == null) ? VelocityLimitListener.NULL : listener;
	}
	public static final VelocityLimitListener getDefaultVelocityLimitListener() {
		return limitListener;
	}
	
	/**载入配置 */
	public static void loadConfig(PhysicsConfig argconfig) {
		if(argconfig != null) {
			config = argconfig;
		}
	}
	
	private Base2D(){
		time.dt = config.dt;
		time.setIteration(config.iteration);
		simulator = new Simulator();
		simulator.initSimulator();
	}

	
	
	BroadPhase _getBroadPhase() {
		return simulator.broadPhase;
	}
	
//	public static 
	/**特殊的范围查询回调函数*/
	private static final MyTreeCallback treeCallback = new MyTreeCallback();
	
	/**查询当前场景中某一范围（AABB）中的物理对象，结果存放在results中
	 * 最终查询到的有效结果个数会返回
	 * @param aabb
	 * @param results 
	 * @return 查询结果数量 */
	public static final int queryPhysicsObject(AABB aabb, PhysicsObject[] results){
		final BroadPhase broadPhase = instance.simulator.broadPhase;
		treeCallback.init(results, broadPhase);
		broadPhase.query(treeCallback, aabb);
		return treeCallback.getQueryCount();
	}
	
	/**查询指定范围（AABB）内的物理对象 
	 * @param treeCallback 回调函数 */
//	public static final void queryPhysicsObject(AABB aabb, TreeCallback treeCallback){
//		instance.simulator.broadPhase.query(treeCallback, aabb);
//	}
	
	/**特殊的线段查询回调函数*/
	private static final MyTreeRayCastCallback treeRayCastCallback = 
			new MyTreeRayCastCallback();
	
	/**查询当前场景与指定线段相交的物理对象，结果存放在results中
	 * 最终查询到的有效结果个数会返回
	 * @param 
	 * @param results 
	 * @return 查询结果数量 */
	public static final int queryPhysicsObject(RayCastInput input, PhysicsObject[] results) {
		final BroadPhase broadPhase = instance.simulator.broadPhase;
		treeRayCastCallback.initRayCast(results, broadPhase);
		broadPhase.raycast(treeRayCastCallback, input);
		return treeRayCastCallback.getQueryCount();
	}

	/**必须先调用<code>initSystem(final int PHYSICS_SYSTEM_MODE)</code><br>
	 * 否则返回为null*/
	public static Base2D instance(){
		return instance;
	}

	/**初始化物理系统<br>
	 * @return */
	public static Base2D initBase2D(){
		if(instance == null) {
			instance = new Base2D();
		}
		return instance;
	}
	
	/**获取标准模拟时间间隔（ms）
	 * 
	 * @return */
	public final float getStepDelta() {
		return time.dt;
	}
	
	/**获取模拟时的迭代次数 */
	public final int getIteration() {
		return time.iteration;
	}
	
	/**设置物理模拟信息
	 * @param dt 模拟间隙
	 * @param iteration 迭代次数 */
	public final void setStepInfo(final float dt,final int iteration){
		time.setIteration(iteration);//iteration;
		time.dt = dt;
	}
	
	/**清除当前场景（C2PhysicsScene）的数据 */
	final void clearSceneData(){
		if(this.currentScene != null) {
			this.currentScene.release();
			this.currentScene.setAttachSystem(false);
			//将broadPhase等记录清空
			simulator.reset();
		}
		currentScene = null;
	}
	
	/**装载物理场景<br>
	 * 销毁broadPhase记录->装载scene参数->初始化scene*/
	public void loadScene(final PhysicsScene scene){
		clearSceneData();
		
		/* 装载场景*/
		this.currentScene = scene;
		simulator.loadPhysicsScene(this.currentScene);
		//设置装载属性
		this.currentScene.setAttachSystem(true);
	}
	
	public String getStatus() {
		return simulator.toString();
	}
	
	/**调用此方法对加入scene中的对象进行物理仿真
	 * @param timeStep 相对于上次执行间隔的时间(ms)*/
	public final void step(final float timeStep){
		if(currentScene == null){
			return;
		}
		
		time.update(timeStep);
		
		currentScene.setSceneLock(true);
		
		simulator.step(time, currentScene);
		
		currentScene.setSceneLock(false);
	}
	
	/**销毁系统处理的场景（主要用于切换）
	 * 会销毁场景中的物理对象 */
	public final void destroyScene(){
		currentScene.release();
		//将broadPhase等记录清空
		simulator.reset();
	}
	
	/** 用于物理系统的销毁*/
	public final void dispose(){
		simulator.release();
		simulator = null;
		FloatPair.release();
		instance = null;
//		treeCallback = null;
//		treeRayCastCallback = null
	}
	
	/**获取现在系统中处理的场景
	 * @return scene */
	public final PhysicsScene getCurrPhysicsScene(){
		return currentScene;
	}
	
	/*Debug*/
	public void debug(){
//		System.out.println("From C2PhysicsSystem: ");
//		System.out.println("physicsobject count: "+(scene.getMoveObjectCount()+
//				scene.getStaticObjectCount()));
//		simulator.debug();
	}
}