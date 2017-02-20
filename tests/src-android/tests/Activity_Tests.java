package tests;

import android.os.Bundle;

import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.cocos2dj.platform.android.ApplicationStartup;

/**
 * android 版本测试启动
 */
public class Activity_Tests extends AndroidApplication {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApplicationStartup.start(new TestAppDelegate_Tests(), this);
    }

    protected void onDestroy() {
        super.onDestroy();
//        System.exit(0);
    }
}
