package com.cocos2dj.base;

import com.cocos2dj.protocol.IFunctionOneArg;

/**
 * EventListenerKeyboard.java
 * <p>
 * 
 * @author Copyright(c) 2017 xujun
 */
public class EventListenerKeyboard extends EventListener implements IFunctionOneArg<Event> {

	public static final String LISTENER_ID = "__cc_keyboard";
	
	public static interface OnKeyPressedCallback {public void onKeyPressed(int keycode, Event e);}
	public static interface OnKeyReleassedCallback {public void onKeyReleassed(int keycode, Event e);}
	
	public static interface KeyCallback extends OnKeyPressedCallback, OnKeyReleassedCallback {
		public static final KeyCallback NULL = new KeyCallback() {
			public void onKeyPressed(int keycode, Event e) {}
			public void onKeyReleassed(int keycode, Event e) {}
		};
	}
	
	OnKeyPressedCallback	_onKeyPressedCallback = KeyCallback.NULL;
	OnKeyReleassedCallback	_onKeyReleassedCallback = KeyCallback.NULL;
	
	public void setOnKeyPressedCallback(OnKeyPressedCallback callback) {
		_onKeyPressedCallback = callback == null ? KeyCallback.NULL : callback;
	}
	public void setOnKeyReleassedCallback(OnKeyReleassedCallback callback) {
		_onKeyReleassedCallback = callback == null ? KeyCallback.NULL : callback;
	}
	
	public void setKeyCallback(KeyCallback callback) {
		setOnKeyPressedCallback(callback);
		setOnKeyReleassedCallback(callback);
	}
	
	
	public static EventListenerKeyboard create() {
		EventListenerKeyboard ret = new EventListenerKeyboard();
		ret.init();
		return ret;
	}
	
	/**use {@link #create()} */
	public EventListenerKeyboard() {}
	
	public boolean init() {
		return init(Type.KEYBOARD, LISTENER_ID, this);
	}
	
	
	@Override
	public final void callback(Event t) {
		EventKeyboard e = (EventKeyboard) t;
		if(e.isPressed()) {
			_onKeyPressedCallback.onKeyPressed(e.getKeyCode(), e);
		} else {
			_onKeyReleassedCallback.onKeyReleassed(e.getKeyCode(), e);
		}
	}
}
