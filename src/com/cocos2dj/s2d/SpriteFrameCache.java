package com.cocos2dj.s2d;

import java.util.HashMap;
import java.util.HashSet;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.cocos2dj.basic.Engine;
import com.cocos2dj.basic.IDisposable;
import com.cocos2dj.macros.CCLog;
import com.cocos2dj.renderer.Texture;

/**
 * SpriteFrameCache.java
 * <p>
 * @author Copyright (c) 2017 xu jun
 */
public class SpriteFrameCache {
	private static SpriteFrameCache _instance;
	private SpriteFrameCache() {}
	public static SpriteFrameCache instance() {
		if(_instance == null) {
			_instance = new SpriteFrameCache();
			Engine.registerDisposable(new IDisposable() {
				@Override
				public void dispose() {
					SpriteFrameCache.destroyInstance();
				}
			});
		}
		return _instance;
	}
	
    /** Returns the shared instance of the Sprite Frame cache.
     *
     * @return The instance of the Sprite Frame Cache.
     */
    public static SpriteFrameCache getInstance() {
    	return instance();
    }

    /** Destroys the cache. It releases all the Sprite Frames and the retained instance.
	 * @js NA
     */
    public static void destroyInstance() {
    	_instance._atlases.clear();
    	_instance._spriteFrames.clear();
    	_instance._loadedFileNames.clear();
    	_instance = null;
    }
    
    public void removeSpriteFramesFromTextureAtlas(String filePath) {
    	TextureAtlas atlas = _atlases.get(filePath);
    	
    	if(atlas == null) {
    		CCLog.debug(this.getClass(), "remove atlas not found : " + filePath);
    		return;
    	}
    	
    	Array<AtlasRegion> rs = atlas.getRegions();
    	for(AtlasRegion r : rs) {
    		_spriteFrames.remove(r.name);
    	}
    }
    
    public final TextureAtlas findTextureAtlas(String filePath) {
    	return _atlases.get(filePath);
    }
    
    /**
     * 读取gdx textureAtlas文件
     * @param filePath atlas 文件路径 也可以自定义
     * @param atlas
     */
    public void addSpriteFrameWithTextureAtlas(String filePath, TextureAtlas atlas) {
    	if(_atlases.containsKey(filePath)) {
    		CCLog.debug(this.getClass(), "file loaded : " + filePath);
    		return;
    	}
    	
    	_atlases.put(filePath, atlas);
    	
    	Array<AtlasRegion> rs = atlas.getRegions();
    	for(AtlasRegion r : rs) {
    		TextureRegion ret = _spriteFrames.put(r.name, r);
    		if(ret != null) {
    			CCLog.debug(this.getClass(), "region name exists : " + r.name);
    		}
    	}
    }
    
    
    
    
    
    
//    public void addSpriteFrameWith
    /** Adds multiple Sprite Frames from a plist file.
     * A texture will be loaded automatically. The texture name will composed by replacing the .plist suffix with .png.
     * If you want to use another texture, you should use the addSpriteFramesWithFile( String plist,  String textureFileName) method.
     * @js addSpriteFrames
     * @lua addSpriteFrames
     *
     * @param plist Plist file name.
     */
    public void addSpriteFramesWithFile(String plist) {
    	
    }

    /** Adds multiple Sprite Frames from a plist file. The texture will be associated with the created sprite frames.
     @since v0.99.5
     * @js addSpriteFrames
     * @lua addSpriteFrames
     *
     * @param plist Plist file name.
     * @param textureFileName Texture file name.
     */
    public void addSpriteFramesWithFile( String plist,  String textureFileName) {
    	
    }

    /** Adds multiple Sprite Frames from a plist file. The texture will be associated with the created sprite frames. 
     * @js addSpriteFrames
     * @lua addSpriteFrames
     *
     * @param plist Plist file name.
     * @param texture Texture pointer.
     */
    public void addSpriteFramesWithFile(String plist, Texture texture) {
    	
    }

    /** Adds multiple Sprite Frames from a plist file content. The texture will be associated with the created sprite frames. 
     * @js NA
     * @lua addSpriteFrames
     *
     * @param plist_content Plist file content string.
     * @param texture Texture pointer.
     */
    public void addSpriteFramesWithFileContent(String plist_content, Texture texture) {
    	
    }

