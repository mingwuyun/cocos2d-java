package tests.nodetest;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.cocos2dj.base.ActionManager;
import com.cocos2dj.base.Director;
import com.cocos2dj.base.EventDispatcher;
import com.cocos2dj.base.Rect;
import com.cocos2dj.basic.BaseInput;
import com.cocos2dj.basic.BaseTask;
import com.cocos2dj.basic.BaseUpdater;
import com.cocos2dj.module.gdxui.GdxUIConfig;
import com.cocos2dj.module.gdxui.GdxUIConsole;
import com.cocos2dj.module.gdxui.GdxUIStage;
import com.cocos2dj.module.gdxui.ModuleGdxUI;
import com.cocos2dj.platform.AppDelegate;
import com.cocos2dj.platform.FileUtils;
import com.cocos2dj.platform.ResolutionPolicy;
import com.cocos2dj.protocol.IAction;
import com.cocos2dj.protocol.INode;
import com.cocos2dj.renderer.Texture;
import com.cocos2dj.s2d.Node;
import com.cocos2dj.s2d.Scene;
import com.cocos2dj.s2d.Sprite;

public class TestNodeAppDelegate implements AppDelegate {

	@Override
	public void initConfiguration() {
		
	}

	@Override
	public boolean applicationDidFinishLaunching() {
		FileUtils.getInstance().addSearchPath("/Users/xujun/remoteProject/Cocos2dJavaImages", FileType.Absolute, true);
		FileUtils.getInstance().addSearchPath("Resource", FileType.Internal, true);
		
		Director.getInstance().getOpenGLView().setDesignResolutionSize(1136, 640, ResolutionPolicy.EXACT_FIT);

//		Director.getInstance().getOpenGLView().setDesignResolutionSize(1136, 640, ResolutionPolicy.NO_BORDER);
//
//		Director.getInstance().getOpenGLView().setDesignResolutionSize(1136, 640, ResolutionPolicy.SHOW_ALL);
//
//		Director.getInstance().getOpenGLView().setDesignResolutionSize(1136, 640, ResolutionPolicy.FIXED_WIDTH);
//
//		Director.getInstance().getOpenGLView().setDesignResolutionSize(1136, 640, ResolutionPolicy.FIXED_HEIGHT);
		
		Director.getInstance().runWithScene(NodeTest.create());
		
		return false;
	}

	@Override
	public void applicationDidEnterBackground() {
		
	}

	@Override
	public void applicationWillEnterForeground() {
		
	}

}


class NodeTest implements INode.NodeCallback {
	
	public static Scene create() {
		Scene scene = new Scene();
		scene.setNodeCallback(new NodeTest());
		return scene;
	}
	
	@Override
	public void onEnter(INode n) {
//		scene.addMudule(Gdxui)
//		Director.instance().getScheduler().renderSchedulePerFrame(scheduleFunc, priority, paused);
		
		Scene scene = (Scene) n;
		Node node = Node.create();
		Node node2 = Node.create();
		Sprite sp = Sprite.create();
			sp.initWithFile("powered.png", Rect.Get(0, 0, 200, 297));
			sp.setAnchorPoint(0.5f, 0.5f);
			
		Sprite spBG = Sprite.create();
		spBG.initWithFile("powered.png", Rect.Get(0, 0, 200, 297));
		spBG.setAnchorPoint(0.f, 0.f);
		spBG.setContentSize(800, 450);
		spBG.setScaleX(4);
		spBG.setScaleY(2);
		
//		spBG.setPosition(0, 0);
		
//		scene.addChild(spBG);
//		scene.addChild(node);		
		
		
		node.addChild(node2);
		node2.addChild(sp);
		
		
		node.setPosition(400, 200);
		
//		node.setScale(1.5f);
		node.setRotation(30);
		node2.setRotation(30);
		sp.setRotation(30);
		
		GdxUIConfig config = GdxUIConfig.instance();
		ModuleGdxUI gdxui = scene.createModule(ModuleGdxUI.class, config);	//创建ui模块
		//模块：gdxUI，base2d，typeFactory，spine，debugDraw，
		gdxui.addUIStage(new GdxUIConsole(), true);
		gdxui.addUIStage(new GdxUIStage() {
			@Override
			protected void onUpdateUI(int dt) {
				
			}
			
			@Override
			protected void onInitUI() {
				Texture t = Director.getInstance().getTextureCache().addImage("powered.png");
				Image image = new Image(new TextureRegionDrawable(t.createTextureRegion()));
				image.setSize(200, 200);
				addUI(image);
			}
			
			@Override
			protected void onDestroyUI() {
				
			}
			
			@Override
			protected void onChanged(Event e, Actor actor) {
				
			}
		}, true);
		
		
		
		
		
//		gdxui.add
		
//		gdxui.addUIManager()
		
//		Texture t = Director.getInstance().getTextureCache().addImage("powered.png");
//		Image image = new Image(new TextureRegionDrawable(t.createTextureRegion()));
////		image.setDrawable(new TextureRegionDrawable(t.createTextureRegion()));
//		image.setSize(200, 200);
////		image.setPosition(400, 200);
//		gdxui.getStage().addActor(image);
//		image.setDebug(true);

//		getUICamera().zoom = updateUICamera();
		
//		OrthographicCamera camera =  (OrthographicCamera) gdxui.getStage().getCamera();
//		camera.viewportWidth = 800;
//		camera.viewportHeight = 450;
//		camera.zoom = 1;
		
//		camera.update(true);
		
//		sp.setScale(1.5f);
		
		
		EventDispatcher _dispatcher =  Director.getInstance().getEventDispatcher();
		
		_dispatcher.addCustomEventListener(Director.EVENT_BEFORE_UPDATE, (e)->{
//			System.out.println("beforeUpdate >>>>>");
		});
		
		ActionManager am = Director.getInstance().getActionManager();
		am.addAction(new IAction() {
			INode node;
			@Override
			public void startWithTarget(INode node) {
				this.node = node;
			}

			@Override
			public INode getOriginalTarget() {
				return node;
			}

			@Override
			public int getTag() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public int getFlag() {
				// TODO Auto-generated method stub
				return 0;
			}

			int i = 100;
			@Override
			public void step(float dt) {
				
				// TODO Auto-generated method stub
//				System.out.println("step >>>");
			}

			@Override
			public void stop() {
				// TODO Auto-generated method stub
				_dispatcher.removeCustomEventListeners(Director.EVENT_BEFORE_UPDATE);
				node2.removeFromParent();
				
//				System.out.println("node" + node.getChildrenCount());
			}

			@Override
			public boolean isDone() {
if(i-- < 0) {

				return true;	
				}
				// TODO Auto-generated method stub
				return false;
			}
			
		}, node, false);
//		new BaseUpdater(){
//			float x = 0;
//			float y = 0;
//			@Override
//			protected boolean onUpdate(float dt) {
////				scene.getDefaultCamera().setPosition(x += 1, y += 1);
//				node2.setPosition(node2.getPositionX() + 1, node2.getPositionY() + 1);
//				return false;
//			}
//
//			@Override
//			protected void onEnd() {
//			}
//			
//		}.attachSchedule();
	}

	@Override
	public void onExit(INode node) {
		
	}

	@Override
	public void onUpdate(INode node, float dt) {
		
	}
	
}
