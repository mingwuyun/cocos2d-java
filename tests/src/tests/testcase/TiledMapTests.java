package tests.testcase;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.cocos2dj.basic.BaseUpdater;
import com.cocos2dj.macros.CC;
import com.cocos2dj.s2d.ActionInterval.MoveBy;
import com.cocos2dj.s2d.ActionInterval.RotateBy;
import com.cocos2dj.s2d.ActionInterval.ScaleTo;
import com.cocos2dj.s2d.Sprite;
import com.cocos2dj.s2d.TMXTiledMap;

import tests.TestCase;
import tests.TestSuite;

public class TiledMapTests extends TestSuite {
	
	public TiledMapTests() {
		addTestCase("TiledMapTest1", ()->{return new TiledMapTest1();});
		addTestCase("GdxTiledMapTest", ()->{return new GdxTiledMapTest();});
	}
	
	static class TestDemo extends TestCase {
		
	}
	
	
	static class TiledMapTest1 extends TestDemo {
		public void onEnter() {
			super.onEnter();
			
			Sprite spriteColor1 = (Sprite) Sprite.create("powered.png").addTo(this);
			spriteColor1.setPosition(100, 300);
			
			TMXTiledMap map = new TMXTiledMap();
			map.initWithTMXFile("Resource/tiles.tmx");
			addChild(map);
			
			map.runAction(ScaleTo.create(2, 2));
			map.runAction(MoveBy.create(2, 200, 0));
			
			Sprite spriteColor2 = (Sprite) Sprite.create("powered.png").addTo(this);
			spriteColor2.setPosition(900, 300);
		}
	}
	
	////////////////////////////////////////
	//直接使用gdx提供的tiledMap
	static class GdxTiledMapTest extends TestDemo {
		private TiledMap map;
		private TiledMapRenderer renderer;
		private OrthographicCamera camera;
		
		private BaseUpdater func;
		
		public void onEnter() {
			super.onEnter();
			
			float w = Gdx.graphics.getWidth();
			float h = Gdx.graphics.getHeight();

			camera = new OrthographicCamera();
			camera.setToOrtho(false, (w / h) * 640, 640);
			camera.update();
			
			map = new TmxMapLoader().load("Resource/tiles.tmx");
			renderer = new OrthogonalTiledMapRenderer(map, 2f);//1f / 32f);
			
			func = CC.Scheduler().renderAfterSchedulePerFrame((t)->{
				camera.position.set(500, 320, 0);
				camera.update();
				renderer.setView(
						camera.combined,
						0, 0, 1000, 500);
				renderer.render();
				return false;
			}, 0, false);
		}
		
		public void onExit() {
			super.onExit();
			map.dispose();
			func.removeSelf();
		}
	}
	
}

