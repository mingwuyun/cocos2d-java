package com.cocos2dj.module.base2d.framework.collision;
/**接触链表<p>
 * 
 * 该链表的 {@link #addContact(Contact)} 以及 {@link #removeContact(Contact)}
 * 方法会判断是否重复添加contact 因为这个数据结构十分惧怕重复添加contact破坏遍历<p>
 * 
 * 不过不会判断是否重复删除contact<p>
 * 
 * @author xu jun
 * Copyright (c) 2015. All rights reserved. */
public class ContactList {
	Contact first = null;
	Contact last = null;
	int size;
	
	/**向链表末端添加contact对象 */
	public final void addContact(final Contact c) {
		//判断contact是否重复添加
		if(c.list_prev != null || c.list_next != null) {return;}
		
		c.list_prev = last;
		
		if(last == null) {
			first = c;
		}
		else {
			last.list_next = c;
		}
		
		last = c;
	    ++size;
	}
	
	/**从链表中移除该contact对象 */
	public final void removeContact(final Contact c) {
        final Contact next = c.list_next;
        final Contact prev = c.list_prev;
        
        if (prev == null) {
            first = next;
        } else {
            prev.list_next = next;
            c.list_prev = null;
        }

        if (next == null) {
            last = prev;
        } else {
            next.list_prev = prev;
            c.list_next = null;
        }

        --size;
	}
	
	/**清空链表中的contact对象 */
	public final void clear() {
		for(Contact curr = first; curr != null; ) {
			Contact next = curr.list_next;
			curr.list_next = null;
			curr.list_prev = null;
			curr = next;
		}
		first = last = null;
		size = 0;
	}
	
	public final int size() {
		return size;
	}
	
	public final Contact first() {
		return first;
	}
	
	
	public String toSimpleString() {
		return "size = " + size + "\n";
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("size = ").append(size).append('\n');
		
		Contact temp = first;
		for(int i = 0; i < size; ++i) {
			sb.append("["+i+"] ").append(temp).append('\n');
			temp = temp.list_next;
		}
		return sb.toString();
	}
}
