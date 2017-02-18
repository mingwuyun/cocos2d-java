package com.cocos2dj.s2d;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.cocos2dj.base.Director;
import com.cocos2dj.base.Rect;
import com.cocos2dj.base.Size;
import com.cocos2dj.macros.CCLog;
import com.cocos2dj.protocol.IAction;
import com.cocos2dj.protocol.INode;

/**
 * Action.java
 * <br>FiniteTimeAction
 * <br>Speed
 * <br>Follow
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
	public Action copy() {
		return null;
	}

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
	
	public boolean isAttached() {return _attached;}
	public void setAttached(boolean attached) {_attached = attached;}
	
	//fields>>
	protected INode		_originalTarget;
	/** 
     * The "target".
     * The target will be set with the 'startWithTarget' method.
     * When the 'stop' method is called, target will be set to nil.
     * The target is 'assigned', it is not 'retained'.
     */
	protected Node		_target;
	 /** The action tag. An identifier of the action. */
	protected int 		_tag = IAction.INVALID_TAG;
	 /** The action flag field. To categorize action into certain groups.*/
	protected int 		_flags = 0;
	
	protected boolean 	_attached = false;
	//fields<<
	
	
	/**
	 * 不限制执行时间的动作
	 * 需要自行设定终结条件
	 */
	public static class InfiniteTimeAction extends Action {
		
		public void setDone(boolean isDone) {_isDone = isDone;}
		
		public InfiniteTimeAction reverse() {
			return null;
		}
		public InfiniteTimeAction copy() {
			return null;
		}
		
		public boolean isDone() {
			return _isDone;
		}
		
		protected boolean _isDone;
	}
	
	/** @class FiniteTimeAction
	 * @brief
	 * Base class actions that do have a finite time duration.
	 * Possible actions:
	 * - An action with a duration of 0 seconds.
	 * - An action with a duration of 35.5 seconds.
	 * Infinite time actions are valid.
	 */
	public static class FiniteTimeAction extends Action {
		
		public final float getDuration() {return _duration;}
		public final void setDuration(float duration) {_duration = duration;}
		
		public FiniteTimeAction reverse() {
			return null;
		}
		
		public FiniteTimeAction copy() {
			return null;
		}
		
		protected float 		_duration;
		protected float 		_elapsed;
		protected boolean   	_firstTick;
	}
	
	/** @class Speed
	 * @brief Changes the speed of an action, making it take longer (speed>1)
	 * or shorter (speed<1) time.
	 * Useful to simulate 'slow motion' or 'fast forward' effect.
	 * @warning This action can't be Sequenceable because it is not an IntervalAction.
	 */
	public static class Speed extends Action {
	    /** Create the action and set the speed.
	     *
	     * @param action An action.
	     * @param speed The action speed.
	     */
	    public static Speed create(ActionInterval action, float speed) {
	    	Speed ret = new Speed();
	    	return ret;
	    }
	    
	    /** Return the speed.
	     *
	     * @return The action speed.
	     */
	    public final float getSpeed() { return _speed; }
	    /** Alter the speed of the inner function in runtime. 
	     *
	     * @param speed Alter the speed of the inner function in runtime.
	     */
	    public final void setSpeed(float speed) { _speed = speed; }

	    /** Replace the interior action.
	     *
	     * @param action The new action, it will replace the running action.
	     */
	    public void setInnerAction(ActionInterval action) {
	    	if(_innerAction != action) {
	    		_innerAction = action;
	    	}
	    }
	    /** Return the interior action.
	     *
	     * @return The interior action.
	     */
	    public final  ActionInterval getInnerAction() { return _innerAction; }

	    //
	    // Override
	    //
	    public Speed copy() {
	    	if(_innerAction != null) {
	    		return Speed.create(_innerAction.copy(), _speed);
	    	}
	    	return null;
	    }
	    
	    public Speed reverse() {
	    	if(_innerAction != null) {
	    		return Speed.create(_innerAction.reverse(), _speed);
	    	}
	    	return null;
	    }
	    
	    public void startWithTarget(INode target) {
	    	if(target != null && _innerAction != null) {
	    		super.startWithTarget(target);
	    		_innerAction.startWithTarget(target);
	    	} else {
	    		CCLog.error("Speed", "Speed::startWithTarget error: target(" + target +") or _innerAction(" + _innerAction + ") is nullptr!");
	    	}
	    }
	    
	    public void stop() {
	    	if(_innerAction != null) {
	    		_innerAction.stop();
	    	}
	    	super.stop();
	    }
	    /**
	     * @param dt in seconds.
	     */
	    public void step(float dt) {
	    	_innerAction.step(dt * _speed);
	    }
	    
	    /** Return true if the action has finished.
	     *
	     * @return Is true if the action has finished.
	     */
	    public boolean isDone() {
	    	return _innerAction.isDone();
	    }
	    
	    public Speed() {
	    	_speed = 0;
	    	_innerAction = null;
	    }
	    
	    /** Initializes the action. */
	    public boolean initWithAction(ActionInterval action, float speed) {
	    	 assert action != null: "action must not be NULL";
	    	 if (action == null) {
    	        CCLog.error("Speed", " error: action is nullptr!");
    	        return false;
    	    }
    	    
    	    _innerAction = action;
    	    _speed = speed;
    	    return true;
	    }

	    protected float _speed;
	    protected ActionInterval _innerAction;
	}
	
	/**
	 * Follow
	 * @brief Follow is an action that "follows" a node.
	 * Eg:
	 * @code
	 * layer->runAction(Follow::create(hero));
	 * @endcode
	 * Instead of using Camera as a "follower", use this action instead.
	 * @since v0.99.2
	 */
	public static class Follow extends Action {
	    /**
	     * Creates the action with a set boundary or with no boundary.
	     *
	     * @param followedNode  The node to be followed.
	     * @param rect  The boundary. If \p rect is equal to Rect::ZERO, it'll work
	     *              with no boundary.
	    */
	    
	    public static Follow create(Node followedNode, final Rect rect) {
	    	return createWithOffset(followedNode, 0, 0, rect);
	    }
	    
	    public static Follow create(Node followedNode) {
	    	return create(followedNode, Rect.Get(0, 0, 0, 0));
	    }
	    
	    /**
	     * Creates the action with a set boundary or with no boundary with offsets.
	     *
	     * @param followedNode  The node to be followed.
	     * @param rect  The boundary. If \p rect is equal to Rect::ZERO, it'll work
	     *              with no boundary.
	     * @param xOffset The horizontal offset from the center of the screen from which the
	     *               node  is to be followed.It can be positive,negative or zero.If
	     *               set to zero the node will be horizontally centered followed.
	     *  @param yOffset The vertical offset from the center of the screen from which the
	     *                 node is to be followed.It can be positive,negative or zero.
	     *                 If set to zero the node will be vertically centered followed.
	     *   If both xOffset and yOffset are set to zero,then the node will be horizontally and vertically centered followed.
	     */

	    public static Follow createWithOffset(Node followedNode,float xOffset,float yOffset, Rect rect) {
	    	Follow follow = new Follow();
	    	follow.initWithTargetAndOffset(followedNode, xOffset, yOffset, rect);
	    	return follow;
	    }
	    
	    /** Return boundarySet.
	     *
	     * @return Return boundarySet.
	     */
	    public final boolean isBoundarySet() { return _boundarySet; }
	    /** Alter behavior - turn on/off boundary. 
	     *
	     * @param value Turn on/off boundary.
	     */
	    public void setBoundarySet(boolean value) { _boundarySet = value;}
	    //
	    // Override
	    //
	    public Follow copy() {
	    	return Follow.createWithOffset(_followedNode, _offsetX, _offsetY, _worldRect);
	    }
	    
	    public Follow reverse() {
	    	return copy();
	    }
	    
	    /**
	     * @param dt in seconds.
	     * @js NA
	     */
	    public void step(float dt) {
	    	if(_boundarySet) {
	    		if(_boundaryFullyCovered) {
	    			return;
	    		}
	    		float tempX = _halfScreenSize.x - _followedNode.getPositionX();
				float tempY = _halfScreenSize.y - _followedNode.getPositionY();
				_target.setPosition(MathUtils.clamp(tempX, _leftBoundary, _rightBoundary),
						MathUtils.clamp(tempY, _bottomBoundary, _topBoundary));
	    	} else {
	    		_target.setPosition(_halfScreenSize.x - _followedNode.getPositionX(),
	    				_halfScreenSize.y - _followedNode.getPositionY());
	    	}
	    }
	    public boolean isDone() {
	    	return !_followedNode.isRunning();
	    }
	    public void stop() {
	    	_target = null;
	    	super.stop();
	    }

	    
	    public Follow() {}
	    
	    /**
	     * Initializes the action with a set boundary or with no boundary.
	     *
	     * @param followedNode  The node to be followed.
	     * @param rect  The boundary. If \p rect is equal to Rect::ZERO, it'll work
	     *              with no boundary.
	    */
	    public boolean initWithTarget(Node followedNode, Rect rect) {
	    	return initWithTargetAndOffset(followedNode, 0, 0, rect);
	    }
	    
	    
	    /**
	     * Initializes the action with a set boundary or with no boundary with offsets.
	     *
	     * @param followedNode  The node to be followed.
	     * @param rect  The boundary. If \p rect is equal to Rect::ZERO, it'll work
	     *              with no boundary.
	     * @param xOffset The horizontal offset from the center of the screen from which the
	     *                node  is to be followed.It can be positive,negative or zero.If
	     *                set to zero the node will be horizontally centered followed.
	     * @param yOffset The vertical offset from the center of the screen from which the
	     *                node is to be followed.It can be positive,negative or zero.
	     *                If set to zero the node will be vertically centered followed.
	     *   If both xOffset and yOffset are set to zero,then the node will be horizontally and vertically centered followed.

	     */
	    public boolean initWithTargetAndOffset(Node followedNode,float xOffset,float yOffset, Rect rect) {
	    	assert followedNode != null : "FollowedNode can't be NULL";
	        if(followedNode == null) {
	        	CCLog.error("Follow", "Follow::initWithTarget error: followedNode is nullptr!");
	            return false;
	        }
	     
	        _followedNode = followedNode;
	        _worldRect.set(rect);
	        _boundarySet = !rect.isZero(); //!rect.equals(Rect::ZERO);
	        _boundaryFullyCovered = false;

	        Size winSize = Director.getInstance().getWinSize();
	        _fullScreenSize.set(winSize.width, winSize.height);
	        _halfScreenSize = _fullScreenSize.scl(0.5f);
	        _offsetX=xOffset;
	        _offsetY=yOffset;
	        _halfScreenSize.x += _offsetX;
	        _halfScreenSize.y += _offsetY;
	        
	        if (_boundarySet) {
	            _leftBoundary = -((rect.x+rect.width) - _fullScreenSize.x);
	            _rightBoundary = -rect.x ;
	            _topBoundary = -rect.y;
	            _bottomBoundary = -((rect.y+rect.height) - _fullScreenSize.y);

	            if(_rightBoundary < _leftBoundary) {
	                // screen width is larger than world's boundary width
	                //set both in the middle of the world
	                _rightBoundary = _leftBoundary = (_leftBoundary + _rightBoundary) / 2;
	            }
	            if(_topBoundary < _bottomBoundary) {
	                // screen width is larger than world's boundary width
	                //set both in the middle of the world
	                _topBoundary = _bottomBoundary = (_topBoundary + _bottomBoundary) / 2;
	            }
	            if( (_topBoundary == _bottomBoundary) && (_leftBoundary == _rightBoundary) ) {
	                _boundaryFullyCovered = true;
	            }
	        }
	        
	        return true;
	    }

	
	    /** Node to follow. */
	    protected Node _followedNode;

	    /** Whether camera should be limited to certain area. */
	    protected boolean _boundarySet;

	    /** If screen size is bigger than the boundary - update not needed. */
	    protected boolean _boundaryFullyCovered;

	    /** Fast access to the screen dimensions. */
	    protected Vector2 _halfScreenSize;
	    protected Vector2 _fullScreenSize;

	    /** World boundaries. */
	    protected float _leftBoundary;
	    protected float _rightBoundary;
	    protected float _topBoundary;
	    protected float _bottomBoundary;
	    
	    /** Horizontal (x) and vertical (y) offset values. */
	    protected float _offsetX;
	    protected float _offsetY;
	    
	    protected Rect _worldRect = new Rect();
	}
}
