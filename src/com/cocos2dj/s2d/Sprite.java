package com.cocos2dj.s2d;

import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.cocos2dj.base.Rect;
import com.cocos2dj.base.Size;
import com.cocos2dj.macros.CCLog;
import com.cocos2dj.renderer.GLProgramCache;
import com.cocos2dj.renderer.RenderCommand;
import com.cocos2dj.renderer.Renderer;
import com.cocos2dj.renderer.Texture;
//import com.cocos2dj.renderer.TextureRegion;

/**
 * Sprite.java
 * <p>
 * @author Copyright (c) 2017 xu jun
 */
public class Sprite extends Node implements RenderCommand.BatchCommandCallback {
     /* Sprite invalid index on the SpriteBatchNode. */
    public static final int INDEX_NOT_INITIALIZED = -1;

    /// @name Creators
    /// @{

    /**
     * Creates an empty sprite without texture. You can call setTexture method subsequently.
     *
     * @memberof Sprite
     * @return An autoreleased sprite object.
     */
    public static Sprite create() {
    	Sprite sprite = new Sprite();
    	sprite.init();
    	return sprite;
    }

    /**
     * Creates a sprite with an image filename.
     *
     * After creation, the rect of sprite will be the size of the image,
     * and the offset will be (0,0).
     *
     * @param   filename A path to image file, e.g., "scene1/monster.png".
     * @return  An autoreleased sprite object.
     */
    public static Sprite create(final String filename) {
    	Sprite sprite = new Sprite();
    	sprite.initWithFile(filename);
    	return sprite;
    }

    /**
     * Creates a sprite with an image filename and a rect.
     *
     * @param   filename A path to image file, e.g., "scene1/monster.png".
     * @param   rect     A subrect of the image file.
     * @return  An autoreleased sprite object.
     */
    public static Sprite create(final String filename, final Rect rect) {
    	Sprite sprite = new Sprite();
    	sprite.initWithFile(filename, rect);
    	return sprite;
    }

    /**
     * Creates a sprite with a Texture2D object.
     *
     * After creation, the rect will be the size of the texture, and the offset will be (0,0).
     *
     * @param   texture    A pointer to a Texture2D object.
     * @return  An autoreleased sprite object.
     */
    public static Sprite createWithTexture(Texture texture) {
    	Sprite sprite = new Sprite();
    	sprite.initWithTexture(texture);
    	return sprite;
    }

    /**
     * Creates a sprite with a texture and a rect.
     *
     * After creation, the offset will be (0,0).
     *
     * @param   texture     A pointer to an existing Texture2D object.
     *                      You can use a Texture2D object for many sprites.
     * @param   rect        Only the contents inside the rect of this texture will be applied for this sprite.
     * @param   rotated     Whether or not the rect is rotated.
     * @return  An autoreleased sprite object.
     */
    public static Sprite createWithTexture(Texture texture, final Rect rect, boolean rotated) {
    	Sprite sprite = new Sprite();
    	sprite.initWithTexture(texture, rect);
    	return sprite;
    }

    /**
     * Creates a sprite with an sprite frame.
     *
     * @param   spriteFrame    A sprite frame which involves a texture and a rect.
     * @return  An autoreleased sprite object.
     */
    public static Sprite createWithSpriteFrame(TextureRegion spriteFrame) {
    	Sprite sprite = new Sprite();
    	sprite.initWithSpriteFrame(spriteFrame);
    	return sprite;
    }
    
    /**
     * Creates a sprite with an sprite frame name.
     *
     * A SpriteFrame will be fetched from the SpriteFrameCache by spriteFrameName param.
     * If the SpriteFrame doesn't exist it will raise an exception.
     *
     * @param   spriteFrameName A null terminated string which indicates the sprite frame name.
     * @return  An autoreleased sprite object.
     */
    public static Sprite createWithSpriteFrameName(final String spriteFrameName) {
    	Sprite sprite = new Sprite();
    	sprite.initWithSpriteFrameName(spriteFrameName);
    	return sprite;
    }
    /////////////////////////////////////
    //TODO  end of creators group


    /** Sets a new texture (from a filename) to the sprite.
     *
     *  @memberof Sprite
     *  It will call `setTextureRect()` with the texture's content size.
     */
    public void setTexture(final String filename) {
    	
    }

    /** @overload
     *
     *  The Texture's rect is not changed.
     */
    public void setTexture(Texture texture) {
    	
    }

