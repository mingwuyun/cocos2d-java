package com.cocos2dj.base;

import com.badlogic.gdx.utils.Array;

public class EventTouch extends Event {

	public static final int MAX_TOUCHES = 15;
	
	public static enum EventCode {
		BEGAN,
		MOVED,
		ENDED,
		CANCELLED,
	}
	
	public EventTouch() {
		super(Type.TOUCH);
	}
	
	
	/** Get event code.
    *
    * @return The code of the event.
    */
   public EventCode getEventCode() { return _eventCode; };
   
   public void setEventCode(EventCode eventCode) {
	   _eventCode = eventCode;
   }
   
   /** Get the touches.
    *
    * @return The touches of the event.
    */
   public final Array<Touch> getTouches() { return _touches; };
   
   private EventCode _eventCode;
   private Array<Touch> _touches = new Array<>();
   
   
   
   /**不要调用 */
   public final void _clearTouch() {
	   _touches.clear();
   }
   
   /**不要调用 */
   public void _addTouch(Touch touch) {
	   //超出15个就删除一个
	   if(_touches.size > MAX_TOUCHES) {
		   _touches.removeIndex(0);
	   }
	   _touches.add(touch);
   }
}
