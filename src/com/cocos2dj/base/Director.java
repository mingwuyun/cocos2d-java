package com.cocos2dj.base;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.cocos2dj.basic.BaseCoreTimer;
import com.cocos2dj.basic.Engine;
import com.cocos2dj.basic.IDisposable;
import com.cocos2dj.macros.CCLog;
import com.cocos2dj.platform.GLView;
import com.cocos2dj.protocol.IFunctionOneArgRet;
import com.cocos2dj.protocol.INode;
import com.cocos2dj.protocol.IScene;
import com.cocos2dj.protocol.ITransitionScene;
import com.cocos2dj.renderer.Renderer;
import com.cocos2dj.renderer.TextureCache;

/**
 * Director.java
 * <br>MATRIX_STACK_TYPE
 * 
@brief Class that creates and handles the main Window and manages how
and when to execute the Scenes.
 
 The Director is also responsible for:
  - initializing the OpenGL context
  - setting the OpenGL pixel format (default on is RGB565)
  - setting the OpenGL buffer depth (default one is 0-bit)
  - setting the projection (default one is 3D)
  - setting the orientation (default one is Portrait)
 
 The Director also sets the default OpenGL context:
  - GL_TEXTURE_2D is enabled
  - GL_VERTEX_ARRAY is enabled
  - GL_COLOR_ARRAY is enabled
  - GL_TEXTURE_COORD_ARRAY is enabled
  
 * @author Copyright (c) 2017 xu jun
 */
public class Director {
	
	public static final String TAG = "Director";
	
	//class>>
	public static enum MATRIX_STACK_TYPE {
	    MATRIX_STACK_MODELVIEW,
	    MATRIX_STACK_PROJECTION,
	    MATRIX_STACK_TEXTURE
	};
	 /** @typedef ccDirectorProjection
    Possible OpenGL projections used by director
    */
   public static enum Projection {
       /// sets a 2D projection (orthogonal projection)
       _2D,
       /// sets a 3D projection with a fovy=60, znear=0.5f and zfar=1500.
       _3D,
       /// it calls "updateProjection" on the projection delegate.
       CUSTOM,
       /// Default projection is 3D projection
       DEFAULT,
   };
	//class<<
	

	public static final String EVENT_PROJECTION_CHANGED = "director_projection_changed";
	public static final String EVENT_AFTER_DRAW = "director_after_draw";
	public static final String EVENT_AFTER_VISIT = "director_after_visit";
	public static final String EVENT_BEFORE_UPDATE = "director_before_update";
	public static final String EVENT_AFTER_UPDATE = "director_after_update";
	public static final String EVENT_RESET = "director_reset";
	public static final String EVENT_CHANGE_SCENE = "direct_change_scene";
	
	private static Director _instance;
	public static final Director getInstance() {
		if(_instance == null) {
			_instance = new Director();
			_instance.init();
			Engine.registerDisposable(new IDisposable() {
				@Override
				public void dispose() {
					_instance = null;
				}
			});
		}
		return _instance;
	}


	public static final Director justInstance() {
		return _instance;
	}
	 /** returns a shared instance of the director */
	public static final Director instance() {
		return getInstance();
	}
	
	//ctor>>
	private Director() {
		this._isStatusLabelUpdated = true;
	}
	//ctor<<
	
	
	//fields>>
	private Array<Matrix4> _modelViewMatrixStack = new Array<Matrix4>(2);
	private Array<Matrix4> _projectionMatrixStack = new Array<Matrix4>(2);
	private Array<Matrix4> _textureMatrixStack = new Array<Matrix4>(2);
	//fields<<
	
	
	//////////////////////////////////////
	//TODO Matrix 矩阵只能读取不能修改
	/*
	init						[x0(t)]
	push load x1				[x0, x1(t)]
		push load x2			[x0, x1, x2(t)]
		pop						[x0, x1(t)]
		
		push load x3			[x0, x1, x3(t)]
			push load x4		[x0, x1, x3, x4(t)]
			pop					[x0, x1, x3(t)]
		pop						[x0, x1(t)]
	pop							[x0(t)]
	*/
	final void showMatrixStackState() {
		System.out.println("_modelViewMatrixStack len : " + _modelViewMatrixStack.size);
		System.out.println("_projectionMatrixStack len : " + _projectionMatrixStack.size);
		System.out.println("_textureMatrixStack len : " + _textureMatrixStack.size);
	}
	
