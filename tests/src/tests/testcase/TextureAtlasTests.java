package tests.testcase;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.cocos2dj.macros.CC;
import com.cocos2dj.s2d.Sprite;

import tests.TestCase;
import tests.TestSuite;

public class TextureAtlasTests extends TestSuite {
	
	public TextureAtlasTests() {
		addTestCase("SpriteFrameCacheTest", ()->{return new SpriteFrameCacheTest();});
		addTestCase("GdxTextureAtlasTest", ()->{return new GdxTextureAtlasTest();});
		
	}
	
	static class TestDemo extends TestCase {
		
	}
	
	//TODO gdx格式直接读取读取
	static class GdxTextureAtlasTest extends TestDemo {
		public void onEnter() {
			super.onEnter();
			
			TextureAtlas atlas;
			atlas = CC.TextureAtlas("pack.atlas");
			
			Sprite.createWithSpriteFrame(atlas.findRegion("scene_b1")).addTo(this).setPosition(100, 300);
			Sprite.createWithSpriteFrame(atlas.findRegion("scene_b2")).addTo(this).setPosition(200, 300);
			Sprite.createWithSpriteFrame(atlas.findRegion("scene_b3")).addTo(this).setPosition(300, 300);
			Sprite.createWithSpriteFrame(atlas.findRegion("scene_b4")).addTo(this).setPosition(400, 300);
			Sprite.createWithSpriteFrame(atlas.findRegion("scene_b5")).addTo(this).setPosition(500, 300);
			Sprite.createWithSpriteFrame(atlas.findRegion("scene_b6")).addTo(this).setPosition(600, 300);
		}
	}
	
	//TODO 使用SptieFrameCache创建纹理
	static class SpriteFrameCacheTest extends TestDemo {
		public void onEnter() {
			super.onEnter();
			
			//读取atlas到spriteFrameCache
			CC.LoadAtlas("pack.atlas"); //("pack.atlas");
			
			Sprite.createWithSpriteFrameName("scene_b1").addTo(this).setPosition(100, 300);
			Sprite.createWithSpriteFrameName("scene_b2").addTo(this).setPosition(200, 300);
			Sprite.createWithSpriteFrameName("scene_b3").addTo(this).setPosition(300, 300);
			
			//释放atlas资源
			CC.UnloadAtlas("pack.atlas");
			
			Sprite.createWithSpriteFrameName("scene_b4").addTo(this).setPosition(400, 300);
			Sprite.createWithSpriteFrameName("scene_b5").addTo(this).setPosition(500, 300);
			Sprite.createWithSpriteFrameName("scene_b6").addTo(this).setPosition(600, 300);
		}
	}
}
