package com.cocos2dj.base;

/**
 * EventCustom.java
 * <p>
 * 
 * @author Copyright (c) 2017 xu jun
 */
public class EventCustom extends Event {
	/** Constructor.
    *
    * @param eventName A given name of the custom event.
    * @js ctor
    */
   public EventCustom(String eventName) {
	   super(Type.CUSTOM);
	   this._eventName = eventName;
   }
   
   /** Sets user data.
    *
    * @param data The user data pointer, it's a void*.
    */
   public final void setUserData(Object data) { _userData = data; }
  
   /** Gets user data.
    *
    * @return The user data pointer, it's a void*.
    */
   public final Object getUserData() { return _userData; }
   
   /** Gets event name.
    *
    * @return The name of the event.
    */
   public final String getEventName() { return _eventName; }
   
   protected Object _userData;       ///< User data
   protected String _eventName;
}