	final void initMatrixStack() {
		_modelViewMatrixStack.clear();
		_projectionMatrixStack.clear();
		_textureMatrixStack.clear();
		
		_modelViewMatrixStack.add(IdentityM4);
		_projectionMatrixStack.add(IdentityM4);
		_textureMatrixStack.add(IdentityM4);
		
//		showMatrixStackState();
	}
	
	public final void pushMatrix(MATRIX_STACK_TYPE type, final Matrix4 mat) {
		switch(type) {
		case MATRIX_STACK_MODELVIEW: _modelViewMatrixStack.add(mat);break;
		case MATRIX_STACK_PROJECTION: _projectionMatrixStack.add(mat); break;
		case MATRIX_STACK_TEXTURE: _modelViewMatrixStack.add(mat); break;
		}
		
//		showMatrixStackState();
	}
	
	public void popMatrix(MATRIX_STACK_TYPE type) {
		switch(type) {
		case MATRIX_STACK_MODELVIEW: _modelViewMatrixStack.pop();  break;
		case MATRIX_STACK_PROJECTION: _projectionMatrixStack.pop(); break;
		case MATRIX_STACK_TEXTURE: _textureMatrixStack.pop(); break;
		}
		
//		showMatrixStackState();
	}
	
	public final void loadMatrix(MATRIX_STACK_TYPE type, final Matrix4 mat) {
		popMatrix(type);
		pushMatrix(type, mat);
	}
	
	public void loadIdentityMatrix(MATRIX_STACK_TYPE type) {
		popMatrix(type);
		pushMatrix(type, IdentityM4);
	}
	
	/**获取当前矩阵 矩阵不可修改，只能读取 */
	public final Matrix4 getMatrix(MATRIX_STACK_TYPE type) {
		switch(type) {
		case MATRIX_STACK_MODELVIEW: return _modelViewMatrixStack.peek();
		case MATRIX_STACK_PROJECTION:  return _projectionMatrixStack.peek();
		case MATRIX_STACK_TEXTURE: return _textureMatrixStack.peek();
		}
		return null;
	}
	
	public void resetMatrixStack() {
		initMatrixStack();
	}
	
	  
	public boolean init() {
		
		_runningScene = null;
		_nextScene = null;
		_notificationNode = null;
		
		_scheduler = new Scheduler();
		_actionManager = new ActionManager();
		_scheduler.scheduleUpdate(_actionManager, Scheduler.PRIORITY_SYSTEM, false);
//		_scheduler.scheduleUpdate(_actionManager, 100, false);
		
		_scenesStack = new Array<>();
		
		_renderer = new Renderer();
		
		
		_eventDispatcher = new EventDispatcher();
		_eventAfterDraw = new EventCustom(EVENT_AFTER_DRAW);
		_eventAfterDraw.setUserData(this);
	    _eventAfterVisit = new EventCustom(EVENT_AFTER_VISIT);
	    _eventAfterVisit.setUserData(this);
	    _eventBeforeUpdate = new  EventCustom(EVENT_BEFORE_UPDATE);
	    _eventBeforeUpdate.setUserData(this);
	    _eventAfterUpdate = new  EventCustom(EVENT_AFTER_UPDATE);
	    _eventAfterUpdate.setUserData(this);
	    _eventProjectionChanged = new  EventCustom(EVENT_PROJECTION_CHANGED);
	    _eventProjectionChanged.setUserData(this);
	    _eventResetDirector = new  EventCustom(EVENT_RESET);
	    
	    _eventChangeScene = new EventCustom(EVENT_CHANGE_SCENE);
	    _eventChangeScene.setUserData(this);
		
		
		initMatrixStack();
		initTextureCache();
		
		setDefaultValues();
		
		GLView view = new GLView();
		this.setOpenGLView(view);
		
		return false;
	}

