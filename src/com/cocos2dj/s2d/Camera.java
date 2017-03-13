package com.cocos2dj.s2d;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.BufferUtils;
import com.cocos2dj.base.CameraManager;
import com.cocos2dj.base.Director;
import com.cocos2dj.base.Size;
import com.cocos2dj.macros.CCLog;
import com.cocos2dj.protocol.ICamera;
import com.cocos2dj.renderer.Renderer;
import com.cocos2dj.renderer.Viewport;

import java.nio.IntBuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Camera.java
 * <br>Type
 * <p>
 * 
 * @author Copyright (c) 2017 xu jun
 */
public class Camera extends Node implements ICamera {
	
	public static final String TAG = "Camera";
	/**
	 * Note: 
	 * Scene creates a default camera. And the default camera mask of Node is 1, therefore it can be seen by the default camera.
	 * During rendering the scene, it draws the objects seen by each camera in the added order except default camera. The default camera is the last one being drawn with.
	 * It's usually a good idea to render 3D objects in a separate camera.
	 * And set the 3d camera flag to CameraFlag::USER1 or anything else except DEFAULT. Dedicate The DEFAULT camera for UI, because it is rendered at last.
	 * You can change the camera order to get different result when depth test is not enabled.
	 * For each camera, transparent 3d sprite is rendered after opaque 3d sprite and other 2d objects.
	 */
	public static final int DEFAULT = 0;
	public static final int USER1 = 1 << 1;
	public static final int USER2 = 1 << 2;
	public static final int USER3 = 1 << 3;
	public static final int USER4 = 1 << 4;
	public static final int USER5 = 1 << 5;
	public static final int USER6 = 1 << 6;
	public static final int USER7 = 1 << 7;
	public static final int USER8 = 1 << 8;
	
   /**
    * The type of camera.
    */
	public static enum Type {
        PERSPECTIVE,
        ORTHOGRAPHIC
    }
	
    /**
    * Creates a perspective camera.
    *
    * @param fieldOfView The field of view for the perspective camera (normally in the range of 40-60 degrees).
    * @param aspectRatio The aspect ratio of the camera (normally the width of the viewport divided by the height of the viewport).
    * @param nearPlane The near plane distance.
    * @param farPlane The far plane distance.
    */
    public static Camera createPerspective(float fieldOfView, float aspectRatio, float nearPlane, float farPlane) {
    	Camera ret = new Camera();
    	ret.initPerspective(fieldOfView, aspectRatio, nearPlane, farPlane);
    	return ret;
    }
    
    /**
    * Creates an orthographic camera.
    *
    * @param zoomX The zoom factor along the X-axis of the orthographic projection (the width of the ortho projection).
    * @param zoomY The zoom factor along the Y-axis of the orthographic projection (the height of the ortho projection).
    * @param nearPlane The near plane distance.
    * @param farPlane The far plane distance.
    */
    public static Camera createOrthographic(float zoomX, float zoomY, float nearPlane, float farPlane) {
    	Camera ret = new Camera();
    	ret.initOrthographic(zoomX, zoomY, nearPlane, farPlane);
    	return ret;
    }

    /** create default camera, the camera type depends on Director::getProjection, the depth of the default camera is 0 */
    public static Camera create() {
    	Camera camera = new Camera();
    	camera.initDefault();
    	camera.setDepth(1);
    	return camera;
    }

    /**
     * Get the visiting camera , the visiting camera shall be set on Scene::render
     */
    public final static Camera getVisitingCamera() {
    	return (Camera) CameraManager._visitingCamera;
    }
    
//    static  experimental::Viewport getDefaultViewport();
//    static void setDefaultViewport( experimental::Viewport vp);

    /**
     * Get the default camera of the current running scene.
     */
    public final static Camera getDefaultCamera() {
    	Scene scene = (Scene) Director.getInstance().getRunningScene();
        if(scene != null) {
            return scene.getDefaultCamera();
        }
        return null;
    }

