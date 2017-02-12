package com.cocos2dj.platform;

import java.nio.FloatBuffer;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.BufferUtils;

import com.cocos2dj.base.Size;
import com.cocos2dj.base.Touch;
import com.cocos2dj.base.EventTouch.EventCode;
import com.cocos2dj.basic.BaseInput;
import com.cocos2dj.basic.Engine;
import com.cocos2dj.macros.CCLog;
import com.cocos2dj.macros.CCMacros;
import com.cocos2dj.renderer.Viewport;
import com.cocos2dj.utils.ObjectPoolBuilder;
import com.cocos2dj.utils.ObjectPoolLinear;
import com.cocos2dj.base.Director;
import com.cocos2dj.base.EventDispatcher;
import com.cocos2dj.base.EventKeyboard;
import com.cocos2dj.base.EventTouch;
import com.cocos2dj.base.Rect;

/**
 * Glview.java
 * <p>
 * <li>屏幕适配相关功能
 * <li>触摸事件分发 (为了适配cocos2dx的架构放到这里)
 * 
 * @author Copyright (c) 2017 xu jun
 */
public class GLView implements InputProcessor {
	
	public GLView() {
		if(Gdx.app.getType() == ApplicationType.Desktop) {
			setFrameSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		} else {
			setFrameSize(Gdx.graphics.getDisplayMode().width, Gdx.graphics.getDisplayMode().height);
		}

if(CCMacros.USE_CC_TOUCH_LISTENER) {
		_eventDispatcher = Director.justInstance().getEventDispatcher();
		BaseInput.instance().addInputProcessor(this);
		createPools();
}
		
		CCLog.engine(TAG, "glview init -> size = " + getFrameSize());
	}

	public static final String TAG = "GLView";
	
    /**
     * Get the frame size of EGL view.
     * In general, it returns the screen size since the EGL view is a fullscreen view.
     */
    public Size getFrameSize() {
    	return _screenSize;
    }

    /**
     * Set the frame size of EGL view.
     */
    public void setFrameSize(float width, float height) {
    	_designResolutionSize.setSize(width, height);
    	_screenSize.setSize(width, height);
    	
    	//默认
    	_resolutionPolicy = ResolutionPolicy.SHOW_ALL;
    	updateDesignResolutionSize();
    }

    /**
     * Get the visible area size of opengl viewport.
     */
    public Size getVisibleSize() {
    	if (_resolutionPolicy == ResolutionPolicy.EXACT_FIT) {
            return getVisibleSize.setSize(_screenSize.width/_scaleX, _screenSize.height/_scaleY);
        } else {
            return getVisibleSize.set(_designResolutionSize);
        }
    }

    /**
     * Get the visible origin point of opengl viewport.
     */
    public Vector2 getVisibleOrigin() {
    	if (_resolutionPolicy == ResolutionPolicy.NO_BORDER) {
            return visibleOrigin.set((_designResolutionSize.width - _screenSize.width/_scaleX)/2, 
                               (_designResolutionSize.height - _screenSize.height/_scaleY)/2);
        } else {
            return visibleOrigin.setZero();
        }
    }

    /**
     * Get the visible rectangle of opengl viewport.
     */
    public Rect getVisibleRect() {
    	Rect ret = poolRect;
        Size s = getVisibleSize();
        Vector2 pos = getVisibleOrigin();
        ret.set(pos.x, pos.y, s.width, s.height);
        return ret;
    }
    
