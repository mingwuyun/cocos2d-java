package tests.testcase;

import com.badlogic.gdx.graphics.Color;
import com.cocos2dj.base.Director;
import com.cocos2dj.renderer.TextureRegion;
import com.cocos2dj.s2d.Node;
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
		addTestCase("Alpha Color Test", ()->{return new SpriteTestAlphaTest();});
		addTestCase("Rotate Color Scale Pos Size", ()->{return new SpriteTest1();});
		addTestCase("Change Frame", ()->{return new SpriteTestChangeFrame();});
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
		
		public void onEnter() {
			super.onEnter();
			
			Sprite spriteColorR = (Sprite) Sprite.create("powered.png").addTo(this);
			spriteColorR.setPosition(100, 300);
			spriteColorR.setColor(Color.RED);
			Sprite spriteColorG = (Sprite) Sprite.create("powered.png").addTo(this);
			spriteColorG.setPosition(300, 300);
			spriteColorG.setColor(Color.GREEN);
			Sprite spriteColorB = (Sprite) Sprite.create("powered.png").addTo(this);
			spriteColorB.setPosition(500, 300);
			spriteColorB.setColor(Color.BLUE);
			
			//use cascadeColor
			Node root = Node.create().addTo(this);
			
			//传导颜色和透明度
			root.setCascadeColorEnabled(true);
			root.setCascadeOpacityEnabled(true);
			
			Sprite.create("powered.png").addTo(root).setPosition(-100, 0);
			Sprite.create("powered.png").addTo(root).setPosition(100, 0);
			
			root.setPosition(800, 300);
			root.setColor(Color.GOLD);
			
			root.setOpacity(0.5f);
		}
	}
	
	//TODO 剔除测试
	static class SpriteCullingTest extends SpriteTestBase {
		
	}
	
	//TODO 添加／删除测试
	static class SpriteAddRemoveTest extends SpriteTestBase {
		
	}
}
