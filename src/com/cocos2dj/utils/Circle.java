package com.stormframework.util;

/**圆形 按圆心——半径的结构储存<p>
 * 
 * @author xu jun
 * Copyright (c) 2012-2014. All rights reserved. */
public class Circle implements Shape {
	/**圆心的坐标*/
	public float x,y;
	/**圆的半径*/
	public float radius;
	
	public Circle() {}
	public Circle(final float x,final float y,final float radius) {
		this.set(x, y, radius);
	}
	public Circle(Circle other) {
		this.set(other);
	}
	
	
	/**设置圆的数据
	 * @param x
	 * @param y
	 * @param radius */
	public final void set(final float x,final float y,final float radius) {
		this.x=x;
		this.y=y;
		this.radius=radius;
	}
	
	/**复制另一圆的数据
	 * @param other */
	public final void set(final Circle other) {
		this.x=other.x;
		this.y=other.y;
		this.radius=other.radius;
	}
	
	
	/**@param x 测试点的x坐标
	 * @param y 测试点的y坐标
	 * @return <code>true 测试点包含在圆内 false 测试点在圆外*/
	public final boolean contains(final float x,final float y) {
		final float dx=x-this.x;
		final float dy=y-this.y;
		return(dx*dx+dy*dy <= radius*radius);
	}
	
}