    /**
     * Set the design resolution size.
     * @param width Design resolution width.
     * @param height Design resolution height.
     * @param resolutionPolicy The resolution policy desired, you may choose:
     *                         [1] EXACT_FIT Fill screen by stretch-to-fit: if the design resolution ratio of width to height is different from the screen resolution ratio, your game view will be stretched.
     *                         [2] NO_BORDER Full screen without black border: if the design resolution ratio of width to height is different from the screen resolution ratio, two areas of your game view will be cut.
     *                         [3] SHOW_ALL  Full screen with black border: if the design resolution ratio of width to height is different from the screen resolution ratio, two black borders will be shown.
     */
    public void setDesignResolutionSize(float width, float height, ResolutionPolicy resolutionPolicy) {
    	if (width == 0.0f || height == 0.0f) {
            throw new RuntimeException("width or height is 0.0 ! ");
        }
//    	this._scaleX = scaleX;
//    	this._scaleY = scaleY;
        _designResolutionSize.setSize(width, height);
        _resolutionPolicy = resolutionPolicy;
        
        updateDesignResolutionSize();
    }

    /** Get design resolution size.
     *  Default resolution size is the same as 'getFrameSize'.
     */
    public final Size  getDesignResolutionSize() {
    	return _designResolutionSize;
    }

    /**
     * Set opengl view port rectangle with points.
     */
    public void setViewPortInPoints(float x , float y , float w , float h) {
    	//TODO implements this in director >>> 
//    	experimental::Viewport vp((float)(x * _scaleX + _viewPortRect.origin.x),
//        (float)(y * _scaleY + _viewPortRect.origin.y),
//        (float)(w * _scaleX),
//        (float)(h * _scaleY));
//    Camera::setDefaultViewport(vp);
    	Viewport.setDefaultViewport(
    			(float)(x * _scaleX + _viewPortRect.x),
    	        (float)(y * _scaleY + _viewPortRect.y),
    	        (float)(w * _scaleX),
    	        (float)(h * _scaleY)
    	);
    	System.out.println("ddirector call - > setViewPortInPoints " + Viewport._defaultViewport);
    }

    /**
     * Set Scissor rectangle with points.
     */
    public void setScissorInPoints(float x , float y , float w , float h) {
    	if(!Engine.instance().isGLThread()) {
    		// 不能在非gl线程中访问
    		CCLog.error(TAG, "GLThread error");
    		return;
    	}
    	Gdx.gl.glScissor(
	    	(int)(x * _scaleX + _viewPortRect.x),
	        (int)(y * _scaleY + _viewPortRect.y),
	        (int)(w * _scaleX),
	        (int)(h * _scaleY)
        );
    }

    /**
     * Get whether GL_SCISSOR_TEST is enable
     */
    public boolean isScissorEnabled() {
    	if(!Engine.instance().isGLThread()) {
    		// 不能在非gl线程中访问
    		CCLog.error(TAG, "GLThread error");
    		return false;
    	}
    	return Gdx.gl.glIsEnabled(GL20.GL_SCISSOR_BOX);
    }

    /**
     * Get the current scissor rectangle
     */
    public Rect getScissorRect() {
    	if(!Engine.instance().isGLThread()) {
    		// 不能在非gl线程中访问
    		CCLog.error(TAG, "GLThread error");
    		return null;
    	}
    	FloatBuffer fb = BufferUtils.newFloatBuffer(16); //4 * 4
    	Gdx.gl.glGetFloatv(GL20.GL_SCISSOR_BOX, fb);
    	float[] params = new float[4];
    	fb.get(params);
    	
        float x = (params[0] - _viewPortRect.x) / _scaleX;
        float y = (params[1] - _viewPortRect.y) / _scaleY;
        float w = params[2] / _scaleX;
        float h = params[3] / _scaleY;
        return (Rect) poolRect.set(x, y, w, h);
    }
    
    /**
     * Get the opengl view port rectangle.
     */
    public final Rect getViewPortRect() {
    	return _viewPortRect;
    }

    /**
     * Get scale factor of the horizontal direction.
     */
    public float getScaleX() {
    	return this._scaleX;
    }

    /**
     * Get scale factor of the vertical direction.
     */
    public float getScaleY() {
    	return this._scaleY;
    }