    /** Returns the Texture2D object used by the sprite. */
    public final Texture getTexture() {
    	
    	return null;
    }

    /**
     * Updates the texture rect of the Sprite in points.
     *
     * It will call setTextureRect(final Rect& rect, boolean rotated, final Size& untrimmedSize) with \p rotated = false, and \p utrimmedSize = rect.size.
     */
    public void setTextureRect(final Rect rect) {
    	
    }

    /** @overload
     *
     * It will update the texture coordinates and the vertex rectangle.
     */
    public void setTextureRect(final Rect rect, boolean rotated, final Size untrimmedSize) {
    	
    }

    /**
     * Sets the vertex rect.
     *
     * It will be called internally by setTextureRect.
     * Useful if you want to create 2x images from SD images in Retina Display.
     * Do not call it manually. Use setTextureRect instead.
     */
    public void setVertexRect(final Rect rect) {
    	
    }
    
    /**Sets a new SpriteFrame to the Sprite.
     * SpriteFrame*/
    public void setSpriteFrame(final String spriteFrameName) {
    	
    }
    
    
    /**set a new textureRegion to the Sprite 
     * 重置尺寸数据 */
    public void setSpriteFrame(TextureRegion newFrame) {
    	setSpriteFrame(newFrame, true);
    }
    
    /**set a new textureRegion to the Sprite 
     * @param reset true 会重置尺寸／中心点数据 */
    public void setSpriteFrame(TextureRegion newFrame, boolean reset) {
    	_sprite.setRegion(newFrame);
    	if(reset) {
    		super.setAnchorPoint(0.5f, 0.5f);
        	super.setContentSize(newFrame.getRegionWidth(), newFrame.getRegionHeight());
        	SET_DIRTY_RECURSIVELY();
    	}
    }
    

//    /** @deprecated Use `setSpriteFrame()` instead. */
//    CC_DEPRECATED_ATTRIBUTE virtual void setDisplayFrame(SpriteFrame *newFrame) { setSpriteFrame(newFrame); }

    /**
     * Returns whether or not a SpriteFrame is being displayed.
     */
    public boolean isFrameDisplayed(TextureRegion frame) {
    
    	return false;
    }

    /**
     * Returns the current displayed frame.
     */
//    public final TextureRegion getSpriteFrame()  {
//    	return _sprite.getre;
//    }
    
//    /** @deprecated Use `getSpriteFrame()` instead.
//     * @js NA
//     */
//    CC_DEPRECATED_ATTRIBUTE virtual SpriteFrame* getDisplayFrame() final { return getSpriteFrame(); }
//    /** @deprecated Use `getSpriteFrame()` instead. */
//    CC_DEPRECATED_ATTRIBUTE virtual SpriteFrame* displayFrame() final { return getSpriteFrame(); };

    /// @} End of frames methods


    /// @{
    /// @name Animation methods
    /**
     * Changes the display frame with animation name and index.
     * The animation name will be get from the AnimationCache.
     */
    public void setDisplayFrameWithAnimationName(final String animationName, int frameIndex) {
    	
    }
    /// @}


    /// @{
    /// @name Sprite Properties' setter/getters.

    /**
     * Whether or not the Sprite needs to be updated in the Atlas.
     *
     * @return True if the sprite needs to be updated in the Atlas, false otherwise.
     */
    public final boolean isDirty() { return _dirty; }

    /**
     * Makes the Sprite to be updated in the Atlas.
     */
    public final void setDirty(boolean dirty) { _dirty = dirty; }


    /**
     * Returns the index used on the TextureAtlas.
     */
    public final int getAtlasIndex() { return _atlasIndex; }

    /**
     * Sets the index used on the TextureAtlas.
     *
     * @warning Don't modify this value unless you know what you are doing.
     */
    public final void setAtlasIndex(int atlasIndex) { _atlasIndex = atlasIndex; }

    /**
     * Returns the rect of the Sprite in points.
     */
//    public final Rect getTextureRect() { return _rect; }

    /**
     * Gets the weak reference of the TextureAtlas when the sprite is rendered using via SpriteBatchNode.
     */
//    public final TextureAtlas getTextureAtlas() { return _textureAtlas; }

    /**
     * Sets the weak reference of the TextureAtlas when the sprite is rendered using via SpriteBatchNode.
     */
//    public final void setTextureAtlas(TextureAtlas textureAtlas) { _textureAtlas = textureAtlas; }