    /**
    * Gets the type of camera.
    */
    public Camera.Type getType() { return _type; }

    /**get  set Camera flag*/
    public int getCameraFlag() { return (int)_cameraFlag; }
    public void setCameraFlag(int flag) { _cameraFlag = (short) flag; }
    
    /**
    * Make Camera looks at target
    *
    * @param target The target camera is point at
    * @param up The up vector, usually it's Y axis
    */
    public void lookAt( Vector3 target,  Vector3 up) {
//    	PerspectiveCamera c = null;
//    	c.lookAt(target);
    }

    /**
    * Gets the camera's projection matrix.
    *
    * @return The camera projection matrix.
    */
    public Matrix4 getProjectionMatrix() {
    	return _projection;
    }
    
    /**
    * Gets the camera's view matrix.
    *
    * @return The camera view matrix.
    */
     public Matrix4 getViewMatrix() {
//    	 Mat4 viewInv(getNodeToWorldTransform());
    	 _viewInv.set(getNodeToWorldTransform());
//	    static int count = sizeof(float) * 16;
//	    if (memcmp(viewInv.m, _viewInv.m, count) != 0)
//	    {
	        _viewProjectionDirty = true;
	        _frustumDirty = true;
//	        _viewInv = viewInv;
//	        _view = viewInv.getInversed();
	        _view.set(_viewInv).inv();
//	    }
	    return _view;
    	    
//    	 ViewPort vp;
//    	 com.badlogic.gdx.graphics.glutils
     }

    /**get view projection matrix*/
     public Matrix4 getViewProjectionMatrix() {
    	 getViewMatrix();
	    if (_viewProjectionDirty) {
	        _viewProjectionDirty = false;
	        _viewProjection.set(_projection).mul(_view);
	    }
	    return _viewProjection;
     }
    
    /** convert the specified point in 3D world-space coordinates into the screen-space coordinates.
     * Origin point at left top corner in screen-space.
     * @param src The world-space position.
     * @return The screen-space position. <b>(pool object)</b/
     */
    public final Vector2 project(final Vector3 src)  {
    	Vector2 screenPos = pool_vec2;
        Size viewport = Director.getInstance().getWinSize();
        
        Vector3 clipPos = pool_vec3;
        clipPos.set(src).mul(getViewProjectionMatrix());
        
        float ndcX = clipPos.x;
        float ndcY = clipPos.y;
        
        screenPos.x = (ndcX + 1.0f) * 0.5f * viewport.width;
        screenPos.y = (1.0f - (ndcY + 1.0f) * 0.5f) * viewport.height;
        return screenPos;
    }
    
    /** convert the specified point in 3D world-space coordinates into the GL-screen-space coordinates.
     *
     * Origin point at left bottom corner in GL-screen-space.
     * @param src The 3D world-space position.
     * @return The GL-screen-space position.<b>(pool object)</b/
     */
    public final Vector2 projectGL(final Vector3 src) {
    	 Vector2 screenPos = pool_vec2;
    	 Size viewport = Director.getInstance().getWinSize();
    	 
    	 Vector3 clipPos = pool_vec3;
         clipPos.set(src).mul(getViewProjectionMatrix());
         
         float ndcX = clipPos.x;
         float ndcY = clipPos.y;
         
         screenPos.x = (ndcX + 1.0f) * 0.5f * viewport.width;
         screenPos.y = (1.0f - (ndcY + 1.0f) * 0.5f) * viewport.height;
         return screenPos;
    }
    
    /**
     * Convert the specified point of screen-space coordinate into the 3D world-space coordinate.
     *
     * Origin point at left top corner in screen-space.
     * @param src The screen-space position.
     * @return The 3D world-space position.
     */
    public Vector3 unproject( Vector3 src) {
    	Vector3 ret = pool_vec3_2;
    	unproject(Director.getInstance().getWinSize(), src, ret);
    	return ret;
    }
    
