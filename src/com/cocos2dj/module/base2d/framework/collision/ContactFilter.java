package com.cocos2dj.module.base2d.framework.collision;

import com.cocos2dj.module.base2d.framework.common.MathUtils;

/**
 * ContactFilter<p>
 * 碰撞过滤器 功能有:<br>
 * 1、判断接触是否产生 如果bit占位与filter占位一致则会产生相交<br>
 * 2、判断接触的物体是否修正 如果bit占位与filter占位一致则不会进行碰撞修正<br>
 * 效果等同与设置isBody为false
 * 
 * @author xujun
 */
public final class ContactFilter {   
	
	public static final int BIT_1 = 1;
	public static final int BIT_2 = 1 << 1;
	public static final int BIT_3 = 1 << 2;
	public static final int BIT_4 = 1 << 3;
	public static final int BIT_5 = 1 << 4;
	public static final int BIT_6 = 1 << 5;
	public static final int BIT_7 = 1 << 6;
	public static final int BIT_8 = 1 << 7;
	
	/**碰撞位*/
	private int contactBit;
	/**碰撞过滤*/
	private int contactFilter;
	/**穿越位*/
	private int passBit;
	/**穿越过滤*/
	private int passFilter = 0;
//	/**穿越位*/
//	private int passBitXZ;
//	/**穿越过滤*/
//	private int passFilterXZ = 0;

	
	
	public ContactFilter(){
		contactBit=0xffff;
		contactFilter=0xffff;
	}
	
	public ContactFilter(int contactBit,int contactFilter){
		this.contactBit=contactBit;
		this.contactFilter=contactFilter;
	}
	
	public ContactFilter(int contactBit,int contactFilter,int passBit,int passFilter){
		this.contactBit=contactBit;
		this.contactFilter=contactFilter;
		this.passBit=passBit;
		this.passFilter=passFilter;
	}

	public ContactFilter(final ContactFilter filter){
		this.setContactFilter(filter);
	}

	
	final static int computeBit(int pos) {
		int r = 1;
		if(pos == 0) {return 0;}
		r = r << pos;
//		for(int i = 1; i < pos; ++i) {
//			r <<= 1;
//		}
		return r;
	}
	
	public static void main(String[] a) {
//		System.out.println(setContactBitPos(-1));
//		System.out.println(setContactBitPos(0));
//		System.out.println(setContactBitPos(8));
//		System.out.println(setContactBitPos(3));
//		System.out.println(setContactBitPos(33));
//		ContactFilter cf = new ContactFilter();
//		cf.setContactFilterPos(0);
//		cf.setContactFilterPos(1);
//		cf.setContactFilterPos(2);
//		cf.setContactFilterPos(3);
//		cf.setContactFilterPos(1,2,3);
	}
	
	/**设置
	 * 
	 * @param pos
	 */
	public final void setContactBitPos(int pos) {
		pos = (int) MathUtils.clamp(pos, 0, 32);
		contactBit = computeBit(pos);
	}
	
	public final void setContactFilterPos(int...filters) {
		int r = 0;
		for(int i = 0; i < filters.length; ++i) {
			int pos = filters[i];
			pos = (int) MathUtils.clamp(pos, 0, 32);
			pos = computeBit(pos);
			r |= pos;
		}
		contactFilter = r;
	}
	
	public final void setPassBitPos(int pos) {
		pos = (int) MathUtils.clamp(pos, 0, 32);
		passBit = computeBit(pos);
	}
	
//	public final void setPassBitPosXZ(int pos) {
//		pos = (int) MathUtils.clamp(pos, 0, 32);
//		passBitXZ = computeBit(pos);
//	}
	
	public final void setPassFilterPos(int...filters) {
		int r = 0;
		for(int i = 0; i < filters.length; ++i) {
			int pos = filters[i];
			pos = (int) MathUtils.clamp(pos, 0, 32);
			pos = computeBit(pos);
			r |= pos;
		}
		passFilter = r;
	}
	
//	public final void setPassFilterPosXZ(int...filters) {
//		int r = 0;
//		for(int i = 0; i < filters.length; ++i) {
//			int pos = filters[i];
//			pos = (int) MathUtils.clamp(pos, 0, 32);
//			pos = computeBit(pos);
//			r |= pos;
//		}
//		passFilterXZ = r;
//	}
	
	/**由另一个filter设置该filter的参数
	 * @param filter 碰撞过滤器*/
	public void setContactFilter(final ContactFilter filter){
		this.contactBit=filter.contactBit;
		this.contactFilter=filter.contactFilter;
		this.passBit=filter.passBit;
		this.passFilter=filter.passFilter;
	}
	
	/**设置碰撞位与过滤 <br>
	 * @param contactBit 注意必须是2的整数次幂 
	 * @param contactFilter 将所有需要碰的物体的contactBit相加作为contactFilter */
	public final void setContactBitFilter(final int contactBit, final int contactFilter){
		this.contactBit=contactBit;
		this.contactFilter=contactFilter;
	}
	
	/**设置碰撞位<br>
	 * 注意必须是2的整数次幂
	 * @param contactBit */
	public final void setContactBit(final int contactBit){
		this.contactBit=contactBit;
	}
	
	/**设置碰撞过滤<br>
	 * 请自行计算   将所有需要碰的物体的contactBit相加作为contactFilter
	 * @param contactFilter */
	public final void setContactFilter(final int contactFilter){
		this.contactFilter=contactFilter;
	}
	
//	public final void setPassBitXZ(int passBitXZ) {
//		this.passBitXZ = passBitXZ;
//	}
//	
//	public final void setPassBitFilterXZ(int passFilterXZ) {
//		this.passFilterXZ = passFilterXZ;
//	}
	
	/** 设置穿透位
	 * @param passBit */
	public final void setPassBit(int passBit){
		this.passBit = passBit;
	}
	
	/** 设置穿透过滤
	 * @param passFilter */
	public void setPassFilter(int passFilter){
		this.passFilter = passFilter;
	}

	/**测试过滤<br>
	 * 返回true则可以碰撞
	 * 返回false不可发生碰撞
	 * @param filter
	 * @return */
	public final boolean testFilter(final ContactFilter filter){
		return ((contactBit & filter.contactFilter) != 0);
	}
	
	/**测试穿透属性
	 * true 可以穿透
	 * false 不可穿透
	 * @param filter
	 * @return */
	public final boolean testPass(final ContactFilter filter){
		return ((passBit & filter.passFilter) != 0);
	}
	
//	public final boolean testPassXZ(ContactFilter filter) {
//		return ((passBitXZ & filter.passFilterXZ) != 0);
//	}

}