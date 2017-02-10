package com.cocos2dj.s2d;

import com.badlogic.gdx.math.Vector2;
import com.cocos2dj.macros.CCLog;
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
//	   System.out.println(this + " [" + _elapsed + " " + _duration+"]"); 
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
//	   dt *= 0.001;		//ms to second
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

//   protected float 		_elapsed;
//   protected boolean   	_firstTick;

   protected boolean sendUpdateEventToScript(float dt, Action actionObject) {
	   return false;
   }
   
   
   /////////////////////////////////////////
   //TODO Sequence
   public static class Sequence extends ActionInterval {
	    /** Helper ructor to create an array of sequenceable actions.
	     *
	     * @return An autoreleased Sequence object.
	     */
	    public static Sequence create(FiniteTimeAction...actions) {
	    	Sequence ret = new Sequence();
	    	ret.init(actions);
	    	return ret;
	    }

	    //
	    // Overrides
	    //
	    public Sequence copy() {
	    	FiniteTimeAction[] newActions = new FiniteTimeAction[_actions.length];
	    	final int n = _actions.length - 1;
	    	for(int i = 0; i <= n; ++i) {
	    		newActions[i] = _actions[i].copy();
	    	}
	    	return Sequence.create(newActions);
	    }
	    
	    public Sequence reverse() {
	    	FiniteTimeAction[] newActions = new FiniteTimeAction[_actions.length];
	    	final int n = _actions.length - 1;
	    	for(int i = 0; i <= n; ++i) {
	    		newActions[i] = _actions[n - i].reverse();
	    	}
	    	return Sequence.create(newActions);
	    }
	    
	    public void startWithTarget(INode target) {
	    	if (target == null) {
	            CCLog.error("Sequence", "Sequence::startWithTarget error: target is nullptr!");
	            return;
	        }
//	        if (_duration > CCMacros.FLT_EPSILON) {
////	            _split = _actions[0].getDuration() / _duration;
//	        }
	        super.startWithTarget(target);
	        
	        _currAction = 0;
	        _totalTime = 0;
	        _actions[0].startWithTarget(target);
//	        System.out.println("start >>>>>>>");
	    }
	    
	    public void stop() {
	    	for(int i = _currAction; i < _actions.length; ++i) {
	    		_actions[i].stop();
	    	}
//	    	if(_last != -1 && _actions[_last] != null) {
//	    		_actions[_last].stop();
//	    	}
	    	super.stop();
	    }
	    
	    /**
	     * @param t In seconds.
	     */
	    public void update(float t) {
	 	   if(_currAction >= _actions.length) {
	    		return;
	 	   }
	 	   
			FiniteTimeAction currAction = _actions[_currAction];
			float currActionElapsed = _elapsed - _totalTime;
			currAction._firstTick = false;
			currAction._elapsed = currActionElapsed;
			
//			System.out.println("totalDuration = " + _duration);
//			System.out.println("_elapsed" + _elapsed);
//			System.out.println("actionDuration = " + currAction._duration);
			
			if(currActionElapsed >= currAction._duration) {
				_totalTime += currAction._duration;
				
				_elapsed = _totalTime;		//防止精度问题，重置为标准时间
				
				++_currAction;
				if(_currAction < _actions.length) {
					_actions[_currAction].startWithTarget(_target);
				}
			} else {
				float updateDt = currActionElapsed / currAction._duration;
				updateDt = Math.max(0, Math.min(1, updateDt));
				currAction.update(updateDt);
			}
	    }
	    
	    public Sequence() {
	    	
	    }

	    /** initializes the action */
//	    public boolean initWithTwoActions(FiniteTimeAction *pActionOne, FiniteTimeAction *pActionTwo);
//	    bool init( Vector<FiniteTimeAction*>& arrayOfActions);
	    public boolean init(FiniteTimeAction...actions) {
	    	int count = actions.length;
	    	if(count == 0) {
	    		return false;
	    	}
	    	float totalD = 0;
	    	for(int i = 0; i < count; ++i) {
	    		totalD += actions[i].getDuration();
	    	}
	    	initWithDuration(totalD);
	    	
	    	_actions = actions;
	    	return true;
	    }
	    
	    protected FiniteTimeAction[] _actions;
	    protected int 		_currAction = 0;
	    protected float		_totalTime = 0;
//	    protected float _split;
//	    protected int _last;
   }
   
	/////////////////////////////////////////
	//TODO Repeat
   public static class Repeat extends ActionInterval {
	    /** Creates a Repeat action. Times is an unsigned integer between 1 and pow(2,30).
	     *
	     * @param action The action needs to repeat.
	     * @param times The repeat times.
	     * @return An autoreleased Repeat object.
	     */
	    public static Repeat create(FiniteTimeAction action, int times) {
	    	Repeat ret = new Repeat();
	    	ret.initWithAction(action, times);
	    	return ret;
	    }

	    /** Sets the inner action.
	     *
	     * @param action The inner action.
	     */
	    public final void setInnerAction(FiniteTimeAction action) {
	        if (_innerAction != action) {
	            _innerAction = action;
	        }
	    }

	    /** Gets the inner action.
	     *
	     * @return The inner action.
	     */
	    public final FiniteTimeAction getInnerAction() {
	        return _innerAction;
	    }

	    //
	    // Overrides
	    //
	    public Repeat copy() {
	    	return Repeat.create(_innerAction, _times);
	    }
	    public Repeat reverse() {
	    	return Repeat.create(_innerAction.reverse(), _times);
	    }
	    public void startWithTarget(INode target) {
	    	super.startWithTarget(target);
	    	_innerAction.startWithTarget(target);
	    }
	    public void stop() {
	    	_innerAction.stop();
	    	super.stop();
	    }
	    
	    /**
	     * @param t In seconds.
	     */
	    public void update(float t) {
	    	if(isDone()) {return;}
	    	
	    	final float totalTime = _innerAction._duration * _total;
			float currActionElapsed = _elapsed - totalTime;
			_innerAction._firstTick = false;
			_innerAction._elapsed = currActionElapsed;
			
			if(currActionElapsed >= _innerAction._duration) {
				_elapsed = totalTime + _innerAction._duration;		//防止精度问题，重置为标准时间
				
				++_total;
				if(_total < _times) {
					_innerAction.startWithTarget(_target);
				}
			} else {
				float updateDt = currActionElapsed / _innerAction._duration;
				updateDt = Math.max(0, Math.min(1, updateDt));
				_innerAction.update(updateDt);
			}
	    }
	    
	    public boolean isDone() {
	    	return _total >= _times;
	    }
	    
	    public Repeat() {
	    	
	    }

	    /** initializes a Repeat action. Times is an unsigned integer between 1 and pow(2,30) */
	    public boolean initWithAction(FiniteTimeAction action, int times) {
	    	float d = action.getDuration() * times;
	    	
	    	if(action != null && super.initWithDuration(d)) {
	    		_times = times;
	    		_innerAction = action;
//	    		_actionInstant = action instanceof ActionInstant ? true : false;
	    		_total = 0;
	    		return true;
	    	}
	    	
	    	return false;
	    }

	    protected int _times;
	    protected int _total;
	    protected float _nextDt;
//	    protected boolean _actionInstant;
	    /** Inner action */
	    protected FiniteTimeAction _innerAction;
   }
   
   //////////////////////////////////////////
   //TODO RepeatForever
   public static class  RepeatForever extends ActionInterval {
       /** Creates the action.
        *
        * @param action The action need to repeat forever.
        * @return An autoreleased RepeatForever object.
        */
       public static RepeatForever create(ActionInterval action) {
    	   RepeatForever ret = new RepeatForever();
    	   ret.initWithAction(action);
    	   return ret;
       }

       /** Sets the inner action.
        *
        * @param action The inner action.
        */
       public final void setInnerAction(ActionInterval action) {
           if (_innerAction != action) {
               _innerAction = action;
           }
       }

       /** Gets the inner action.
        *
        * @return The inner action.
        */
       public final ActionInterval getInnerAction() {
           return _innerAction;
       }

       //
       // Overrides
       //
       public RepeatForever copy() {
	    	return RepeatForever.create(_innerAction.copy());
	   }
	   public RepeatForever reverse() {
	    	return RepeatForever.create(_innerAction.reverse());
	   }
	   public void startWithTarget(INode target) {
	    	super.startWithTarget(target);
	    	_innerAction.startWithTarget(target);
	   }
	   
	   /**
	    * @param t In seconds.
	    */
	   public void step(float dt) {
	    	_innerAction.step(dt);
	    	if(_innerAction.isDone()) {
	    		float diff = _innerAction._elapsed - _innerAction._duration;
	            if (diff > _innerAction._duration) {
	                diff = diff % _innerAction._duration;	//fmodf
	            }
	            _innerAction.startWithTarget(_target);
	            _innerAction.step(0.0f);
	            _innerAction.step(diff);
	    	}
	   }
	   
	   public boolean isDone() {
	    	return false;
	   }
	    
       
       public RepeatForever() {
		   
	   }

       /** initializes the action */
       public boolean initWithAction(ActionInterval action) {
    	   assert action != null: "action can't be nullptr!";
    	    if (action == null) {
    	        CCLog.error("RepeatForever", "RepeatForever::initWithAction error:action is nullptr!");
    	        return false;
    	    }
    	    _innerAction = action;
    	    return true;
       }

       /** Inner action */
       protected ActionInterval _innerAction;
   }
   
   
   //////////////////////////////////////
   //TODO Spawn
   public static class Spawn extends ActionInterval {
	   
       public static Spawn create(FiniteTimeAction...actions) {
    	   Spawn ret = new Spawn();
    	   ret.init(actions);
    	   return ret;
       }

       //
       // Overrides
       //
        public Spawn clone() {
        	FiniteTimeAction[] newActions = new FiniteTimeAction[_actions.length];
	    	final int n = _actions.length - 1;
	    	for(int i = 0; i <= n; ++i) {
	    		newActions[i] = _actions[i].copy();
	    	}
        	return Spawn.create(newActions);
        }
        
        public Spawn reverse() {
        	FiniteTimeAction[] newActions = new FiniteTimeAction[_actions.length];
	    	final int n = _actions.length - 1;
	    	for(int i = 0; i <= n; ++i) {
	    		newActions[i] = _actions[i].reverse();
	    	}
        	return Spawn.create(newActions);
        }
        public void startWithTarget(INode target) {
        	if (target == null) {
                CCLog.error("Spawn", "Spawn::startWithTarget error: target is nullptr!");
                return;
            }
        	for(FiniteTimeAction action : _actions) {
        		action.startWithTarget(target);
        	}
        	super.startWithTarget(target);
        }
        public void stop() {
        	for(FiniteTimeAction action : _actions) {
        		action.stop();
        	}
        	super.stop();
        }
       /**
        * @param time In seconds.
        */
        public void update(float time) {
        	if(isDone()) {return;}
        	
        	for(FiniteTimeAction action : _actions) {
        		if(action._elapsed < 0) {
        			continue;
        		}
        		
        		action._elapsed = _elapsed;
        		float updateDt = _elapsed / action._duration;
				updateDt = Math.max(0, Math.min(1, updateDt));
				
				action.update(updateDt);
				if(updateDt >= 1) {
					action._elapsed = -1;	//stop Flag
				}
        	}
        }
       
       public Spawn() {
    	   
       }

       public boolean init(FiniteTimeAction...actions) {
    	   _actions = actions;
    	   float d = 0;
    	   for(int i = 0; i < actions.length; ++i) {
    		   float currD = actions[i]._duration;
    		   d = currD > d ? currD : d;
    	   }
    	   initWithDuration(d);
    	   return true;
       }
       
       protected FiniteTimeAction[] _actions;
   }
   
   
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
//	    static MoveBy* create(float duration,  Vec3& deltaPosition);
	
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
		public boolean initWithDuration(float duration, float deltaPositionX, float deltaPositionY) {
			if(super.initWithDuration(duration)) {
				_positionDeltaX = deltaPositionX;
				_positionDeltaY = deltaPositionY;
				return true;
			}
			return false;
		}
	
		protected float _positionDeltaX;
		protected float _positionDeltaY;
		protected float _startPositionX;
		protected float _startPositionY;
		protected float _previousPositionX;
		protected float _previousPositionY;
	}
   
	////////////////////////////
	//TODO MoveTo
   public static class MoveTo extends MoveBy {
	    /** 
	     * Creates the action.
	     * @param duration Duration time, in seconds.
	     * @param position The destination position in 2d.
	     * @return An autoreleased MoveTo object.
	     */
	    public static MoveTo create(float duration, Vector2 position) {
	    	return create(duration, position.x, position.y);
	    }
	    
	    public static MoveTo create(float duration, float x, float y) {
	    	MoveTo ret = new MoveTo();
	    	ret.initWithDuration(duration, x, y);
	    	return ret;
	    }

	    //
	    // Overrides
	    //
	    public MoveTo copy() {
	    	return MoveTo.create(_duration, _endX, _endY);
	    }
	    public MoveTo reverse() {
	    	throw new RuntimeException("reverse() not supported in MoveTo");
	    }
	    public void startWithTarget(INode arg_target) {
	    	super.startWithTarget(arg_target);
	    	Node target = (Node) arg_target;
	    	_positionDeltaX = _endX - target.getPositionX();
	    	_positionDeltaY = _endY - target.getPositionY(); 
	    }
	    
	    public MoveTo() {}
	    /** 
	     * initializes the action
	     * @param duration in seconds
	     */
	    public boolean initWithDuration(float duration,  float x, float y) {
	    	if(super.initWithDuration(duration)) {
	    		_endX = x;
	    		_endY = y;
	    		return true;
	    	}
	    	return false;
	    }

	    protected float 	_endX;
	    protected float 	_endY;
   }
   
   ////////////////////////////
   //TODO RotateTo
   public static class RotateTo extends ActionInterval {

	    /** 
	     * Creates the action.
	     *
	     * @param duration Duration time, in seconds.
	     * @param dstAngle In degreesCW.
	     * @return An autoreleased RotateTo object.
	     */
	    public static RotateTo create(float duration, float dstAngle) {
	    	RotateTo ret = new RotateTo();
	    	ret.initWithDuration(duration, dstAngle);
	    	return ret;
	    }

	    //
	    // Overrides
	    //
	    public RotateTo copy() {
	    	return RotateTo.create(_duration, _dstAngle);
	    }
	    public RotateTo reverse() {
	    	throw new RuntimeException("RotateTo doesn't support the 'reverse' method");
	    }
	    public void startWithTarget(INode target) {
	    	super.startWithTarget(target);
	    	calculateAngles();
	    }
	    /**
	     * @param time In seconds.
	     */
	    public void update(float t) {
	    	if(_target != null) {
	    		_target.setRotation(_startAngle + _diffAngle * t);
	    	}
	    }
	    
	    public RotateTo() {}

	    /** 
	     * initializes the action
	     * @param duration in seconds
	     * @param dstAngle in degrees
	     */
	    public boolean initWithDuration(float duration, float dstAngle) {
	    	if(super.initWithDuration(duration)) {
	    		_dstAngle = dstAngle;
	    		return true;
	    	}
	    	return false;
	    }

	    /** 
	     * calculates the start and diff angles
	     * @param dstAngle in degreesCW
	     */
	    final void calculateAngles() {
	    	if (_startAngle > 0) {
	            _startAngle = _startAngle % 360.0f;
	        } else {
	            _startAngle = _startAngle % -360.0f;
	        }
	        _diffAngle = _dstAngle - _startAngle;
	        if (_diffAngle > 180) {
	            _diffAngle -= 360;
	        }
	        if (_diffAngle < -180) {
	            _diffAngle += 360;
	        }
	    }
	    
	    protected float		_dstAngle;
	    protected float		_startAngle;
	    protected float 	_diffAngle;
   }
   
   ////////////////////////////
   //TODO RotateBy
   public static class RotateBy extends ActionInterval {
       /** 
        * Creates the action.
        *
        * @param duration Duration time, in seconds.
        * @param deltaAngle In degreesCW.
        * @return An autoreleased RotateBy object.
        */
       public static RotateBy create(float duration, float deltaAngle) {
    	   RotateBy ret = new RotateBy();
    	   ret.initWithDuration(duration, deltaAngle);
    	   return ret;
       }

       //
       // Override
       //
       public RotateBy copy() {
    	   return RotateBy.create(_duration, _deltaAngle);
       }
       public RotateBy reverse() {
    	   return RotateBy.create(_duration, -_deltaAngle);
       }
       public void startWithTarget(INode target) {
    	   super.startWithTarget(target);
    	   _startAngle = ((Node)target).getRotation();
       }
       /**
        * @param time In seconds.
        */
       public void update(float time) {
    	   if(_target != null) {
    		   float ret = _startAngle + _deltaAngle * time;
    		   _target.setRotation(ret);
    	   }
       }
       
       public RotateBy() {}

       /** initializes the action */
       public boolean initWithDuration(float duration, float deltaAngle) {
    	   if(super.initWithDuration(duration)) {
    		   _deltaAngle = deltaAngle;
    		   return true;
    	   }
    	   return false;
       }
       
       protected float _deltaAngle;
       protected float _startAngle;
   }
}
