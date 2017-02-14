package com.cocos2dj.s2d;

import com.badlogic.gdx.graphics.Color;
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

   protected boolean CC_ENABLE_STACKABLE_ACTIONS = true;
   /**是否开启动作叠加模式 默认开启(true) */
   public void DEFINE_CC_ENABLE_STACKABLE_ACTIONS(boolean enableStackable) {
	   CC_ENABLE_STACKABLE_ACTIONS = enableStackable;
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
	    
	    public boolean isDone() {
	    	return _currAction >= _actions.length;
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
			
//			System.out.println("currAction = " + _currAction
//					+ " / maxAction = " + _actions.length);
//			System.out.println(currActionElapsed + " / " + currAction._duration);
//			System.out.println("totalDuration = " + _duration);
//			System.out.println("_elapsed" + _elapsed);
			
			if(currActionElapsed >= currAction._duration) {
				
				if(currAction._duration == 0) { //instant action.
					currAction.update(1f);
				}
				
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
			
			if(currActionElapsed >= _innerAction._duration 
					&& _innerAction.isDone()) {	//FIX: 确保完成
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
if (CC_ENABLE_STACKABLE_ACTIONS) {
				float currX = _target.getPositionX();
				float currY = _target.getPositionY();
				float diffX = currX - _previousPositionX;
				float diffY = currY - _previousPositionY;
				
				_startPositionX = _startPositionX + diffX;
				_startPositionY = _startPositionY + diffY;
				
				float newPosX = _startPositionX + _positionDeltaX * t;
				float newPosY = _startPositionY + _positionDeltaY * t;
				_target.setPosition(newPosX, newPosY);
				_previousPositionX = newPosX;
				_previousPositionY = newPosY;
} else {
	    		_target.setPosition(_startPositionX + _positionDeltaX * t, 
	    				_startPositionY + _positionDeltaY * t);
} //CC_ENABLE_STACKABLE_ACTIONS
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
   
	// Bezier cubic formula:
	// ((1 - t) + t)3 = 1 
	//Expands to ...
	//(1 - t)3 + 3t(1-t)2 + 3t2(1 - t) + t3 = 1 
	public static float bezierat( float a, float b, float c, float d, float t ) {
		float tt = 1 - t;
		return ((float)Math.pow(tt, 3) * a 
				+ 3 * t * (float)Math.pow(tt, 2) * b
				+ 3 * (float)Math.pow(t, 2) * tt * c 
				+ (float)Math.pow(t, 3) * d);
//	 return (powf(1-t,3) * a + 
//	         3*t*(powf(1-t,2))*b + 
//	         3*powf(t,2)*(1-t)*c +
//	         powf(t,3)*d );
	}
	
   //////////////////////////////////
   //TODO BezierBy
   public static class BezierBy extends ActionInterval {
	   
	    public static BezierBy create(float t, Vector2 end, Vector2 control) {
	    	return create(t, end, control, control);
	    }
	    /** Creates the action with a duration and a bezier configuration.
	     * @param t Duration time, in seconds.
	     * @param end
	     * @param control1
	     * @param control2
	     * @return An autoreleased BezierBy object.
	     * @code
	     * When this function bound to js or lua,the input params are changed.
	     * in js: var create(var t,var table)
	     * in lua: local create(local t, local table)
	     * @endcode
	     */
	    public static BezierBy create(float t, Vector2 end, Vector2 control1, Vector2 control2) {
	    	return create(t, end.x, end.y, control1.x, control1.y, control2.x, control2.y);
	    }
	    
	    public static BezierBy create(float t, float endX, float endY, float cX, float cY) {
	    	return create(t, endX, endY, cX, cY, cX, cY);
	    }
	    
	    public static BezierBy create(float t, float endX, float endY, float c1X, float c1Y, float c2X, float c2Y) {
	    	BezierBy ret = new BezierBy();
	    	ret.initWithDuration(t, endX, endY, c1X, c1Y, c2X, c2Y);
	    	return ret;
	    }
	    
	    //
	    // Overrides
	    //
	    public BezierBy copy() {
	    	return create(_duration, _endPositionX, _endPositionY, _control1X, _control1Y, _control2X, _control2Y);
	    }
	    public BezierBy reverse() {
	    	return create(_duration, -_endPositionX, -_endPositionX, 
	    			_control2X - _endPositionX, _control2Y - _endPositionY, 
	    			_control1X - _endPositionX, _control1Y - _endPositionY);
	    }
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
	    
	    public BezierBy() {}

	    /** 
	     * initializes the action with a duration and a bezier configuration
	     * @param t in seconds
	     */
	    public boolean initWithDuration(float t, float endX, float endY, float c1X, float c1Y, float c2X, float c2Y) {
	    	if(super.initWithDuration(t)) {
	    		_endPositionX = endX;
	    		_endPositionY = endY;
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
	    protected float 		_endPositionX;
	    protected float			_endPositionY;
	    protected float 		_control1X;
	    protected float			_control1Y;
	    protected float			_control2X;
	    protected float 		_control2Y;
//	    Vec2 _startPosition;
//	    Vec2 _previousPosition;
   }
   
   ////////////////////////////////////////////////////
   //TODO JumpBy
   public static class JumpBy extends ActionInterval {
	   /** 
	     * Creates the action.
	     * @param duration Duration time, in seconds.
	     * @param position The jumping distance.
	     * @param height The jumping height.
	     * @param jumps The jumping times.
	     * @return An autoreleased JumpBy object.
	     */
	    public static JumpBy create(float duration, Vector2 position, float height, int jumps) {
	    	return create(duration, position.x, position.y, height, jumps);
	    }
	    
	    /** 
	     * Creates the action.
	     * @param duration Duration time, in seconds.
	     * @param posX The jumping distance X.
	     * @param posY The jumping distance Y.
	     * @param height The jumping height.
	     * @param jumps The jumping times.
	     * @return An autoreleased JumpBy object.
	     */
	    public static JumpBy create(float duration, float posX, float posY, float height, int jumps) {
	    	JumpBy ret = new JumpBy();
	    	if(ret.initWithDuration(duration, posX, posY, height, jumps)) {
	    		return ret;
	    	}
	    	return null;
	    }

	    //
	    // Overrides
	    //
	    public JumpBy copy() {
	    	return JumpBy.create(_duration, _deltaX, _deltaY, _height, _jumps);
	    }
	    public JumpBy reverse() {
	    	return JumpBy.create(_duration, -_deltaX, -_deltaY, _height, _jumps);
	    }
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
	        if (_target != null) {
	            float frac = t * _jumps % 1.0f;
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
	    
	    public JumpBy() {}

	    /** 
	     * initializes the action
	     * @param duration in seconds
	     */
	    public boolean initWithDuration(float duration, float positionX, float positionY, float height, int jumps) {
	    	if(jumps < 0) {
	    		CCLog.error("JumpBy", "JumpBy::initWithDuration error: Number of jumps must be >= 0");
	    		return false;
	    	}
	    	
	    	if(super.initWithDuration(duration)) {
	    		_deltaX = positionX;
	    		_deltaY = positionY;
	    		_height = height;
	    		_jumps = jumps;
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
	    int             _jumps;
   }
   
   ///////////////////////////////////////////////////
   //TODO JumpTo
   public static class JumpTo extends JumpBy {
	   /** 
	     * Creates the action.
	     * @param duration Duration time, in seconds.
	     * @param position The jumping destination position.
	     * @param height The jumping height.
	     * @param jumps The jumping times.
	     * @return An autoreleased JumpTo object.
	     */
	    public static JumpTo create(float duration, Vector2 position, float height, int jumps) {
	    	return JumpTo.create(duration, position.x, position.y, height, jumps);
	    }
	    
	    /** 
	     * Creates the action.
	     * @param duration Duration time, in seconds.
	     * @param x The jumping destination position X.
	     * @param y The jumping destination position Y.
	     * @param height The jumping height.
	     * @param jumps The jumping times.
	     * @return An autoreleased JumpTo object.
	     */
	    public static JumpTo create(float duration, float x, float y, float height, int jumps) {
	    	JumpTo ret = new JumpTo();
	    	if(ret.initWithDuration(duration, x, y, height, jumps)) {
	    		return ret;
	    	}
	    	return null;
	    }

	    //
	    // Override
	    //
	     public void startWithTarget(INode target) {
	    	 super.startWithTarget(target);
	    	 _deltaX = _endPositionX - _startPositionX;
	    	 _deltaY = _endPositionY - _startPositionY;
//	    	 System.out.println(" " + _deltaX + ", " + _deltaY);
	     }
	     
	     public JumpTo copy() {
	    	 JumpTo ret = JumpTo.create(_duration, _endPositionX, _endPositionY, _height, _jumps);
	    	 return ret;
	     }
	     public JumpTo reverse() {
	    	 throw new RuntimeException("JumpTo doesn't support the 'reverse' method");
	     }

	    public JumpTo() {}
	    /** 
	     * initializes the action
	     * @param duration In seconds.
	     */
	    public boolean initWithDuration(float duration, float x, float y, float height, int jumps) {
	    	if(_jumps < 0) {
	    		CCLog.error("JumpTo", "JumpTo::initWithDuration error: Number of jumps must be >= 0");
	    		return false;
	    	}
	    	
	    	if(super.initWithDuration(duration)) {
	    		_endPositionX = x;
	    		_endPositionY = y;
	    		_height = height;
	    		_jumps = jumps;
	    		return true;
	    	}
	    	return false;
	    }

	    protected float _endPositionX;
	    protected float _endPositionY;
   }
   
   ////////////////////////////////////////////////////
   //TODO ScaleTo
   public static class ScaleTo extends ActionInterval {
	   /** 
	     * Creates the action with the same scale factor for X and Y.
	     * @param duration Duration time, in seconds.
	     * @param s Scale factor of x and y.
	     * @return An autoreleased ScaleTo object.
	     */
	    public static ScaleTo create(float duration, float s) {
	    	ScaleTo ret = new ScaleTo();
	    	ret.initWithDuration(duration, s);
	    	return ret;
	    }
	
	    /** 
	     * Creates the action with and X factor and a Y factor.
	     * @param duration Duration time, in seconds.
	     * @param sx Scale factor of x.
	     * @param sy Scale factor of y.
	     * @return An autoreleased ScaleTo object.
	     */
	    public static ScaleTo create(float duration, float sx, float sy) {
	    	ScaleTo ret = new ScaleTo();
	    	ret.initWithDuration(duration, sx, sy);
	    	return ret;
	    }
	
	    //
	    // Overrides
	    //
	     public ScaleTo copy() {
	    	 return ScaleTo.create(_duration, _endScaleX, _endScaleY);
	     }
	     public ScaleTo reverse() {
	    	 throw new RuntimeException("JumpTo doesn't support the 'reverse' method");
	     }
	     
	     public void startWithTarget(INode target) {
	    	 super.startWithTarget(target);
	    	 _startScaleX = _target.getScaleX();
	    	 _startScaleY = _target.getScaleY();
	    	 _deltaX = _endScaleX - _startScaleX;
	    	 _deltaY = _endScaleY - _startScaleY;
	     }
	     
	    /**
	     * @param time In seconds.
	     */
	     public void update(float time) {
	    	 _target.setScaleX(_startScaleX + _deltaX * time);
	    	 _target.setScaleY(_startScaleY + _deltaY * time);
	     }
	     
	    
	    public ScaleTo() {}
	
	    /** 
	     * initializes the action with the same scale factor for X and Y
	     * @param duration in seconds
	     */
	    public boolean initWithDuration(float duration, float s) {
	    	if(super.initWithDuration(duration)) {
	    		_endScaleX = s;
	    		_endScaleY = s;
	    		return true;
	    	}
	    	return false;
	    }
	    /** 
	     * initializes the action with and X factor and a Y factor 
	     * @param duration in seconds
	     */
	    public boolean initWithDuration(float duration, float sx, float sy) {
	    	if(super.initWithDuration(duration)) {
	    		_endScaleX = sx;
	    		_endScaleY = sy;
	    		return true;
	    	}
	    	return false;
	    }
	
	    protected float _scaleX;
	    protected float _scaleY;
	    protected float _startScaleX;
	    protected float _startScaleY;
	    protected float _endScaleX;
	    protected float _endScaleY;
	    protected float _deltaX;
	    protected float _deltaY;
   }
   
   //////////////////////////////////////
   //TODO ScaleBy
   public static class ScaleBy extends ScaleTo {
	    /** 
	     * Creates the action with the same scale factor for X and Y.
	     * @param duration Duration time, in seconds.
	     * @param s Scale factor of x and y.
	     * @return An autoreleased ScaleBy object.
	     */
	    public static ScaleBy create(float duration, float s) {
	    	return ScaleBy.create(duration, s, s);
	    }

	    /** 
	     * <b>scaleBy的运行逻辑 newScale = oldScale * deltaScale
	     * 执行两次scaleBy 1.5f的结果等于执行一次scaleBy 2.25, 而不是3
	     * </b>
	     * Creates the action with and X factor and a Y factor.
	     * @param duration Duration time, in seconds.
	     * @param sx Scale factor of x.
	     * @param sy Scale factor of y.
	     * @return An autoreleased ScaleBy object.
	     */
	    public static ScaleBy create(float duration, float sx, float sy) {
	    	ScaleBy ret = new ScaleBy();
	    	if(ret.initWithDuration(duration, sx, sy)) {
	    		return ret;
	    	}
	    	return null;
	    }

	    //
	    // Overrides
	    //
	    public void startWithTarget(INode target) {
	    	super.startWithTarget(target);
	    	_deltaX = _startScaleX * _endScaleX - _startScaleX;
	    	_deltaY = _startScaleY * _endScaleY - _startScaleY;
	    }
	    public ScaleBy copy() {
	    	return ScaleBy.create(_duration, _endScaleX, _endScaleY);
	    }
	    public ScaleBy reverse() {
	    	return ScaleBy.create(_duration, 1f / _endScaleX, 1f / _endScaleY);
	    }
	}
   
   	/////////////////////////////////////
   	//TODO Blink 
    public static class Blink extends ActionInterval {
    	/** 
         * Creates the action.
         * @param duration Duration time, in seconds.
         * @param blinks Blink times.
         * @return An autoreleased Blink object.
         */
        public static Blink create(float duration, int blinks) {
        	Blink ret = new Blink();
        	if(ret.initWithDuration(duration, blinks)) {
        		return ret;
        	}
        	return null;
        }

        //
        // Overrides
        //
         public Blink copy() {
        	 return Blink.create(_duration, _times);
         }
         public Blink reverse() {
        	 return Blink.create(_duration, _times);
         }
        /**
         * @param time In seconds.
         */
         public void update(float time) {
        	 if(!isDone()) {
        		 float slice = 1.0f / _times;
        		 float m = time % slice;
        		 _target.setVisible(m > slice/2f);
        	 }
         }
         public void startWithTarget(INode target) { 
        	 super.startWithTarget(target);
        	 _originalState = _target.isVisible();
         }
         public void stop() { 
        	 _target.setVisible(_originalState);
        	 super.stop();
         }
        
        public Blink() {}

        /** 
         * initializes the action 
         * @param duration in seconds
         */
        public boolean initWithDuration(float duration, int blinks) {
        	assert blinks>=0: "blinks should be >= 0";
            if (blinks < 0) {
                CCLog.error("Blink", "Blink::initWithDuration error:blinks should be >= 0");
                return false;
            }
            if (super.initWithDuration(duration)) {
                _times = blinks;
                return true;
            }
            return false;
        }
        
        protected int _times;
        protected boolean _originalState;
    }
    
    //////////////////////////////////
    //FadeTo
    public static class FadeTo extends ActionInterval {
    	/** 
         * Creates an action with duration and opacity.
         * @param duration Duration time, in seconds.
         * @param opacity A certain opacity, the range is from 0 to 255.
         * @return An autoreleased FadeTo object.
         */
        public static FadeTo create(float duration, float opacity) {
        	FadeTo ret = new FadeTo();
        	if(ret.initWithDuration(duration, opacity)) {
        		return ret;
        	}
        	return null;
        }

        //
        // Overrides
        //
         public FadeTo clone()  {
        	 return FadeTo.create(_duration, _toOpacity);
         }
         public FadeTo reverse()  {
        	 throw new RuntimeException("reverse() not supported in FadeTo");
         }
         public void startWithTarget(INode target) {
        	 super.startWithTarget(target);
        	 _fromOpacity = _target.getOpacity();
         }
        /**
         * @param time In seconds.
         */
         public void update(float time) {
        	 _target.setOpacity(_fromOpacity + (_toOpacity - _fromOpacity) * time);
         }
        
        public FadeTo() {}

        /** 
         * initializes the action with duration and opacity 
         * @param duration in seconds
         */
        public boolean initWithDuration(float duration, float opacity) {
        	if(opacity > 1f) {
        		CCLog.error("FadeTo", "opacity should in range [0, 1]");
        	}
        	if(super.initWithDuration(duration)) {
        		_toOpacity = opacity;
        		return true;
        	}
        	return false;
        }

    	protected float _toOpacity;
        protected float _fromOpacity;
    }

    ///////////////////////////////////////
    //TODO FadeIn
    public static class FadeIn extends FadeTo {
        /** 
         * Creates the action.
         * @param d Duration time, in seconds.
         * @return An autoreleased FadeIn object.
         */
        public static FadeIn create(float d) {
        	FadeIn ret = new FadeIn();
        	if(ret.initWithDuration(d, 1f)) {
        		return ret;
        	}
        	return null;
        }

        //
        // Overrides
        //
        public void startWithTarget(INode target) {
        	super.startWithTarget(target);
        	_toOpacity = 1f;
        	_fromOpacity = _target.getOpacity();
        }
        public FadeIn clone() {
        	return FadeIn.create(_duration);
        }
        public FadeOut reverse() {
        	return FadeOut.create(_duration);
//        	FadeOut.create(_duration)
        }

//        public void setReverseAction(FadeTo ac) {
//        	this._reverseAction = ac;
//        }

        public FadeIn() {}

//        protected FadeTo _reverseAction;
    };

    ///////////////////////////////////////
    //TODO FadeOut
    public static class FadeOut extends FadeTo {
        /** 
         * Creates the action.
         * @param d Duration time, in seconds.
         */
        public static FadeOut create(float d) {
        	FadeOut ret = new FadeOut();
        	if(ret.initWithDuration(d, 0)) {
        		return ret;
        	}
        	return null;
        }

	    //
	    // Overrides
	    //
	    public void startWithTarget(INode target) {
	    	super.startWithTarget(target);
	    	_toOpacity = 0f;
	    	_fromOpacity = _target.getOpacity();
	    }
	    public FadeOut clone() {
	    	return FadeOut.create(_duration);
	    }
	    public FadeIn reverse() {
	    	return FadeIn.create(_duration);
	    }
	
//	    public void setReverseAction(FadeTo ac) {
//	    	
//	    }
	    public FadeOut() {}
//	    protected FadeTo _reverseAction;
    }
    
    /////////////////////////////////////////
    //TODO TintTo
    public static class TintTo extends ActionInterval {
    	/** 
         * Creates an action with duration and color.
         * @param duration Duration time, in seconds.
         * @param red Red Color, from 0 to 255.
         * @param green Green Color, from 0 to 255.
         * @param blue Blue Color, from 0 to 255.
         * @return An autoreleased TintTo object.
         */
        public static TintTo create(float duration, float red, float green, float blue) {
        	TintTo ret = new TintTo();
        	if(ret.initWithDuration(duration, red, green, blue)) {
        		return ret;
        	}
        	return null;
        }
        
        /**
         * Creates an action with duration and color.
         * @param duration Duration time, in seconds.
         * @param color It's a Color3B type.
         * @return An autoreleased TintTo object.
         */
        public static TintTo create(float duration, final Color color) {
        	return TintTo.create(duration, color.r, color.g, color.b);
        }

        //
        // Overrides
        //
        public TintTo copy() {
        	return TintTo.create(_duration, _toR, _fromG, _fromB);
        }
        public TintTo reverse() {
        	throw new RuntimeException("reverse() not supported in TintTo");
        }
        public void startWithTarget(INode target) {
        	super.startWithTarget(target);
        	_fromR = _target.getColorR();
        	_fromG = _target.getColorG();
        	_fromB = _target.getColorB();
        }
        /**
         * @param time In seconds.
         */
        public void update(float time) {
        	_target.setColor(_fromR + (_toR - _fromR) * time, 
        			_fromG + (_toG - _fromG) * time, 
        			_fromB + (_toB - _fromB) * time);
        }
        
        public TintTo() {}

        /** initializes the action with duration and color */
        public boolean initWithDuration(float duration, float red, float green, float blue) {
        	if(red > 1f || green > 1f || blue > 1f) {
        		CCLog.debug("TintTo", "r, g, b range is [0, 1]");
        	}
        	_toR = red;
        	_toG = green;
        	_toB = blue;
        	
        	if(super.initWithDuration(duration)) {
        		return true;
        	}
        	return false;
        }

        float _toR, _toG, _toB;
        float _fromR, _fromG, _fromB;
    }
    
    
    ///////////////////////////////
    //TODO TintBy
    public static class TintBy extends ActionInterval {
        /** 
         * Creates an action with duration and color.
         * @param duration Duration time, in seconds.
         * @param deltaRed Delta red color.
         * @param deltaGreen Delta green color.
         * @param deltaBlue Delta blue color.
         * @return An autoreleased TintBy object.
         */
        public static TintBy create(float duration, float deltaRed, float deltaGreen, float deltaBlue) {
        	
        }

        //
        // Overrides
        //
        public TintBy copy() {
        	 
        }
        public TintBy reverse() {
        	 
        }
        public void startWithTarget(INode target) {
        	 
        }
        /**
         * @param time In seconds.
         */
        public void update(float time) {
        	 
         }
        
        TintBy() {}

        /** initializes the action with duration and color */
        boolean initWithDuration(float duration, float deltaRed, float deltaGreen, float deltaBlue) {
        	
        }
        
        float _deltaR;
        float _deltaG;
        float _deltaB;

        float _fromR;
        float _fromG;
        float _fromB;
    }
}
