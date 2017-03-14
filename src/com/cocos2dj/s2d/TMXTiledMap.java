package com.cocos2dj.s2d;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.cocos2dj.base.Director;
import com.cocos2dj.base.Director.MATRIX_STACK_TYPE;
import com.cocos2dj.base.Size;
import com.cocos2dj.macros.CC;
import com.cocos2dj.macros.CCLog;
import com.cocos2dj.renderer.Renderer;
import com.cocos2dj.renderer.RenderCommand.DrawCommand;
import com.cocos2dj.renderer.RenderCommand.DrawCommandCallback;

/**
 * TMXTiledMap.java
 * <p>
 * 
 * @author Copyright(c) 2017 xujun
 */
public class TMXTiledMap extends Node implements DrawCommandCallback {
	
	/** Possible orientations of the TMX map. */
	public static enum TiledMapType {
	    /** Orthogonal orientation. */
	    OrientationOrtho,

	    /** Hexagonal orientation. */
	    OrientationHex,

	    /** Isometric orientation. */
	    OrientationIso,
	    
	    /** Isometric staggered orientation. */
	    OrientationStaggered,
	};
	
	
	/** Creates a TMX Tiled Map with a TMX file.
    *
    * @return An autorelease object.
    */
   public static TMXTiledMap create(String tmxFile) {
	   TMXTiledMap ret = new TMXTiledMap();
	   ret.initWithTMXFile(tmxFile);
	   return ret;
   }
   
   public static TMXTiledMap create(FileHandle tmxFile) {
	   TMXTiledMap ret = new TMXTiledMap();
	   ret.initWithTMXFile(tmxFile);
	   return ret;
   }
   

   TiledMap 		_tileMap;
   TiledMapRenderer _tileMapRender;
   Size				_mapSize = new Size();
   int 				_tileWidthCount, _tileHeightCount;
   Size				_tileSize = new Size();
   TiledMapType 	_mapOrientation;
   DrawCommand		_drawCommand;
   float _width, _height;
   
   /** Return the FastTMXLayer for the specific layer. 
    * 
    * @return Return the FastTMXLayer for the specific layer.
    */
   public MapLayer getLayer(String layerName) {
	   return _tileMap.getLayers().get(layerName);
   }

   public MapLayer getLayer(int index) {
	   return _tileMap.getLayers().get(index);
   }
   
   public MapLayers getLayers() {
	   return _tileMap.getLayers();
   }
   
   /**直接获取 tileLayer对象*/
   public TiledMapTileLayer getTileLayer(int index) {
	   return (TiledMapTileLayer) _tileMap.getLayers().get(index);
   }
   
   /**直接获取 tileLayer对象*/
   public TiledMapTileLayer getTileLayer(String layerName) {
	   return (TiledMapTileLayer) _tileMap.getLayers().get(layerName);
   }
   
   /** Return the TMXObjectGroup for the specific group. 
    * 
    * @return Return the TMXObjectGroup for the specific group.
    */
   public MapObjects getObjectGroup(String groupName) {
	   return _tileMap.getLayers().get(groupName).getObjects();
   }

   /** Return the value for the specific property name.
    *
    * @return Return the value for the specific property name.
    */
   public Object getProperty( String propertyName) {
	   return _tileMap.getProperties().get(propertyName);
   }
   
   public MapProperties getProperties() {
	   return _tileMap.getProperties();
   }

   /** The map's size property measured in tiles. 
    *
    * @return The map's size property measured in tiles.
    */
   public final Size getMapSize()  { return _mapSize;};
   
   /** Set the map's size property measured in tiles.
    *
    * @param mapSize The map's size property measured in tiles.
    */
   public final void setMapSize( Size mapSize) { _mapSize = mapSize; };

   public final int getWidthTileCount() {return _tileWidthCount;}
   public final int getHeightTileCount() {return _tileHeightCount;}
   
   /** The tiles's size property measured in pixels.
    *
    * @return The tiles's size property measured in pixels.
    */
   public final Size getTileSize()  { return _tileSize; };
   
   /** Set the tiles's size property measured in pixels. 
    *
    * @param tileSize The tiles's size property measured in pixels.
    */
   public void setTileSize( Size tileSize) { _tileSize = tileSize; };

   /** Get map orientation. 
    *
    * @return The map orientation.
    */
   public TiledMapType getMapOrientation()  { return _mapOrientation; };
   
   /** Set map orientation. 
    *
    * @param mapOrientation The map orientation.
    */
//   public void setMapOrientation(int mapOrientation) { _mapOrientation = mapOrientation; };
   

   public String getDescription()  {
	   return "<FastTMXTiledMap | Tag = " + _tag + " Layers = " + getLayers().getCount();
   }

   /**
    * @js ctor
    */
   public TMXTiledMap() {
	   _drawCommand = new DrawCommand(this);
	   _width = Director.getInstance().getVisibleSize().width;
	   _height = Director.getInstance().getVisibleSize().height;
   }

   
   private void _initProperties() {
	   MapProperties ps = _tileMap.getProperties();
	   Object temp = null;
	   if((temp = ps.get("width")) != null) {
		   _tileWidthCount = (int) temp;
	   }
	   if((temp = ps.get("height")) != null) {
		   _tileHeightCount = (int) temp;
	   }
	   if((temp = ps.get("tilewidth")) != null) {
		   _tileSize.width = (int) temp;
	   }
	   if((temp = ps.get("tileheight")) != null) {
		   _tileSize.height = (int) temp;
	   }
	   if((temp = ps.get("orientation")) != null) {
		   String orientation = (String) temp;
		   switch(orientation) {
		   case "isometric":
			   _mapOrientation = TiledMapType.OrientationIso;
			   break;
		   case "orthogonal":
			   _mapOrientation = TiledMapType.OrientationOrtho;
			   break;
		   default:
			   CCLog.error("TMXTiledMap", "orientation type not found : " + temp);
		   }
	   }
	   
	   _mapSize.width = _tileWidthCount * _tileSize.width;
	   _mapSize.height = _tileHeightCount * _tileSize.height;
	   
   }
   
   /** initializes a TMX Tiled Map with a TMX file */
   public boolean initWithTMXFile(String tmxFile) {
	   if(_tileMap != null) {
		   _tileMap.dispose();
	   }
	   TmxMapLoader newLoader = new TmxMapLoader() {
		   public FileHandle resolve(String fileName) {
			   return CC.File(fileName);
		   }
	   };
	   _tileMap = newLoader.load(tmxFile);
	   _initProperties();
	   _tileMapRender = new OrthogonalTiledMapRenderer(_tileMap, 1f);
	   return true;
   }
   
   public boolean initWithTMXFile(FileHandle tmxFile) {
	   if(_tileMap != null) {
		   _tileMap.dispose();
	   }
	   TmxMapLoader newLoader = new TmxMapLoader() {
		   public FileHandle resolve(String fileName) {
			   return tmxFile;
		   }
	   };
	   _tileMap = newLoader.load(null);
	   return true;
   }


   public void draw(Renderer renderer, final Matrix4 transform, int flags) {
	   renderer.addDrawCommand(_drawCommand);
   }
   
   @Override
   public void onExit() {
	   super.onExit();
	   _tileMap.dispose();
	   _tileMap = null;
   }
   
   @Override
   public void onCommand() {
		Camera camera = Camera.getVisitingCamera();
		_tileMapRender.setView(Director.getInstance().getMatrix(MATRIX_STACK_TYPE.MATRIX_STACK_PROJECTION), 
				camera.getPositionX(), camera.getPositionY(), 
				_width, _height);
		_tileMapRender.render();
//		_tileMapRender.ren
   }

}
