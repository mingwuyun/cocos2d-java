package com.cocos2dj.renderer;

/**
 * TextureRegion.java
 * <p>
 * 
 * 纹理区域（TextureRegion）是纹理的一个矩形部分。该区域中坐标系统的原点在
 * 左上角（与android的canvas坐标系统一样，与openGL坐标系统y轴相反）
 * 
 * @author Copyright (c) 2015 xu jun
 */
public class TextureRegion extends com.badlogic.gdx.graphics.g2d.TextureRegion {

public TextureRegion () { }

	
	public TextureRegion(com.badlogic.gdx.graphics.g2d.TextureRegion region) {
		super(region);
	}
	
	/**以给定纹理的原始尺寸构建范围*/
	public TextureRegion (final Texture texture) {
		super(texture);
	}

	/**以给定的Texture，在其坐标系内从(0,0)点开始以指定的长宽构建TextureRegion 
	 * @param texture 源Texture
	 * @param width TextureRegion的宽（int）如果需要翻转可以设为负值
	 * @param height TextureRegion的高（int）如果需要翻转可以设为负值 */
	public TextureRegion (final Texture texture, final int width, final int height) {
		super(texture, width, height);
	}

	/**以给定的Texture，在其坐标系内构建TextureRegion 
	 * @param texture 源Texture
	 * @param x 在以Texture的左上角为原点的坐标系中，TextureRegion的x坐标
	 * @param y 在以Texture的左上角为原点的坐标系中，TextureRegion的y坐标
	 * @param width TextureRegion的宽（int）如果需要翻转可以设为负值
	 * @param height TextureRegion的高（int）如果需要翻转可以设为负值 */
	public TextureRegion (final Texture texture, final int x, final int y, final int width, final int height) {
		super(texture, x, y, width, height);
	}

	/**以给定的Texture，在其坐标系内构建TextureRegion 
	 * @param texture 源Texture
	 * @param u 在以Texture的左上角为原点的坐标系中，TextureRegion的u坐标
	 * @param v 在以Texture的左上角为原点的坐标系中，TextureRegion的v坐标	 
	 * @param u2 在以Texture的左上角为原点的坐标系中，TextureRegion的u2坐标
	 * @param v2 在以Texture的左上角为原点的坐标系中，TextureRegion的vv坐标 */
	public TextureRegion (final Texture texture, final float u, final float v, final float u2, final float v2) {
		super(texture, u, v, u2, v2);
	}

	/**构造与给定范围一样的TextureRegion*/
	public TextureRegion (final TextureRegion region) {
		super(region);
	}

	/**以给定的TextureRegion作为Texture，在其坐标系内构建TextureRegion 
	 * @param region 源TextureRegion
	 * @param x 以指定TextureRegion的左上角为原点的坐标系中，新TextureRegion的x坐标
	 * @param y 以指定TextureRegion的左上角为原点的坐标系中，新TextureRegion的y坐标
	 * @param width 新TextureRegion的宽（int）如果需要翻转可以设为负值
	 * @param height新TextureRegion的高（int）如果需要翻转可以设为负值 */
	public TextureRegion (final TextureRegion region, final int x, final int y, final int width, final int height) {
		setRegion(region, x, y, width, height);
	}

	/**将gdx的region包装成SE的region */
	public void warp(com.badlogic.gdx.graphics.g2d.TextureRegion region) {
		super.setRegion(region);
	}
	
	/**将范围设置为texture的范围*/
	public void setRegion (final Texture texture) {
		
		setRegion(0, 0, texture.getWidth(), texture.getHeight());
	}

	/**构建指定TextureRegion的复本 */
	public final void setRegion (final TextureRegion region) {
		setTexture(region.getTexture());
		setRegion(region.getU(), region.getV(), region.getU2(), region.getV2());
	}

	/**以给定的TextureRegion作为Texture，在其坐标系内构建TextureRegion 
	 * @param region 源TextureRegion
	 * @param x 以指定TextureRegion的左上角为原点的坐标系中，新TextureRegion的x坐标
	 * @param y 以指定TextureRegion的左上角为原点的坐标系中，新TextureRegion的y坐标
	 * @param width 新TextureRegion的宽（int）如果需要翻转可以设为负值
	 * @param height新TextureRegion的高（int）如果需要翻转可以设为负值 */
	public void setRegion (final TextureRegion region, final int x, final int y, final int width, final int height) {
		setTexture(region.getTexture());
		setRegion(region.getRegionX() + x, region.getRegionY() + y, width, height);
	}

	/**翻转纹理
	 * @param x 值为true时纹理会相对y轴翻转（水平翻转）
	 * @param y 值为true是纹理会相对x轴翻转（垂直翻转）*/
	public void flip (final boolean x, final boolean y) {
		super.flip(x, y);
	}

