package tests.testcase;

import com.badlogic.gdx.math.Vector2;
import com.cocos2dj.module.base2d.ComponentPhysics;
import com.cocos2dj.module.base2d.ComponentPhysics.ContactCallback;
import com.cocos2dj.module.base2d.ModuleBase2d;
import com.cocos2dj.module.base2d.framework.collision.ContactCollisionData;
import com.cocos2dj.s2d.DrawNode;
import com.cocos2dj.s2d.Node;
import com.cocos2dj.s2d.Sprite;

import tests.TestCase;
import tests.TestSuite;

/**
 * Base2D 物理引擎测试
 * 
 * @author xu jun
 */
public class Base2dTests extends TestSuite {
	
	public Base2dTests() {
		addTestCase("Base2DTest1", ()->{return new Base2DTest1();});
	}
	
	static class Base2DDemo extends TestCase {
		
	}
	
	/////////////////////
	//小人
	static class Mario {
		Node 				aim;
		DrawNode			debugDraw;
		ComponentPhysics	body;
	}
	/////////////////////
	
	//Base2D运行测试 测试了碰撞监听和休眠
	// test contactListener and sleep/awake
	static class Base2DTest1 extends Base2DDemo {
		
		Sprite spr1;		ComponentPhysics phy1;
		Sprite spr2;		ComponentPhysics phy2;
		Sprite sprGround;	ComponentPhysics phyGround;		//地面
		
		ModuleBase2d moduleBase2d;
		
		public void onEnter() {
			super.onEnter();
			
			// 添加moduleBase2d插件
			moduleBase2d = (ModuleBase2d) addModule(new ModuleBase2d());	
			
			spr1 = Sprite.create("powered.png"); //addChild(spr1);
			spr2 = Sprite.create("powered.png"); 
			addChild(spr2);
			sprGround = Sprite.create("powered.png"); 
			addChild(sprGround);
			
			Node spr1Node = Node.create();
			addChild(spr1Node);
			spr1Node.addChild(spr1);
			spr1Node.setPosition(200, 200);
			
			phy1 = moduleBase2d.createDynamicObjectWithAABB(-50, -50, 50, 50).bindNode(spr1);
			//spr1.addComponent(phy1);
			spr1.setContentSize(100, 100);
			phy1.setVelocityX(5);
			phy1.setAccelerateY(-0.5f);
			spr1.setPosition(400, 400);		//位置同步，设置node会自动同步到phy对象

			
			phy2 = moduleBase2d.createDynamicObjectWithAABB(-50, -50, 50, 50).bindNode(spr2);
			spr2.setContentSize(100, 100);
			phy2.setAccelerateY(-0.5f);
			phy2.setPosition(800, 300);		//直接设置phy也可以
			
			phyGround = moduleBase2d.createStaticObjectWithAABB(0, 0, 1200, 100);
			sprGround.setRect(0, 0, 1200, 100);
			
			schedule((t)->{
				phy1.setPosition(500, 200);
				return false;
			}, 2);
			schedule((t)->{
				//唤醒 phy2
				phy2.setSleep(false);
				spr2.setVisible(true);
				phy2.setPosition(800, 300);
				return false;
			}, 3);
			
			//碰撞监听
			phy1.setContactCallback(new ContactCallback() {
				@Override
				public void onContactDestroyed(ComponentPhysics self, ComponentPhysics other, Vector2 MTD, ContactCollisionData data) {
					System.out.println("onContactDestroyed");
				}
				@Override
				public void onContactPersisted(ComponentPhysics self, ComponentPhysics other, Vector2 MTD, ContactCollisionData data) {
					
				}
				@Override
				public void onContactCreated(ComponentPhysics self, ComponentPhysics other, Vector2 MTD, ContactCollisionData data) {
					Node otherNode = other.getOwner();
					//phy2休眠
					if(otherNode != null) {
						otherNode.setVisible(false);
						other.setSleep(true);
					}
				}
			});
		}
		
		
		public String subtitle() {
			return "contactListener and sleep/awake";
		}
	}
	
	
	//Base2D运行测试 测试 多边形和地面检测
	// test polygon and ground check
	static class Base2DTest2 extends Base2DDemo {
		
		ComponentPhysics phy1;
//		ComponentPhysics phy2;
		ComponentPhysics phyGround;		//地面
		
		ModuleBase2d moduleBase2d;
		
		public void onEnter() {
			super.onEnter();
			
			// 添加moduleBase2d插件
			moduleBase2d = createModule(ModuleBase2d.class);	
			
//			spr1 = Sprite.create("powered.png"); //addChild(spr1);
//			spr2 = Sprite.create("powered.png"); 
//			addChild(spr2);
//			sprGround = Sprite.create("powered.png"); 
//			addChild(sprGround);
//			
//			Node spr1Node = Node.create();
//			addChild(spr1Node);
//			spr1Node.addChild(spr1);
//			spr1Node.setPosition(200, 200);
//			
//			phy1 = moduleBase2d.createDynamicObjectWithAABB(-50, -50, 50, 50).bindNode(spr1);
//			//spr1.addComponent(phy1);
//			spr1.setContentSize(100, 100);
//			phy1.setVelocityX(5);
//			phy1.setAccelerateY(-0.5f);
//			spr1.setPosition(400, 400);		//位置同步，设置node会自动同步到phy对象
//
//			
//			phy2 = moduleBase2d.createDynamicObjectWithAABB(-50, -50, 50, 50).bindNode(spr2);
//			spr2.setContentSize(100, 100);
//			phy2.setAccelerateY(-0.5f);
//			phy2.setPosition(800, 300);		//直接设置phy也可以
//			
//			phyGround = moduleBase2d.createStaticObjectWithAABB(0, 0, 1200, 100);
//			sprGround.setRect(0, 0, 1200, 100);
//			
//			schedule((t)->{
//				phy1.setPosition(500, 200);
//				return false;
//			}, 2);
//			schedule((t)->{
//				//唤醒 phy2
//				phy2.setSleep(false);
//				spr2.setVisible(true);
//				phy2.setPosition(800, 300);
//				return false;
//			}, 3);
//			
//			//碰撞监听
//			phy1.setContactCallback(new ContactCallback() {
//				@Override
//				public void onContactDestroyed(ComponentPhysics self, ComponentPhysics other, Vector2 MTD, ContactCollisionData data) {
//					System.out.println("onContactDestroyed");
//				}
//				@Override
//				public void onContactPersisted(ComponentPhysics self, ComponentPhysics other, Vector2 MTD, ContactCollisionData data) {
//					
//				}
//				@Override
//				public void onContactCreated(ComponentPhysics self, ComponentPhysics other, Vector2 MTD, ContactCollisionData data) {
//					Node otherNode = other.getOwner();
//					//phy2休眠
//					if(otherNode != null) {
//						otherNode.setVisible(false);
//						other.setSleep(true);
//					}
//				}
//			});
		}
		
		
		public String subtitle() {
			return "contactListener and sleep/awake";
		}
	}
}
