package com.cocos2dj.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.cocos2dj.base.Director;
import com.cocos2dj.base.Director.MATRIX_STACK_TYPE;
import com.cocos2dj.base.Rect;
import com.cocos2dj.base.Size;
import com.cocos2dj.s2d.Camera;

/**
 * Renderer.java
 * <p>
 * 
 * @author Copyright (c) 2017 xu jun
 */
public class Renderer {

	public static void setProjectionMatrixDirty() {
		
	}
	
	
	PolygonSpriteBatch		batch;
	ShapeRenderer 			shapeRenderer;	//lazy
//	SpriteCache				spriteCache;
	Array<RenderCommand>	commandQueue;
	Array<RenderCommand> 	shapeCommandQueue;
	Matrix4					_projection;	//当前相机投影矩阵
	Director				_director;
	
	public Renderer() {
		batch = new PolygonSpriteBatch(2000, GLProgramCache.getInstance().getSpriteBatchDefaultProgram());
		commandQueue = new Array<>(128);
		shapeCommandQueue = new Array<>(64);
		_director = Director.justInstance();
	}
	
	final ShapeRenderer getShapeRenderer() {
		if(shapeRenderer == null) {
			shapeRenderer = new ShapeRenderer(1000);
			shapeRenderer.setAutoShapeType(true);
		}
		return shapeRenderer;
	}
	
	public void setCurrentProjection(Matrix4 projection) {
		_projection = projection;
	}
	
	/**清理gl状态 */
	public void clear() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		batch.totalRenderCalls = 0;	//debug
		
	}
	
	public void clearDrawStats() {
		
	}
	
	public void render() {
		_projection = _director.getMatrix(MATRIX_STACK_TYPE.MATRIX_STACK_PROJECTION);
		batch.setProjectionMatrix(_projection);

		for(int i = 0, n = commandQueue.size; i < n; ++i) {
			commandQueue.get(i).execute(this);
		}
		
		if(batch.isDrawing()) {
			batch.end();
		}
		commandQueue.clear();
		
		if(shapeRenderer != null) {
			shapeRenderer.setProjectionMatrix(_projection);
		}
		for(int i = 0, n = shapeCommandQueue.size; i < n; ++i) {
			shapeCommandQueue.get(i).execute(this);
		}
		if(shapeRenderer != null && shapeRenderer.isDrawing()) {
			shapeRenderer.end();
		}
		shapeCommandQueue.clear();
	}
	
	static final Rect stackRect = new Rect();
	static final Vector3 stackVec3 = new Vector3();
	
	/**
	 * 相机包含测试
	 * @param transform 变换
	 * @param size 尺寸
	 * @param anchorPointInPoints 锚dian偏移
	 * @return
	 */
	public final boolean checkVisibility(final Matrix4 transform, final Size size, final Vector2 anchorPointInPoints) {
		final Director director = Director.getInstance();
	    //If draw to Rendertexture, return true directly.
	    // only cull the default camera. The culling algorithm is valid for default camera.
	    if (director.getRunningScene() == null) {// || (scene.getDefaultCamera() != Camera.getVisitingCamera())) {
	        return true;
	    }
	    
	    Vector2 temporigin = director.getVisibleOrigin();
	    Size tempsize = director.getVisibleSize();
	    Rect visiableRect = stackRect;
	    visiableRect.set(temporigin.x, temporigin.y, tempsize.width, tempsize.height);
	    
	    // transform center point to screen space
	    float hSizeX = size.width/2;
	    float hSizeY = size.height/2;
	    Vector3 v3p = stackVec3.set(hSizeX - anchorPointInPoints.x, hSizeY - anchorPointInPoints.y, 0);
	    v3p.mul(transform);
//	    transform.transformPoint(v3p);
	    Vector2 v2p = Camera.getVisitingCamera().projectGL(v3p);

	    // convert content size to world coordinates
	    final float[] val = transform.val;
	    float wshw = Math.max(Math.abs(hSizeX * val[Matrix4.M00] + hSizeY * val[Matrix4.M10]), 
	    		Math.abs(hSizeX * val[Matrix4.M00] - hSizeY * val[Matrix4.M10]));
//	    		std::max(fabsf(hSizeX * transform.m[0] + hSizeY * transform.m[4]), fabsf(hSizeX * transform.m[0] - hSizeY * transform.m[4]));
	    float wshh = Math.max(Math.abs(hSizeX * val[Matrix4.M01] + hSizeY * val[Matrix4.M11]), 
	    		Math.abs(hSizeX * val[Matrix4.M01] - hSizeY * val[Matrix4.M11])); 
//	    		std::max(fabsf(hSizeX * transform.m[1] + hSizeY * transform.m[5]), fabsf(hSizeX * transform.m[1] - hSizeY * transform.m[5]));
	    
	    // enlarge visible rect half size in screen coord
	    visiableRect.x -= wshw;
	    visiableRect.y -= wshh;
	    visiableRect.width += wshw * 2;
	    visiableRect.height += wshh * 2;
	    boolean ret = visiableRect.containsPoint(v2p);
//	    System.out.println("visiableRect = " + visiableRect + " pos = " + v2p);
	    return ret;
	}
	
	//////////////////////////////////
	public final void addBatchCommand(RenderCommand.BatchCommand cmd) {
		commandQueue.add(cmd);	
	}
	
	public final void addCacheCommand(RenderCommand.CacheCommand cmd) {
		
	}
	
	public final void addDrawCommand(RenderCommand.DrawCommand cmd) {
		commandQueue.add(cmd);
	}
	
	public final void addShapeCommand(RenderCommand.ShapeCommand cmd) {
		shapeCommandQueue.add(cmd);
	}
	
	
}
