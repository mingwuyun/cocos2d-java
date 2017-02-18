package com.cocos2dj.s2d;

import com.badlogic.gdx.math.Vector2;
import com.cocos2dj.macros.CCLog;
import com.cocos2dj.protocol.INode;
import com.cocos2dj.s2d.Action.InfiniteTimeAction;

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
	   final boolean stopFlag = _updateFunc == null ? false : _updateFunc.onUpdate(this, _elapsed); 
	   
	   if(!stopFlag) {
		   this.update(_elapsed);
	   } else {
		   if(_endFunc != null) {
			   _endFunc.onDone();
		   }
		   setDone(true);
		   return;
	   }
	   
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
    
    public static interface OnUpdateCallback {
    	/**@return if true end this action or call <br>self.setDone() */
    	public boolean onUpdate(ActionCondition self, float dt);
    }
    public static interface OnDoneCallback {
    	public void onDone();
    }
    public static interface ActionConditionCallback extends OnUpdateCallback, OnDoneCallback {}
    
    protected OnUpdateCallback	_updateFunc;
    protected OnDoneCallback	_endFunc;
    
    public void setUpdateCallback(OnUpdateCallback updateFunc) {
    	_updateFunc = updateFunc;
    }
    public void setEndCallback(OnDoneCallback endFunc) {
    	_endFunc = endFunc;
    }
    public void setActionCallback(ActionConditionCallback callback) {
    	_updateFunc = callback;
    	_endFunc = callback;
    }
    
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
//	    		System.out.println("t = " + t + _target.getPosition() + _secTransX);
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
	//TODO Bezier
    public static final float bezierat( float a, float b, float c, float d, float t ) {
		float tt = 1 - t;
		return ((float)Math.pow(tt, 3) * a 
				+ 3 * t * (float)Math.pow(tt, 2) * b
				+ 3 * (float)Math.pow(t, 2) * tt * c 
				+ (float)Math.pow(t, 3) * d);
	}
    
    public static class Bezier extends ActionCondition {
    	public static Bezier create(float t, float timeUnit, Vector2 end, Vector2 control) {
	    	return create(t, timeUnit, end, control, control);
	    }
    	
	    public static Bezier create(float t, float timeUnit, Vector2 end, Vector2 control1, Vector2 control2) {
	    	return create(t, timeUnit, end.x, end.y, control1.x, control1.y, control2.x, control2.y);
	    }
	    
	    public static Bezier create(float t, float timeUnit, float endX, float endY, float cX, float cY) {
	    	return create(t, timeUnit, endX, endY, cX, cY, cX, cY);
	    }
	    
	    /**
	     * Creates the action with a endTime, timeUnit and a bezier
	     * @param t	结束时间
	     * @param timeUnit	定义bezier曲线对单位时间
	     * @param endX bezier config 相对target的坐标
	     * @param endY
	     * @return
	     */
	    public static Bezier create(float t, float timeUnit, float endX, float endY, float c1X, float c1Y, float c2X, float c2Y) {
	    	Bezier ret = new Bezier();
	    	ret.initWithEndTime(t, timeUnit, endX, endY, c1X, c1Y, c2X, c2Y);
	    	return ret;
	    }
	    
	    //
	    // s
	    //
//	    public Bezier copy() {
//	    	return null;
////	    	return create(_duration, _endPositionX, _endPositionY, _control1X, _control1Y, _control2X, _control2Y);
//	    }
//	    public Bezier reverse() {
////	    	return create(_duration, -_endPositionX, -_endPositionX, 
////	    			_control2X - _endPositionX, _control2Y - _endPositionY, 
////	    			_control1X - _endPositionX, _control1Y - _endPositionY);
//	    	return null;
//	    }
	    
	    public void startWithTarget(INode target) {
	    	super.startWithTarget(target);
	    	Node node = (Node) target;
	    	_prevPositionX = _startPositionX = node.getPositionX();
	    	_prevPositionY = _startPositionY = node.getPositionY();
	    }
	    
	    /**
	     * @param time In seconds.
	     */
	    public void update(float time) {
	    	time /= _timeUnit;
	    	if(_target != null) {
	    		float x = bezierat(0, _control1X, _control2X, _endPositionX, time);
	    		float y = bezierat(0, _control1Y, _control2Y, _endPositionY, time);
	    		
if(CC_ENABLE_STACKABLE_ACTIONS) {
				float currPosX = _target.getPositionX();
				float currPosY = _target.getPositionY();
				float diffX = currPosX - _prevPositionX;
				float diffY = currPosY - _prevPositionY;
				
				_startPositionX += diffX;
				_startPositionY += diffY;
				
				float newPosX = _startPositionX + x;
				float newPosY = _startPositionY + y;
				_target.setPosition(newPosX, newPosY);
				_prevPositionX = newPosX;
				_prevPositionY = newPosY;
} else {
	    		_target.setPosition(_startPositionX + x,  _startPositionY + y);
}//CC_ENABLE_STACKABLE_ACTIONS
	    	}
	    }
	    
	    public Bezier() {}

	    /** 
	     * @param t timeUnit in seconds
	     */
	    public boolean initWithEndTime(float t, float timeUnit, float endX, float endY, float c1X, float c1Y, float c2X, float c2Y) {
	    	if(super.initWithEndTime(t)) {
	    		if(_timeUnit == 0f) {
	    			assert false : "timeUnit is zero!";
	    			return false;
	    		}
	    		_endPositionX = endX;
	    		_endPositionY = endY;
	    		
	    		_timeUnit = timeUnit;
	    		
	    		_control1X = c1X;
	    		_control1Y = c1Y;
	    		_control2X = c2X;
	    		_control2Y = c2Y;
	    		return true;
	    	}
	    	return false;
	    }

	    protected float 		_startPositionX;
	    protected float 		_startPositionY;
	    protected float			_prevPositionX;
	    protected float			_prevPositionY;
	    
	    protected float			_timeUnit = 1f;
	    
	    protected float 		_endPositionX;
	    protected float			_endPositionY;
	    protected float 		_control1X;
	    protected float			_control1Y;
	    protected float			_control2X;
	    protected float 		_control2Y;
    }
    
    
    /////////////////////////////////
    //TODO Jump
    /**
     * one time Jump. cannot set jumptimes
     */
    public static class Jump extends ActionCondition {
    	/** 
	     * Creates the action.
	     * @param endTime  -1 run forever, in seconds.
	     * @param position The jumping distance.
	     * @param height The jumping height.
	     * @param jumps The jumping times.
	     */
	    public static Jump create(float endTime, float timeUnit, Vector2 position, float height) {
	    	return create(endTime, timeUnit, position.x, position.y, height);
	    }
	    
	    /** 
	     * Creates the action.
	     * @param endTime endTime -1 run forever, in seconds.
	     * @param timeUnit 运动速率控制
	     * @param posX The jumping distance X.
	     * @param posY The jumping distance Y.
	     * @param height The jumping height.
	     * @param jumps The jumping times.
	     * @return An autoreleased JumpBy object.
	     */
	    public static Jump create(float endTime, float timeUnit, float posX, float posY, float height) {
	    	Jump ret = new Jump();
	    	if(ret.initWithEndTime(endTime, timeUnit, posX, posY, height)) {
	    		return ret;
	    	}
	    	return null;
	    }

//	    public Jump copy() {
//	    }
//	    public Jump reverse() {
//	    }
	    
	    public void startWithTarget(INode target) {
	    	super.startWithTarget(target);
	    	_prevPositionX = _startPositionX = _target.getPositionX();
	    	_prevPositionY = _startPositionY = _target.getPositionY();
	    }
	    /**
	     * @param time In seconds.
	     */
	    public void update(float t) {
	        // parabolic jump (since v0.8.2)
	    	t /= _timeUnit;
	        if (_target != null) {
	            float frac = t;// * _jumps % 1.0f;
	            float y = _height * 4 * frac * (1 - frac);
	            y += _deltaY * t;
	
	            float x = _deltaX * t;
if(CC_ENABLE_STACKABLE_ACTIONS) {
	            float currentPosX = _target.getPositionX();
	            float currentPosY = _target.getPositionY();
	
	            float diffX = currentPosX - _prevPositionX;
	            float diffY = currentPosY - _prevPositionY;
	            
	            _startPositionX = diffX + _startPositionX;
	            _startPositionY = diffY + _startPositionY;
	
	            float newPosX = _startPositionX + x;
	            float newPosY = _startPositionY + y;
	            
//	            System.out.println("update action set node pos >>>" + System.nanoTime());
//	            System.out.println("jump>>>" + _target.getPosition());
	            _target.setPosition(newPosX, newPosY);
	            
	            _prevPositionX = newPosX;
	            _prevPositionY = newPosY;
} else {
	            _target.setPosition(_startPositionX + x, _startPositionY + y);
}// !CC_ENABLE_STACKABLE_ACTIONS	
	        }
	    }
	    
	    public Jump() {}

	    /** 
	     * initializes the action
	     * @param duration in seconds
	     */
	    public boolean initWithEndTime(float endTime, float timeUnit, float deltaX, float deltaY, float height) {
	    	if(timeUnit == 0) {
	    		CCLog.error("Jump", "timeUnit error: Number of jumps must be >= 0");
	    		return false;
	    	}
	    	
	    	if(super.initWithEndTime(endTime)) {
	    		_timeUnit = timeUnit;
	    		_deltaX = deltaX;
	    		_deltaY = deltaY;
	    		_height = height;
	    		return true;
	    	}
	    	return false;
	    }

	    protected float _startPositionX;
	    protected float _startPositionY;
	    protected float _deltaX;
	    protected float _deltaY;
	    protected float _prevPositionX;
	    protected float _prevPositionY;
	    float           _height;
	    protected float _timeUnit = 1f;
    }
}
