package com.cocos2dj.macros;

import com.cocos2dj.base.Director;
import com.cocos2dj.renderer.Texture;

public final class CC {

	//image
	public static Texture LoadImage(String fileName) {
		Texture t = Director.getInstance().getTextureCache().addImage(fileName);
		return t;
	}
}
