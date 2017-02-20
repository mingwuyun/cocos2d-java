package com.cocos2dj.utils;

import com.badlogic.gdx.math.Matrix4;
import com.cocos2dj.base.AffineTransform;

public class TransformUtils {
	
	/**
	 Conversion between mat4*4 and AffineTransform.
	 @param m The Mat4*4 pointer.
	 @param t Affine transform.
	 */
	public static void CGAffineToGL(final AffineTransform t, final float[] m) {
		// | m[0] m[4] m[8]  m[12] |     | m11 m21 m31 m41 |     | a c 0 tx |
	    // | m[1] m[5] m[9]  m[13] |     | m12 m22 m32 m42 |     | b d 0 ty |
	    // | m[2] m[6] m[10] m[14] | <=> | m13 m23 m33 m43 | <=> | 0 0 1  0 |
	    // | m[3] m[7] m[11] m[15] |     | m14 m24 m34 m44 |     | 0 0 0  1 |
//in libgdx >>>
		// | m[0] m[1] m[2]  m[3] |      | m00 m01 m02 m03 |     | a c 0 tx |
	    // | m[4] m[5] m[6]  m[7] |      | m10 m11 m12 m13 |     | b d 0 ty |
	    // | m[8] m[9] m[10] m[11] | <=> | m20 m21 m22 m23 | <=> | 0 0 1  0 |
	    // | m[12] m[13] m[14] m[15] |   | m30 m31 m32 m33 |     | 0 0 0  1 |
		m[2] = m[3] = m[6] = m[7] = m[8] = m[9] = m[11] = m[14] = 0.0f;
	    m[10] = m[15] = 1.0f;
	    m[0] = t.a; m[4] = t.c; m[12] = t.tx;
	    m[1] = t.b; m[5] = t.d; m[13] = t.ty;
	}
	
	public static void GLToCGAffine(final float[] m, AffineTransform t) {
		t.a = m[0]; t.c = m[4]; t.tx = m[Matrix4.M03];
	    t.b = m[1]; t.d = m[5]; t.ty = m[Matrix4.M13];
	}
}
