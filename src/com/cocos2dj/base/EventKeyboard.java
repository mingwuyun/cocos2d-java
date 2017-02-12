package com.cocos2dj.base;

/**
 * EventKeyboard.java
 * <p>
 * 
 * @author Copyright(c) 2017 xujun
 */
public class EventKeyboard extends Event {

	public EventKeyboard() {
		super(Type.KEYBOARD);
	}

    /** 
    * @param keyCode A given keycode.
    * @param isPressed True if the key is pressed.
    */
	public void init(int keyCode, boolean isPressed) {
		_keyCode = keyCode;
		_isPressed = isPressed;
	}
	
	public int getKeyCode() {return _keyCode;}
	public boolean isPressed() {return _isPressed;}
	
	private int 		_keyCode;
	private boolean 	_isPressed;
}
