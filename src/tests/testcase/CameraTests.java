package tests.testcase;

import com.cocos2dj.base.Rect;
import com.cocos2dj.s2d.Camera;
import com.cocos2dj.s2d.Sprite;

import tests.TestCase;
import tests.TestSuite;

/**
 * 相机测试测试
 * 
 * @author xujun
 */
public class CameraTests extends TestSuite {
	
	public CameraTests() {
		addTestCase("CullingCameraTest", ()->{return new CullingCameraTest();});
		addTestCase("MultiCameraTest", ()->{return new MultiCameraTest();});
	}
	
	static class CameraTest extends TestCase {}
	
	/////////////////////////////////////
	//TODO 多相机测试
	static class MultiCameraTest extends CameraTest {
		
		Sprite c1Spr1, c1Spr2;
		Sprite c2Spr1;//, c2Spr2;
		
		Camera camera1;
		Camera camera2;
		
		public void onEnter() {
			super.onEnter();
			
			// 两个相机
			camera1 = getDefaultCamera();
			camera1.setCameraFlag(Camera.USER1);
			camera2 = Camera.create();
			camera2.setCameraFlag(Camera.USER2);
			addChild(camera2);
			
			camera2.setDepth(1);
			camera1.setDepth(2);
			
			
			c2Spr1 = Sprite.create("background.png", Rect.Get(0, 0, 1136, 640));
			c1Spr1 = Sprite.create("SpinningPeas.png");
			c1Spr2 = Sprite.create("Pea.png");
			addChild(c2Spr1);
			addChild(c1Spr1); addChild(c1Spr2);
			
			c2Spr1.setCameraMask(Camera.USER2);
			
			c1Spr1.setPosition(400, 200);
			c1Spr1.setCameraMask(Camera.USER1); 
			c1Spr2.setPosition(800, 200);
			c1Spr2.setCameraMask(Camera.USER1);
			
			//两个相机以不同到速度移动
			camera2.schedule((dt)->{
				camera2.setPositionX(camera2.getPositionX() + 5f);
//				System.out.println("bg inBounds = " + c2Spr1.isInsideBounds());
				return false;
			}, 0.02f);
			
			camera1.schedule((dt)->{
				camera1.setPositionX(camera1.getPositionX() + 1f);
//				System.out.println("image1 inBounds = " + c1Spr1.isInsideBounds());
//				System.out.println("image2 inBounds = " + c1Spr2.isInsideBounds());
				return false;
			}, 0.02f);
		}
	}
	
	
	/////////////////////////////////////
	//TODO 剔除测试
	static class CullingCameraTest extends CameraTest {
		
		Sprite c1Spr1, c1Spr2;
		Sprite c2Spr1;
		
		Camera camera1;
		Camera camera2;
		
		public void onEnter() {
			super.onEnter();
			
			// 两个相机
			camera1 = getDefaultCamera();
			camera2 = Camera.create();
			camera2.setCameraFlag(0x0002);
			addChild(camera2);
			
			camera2.setDepth(1);
			camera1.setDepth(2);
			
			
			c2Spr1 = Sprite.create("background.png", Rect.Get(0, 0, 320, 200)); c2Spr1.setName("c2Spr1");
			c1Spr1 = Sprite.create("SpinningPeas.png");
			c1Spr2 = Sprite.create("Pea.png");
			c2Spr1.setRotation(45);
			
			addChild(c2Spr1);
			addChild(c1Spr1); 
			addChild(c1Spr2);
			
			c2Spr1.setCameraMask(0x0002);
			
			c1Spr1.setPosition(400, 200);
			c1Spr1.setCameraMask(0x0001); 
			c1Spr2.setPosition(800, 200);
			c1Spr2.setCameraMask(0x0001);
			
			c2Spr1.scheduleUpdate();
			c2Spr1.setOnUpdateCallback((node, t)->{
				c2Spr1.setPositionX(c2Spr1.getPositionX() + 5);
				System.out.println("culling inBounds bg = " + c2Spr1.isInsideBounds());
			});
			
			c1Spr1.scheduleUpdate();
			c1Spr1.setOnUpdateCallback((node, t)->{
				c1Spr1.setPositionX(c1Spr1.getPositionX() - 5);
				System.out.println("culling inBounds 1 = " + c1Spr1.isInsideBounds());
			});
			
			c1Spr2.scheduleUpdate();
			c1Spr2.setOnUpdateCallback((node, t)->{
				c1Spr2.setPositionY(c1Spr2.getPositionY() + 5);
				System.out.println("culling inBounds 2 = " + c1Spr2.isInsideBounds());
			});
		}
	}
}