    /**
     * Returns the flag which indicates whether the sprite is flipped horizontally or not.
     *
     * It only flips the texture of the sprite, and not the texture of the sprite's children.
     * Also, flipping the texture doesn't alter the anchorPoint.
     * If you want to flip the anchorPoint too, and/or to flip the children too use:
     * sprite->setScaleX(sprite->getScaleX() * -1);
     *
     * @return true if the sprite is flipped horizontally, false otherwise.
     */
    public boolean isFlippedX() {
    	return _sprite.isFlipX();
    }
    
    /**
     * Sets whether the sprite should be flipped horizontally or not.
     *
     * @param flippedX true if the sprite should be flipped horizontally, false otherwise.
     */
    public void setFlippedX(boolean flippedX) {
    	_sprite.setFlip(flippedX, _sprite.isFlipY());
    }

    /** @deprecated Use isFlippedX() instead.*/
    public boolean isFlipX() { return isFlippedX(); };
    /** @deprecated Use setFlippedX() instead */
    public void setFlipX(boolean flippedX) { setFlippedX(flippedX); };

    /**
     * Return the flag which indicates whether the sprite is flipped vertically or not.
     *
     * It only flips the texture of the sprite, and not the texture of the sprite's children.
     * Also, flipping the texture doesn't alter the anchorPoint.
     * If you want to flip the anchorPoint too, and/or to flip the children too use:
     * sprite->setScaleY(sprite->getScaleY() * -1);
     *
     * @return true if the sprite is flipped vertically, false otherwise.
     */
    public final boolean isFlippedY() {
    	return _sprite.isFlipY();
    }
    
    /**
     * Sets whether the sprite should be flipped vertically or not.
     *
     * @param flippedY true if the sprite should be flipped vertically, false otherwise.
     */
    public void setFlippedY(boolean flippedY) {
    	_sprite.setFlip(_sprite.isFlipX(), flippedY);
    }
    
    /** @deprecated Use isFlippedY() instead.*/
    public boolean isFlipY() { return isFlippedY(); }
    /** @deprecated Use setFlippedY() instead. */
    public void setFlipY(boolean flippedY) { setFlippedY(flippedY); }
    
    public void setFlip(boolean flippedX, boolean flippedY) {
    	_sprite.setFlip(flippedX, flippedY);
    }
    
    //
    // Overrides
    //
    /// @{
    /// @name Functions inherited from TextureProtocol.
    /**
    *@code
    *When this function bound into js or lua,the parameter will be changed.
    *In js: var setBlendFunc(var src, var dst).
    *In lua: local setBlendFunc(local src, local dst).
    *@endcode
    */
//    public void setBlendFunc(final BlendFunc &blendFunc) override { _blendFunc = blendFunc; }
    /**
    * @js  NA
    * @lua NA
    */
//    inline final BlendFunc& getBlendFunc() final override { return _blendFunc; }
    /// @}

    /**
     * @js NA
     */
    public final String getDescription() {
    	return "<sprite | Tag = " + _tag + ">";
    }

    /// @{
    /// @name Functions inherited from Node.
    protected void setDirtyRecursively(final boolean b) {
    	_recursiveDirty = b;
    	setDirty(b);
    	
    	for(int i = 0; i < _children.size; ++i) {
    		Node curr = _children.get(i);
    		if(curr instanceof Sprite) {
    			((Sprite)curr).setDirtyRecursively(b);
    		}
    	}
    }
    
    private final void SET_DIRTY_RECURSIVELY() {
//    	if(!_recursiveDirty) {
		_recursiveDirty = true;
		setDirty(true);
		if(_children.size > 0) {
			setDirtyRecursively(true);
		}
//    	}
    }
    
    /**
     * 是否在相机范围内
     * @return true
     */
    public boolean isInsideBounds() {
    	return _insideBounds;
    }
    
    /**
     * 设置是否开启剔除
     * @param useCulling
     */
    public void setUseCulling(boolean useCulling) {
    	this._useCulling = useCulling;
    }
    
    /**设置sprite的显示矩形 
     * 结果不受anchorPoint影响 */
    public final void setRect(float x, float y, float width, float height) {
    	setContentSize(width, height);
    	Vector2 temp = getAnchorPointInPoints();
    	setPosition(x + temp.x, y + temp.y);
    }
    
    public void setContentSize(float width, float height) {
    	super.setContentSize(width, height);
    	SET_DIRTY_RECURSIVELY();
    }
    
    public void setScaleX(float scaleX) {
    	super.setScaleX(scaleX);
    	SET_DIRTY_RECURSIVELY();
    }
    
