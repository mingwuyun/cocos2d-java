package tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.cocos2dj.base.Director;
import com.cocos2dj.module.gdxui.GdxUIConsole.ConsoleHandle;
import com.cocos2dj.module.gdxui.GdxUIDebugInfo;
import com.cocos2dj.module.gdxui.GdxUIDebugInfo.DebugListener;
import com.cocos2dj.module.gdxui.GdxUISkin;
import com.cocos2dj.module.gdxui.GdxUIStage;
import com.cocos2dj.module.gdxui.ModuleGdxUI;
import com.cocos2dj.platform.AppDelegate;
import com.cocos2dj.platform.FileUtils;
import com.cocos2dj.platform.ResolutionPolicy;
import com.cocos2dj.protocol.INode;
import com.cocos2dj.s2d.Scene;


/**
 * GdxUI插件测试<p>
 * 
 * @author xujun
 */
public class TestAppDelegate_GdxUI implements AppDelegate {

	@Override
	public void initConfiguration() {
		
	}

	@Override
	public boolean applicationDidFinishLaunching() {
		FileUtils.getInstance().addSearchPath("/Users/xujun/remoteProject/Cocos2dJavaImages", FileType.Absolute, true);
		FileUtils.getInstance().addSearchPath("Resource", FileType.Internal, true);
		
		Director.getInstance().getOpenGLView().setDesignResolutionSize(1136, 640, ResolutionPolicy.EXACT_FIT);
		
		Director.getInstance().runWithScene(GdxuiScene.create());
		return true;
	}

	@Override
	public void applicationDidEnterBackground() {
		
	}

	@Override
	public void applicationWillEnterForeground() {
		
	}

	
	
}



////////////////////////
//GdxuiScene
class GdxuiScene implements INode.NodeCallback {
	
	Scene 			_scene;
	ModuleGdxUI		_gdxui;
	
	public static Scene create() {
		GdxuiScene instance = new GdxuiScene();
		instance._scene = new Scene();
		instance._scene.setNodeCallback(instance);
		return instance._scene;
	}
	
	
	@Override
	public void onEnter(INode n) {
		_gdxui = _scene.createModule(ModuleGdxUI.class);
		
		//创建控制台
		_gdxui.createConsole();	
		_gdxui.getConsole().addConsoleHandle(new ConsoleHandle("Hello"){
			//测试控制台
			@Override
			public String handle(String cmd) {
				switch(cmd) {
				case "hello":	
					return "how are you";	//收到hello返回how are you
				case "exit":
					Gdx.app.exit();	//输入exit退出
					return SUCCESS;
				case "add":	
					//命令行参数的使用：
					//输入：add:1,2 使用带参数命令
					float a1 = getArgFloat(0);		//获取参数1
					float a2 = getArgFloat(1);		//获取参数2
					float ret = a1 + a2;
					return a1 + " + " + a2 + " = " + ret;
				}
				return null;	//错误
			}
		});
		
		//创建debug输出
		GdxUIDebugInfo debugInfo = ModuleGdxUI.createDebugInfo();
		debugInfo.addDebugListener(GdxUIDebugInfo.DebugFPS);	//fps输出
		debugInfo.addDebugListener(GdxUIDebugInfo.DebugHeap);	//内存输出
		debugInfo.addDebugListener(new DebugListener() {
			@Override
			public String onOutput() {
				return "enter ~ call console";
			}
		});
		//java 8
//		debugInfo.addDebugListener(()->{
//				return "enter ~ call console";
//		});
		_gdxui.addUIStage(debugInfo, true);
		
		//创建uiStage
		_gdxui.addUIStage(new GdxUITableViewTest(), true); 
	}

	@Override
	public void onExit(INode n) {}
	@Override
	public void onUpdate(INode n, float dt) {}
}


/////////////////////////
//tableView测试
class GdxUITableViewTest extends GdxUIStage {
	
	ScrollPane 			_scrollPane;
	Table				_table;
	
	
	@Override
	protected void onInitUI() {
		_table = new Table();
		_scrollPane = new ScrollPane(_table);
		
//		Box2D.init();
//		World world = new World(new Vector2(0, -10), true);
//		BodyDef bd = new BodyDef();
//		bd.fixedRotation = true;
//		bd.type = BodyType.DynamicBody;
//		bd.position.set(0, 10);
//		
//		Body body = world.createBody(bd);
//		FixtureDef fd = new FixtureDef();
//		fd.shape = new Shape();
//		body.createFixture(fd);
//		world.dispose();
		
//		body.app
//		body.setLinearVelocity(1f, 0);
//		body.setActive(true);
//		
//		Director.getInstance().getScheduler().scheduleUpdate((dt)->{
//			world.step(16, 2, 2);
//			body.setLinearVelocity(1f, 0);
//			System.out.println("body position = " + body.getPosition());
//			return false;
//		}, 0, false);
		
		
		addUI(_scrollPane);
		
		{
			Label l = new Label("ActionManager", GdxUISkin.instance().getDeafult());
			l.setFontScale(1.5f);
			l.setTouchable(Touchable.enabled);
//			InputEventListener d;
//			InputListener in;
			l.addListener(new EventListener() {
				@Override
				public boolean handle(Event event) {
					InputEvent inputEvent = null;
					if(event instanceof InputEvent) {inputEvent = (InputEvent) event;}
					switch(inputEvent.getType()) {
					case touchDown:
						System.out.println("enter new Scene");
						break;
					case touchUp:
						break;
					default:
					}
					return false;
				}
			});
			_table.add(l).center().row();
		}
		{
			Label l = new Label("ActionManager", GdxUISkin.instance().getDeafult());
			l.setFontScale(1.5f);
			l.setTouchable(Touchable.enabled);
			l.addListener(new EventListener() {
				@Override
				public boolean handle(Event event) {
					InputEvent inputEvent = null;
					if(event instanceof InputEvent) {inputEvent = (InputEvent) event;}
					switch(inputEvent.getType()) {
					case touchDown:
						System.out.println("enter new Scene");
						break;
					case touchUp:
						break;
					default:
					}
					return false;
				}
			});
			_table.add(l).center().row();
		}
		{
			Label l = new Label("ActionManager", GdxUISkin.instance().getDeafult());
			l.setFontScale(1.5f);
			l.setTouchable(Touchable.enabled);
			l.addListener(new EventListener() {
				@Override
				public boolean handle(Event event) {
					InputEvent inputEvent = null;
					if(event instanceof InputEvent) {inputEvent = (InputEvent) event;}
					switch(inputEvent.getType()) {
					case touchDown:
						System.out.println("enter new Scene");
						break;
					case touchUp:
						break;
					default:
					}
					return false;
				}
			});
			_table.add(l).center().row();
		}
		
		_scrollPane.setSize(400, 100);
		_scrollPane.setPosition(getUICenterX(), getUICenterY(), Align.center);
		
		
		Label label = new Label("press '~' call console. and try: \n "
				+ "enter 'hello' and 'Enter' \n" 
				+ "enter 'add:1,2' and 'Enter' \n"
				+ "enter 'exit' close this application", GdxUISkin.instance().getDeafult());
		label.setAlignment(Align.topLeft);
		label.setPosition(getUICenterX() - 100, getUIHeight() - 100);
		addUI(label);
		label.setFontScale(2f);
	}

	@Override
	protected void onDestroyUI() {
		
	}

	@Override
	protected void onUpdateUI(int dt) {
	}

	@Override
	protected void onChanged(Event e, Actor actor) {
		
	}
	
}