    /**
     * Convert the specified point of GL-screen-space coordinate into the 3D world-space coordinate.
     *
     * Origin point at left bottom corner in GL-screen-space.
     * @param src The GL-screen-space position.
     * @return The 3D world-space position.
     */
    public Vector3 unprojectGL( Vector3 src) {
    	Vector3 ret = pool_vec3_2;
    	unprojectGL(Director.getInstance().getWinSize(), src, ret);
    	return ret;
    }
    
    /**
     * Convert the specified point of screen-space coordinate into the 3D world-space coordinate.
     *
     * Origin point at left top corner in screen-space.
     * @param size The window size to use.
     * @param src  The screen-space position.
     * @param dst  The 3D world-space position.
     */
    public void unproject( Size size,  Vector3 src, Vector3 dst) {
    	Vector3 screen = pool_vec3.set(src.x / size.width, ((size.height - src.y)) / size.height, src.z);
	    screen.x = screen.x * 2.0f - 1.0f;
	    screen.y = screen.y * 2.0f - 1.0f;
	    screen.z = screen.z * 2.0f - 1.0f;
	    
	    projectionInv.set(getViewProjectionMatrix());
	    projectionInv.inv();
	    
	    screen.mul(projectionInv);
	    dst.set(screen);
    }
    
    /**
     * Convert the specified point of GL-screen-space coordinate into the 3D world-space coordinate.
     *
     * Origin point at left bottom corner in GL-screen-space.
     * @param size The window size to use.
     * @param src  The GL-screen-space position.
     * @param dst  The 3D world-space position.
     */
    public void unprojectGL(Size size,  Vector3 src, Vector3 dst) {
    	Vector3 screen = pool_vec3.set(src.x / size.width, src.y / size.height, src.z);
	    screen.x = screen.x * 2.0f - 1.0f;
	    screen.y = screen.y * 2.0f - 1.0f;
	    screen.z = screen.z * 2.0f - 1.0f;
	    
	    projectionInv.set(getViewProjectionMatrix());
	    projectionInv.inv();
	    
	    screen.mul(projectionInv);
	    dst.set(screen);
    }

    /**
     * Is this aabb visible in frustum
     */
    public boolean isVisibleInFrustum(Rectangle aabb) {
    	if(_frustumDirty) {
//    		_frustum.
    		_frustumDirty = false;
    	}
    	return true;
    }
    
    /**
     * Get object depth towards camera
     */
    public float getDepthInView( Matrix4 transform) {
    	//TODO
    	return 0f;
    }
    
    /**
     * set depth, camera with larger depth is drawn on top of camera with smaller depth, the depth of camera with CameraFlag::DEFAULT is 0, user defined camera is -1 by default
     * <br>depth越小 越先绘制
     */
    public void setDepth(int depth) {
    	if(_depth != depth) {
    		_depth = depth;
    		if(_scene != null) {
    			_scene.setCameraOrderDirty();
    		}
    	}
    }
    
    /**
     * get depth, camera with larger depth is drawn on top of camera with smaller depth, the depth of camera with CameraFlag::DEFAULT is 0, user defined camera is -1 by default
     */
    public int getDepth()  { return _depth; }
    
    /**
     get rendered order
     */
    public  int getRenderOrder() {
    	int result = 0;
//    	if(_fob != null) {
    		
//    	} else {
    		result = 127 << 8;
//    	}
    	result += _depth;
    	return result;
    }
    
    /**Get the frustum's far plane.*/
    public float getFarPlane()  { return _farPlane; }

    /**Get the frustum's near plane.*/
    public float getNearPlane()  { return _nearPlane; }
    
    
    ////////////////////////////////////////////////
    //override
    @Override
    public void onEnter() {
    	if(_scene == null) {
    		setScene(getScene());
    	}
    	super.onEnter();
    }
    
    @Override
    public void onExit() {
    	setScene(null);
    	super.onExit();
    	release();
    }
    
