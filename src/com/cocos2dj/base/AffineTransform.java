package com.cocos2dj.base;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * AffineTransform.java
 * <p>
 * <pre>
 * Affine transform
 * a   b    0
 * c   d    0
 * tx  ty   1
 * 
 * Identity
 * 1   0    0
 * 0   1    0
 * 0   0    1
</pre>
*/
public class AffineTransform {
	
    public float a, b, c, d;
    public float tx, ty;
    
    public boolean equals(Object obj) {
    	if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		return AffineTransformEqualToTransform(this, (AffineTransform) obj);
    }
    
    public static final AffineTransform IDENTITY = new AffineTransform();

	/**@}*/

	/**Make affine transform.*/
    private static final AffineTransform stackInstance = new AffineTransform();
    public static final AffineTransform getStackAffineTransform() {
    	return stackInstance;
    }
    
	public static final AffineTransform AffineTransformMakeOut(float a, float b, float c, float d, float tx, float ty, AffineTransform out) {
		out.a = a;
		out.b = b;
		out.c = c;
		out.d = d;
		out.tx = tx;
		out.ty = ty;
		return out;
	}
	
	static final Vector2 stackVec2 = new Vector2();
	/**Multiply point (x,y,1) by a  affine transform.
	 * @return <b>stack object </b>*/
	public static final Vector2 PointApplyAffineTransform(final Vector2 point, final AffineTransform t) {
		 Vector2 p = stackVec2;
		 p.x = (t.a * point.x + t.c * point.y + t.tx);
		 p.y = (t.b * point.x + t.d * point.y + t.ty);
		 return p;
	 }
	
	public static final Vector2 PointApplyAffineTransform(final float pointX, final float pointY, final AffineTransform t) {
		 Vector2 p = stackVec2;
		 p.x = (t.a * pointX + t.c * pointY + t.tx);
		 p.y = (t.b * pointX + t.d * pointY + t.ty);
		 return p;
	 } 

	/**Multiply size (width,height,0) by a  affine transform.*/
	static final Size stackSize = new Size();
	public static final Size SizeApplyAffineTransform(final Size size, final AffineTransform t) {
		Size s = stackSize;
		s.width = (t.a * size.width + t.c * size.height);
		s.height = (t.b * size.width + t.d * size.height);
		return s;
	}
	/**Make identity affine transform.*/
	public static AffineTransform AffineTransformMakeIdentity() {
		AffineTransform ret = new AffineTransform();
		AffineTransformMakeOut(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, ret);
		return ret;
	}
	
	static final float[]	stackPoints_1 = new float[4];
	static final float[]	stackPoints_2 = new float[4];
	/**Transform Rect, which will transform the four vertices of the point.*/
	public static final Rect RectApplyAffineTransform(final Rect rect, final AffineTransform anAffineTransform, final Rect out) {
		float top    = rect.getMinY();
	    float left   = rect.getMinX();
	    float right  = rect.getMaxX();
	    float bottom = rect.getMaxY();
	    
	    float[] pointsX = stackPoints_1;
	    float[] pointsY = stackPoints_2;
	    
	    Vector2 topLeft = PointApplyAffineTransform(left, top, anAffineTransform);
	    pointsX[0] = topLeft.x; pointsY[0] = topLeft.y;
	    Vector2 topRight = PointApplyAffineTransform(right, top, anAffineTransform);
	    pointsX[1] = topRight.x; pointsY[1] = topRight.y;
	    Vector2 bottomLeft = PointApplyAffineTransform(left, bottom, anAffineTransform);
	    pointsX[2] = bottomLeft.x; pointsY[2] = bottomLeft.y;
	    Vector2 bottomRight = PointApplyAffineTransform(right, bottom, anAffineTransform);
	    pointsX[3] = bottomRight.x; pointsY[3] = bottomRight.y;
	    
	    float minX = pointsX[0], maxX = pointsX[0];
	    float minY = pointsY[0], maxY = pointsY[0];
	    for(int i = 1; i < 4; ++i) {
	    	float x = pointsX[i];
	    	float y = pointsY[i];
	    	minX = x < minX ? x : minX;
	    	maxX = x > maxX ? x : maxX;
	    	minY = y < minY ? y : minY;
	    	maxY = y > maxY ? y : maxY;
	    }
	    out.set(minX, minY, maxX - minX, maxY - minY);
	    return out;
	}
	