    /** returns the current Resolution policy */
    public ResolutionPolicy getResolutionPolicy() { return _resolutionPolicy; }

    
	protected void updateDesignResolutionSize() {
		if (_screenSize.width > 0 && _screenSize.height > 0
		        && _designResolutionSize.width > 0 && _designResolutionSize.height > 0) {
	        _scaleX = (float)_screenSize.width / _designResolutionSize.width;
	        _scaleY = (float)_screenSize.height / _designResolutionSize.height;
	        
	        System.out.println("policy = " + _resolutionPolicy);
	        if (_resolutionPolicy == ResolutionPolicy.NO_BORDER) {
	            _scaleX = _scaleY = _scaleX > _scaleY ? _scaleX : _scaleY;//MAX(_scaleX, _scaleY);
	        } else if (_resolutionPolicy == ResolutionPolicy.SHOW_ALL) {
	            _scaleX = _scaleY = _scaleX < _scaleY ? _scaleX : _scaleY;//MIN(_scaleX, _scaleY);
	        } else if ( _resolutionPolicy == ResolutionPolicy.FIXED_HEIGHT) {
	            _scaleX = _scaleY;
	            _designResolutionSize.width = MathUtils.ceil(_screenSize.width/_scaleX);
	        } else if ( _resolutionPolicy == ResolutionPolicy.FIXED_WIDTH) {
	            _scaleY = _scaleX;
	            _designResolutionSize.height = MathUtils.ceil(_screenSize.height/_scaleY);
	        }
	        
	        // calculate the rect of viewport
	        float viewPortW = _designResolutionSize.width * _scaleX;
	        float viewPortH = _designResolutionSize.height * _scaleY;
	        
//	        System.out.println("_designResolutionSize = " + _designResolutionSize);
//	        System.out.println(_scaleX + ", " + _scaleY);
	        
//	        System.out.println("screen = " + _screenSize);
//	        System.out.println(viewPortW + ", " + viewPortH);
	        _viewPortRect.setRect((_screenSize.width - viewPortW) / 2, (_screenSize.height - viewPortH) / 2, viewPortW, viewPortH);
	        
//	        System.out.println("updateDesignResolutionSize.viewport " + _viewPortRect);
	        
	        // reset director's member variables to fit visible rect
	        Director director = Director.getInstance();
//	        System.out.println("design = " + _designResolutionSize);
	        director._winSizeInPoints.set(_designResolutionSize);
//	        getDesignResolutionSize()
	        director._isStatusLabelUpdated = true;
	        director.setProjection(director.getProjection());
	        
	        
	        //TODO debug >>>>>>
//	        setViewPortInPoints(_viewPortRect.x, _viewPortRect.y, _viewPortRect.width, _viewPortRect.height);
	        
	        
	        
	        //show info
	        StringBuffer sb = new StringBuffer();
	        sb.append("glviewinfo >> \n resolutionPolicy = ").append(_resolutionPolicy).append('\n');
	        sb.append(" screen = ").append(_screenSize).append('\n');
	        sb.append(" designResolution = ").append(_designResolutionSize).append('\n');
	        sb.append(" viewPortRect = ").append(_viewPortRect).append('\n');
	        sb.append(" visibleRect = ").append(getVisibleRect()).append('\n');
	        sb.append(" scale = {").append(_scaleX).append(", ").append(_scaleY).append("}\n");
	        CCLog.engine(TAG, sb.toString());
		}
	}
	
	
	static final Rect poolRect = new Rect();
	final Vector2 visibleOrigin = new Vector2();
	final Size getVisibleSize = new Size();
//    void handleTouchesOfEndOrCancel(EventTouch::EventCode eventCode, int num, intptr_t ids[], float xs[], float ys[]);
    // real screen size
    Size _screenSize = new Size();
    // resolution size, it is the size appropriate for the app resources.
    Size _designResolutionSize = new Size();
    // the view port size
    Rect _viewPortRect = new Rect();
    // the view name
    String _viewName = "GlView";

