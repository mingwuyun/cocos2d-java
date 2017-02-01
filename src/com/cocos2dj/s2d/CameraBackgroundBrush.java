package com.cocos2dj.s2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;

/**
 * 
 * @author Copyright (c) 2017 xu jun
 */
public class CameraBackgroundBrush {

	public static enum BrushType {
		NONE, //none brush
        DEPTH, // depth brush. See CameraBackgroundDepthBrush
        COLOR, // color brush. See CameraBackgroundColorBrush
        SKYBOX, // skybox brush. See CameraBackgroundSkyBoxBrush
	}
	/**
     * Creates a none brush, it does nothing when clear the background
     * @return Created brush.
     */
    public static CameraBackgroundBrush createNoneBrush() {
    	
    }
    
    /**
     * Creates a depth brush, which clears depth buffer with a given depth.
     * @param depth Depth used to clear depth buffer
     * @return Created brush
     */
//    public static CameraBackgroundDepthBrush createDepthBrush(float depth = 1.f) {
//    	
//    }
    
    /**
     * Creates a color brush
     * @param color Color of brush
     * @param depth Depth used to clear depth buffer
     * @return Created brush
     */
    public static CameraBackgroundColorBrush createColorBrush(Color color, float depth) {
    	CameraBackgroundColorBrush camera = new CameraBackgroundColorBrush();
    	return camera;
    }
    
    /** Creates a Skybox brush with 6 textures.
    @param positive_x texture for the right side of the texture cube face.
    @param negative_x texture for the up side of the texture cube face.
    @param positive_y texture for the top side of the texture cube face
    @param negative_y texture for the bottom side of the texture cube face
    @param positive_z texture for the forward side of the texture cube face.
    @param negative_z texture for the rear side of the texture cube face.
    @return  A new brush inited with given parameters.
    */
//   static CameraBackgroundSkyBoxBrush* createSkyboxBrush(const std::string& positive_x, const std::string& negative_x,
//                                                         const std::string& positive_y, const std::string& negative_y,
//                                                         const std::string& positive_z, const std::string& negative_z) {
//	}
	
	
	public boolean isValid() {
		return true;
	}
	
	/**
     * get brush type
     * @return BrushType
     */
    public BrushType getBrushType() { return BrushType.NONE;}
    /**
     * draw the background
     */
    public void drawBackground(Camera camera) {}

    protected CameraBackgroundBrush() {
    	
    }

    boolean init() { return true; }
    
//    GLProgramState* _glProgramState;
    
    
    /**
     * Depth brush clear depth buffer with given depth
     */
    class CC_DLL CameraBackgroundDepthBrush : public CameraBackgroundBrush
    {
    public:
        /**
         * Create a depth brush
         * @param depth Depth used to clear the depth buffer
         * @return Created brush
         */
        static CameraBackgroundDepthBrush* create(float depth);
        
        /**
         * Get brush type. Should be BrushType::DEPTH
         * @return brush type
         */
        virtual BrushType getBrushType() const override { return BrushType::DEPTH; }
        
        /**
         * Draw background
         */
        public void drawBackground(final Camera camera) {
        	Gdx.gl.glColorMask(_clearColor, _clearColor, _clearColor, _clearColor);
        	Gdx.gl.glStencilMask(0);
        	
        	//get old
        	Gdx.gl.glIsEnabled(GL20.GL_DEPTH_TEST);
//        	Gdx.gl.glGetIntegerv(pname, params);
//        	Gdx.gl.glGetBooleanv(pname, params);
        	
        	Gdx.gl.glDepthMask(true);
        	Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        	Gdx.gl.glDepthFunc(GL20.GL_ALWAYS);
        	
//        	if(o)
        	/* IMPORTANT: We only need to update the states that are not restored.
            Since we don't know what was the previous value of the mask, we update the RenderState
            after setting it.
            The other values don't need to be updated since they were restored to their original values
            */
           Gdx.gl.glStencilMask(0xFFFFF);
           //        RenderState::StateBlock::_defaultState->setStencilWrite(0xFFFFF);
           
           /* BUG: RenderState does not support glColorMask yet. */
           Gdx.gl.glColorMask(true, true, true, true);
        }
        
        /**
         * Set depth
         * @param depth Depth used to clear depth buffer
         */
        void setDepth(float depth) { _depth = depth; }
        
//    CC_CONSTRUCTOR_ACCESS:
//        CameraBackgroundDepthBrush();
//        virtual ~CameraBackgroundDepthBrush();
//
//        virtual bool init() override;
        
    protected float _depth;
        
    protected boolean _clearColor;
    
//    V3F_C4B_T2F_Quad _quad;
    };
    
	static class CameraBackgroundColorBrush extends CameraBackgroundBrush {
		
		public final BrushType getBrushType() {
			return BrushType.COLOR;
		}
		
		protected Color _color;
	}
	
	
}
