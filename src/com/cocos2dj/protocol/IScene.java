package com.cocos2dj.protocol;

import com.badlogic.gdx.math.Matrix4;
import com.cocos2dj.renderer.Renderer;

public interface IScene extends INode {
	
	public int getCamerasCount();
	
	public ICamera getCamera(int index);
	
	public void render(Renderer renderer, Matrix4 eyeTransform, Matrix4 eyeProjection);
}
