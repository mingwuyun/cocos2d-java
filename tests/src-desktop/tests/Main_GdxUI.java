package tests;

import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.cocos2dj.platform.desktop.ApplicationStartup;

/**
 * GdxUI 组件测试
 * 
 * @author xujun
 */
public class Main_GdxUI {

	public static void main(String[] args) {
		LwjglApplicationConfiguration conf = ApplicationStartup.getConfiguration();
		conf.width = 	800;  	conf.height = 	480;
		ApplicationStartup.start(new TestAppDelegate_GdxUI());
	}
}
