package com.cocos2dj.s2d;

import com.badlogic.gdx.graphics.Color;

/**
 * 没有完成
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
    	return new CameraBackgroundBrush();
    }
    
    /**
     * Creates a color brush
     * @param color Color of brush
     * @param depth Depth used to clear depth buffer
     * @return Created brush
     */
    public static CameraBackgroundColorBrush createColorBrush(Color color, float depth) {
    	CameraBackgroundColorBrush camera = new CameraBackgroundColorBrush();
    	camera.setColor(color);
    	camera.setDepth(0f);
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
    protected CameraBackgroundBrush() {}
    boolean init() { return true; }
    
    
    /**
     * 2d brush
     */
	static class CameraBackgroundColorBrush extends CameraBackgroundBrush {
		
		public final BrushType getBrushType() {
			return BrushType.COLOR;
		}
		public Color getColor() {
			return _color;
		}
		public void setColor(float r, float g, float b, float a) {
			_color.set(r, g, b, a);
		}
		public void setColor(Color color) {
			_color.set(color);
		}
		
		public float getDepth() {
			return _depth;
		}
		public void setDepth(float depth) {
			_depth = depth;
		}
		
		/**
         * Draw background
         */
        public void drawBackground(final Camera camera) {
        	
        }
        
		protected Color _color = new Color();
		protected float _depth = 0;
	}
	
	
}
