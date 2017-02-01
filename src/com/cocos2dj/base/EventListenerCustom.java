package com.cocos2dj.base;

import com.cocos2dj.protocol.IFunctionOneArg;

public class EventListenerCustom extends EventListener {
	
    /** Creates an event listener with type and callback.
     * @param eventName The type of the event.
     * @param callback The callback function when the specified event was emitted.
     * @return An autoreleased EventListenerCustom object.
     */
    public static EventListenerCustom create(String eventName, IFunctionOneArg<EventCustom> callback) {
    	EventListenerCustom ret = new EventListenerCustom();
    	ret.init(eventName, callback);
    	return ret;
	}
    
    /// Overrides
    public boolean checkAvailable() {
    	return _onCustomEvent != null && super.checkAvailable();
    }
    
//    virtual EventListenerCustom* clone() override;
    
    /** Constructor */
    protected EventListenerCustom() {
    	
    }
    
    private IFunctionOneArg<Event> warpCallback = new IFunctionOneArg<Event>() {
		@Override
		public void callback(Event t) {
			if(_onCustomEvent != null) {
				_onCustomEvent.callback((EventCustom) t);
			}
		}
	};
	
    /** Initializes event with type and callback function */
    protected boolean init(String listenerId,  IFunctionOneArg<EventCustom> callback) {
    	_onCustomEvent = callback;
    	return super.init(Type.CUSTOM, listenerId, warpCallback);
    }
    
    protected IFunctionOneArg<EventCustom> _onCustomEvent;
    
}
