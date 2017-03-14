package com.cocos2dj.macros;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.cocos2dj.base.Director;
import com.cocos2dj.module.Module;
import com.cocos2dj.platform.FileUtils;
import com.cocos2dj.renderer.Texture;
import com.cocos2dj.s2d.Scene;
import com.cocos2dj.s2d.SpriteFrameCache;

public final class CC {

	@SuppressWarnings("unchecked")
	public static<T extends Module> T GetRunningSceneModule(Class<T> clazz) {
		return (T) GetRunningScene().getModule(clazz);
	}
	
	public static Scene GetRunningScene() {
		return (Scene) Director.getInstance().getRunningScene();
	}
	
	public static com.cocos2dj.base.Scheduler Scheduler() {
		return Director.getInstance().getScheduler();
	}
	
	public static Director Director() {
		return Director.getInstance();
	}
	
	//image
	public static Texture LoadImage(String fileName) {
		Texture t = Director.getInstance().getTextureCache().addImage(fileName);
		return t;
	}
	
	/**创建并返回TextureAtlas对象 不会添加到
	 * SpriteFrameCache */
	public static TextureAtlas TextureAtlas(String packName) {
		return new TextureAtlas(CC.File(packName));
	}
	
	/**创建并返回TextureAtlas对象 不会添加到
	 * SpriteFrameCache */
	public static TextureAtlas TextureAtlas(String packName, String imgPath) {
		return new TextureAtlas(CC.File(packName), CC.File(imgPath));
	}
	
	/**
	 * 装载atlas对象 会添加到SpriteFrameCache中
	 * @param packName
	 */
	public static void LoadAtlas(String packName) {
		if(SpriteFrameCache.instance().findTextureAtlas(packName) != null) {
			return;
		}
		TextureAtlas ta = new TextureAtlas(CC.File(packName));
		SpriteFrameCache.instance().addSpriteFrameWithTextureAtlas(packName, ta);
	}
	
	public static void LoadAtlas(String packName, String imgPath) {
		if(SpriteFrameCache.instance().findTextureAtlas(packName) != null) {
			return;
		}
		TextureAtlas ta = new TextureAtlas(CC.File(packName), CC.File(imgPath));
		SpriteFrameCache.instance().addSpriteFrameWithTextureAtlas(packName, ta);
	}
	
	public static void UnloadAtlas(String packName) {
		SpriteFrameCache.instance().removeSpriteFramesFromTextureAtlas(packName);
	}
	
	
	//file
	public static FileHandle File(String fileName) {
		return FileUtils.getInstance().getFileHandle(fileName);
	}
}
