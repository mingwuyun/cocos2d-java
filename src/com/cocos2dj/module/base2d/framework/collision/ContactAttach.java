package com.cocos2dj.module.base2d.framework.collision;

import com.cocos2dj.module.base2d.framework.PhysicsObject;

/**ContactAttach<p>
 * 接触连接 */
public class ContactAttach {
	
	/**关联的接触*/
	public Contact contact;
	
	/**获取contact的另一个接触物体<p>
	 * 从C2PhysicsObject访问该对象时 other表示与其接触的另一个C2PhysicsObject对象 */
	public PhysicsObject other;
	
	/**链表访问 previous*/
	public ContactAttach prev;
	
	/**链表访问 next*/
	public ContactAttach next;
	
	/**@return true 对应contact有效   */
	public final boolean isContact() {
		return contact.isContacting() || contact.isContacted();
	}
}
