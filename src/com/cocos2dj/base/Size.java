package com.cocos2dj.base;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.NumberUtils;

public final class Size {
	
	public float width;
	public float height;
	
	public Size() {}
	public Size(float width, float height) {
		this.width = width;
		this.height = height;
	}
	public Size(Size other) {
		this.width = other.width;
		this.height = other.height;
	}
	
	public Vector2 vector2() {
		return new Vector2(width, height);
	}
	
	public Size set(Size other) {
		this.width = other.width;
		this.height = other.height;
		return this;
	}
	
	public Size setSize(float width, float height) {
		this.width = width;
		this.height = height;
		return this;
	}
	
	public String toString() {
		return "{w=" + width + ", h=" + height + "}";
	}
	
	public boolean equals(Size obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Size other = (Size)obj;
		if (NumberUtils.floatToIntBits(width) != NumberUtils.floatToIntBits(other.width)) return false;
		if (NumberUtils.floatToIntBits(height) != NumberUtils.floatToIntBits(other.height)) return false;
		return true;
	}

}
