package com.cocos2dj.base;

import com.badlogic.gdx.utils.Array;
/**
 * EventListenerTouchAllAtOnce.java
 * <p>
 * 
 * @author Copyright(c) 2017 xujun
 *
 */
public class EventListenerTouchAllAtOnce extends EventListener {

	public static final String LISTENER_ID = "__cc_touch_all_at_once";
	
	public static interface OnTouchesBeganCallback {public void onTouchBegan(Array<Touch> touches, Event event);}
	public static interface OnTouchesMovedCallback {public void onTouchMoved(Array<Touch> touches, Event event);}
	public static interface OnTouchesEndedCallback {public void onTouchEnded(Array<Touch> touches, Event event);}
	public static interface OnTouchesCancelledCallback {public void onTouchCancelled(Array<Touch> touches, Event event);}
	
	OnTouchesBeganCallback _onTouchBegan = TouchesCallback.NULL;
	OnTouchesMovedCallback _onTouchMoved = TouchesCallback.NULL;
	OnTouchesEndedCallback _onTouchEnded = TouchesCallback.NULL;
	OnTouchesCancelledCallback _onTouchCanceled;
	
	public void setOnTouchesBeganCallback(OnTouchesBeganCallback callback) {
		_onTouchBegan = callback == null ? TouchesCallback.NULL : callback;
	}
	public void setOnTouchesMovedCallback(OnTouchesMovedCallback callback) {
		_onTouchMoved = callback == null ? TouchesCallback.NULL : callback;
	}
	public void setOnTouchesEndedCallback(OnTouchesEndedCallback callback) {
		_onTouchEnded = callback == null ? TouchesCallback.NULL : callback;
	}
	public void setOnTouchesCancelledCallback(OnTouchesCancelledCallback callback) {
		_onTouchCanceled = callback;
	}
	
	public static interface TouchesCallback extends OnTouchesBeganCallback, OnTouchesMovedCallback, OnTouchesEndedCallback {
//		public boolean onTouchBegan(Touch touch, Event event);
		public static TouchesCallback NULL = new TouchesCallback() {
			public void onTouchBegan(Array<Touch> touches, Event event) {}
			public void onTouchMoved(Array<Touch> touches, Event event) {}
			public void onTouchEnded(Array<Touch> touches, Event event) {}
//			public void onTouchCancelled(Touch touch, Event event) {}
		};
	}
	
	
	public boolean checkAvailable() {
	    if (_onTouchBegan == null && _onTouchMoved == null
	        && _onTouchEnded == null) {
	        assert false: "Invalid EventListenerTouchAllAtOnce!";
	        return false;
	    }
	    return true;
	}
	
	public void onTouchesBegan(Array<Touch> touches, Event event) {
		_onTouchBegan.onTouchBegan(touches, event);
    }
	public void onTouchesMoved(Array<Touch> touches, Event event) {
		_onTouchMoved.onTouchMoved(touches, event);
	}
	public void onTouchesEnded(Array<Touch> touches, Event event) {
		_onTouchEnded.onTouchEnded(touches, event);
	}
	public void onTouchesCancelled(Array<Touch> touches, Event event) {
		if(_onTouchCanceled != null) {
			_onTouchCanceled.onTouchCancelled(touches, event);
		}
	}

    public void setTouchesCallback(TouchesCallback callback) {
    	setOnTouchesBeganCallback(callback);
    	setOnTouchesMovedCallback(callback);
    	setOnTouchesEndedCallback(callback);
    }
}
