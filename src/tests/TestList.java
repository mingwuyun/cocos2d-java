package tests;

import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.graphics.g2d.freetype.FreeType.Size;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.cocos2dj.base.Director;
import com.cocos2dj.base.Size;
import com.cocos2dj.module.gdxui.GdxUIDebugInfo;
import com.cocos2dj.module.gdxui.GdxUISkin;
import com.cocos2dj.module.gdxui.ModuleGdxUI;
import com.cocos2dj.protocol.IFunctionZeroArg;
import com.cocos2dj.protocol.INode;
import com.cocos2dj.protocol.INode.OnEnterCallback;
import com.cocos2dj.s2d.Scene;

public class TestList extends TestBase {

	/**在runThisTest之前调用这个方法，否则无效 */
	public void addTest(String name, IFunctionZeroArg<TestBase> callback) {
		_childTestNames.add(name);
		_testCallbacks.add(callback);
	}
	
	//更新测试场景
	public void touchTableCell(int index) {
		TestBase test = _testCallbacks.get(index).callback();
		if(test.getChildTestCount() > 0) {		
			// 必须是suite才能直接运行
			_shouldRestoreTableOffset = true;
			_cellTouchEnabled = false;
			test._setTestParent(this);
			test.runThisTest();
		} else {
			test = null;
		}
	}
	
	@Override
	public void runThisTest() {
		System.gc();	//gc test
		
		_cellTouchEnabled = true;
		Director director = Director.getInstance();
	    Scene scene = Scene.createScene();
	    _gdxui = scene.createModule(ModuleGdxUI.class);
	    
	    scene.setOnEnterCallback(new OnEnterCallback() {
			@Override
			public void onEnter(INode n) {
				//debug
//				System.out.println("fuck onEnter");
			    GdxUIDebugInfo debugInfo = new GdxUIDebugInfo();
		    	debugInfo.addDebugListener(GdxUIDebugInfo.DebugFPS);
		    	debugInfo.addDebugListener(GdxUIDebugInfo.DebugHeap);
		    	_gdxui.addUIStage(debugInfo, true);
			};
	    });
	    Size visibleSize = director.getVisibleSize();
//	    Vector2 origin = director.getVisibleOrigin();
	    
	    final float W = visibleSize.width;
	    final float H = visibleSize.height;
	    
	    Skin skin = GdxUISkin.instance().getDeafult();
	    
	    //create table
	    Table _table = new Table();
	    for(int i = 0; i < _childTestNames.size; ++i) {
	    	Label label = new Label(_childTestNames.get(i), skin);
	    	label.setFontScale(2f);
	    	_table.add(label).center().row();
	    	label.setTouchable(Touchable.enabled);
	    	final int index = i;
	    	label.addListener(new EventListener() {
				@Override
				public boolean handle(Event event) {
					if(event instanceof InputEvent) {
						InputEvent e = (InputEvent) event;
						switch(e.getType()) {
						case touchDown:
							if(_cellTouchEnabled) {
								_cellTouchEnabled = false;
								touchTableCell(index);
							}
							break;
						default:
						}
					}
					return false;
				}
			});
	    }
	    
	    ScrollPane _scroll = new ScrollPane(_table);
	    _gdxui.addUIDefault(_scroll);
	    _scroll.setSize(W, H);
	    
//
	    if (_parentTest != null) {	//返回上层
	        //Add back button.
	    	TextButton button = new TextButton("Back", skin);
	    	button.setPosition(W - 100, H - 50);
	    	button.setSize(100, 50);
	    	_gdxui.addUIDefault(button);
	        button.addListener(new ChangeListener(){
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					backsUpOneLevel();
				}
	        });
	    } else {	//退出
	    	TextButton button = new TextButton("Exit", skin);
	    	button.setPosition(W - 100, H - 50);
	    	button.setSize(100, 50);
	    	_gdxui.addUIDefault(button);
	        button.addListener(new ChangeListener(){
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					Gdx.app.exit();
				}
	        });
	    	
//	    	Button button = new Button(skin);
//	        Label label = new Label("Exit", skin);
//	        label.setTouchable(Touchable.disabled);
//	        label.setFontScale(2f);
//	        button.setSize(100, 40);
	        
//	        Group g = new Group();
//	        g.addActor(button);
//	        button.setPosition(-50, 0);
//	        g.addActor(label);
//	        label.setPosition(-label.getPrefWidth()/2f, 5);
//	        
//	        g.setPosition(W-50, H-50);
//	        _gdxui.addUIDefault(g);
	        
//	        g.addListener(new ChangeListener(){
//				@Override
//				public void changed(ChangeEvent event, Actor actor) {
//					Gdx.app.exit();
//				}
//	        });
	    }

	    director.replaceScene(scene);
	}

	ModuleGdxUI _gdxui;
	Array<IFunctionZeroArg<TestBase>> _testCallbacks = new Array<>();
    boolean _cellTouchEnabled;
    boolean _shouldRestoreTableOffset;
    Vector2 _tableOffset = new Vector2();
}
