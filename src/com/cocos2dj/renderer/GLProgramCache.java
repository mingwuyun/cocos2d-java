package com.cocos2dj.renderer;

import java.util.HashMap;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.cocos2dj.basic.Engine;
import com.cocos2dj.basic.IDisposable;
import com.cocos2dj.macros.CCLog;
import com.cocos2dj.platform.FileUtils;

/**
 * GLProgramCache.java
 * <p>
 * 
 * @author Copyright 2017 xujun
 *
 */
public class GLProgramCache {
	
	public static final String TAG = "GLProgramCache";
	
	private GLProgramCache() {}
	private static GLProgramCache _instance;
	public static GLProgramCache getInstance() {
		if(_instance == null) {
			_instance = new GLProgramCache();
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
	
	/** loads the default shaders */
    public void loadDefaultGLPrograms() {
    	
//    	SpriteBatch.createDefaultShader();
    }
    
    /** reload the default shaders */
    public void reloadDefaultGLPrograms() {
    	
    }

    /** returns a GL program for a given key 
     */
    public ShaderProgram getGLProgram(String key) {
    	return _programs.get(key);
    }
    
    /** adds a GLProgram to the cache for a given name */
    public void addGLProgram(ShaderProgram program, String key) {
    	ShaderProgram ret = _programs.put(key, program);
    	if(ret != null) {
    		ret.dispose();
    		CCLog.engine(TAG, "reset key program : " + key);
    	}
    }
    
    public void removeGLProgram(String key) {
    	ShaderProgram ret = _programs.remove(key);
    	if(ret != null) {
    		ret.dispose();
    	} else {
    		CCLog.error(TAG, "program not found.  key : " + key);
    	}
    }
    
    public ShaderProgram loadGLProgramFromStringAndFile(String vertexShader,
    		String fragmentShaderPath) {
    	return loadGLProgramFromStringAndFile(vertexShader, fragmentShaderPath,
    			fragmentShaderPath);
    }
    
    public ShaderProgram loadGLProgramFromStringAndFile(String vertexShader,
    		String fragmentShaderPath, String key) {
    	return loadGLProgramFromString(vertexShader, 
    			FileUtils.getInstance().getFileHandle(fragmentShaderPath).readString(), key);
    }
    
    public ShaderProgram loadGLProgramFromFile(String vertexShaderPath, String fragmentShaderPath) {
    	return loadGLProgramFromFile(vertexShaderPath, fragmentShaderPath, 
    			vertexShaderPath + fragmentShaderPath);
    }
    
    public ShaderProgram loadGLProgramFromFile(String vertexShaderPath, String fragmentShaderPath, String key) {
    	ShaderProgram ret = _programs.get(key);
    	if(ret != null) {
    		CCLog.engine(TAG, "program exists. call removeGLProgram() first");
    		return ret;
    	}
    	ShaderProgram program = new ShaderProgram(
    			FileUtils.getInstance().getFileHandle(vertexShaderPath), 
    			FileUtils.getInstance().getFileHandle(fragmentShaderPath));
    	addGLProgram(program, key);
    	return program;
    }
    
    public ShaderProgram loadGLProgramFromString(String vertexShader, String fragmentShader, String key) {
    	ShaderProgram ret = _programs.get(key);
    	if(ret != null) {
    		CCLog.engine(TAG, "program exists. call removeGLProgram() first");
    		return ret;
    	}
    	ShaderProgram program = new ShaderProgram(vertexShader, fragmentShader);
    	addGLProgram(program, key);
    	return program;
    }
    
    /** reload default programs these are relative to light */
    public void reloadDefaultGLProgramsRelativeToLights() {
    	
    }
    
    public void loadDefaultShaders() { loadDefaultGLPrograms(); }
    public void reloadDefaultShaders() { reloadDefaultGLPrograms(); }
    public ShaderProgram getShaderProgram(String key) { return getGLProgram(key); }
    public void addShaderProgram(ShaderProgram program, String key) { addGLProgram(program, key); }
    
	public final ShaderProgram getSpriteBatchDefaultProgram() {
		return spriteBatchDefault;
	}
    
    final void loadDefaultGLProgram(ShaderProgram program, int type) {
    	
    }
    
    final void init() {
    	if(spriteBatchDefault == null) {
    		spriteBatchDefault = SpriteBatch.createDefaultShader();
    	}
    }
    
    /**Get macro define for lights in current openGL driver.*/
    final String getShaderMacrosForLight() {
    	return null;
    }
    
    
	//fields>>
    private ShaderProgram spriteBatchDefault; 
	private HashMap<String, ShaderProgram>  _programs = new HashMap<>();
	//fields<<
}