	// attribute
	/** Get current running Scene. Director can only run one Scene at a time */
	public IScene getRunningScene() {return _runningScene;}
    /** Get the FPS value */
    public double getAnimationInterval() { return _animationInterval; }
    /** Set the FPS value. */
    public void setAnimationInterval(double interval) { 
    	
    }

    /** Whether or not to display the FPS on the bottom-left corner */
    public boolean isDisplayStats() { return _displayStats; }
    /** Display the FPS on the bottom-left corner */
    public void setDisplayStats(boolean displayStats) { _displayStats = displayStats; }
    
    /** seconds per frame */
    public float getSecondsPerFrame() { return _secondsPerFrame; }

    /** Get the GLView, where everything is rendered
    * @js NA
    * @lua NA
    */
    public GLView getOpenGLView() { return _openGLView; }
    public void setOpenGLView(GLView openGLView) {
    	if(_openGLView != openGLView) {
    		if(_openGLView != null) {
    			_openGLView.removeSelf();	//clear input Listener
    		}
    		
    		_openGLView = openGLView;
    		
    		// set size
//            _winSizeInPoints.set(_openGLView.getDesignResolutionSize());
            _isStatusLabelUpdated = true;

            if (_openGLView != null) {
                setGLDefaultValues();
            }
//            _renderer.initGLView();
//            CHECK_GL_ERROR_DEBUG();
            
            if (_eventDispatcher != null) {
                _eventDispatcher.setEnabled(true);
            }
            
//            _defaultFBO = experimental::FrameBuffer::getOrCreateDefaultFBO(_openGLView);
            
//    		_openGLView.setDesignResolutionSize(_openGLView.getDesignResolutionSize().width, _openGLView.getDesignResolutionSize().width, ResolutionPolicy.EXACT_FIT);
//    		_winSizeInPoints = _openGLView.getDesignResolutionSize();
//    		_renderer.
    	}
    }
    
    public TextureCache getTextureCache() {
    	return _textureCache;
    }

    public boolean isNextDeltaTimeZero() { return _nextDeltaTimeZero; }
    public void setNextDeltaTimeZero(boolean nextDeltaTimeZero) {
    	
    }

    /** Whether or not the Director is paused */
    public boolean isPaused() { return _paused; }

    /** How many frames were called since the director started */
    public long getTotalFrames() { return _totalFrames; }
    public Projection getProjection() { return _projection; }
    /** 
    * Sets an OpenGL projection
    */
    public void setProjection(Projection projection) {
    	Size size = _winSizeInPoints;
//    	System.out.println("size setP = " + _winSizeInPoints);
    	setViewport();
    	switch(projection) {
    	case _2D:
    		Matrix4 orthoMatrix = new Matrix4();
    		orthoMatrix.setToOrtho(0, size.width, 0, size.height, -1024, 1024);
//            Matrix4::createOrthographicOffCenter(0, size.width, 0, size.height, -1024, 1024, &orthoMatrix);
            loadMatrix(MATRIX_STACK_TYPE.MATRIX_STACK_PROJECTION, orthoMatrix);
            loadIdentityMatrix(MATRIX_STACK_TYPE.MATRIX_STACK_MODELVIEW);
    		break;
    	case _3D:
//    		 float zeye = this.getZEye();
//    		 Matrix4 matrixPerspective = new Matrix4(), matrixLookup = new Matrix4();
    		 
             // issue #1334
//    		 matrixPerspective.setToProjection(near, far, fovy, aspectRatio)
//             Mat4::createPerspective(60, (GLfloat)size.width/size.height, 10, zeye+size.height/2, &matrixPerspective);
//
//             Vec3 eye(size.width/2, size.height/2, zeye), center(size.width/2, size.height/2, 0.0f), up(0.0f, 1.0f, 0.0f);
//             Mat4::createLookAt(eye, center, up, &matrixLookup);
//             Mat4 proj3d = matrixPerspective * matrixLookup;
//
//             loadMatrix(MATRIX_STACK_TYPE::MATRIX_STACK_PROJECTION, proj3d);
//             loadIdentityMatrix(MATRIX_STACK_TYPE::MATRIX_STACK_MODELVIEW);
    		break;
    	case CUSTOM:
    		 // Projection Delegate is no longer needed
            // since the event "PROJECTION CHANGED" is emitted
    		break;
    	case DEFAULT:
    		CCLog.engine(TAG, "cocos2d: Director: unrecognized projection");
    		break;
    	}
    	
    	_projection = projection;
//    	Renderer.setProjectionMatrixDirty();
    	
    	_eventDispatcher.dispatchEvent(_eventProjectionChanged);
    }
    
