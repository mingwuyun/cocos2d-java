package com.cocos2dj.protocol;

import com.badlogic.gdx.math.Matrix4;
import com.cocos2dj.renderer.Renderer;

/**
 * INode.java
 * <p>
 * 
 * Node相关协议
 * 
 * @author Copyright (c) 2017 xu jun
 *
 */
public interface INode {
	 /**
     * Override this method to draw your own node.
     * The following GL states will be enabled by default:
     * - `glEnableClientState(GL_VERTEX_ARRAY);`
     * - `glEnableClientState(GL_COLOR_ARRAY);`
     * - `glEnableClientState(GL_TEXTURE_COORD_ARRAY);`
     * - `glEnable(GL_TEXTURE_2D);`
     * AND YOU SHOULD NOT DISABLE THEM AFTER DRAWING YOUR NODE
     * But if you enable any other GL state, you should disable it after drawing your node.
     */
    public void draw(Renderer renderer, Matrix4 transform, int flags);
//    public void draw();

    /**
     * Visits this node's children and draw them recursively.
     */
    public void visit(Renderer renderer,  Matrix4 transform, int flags);
//    virtual void visit() final;
    
    
    public boolean isRunning();
	
	public void onEnter();
	
	public void onExit();
	
	public void cleanup();
	
	public void onExitTransitionDidStart();
	
	public void onEnterTransitionDidFinish();
    
    //children
	public int getChildrenCount();
	
	public INode getChild(int index);
	
	public void sortAllChildren();
	
	// Zorder
	public int getLocalZOrder();
	
	public float getGlobalZOrder();
	
	//camera
	public int getCameraMask();
	
	
	////////////////////////////////////////////
	// callback
	public static interface OnEnterCallback {public void onEnter(INode n);}
	public static interface OnExitCallback {public void onExit(INode n);}
	public static interface OnExitTransitionDidStartCallback {public void onExitTransitionDidStart(INode n);}
	public static interface OnEnterTransitionDidFinishCallback {public void onEnterTransitionDidFinish(INode n);}
	
	public static interface OnUpdateCallback {public void onUpdate(INode n, float dt);}
	
	public static interface NodeCallback extends OnEnterCallback, OnExitCallback, OnUpdateCallback {}
	public static interface NodeWithTransitionCallback extends OnEnterCallback, OnExitCallback, OnUpdateCallback, 
								OnEnterTransitionDidFinishCallback, OnExitTransitionDidStartCallback {}
}