    public void setScaleY(float scaleY) {
    	super.setScaleY(scaleY);
    	SET_DIRTY_RECURSIVELY();
    }
    
    public void setScale(float scaleX, float scaleY) {
    	super.setScale(scaleX, scaleY);
    	SET_DIRTY_RECURSIVELY();
    }
    
    public void setPosition(final Vector2 pos) {
    	super.setPosition(pos);
    	SET_DIRTY_RECURSIVELY();
    }
    public void setPosition(float x, float y) {
    	super.setPosition(x, y);
    	SET_DIRTY_RECURSIVELY();
    }
    public void setRotation(float rotation) {
    	super.setRotation(rotation);
    	SET_DIRTY_RECURSIVELY();
    }
    public void setScale(float scale) {
    	super.setScale(scale);
    	SET_DIRTY_RECURSIVELY();
    }
    public void setPositionZ(float positionZ) {
    	super.setPositionZ(positionZ);
    	SET_DIRTY_RECURSIVELY();
    }
    public void setAnchorPoint(final Vector2 anchor) {
    	super.setAnchorPoint(anchor);
    	SET_DIRTY_RECURSIVELY();
    }
    public void setAnchorPoint(final float anchorPointX, final float anchorPointY) {
    	super.setAnchorPoint(anchorPointX, anchorPointY);
    	SET_DIRTY_RECURSIVELY();
    }
    
    public void setVisible(boolean bVisible) {
    	super.setVisible(bVisible);
    	SET_DIRTY_RECURSIVELY();
    }
    
    /**剔除在visit中完成 draw绘制即可 */
    public void draw(Renderer renderer, final Matrix4 transform, int flags) {
    	if(isDirty() || ((flags & FLAGS_TRANSFORM_DIRTY) != 0)) {
    		setDirty(false);
    		
    		final float[] val = transform.val;
    		//gdx 中 origin仅与旋转／放缩相关，需要自行减去origin坐标才会修改中心
    		_sprite.setOrigin(_anchorPointInPoints.x, _anchorPointInPoints.y);
    		_sprite.setPosition(val[Matrix4.M03] - _anchorPointInPoints.x, 
    				val[Matrix4.M13] - _anchorPointInPoints.y);
    		
    		//scale
    		float scaleX2 = transform.getScaleXSquared();
    		float scaleY2 = transform.getScaleYSquared();
    		if(scaleX2 != 1.0f || scaleY2 != 1.0f) {
    			_sprite.setScale((float)Math.sqrt(scaleX2), (float)Math.sqrt(scaleY2));
    		}
    		
    		//rotate
    		_sprite.setRotation(_modelRotationZ);
    		
    		//size
//    		System.out.print("Sprite contentSize = " + _contentSize);
    		_sprite.setSize(_contentSize.width, _contentSize.height);
    		
    		//skew 
    		//...
    	}
    	
    	if(_useCulling) {
//			Camera visitingCamera = Camera.getVisitingCamera();
//			Camera defaultCamera = Camera.getDefaultCamera();
//			if(visitingCamera == defaultCamera) {
//				_insideBounds = (((flags & FLAGS_TRANSFORM_DIRTY) != 0) || visitingCamera.isViewProjectionUpdated()) 
//						? renderer.checkVisibility(transform, _contentSize) : _insideBounds;
//			} else {
				_insideBounds = renderer.checkVisibility(transform, _contentSize, _anchorPointInPoints);
//			}
    	} else {
    		_insideBounds = true;
    	}
		
    	if(_insideBounds) {
    		//color
    		_sprite.setColor(_displayColor);
    		renderer.addBatchCommand(_renderCommand);
    	} 
//    	else {
//    		CCLog.engine("Sprite", "cull this sprite");
//    	}
    }
    
    protected void updateColor() {
//    	_sprite.setColor(0, 0, 0, 0);
	}

    public void setOpacityModifyRGB(boolean modify) {
    	if(_opacityModifyRGB != modify) {
    		_opacityModifyRGB = modify;
    		updateColor();
    	}
    }
    
    public boolean isOpacityModifyRGB() {
    	return _opacityModifyRGB;
    }
    /// @}

//    public int getResourceType() { return _fileType; }
//    public final String getResourceName() { return _fileName; }

//CC_finalRUCTOR_ACCESS :
	/**
     * @js ctor
     */
    //ctor>>
    protected Sprite() {
    	_renderCommand = new RenderCommand.BatchCommand(this);
    }

    
    /* Initializes an empty sprite with no parameters. */
    public void init() {
    	if(_sprite == null) {
    		_sprite = new com.badlogic.gdx.graphics.g2d.Sprite();
    	}
//    	initWithTexture(null, Rect.Get(0, 0, 0, 0));
    }

