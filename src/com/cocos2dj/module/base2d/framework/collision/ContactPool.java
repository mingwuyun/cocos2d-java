package com.cocos2dj.module.base2d.framework.collision;

import com.cocos2dj.module.base2d.jbox2d.pooling.MutableStack;

/**ContactPool <p>
 * 接触缓存池 
 * 初始值为16个之后每当获取contact数量超过缓存数之后 池容量增长1倍  <br>
 * <code>get()</code>方法用于获取缓存contact<br>
 * <code>cycle(Contact contact)</code>方法用于将contact送回缓存中 */
public class ContactPool {
	
	private static final int INIT_CAPACITY = 16;
	private static ContactPool cp;
	
	public static final ContactPool getContactPool(){
		if(cp == null){
			cp = new ContactPool();
		}
		return cp;
	}
	
	private MutableStack<Contact, Contact> pool;
	
	/**重新分配内存 */
	public final void reset() {
		pool.reset(INIT_CAPACITY);
	}
	
	public ContactPool() {
		pool = new MutableStack<Contact,Contact>(Contact.class, INIT_CAPACITY);
	}
	
	/**从池中获取一个contact对象
	 * @return contact */
	public final Contact get() {
		return pool.pop();
	}
	
	public final void cycle(final Contact contact){
		contact.clearAdvanceFlag();
		contact.setContacted(false);
		pool.push(contact);
	}
	
	
	public String toString() {
		return pool.toString();
	}
}