    /** Sets the glViewport*/
    public void setViewport() {
    	if (_openGLView != null){
            _openGLView.setViewPortInPoints(0, 0, _winSizeInPoints.width, _winSizeInPoints.height);
        }
    }

    /** How many frames were called since the director started */
    
    
    /** Whether or not the replaced scene will receive the cleanup message.
     If the new scene is pushed, then the old scene won't receive the "cleanup" message.
     If the new scene replaces the old one, the it will receive the "cleanup" message.
     @since v0.99.0
     */
    public boolean isSendCleanupToScene() { return _sendCleanupToScene; }

    /** This object will be visited after the main scene is visited.
     This object MUST implement the "visit" selector.
     Useful to hook a notification object, like Notifications (http://github.com/manucorporat/CCNotifications)
     @since v0.99.5
     */
    public INode getNotificationNode() { return _notificationNode; }
    public void setNotificationNode(INode node) {
    	
    }
    
    
    // window size

    /** returns the size of the OpenGL view in points.
    */
    public Size getWinSize() {
    
    	return _winSizeInPoints;
    }

    /** returns the size of the OpenGL view in pixels.
    */
    public Size getWinSizeInPixels() {
    	
    	return null;
    }
    
    /** returns visible size of the OpenGL view in points.
     *  the value is equal to getWinSize if don't invoke
     *  GLView::setDesignResolutionSize()
     */
    public Size getVisibleSize() {
    	if (_openGLView != null) {
            return _openGLView.getVisibleSize();
        } else {
            return new Size();
        }
    }
    
    /** returns visible origin of the OpenGL view in points.
     */
    public Vector2 getVisibleOrigin() {
    	if (_openGLView != null) {
            return _openGLView.getVisibleOrigin();
        } else {
            return new Vector2();
        }
    }

    
    /** converts a UIKit coordinate to an OpenGL coordinate
     Useful to convert (multi) touch coordinates to the current layout (portrait or landscape)
     @return <b>pool object</b>
     */
    public Vector2 convertToGL(Vector2 point) {
    	Size fSize = _openGLView.getFrameSize();
    	Size glSize = _openGLView.getDesignResolutionSize();
    	float x = point.x * glSize.width / fSize.width;
    	float y = (fSize.height - point.y) * glSize.height / fSize.height;
    	return stackVec2_1.set(x, y);
    	
//    	Matrix4 transform = stackM4_1;
//    	GLToClipTransform(transform);
////    	float zClip = transform.val[Matrix4.M32] / transform.val[Matrix4.M33];
//    	Matrix4 transformInv = transform.inv();
//
//    	System.out.println("point = " + point);
//    	Size glSize = _openGLView.getDesignResolutionSize();
//    	
////    	System.out.println(glSize);
//    	
//    	Vector3 clipCoord = stackVec3_1.set(2f * point.x/glSize.width - 1f, 1f - 2f * point.y/glSize.height, 1);
//    	Vector3 glCoord = clipCoord.mul(transformInv);
//    	System.out.println(transformInv);
//    	return stackVec2_1.set(glCoord.x, glCoord.y);
    }

