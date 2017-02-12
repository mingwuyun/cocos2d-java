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
	    	
	    }

	    /** Executes the callback.
	     */
	    public void execute() {
	    	
	    }

	    /** Get the selector target.
	     *
	     * @return The selector target.
	     */
	    inline Ref* getTargetCallback()
	    {
	        return _selectorTarget;
	    }

	    /** Set the selector target.
	     *
	     * @param sel The selector target.
	     */
	    inline void setTargetCallback(Ref* sel)
	    {
	        if (sel != _selectorTarget)
	        {
	            CC_SAFE_RETAIN(sel);
	            CC_SAFE_RELEASE(_selectorTarget);
	            _selectorTarget = sel;
	        }
	    }
	    //
	    // Overrides
	    //
	    /**
	     * @param time In seconds.
	     */
	    virtual void update(float time) override;
	    virtual CallFunc* reverse() const override;
	    virtual CallFunc* clone() const override;
	    
	CC_CONSTRUCTOR_ACCESS:
	    CallFunc()
	    : _selectorTarget(nullptr)
	    , _callFunc(nullptr)
	    , _function(nullptr)
	    {
	    }
	    virtual ~CallFunc();
	    
	    /** initializes the action with the std::function<void()>
	     * @lua NA
	     */
	    bool initWithFunction(const std::function<void()>& func);

	protected:
	    /** Target that will be called */
	    Ref*   _selectorTarget;

	    union
	    {
	        SEL_CallFunc    _callFunc;
	        SEL_CallFuncN    _callFuncN;
	    };
	    
	    /** function that will be called */
	    std::function<void()> _function;
	}
}
