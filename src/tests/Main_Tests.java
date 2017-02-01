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
//		conf.width = 	800;  	conf.height = 	450; 
		conf.width = 	600;  	conf.height = 	338; 
		ApplicationStartup.start(new TestAppDelegate_Tests());
	}
	
}