    /** converts an OpenGL coordinate to a UIKit coordinate
     Useful to convert node points to window points for calls such as glScissor
     @return <b>pool object</b>
     */
    public Vector2 convertToUI(Vector2 point) {
    	Size fSize = _openGLView.getFrameSize();
    	Size glSize = _openGLView.getDesignResolutionSize();
    	float x = point.x * glSize.width / fSize.width;
    	float y = point.y * glSize.height / fSize.height;
    	return stackVec2_1.set(x, y);
//    	GLToClipTransform(transform);
//    	float zClip = transform.val[Matrix4.M32] / transform.val[Matrix4.M33];
//    	Matrix4 transformInv = transform.inv();
//
//    	Size glSize = _openGLView.getDesignResolutionSize();
//    	Vector3 clipCoord = stackVec3_1.set(0, 0, zClip);
//    	return null;
    }
   
    /// XXX: missing description 
    public float getZEye()  {
    	
    	return 0;
    }

    //////////////////////////////////////
    //TODO Scene Management

    /** Enters the Director's main loop with the given Scene.
     * Call it to run only your FIRST scene.
     * Don't call it if there is already a running scene.
     *
     * It will call pushScene: and then it will call startAnimation
     */
    public void runWithScene(IScene scene) {
    	assert scene != null : "This command can only be used to start the Director. There is already a scene present.";
    	assert _runningScene == null : "_runningScene should be null";
    	
    	pushScene(scene);
    	startAnimation();
    }

    /** Suspends the execution of the running scene, pushing it on the stack of suspended scenes.
     * The new scene will be executed.
     * Try to avoid big stacks of pushed scenes to reduce memory allocation. 
     * ONLY call it if there is a running scene.
     */
    public void pushScene(IScene scene) {
    	assert scene != null : "the scene should not null";
    	
    	_sendCleanupToScene = false;
    	
    	_scenesStack.add(scene);
    	_nextScene = scene;
    }

    /** Pops out a scene from the stack.
     * This scene will replace the running one.
     * The running scene will be deleted. If there are no more scenes in the stack the execution is terminated.
     * ONLY call it if there is a running scene.
     */
    public void popScene() {
    	assert _runningScene != null : "running scene should not null";
    	
    	_scenesStack.pop();
    	int c = _scenesStack.size;
    	
    	if (c == 0) {
    		end();
    	} else {
    		_sendCleanupToScene = true;
    		_nextScene = _scenesStack.get(c - 1);
    	}
    }
    
    public void popSceneWithTransition(IFunctionOneArgRet<IScene, IScene> transitionFunc) {
    	
    }

    /** Pops out all scenes from the stack until the root scene in the queue.
     * This scene will replace the running one.
     * Internally it will call `popToSceneStackLevel(1)`
     */
    public void popToRootScene() {
    	
    }

    /** Pops out all scenes from the stack until it reaches `level`.
     If level is 0, it will end the director.
     If level is 1, it will pop all scenes until it reaches to root scene.
     If level is <= than the current stack level, it won't do anything.
     */
 	public void popToSceneStackLevel(int level) {
 		
 	}

    /** Replaces the running scene with a new one. The running scene is terminated.
     * ONLY call it if there is a running scene.
     */
    public void replaceScene(IScene scene) {
    	assert scene != null : "the scene should not be null";
    	
    	if(_runningScene == null) {
    		runWithScene(scene);
    		return;
    	}
    	
    	if(scene == _nextScene) {
    		return;
    	}
    	
    	if(_nextScene != null) {
    		if(_nextScene.isRunning()) {
    			_nextScene.onExit();
    		}
    		_nextScene.cleanup();
    		_nextScene = null;
    	}
    	
    	int index = _scenesStack.size - 1;
    	_sendCleanupToScene = true;
    	_scenesStack.set(index, scene);
    	_nextScene = scene;
    }

