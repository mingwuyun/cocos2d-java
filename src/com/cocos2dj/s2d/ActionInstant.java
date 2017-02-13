package com.cocos2dj.s2d;

import com.cocos2dj.macros.CCLog;
import com.cocos2dj.s2d.Action.FiniteTimeAction;

/**
 * ActionInstant.java
 * <p>
 *  瞬时动作
 *  @brief Instant actions are immediate actions. They don't have a duration like the IntervalAction actions.
 *  
 * @author Copyright(c) 2017 xujun
 */
public class ActionInstant extends FiniteTimeAction {

	public ActionInstant copy() {
		CCLog.error("ActionInstant", "should override method copy()");
		return null;
	}
	
	public ActionInstant reverse() {
		CCLog.error("ActionInstant", "should override method copy()");
		return null;
	}
	
	public boolean isDone() {
		return true;
	}
	public void step(float dt) {
		update(1f);
	}
	public void update(float t) {
		
	}
	
	//////////////////////////////////////
	//TODO CallFunc
	public static class CallFunc extends ActionInstant {
		
		/** Creates the action with the callback of type std::function<void()>.
	     This is the preferred way to create the callback.
	     * When this function bound in js or lua ,the input param will be changed.
	     * In js: var create(var func, var this, var [data]) or var create(var func).
	     * In lua:local create(local funcID).
	     *
	     * @param func  A callback function need to be executed.
	     * @return  An autoreleased CallFunc object.
	     */
	    public static CallFunc create(Runnable func) {
	    	CallFunc ret = new CallFunc();
	    	if(ret.initWithFunction(func)) {
	    		return ret;
	    	}
	    	return null;
	    }

	    /** Executes the callback.
	     */
	    public void execute() {
	    	if(_function != null) {
	    		_function.run();
	    	}
	    }

	    //
	    // Overrides
	    //
	    /**
	     * @param time In seconds.
	     */
	    public void update(float time) {
	    	execute();
	    }
	    public CallFunc reverse() {
	    	return CallFunc.create(_function);
	    }
	    public CallFunc copy() {
	    	return CallFunc.create(_function);
	    }
	    
//	CC_CONSTRUCTOR_ACCESS:
	    public CallFunc() {
	    	
	    }
	    
	    /** initializes the action with the std::function<void()>
	     * @lua NA
	     */
	    public boolean initWithFunction(Runnable func) {
	    	this._function = func;
	    	return true;
	    }

	    
	    /** function that will be called */
	    Runnable	_function;
//	    std::function<void()> _function;
	}
}
