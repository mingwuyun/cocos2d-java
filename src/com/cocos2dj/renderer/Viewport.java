package com.cocos2dj.renderer;

public final class Viewport {
	
	public static Viewport _defaultViewport = new Viewport();
	
	public static final void setDefaultViewport(float left, float bottom, float width, float height) {
		_defaultViewport._left = left;
		_defaultViewport._bottom = bottom;
		_defaultViewport._width = width;
		_defaultViewport._height = height;
	}
	
	public Viewport(float left, float bottom, float width, float height) {
		this._left = left;
		this._bottom = bottom;
		this._width = width;
		this._height = height;
	}
	
	public Viewport() {
		
	}
	
	public void set(Viewport other) {
		this._left = other._left;
		this._bottom = other._bottom;
		this._width = other._width;
		this._height = other._height;
	}
	
	public float _left;
    public float _bottom;
    public float _width;
    public float _height;
	 
    
    public String toString() {
    	return "left = " + _left + " bottom = " + _bottom + " width = " + _width + " height = " + _height;
    }
}