package com.cocos2dj.platform;

import java.util.HashMap;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.cocos2dj.basic.Engine;
import com.cocos2dj.basic.IDisposable;
import com.cocos2dj.macros.CCLog;

/**
 * FileUtils.java
 * <br>FullFilePath
 * <p>
 * 
 * 删除不必要的方法，仅保留一个主要功能：searchPath
 * 
 * @author Copyright (c) 2017 xu jun
 */
public class FileUtils {
	
	public static final String TAG = "FileUtils";
	
    /**
     *  Gets the instance of FileUtils.
     */
    public static FileUtils getInstance() {
    	if(_instance == null) {
    		_instance = new FileUtils();
			Engine.registerDisposable(new IDisposable() {
				@Override
				public void dispose() {
					_instance = null;
				}
			});
		}
    	return _instance;
	}
    private static FileUtils _instance;
    public static FileUtils instance() {
    	return getInstance();
    }
    
    
    /**
     *  Purges full path caches.
     */
     public void purgeCachedEntries() {
    	 _fullPathCache.clear();
     }
     
    /**
     * 文件完整路径（包含路径和方式）
     * */
    public static final class FullFilePath {
    	public final String 		path;
    	public final FileType		type;
    	public FullFilePath(String path, FileType type) {
    		if(path.length() > 0 && path.charAt(path.length() - 1) != '/') {
    			this.path = path + '/';	//补充分隔符
    		} else {
    			this.path = path;
    		}
    		this.type = type;
    	}
    	
    	public String toString() {
    		return "path = " + path + ", type = " + type;
    	}
    }
    
    /**
     * 获取文件实例
     * @param fileName
     * @return
     */
    public FileHandle getFileHandle(final String fileName) {
    	FullFilePath ffp = fullPathForFileName(fileName);
    	if(ffp == null) {
    		CCLog.engine(TAG, "file not found : " + fileName);
    		return null;
    	}
    	
    	FileHandle fh = Gdx.files.getFileHandle(ffp.path + fileName, ffp.type);
    	
    	return fh;
    }
    
    /**
     * 从搜索队列中找到完整文件路径
     * @param fileName
     * @return
     */
    public FullFilePath fullPathForFileName(String fileName) {
    	FullFilePath ret = _fullPathCache.get(fileName);
    	if(ret != null) {
    		return ret;
    	}
    	
    	for(int i = 0; i < _searchPathArray.size; ++i) {
    		FullFilePath curr = _searchPathArray.get(i);
    		System.out.println(curr);
    		FileHandle fh = Gdx.files.getFileHandle(curr.path + fileName, curr.type);
    		if(fh != null && fh.exists()) {
    			ret = curr;
    			break;
    		}
    	}
    	
    	if(ret == null) {
    		ret = _defaultResRootPath;
    	}
    	
    	_fullPathCache.put(fileName, ret);
    	return ret;
    }

    /**
     * 设置搜索路径 搜索优先级：从前到后
     * @param searchPaths
     */
    public void setSearchPaths(Array<FullFilePath> searchPaths) {
    	_searchPathArray.clear();
    	for(FullFilePath f : searchPaths) {
    		this._searchPathArray.add(f);
    	}
    }

    /**
     * 默认搜索路径
     * Set default resource root path.
     */
    public void setDefaultResourceRootPath( String path, FileType type) {
    	this._defaultResRootPath = new FullFilePath(path, type);
    }

    /**
     * 添加搜索路径（后添加的优先级高）
     * @param path
     * @param type
     */
    public void pushSearchPath(String path, FileType type) {
    	this._searchPathArray.insert(0, new FullFilePath(path, type));
    }
    
    /**
      * Add search path.
      */
    public void addSearchPath( String path,  FileType type, boolean front) {
    	if(front) {
    		pushSearchPath(path, type);
    	} else {
    		this._searchPathArray.add(new FullFilePath(path, type));
    	}
    }

    /**
     *  Gets the array of search paths.
     *
     *  @return The array of search paths.
     *  @see fullPathForFilename( char*).
     *  @lua NA
     */
     public Array<FullFilePath> getSearchPaths() {
    	 return _searchPathArray;
     }

    /**
     *  Gets the writable path. 获取存储路径
     *  @return  The path that can be write/read a file in
     */
     public String getWritablePath() {
    	 return Gdx.files.getExternalStoragePath();
     }

    /**
     *  Checks whether a file exists.
     *
     *  @note If a relative path was passed in, it will be inserted a default root path at the beginning.
     *  @param filename The path of the file, it could be a relative or absolute path.
     *  @return True if the file exists, false if not.
     */
     public boolean isFileExist( String filename) {
    	 FileHandle fh = getFileHandle(filename);
    	 if(fh == null) {
    		 return false;
    	 }
    	 return fh.exists();
     }
     

