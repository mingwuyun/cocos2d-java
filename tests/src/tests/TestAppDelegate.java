package tests;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.files.FileHandle;
import com.cocos2dj.base.Director;
import com.cocos2dj.platform.AppDelegate;
import com.cocos2dj.platform.FileUtils;
import com.cocos2dj.protocol.INode;
import com.cocos2dj.s2d.Node;
import com.cocos2dj.s2d.Scene;

public class TestAppDelegate implements AppDelegate {

	@Override
	public void initConfiguration() {
		
	}

	@Override
	public boolean applicationDidFinishLaunching() {
		Director director = Director.getInstance();
	    // turn on display FPS
	    director.setDisplayStats(true);
	    // set FPS. the default value is 1.0/60 if you don't call this
	    director.setAnimationInterval(1.0f / 60);
	    // create a scene. it's an autorelease object
	    Scene scene = new HelloWorldScene();
//	    // run
//	    System.out.println("start up");
	    director.runWithScene(scene);
	    
//	    FileUtils.getInstance().setDefaultResourceRootPath("", FileType.Classpath);
	    FileUtils.getInstance().addSearchPath("/Users/xujun/remoteProject/Cocos2dJavaImages", FileType.Absolute, false);
	    
	    FileHandle fh = FileUtils.getInstance().getFileHandle("assetMgrBackground2.png");
	    System.out.println("fh = " + fh.exists());
	    
	    System.out.println("fh direct = " + FileUtils.instance().isFileExist("assetMgrBackground2.png"));
	    
//	    new Base();
//	    new Base1();
//	    new Base1("hahahahaha");
//	    new Base1("ddddddddd");
		return true;
	}
	
	static class Base {
		public Base() {
//			System.out.println("base init !");
		}
	}
	
	static class Base1 extends Base{
		public Base1() {
			
		}
		
		public Base1(String d) {
			System.out.println("base 1 init !" + d);
		}
	}
	
	
	@Override
	public void applicationDidEnterBackground() {
		
	}

	@Override
	public void applicationWillEnterForeground() {
//		PolygonSpriteBatch db;
//		db.draw(region, x, y, originX, originY, width, height, scaleX, scaleY, rotation);
	}
	
	
	public static class HelloWorld2Scene extends Scene {
		@Override
		public void onEnter() {
			super.onEnter();
			
//			Director.getInstance().getScheduler().schedule((t)->{
//				return false;
//			}, this, 0f, 1, 1f, false);
			
			System.out.println("onEnter HelloWorld2 Success");
		}
		
		public void onExit() {
			super.onExit();
			
			System.out.println("onExit HelloWorld2 Success");
		}
	
	}
	
	public static class HelloWorldScene extends Scene implements INode.NodeCallback {
		
		public void init() {
			this.setNodeCallback(this);
		}

		@Override
		public void onEnter(INode _node) {
			System.out.println("onEnter HelloWorld Success");
			Node node = new Node();
			this.addChild(node);
			 
			
			
			
//			sprite.setContentSize(contentSize);
			
			
//			Sprite sprite = Sprite.create("", rect);
//			node.addChild(sprite);
			
//			node.scheduleUpdate();
//			node.setOnUpdateCallback((n, t)->{
//				System.out.println("onUpdate >>>>>>>>>>>" + t);
//			});
//			scheduleUpdate();
//			Director.getInstance().getScheduler().schedule((t)->{
//				_director.replaceScene(new HelloWorld2Scene());
//				return false;
//			}, this, 0f, 1, 1f, false);
		}

		@Override
		public void onExit(INode _node) {
			System.out.println("onExit HelloWorld Success");
		}

		@Override
		public void onUpdate(INode node, float dt) {
//			System.out.println("___________node");
		}
	
	}

}
