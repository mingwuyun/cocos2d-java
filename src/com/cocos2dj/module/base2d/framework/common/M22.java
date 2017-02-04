package com.cocos2dj.module.base2d.framework.common;

import com.badlogic.gdx.math.Vector2;

/**
 * 二阶矩阵 用于旋转
 * {col1.x, col2.x}
 * {col1.y, col2.y}
 */
public final class M22 {
	
	public final Vector2 col1 = new Vector2();
	public final Vector2 col2 = new Vector2();
	
	/** M22(this)=M22(zero)*/
	public M22() {}

	/**M22(this)={c1.x,c2.x}<br>
	 * __________{c1.y,c2.y}
	 * @param c1
	 * @param c2 */
	public M22(final Vector2 c1, final Vector2 c2) {
		col1.set(c1);
		col2.set(c2);
	}

	/**M22(this)={col1x,xol2x}<br>
	 * __________{col1y,col2y}
	 * @param col1x
	 * @param col2x
	 * @param col1y
	 * @param col2y */
	public M22(final float col1x, final float col2x, final float col1y, final float col2y) {
		col1.set(col1x, col1y);
		col2.set(col2x, col2y);
	}
	
	/**M22(this)=M22(m)
	 * @param m */
	public M22(final M22 m){
		col1.set(m.col1);
		col2.set(m.col2);
	}
	
	public String toString() {
		String s = "";
		s += "{"+col1.x+","+col2.x+"}\n";
		s += "{"+col1.y+","+col2.y+"}";
		return s;
	}

	public final M22 clone() {
		return new M22(col1, col2);
	}
	
	
	/**M22(this)={cos(angle,-sin(angle))}<br>
	 * __________{sin(angle,cos(angle))}
	 * @param angle */
	public final void set(final float angle) {
		final float c = MathUtils.cos(angle);
		final float s = MathUtils.sin(angle);
		col1.x = c; col2.x = -s;
		col1.y = s; col2.y = c;
	}
	
	/**row1={1,0}<br>
	 * row2={0,1} */
	public final void setIdentity() {
		col1.x = 1.0f;
		col2.x = 0.0f;
		col1.y = 0.0f;
		col2.y = 1.0f;
	}

	/**row1={0,0}<br>
	 * row2={0,0} */
	public final void setZero() {
		col1.x = 0.0f;
		col2.x = 0.0f;
		col1.y = 0.0f;
		col2.y = 0.0f;
	}

	/**row1={c1.x,c2.x}<br>
	 * row2={c1.y,c2.y}
	 * @param c1 Column 1
	 * @param c2 Column 2 */
	public final void set(final Vector2 c1, final Vector2 c2) {
	/*************************************************************/
		col1.x = c1.x;
		col2.x = c2.x;
		col1.y = c1.y;
		col2.y = c2.y;
	}
	
	/**M22(this)=M22(m)
	 * @param m
	 * @return M22(this) */
	public final M22 set(final M22 m) {
		col1.x = m.col1.x;
		col1.y = m.col1.y;
		col2.x = m.col2.x;
		col2.y = m.col2.y;
		return this;
	}

	/**M22(this)={col1x,col2x}<br>
	 * __________{col1y,col2y}
	 * @param col1x
	 * @param col2x
	 * @param col1y
	 * @param col2y
	 * @return M22(this) */
	public final M22 set(final float col1x, final float col2x, final float col1y, final float col2y) {
		col1.x = col1x;
		col1.y = col1y;
		col2.x = col2x;
		col2.y = col2y;
		return this;
	}
	
	/**M22(new)=M22(this)+M22(another)
	 * @param another
	 * @return M22(new) */
	public final M22 add(final M22 another) {
		final M22 temp = new M22();
		temp.col1.x = col1.x + another.col1.x;
		temp.col1.y = col1.y + another.col1.y;
		temp.col2.x = col2.x + another.col2.x;
		temp.col2.y = col2.y + another.col2.y;
		return temp;
	}

	/**M22(this)=M22(thisBefore)+M22(another)
	 * @param another
	 * @return M22(this) */
	public final M22 addThis(final M22 another) {
		col1.x += another.col1.x;
		col1.y += another.col1.y;
		col2.x += another.col2.x;
		col2.y += another.col2.y;
		return this;
	}
	
	/**M22(new)=-M22(this)
	 * @return M22(new) */
	public final M22 negate() {
		final M22 temp=new M22();
		V2.negate(col1);
		V2.negate(col2);
		return temp;
	}
	
