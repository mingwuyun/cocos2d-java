package tests.testcase;

import com.cocos2dj.base.Director;
import com.cocos2dj.renderer.TextureRegion;
import com.cocos2dj.s2d.Sprite;

import tests.TestCase;
import tests.TestSuite;

/**
 * Sprite 测试
 *  
 * @author xujun
 */
public class SpriteTests extends TestSuite {

	public SpriteTests() {
		addTestCase("Rotate Color Scale Pos Size", ()->{return new SpriteTest1();});
		addTestCase("Change Frame", ()->{return new SpriteTestChangeFrame();});
		addTestCase("Alpha Test", ()->{return new SpriteTestAlphaTest();});
//		addTestCase("Culling Test", ()->{return new SpriteCullingTest();});
//		addTestCase("Add Remove Test", ()->{return new SpriteAddRemoveTest();});
	}
	
	//先留着看有没有用
	static class SpriteTestBase extends TestCase {}
	
	//TODO 测试旋转／颜色／放缩／位置／指定尺寸等功能
	static class SpriteTest1 extends SpriteTestBase {
		
		Sprite spriteRotate;
		Sprite spriteColor;
		Sprite spriteScale;
		Sprite spritePos;
		Sprite spriteSize;
		
		public void onEnter() {
			super.onEnter();
			spritePos = (Sprite) Sprite.create("powered.png").addTo(this);
			spritePos.setPosition(600, 400);
			
			spriteRotate = (Sprite) Sprite.create("powered.png").addTo(this);
			spriteRotate.setPosition(100, 100);
			spriteRotate.setRotation(60);
		}
	}
	
	static class SpriteTestChangeFrame extends SpriteTestBase {
		
		TextureRegion frame1;
		TextureRegion frame2;
		TextureRegion frame3;
		Sprite sprite;
		public void onEnter() {
			super.onEnter();
			
			frame1 = Director.getInstance().getTextureCache().addImage("Pea.png").createTextureRegion();
			frame2 = Director.getInstance().getTextureCache().addImage("powered.png").createTextureRegion();
			frame3 = Director.getInstance().getTextureCache().addImage("SpinningPeas.png").createTextureRegion();
			
			sprite = (Sprite) Sprite.create().addTo(this);
			sprite.setPosition(400, 200);
			
			Director.getInstance().getScheduler().schedule((t)->{
				sprite.setSpriteFrame(frame2, true);	//重置尺寸数据
				return true;
			}, this, 0, 0, 1, false);
			Director.getInstance().getScheduler().schedule((t)->{
				sprite.setSpriteFrame(frame3, false);	//维持上个尺寸数据
				return true;
			}, this, 0, 0, 2, false);
			
			sprite.initWithSpriteFrame(frame1);
		}
	}
	
	//TODO alpha （父子节点） 测试
	static class SpriteTestAlphaTest extends SpriteTestBase {
		
//		Sprite spriteRotate;
//		Sprite spriteColor;
//		Sprite spriteScale;
//		Sprite spritePos;
		
		public void onEnter() {
			super.onEnter();
			
		}
	}
	
	//TODO 剔除测试
	static class SpriteCullingTest extends SpriteTestBase {
		
	}
	
	//TODO 添加／删除测试
	static class SpriteAddRemoveTest extends SpriteTestBase {
		
	}
}