    /**
     *  Checks whether the path is a directory.
     *
     *  @param dirPath The path of the directory, it could be a relative or an absolute path.
     *  @return True if the directory exists, false if not.
     */
     public boolean isDirectoryExist( String dirPath) {
    	 CCLog.engine(TAG, "not implement");
    	 return false;
     }

    /**
     *  Creates a directory. 创建目录
     *
     *  @param dirPath The path of the directory, it must be an absolute path.
     *  @return True if the directory have been created successfully, false if not.
     */
     public boolean createDirectory( String dirPath) {
    	 CCLog.engine(TAG, "not implement");
    	 return false;
     }

    /**
     *  Removes a directory. 移除目录
     *
     *  @param dirPath  The full path of the directory, it must be an absolute path.
     *  @return True if the directory have been removed successfully, false if not.
     */
     public boolean removeDirectory( String dirPath) {
    	 CCLog.engine(TAG, "not implement");
    	 return false;
     }

    /**
     *  Removes a file.
     *
     *  @param filepath The full path of the file, it must be an absolute path.
     *  @return True if the file have been removed successfully, false if not.
     */
     public boolean removeFile( String filepath) {
    	 CCLog.engine(TAG, "not implement");
    	 return false;
     }

    /**
     *  Renames a file under the given directory.
     *
     *  @param path     The parent directory path of the file, it must be an absolute path.
     *  @param oldname  The current name of the file.
     *  @param name     The new name of the file.
     *  @return True if the file have been renamed successfully, false if not.
     */
     public boolean renameFile( String path,  String oldname,  String name) {
    	 CCLog.engine(TAG, "not implement");
    	 return false;
     }

    /**
     *  Renames a file under the given directory.
     *
     *  @param oldfullpath  The current fullpath of the file. Includes path and name.
     *  @param newfullpath  The new fullpath of the file. Includes path and name.
     *  @return True if the file have been renamed successfully, false if not.
     */
     public boolean renameFile( String oldfullpath,  String newfullpath) {
    	 CCLog.engine(TAG, "not implement");
    	 return false;
     }

    /**
     *  Retrieve the file size.
     *
     *  @note If a relative path was passed in, it will be inserted a default root path at the beginning.
     *  @param filepath The path of the file, it could be a relative or absolute path.
     *  @return The file size.
     */
     public long getFileSize( String filepath) {
    	 FileHandle fh = getFileHandle(filepath);
    	 if(fh != null) {
    		 if(fh.exists()) {
    			 return fh.length();
    		 }
    	 }
    	 return 0;
     }

    /** Returns the full path cache. */
     public HashMap<String, FullFilePath> getFullPathCache()  { return _fullPathCache; }

    /**
     *  The default ructor.
     */
    protected FileUtils() {
    	
    }

    /**
     *  Checks whether a file exists without considering search paths and resolution orders.
     *  @param filename The file (with absolute path) to look up for
     *  @return Returns true if the file found at the given absolute path, otherwise returns false
     */
//     public boolean isFileExistInternal(String filename) {
//    	 
//     }

    /**
     *  Checks whether a directory exists without considering search paths and resolution orders.
     *  @param dirPath The directory (with absolute path) to look up for
     *  @return Returns true if the directory found at the given absolute path, otherwise returns false
     */
//     public boolean isDirectoryExistInternal( String dirPath) {
//    	 
//     }

    /**
     *  Gets full path for filename, resolution directory and search path.
     *
     *  @param filename The file name.
     *  @param resolutionDirectory The resolution directory.
     *  @param searchPath The search path.
     *  @return The full path of the file. It will return an empty string if the full path of the file doesn't exist.
     */
//     public String getPathForFilename( String filename,  String resolutionDirectory,  String searchPath) {
//    	 
//     }

    /**
     *  Gets full path for the directory and the filename.
     *
     *  @note Only iOS and Mac need to override this method since they are using
     *        `[[NSBundle mainBundle] pathForResource: ofType: inDirectory:]` to make a full path.
     *        Other platforms will use the default implementation of this method.
     *  @param directory The directory contains the file we are looking for.
     *  @param filename  The name of the file.
     *  @return The full path of the file, if the file can't be found, it will return an empty string.
     */
//     public String getFullPathForDirectoryAndFilename( String directory,  String filename) {
//    	 CCLog.engine(TAG, "not implement");
//    	 return null;
//     }
     

    /**
     * The vector contains search paths.
     * The lower index of the element in this vector, the higher priority for this search path.
     */
    Array<FullFilePath> _searchPathArray = new Array<>(2);

    /**
     * 默认资源路径
     */
    FullFilePath _defaultResRootPath = new FullFilePath("", FileType.Internal);

    /**
     *  The full path cache. When a file is found, it will be added into this cache.
     *  This variable is used for improving the performance of file search.
     */
    HashMap<String, FullFilePath> _fullPathCache = new HashMap<>();

    /**
     * Writable path.
     */
    String _writablePath;
}
