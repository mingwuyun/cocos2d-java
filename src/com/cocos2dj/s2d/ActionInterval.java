package com.cocos2dj.s2d;

import com.cocos2dj.macros.CCMacros;
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
	   
	// needed for rewind. elapsed could be negative
	   float updateDt = Math.max(0, Math.min(1, _elapsed / _duration));
	   
//	   if(send)
	   
	   this.update(updateDt);
   }
   
   public void startWithTarget(Node target) {
	   super.startWithTarget(target);
	   _elapsed = 0.0f;
	   _firstTick = true;
   }
   
//   public ActionInterval reverse() {
//       assert false : "";
//       return null;
//   }
//
//   virtual ActionInterval *clone() const override
//   {
//       CC_ASSERT(0);
//       return nullptr;
//   }

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

   protected float _elapsed;
   protected boolean   _firstTick;

   protected boolean sendUpdateEventToScript(float dt, Action actionObject) {
	   return false;
   }
   
   
   /////////////////////////////////////////
   //TODO Sequence
   
}
