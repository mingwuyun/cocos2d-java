package com.cocos2dj.s2d;

import com.badlogic.gdx.math.Vector2;
import com.cocos2dj.macros.CCMacros;
import com.cocos2dj.protocol.INode;
import com.cocos2dj.s2d.Action.FiniteTimeAction;

/**
 * ActionInterval.java
 * <p>
 * 
 * @author Copyright (c) 2017 xujun
 *
 */
public class ActionInterval extends FiniteTimeAction {
	
	/** How many seconds had elapsed since the actions started to run.
    *
    * @return The seconds had elapsed since the actions started to run.
    */
   public float getElapsed() { return _elapsed; }

   /** Sets the amplitude rate, extension in GridAction
    *
    * @param amp   The amplitude rate.
    */
   public void setAmplitudeRate(float amp) {
	    // Abstract class needs implementation
	    assert false: "Subclass should implement this method!";
   }
   
   /** Gets the amplitude rate, extension in GridAction
    *
    * @return  The amplitude rate.
    */
   public float getAmplitudeRate() {
	    assert false: "Subclass should implement this method!";
   		return 0;
   }

   //
   // Overrides
   public boolean isDone() {
	   return _elapsed >= _duration;
   }
   
   public ActionInterval copy() {
	   return null;
   }
   
   public ActionInterval reverse() {
		return null;
	}
   
   /**
    * @param dt in seconds
    */
   public void step(float dt) {
	   dt *= 0.001;		//ms to second
	   if(_firstTick) {
		   _firstTick = false;
		   _elapsed = 0;
	   } else {
		   _elapsed += dt;
	   }
	   
	// needed for rewind. elapsed could be negative
	   float updateDt = Math.max(0, Math.min(1, _elapsed / _duration));
	   
	   this.update(updateDt);
   }
   
   public void startWithTarget(INode target) {
	   super.startWithTarget(target);
	   _elapsed = 0.0f;
	   _firstTick = true;
   }
   

   /** initializes the action */
   boolean initWithDuration(float d) {
	   _duration = d;

	    // prevent division by 0
	    // This comparison could be in step:, but it might decrease the performance
	    // by 3% in heavy based action games.
	    if (_duration <= CCMacros.FLT_EPSILON)
	    {
	        _duration = CCMacros.FLT_EPSILON;
	    }

	    _elapsed = 0;
	    _firstTick = true;

	    return true;
   }

   protected float 		_elapsed;
   protected boolean   	_firstTick;

   protected boolean sendUpdateEventToScript(float dt, Action actionObject) {
	   return false;
   }
   
   
   /////////////////////////////////////////
   //TODO Sequence
   
   
   /////////////////////////////////////////
   //TODO MoveBy
   public static class MoveBy extends ActionInterval {
	   
	   /** 
		 * Creates the action.
		 *
		 * @param duration Duration time, in seconds.
		 * @param deltaPosition The delta distance in 2d, it's a Vec2 type.
		 * @return An autoreleased MoveBy object.
		 */
	    public static MoveBy create(float duration, Vector2 deltaPosition) {
	    	return create(duration, deltaPosition.x, deltaPosition.y);
	    }
	    
	    public static MoveBy create(float duration, float deltaPositionX, float deltaPositionY) {
	    	MoveBy ret = new MoveBy();
	    	ret.initWithDuration(duration, deltaPositionX, deltaPositionY);
	    	return ret;
	    }
	    
	    /**
	     * Creates the action.
	     *
	     * @param duration Duration time, in seconds.
	     * @param deltaPosition The delta distance in 3d, it's a Vec3 type.
	     * @return An autoreleased MoveBy object.
	     */
//	    static MoveBy* create(float duration, const Vec3& deltaPosition);
	
	    //
	    // Overrides
	    //
	    public MoveBy copy() {
	    	return MoveBy.create(_duration, _positionDeltaX, _positionDeltaY);
	    }
	    public MoveBy reverse() {
	    	return MoveBy.create(_duration, -_positionDeltaX, -_positionDeltaY);
	    }
	    public void startWithTarget(INode arg_target) {
	    	Node target = (Node) arg_target;
	    	super.startWithTarget(target);
	    	_previousPositionX = _startPositionX = target.getPositionX();
	    	_previousPositionY = _startPositionY = target.getPositionY();
	    }
	    /**
	     * @param time in seconds
	     */
	    public void update(float t) {
	    	if(_target != null) {
	    		_target.setPosition(_startPositionX + _positionDeltaX * t, 
	    				_startPositionY + _positionDeltaY * t);
	    	}
	    }
	    
	    /** initializes the action */
		public final boolean initWithDuration(float duration, Vector2 deltaPosition) {
			return initWithDuration(duration, deltaPosition.x, deltaPosition.y);
		}
		public final boolean initWithDuration(float duration, float deltaPositionX, float deltaPositionY) {
			if(super.initWithDuration(duration)) {
				_positionDeltaX = deltaPositionX;
				_positionDeltaY = deltaPositionY;
				return true;
			}
			return false;
		}
//		public boolean initWithDuration(float duration, const Vec3& deltaPosition) {
//			
//		}
	
		protected float _positionDeltaX;
		protected float _positionDeltaY;
		protected float _startPositionX;
		protected float _startPositionY;
		protected float _previousPositionX;
		protected float _previousPositionY;
	}
}