    float _scaleX = 1f;
    float _scaleY = 1f;
    ResolutionPolicy _resolutionPolicy;

    
    ///////////////////////////////////
    //TODO touch events
//  virtual void setViewName(const std::string& viewname);
//  const std::string& getViewName() const;

//  /** Touch events are handled by default; if you want to customize your handlers, please override these functions: */
//  virtual void handleTouchesBegin(int num, intptr_t ids[], float xs[], float ys[]);
//  virtual void handleTouchesMove(int num, intptr_t ids[], float xs[], float ys[]);
//  virtual void handleTouchesEnd(int num, intptr_t ids[], float xs[], float ys[]);
//  virtual void handleTouchesCancel(int num, intptr_t ids[], float xs[], float ys[]);
    private ObjectPoolLinear<EventTouch> 				stackTouchEvent;
    private ObjectPoolLinear<EventKeyboard>				stackKeyboardEvent;
    private ObjectPoolLinear<Touch>						stackTouch;
    EventDispatcher	_eventDispatcher;
    
    public void removeSelf() {
    	BaseInput.instance().removeInputProcessor(this);
    	_eventDispatcher = null;
    }
    
    final void createPools() {
    	stackTouchEvent = ObjectPoolBuilder.<EventTouch>startBuilder().setInitCount(8).setAddCount(2).setClass(EventTouch.class).create();
    	stackKeyboardEvent = ObjectPoolBuilder.<EventKeyboard>startBuilder().setInitCount(8).setAddCount(2).setClass(EventKeyboard.class).create();
    	stackTouch = ObjectPoolBuilder.<Touch>startBuilder().setInitCount(8).setAddCount(2).setClass(Touch.class).create();
    }
    
	@Override
	public boolean keyDown(int keycode) {
if(CCMacros.USE_CC_TOUCH_LISTENER) {
		EventKeyboard event = stackKeyboardEvent.pop();
		event.init(keycode, true);
		_eventDispatcher.dispatchEvent(event);
		stackKeyboardEvent.push(event);
}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
if(CCMacros.USE_CC_TOUCH_LISTENER) {
		EventKeyboard event = stackKeyboardEvent.pop();
		event.init(keycode, false);
		_eventDispatcher.dispatchEvent(event);
		stackKeyboardEvent.push(event);
}
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
if(CCMacros.USE_CC_TOUCH_LISTENER) {
		EventTouch evectTouch = stackTouchEvent.pop();
		Touch touch = stackTouch.pop();
		
		touch.setTouchInfo(button, screenX, screenY);
		evectTouch._addTouch(touch);
		evectTouch.setEventCode(EventCode.BEGAN);
		_eventDispatcher.dispatchEvent(evectTouch);
		
		stackTouch.push(touch);
		evectTouch._clearTouch();
		stackTouchEvent.push(evectTouch);
}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
if(CCMacros.USE_CC_TOUCH_LISTENER) {
		EventTouch evectTouch = stackTouchEvent.pop();
		Touch touch = stackTouch.pop();
		
		touch.setTouchInfo(button, screenX, screenY);
		evectTouch._addTouch(touch);
		evectTouch.setEventCode(EventCode.ENDED);
		_eventDispatcher.dispatchEvent(evectTouch);
		
		stackTouch.push(touch);
		evectTouch._clearTouch();
		stackTouchEvent.push(evectTouch);
}
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
if(CCMacros.USE_CC_TOUCH_LISTENER) {
			EventTouch evectTouch = stackTouchEvent.pop();
			Touch touch = stackTouch.pop();
			
			touch.setTouchInfo(pointer, screenX, screenY);
			evectTouch._addTouch(touch);
			evectTouch.setEventCode(EventCode.MOVED);
			_eventDispatcher.dispatchEvent(evectTouch);
			
			stackTouch.push(touch);
			stackTouchEvent.push(evectTouch);
}		
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
};