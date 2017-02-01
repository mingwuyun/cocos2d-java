package com.cocos2dj.s2d;

import com.cocos2dj.macros.CCLog;
import com.cocos2dj.protocol.IAction;
import com.cocos2dj.protocol.INode;

/**
 * Action.java
 * <br>
 * <p>
 * 
 * @author Copyright (c) 2017 xu jun
 */
public class Action implements IAction {

	/**
	 * 创造动作的逆序动作
	 * @return
	 */
	public Action reverse() {
		return null;
	}
	
	/**
	 * 复制动作
	 * @return
	 */
	public Action cloneAction() {
		return null;
	}

	//getset>>
	/** Return certain target.
    * @return A certain target.
    */
	public Node getTarget() {return _target;}
	/** The action will modify the target properties. 
    *
    * @param target A certain target.
    */
	public void setTarget(Node node) {_target = node;}
	/** Return a original Target. 
    *
    * @return A original Target.
    */
	public final void setOriginalTarget(Node originalTarget) {_originalTarget = originalTarget;}
	public final INode getOriginalTarget() {return _originalTarget;}

	public final void setTag(int tag) {this._tag = tag;}
	/** Returns a tag that is used to identify the action easily. 
    *
    * @return A tag.
    */
	public final int getTag() {return _tag;}
	public final void setFlags(int flags) {_flags = flags;}
	public final int getFlags() {return _flags;}
	//getset<<
	
	@Override
	public void startWithTarget(INode node) {
		_originalTarget = _target = (Node) node;
	}
	
	/** 
     * Called once per frame. time a value between 0 and 1.

     * For example:
     * - 0 Means that the action just started.
     * - 0.5 Means that the action is in the middle.
     * - 1 Means that the action is over.
     *
     * @param time A value between 0 and 1.
     */
	public void update(float time) {
		CCLog.error("Action update", "override me");
	}
	
	/** Called every frame with it's delta time, dt in seconds. DON'T override unless you know what you are doing. 
    *
    * @param dt In seconds.
    */
	public void step(float dt) {
		CCLog.error("Action step", "override me");
	}

	@Override
	public void stop() {
		_target = null;
	}

	@Override
	public boolean isDone() {
		return true;
	}

	
	//fields>>
	protected INode		_originalTarget;
	protected Node		_target;
	protected int 		_tag;
	protected int 		_flags;
	//fields<<
	
	
	
	public static class FiniteTimeAction extends Action {
		
		public final float getDuration() {return _duration;}
		public final void setDuration(float duration) {_duration = duration;}
		
//		public final FiniteTimeAction reverse() {
//			return null;
//		}
		
		protected float _duration;
	}
	
	public static class Speed extends Action {
		
		
	}
	
	//TODO unfinish
	public static class Follow extends Action {
		
	}
}