    /**
     * Initializes a sprite with a texture.
     *
     * After initialization, the rect used will be the size of the texture, and the offset will be (0,0).
     *
     * @param   texture    A pointer to an existing Texture2D object.
     *                      You can use a Texture2D object for many sprites.
     * @return  True if the sprite is initialized properly, false otherwise.
     */
    public boolean initWithTexture(Texture texture) {
    	assert texture != null: "Invalid texture for sprite";
    	Rect rect = Rect.Get(0, 0, 0, 0);
    	rect.width = texture.getWidth();
    	rect.height = texture.getHeight();
    	return initWithTexture(texture, rect);
    }

    /**
     * Initializes a sprite with a texture and a rect in points, optionally rotated.
     *
     * After initialization, the offset will be (0,0).
     * @note    This is the designated initializer.
     *
     * @param   texture    A Texture2D object whose texture will be applied to this sprite.
     * @param   rect        A rectangle assigned the contents of texture.
     * @param   rotated     Whether or not the texture rectangle is rotated.
     * @return  True if the sprite is initialized properly, false otherwise.
     */
    public boolean initWithTexture(Texture texture, final Rect rect) {
    	_recursiveDirty = true;
    	setDirty(true);
    	
    	_opacityModifyRGB = true;
    	
    	super.setAnchorPoint(0.5f, 0.5f);
    	super.setContentSize(rect.width, rect.height);
    	setPosition(rect.x + rect.width / 2f, rect.y + rect.height / 2f);
    	if(_sprite == null) {
    		_sprite = new com.badlogic.gdx.graphics.g2d.Sprite(texture);
    	} else {
    		_sprite.setRegion(texture.createTextureRegion());
    	}
    	return true;
    }
    
    /**
     * 使用TextureRegion创建Sprite
     * @param region
     * @return
     */
    public boolean initWithTextureRegion(TextureRegion region) {
    	_recursiveDirty = true;
    	setDirty(true);
    	
    	_opacityModifyRGB = true;
    	
    	super.setAnchorPoint(0.5f, 0.5f);
    	super.setContentSize(region.getRegionWidth(), region.getRegionHeight());
    	
    	if(_sprite != null) {
    		_sprite = new com.badlogic.gdx.graphics.g2d.Sprite(region);
    	} else {
    		_sprite.setRegion(region);
    	}
    	return true;
    }

    /**
     * Initializes a sprite with an SpriteFrame. The texture and rect in SpriteFrame will be applied on this sprite.
     *
     * @param   spriteFrame  A SpriteFrame object. It should includes a valid texture and a rect.
     * @return  True if the sprite is initialized properly, false otherwise.
     */
    public boolean initWithSpriteFrame(TextureRegion spriteFrame) {
    	initWithTextureRegion(spriteFrame);
    	return true;
    }

//    /**
//     * @see #initWithSpriteFrameName(TextureAtlas, String) 
//     */
//    public boolean initWithSpriteFrameName(String spriteFrameName) {
//    	return initWithSpriteFrameName(null, spriteFrameName);
//    }
    
    /**
     * Initializes a sprite with an sprite frame name.
     *
     * A SpriteFrame will be fetched from the SpriteFrameCache by name.
     * If the SpriteFrame doesn't exist it will raise an exception.
     *
     * @param   spriteFrameName  A key string that can fected a valid SpriteFrame from SpriteFrameCache.
     * @return  True if the sprite is initialized properly, false otherwise.
     */
    public boolean initWithSpriteFrameName(final String spriteFrameName) {
//    	if(atlas != null) {
//    		_textureAtlas = atlas;
//    	}
//    	_textureAtlas = 
//    	if(_textureAtlas == null) {
//    		CCLog.error("Sprite", "_textureAtlas is null !");
//    		return false;
//    	}
//    	AtlasRegion ar = _textureAtlas.findRegion(spriteFrameName);
//    	if(ar == null) {
//    		CCLog.error("Sprite", "spriteName not find !" + spriteFrameName);
//    		return false;
//    	}
    	TextureRegion ar = SpriteFrameCache.instance().getSpriteFrameByName(spriteFrameName);
    	if(ar == null) {
    		return false;
    	}
    	TextureRegion spriteFrame = new TextureRegion(ar);
    	return initWithSpriteFrame(spriteFrame);
    }

