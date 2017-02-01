package com.cocos2dj.base;

import com.badlogic.gdx.utils.Array;

public class EventListenerTouchOneByOne extends EventListener {

	public static final String LISTENER_ID = "__cc_touch_one_by_one";
	
	public static interface TouchCallback {
		public boolean onTouchBegan(Touch touch, Event event);
		public void onTouchMoved(Touch touch, Event event);
		public void onTouchEnded(Touch touch, Event event);
		public void onTouchCancelled(Touch touch, Event event);
		
		public static TouchCallback NULL = new TouchCallback() {
			public boolean onTouchBegan(Touch touch, Event event) {return false;}
			public void onTouchMoved(Touch touch, Event event) {}
			public void onTouchEnded(Touch touch, Event event) {}
			public void onTouchCancelled(Touch touch, Event event) {}
		};
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
    	return _callback.onTouchBegan(touch, event);
    }
	public void onTouchMoved(Touch touch, Event event) {
		_callback.onTouchMoved(touch, event);
	}
	public void onTouchEnded(Touch touch, Event event) {
		_callback.onTouchEnded(touch, event);
	}
	public void onTouchCancelled(Touch touch, Event event) {
		_callback.onTouchCancelled(touch, event);
	}

    private TouchCallback _callback = TouchCallback.NULL;
    public void setTouchCallback(TouchCallback callback) {
    	_callback = callback == null ? TouchCallback.NULL : callback;
    }
	
	
//CC_CONSTRUCTOR_ACCESS:
    EventListenerTouchOneByOne() {
    	
    }
    
    boolean init() {
    	return true;	
    }
    
    Array<Touch> _claimedTouches;
    boolean _needSwallow;
}