    /** Ends the execution, releases the running scene.
     It doesn't remove the OpenGL view from its parent. You have to do it manually.
     * @lua endToLua
     */
    public void end() {
    	
    }

    /** Pauses the running scene.
     The running scene will be _drawed_ but all scheduled timers will be paused
     While paused, the draw rate will be 4 FPS to reduce CPU consumption
     */
    public void pause() {
    	
    }

    /** Resumes the paused scene
     The scheduled timers will be activated again.
     The "delta time" will be 0 (as if the game wasn't paused)
     */
    public void resume() {
    	
    }

    /** Stops the animation. Nothing will be drawn. The main loop won't be triggered anymore.
     If you don't want to pause your animation call [pause] instead.
     */
    public void stopAnimation() {
    	
    }

    /** The main loop is triggered again.
     Call this function only if [stopAnimation] was called earlier
     @warning Don't call this function to start the main loop. To run the main loop call runWithScene
     */
    public void startAnimation() {
    	
    }
    
    final void setNextScene() {
    	boolean runningIsTranstion = false;
    	boolean newIsTransition = false;
    	if(_runningScene instanceof ITransitionScene) {
    		runningIsTranstion = true;
    	}
    	if(_nextScene instanceof ITransitionScene) {
    		newIsTransition = true;
    	}
    	
    	if(!newIsTransition) {
    		if(_runningScene != null) {
    			_runningScene.onExitTransitionDidStart();
    			_runningScene.onExit();
    		}
    		
    		if(_sendCleanupToScene && _runningScene != null) {
    			_runningScene.cleanup();
    		}
    	}
    	
//    	if(_runningScene != null) {
//    		_runningScene = null;
//    	}
    	// 放入最后一个场景(nullable)
    	_eventChangeScene.setUserData(_runningScene);
    	
    	_runningScene = _nextScene;
    	_nextScene = null;
    	
    	if(!runningIsTranstion && _runningScene != null) {
    		_eventDispatcher.dispatchEvent(_eventChangeScene);
    		
    		_runningScene.onEnter();
    		_runningScene.onEnterTransitionDidFinish();
    	}
    	
    	// 清除引用
    	_eventChangeScene.setUserData(null);
    }
    
    //////////////////////////////////////
    

    
    // Memory Helper

    /** Removes all cocos2d cached data.
     It will purge the TextureCache, SpriteFrameCache, LabelBMFont cache
     @since v0.99.3
     */
    public void purgeCachedData() {
    	
    }

	/** sets the default values based on the Configuration info */
    public void setDefaultValues() {
    	
    }

    // OpenGL Helper

    /** sets the OpenGL default values */
    public void setGLDefaultValues() {
    	
    }

    /** enables/disables OpenGL alpha blending */
    public void setAlphaBlending(boolean on) {
    	
    }

    /** enables/disables OpenGL depth test */
   public void setDepthTest(boolean on) {
	   
   }

   
   
   
   

    /** The size in pixels of the surface. It could be different than the screen size.
    High-res devices might have a higher surface size than the screen size.
    Only available when compiled using SDK >= 4.0.
    @since v0.99.4
    */
    public void setContentScaleFactor(float scaleFactor) {
    	
    }
    public float getContentScaleFactor()  { return _contentScaleFactor; }

    /** Gets the Scheduler associated with this director
     @since v2.0
     */
    public Scheduler getScheduler()  { return _scheduler; }
	
	
	
    /** Sets the Scheduler associated with this director
     @since v2.0
     */
    public void setScheduler(Scheduler scheduler) {
		if(scheduler != this._scheduler) {
			this._scheduler.release();
		}
		this._scheduler = scheduler;
	}
    

    /** Gets the ActionManager associated with this director
     @since v2.0
     */
    public ActionManager getActionManager()  { return _actionManager; }
    
