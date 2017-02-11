package tests;

import com.badlogic.gdx.Files.FileType;
import com.cocos2dj.base.Director;
import com.cocos2dj.basic.Engine;
import com.cocos2dj.platform.AppDelegate;
import com.cocos2dj.platform.FileUtils;
import com.cocos2dj.platform.ResolutionPolicy;

/**
 * 
 * @author xujun
 */
public class TestAppDelegate_Tests implements AppDelegate {

	TestController _testController;
	
	@Override
	public void initConfiguration() {
		Engine.setSingleThreadMode();
//		Engine.setDoubleThreadMode();
	}

	@Override
	public boolean applicationDidFinishLaunching() {
		FileUtils.getInstance().addSearchPath("/Users/xujun/remoteProject/Cocos2dJavaImages", FileType.Absolute, true);
		FileUtils.getInstance().addSearchPath("Resource", FileType.Internal, true);
		
		Director.getInstance().getOpenGLView().setDesignResolutionSize(1136, 640, ResolutionPolicy.EXACT_FIT);
		
		_testController = TestController.getInstance();
		_testController.start();
		return true;
	}

	@Override
	public void applicationDidEnterBackground() {
		if(_testController != null) {
			_testController.onEnterBackground();
		}
		Director.getInstance().stopAnimation();
	}

	@Override
	public void applicationWillEnterForeground() {
		if(_testController != null) {
			_testController.onEnterForeground();
		}
		Director.getInstance().startAnimation();
	}

}
