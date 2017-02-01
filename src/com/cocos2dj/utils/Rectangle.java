package com.stormframework.util;

import java.io.Serializable;

/**矩形 <p>
 * 按照“最小值——直径”的结构记录数据（左下角坐标以及长与宽）<p>
 * 
 * @author xu jun
 * Copyright (c) 2012-2014. All rights reserved. */
public class Rectangle implements Serializable, Shape {
	
	private static final long serialVersionUID = 5733252015138115702L;
	
	public final V2 position = new V2();
	public float width, height;

	public Rectangle () {}

	/**@param x 左下角x坐标
	 * @param y 左下角y坐标
	 * @param width 宽
	 * @param height 高 */
	public Rectangle (float x, float y, float width, float height) {
		position.set(x, y);
		this.width = width;
		this.height = height;
	}

	/**创建一个与给定矩形相同的矩形
	 * @param 需要复制的矩形 */
	public Rectangle (final Rectangle rect) {
		position.set(rect.position);
		width = rect.width;
		height = rect.height;
	}
	
	/**@return 获取该矩形的中心X坐标 */
	public final float getCenterX(){
		return position.x+width/2;
	}
	
	/**@return 获取该矩形的中心Y坐标 */
	public final float getCenterY(){
		return position.y+height/2;
	}
	
	/**设置rectangle的左下角坐标*/
	public final void setXY(final float x,final float y){
		position.set(x, y);
	}
	
	/**设置rectangle的中心位置（更新的是左下角坐标）*/
	public final void setCenterXY(final float x,final float y){
		position.set(x-width/2,y-height/2);
	}
	
	/**设置ractangle的宽与高*/
	public final void setWidthHeight(final float width,final float height){
		this.width=width;
		this.height=height;
	}
	
	public final Rectangle set (final float x, final float y, final float width, final float height) {
		position.set(x, y);
		this.width = width;
		this.height = height;
		return this;
	}
	
	/**复制另一个矩形的参数
	 * @param 作为数据源的矩形 */
	public final void set (final Rectangle rect) {
		position.set(rect.position);
		this.width = rect.width;
		this.height = rect.height;
	}

	/**@param rectangel 需要测试的矩形 {@link Rectangle}.
	 * @return <code>true 另一个rectangle包含在此rectangle中 <br>
	 * false 另一个矩形没有被该rectangle包含 */
	public final boolean contains (final Rectangle rectangle) {
		final float xmin = rectangle.position.x;
		final float xmax = xmin + rectangle.width;

		final float ymin = rectangle.position.y;
		final float ymax = ymin + rectangle.height;

		return ((xmin > position.x && xmin < position.x + width) 
				&& (xmax > position.x && xmax < position.x + width))
				&& ((ymin > position.y && ymin < position.y + height) 
				&& (ymax > position.y && ymax < position.y + height));
	}

	/**@param rectangle {@link Rectangle}
	 * @return <code>true 两个矩形相交 <br>
	 * false 两个矩形不相交 */
	public final boolean overlaps (final Rectangle another) {
		float t;
		if((t = position.x - another.position.x) > another.width || -t > width)
			return false;
		if((t = position.y - another.position.y) > another.height || -t > height)
		    return false;
		return true;
	}
	
	public final boolean overlaps (final float x, final float y, 
			final float width, final float height) {
		float t;
		if((t = position.x - x) > width || -t > this.width)
			return false;
		if((t = position.y - y) > height || -t > this.height)
		    return false;
		return true;
	}

	/**@param x 被测点的x坐标
	 * @param y 被测点的y坐标
	 * @return <code>true 被测点包含在此rectangle中 <br>
	 * false 被测点没有被该rectangle包含 */
	public final boolean contains (final float x, final float y) {
		return position.x < x && position.x + width > x 
				&& position.y < y && position.y + height > y;
	}

	/**融合两个rect（使新的rect可以包含之前的两个rect）
	 * @param rect 另一个rect */
	public final void merge (final Rectangle rect) {
		final float minX = Math.min(position.x, rect.position.x);
		final float maxX = Math.max(position.x + width, rect.position.x + rect.width);
		position.x = minX;
		width = maxX - minX;

		final float minY = Math.min(position.y, rect.position.y);
		final float maxY = Math.max(position.y + height, rect.position.y + rect.height);
		position.y = minY;
		height = maxY - minY;
	}

	public String toString () {
		return position+"," + width + "," + height;
	}
}

