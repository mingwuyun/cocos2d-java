package com.cocos2dj.s2d;

import java.util.Comparator;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;
import com.cocos2dj.base.CameraManager;
import com.cocos2dj.base.Director.MATRIX_STACK_TYPE;
import com.cocos2dj.module.Module;
import com.cocos2dj.module.ModuleManager;
import com.cocos2dj.protocol.ICamera;
import com.cocos2dj.protocol.IScene;
import com.cocos2dj.renderer.Renderer;

/**
 * 
 * @author Copyright (c) 2017 xu jun
 *
 */
public class Scene extends Node implements IScene {
	
	public static Scene createScene() {
		return new Scene();
	}
	
	//ctor>>
	public Scene() {
//		System.out.println("Director = " + _director);
		_ignoreAnchorPointForPosition = true;
	    setAnchorPoint(0.5f, 0.5f);
	    
	    _cameraOrderDirty = true;
	    
	    //create default camera
	    _defaultCamera = Camera.create();
	    addChild(_defaultCamera);		//具体处理在onEnter回调中
	    
//	    _event = Director::getInstance()->getEventDispatcher()->addCustomEventListener(Director::EVENT_PROJECTION_CHANGED, std::bind(&Scene::onProjectionChanged, this, std::placeholders::_1));
//	    _event->retain();
	    _moduleManager = new ModuleManager(this);
	    
	    CameraManager._visitingCamera = null;
	}
	
	ModuleManager _moduleManager;
	boolean _cameraOrderDirty;
	Camera _defaultCamera;
	Array<Camera> _cameras = new Array<>();
	
	public Camera getDefaultCamera() {
		return _defaultCamera;
	}
	 
	
	public void setCameraOrderDirty() {
		_cameraOrderDirty = true;
	}
	
	static final Comparator<Camera> camera_cmp = new Comparator<Camera>() {
		@Override
		public int compare(Camera o1, Camera o2) {
			return o1.getRenderOrder() - o2.getRenderOrder();
		}
	};
	
	public Array<Camera> getCameras() {
		if (_cameraOrderDirty) {
			_cameras.sort(camera_cmp);
	        _cameraOrderDirty = false;
	    }
	    return _cameras;
	}
	
	@Override
	public void render(Renderer renderer, Matrix4 eyeTransform, Matrix4 eyeProjection) {
	    final Matrix4 transform = getNodeToParentTransform();
	    for (Camera camera : getCameras()) {
	        if (!camera.isVisible()) {
	        	continue;
	        }
	        CameraManager._visitingCamera = camera;
//	        if (Camera.getDefaultCamera().getCameraFlag() == Camera.DEFAULT) {
//	            defaultCamera = Camera._visitingCamera;
//	        }
	        // There are two ways to modify the "default camera" with the eye Transform:
	        // a) modify the "nodeToParentTransform" matrix
	        // b) modify the "additional transform" matrix
	        // both alternatives are correct, if the user manually modifies the camera with a camera->setPosition()
	        // then the "nodeToParent transform" will be lost.
	        // And it is important that the change is "permament", because the matrix might be used for calculate
	        // culling and other stuff.
	        
//	        if (eyeProjection)
//	            camera->setAdditionalProjection(*eyeProjection * camera->getProjectionMatrix().getInversed());
//	        camera->setAdditionalTransform(eyeTransform.getInversed());

	        _director.pushMatrix(MATRIX_STACK_TYPE.MATRIX_STACK_PROJECTION, ((Camera)CameraManager._visitingCamera).getViewProjectionMatrix());
	        
	        camera.apply();
	        //clear background with max depth
	        camera.clearBackground();
	        //visit the scene
	        visit(renderer, transform, 0);
//	#if CC_USE_NAVMESH
//	        if (_navMesh && _navMeshDebugCamera == camera)
//	        {
//	            _navMesh->debugDraw(renderer);
//	        }
//	#endif
	        renderer.render();
	        camera.restore();

	        _director.popMatrix(MATRIX_STACK_TYPE.MATRIX_STACK_PROJECTION);

	        // we shouldn't restore the transform matrix since it could be used
	        // from "update" or other parts of the game to calculate culling or something else.
//	        camera->setNodeToParentTransform(eyeCopy);
	    }
	    CameraManager._visitingCamera = null;
	}


	
	@Override
	public int getCamerasCount() {
		return _cameras.size;
	}

	@Override
	public ICamera getCamera(int index) {
		return _cameras.get(index);
	}
	
	@Override
	public void onEnter() {
		_moduleManager.setRunning(true);	//必须这个顺序！
		_moduleManager.onSceneEnter();
		super.onEnter();
		_moduleManager.onSceneEnterAfter();
	}
	
	@Override
	public void onExit() {
		super.onExit();
		_moduleManager.onSceneExit();
		_moduleManager.setRunning(false);
	}
	
	
	////////////////////////////////////////////////////
	//TODO Scene
	public final Module addModule(Module module) {
		return addModule(module, null);
	}
	
	public final Module addModule(Module module, Object config) {
		_moduleManager.addModule(module, config);
		return module;
	}
	
	public final Module getModule(Class<? extends Module> clazz) {
		return _moduleManager.getModule(clazz);
	}
	
	public final Module getModule(String moduleName) {
		return _moduleManager.getModule(moduleName);
	}
	
	public final <T extends Module> T createModule(Class<T> clazz) {
		return createModule(clazz, null);
	}
	
	public final <T extends Module> T createModule(Class<T> clazz, Object config) {
		T module = null;
		try {
			module = clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		_moduleManager.addModule(module, config);
		return module;
	}
}