	static final Vector3 stackVec3 = new Vector3();
	/**@{
	 Transform Vector2 and Rect by Matrix4.
	 */
	 public static final Rect RectApplyTransform(final Rect rect, final Matrix4 transform, final Rect out) {
		 float top    = rect.getMinY();
		    float left   = rect.getMinX();
		    float right  = rect.getMaxX();
		    float bottom = rect.getMaxY();
		    
		    float[] pointsX = stackPoints_1;
		    float[] pointsY = stackPoints_2;
		    
		    Vector3 p = stackVec3;
		    p.set(left, top, 0).mul(transform);
		    pointsX[0] = p.x; pointsY[0] = p.y;
		    p.set(right, top, 0).mul(transform);
		    pointsX[1] = p.x; pointsY[1] = p.y;
		    p.set(left, bottom, 0).mul(transform);
		    pointsX[2] = p.x; pointsY[2] = p.y;
		    p.set(right, bottom, 0).mul(transform);
		    pointsX[3] = p.x; pointsY[3] = p.y;
		    
		    float minX = pointsX[0], maxX = pointsX[0];
		    float minY = pointsY[0], maxY = pointsY[0];
		    for(int i = 1; i < 4; ++i) {
		    	float x = pointsX[i];
		    	float y = pointsY[i];
		    	minX = x < minX ? x : minX;
		    	maxX = x > maxX ? x : maxX;
		    	minY = y < minY ? y : minY;
		    	maxY = y > maxY ? y : maxY;
		    }
		    out.set(minX, minY, maxX - minX, maxY - minY);
		    return out;
	 }
	 
	 public static Vector2 PointApplyTransform(final Vector2 point, final Matrix4 transform, Vector2 out) {
		 stackVec3.set(point, 0);
		 stackVec3.mul(transform);
		 out.x = stackVec3.x;
		 out.y = stackVec3.y;
		 return out;
	 }
	 
	 public static Vector2 PointApplyTransform(final float pointX, final float pointY, final Matrix4 transform, Vector2 out) {
		 stackVec3.set(pointX, pointY,0);
		 stackVec3.mul(transform);
		 out.x = stackVec3.x;
		 out.y = stackVec3.y;
		 return out;
	 }
	 
	/**@}*/
	/**
	 Translation, equals
	 1  0  1
	 0  1  0   * affine transform
	 tx ty 1
	 */
	 public static final AffineTransform AffineTransformTranslate(final AffineTransform t, float tx, float ty, AffineTransform out) {
		 return AffineTransformMakeOut(t.a, t.b, t.c, t.d, t.tx + t.a * tx + t.c * ty, t.ty + t.b * tx + t.d * ty, out);
	 }
	 
	/**
	 * //use degress
	 Rotation, equals
	 cos(angle)   sin(angle)   0
	 -sin(angle)  cos(angle)   0  * AffineTransform
	 0            0            1
	 */
	 public static final AffineTransform AffineTransformRotate(final AffineTransform t, float anAngle, AffineTransform out) {
		 float sine = MathUtils.sinDeg(anAngle);
	    float cosine = MathUtils.cosDeg(anAngle);

	    return AffineTransformMakeOut(t.a * cosine + t.c * sine,
	                                    t.b * cosine + t.d * sine,
	                                    t.c * cosine - t.a * sine,
	                                    t.d * cosine - t.b * sine,
	                                    t.tx,
	                                    t.ty, out);
	 }
	/**
	 Scale, equals
	 sx   0   0
	 0    sy  0  * affineTransform
	 0    0   1
	 */
	 public static final AffineTransform AffineTransformScale(final AffineTransform t, float sx, float sy, AffineTransform out) {
		 return AffineTransformMakeOut(t.a * sx, t.b * sx, t.c * sy, t.d * sy, t.tx, t.ty
				 , out);
	 }
	/**Concat two affine transform, t1 * t2*/
	 public static final AffineTransform AffineTransformConcat(final AffineTransform t1, final AffineTransform t2, final AffineTransform out) {
		 return AffineTransformMakeOut(t1.a * t2.a + t1.b * t2.c, t1.a * t2.b + t1.b * t2.d, //a,b
                 t1.c * t2.a + t1.d * t2.c, t1.c * t2.b + t1.d * t2.d, //c,d
                 t1.tx * t2.a + t1.ty * t2.c + t2.tx,                  //tx
                 t1.tx * t2.b + t1.ty * t2.d + t2.ty, 					//ty
                 out);
	 }
	 
	/**Compare affine transform.*/
	 public static final boolean AffineTransformEqualToTransform(final AffineTransform t1, final AffineTransform t2) {
		 return (t1.a == t2.a && t1.b == t2.b && t1.c == t2.c && t1.d == t2.d && t1.tx == t2.tx && t1.ty == t2.ty); 
	 }
	 
	/**Get the inverse of affine transform.*/
	 public static final AffineTransform AffineTransformInvert(final AffineTransform t, final AffineTransform out) {
		 float determinant = 1 / (t.a * t.d - t.b * t.c);
		 return AffineTransformMakeOut(determinant * t.d, -determinant * t.b, -determinant * t.c, determinant * t.a,
		                            determinant * (t.c * t.ty - t.d * t.tx), determinant * (t.b * t.tx - t.a * t.ty),
		                            out);
	 }
}
