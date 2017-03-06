package tests.testcase;

import com.cocos2dj.module.base2d.ComponentPhysics;
import com.cocos2dj.module.base2d.ModuleBase2d;
import com.cocos2dj.module.base2d.framework.collision.Shape;
import com.cocos2dj.s2d.DrawNode;

import tests.TestCase;
import tests.TestSuite;
import tests.testcase.Base2dTests.Mario;

public class Base2dTests2 extends TestSuite {
	
	public Base2dTests2() {
		addTestCase("StaticObjectTest", ()->{return new StaticObjectTest();});
		addTestCase("DetectObjectTest", ()->{return new DetectObjectTest();});
//		addTestCase("Base2DTest2", ()->{return new Base2DTest2();});
//		addTestCase("Base2DTest1", ()->{return new Base2DTest1();});
	}
	
	static class Base2DDemo extends TestCase {
		protected ModuleBase2d base2d;
		
		public void onEnter() {
			super.onEnter();
			base2d = createModule(ModuleBase2d.class);
		}
	}

	////////////////////////////
	//static test
	public static class StaticObjectTest extends Base2DDemo {
		
		Mario mario;
		
		public void onEnter() {
			super.onEnter();
			
			float[] args = new float[]{0, 0, 1500, 0, 0, 200};
//			float[] args = new float[]{0, 0, 1500, 0, 1500, 200};
			
			DrawNode ground = (DrawNode) DrawNode.create().addTo(this);
			ground.drawPolygon(args, null);
			ComponentPhysics phy = ModuleBase2d.createStatic();
			Shape shape = ModuleBase2d.createPolygon(args);
			phy.addShape(shape);
			
			phy.setPosition(100, 00);
			ground.setPosition(100, 00);
			
			phy.setFriction(1);
			phy.setStaticFriction(1);
			
			base2d.getCurrentPhysicsScene().add(phy);
			
			
			base2d.createStaticObjectWithAABB(0, -50, 1000, 100);
			
			mario = new Mario();
			mario.init(this);
			
			mario.aim.setPosition(500, 500);
		}
		
	}
	
	
	////////////////////////////
	//DetectTest
	public static class DetectObjectTest extends Base2DDemo {
		
		Mario mario;
		
		public void onEnter() {
			super.onEnter();
			
			float[] args = new float[]{
					0, 0,
					1500, 0,
					1500, 200
			};
			
			DrawNode ground = (DrawNode) DrawNode.create().addTo(this);
			ground.drawPolygon(args, null);
			ComponentPhysics phy = ModuleBase2d.createStatic();
			Shape shape = ModuleBase2d.createPolygon(args);
			phy.addShape(shape);
			
			phy.setPosition(100, 100);
			ground.setPosition(100, 100);
			base2d.getCurrentPhysicsScene().add(phy);
			
			
			base2d.createStaticObjectWithAABB(0, -50, 1000, 100);
			
			mario = new Mario();
			mario.init(this);
			mario.aim.setPosition(100, 200);
			
			{
			DrawNode testNode = (DrawNode) DrawNode.create().addTo(this);
			ComponentPhysics p = ModuleBase2d.createDetect();
			Shape s = ModuleBase2d.createAABB(0, 0, 50, 50);
			p.addShape(s);
			
			testNode.drawSolidRect(0, 0, 50, 50, null);
			testNode.addComponent(p);
			
			testNode.setPosition(200, 200);
			p.setAccelerateY(-1f);
			}
			
			{
			DrawNode testNode = (DrawNode) DrawNode.create().addTo(this);
			ComponentPhysics p = ModuleBase2d.createDetect();
			Shape s = ModuleBase2d.createAABB(0, 0, 50, 50);
			p.addShape(s);
			
			testNode.drawSolidRect(0, 0, 50, 50, null);
			testNode.addComponent(p);
			
			testNode.setPosition(400, 400);
			
			p.setAccelerateY(-1f);
			}
		}
		
	}
	
	
	
	///////////////////////////
	//TileTest
	
	
}
