package com.cocos2dj.macros;

import com.badlogic.gdx.files.FileHandle;
import com.cocos2dj.base.Director;
import com.cocos2dj.module.Module;
import com.cocos2dj.platform.FileUtils;
import com.cocos2dj.renderer.Texture;
import com.cocos2dj.s2d.Scene;

public final class CC {

	@SuppressWarnings("unchecked")
	public static<T extends Module> T GetRunningSceneModule(Class<T> clazz) {
		return (T) GetRunningScene().getModule(clazz);
	}
	
	public static Scene GetRunningScene() {
		return (Scene) Director.getInstance().getRunningScene();
	}
	
	//image
	public static Texture LoadImage(String fileName) {
		Texture t = Director.getInstance().getTextureCache().addImage(fileName);
		return t;
	}
	
	//file
	public static FileHandle File(String fileName) {
		return FileUtils.getInstance().getFileHandle(fileName);
	}
}
