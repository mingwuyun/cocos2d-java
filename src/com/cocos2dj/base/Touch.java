package com.cocos2dj.base;

import com.badlogic.gdx.math.Vector2;

/**
 * Touch.java
 * <br>DispatchMode
 * <p>
 * @author Copyright (c) 2017 xu jun
 */
public final class Touch {
	/** 
     * Dispatch mode, how the touches are dispatched.
     */
    public static enum DispatchMode {
        ALL_AT_ONCE, 
        ONE_BY_ONE,  
    };

    public Touch() {}

    /** Returns the current touch location in OpenGL coordinates.
     *
     * @return <b>pool object</b>The current touch location in OpenGL coordinates.
     */
    public Vector2 getLocation()  {
    	return Director.getInstance().convertToGL(_point);
    }
    /** Returns the previous touch location in OpenGL coordinates.
     *
     * @return <b>pool object</b>The previous touch location in OpenGL coordinates.
     */
    public Vector2 getPreviousLocation() {
    	return Director.getInstance().convertToGL(_prevPoint);
    }
    /** Returns the start touch location in OpenGL coordinates.
     *
     * @return The start touch location in OpenGL coordinates.
     */
    public Vector2 getStartLocation() {
    	return Director.getInstance().convertToGL(_startPoint);
    }
    /** Returns the delta of 2 current touches locations in screen coordinates.
     *
     * @return <b>pool object</b> The delta of 2 current touches locations in screen coordinates.
     */
    public Vector2 getDelta() {
    	return stackVec2.set(getLocation()).sub(getPreviousLocation());
    }
    /** Returns the current touch location in screen coordinates.
     *
     * @return The current touch location in screen coordinates.
     */
    public Vector2 getLocationInView() {
    	return _point;
    }
    /** Returns the previous touch location in screen coordinates. 
     *
     * @return The previous touch location in screen coordinates.
     */
    public Vector2 getPreviousLocationInView() {
    	return _prevPoint;
    }
    /** Returns the start touch location in screen coordinates.
     *
     * @return The start touch location in screen coordinates.
     */
    public Vector2 getStartLocationInView() {
    	return _startPoint;
    }
    
    /** Set the touch information. It always used to monitor touch event.
     *
     * @param id A given id
     * @param x A given x coordinate.
     * @param y A given y coordinate.
     */
    public void setTouchInfo(int id, float x, float y)
    {
        _id = id;
        _prevPoint.set(_point);
        _point.x   = x;
        _point.y   = y;
        _curForce = 0.0f;
        _maxForce = 0.0f;
        if (!_startPointCaptured) {
            _startPoint.set(_point);
            _startPointCaptured = true;
            _prevPoint.set(_point);
        }
    }

    /** Set the touch information. It always used to monitor touch event.
     *
     * @param id A given id
     * @param x A given x coordinate.
     * @param y A given y coordinate.
     * @param force Current force for 3d touch.
     * @param maxForce maximum possible force for 3d touch.
     */
    public void setTouchInfo(int id, float x, float y, float force, float maxForce)
    {
        _id = id;
        _prevPoint.set(_point);
        _point.x   = x;
        _point.y   = y;
        _curForce = force;
        _maxForce = maxForce;
        if (!_startPointCaptured)
        {
            _startPoint.set(_point);
            _startPointCaptured = true;
            _prevPoint.set( _point);
        }
    }
    /** Get touch id.
     *
     * @return The id of touch.
     */
    public int getID() {
        return _id;
    }
    
    /** Returns the current touch force for 3d touch.
     *
     * @return The current touch force for 3d touch.
     */
    public float getCurrentForce() {
    	return _curForce;
    }
    /** Returns the maximum touch force for 3d touch.
     *
     * @return The maximum touch force for 3d touch.
     */
    public float getMaxForce() {
    	return _maxForce;
    }

    
    public void clear() {
    	_startPointCaptured = false;
    	_id = 0;
    	_curForce = 0;
    	_maxForce = 0;
    }
    
    private int _id;
    private boolean _startPointCaptured = false;
    private Vector2 _startPoint = new Vector2();
    private Vector2 _point = new Vector2();
    private Vector2 _prevPoint = new Vector2();
    private float _curForce;
    private float _maxForce;
    
    static Vector2 stackVec2 = new Vector2();
    
    
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	sb.append("id = ").append(_id);
    	sb.append(" point = ").append(_point).append(' ');
    	return sb.toString();
    }
}
