package com.cocos2dj.base;

import com.badlogic.gdx.utils.Array;

public class EventListenerTouchAllAtOnce extends EventListener {

	public static final String LISTENER_ID = "__cc_touch_all_at_once";
	
	public interface TouchesCallback {
		public void onTouchBegan(Array<Touch> touches, Event event);
		public void onTouchMoved(Array<Touch> touches, Event event);
		public void onTouchEnded(Array<Touch> touches, Event event);
		public void onTouchCancelled(Array<Touch> touches, Event event);
		
		public static TouchesCallback NULL = new TouchesCallback() {
			public void onTouchBegan(Array<Touch> touch, Event event) {}
			public void onTouchMoved(Array<Touch> touch, Event event) {}
			public void onTouchEnded(Array<Touch> touch, Event event) {}
			public void onTouchCancelled(Array<Touch> touch, Event event) {}
		};
	}
	
	public void onTouchBegan(Array<Touch> touches, Event event) {
    	_callback.onTouchBegan(touches, event);
    }
	public void onTouchMoved(Array<Touch> touches, Event event) {
		_callback.onTouchMoved(touches, event);
	}
	public void onTouchEnded(Array<Touch> touches, Event event) {
		_callback.onTouchEnded(touches, event);
	}
	public void onTouchCancelled(Array<Touch> touches, Event event) {
		_callback.onTouchCancelled(touches, event);
	}

    private TouchesCallback _callback = TouchesCallback.NULL;
    public void setTouchesCallback(TouchesCallback callback) {
    	_callback = callback == null ? TouchesCallback.NULL : callback;
    }
}
