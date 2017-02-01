package com.cocos2dj.base;

import com.cocos2dj.protocol.IFunctionOneArg;
import com.cocos2dj.protocol.INode;

/**
 * EventListener.java
 * <br>Type
 * <p>
 * @author Copyright (c) 2017 xu jun
 */
public abstract class EventListener {
	
	public static enum Type { 
		UNKNOWN,
	    TOUCH_ONE_BY_ONE,
	    TOUCH_ALL_AT_ONCE,
	    KEYBOARD,
	    MOUSE,
	    ACCELERATION,
	    FOCUS,
		GAME_CONTROLLER,
	    CUSTOM
	}
	
    /**
     * Constructor
     */
    EventListener() {
    	
    }

    /** 
     * Initializes event with type and callback function
     */
    protected boolean init(Type t,  String listenerID,  IFunctionOneArg<Event> callback) {
    	_onEvent = callback;
    	_type = t;
    	_listenerID = listenerID;
    	_isRegistered = false;
    	_paused = true;
    	_isEnabled = true;
    	
    	return true;
    }
    
    

    /** Checks whether the listener is available.
     * 
     * @return True if the listener is available.
     */
     public boolean checkAvailable() {
    	 return _onEvent != null;
     }

    /** Clones the listener, its subclasses have to override this method.
     */
//     public EventListener clone() {}

    /** Enables or disables the listener.
     * @note Only listeners with `enabled` state will be able to receive events.
     *        When an listener was initialized, it's enabled by default.
     *        An event listener can receive events when it is enabled and is not paused.
     *        paused state is always false when it is a fixed priority listener.
     *
     * @param enabled True if enables the listener.
     */
     public void setEnabled(boolean enabled) { _isEnabled = enabled; };

    /** Checks whether the listener is enabled.
     *
     * @return True if the listener is enabled.
     */
     public boolean isEnabled()  { return _isEnabled; };
     
    /** Sets paused state for the listener
     *  The paused state is only used for scene graph priority listeners.
     *  `EventDispatcher::resumeAllEventListenersForTarget(node)` will set the paused state to `true`,
     *  while `EventDispatcher::pauseAllEventListenersForTarget(node)` will set it to `false`.
     *  @note 1) Fixed priority listeners will never get paused. If a fixed priority doesn't want to receive events,
     *           call `setEnabled(false)` instead.
     *        2) In `Node`'s onEnter and onExit, the `paused state` of the listeners which associated with that node will be automatically updated.
     */
     protected void setPaused(boolean paused) { _paused = paused; };

    /** Checks whether the listener is paused */
     protected boolean isPaused()  { return _paused; };

    /** Marks the listener was registered by EventDispatcher */
     protected void setRegistered(boolean registered) { _isRegistered = registered; };

    /** Checks whether the listener was registered by EventDispatcher */
     protected boolean isRegistered()  { return _isRegistered; };

    /** Gets the type of this listener
     *  @note It's different from `EventType`, e.g. TouchEvent has two kinds of event listeners - EventListenerOneByOne, EventListenerAllAtOnce
     */
     protected Type getType()  { return _type; };

    /** Gets the listener ID of this listener
     *  When event is being dispatched, listener ID is used as key for searching listeners according to event type.
     */
     protected String getListenerID()  { return _listenerID; };

    /** Sets the fixed priority for this listener
     *  @note This method is only used for `fixed priority listeners`, it needs to access a non-zero value.
     *  0 is reserved for scene graph priority listeners
     */
     protected void setFixedPriority(int fixedPriority) { _fixedPriority = fixedPriority; };

    /** Gets the fixed priority of this listener
     *  @return 0 if it's a scene graph priority listener, non-zero for fixed priority listener
     */
     protected int getFixedPriority()  { return _fixedPriority; };

    /** Sets the node associated with this listener */
     protected void setAssociatedNode(INode node) { _node = node; };

    /** Gets the node associated with this listener
     *  @return nullptr if it's a fixed priority listener, otherwise return non-nullptr
     */
     protected INode getAssociatedNode()  { return _node; };

     
    //fields>>
    IFunctionOneArg<Event> _onEvent;   /// Event callback function

    Type _type;                             /// Event listener type
    String _listenerID;                 /// Event listener ID
    boolean _isRegistered;                     /// Whether the listener has been added to dispatcher.

    int   _fixedPriority;   // The higher the number, the higher the priority, 0 is for scene graph base priority.
    INode _node;            // scene graph based priority
    boolean _paused;           // Whether the listener is paused
    boolean _isEnabled;        // Whether the listener is enabled
}