    /** Sets the ActionManager associated with this director
     @since v2.0
     */
    public void setActionManager(ActionManager actionManager) {
    	
    }
    
    /** Gets the EventDispatcher associated with this director 
     @since v3.0
     */
    public EventDispatcher getEventDispatcher()  { return _eventDispatcher; }
    
    /** Sets the EventDispatcher associated with this director 
     @since v3.0
     */
    public void setEventDispatcher(EventDispatcher dispatcher) {
    	
    }

    /** Returns the Renderer
     @since v3.0
     */
    public Renderer getRenderer()  { return _renderer; }

    /** Returns the Console 
     @since v3.0
     */
//#if  (CC_TARGET_PLATFORM != CC_PLATFORM_WINRT)
//    Console* getConsole()  { return _console; }
//#endif

    /* Gets delta time since last tick to main loop */
	public float getDeltaTime() {
		return Gdx.graphics.getDeltaTime();
	}
    
    /**
     *  get Frame Rate
     */
    float getFrameRate()  { return _frameRate; }

    protected void purgeDirector() {
    	
    }
    
    protected boolean _purgeDirectorInNextLoop; // this flag will be set to true in end()
    
    
    
    final void showStats() {
    	
    }
    
    final void createStatsLabel() {
    	
    }
    final void calculateMPF() {
    	
    }
    
    final void getFPSImageData(String[] info, int length) {
    	
    }
    
    /** calculates delta time since last time it was called */    
    final void calculateDeltaTime() {
    	
    }

    //textureCache creation or release
    final void initTextureCache() {
    	_textureCache = new TextureCache();
    }
    
    final void destroyTextureCache() {
    	
    }

    
    
	///////////////////////////////////////////
	//TODO mainLoop 
	public final void mainLoop(int deltaMS) {
		if(!_paused) {
			_eventDispatcher.dispatchEvent(_eventBeforeUpdate);
		}
		//next in engine call scheduler.update
	}
	
	/**清除渲染器状态*/
	public final void clearRendererState() {
		_renderer.clear();
	}
	
	/** Draw the scene.
	This method is called every frame. Don't call it manually.
	*/
	public final void drawScene() {
		if(!_paused) {
			_eventDispatcher.dispatchEvent(_eventAfterUpdate);
		}
//		experimental::FrameBuffer::clearAllFBOs();
		
	    /* to avoid flickr, nextScene MUST be here: after tick and before draw.
	     * FIXME: Which bug is this one. It seems that it can't be reproduced with v0.9
	     */
		if(_nextScene != null) {
			setNextScene();
		}
		
//		pushMatrix(MATRIX_STACK_TYPE.MATRIX_STACK_MODELVIEW);
		//更新
		_scheduler.update(BaseCoreTimer.delta);
		
		if (_runningScene != null) {
	        //clear draw stats
	        _renderer.clearDrawStats();
	        
	        //render the scene
	        _runningScene.render(_renderer, IdentityM4, null);
//	        _openGLView->renderScene(_runningScene , _renderer);
//	        _eventDispatcher->dispatchEvent(_eventAfterVisit);
	    }

	    // draw the notifications node
	    if (_notificationNode != null) {
	        _notificationNode.visit(_renderer, IdentityM4, 0);
	    }

	    if (_displayStats) {
	        showStats();
	    }
	    
	    _renderer.render();

//	    _eventDispatcher.dispatchEvent(_eventAfterDraw);
	    
//	    popMatrix(MATRIX_STACK_TYPE.MATRIX_STACK_MODELVIEW);
	    _totalFrames++;

	    if (_displayStats) {
	    	calculateMPF();
	    }
	}
	
	////////////////////////////////////
	
	public final Matrix4 IdentityM4 = new Matrix4();

    /** Scheduler associated with this director
     @since v2.0
     */
    Scheduler _scheduler;
    
