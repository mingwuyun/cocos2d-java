package com.cocos2dj.base;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;


public class Rect extends Rectangle {
	
	private static final long serialVersionUID = -3855101754113979241L;
	private static final Rect _rect = new Rect();
	
	public static Rect getStackInstance() {
		return _rect;
	}
	
	/**
	 * 值传递使用的Rect 返回一个静态Rect
	 */
	public static Rect Get(float x, float y, float w, float h) {
		return (Rect) _rect.set(x, y, w, h);
	}
	
	public Rect() {
		super();
	}
	
	public boolean isZero() {
		return x == 0 && y == 0 && width == 0 && height == 0;
	}
	
    /**
     * @js NA
     */
	public Rect(float x, float y, float width, float height) {
		super(x, y, width, height);
	}
	
    /**
     * @js NA
     * @lua NA
     */
	public Rect(Rect other) {
		super(other);
	}
	
	public Rect(Rectangle other) {
		super(other);
	}
	
	
    /**
     * @js NA
     * @lua NA
     */
	public void setRect(float x, float y, float width, float height) {
		this.set(x, y, width, height);
	}
	
	public boolean containsPoint(float x, float y) {
		return contains(x, y);
	}
	
    /**
     * @js NA
     */
	public boolean containsPoint(Vector2 point) {
		return contains(point);
	}
    /**
     * @js NA
     */
	public boolean intersectsRect(final Rect rect) {
		return contains(rect);
	}
    /**
     * @js NA
     * @lua NA
     */
	public Rect unionWithRect(final Rect rect) {
		Rectangle ret = merge(rect);
		if(ret instanceof Rect) {
			return (Rect) ret;
		} else {
			return new Rect(rect);
		}
	}
    
	public float getMinX() {return x;}
	public float getMinY() {return y;}
	public float getMaxX() {return x + width;}
	public float getMaxY() {return y + height;}
    static final Rect ZERO = new Rect();
}