    @Override
    public void visit(Renderer renderer,  Matrix4 parentTransform, int parentFlags) {
    	_viewProjectionUpdated = _transformUpdated;
    	super.visit(renderer, parentTransform, parentFlags);
    }
    //////////////////////////////////////////////////
    
    
    /**
     Before rendering scene with this camera, the background need to be cleared. It clears the depth buffer with max depth by default. Use setBackgroundBrush to modify the default behavior
     */
    public void clearBackground() {
//    	if(_clearBrush != null) {
//    		_clearBrush.drawBackground(this);
//    	}
    }
    
    /**
     Apply the FBO, RenderTargets and viewport.
     */
    public void apply() {
    	applyFrameBufferObject();
    	applyViewport();
    }
    
    /**
     Restor the FBO, RenderTargets and viewport.
     */
    public void restore() {
    	
    }

    /**
     Set FBO, which will attach several render target for the rendered result.
     */
//    public void setFrameBufferObject(experimental::FrameBuffer* fbo) {
    	//TODO unfinshed
//    }
    
    /**
     Set Viewport for camera.
     */
    public void setViewport(Viewport vp) {
    	this._viewport.set(vp);
    }
    
    public Viewport getViewport() {return _viewport;}

    /**
     * Whether or not the viewprojection matrix was updated since the last frame.
     * @return True if the viewprojection matrix was updated since the last frame.
     */
    public boolean isViewProjectionUpdated()  {return _viewProjectionUpdated;}

    /**
     * set the background brush. See CameraBackgroundBrush for more information.
     * @param clearBrush Brush used to clear the background
     */
    public void setBackgroundBrush(CameraBackgroundBrush clearBrush) {
    	this._clearBrush = clearBrush;
    }

    /**
     * Get clear brush
     */
    public CameraBackgroundBrush getBackgroundBrush()  { return _clearBrush; }

    public boolean isBrushValid() {
    	return _clearBrush != null && _clearBrush.isValid();
    }

    
//CC_RUCTOR_ACCESS:
    //ctor>>
    Camera() {}
    
    /**
     * 可能涉及到frameBuffer 需要调用该方法销毁
     */
    public void release() {
    	super.release();
    }

    /**
     * Set the scene,this method shall not be invoke manually
     */
    void setScene(Scene scene) {
    	if (_scene != scene) {
            //remove old scene
            if (_scene != null) {
            	_scene._cameras.clear();
            }
            //set new scene
            if (scene != null) {
                _scene = scene;
                
                _scene._cameras.add(this);
                _scene.setCameraOrderDirty();
            }
        }
    }

    /**set additional matrix for the projection matrix, it multiplies mat to projection matrix when called, used by WP8*/
    void setAdditionalProjection( Matrix4 mat) {
    	
    }

    /** init camera */
    final boolean initDefault() {
    	Size size = Director.getInstance().getWinSize();
        //create default camera
    	Director.Projection projection = Director.getInstance().getProjection();
//    	System.out.println("projection = " + projection);
        switch (projection)
        {
            case _2D: {
//            	System.out.println("size = " + size);
                initOrthographic(size.width, size.height, -1024, 1024);
                
                setPosition3D(new Vector3(0.0f, 0.0f, 0.0f));
                setRotation3D(new Vector3(0.f, 0.f, 0.f));
                break;
            }
            case _3D:
            {
                float zeye = Director.getInstance().getZEye();
                initPerspective(60, (float)size.width / size.height, 10, zeye + size.height / 2.0f);
                Vector3 eye = new Vector3(size.width/2, size.height/2.0f, zeye);
                Vector3 center = new Vector3(size.width/2, size.height/2, 0.0f); 
                Vector3	up = new Vector3(0.0f, 1.0f, 0.0f);
                setPosition3D(eye);
                lookAt(center, up);
                break;
            }
            default:
                CCLog.engine(TAG, "unrecognized projection");
                break;
        }
        return true;
    }
    