    /** ActionManager associated with this director
     @since v2.0
     */
    ActionManager _actionManager;
    
    /** EventDispatcher associated with this director
     @since v3.0
     */
    EventDispatcher _eventDispatcher;
    EventCustom 	_eventProjectionChanged, 
    				_eventAfterDraw, 
    				_eventAfterVisit, 
    				_eventAfterUpdate,
    				_eventResetDirector,
    				_eventBeforeUpdate;
    
    EventCustom		_eventChangeScene;
        
    /* delta time since last tick to main loop */
	float _deltaTime;
    
    /* The GLView, where everything is rendered */
    GLView _openGLView;

    //texture cache belongs to this director
    TextureCache _textureCache;

    double _animationInterval;
    double _oldAnimationInterval;

    /* landscape mode ? */
    boolean _landscape;
    
    boolean _displayStats;
    float _accumDt;
    float _frameRate;
    
    //TODO LabelAtlas 
//    LabelAtlas *_FPSLabel;
//    LabelAtlas *_drawnBatchesLabel;
//    LabelAtlas *_drawnVerticesLabel;
    
    /** Whether or not the Director is paused */
    boolean _paused;

    /* How many frames were called since the director started */
    long _totalFrames;
    int _frames;
    float _secondsPerFrame;
    
    /* The running scene */
    IScene _runningScene;
    
    /* will be the next 'runningScene' in the next frame
     nextScene is a weak reference. */
    IScene _nextScene;
    
    /* If true, then "old" scene will receive the cleanup message */
    boolean _sendCleanupToScene;

    /* scheduled scenes */
    Array<IScene> _scenesStack;
    
    /* last time the main loop was updated */
//    struct timeval *_lastUpdate;

    /* whether or not the next delta time will be zero */
    boolean _nextDeltaTimeZero;
    
    /* projection used */
    Projection _projection = Projection._2D;

    /* window size in points */
    public Size _winSizeInPoints = new Size();
    
    /* content scale factor */
    float _contentScaleFactor;

    /* This object will be visited after the scene. Useful to hook a notification node */
    INode _notificationNode;

    /* Renderer for the Director */
    Renderer _renderer;

    public boolean _isStatusLabelUpdated;
    
    private static final Vector2 stackVec2_1 = new Vector2();
//    private static final Matrix4 stackM4_1 = new Matrix4();
//    private static final Vector3 stackVec3_1 = new Vector3();
    public static void GLToClipTransform(Matrix4 transformOut) {
        if(null == transformOut) return;
        
        Director director = Director.getInstance();

        Matrix4 projection = director.getMatrix(MATRIX_STACK_TYPE.MATRIX_STACK_PROJECTION);
        Matrix4 modelview = director.getMatrix(MATRIX_STACK_TYPE.MATRIX_STACK_MODELVIEW);
        
//        System.out.println(projection);
//        System.out.println(modelview);
        
        //?
        transformOut.set(projection);
        transformOut.mul(modelview);
//        *transformOut = projection * modelview;
    }
    
//#if  (CC_TARGET_PLATFORM != CC_PLATFORM_WINRT)
//    /* Console for the director */
//    Console *_console;
//#endif
}

/** 
 @brief DisplayLinkDirector is a Director that synchronizes timers with the refresh rate of the display.
 
 Features and Limitations:
  - Scheduled timers & drawing are synchronizes with the refresh rate of the display
  - Only supports animation intervals of 1/60 1/30 & 1/15
 
 @since v0.8.2
 */
//class DisplayLinkDirector : public Director
//{
//public:
//    DisplayLinkDirector() 
//        : _invalid(false)
//    {}
//    virtual ~DisplayLinkDirector(){}
//
//    //
//    // Overrides
//    //
//    virtual void mainLoop() override;
//    virtual void setAnimationInterval(double value) override;
//    virtual void startAnimation() override;
//    virtual void stopAnimation() override;
//
//protected:
//    bool _invalid;
//};
	
	
	
	//methods<<
//}