    /** Adds an sprite frame with a given name.
     If the name already exists, then the contents of the old name will be replaced with the new one.
     *
     * @param frame A certain sprite frame.
     * @param frameName The name of the sprite frame.
     */
    public void addSpriteFrame(TextureRegion frame,  String frameName) {
    	_spriteFrames.put(frameName, frame);
    }

    /** Check if multiple Sprite Frames from a plist file have been loaded.
    * @js NA
    * @lua NA
    *
    * @param plist Plist file name.
    * @return True if the file is loaded.
    */
    public boolean isSpriteFramesWithFileLoaded( String plist) {
    	
    	return false;
    }

    /** Purges the dictionary of loaded sprite frames.
     * Call this method if you receive the "Memory Warning".
     * In the short term: it will free some resources preventing your app from being killed.
     * In the medium term: it will allocate more resources.
     * In the long term: it will be the same.
     */
    public void removeSpriteFrames() {
    	
    }

    /** Removes unused sprite frames.
     * Sprite Frames that have a retain count of 1 will be deleted.
     * It is convenient to call this method after when starting a new Scene.
	 * @js NA
     */
    public void removeUnusedSpriteFrames() {
    	
    }

    /** Deletes an sprite frame from the sprite frame cache. 
     *
     * @param name The name of the sprite frame that needs to removed.
     */
    public void removeSpriteFrameByName( String name) {
    	
    }

    /** Removes multiple Sprite Frames from a plist file.
    * Sprite Frames stored in this file will be removed.
    * It is convenient to call this method when a specific texture needs to be removed.
    * @since v0.99.5
    *
    * @param plist The name of the plist that needs to removed.
    */
    public void removeSpriteFramesFromFile( String plist) {
    	
    }

    /** Removes multiple Sprite Frames from a plist file content.
    * Sprite Frames stored in this file will be removed.
    * It is convenient to call this method when a specific texture needs to be removed.
    *
    * @param plist_content The string of the plist content that needs to removed.
    * @js NA
    */
    public void removeSpriteFramesFromFileContent( String plist_content) {
    	
    }

    /** Removes all Sprite Frames associated with the specified textures.
     * It is convenient to call this method when a specific texture needs to be removed.
     * @since v0.995.
     *
     * @param texture The texture that needs to removed.
     */
    public void removeSpriteFramesFromTexture(Texture texture) {
    	
    }

    /** Returns an Sprite Frame that was previously added.
     If the name is not found it will return nil.
     You should retain the returned copy if you are going to use it.
     * @js getSpriteFrame
     * @lua getSpriteFrame
     *
     * @param name A certain sprite frame name.
     * @return The sprite frame.
     */
    public TextureRegion getSpriteFrameByName( String name) {
    	return _spriteFrames.get(name);
    }

    public boolean reloadTexture( String plist) {
    	
    	return false;
    }


    /*Adds multiple Sprite Frames with a dictionary. The texture will be associated with the created sprite frames.
     */
//    public void addSpriteFramesWithDictionary(ValueMap dictionary, Texture2D *texture);
    
    /*Adds multiple Sprite Frames with a dictionary. The texture will be associated with the created sprite frames.
     */
//    void addSpriteFramesWithDictionary(ValueMap dictionary,  String texturePath);
    
    /** Removes multiple Sprite Frames from Dictionary.
    * @since v0.99.5
    */
//    void removeSpriteFramesFromDictionary(ValueMap dictionary);

    /** Parses list of space-separated integers */
//    void parseIntegerList( String string, std::vector<int> res);
    
    /** Configures PolygonInfo class with the passed sizes + triangles */
//    void initializePolygonInfo( Size textureSize,
//                                Size spriteSize,
//                                std::vector<int> vertices,
//                                std::vector<int> verticesUV,
//                                std::vector<int> triangleIndices,
//                               PolygonInfo polygonInfo);

//    void reloadSpriteFramesWithDictionary(ValueMap dictionary, Texture2D *texture);

    HashMap<String, TextureRegion> _spriteFrames = new HashMap<>();
//    ValueMap _spriteFramesAliases;
    HashSet<String>  _loadedFileNames = new HashSet<>();
    HashMap<String, TextureAtlas> _atlases = new HashMap<>();
    
};
