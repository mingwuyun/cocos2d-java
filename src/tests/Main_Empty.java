package tests;

import com.cocos2dj.platform.AppDelegate;
import com.cocos2dj.platform.desktop.ApplicationStartup;


public class Main_Empty {

	public static void main(String[] args) {
//		LwjglApplicationConfiguration conf = ApplicationStartup.getConfiguration();
//		conf.width = 	800;  	conf.height = 	450;
		ApplicationStartup.start(new AppDelegate(){

			@Override
			public void initConfiguration() {
				
			}

			@Override
			public boolean applicationDidFinishLaunching() {
				return false;
			}

			@Override
			public void applicationDidEnterBackground() {
				
			}

			@Override
			public void applicationWillEnterForeground() {
				
			}
			
		});
	}
}
