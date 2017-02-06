package com.cocos2dj.s2d;

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
	public Action cloneAction() {
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
	protected int 		_tag;
	 /** The action flag field. To categorize action into certain groups.*/
	protected int 		_flags;
	//fields<<
	
	
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
		
//		public final FiniteTimeAction reverse() {
//			return null;
//		}
		
		protected float _duration;
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
	    static Speed* create(ActionInterval* action, float speed);
	    /** Return the speed.
	     *
	     * @return The action speed.
	     */
	    inline float getSpeed(void) const { return _speed; }
	    /** Alter the speed of the inner function in runtime. 
	     *
	     * @param speed Alter the speed of the inner function in runtime.
	     */
	    inline void setSpeed(float speed) { _speed = speed; }

	    /** Replace the interior action.
	     *
	     * @param action The new action, it will replace the running action.
	     */
	    void setInnerAction(ActionInterval *action);
	    /** Return the interior action.
	     *
	     * @return The interior action.
	     */
	    inline ActionInterval* getInnerAction() const { return _innerAction; }

	    //
	    // Override
	    //
	    virtual Speed* clone() const override;
	    virtual Speed* reverse() const override;
	    virtual void startWithTarget(Node* target) override;
	    virtual void stop() override;
	    /**
	     * @param dt in seconds.
	     */
	    virtual void step(float dt) override;
	    /** Return true if the action has finished.
	     *
	     * @return Is true if the action has finished.
	     */
	    virtual bool isDone() const  override;
	    
	CC_CONSTRUCTOR_ACCESS:
	    Speed();
	    virtual ~Speed(void);
	    /** Initializes the action. */
	    bool initWithAction(ActionInterval *action, float speed);

	protected:
	    float _speed;
	    ActionInterval *_innerAction;
		
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
	    
	    static Follow* create(Node *followedNode, const Rect& rect = Rect::ZERO);
	    
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

	    static Follow* createWithOffset(Node* followedNode,float xOffset,float yOffset,const Rect& rect = Rect::ZERO);
	    
	    /** Return boundarySet.
	     *
	     * @return Return boundarySet.
	     */
	    inline bool isBoundarySet() const { return _boundarySet; }
	    /** Alter behavior - turn on/off boundary. 
	     *
	     * @param value Turn on/off boundary.
	     */
	    inline void setBoundarySet(bool value) { _boundarySet = value; }
	    
	    /** @deprecated Alter behavior - turn on/off boundary. 
	     *
	     * @param value Turn on/off boundary.
	     */
	    CC_DEPRECATED_ATTRIBUTE inline void setBoudarySet(bool value) { setBoundarySet(value); }

	    //
	    // Override
	    //
	    virtual Follow* clone() const override;
	    virtual Follow* reverse() const override;
	    /**
	     * @param dt in seconds.
	     * @js NA
	     */
	    virtual void step(float dt) override;
	    virtual bool isDone() const override;
	    virtual void stop() override;

	CC_CONSTRUCTOR_ACCESS:
	    /**
	     * @js ctor
	     */
	    Follow()
	    : _followedNode(nullptr)
	    , _boundarySet(false)
	    , _boundaryFullyCovered(false)
	    , _leftBoundary(0.0)
	    , _rightBoundary(0.0)
	    , _topBoundary(0.0)
	    , _bottomBoundary(0.0)
	    , _offsetX(0.0)
	    , _offsetY(0.0)
	    , _worldRect(Rect::ZERO)
	    {}
	    /**
	     * @js NA
	     * @lua NA
	     */
	    virtual ~Follow();
	    
	    /**
	     * Initializes the action with a set boundary or with no boundary.
	     *
	     * @param followedNode  The node to be followed.
	     * @param rect  The boundary. If \p rect is equal to Rect::ZERO, it'll work
	     *              with no boundary.
	    */
	    bool initWithTarget(Node *followedNode, const Rect& rect = Rect::ZERO);
	    
	    
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
	    bool initWithTargetAndOffset(Node *followedNode,float xOffset,float yOffset,const Rect& rect = Rect::ZERO);

	protected:
	    /** Node to follow. */
	    Node *_followedNode;

	    /** Whether camera should be limited to certain area. */
	    bool _boundarySet;

	    /** If screen size is bigger than the boundary - update not needed. */
	    bool _boundaryFullyCovered;

	    /** Fast access to the screen dimensions. */
	    Vec2 _halfScreenSize;
	    Vec2 _fullScreenSize;

	    /** World boundaries. */
	    float _leftBoundary;
	    float _rightBoundary;
	    float _topBoundary;
	    float _bottomBoundary;
	    
	    /** Horizontal (x) and vertical (y) offset values. */
	    float _offsetX;
	    float _offsetY;
	    
	    Rect _worldRect;

	}
}