	/**设置纹理区域中的偏移量。 所呈现的新区域是该区域的平移所给偏移量后的区域。
	 * <b>注意移动时区域应该包含在纹理范围内
	 * 
	 * @param xAmount 水平偏移量（相对纹理长度的百分比）
	 * @param yAmount 垂直偏移量（相对纹理长度的百分比）
	 * <b>注意这个量与绘图坐标的y相反所以向上移动应该为负数 */
	public void scroll (float xAmount, float yAmount) {
		super.scroll(xAmount, yAmount);
	}

	/**按指定的宽与高切切分图片 
	 * 注意如果指定的宽高不能被整除texture的宽与高整除则会舍去多出的部分
	 * 
	 * @param tileWidth 
	 * @param tileHeight 
	 * @return 一个二维数组 排列是 [row][column]. */
	public TextureRegion[][] split2 (final int tileWidth, final int tileHeight) {
		int x = getRegionX();
		int y = getRegionY();
		int width = getRegionWidth();
		int height = getRegionHeight();

		if (width < 0) {
			x = x - width;
			width = -width;
		}

		if (height < 0) {
			y = y - height;
			height = -height;
		}

		int rows = height / tileHeight;
		int cols = width / tileWidth;

		int startX = x;
		TextureRegion[][] tiles = new TextureRegion[rows][cols];
		for (int row = 0; row < rows; row++, y += tileHeight) {
			x = startX;
			for (int col = 0; col < cols; col++, x += tileWidth) {
				tiles[row][col] = new TextureRegion((Texture) getTexture(), x, y, tileWidth, tileHeight);
			}
		}

		return tiles;
	}

	/**按指定的宽与高切切分图片 
	 * 注意如果指定的宽高不能被整除texture的宽与高整除则会舍去多出的部分
	 * 
	 * @param tileWidth 
	 * @param tileHeight 
	 * @return 一个二维数组 排列是 [row][column]. */
	public static final TextureRegion[][] split2 (Texture texture, int tileWidth, int tileHeight) {
		TextureRegion region = new TextureRegion(texture);
		return region.split2(tileWidth, tileHeight);
	}
	
	/**按指定的宽与高切切分图片 
	 * 注意如果指定的宽高不能被整除texture的宽与高整除则会舍去多出的部分
	 * 
	 * @param tileWidth 
	 * @param tileHeight 
	 * @return 一个一维数组 排列是 自上而下， 自左向右 */
	public TextureRegion[] split1 (final int tileWidth, final int tileHeight) {
		int x = getRegionX();
		int y = getRegionY();
		int width = getRegionWidth();
		int height = getRegionHeight();

		if (width < 0) {
			x = x - width;
			width = -width;
		}

		if (height < 0) {
			y = y - height;
			height = -height;
		}

		int rows = height / tileHeight;
		int cols = width / tileWidth;

		int startX = x;
		TextureRegion[] tiles = new TextureRegion[rows*cols];
		for (int row = 0; row < rows; row++, y += tileHeight) {
			x = startX;
			for (int col = 0; col < cols; col++, x += tileWidth) {
				tiles[row*cols+col] = new TextureRegion((Texture) getTexture(), x, y, tileWidth, tileHeight);
			}
		}

		return tiles;
	}
	
	/**按指定的宽与高切切分图片 
	 * 注意如果指定的宽高不能被整除texture的宽与高整除则会舍去多出的部分
	 * 
	 * @param rows
	 * @param cols 
	 * @return 一个一维数组 排列是 自上而下， 自左向右 */
	public TextureRegion[] splitRowCol (final int rows, final int cols) {
		int x = getRegionX();
		int y = getRegionY();
		int width = getRegionWidth();
		int height = getRegionHeight();

		if (width < 0) {
			x = x - width;
			width = -width;
		}

		if (height < 0) {
			y = y - height;
			height = -height;
		}

		int tileHeight = height/rows;
		int tileWidth = width/cols;
//		int rows = height / tileHeight;
//		int cols = width / tileWidth;

		int startX = x;
		TextureRegion[] tiles = new TextureRegion[rows*cols];
		for (int row = 0; row < rows; row++, y += tileHeight) {
			x = startX;
			for (int col = 0; col < cols; col++, x += tileWidth) {
				tiles[row*cols+col] = new TextureRegion((Texture) getTexture(), x, y, tileWidth, tileHeight);
			}
		}

		return tiles;
	}

	/**按指定的宽与高切切分图片 
	 * 注意如果指定的宽高不能被整除texture的宽与高整除则会舍去多出的部分
	 * 
	 * @param tileWidth 
	 * @param tileHeight 
	 * @return 一个一维数组 排列是自上而下 自左向右  */
	public static final TextureRegion[] split (Texture texture, int tileWidth, int tileHeight) {
		TextureRegion region = new TextureRegion(texture);
		return region.split1(tileWidth, tileHeight);
	}
	
}
