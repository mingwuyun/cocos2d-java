package com.cocos2dj.renderer;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.Pixmap.Format;

/**
 * Texture.java
 * <p>
 *
 * openGL纹理 扩展了转换TextureRegion的相关方法<p>
 * 
 * @author Copyright (c) 2015 xu jun
 */
public class Texture extends com.badlogic.gdx.graphics.Texture {
	
	public Texture (final String internalPath) {
		super(internalPath);
	}

	public Texture (FileHandle file) {
		super(file);
	}

	public Texture (FileHandle file, boolean useMipMaps) {
		super(file, useMipMaps);
	}

	public Texture (FileHandle file, Format format, boolean useMipMaps) {
		super(file, format, useMipMaps);
	}

	public Texture (Pixmap pixmap) {
		super(pixmap);
	}

	public Texture (Pixmap pixmap, boolean useMipMaps) {
		super(pixmap, useMipMaps);
	}

	public Texture (Pixmap pixmap, Format format, boolean useMipMaps) {
		super(pixmap, format, useMipMaps);
	}

	public Texture (int width, int height, Format format) {
		super(width, height, format);
	}

	public Texture (TextureData data) {
		super(data);
	}
	
	/*
	 * 工具方法，用来快速生成TextureRegion类
	 */
	/**按指定的行列分割纹理
	 * <b>注意不要反了,先分割的是y方向 </b>
	 * @param rows 分割的行数
	 * @param cols 分割的列数
	 * @return TextureRegion[] 存放顺序为：先左后右，向上后下*/
	public final TextureRegion[] splitRowCol(final int rows,final int cols){
		final int tileWidth=getWidth()/cols;
		final int tileHeight=getHeight()/rows;
		
		TextureRegion[] temp=new TextureRegion[rows*cols];
		int x;
		int y=0;
		for (int row = 0; row < rows; row++, y += tileHeight) {
			x=0;
			for (int col = 0; col < cols; col++, x += tileWidth) {
				temp[row*cols+col] = new TextureRegion(this, x, y, tileWidth, tileHeight);
			}
		}
		
		return temp;
	}
	
	/**按指定的“瓦片”宽与高切分纹理（如从左上角开始切割则offsetX与offsetY均为0）
	 * 如果“瓦片”的宽高不可整除则取整数部分（如100*100切30*30的部分则只会得到9片）
	 * @param offsetX 开始位置x偏移
	 * @param offsetY 开始位置y偏移
	 * @param tileWidth 每一部分的宽
	 * @param tileHeight 每一部分的高
	 * @return TextureRegion[] 存放顺序为：先左后右，向上后下*/
	public final TextureRegion[] split(final int tileWidth,final int tileHeight){
		return split(0,0,tileWidth,tileHeight);
	}
	
	/**按指定的“瓦片”宽与高切分纹理（如从左上角开始切割则offsetX与offsetY均为0）
	 * 如果“瓦片”的宽高不可整除则取整数部分（如100*100切30*30的部分则只会得到9片）
	 * @param offsetX 开始位置x偏移
	 * @param offsetY 开始位置y偏移
	 * @param tileWidth 每一部分的宽
	 * @param tileHeight 每一部分的高
	 * @return TextureRegion[] 存放顺序为：先左后右，向上后下*/
	public final TextureRegion[] split(final int offsetX,final int offsetY,
			final int tileWidth,final int tileHeight){
		
		final int rows=(this.getHeight()-offsetY)/tileHeight; //行数
		final int cols=(this.getWidth()-offsetX)/tileWidth;	//列数
		
		TextureRegion[] temp=new TextureRegion[rows*cols];
		
		int x;
		int y=offsetY;
		
		for (int row = 0; row < rows; row++, y += tileHeight) {
			x=offsetX;
			for (int col = 0; col < cols; col++, x += tileWidth) {
				temp[row*cols+col] = new TextureRegion(this, x, y, tileWidth, tileHeight);
			}
		}
		
		return temp;
	}
	
	/**生成一个包含全部纹理的纹理范围
	 * @return TextureRegion */
	public final TextureRegion createTextureRegion(){
		TextureRegion temp=new TextureRegion(this);
		return temp;
	}
	
	/**根据纹理中范围的最小值坐标以及长与宽创建TextureRegion
	 * @param x
	 * @param y
	 * @param width
	 * @param height */
	public final TextureRegion createTextureRegionWH(int x,int y,int width,int height){
		TextureRegion temp=new TextureRegion(this,x,y,width,height);
		return temp;
	}
	
	/**根据纹理中范围的最小最大值坐标创建TextureRegion
	 * 注意纹理坐标系的y轴正方向朝下
	 * @param x0
	 * @param y0
	 * @param x1
	 * @param y1 */
	public final TextureRegion createTextureRegionXY(int x0,int y0,int x1,int y1){
		TextureRegion temp=new TextureRegion(this, x0, y0, x1-x0, y1-y0);
		return temp;
	}
}