	/**M22(this)=-M22(thisBefore)
	 * @return M22(this) */
	public final M22 negateThis() {
		V2.negate(col1);
		V2.negate(col2);
		return this;
	}
	
	/**M22(this)xM22(new)=M22(Identity)
	 * @return M22(new) */
	public final M22 invert() {
		final float a = col1.x, b = col2.x, c = col1.y, d = col2.y;
		final M22 another = new M22();
		float det = a * d - b * c;
		det = 1.0f / det;
		another.col1.x = det * d;
		another.col2.x = -det * b;
		another.col1.y = -det * c;
		another.col2.y = det * a;
		return another;
	}
	
	/**M22(thisBefore)xM22(this)=M22(Identity)
	 * @return M22(this) */
	public final M22 invertThis() {
		final float a = col1.x, b = col2.x, c = col1.y, d = col2.y;
		float det = a * d - b * c;
		det = 1.0f / det;
		col1.x = det * d;
		col2.x = -det * b;
		col1.y = -det * c;
		col2.y = det * a;
		return this;
	}

	/**row1={|x1|,|x2|}<br>
	 * row2={|y1|,|y2|}
	 * @return M22(new) */
	public final M22 abs() {
		return new M22(Math.abs(col1.x),Math.abs(col2.x),
		               Math.abs(col1.y),Math.abs(col2.y));
	}

	/**row1={|x1|,|x2|}<br>
	 * row2={|y1|,|y2|}
	 * @return M22(this) */
	public final void absThis(){
		V2.abs(col1);
		V2.abs(col2);
	}

	/**M22(new)=[M22(this)]T
	 * @return M22(new) */
	public final M22 trans(){
		final M22 temp=new M22();
		temp.col1.x=col1.x;
		temp.col2.x=col1.y;
		temp.col1.y=col2.x;
		temp.col2.y=col2.y;
		return temp;
	}
	
	/**M22(this)=[M22(thisBefore)]T
	 * @return M22(this) */
	public final M22 transThis(){
		final float tempy1=col2.x;
		col2.x=col1.y;
		col1.y=tempy1;
		return this;
	}
	
	/**M22(this)Vector2(v)=Vector2(new)
	 * @param v
	 * @return Vector2(new) */
	public final Vector2 mul(final Vector2 v) {
		return new Vector2(col1.x * v.x + col2.x * v.y, col1.y * v.x + col2.y
		                * v.y);
	}

	/**M22(this)M22(another)=M22(new)
	 * @param another
	 * @return M22(new) */
	public final M22 mul(final M22 another) {
	/*************************************************************/
		final M22 temp = new M22();
		temp.col1.x = col1.x * another.col1.x + col2.x * another.col1.y;
		temp.col1.y = col1.y * another.col1.x + col2.y * another.col1.y;
		temp.col2.x = col1.x * another.col2.x + col2.x * another.col2.y;
		temp.col2.y = col1.y * another.col2.x + col2.y * another.col2.y;
		return temp;
	}
	
	/**M22(thisBefore)M22(another)=M22(this)
	 * @param another
	 * @return M22(this) */
	public final M22 mulThis(final M22 another){
		final float tempx1 = col1.x * another.col1.x + col2.x * another.col1.y;
		final float tempx2 = col1.x * another.col2.x + col2.x * another.col2.y;
		final float tempy2 = col1.y * another.col2.x + col2.y * another.col2.y;
	    final float tempy1 = col1.y * another.col1.x + col2.y * another.col1.y;
		col1.x = tempx1;
		col1.y = tempy1;
		col2.x = tempx2;
		col2.y = tempy2;
		return this;
	}

	/**M22(this)=M22(thisBefore)[M22(another)]T
	 * @param another
	 * @return M22(this) */
	public final M22 mulTrans(final M22 another) {
		final M22 temp = new M22();
		temp.col1.x = V2.dot(this.col1, another.col1);
		temp.col2.x = V2.dot(this.col1, another.col2);
		temp.col1.y = V2.dot(this.col2, another.col1);
		temp.col2.y = V2.dot(this.col2, another.col2);
		return temp;
	}
	