    final boolean initPerspective(float fieldOfView, float aspectRatio, float nearPlane, float farPlane) {
    	_fieldOfView = fieldOfView;
        _aspectRatio = aspectRatio;
        _nearPlane = nearPlane;
        _farPlane = farPlane;
        _projection.setToProjection(_nearPlane, _farPlane, _fieldOfView, _aspectRatio);
        _viewProjectionDirty = true;
        _frustumDirty = true;
        return true;
    }
    
    final boolean initOrthographic(float zoomX, float zoomY, float nearPlane, float farPlane) {
		 _zoom[0] = zoomX;
		 _zoom[1] = zoomY;
	    _nearPlane = nearPlane;
	    _farPlane = farPlane;
	    _projection.setToOrtho(0, _zoom[0], 0, _zoom[1], _nearPlane, _farPlane);
	    _viewProjectionDirty = true;
	    _frustumDirty = true;
	    return true;
    }
    
    final void applyFrameBufferObject() {
    	
    }
    
    
    final void applyViewport() {
		Gdx.gl.glGetIntegerv(GL20.GL_VIEWPORT, _oldViewport);
    	
//        if(null == _fbo) {
    		final Viewport defaultViewport = Viewport._defaultViewport;
//    		System.out.println(defaultViewport);
            Gdx.gl.glViewport((int)defaultViewport._left, (int)defaultViewport._bottom, (int)defaultViewport._width, (int)defaultViewport._height);
//            Gdx.gl.glViewport(0, 100, 200, 200);

//        } else {
//            Gdx.gl.glViewport(_viewport._left * _fbo->getWidth(), _viewport._bottom * _fbo->getHeight(),
//                       _viewport._width * _fbo->getWidth(), _viewport._height * _fbo->getHeight());
//        }
    }
    
    final void restoreFrameBufferObject() {
    	
    }
    
    final void restoreViewport() {
    	Gdx.gl.glViewport(_oldViewport.get(0), _oldViewport.get(1), 
    			_oldViewport.get(2), _oldViewport.get(3));
    }
    

//protected:
    static Vector2 pool_vec2 = new Vector2();
    static Vector3 pool_vec3 = new Vector3();
    static Vector3 pool_vec3_2 = new Vector3();
    static Matrix4 projectionInv = new Matrix4();		//lazy
    
//    public static Camera _visitingCamera;
    
//    static experimental::Viewport _defaultViewport;
    // use Viewport._defaultViewport

    Scene _scene; //Scene camera belongs to
    Matrix4 _projection = new Matrix4();
    
    
    Matrix4 _view = new Matrix4();
    Matrix4 _viewInv = new Matrix4();
    Matrix4 _viewProjection = new Matrix4();

    
    
    Vector3 _up;
    Camera.Type 	_type;
    float 			_fieldOfView;
    float[] 		_zoom = new float[]{0, 0};
    float 			_aspectRatio;
    float 			_nearPlane;
    float 			_farPlane;
    boolean  		_viewProjectionDirty = true;
    boolean 		_viewProjectionUpdated; //Whether or not the viewprojection matrix was updated since the last frame.
    int 			_cameraFlag = 1; // camera flag
    Frustum			_frustum = new Frustum();
    boolean 		_frustumDirty = true;
    int  			_depth;                 //camera depth, the depth of camera with CameraFlag::DEFAULT flag is 0 by default, a camera with larger depth is drawn on top of camera with smaller depth
    CameraBackgroundBrush _clearBrush; //brush used to clear the back ground
    Viewport _viewport = new Viewport();
//    experimental::FrameBuffer* _fbo;
//    GLint _oldViewport[4];
    IntBuffer _oldViewport = BufferUtils.newIntBuffer(16);
    
//    static:
//    experimental::Viewport _viewport;
    
//    experimental::FrameBuffer* _fbo;
    
//    GLint _oldViewport[4];
}
