package com.cocos2dj.module.base2d;

import com.cocos2dj.base.Director;
import com.cocos2dj.basic.BaseUpdater;
import com.cocos2dj.module.Module;
import com.cocos2dj.module.base2d.framework.Base2D;
import com.cocos2dj.module.base2d.framework.PhysicsObjectType;
import com.cocos2dj.module.base2d.framework.PhysicsScene;
import com.cocos2dj.module.base2d.framework.collision.AABBShape;
import com.cocos2dj.module.base2d.framework.collision.Shape;
import com.cocos2dj.protocol.IScene;
import com.cocos2dj.protocol.IUpdater;

/**
 * ModuleBase2d.java
 * <p>
 * 
 * Base2D物理引擎模块， 基于速度。 该引擎善于模拟受控物体 <p>
 * 
 * @author Copyright(c) 2017 xujun
 */
public class ModuleBase2d extends Module implements IUpdater {
	
	public static final String ModuleId = "Base2D";
	public static final String ModuleType = "Physics2D";

	
	public ModuleBase2d() {
		super(ModuleId, ModuleType);
		if(base2d == null) {
			base2d = Base2D.initBase2D();
		}
	}

	
	@Override
	public void onEnter(IScene scene, Object config) {
		physicsScene = new PhysicsScene();
		base2d.loadScene(physicsScene);
		if(updater != null) {
			updater.kill();
		}
		updater =  Director.getInstance().getScheduler().mainSchedulePerFrame(this, physicsScene, 0, false);
	}

	@Override
	public void onExit(IScene scene) {
		base2d.destroyScene();
		physicsScene = null;
		updater.kill();
		updater = null;
	}
	
	@Override
	public final boolean update(float dt) {
		base2d.step(dt);
		return false;
	}
	
	
	//fields>>
	private Base2D 			base2d;
	private PhysicsScene	physicsScene;
	BaseUpdater				updater;
	//fields<<
	
	
	//create>>
	/*
	 * 推荐使用下面的方法创建对象，避免初始化顺序错误
	 */
	public ComponentPhysics createDynamicObject(Shape shape) {
		if(physicsScene == null) {
			return null;
		}
		ComponentPhysics phy = new ComponentPhysics();
		phy.addShape(shape);
		physicsScene.add(phy);
		return phy;
	}
	
	public ComponentPhysics createDynamicObjectWithAABB(float x0, float y0, float x1, float y1) {
		AABBShape shape = new AABBShape();
		shape.setAABBShape(x0, y0, x1, y1);
		return createDynamicObject(shape);
	}
	
	public ComponentPhysics createStaticObject(Shape shape, float x, float y) {
		if(physicsScene == null) {
			return null;
		}
		ComponentPhysics phy = new ComponentPhysics(PhysicsObjectType.Static);
		phy.addShape(shape);
		physicsScene.add(phy);
		phy.setPosition(x, y);
		return phy;
	}
	
	public ComponentPhysics createMoveObject(Shape shape) {
		if(physicsScene == null) {
			return null;
		}
		ComponentPhysics phy = new ComponentPhysics(PhysicsObjectType.Move);
		phy.addShape(shape);
		physicsScene.add(phy);
		return phy;
	}
	
	public ComponentPhysics createDetectObject(Shape shape) {
		if(physicsScene == null) {
			return null;
		}
		ComponentPhysics phy = new ComponentPhysics(PhysicsObjectType.Detect);
		phy.addShape(shape);
		physicsScene.add(phy);
		return phy;
	}
	
	public ComponentPhysics createDetectObjectWithAABB(float x0, float y0, float x1, float y1) {
		AABBShape shape = new AABBShape();
		shape.setAABBShape(x0, y0, x1, y1);
		return createDetectObject(shape);
	}
	
	public ComponentPhysics createStaticObjectWithAABB(float x, float y, float width, float height) {
		AABBShape shape = new AABBShape();
		shape.setAABBShape(0, 0, width, height);
		return createStaticObject(shape, x, y);
	}
	
	public ComponentPhysics createStaticObjectWithAABBWorld(float x0, float y0, float x1, float y1) {
		AABBShape shape = new AABBShape();
		shape.setAABBShape(0, 0, x1 - x0, y1 - y0);
		return createStaticObject(shape, x0, y1);
	}
	//static<<
	
}
