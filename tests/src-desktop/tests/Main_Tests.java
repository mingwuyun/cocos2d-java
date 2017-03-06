package tests;

import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.cocos2dj.platform.desktop.ApplicationStartup;

/**
 * 测试
 * 
 * @author xujun
 */
public class Main_Tests {
	
	public static void main(String[] args) {
		LwjglApplicationConfiguration conf = ApplicationStartup.getConfiguration();
//		conf.width = 	600;  	conf.height = 	338;
		conf.width = 480; conf.height = 270;
		conf.x = 50;
//		conf.width = 	800;  	conf.height = 	450;
		// cancel fps limit
//		conf.vSyncEnabled = false;conf.backgroundFPS = 0;conf.foregroundFPS = 0;
		ApplicationStartup.start(new TestAppDelegate_Tests());
	}
	
}
