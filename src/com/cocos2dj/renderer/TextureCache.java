package com.cocos2dj.renderer;

import java.util.HashMap;

import com.badlogic.gdx.files.FileHandle;
import com.cocos2dj.base.Director;
import com.cocos2dj.platform.FileUtils;
import com.cocos2dj.protocol.IFunctionOneArg;

/**
 * TextureCache.java
 * <p>
 * 
 * @author Copyright (c) 2017 xu jun
 */
public class TextureCache {

    /** Returns the shared instance of the cache. */
    public static TextureCache getInstance() {
    	return Director.getInstance().getTextureCache();
    }

    
    //ctor>>
    public TextureCache() {
    	
    }
    //ctor<<
    
    
    /**
     * @js NA
     * @lua NA
     */
    public final String getDescription() {
    	return "<TextureCache | Number of textures = " + _textures.size() + ">";
    }

//	    Dictionary* snapshotTextures();

    /** 
     * 想缓存中添加纹理 以path为键值
    */
    public Texture addImage(String path) {
    	Texture t = _textures.get(path);
    	
    	if(t == null) {
    		FileHandle handle = FileUtils.getInstance().getFileHandle(path);
    		t = new Texture(handle);
    		_textures.put(path, t);
    	}
    	
    	return t;
    }
    

    /** Returns a Texture2D object given a file image.
    * If the file image was not previously loaded, it will create a new Texture2D object and it will return it.
    * Otherwise it will load a texture in a new thread, and when the image is loaded, the callback will be called with the Texture2D as a parameter.
    * The callback will be called from the main thread, so it is safe to create any cocos2d object from the callback.
    * Supported image extensions: .png, .jpg
     @param filepath A null terminated string.
     @param callback A callback function would be invoked after the image is loaded.
     @since v0.8
    */
     public void addImageAsync(String filepath,  IFunctionOneArg<Texture> callback) {
    	
     }
    
    /** Unbind a specified bound image asynchronous callback.
     * In the case an object who was bound to an image asynchronous callback was destroyed before the callback is invoked,
     * the object always need to unbind this callback manually.
     * @param filename It's the related/absolute path of the file image.
     * @since v3.1
     */
     public void unbindImageAsync( String filename) {
    	 
     }
    
    /** Unbind all bound image asynchronous load callbacks.
     * @since v3.1
     */
     public void unbindAllImageAsync() {
    	 
     }

    /** Returns a Texture2D object given an Image.
    * If the image was not previously loaded, it will create a new Texture2D object and it will return it.
    * Otherwise it will return a reference of a previously loaded image.
    * @param key The "key" parameter will be used as the "key" for the cache.
    * If "key" is nil, then a new texture will be created each time.
    */
//    public Texture addImage(Image image,  String key) {
    	
//    }
    
//    CC_DEPRECATED_ATTRIBUTE Texture2D* addUIImage(Image image,  String key) { return addImage(image,key); }

    /** Returns an already created texture. Returns nil if the texture doesn't exist.
    @param key It's the related/absolute path of the file image.
    @since v0.99.5
    */
    public Texture getTextureForKey(String key) {
    	return _textures.get(key);
    }
    
//    CC_DEPRECATED_ATTRIBUTE Texture2D* textureForKey( String key)  { return getTextureForKey(key); }

    /** Reload texture from the image file.
    * If the file image hasn't loaded before, load it.
    * Otherwise the texture will be reloaded from the file image.
    * @param fileName It's the related/absolute path of the file image.
    * @return True if the reloading is succeed, otherwise return false.
    */
    public boolean reloadTexture(String fileName) {
    	
    	return false;
    }

    /** Purges the dictionary of loaded textures.
    * Call this method if you receive the "Memory Warning".
    * In the short term: it will free some resources preventing your app from being killed.
    * In the medium term: it will allocate more resources.
    * In the long term: it will be the same.
    */
    public void removeAllTextures() {
    	
    }

    /** Removes unused textures.
    * Textures that have a retain count of 1 will be deleted.
    * It is convenient to call this method after when starting a new Scene.
    * @since v0.8
    */
    public void removeUnusedTextures() {
    	
    }

    /** Deletes a texture from the cache given a texture.
    */
    public void removeTexture(Texture texture) {
    	
    }

    /** Deletes a texture from the cache given a its key name.
    @param key It's the related/absolute path of the file image.
    @since v0.99.4
    */
    public void removeTextureForKey( String key) {
    	
    }

    /** Output to CCLOG the current contents of this TextureCache.
    * This will attempt to calculate the size of each texture, and the total texture memory in use.
    *
    * @since v1.0
    */
    public String getCachedTextureInfo() {
    	
    	return null;
    }

    //Wait for texture cache to quit before destroy instance.
    /**Called by director, please do not called outside.*/
    public void waitForQuit() {
    	
    }

    /**
     * Get the file path of the texture
     *
     * @param texture A Texture2D object pointer.
     *
     * @return The full path of the file.
     */
    public String getTextureFilePath(Texture texture) {
    	
    	return null;
    }

    /** Reload texture from a new file.
    * This function is mainly for editor, won't suggest use it in game for performance reason.
    *
    * @param srcName Original texture file name.
    * @param dstName New texture file name.
    *
    * @since v3.10
    */
    public void renameTextureWithKey( String srcName,  String dstName) {
    	
    }


	private void addImageAsyncCallBack(float dt) {
    	
    }
	
	private void loadImage() {
    	
    }
	
//	private void parseNinePatchImage(Image image, Texture texture,  String path) {
//    	
//  }
    
	
//protected:
//    struct AsyncStruct;
    
//    std::thread* _loadingThread;

//    std::deque<AsyncStruct*> _asyncStructQueue;
//    std::deque<AsyncStruct*> _requestQueue;
//    std::deque<AsyncStruct*> _responseQueue;

//    std::mutex _requestMutex;
//    std::mutex _responseMutex;
//    std::condition_variable _sleepCondition;

    boolean _needQuit;

    int _asyncRefCount;
    
    HashMap<String, Texture>	_textures = new HashMap<>();
//    std::unordered_map<String, Texture2D*> _textures;
};

//#if CC_ENABLE_CACHE_TEXTURE_DATA

//class VolatileTexture
//{
//    typedef enum {
//        kInvalid = 0,
//        kImageFile,
//        kImageData,
//        kString,
//        kImage,
//    }ccCachedImageType;
//
//private:
//    VolatileTexture(Texture2D *t);
//    /**
//     * @js NA
//     * @lua NA
//     */
//    ~VolatileTexture();
//
//protected:
////	    friend class  VolatileTextureMgr;	//libgdx do it
//    Texture2D *_texture;
//    
//    Image *_uiImage;
//
//    ccCachedImageType _cashedImageType;
//
//    void *_textureData;
//    int  _dataLen;
//    Size _textureSize;
//    Texture2D::PixelFormat _pixelFormat;
//
//    String _fileName;
//
//    boolean                      _hasMipmaps;
//    Texture2D::TexParams      _texParams;
//    String               _text;
//    FontDefinition            _fontDefinition;
//}
