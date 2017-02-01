package com.cocos2dj.utils;

import com.physicscard2d.framework.common.MathUtils;


/**������������<p>
 * ����Ӧ�����ķ�����ѷ����� �ṩͳһ�ĳ�����ʾ
 * @author xu jun */
public class Direct {
	
	public static final int NULL = 0;
	public static final int RIGHT = 1;
	public static final int RIGHT_UP = 2;
	public static final int UP = 3;
	public static final int LEFT_UP = 4;
	public static final int LEFT = 5;
	public static final int LEFT_DOWN = 6;
	public static final int DOWN = 7;
	public static final int RIGHT_DOWN = 8;
	
	
	
	/**����t1�����t0�ķ�λ���ķ���
	 * @return t1�����t0�ķ�λ Direct��ĳ��� */
	public static final int getDirect4(final V2 t1, final V2 t0){
		final float dx = t1.x-t0.x;
		final float dy = t1.y-t0.y;
		
		if(Math.abs(dx) < Math.abs(dy)){
			if(dy > 0) {return Direct.UP;}
			else {return Direct.DOWN;}
		}
		else{
			if(dx > 0) {return Direct.RIGHT;}
			else {return Direct.LEFT;}
		}
	}
	
	/**����t1�����t0�ķ�λ���˷���
	 * @return t1�����t0�ķ�λ Direct��ĳ��� */
	public static final int getDirect8(final V2 t1, final V2 t0){
		final float dx = t1.x-t0.x;
		final float dy = t1.y-t0.y;
		
		final float tan = dy/dx;
		
		if(dy > 0){
			if(dx > 0){
				if(tan < 0.414)return Direct.RIGHT;
				else if(tan > 2.414)return Direct.UP;
				else return Direct.RIGHT_UP;
			}
			else{
				if(tan > -0.414)return Direct.LEFT;
				else if(tan < -2.414)return Direct.UP;
				else return Direct.LEFT_UP;
			}
		}
		else{
			if(dx>0){
				if(tan > -0.414)return Direct.RIGHT;
				else if(tan < -2.414)return Direct.DOWN;
				else return Direct.RIGHT_DOWN;
			}
			else{
				if(tan < 0.414)return Direct.LEFT;
				else if(tan>2.414)return Direct.DOWN;
				else return Direct.LEFT_DOWN;
			}
		}
	}
	
	/**����t1�����t0�ķ�λ������ֵ��
	 * @return �����t0����ϵ�Ļ��� */
	public static final float getDirectRad(final V2 t1, final V2 t0){
		final float dx = t1.x-t0.x;
		final float dy = t1.y-t0.y;
		
		return MathUtils.atan2(dy, dx);
	}
}
