package com.cocos2dj.s2d;

import com.badlogic.gdx.math.Vector2;
import com.cocos2dj.macros.CCMacros;
import com.cocos2dj.protocol.INode;
import com.cocos2dj.s2d.Action.InfiniteTimeAction;
import com.cocos2dj.s2d.ActionInterval.MoveBy;

/**
 * ActionCondition.java
 * <p>
 *  条件结束动作类型 调用
 *  {@link #setDone(boolean)} 设置结束条件
 *  
 * @author Copyright(c) 2017 xujun
 */
public class ActionCondition extends InfiniteTimeAction {
	
	/** How many seconds had elapsed since the actions started to run.
    *
    * @return The seconds had elapsed since the actions started to run.
    */
    public float getElapsed() { return _elapsed; }
   
    public ActionCondition copy() {
	   return null;
    }
   
    public ActionCondition reverse() {
		return null;
	}
   
    /**
     * @param dt in seconds
     */
    public void step(float dt) {
	   if(_firstTick) {
		   _firstTick = false;
		   _elapsed = 0;
	   } else {
		   _elapsed += dt;
	   }
	   this.update(_elapsed);
	   
	   if(_endTime > 0) {
		   if(_elapsed > _endTime) {
			   setDone(true);
		   }
	   }
    }
    
    public void startWithTarget(INode target) {
	   super.startWithTarget(target);
	   _elapsed = 0.0f;
	   _firstTick = true;
    }
	 
    /** initializes the action 
     * @param timeUn*/
    public boolean initWithEndTime(float endTime) {
    	_endTime = endTime;
 	    _elapsed = 0;
 	    _firstTick = true;
 	    return true;
    }
    
	
    
    /**second 默认1s为单位 结束时间 -1不限制 */
    protected float		_endTime = -1;	
    protected float 	_elapsed;
    protected boolean	_firstTick;
    protected boolean 	CC_ENABLE_STACKABLE_ACTIONS = true;
    /**是否开启动作叠加模式 默认开启(true) */
    public void DEFINE_CC_ENABLE_STACKABLE_ACTIONS(boolean enableStackable) {
    	CC_ENABLE_STACKABLE_ACTIONS = enableStackable;
    }
    
    
    ///////////////////////////////////////////
    //TODO Move
    public static class Move extends ActionCondition {
    	
	    public static Move create(float endTime, Vector2 secTrans) {
	    	return create(endTime, secTrans.x, secTrans.y);
	    }
	    
	    public static Move create(float endTime, float secTransX, float secTransY) {
	    	Move ret = new Move();
	    	ret.initWithEndTime(endTime, secTransX, secTransY);
	    	return ret;
	    }
	    
//	    public MoveBy copy() {
//	    	return MoveBy.create(_duration, _positionDeltaX, _positionDeltaY);
//	    }
//	    public MoveBy reverse() {
//	    	return MoveBy.create(_duration, -_positionDeltaX, -_positionDeltaY);
//	    }
	    public void startWithTarget(INode target) {
	    	super.startWithTarget(target);
	    	_previousPositionX = _startPositionX = _target.getPositionX();
	    	_previousPositionY = _startPositionY = _target.getPositionY();
	    }
	    /**
	     * @param time in seconds
	     */
	    public void update(float t) {
//	    	System.out.println("t = " + t + _target.getPosition());
	    	if(_target != null) {
	    		System.out.println("t = " + t + _target.getPosition() + _secTransX);
if (CC_ENABLE_STACKABLE_ACTIONS) {
				float currX = _target.getPositionX();
				float currY = _target.getPositionY();
				float diffX = currX - _previousPositionX;
				float diffY = currY - _previousPositionY;
				
				_startPositionX = _startPositionX + diffX;
				_startPositionY = _startPositionY + diffY;
				
				float newPosX = _startPositionX + _secTransX * t;
				float newPosY = _startPositionY + _srcTransY * t;
				_target.setPosition(newPosX, newPosY);
				_previousPositionX = newPosX;
				_previousPositionY = newPosY;
} else {
	    		_target.setPosition(_startPositionX + _secTransX * t, 
	    				_startPositionY + _srcTransY * t);
} //CC_ENABLE_STACKABLE_ACTIONS
	    	}
	    }
	    
	    /** initializes the action */
//		public final boolean initWithDuration(float duration, Vector2 deltaPosition) {
//			return initWithDuration(duration, deltaPosition.x, deltaPosition.y);
//		}
		public boolean initWithEndTime(float endTime, float secTransX, float srcTransY) {
			if(super.initWithEndTime(endTime)) {
				_secTransX = secTransX;
				_srcTransY = srcTransY;
				return true;
			}
			return false;
		}
	
		protected float _secTransX;
		protected float _srcTransY;
		
		protected float _startPositionX;
		protected float _startPositionY;
		protected float _previousPositionX;
		protected float _previousPositionY;
    }
    
	///////////////////////////////////////////
	//TODO Move
    public static class Bezier extends ActionCondition {
    	
    }
}
