package com.cocos2dj.base;

import com.badlogic.gdx.utils.Array;
import com.cocos2dj.macros.CCLog;

/**
 * EventListenerTouchOneByOne.java
 * <p>
 * 
 * @author Copyright(c) 2017 xujun
 */
public class EventListenerTouchOneByOne extends EventListener {

	public static final String LISTENER_ID = "__cc_touch_one_by_one";
	
	public static interface OnTouchBeganCallback {public boolean onTouchBegan(Touch touch, Event event);}
	public static interface OnTouchMovedCallback {public void onTouchMoved(Touch touch, Event event);}
	public static interface OnTouchEndedCallback {public void onTouchEnded(Touch touch, Event event);}
	public static interface OnTouchCancelledCallback {public void onTouchCancelled(Touch touch, Event event);}
	
	OnTouchBeganCallback _onTouchBegan = TouchCallback.NULL;
	OnTouchMovedCallback _onTouchMoved = TouchCallback.NULL;
	OnTouchEndedCallback _onTouchEnded = TouchCallback.NULL;
	OnTouchCancelledCallback _onTouchCanceled;
	
	public void setOnTouchBeganCallback(OnTouchBeganCallback callback) {
		_onTouchBegan = callback == null ? TouchCallback.NULL : callback;
	}
	public void setOnTouchMovedCallback(OnTouchMovedCallback callback) {
		_onTouchMoved = callback == null ? TouchCallback.NULL : callback;
	}
	public void setOnTouchEndedCallback(OnTouchEndedCallback callback) {
		_onTouchEnded = callback == null ? TouchCallback.NULL : callback;
	}
	public void setOnTouchCancelledCallback(OnTouchCancelledCallback callback) {
		_onTouchCanceled = callback;
	}
	
	public static interface TouchCallback extends OnTouchBeganCallback, OnTouchMovedCallback, OnTouchEndedCallback {
//		public boolean onTouchBegan(Touch touch, Event event);
//		public void onTouchMoved(Touch touch, Event event);
//		public void onTouchEnded(Touch touch, Event event);
//		public void onTouchCancelled(Touch touch, Event event);
		public static TouchCallback NULL = new TouchCallback() {
			public boolean onTouchBegan(Touch touch, Event event) {return false;}
			public void onTouchMoved(Touch touch, Event event) {}
			public void onTouchEnded(Touch touch, Event event) {}
//			public void onTouchCancelled(Touch touch, Event event) {}
		};
	}
	
	
	public static EventListenerTouchOneByOne create() {
		EventListenerTouchOneByOne ret = new EventListenerTouchOneByOne();
		ret.init();
		return ret;
	}
    
    /** Whether or not to swall touches.
     *
     * @param needSwallow True if needs to swall touches.
     */
    public void setSwallowTouches(boolean needSwallow) {
    	_needSwallow = needSwallow;
    }
    
    /** Is swall touches or not.
     *
     * @return True if needs to swall touches.
     */
    public boolean isSwallowTouches() {
    	return _needSwallow;
    }
    
    /// Overrides
//    virtual EventListenerTouchOneByOne* clone() override;
//    virtual bool checkAvailable() override;
    
    
    public boolean onTouchBegan(Touch touch, Event event) {
    	return _onTouchBegan.onTouchBegan(touch, event);
    }
	public void onTouchMoved(Touch touch, Event event) {
//		_callback.onTouchMoved(touch, event);
		_onTouchMoved.onTouchMoved(touch, event);
	}
	public void onTouchEnded(Touch touch, Event event) {
		_onTouchEnded.onTouchEnded(touch, event);
	}
	public void onTouchCancelled(Touch touch, Event event) {
		if(_onTouchCanceled != null) {
			_onTouchCanceled.onTouchCancelled(touch, event);
		}
	}

//    private TouchCallback _callback = TouchCallback.NULL;
    public void setTouchCallback(TouchCallback callback) {
    	setOnTouchBeganCallback(callback);
    	setOnTouchMovedCallback(callback);
    	setOnTouchEndedCallback(callback);
    }
	
    @Override
    public boolean checkAvailable() {
        // EventDispatcher will use the return value of 'onTouchBegan' to determine whether to pass following 'move', 'end'
        // message to 'EventListenerTouchOneByOne' or not. So 'onTouchBegan' needs to be set.
        if (_onTouchBegan == null) {
            CCLog.error("EventListenerTouchOneByOne", "Invalid EventListenerTouchOneByOne!");
            return false;
        }
        return true;
    }
    
//CC_CONSTRUCTOR_ACCESS:
    public EventListenerTouchOneByOne() {
    	
    }
    
    boolean init() {
    	if(super.init(Type.TOUCH_ONE_BY_ONE, LISTENER_ID, null)) {
    		return true;
    	}
    	return false;	
    }
    
    Array<Touch> _claimedTouches = new Array<Touch>(2);
    boolean _needSwallow;
}