	/**M22(new)=M22(this)[M22(another)]T
	 * @param another
	 * @return M22(new) */
	public final M22 mulTransThis(final M22 another){
		final float tempx1 = col1.x * another.col1.x + col1.y * another.col1.y;
		final float tempx2 = col1.x * another.col2.x + col1.y * another.col2.y;
		final float tempy1 = col2.x * another.col1.x + col2.y * another.col1.y;
		final float tempy2 = col2.x * another.col2.x + col2.y * another.col2.y;
		col1.x = tempx1;
		col2.x = tempx2;
		col1.y = tempy1;
		col2.y = tempy2;
		return this;
	}

	/**Vector2(new)=[M22(this)]T[Vector2(v)]
	 * @param v
	 * @return Vector2(new) */
	public final Vector2 mulTrans(final Vector2 v) {
		return new Vector2((v.x * col1.x + v.y * col1.y), (v.x * col2.x + v.y * col2.y));
	}

	/**M22(this)Vector2(new)=Vector2(another)
	 * @return Vector2(new) */
	public final Vector2 solve(final Vector2 b) {
		float det = col1.x * col2.y - col2.x * col1.y;
		det = 1.0f / det;
		final Vector2 v = new Vector2( det * (col2.y * b.x - col2.x * b.y),det * (col1.x * b.y - col1.y * b.x) );
		return v;
	}

	/**Vector2(r)=M22(m)Vector2(v)
	 * @param m
	 * @param v
	 * @param r
	 * @return Vector2(r) */
	public final static Vector2 mul(final M22 m, final Vector2 v, final Vector2 r) {
		r.set(m.col1.x * v.x + m.col2.x * v.y, m.col1.y * v.x + m.col2.y * v.y);
		return r;
	}
	
	/**Vector2(result)=M22(m)Vector2(v)
	 * @param m
	 * @param v
	 * @param result */
	public final static void mulOut(final M22 m, final Vector2 v, final Vector2 result){
		final float x = m.col1.x * v.x + m.col2.x * v.y;
		final float y = m.col1.y * v.x + m.col2.y * v.y;
		result.set(x, y);
	}

	/**M22(m3)=M22(m1)M22(m2)
	 * @param m1
	 * @param m2
	 * @param m3
	 * @return M22(m3) */
	public final static M22 mul(final M22 m1, final M22 m2, final M22 m3){
		m3.col1.x = m1.col1.x * m2.col1.x + m1.col2.x * m2.col1.y;
		m3.col1.y = m1.col1.y * m2.col1.x + m1.col2.y * m2.col1.y;
		m3.col2.x = m1.col1.x * m2.col2.x + m1.col2.x * m2.col2.y;
		m3.col2.y = m1.col1.y * m2.col2.x + m1.col2.y * m2.col2.y;
		return m3;
	}

	/**Vector2(r)=M22(m)Vector2(v)
	 * @param m
	 * @param v
	 * @param r
	 * @return Vector2(r) */
	public final static Vector2 mulTrans(final M22 m, final Vector2 v, final Vector2 r) {
		r.set((v.x * m.col1.x + v.y * m.col1.y), (v.x * m.col2.x + v.y * m.col2.y));
		return r;
	}


	/**M22(m1)=M22(m1)M22(m2)
	 * @param A
	 * @param B
	 * @return M22(m1) */
	public final static M22 mulTrans(final M22 m1, final M22 m2){
		return m1.mulTrans(m2);
	}
	
	/**row1={cos(angle),-sin(angle)}<br>
	 * row2={sin(angle), cos(angle)}
	 * @param angle
	 * @param m
	 * @return M22(m) */
	public final static M22 createRotationalTransform(final float angle, final M22 m){
		final float c = (float) MathUtils.cos(angle);
		final float s = (float) MathUtils.sin(angle);
		m.col1.x = c;
		m.col2.x = -s;
		m.col1.y = s;
		m.col2.y = c;
		return m;
	}
	
	/**M22(new)={scale,00000}<br>
	 * _________{00000,scale}
	 * @param scale
	 * @return M22(new) */
	public final static M22 createScaleTransform(final float scale, final M22 m){
		m.col1.x = scale;
		m.col2.y = scale;
		return m;
	}
	
	/**M22(new)={scaleX,000000}<br>
	 * _________{000000,scaleY}
	 * @param scaleX
	 * @param scaleY
	 * @param m
	 * @return M22(m) */
	public final static M22 createScaleTransform(float scaleX, float scaleY, final M22 m){
		m.col1.x = scaleX;
		m.col2.y = scaleY;
		return m;
	}
	
}
