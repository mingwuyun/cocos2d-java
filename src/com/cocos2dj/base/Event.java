package com.cocos2dj.base;

import com.cocos2dj.protocol.INode;

/**
 * Event.java
 * <br>Type
 * <p>
 * 
 * @author Copyright (c) 2017 xu jun
 */
public class Event {
	
	public static enum Type {
        TOUCH,
        KEYBOARD,
        ACCELERATION,
        MOUSE,
        FOCUS,
        GAME_CONTROLLER,
        CUSTOM
    };
    
	public <T> T getObject(T t) {
		return t;
	}
	
	Event(Type type) {
		this._type = type;
	}
	

    /** Gets the event type.
     *
     * @return The event type.
     */
	public Type getType() { return _type; };
    
    /** Stops propagation for current event.
     */
	public void stopPropagation() { _isStopped = true; };
    
    /** Checks whether the event has been stopped.
     *
     * @return True if the event has been stopped.
     */
	public boolean isStopped() { return _isStopped; };
    
    /** Gets current target of the event.
     * @return The target with which the event associates.
     * @note It's only available when the event listener is associated with node.
     *        It returns 0 when the listener is associated with fixed priority.
     */
	public INode getCurrentTarget() { return _currentTarget; };
    
    /** Sets current target */
	protected void setCurrentTarget(INode target) { _currentTarget = target; };
    
	protected Type _type;     ///< Event type
    
	protected boolean _isStopped;       ///< whether the event has been stopped.
	protected INode _currentTarget;  ///< Current target
    
}
