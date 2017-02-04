package tests.testcase;

import com.cocos2dj.module.base2d.framework.Base2D;
import com.cocos2dj.module.base2d.framework.PhysicsObject;
import com.cocos2dj.module.base2d.framework.PhysicsObjectType;
import com.cocos2dj.module.base2d.framework.PhysicsScene;
import com.cocos2dj.s2d.Sprite;

import tests.TestCase;
import tests.TestSuite;

/**
 * Base2D 物理引擎测试
 * 
 * @author xj
 *
 */
public class Base2dTests extends TestSuite {
	
	public Base2dTests() {
		addTestCase("Base2DTest1", ()->{return new Base2DTest1();});
	}
	
	static class Base2DDemo extends TestCase {
		
	}
	
	//不用插件运行base2d
	static class Base2DTest1 extends Base2DDemo {
		
		PhysicsScene scene;
		Sprite spr1;		PhysicsObject phy1;
		Sprite spr2;		PhysicsObject phy2;
		Sprite sprGround;	PhysicsObject phyGround;		//地面
		
		public void onEnter() {
			Base2D.initPhysicsCard2D();
			
			super.onEnter();
			scene = new PhysicsScene();
			Base2D.instance().loadScene(scene);
			
			spr1 = Sprite.create("powered.png"); addChild(spr1);
			spr2 = Sprite.create("powered.png"); addChild(spr2);
			sprGround = Sprite.create("powered.png"); addChild(sprGround);
			
			phy1 = new PhysicsObject();
			phy2 = new PhysicsObject();
			phyGround = new PhysicsObject(PhysicsObjectType.Static);
			
			phy1.createShapeAsAABB(-50, -50, 50, 50);
			spr1.setContentSize(100, 100);
			spr1.scheduleUpdate();
			spr1.setOnUpdateCallback((node, dt)->{
				spr1.setPosition(phy1.getPosition());
			});
			phy1.setPosition(400, 400);
			phy1.setVelocityX(5);
			phy1.setAccelerateY(-0.5f);
			
			
			phy2.createShapeAsAABB(-50, -50, 50, 50);
			spr2.setContentSize(100, 100);
			spr2.scheduleUpdate();
			spr2.setOnUpdateCallback((node, dt)->{
				spr2.setPosition(phy2.getPosition());
			});
			phy2.setPosition(800, 300);
			phy2.setAccelerateY(-0.5f);
			
			phyGround.createShapeAsAABB(0, 0, 1200, 100);
			sprGround.setRect(0, 0, 1200, 100);
			
			scene.add(phy1);
			scene.add(phy2);
			scene.add(phyGround);
			scheduleUpdate();
		}
		
		public boolean update(float dt) {
			super.update(dt);
			Base2D.instance().step(dt);
			return false;
		}
		
		public String subtitle() {
			return "physicsTest";
		}
	}
}
