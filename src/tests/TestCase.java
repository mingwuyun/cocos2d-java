package tests;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.cocos2dj.base.Director;
import com.cocos2dj.module.gdxui.GdxUIDebugInfo;
import com.cocos2dj.module.gdxui.GdxUIManager;
import com.cocos2dj.module.gdxui.GdxUISkin;
import com.cocos2dj.module.gdxui.GdxUIStage;
import com.cocos2dj.module.gdxui.ModuleGdxUI;
import com.cocos2dj.s2d.Scene;

public class TestCase extends Scene {
    /** TestCase test type.*/
    public static enum Type {
        /** For testing whether test case not crash.*/
        ROBUSTNESS,
        /**
         * For check the correctness of regular test cases. 
         * A test case passes only if the actual output equal to the expected output.
         */
        UNIT,
        /** @warning The test type is not achieved.*/
        GRAPHICAL_STATIC,
        /** @note It's mean the test case need test manually.*/
        MANUAL
    };
    
    public TestCase() {
    	Director.getInstance().getTextureCache().removeUnusedTextures();
//    	schedule((t)->{
//    		return false;
//    	}, "AccumulatedTimeUse");
    }

    public String title()  { return ""; }
    public String subtitle()  { return ""; }

    /** Returns the test type, the default type is Type::ROBUSTNESS.*/
    public Type getTestType()  {
    	return Type.ROBUSTNESS;
    }
    /** Returns the time the test case needs.*/
    public float getDuration()  {
    	return 0.2f;
    }


    /** Returns the expected output.*/
    public String getExpectedOutput() { return ""; }
    /** Returns the actual output.*/
    public String getActualOutput()  { return ""; }

    /** Callback functions.*/
    public  void restartTestCallback(Object sender) {
    	if(_testSuite != null) {
    		_testSuite.restartCurrTest();
    	}
    }
    public void nextTestCallback(Object sender) {
    	if(_testSuite != null) {
    		_testSuite.enterNextTest();
    	}
    }
    public  void priorTestCallback(Object sender) {
    	if(_testSuite != null) {
    		_testSuite.enterPreviousTest();
    	}
    }
    public void onBackCallback(Object sender) {
    	if(_testSuite != null) {
    		_testSuite.backsUpOneLevel();
    	}
    }

    /**
     * You should NEVER call this method, unless you know what you are doing.
     */
    public void setTestSuite(TestSuite testSuite) {
    	_testSuite = testSuite;
    }
    public TestSuite getTestSuite()  { return _testSuite; }

    /** Returns the run time of test case.*/
    float getRunTime()  { return _runTime; }

    /**
     * You should NEVER call this method, unless you know what you are doing.
     */
    public void setTestCaseName(String name) { _testCaseName = name; }
    public String getTestCaseName()  { return _testCaseName; }

    
    public void onEnter() {
    	super.onEnter();
    	_gdxui = createModule(ModuleGdxUI.class);
    	GdxUIDebugInfo debugInfo = new GdxUIDebugInfo();
    	debugInfo.addDebugListener(GdxUIDebugInfo.DebugFPS);
    	debugInfo.addDebugListener(GdxUIDebugInfo.DebugHeap);
    	_gdxui.addUIStage(debugInfo, true);
    	
    	Skin skin = GdxUISkin.instance().getDeafult();
    	Label _titleLabel = new Label(_testCaseName, skin);
    	float W = ModuleGdxUI.getUIConfig().uiDefaultWidth;
    	float H = ModuleGdxUI.getUIConfig().uiDefaultHeight;
    	_titleLabel.setFontScale(2f);
    	_titleLabel.setPosition(W/2f, H - 60);
    	_gdxui.addUIDefault(_titleLabel);
    	
    	TextButton _nextButton = new TextButton("Next", skin);
    	_nextButton.setSize(80, 30);
    	_gdxui.addUIDefault(_nextButton);
    	TextButton _prevButton = new TextButton("Prev", skin);
    	_prevButton.setSize(80, 30);
    	_gdxui.addUIDefault(_prevButton);
    	TextButton _restartButton = new TextButton("Restart", skin);
    	_restartButton.setSize(80, 30);
    	_gdxui.addUIDefault(_restartButton);
    	
    	TextButton _backButton = new TextButton("Back", skin);
    	_backButton.setSize(80, 30);
    	_gdxui.addUIDefault(_backButton);
    	
    	
    	
    	_nextButton.setPosition(W - 100, H * 0.9f);
    	_prevButton.setPosition(W - 100, H * 0.9f - 50);
    	_restartButton.setPosition(W - 100, H * 0.9f - 100);
    	_backButton.setPosition(W - 100, H * 0.9f - 150);
    	
    	_backButton.addListener(new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onBackCallback(this);
			}
        });
    	
    	_nextButton.addListener(new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				nextTestCallback(this);
			}
        });
    	
    	_prevButton.addListener(new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				priorTestCallback(this);
			}
        });
    	
    	_restartButton.addListener(new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				restartTestCallback(this);
			}
        });
    }
    
    public void init() {
    	super.init();
    	
    }

    protected ModuleGdxUI _gdxui;
    
    private TestSuite _testSuite;
    private float _runTime;
    String _testCaseName;	
}