    /**
     * Initializes a sprite with an image filename.
     *
     * This method will find filename from local file system, load its content to Texture2D,
     * then use Texture2D to create a sprite.
     * After initialization, the rect used will be the size of the image. The offset will be (0,0).
     *
     * @param   filename The path to an image file in local file system.
     * @return  True if the sprite is initialized properly, false otherwise.
     * @lua     init
     */
    public boolean initWithFile(String filename) {
    	Texture t = _director.getTextureCache().addImage(filename);
    	if(t != null) {
    		return initWithTextureRegion(t.createTextureRegion());
    	}
    	return false;
    }

    /**
     * Initializes a sprite with an image filename, and a rect.
     *
     * This method will find filename from local file system, load its content to Texture2D,
     * then use Texture2D to create a sprite.
     * After initialization, the offset will be (0,0).
     *
     * @param   filename The path to an image file in local file system.
     * @param   rect        The rectangle assigned the content area from texture.
     * @return  True if the sprite is initialized properly, false otherwise.
     * @lua     init
     */
    public boolean initWithFile(final String filename, final Rect rect) {
    	Texture t = _director.getTextureCache().addImage(filename);
    	if(t != null) {
    		return initWithTexture(t, rect);
    	}
    	return false;
    }
    
    
   
    @Override
	public void onCommand(PolygonSpriteBatch batch) {
    	if(_shaderProgram == null) {
    		ShaderProgram p = GLProgramCache.getInstance().getSpriteBatchDefaultProgram();
    		if(batch.getShader() != p) {
    			batch.setShader(null);
    		}
    	} else {
    		if(batch.getShader() != _shaderProgram) {
    			batch.setShader(_shaderProgram);
    		}
    	}
    	if(_sprite.getTexture() != null) {
    		_sprite.draw(batch);
    	}
	}
    
    
    final RenderCommand.BatchCommand _renderCommand;
    
//    void updateColor() override;
//    virtual void setTextureCoords(final Rect& rect);
//    virtual void updateBlendFunc();
//    virtual void setReorderChildDirtyRecursively();
//    virtual void setDirtyRecursively(boolean value);


    
    //
    // Data used when the sprite is rendered using a SpriteSheet
    //
    protected com.badlogic.gdx.graphics.g2d.Sprite _sprite;
//    {
//    	_sprite.d
//    }
//    TextureAtlas        _textureAtlas;      /// SpriteBatchNode texture atlas (weak reference)
    int             	_atlasIndex;        /// Absolute (real) Index on the SpriteSheet
//    SpriteBatchNode     _batchNode;         /// Used batch node (weak reference)

    boolean                _dirty;             /// Whether the sprite needs to be updated
    boolean                _recursiveDirty;    /// Whether all of the sprite's children needs to be updated
    boolean                _shouldBeHidden;    /// should not be drawn because one of the ancestors is not visible
//    Mat4              	_transformToBatch;
    
    boolean				_useCulling = true;		//是否开启剔除 			 

    //
    // Data used when the sprite is self-rendered
    //
//    BlendFunc        _blendFunc;            /// It's required for TextureProtocol inheritance
//    Texture2D*       _texture;              /// Texture2D object that is used to render the sprite
//    SpriteFrame     _spriteFrame;
//    TrianglesCommand _trianglesCommand;     ///
//#if CC_SPRITE_DEBUG_DRAW
    DrawNode _debugDrawNode;
//#endif //CC_SPRITE_DEBUG_DRAW
    //
    // Shared data
    //

    // texture
//    Rect _rect;                             	/// Rectangle of Texture2D
//    boolean   _rectRotated;                    /// Whether the texture is rotated

    // Offset Position (used by Zwoptex)
//    Vector2 _offsetPosition;
//    Vector2 _unflippedOffsetPositionFromCenter;

    // vertex coords, texture coords and color info
//    Quaternion _quad;
//    PolygonInfo  _polyInfo;

    // opacity and RGB protocol
    boolean _opacityModifyRGB;

    // image is flipped
//    boolean _flippedX;                         /// Whether the sprite is flipped horizontally or not
//    boolean _flippedY;                         /// Whether the sprite is flipped vertically or not

    boolean _insideBounds;                     /// whether or not the sprite was inside bounds the previous frame

//    String _fileName;
//    int _fileType;

	
}
