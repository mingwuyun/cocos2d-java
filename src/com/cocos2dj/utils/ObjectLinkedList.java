package com.cocos2dj.utils;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * ObjectLinkedList.java
 * <br>ILinkedObject
 * <p>
 * 
 * 链表 需要成员继承 {@link ILinkedObject } 其他同普通链表
 * <pre>
 * class A implements ILinkedObject<Element> {
 	A _next;
	A _prev;
	public void _set_next(A next) {_next = next;}
	public void _set_prev(A prev) {_prev = prev;}
	public A get_next() {return _next;}
	public A get_prev() {return _prev;}
 * }
 * </pre>
 * 
 * @author Copyright (c) 2017 xu jun
 *
 * @param <T>
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class ObjectLinkedList<T> implements Iterable<T> {
	
//	static class X implements ILinkedObject<X> {
//		X prev;
//		X next;
//		
//		final int tag;
//		public X(int tag) {this.tag = tag;}
//		
//		@Override
//		public void _set_next(X next) {this.next = next;}
//		@Override
//		public void _set_prev(X prev) {this.prev = prev;}
//		@Override
//		public X get_next() {return next;}
//		@Override
//		public X get_prev() {return prev;}
//		
//	}
//	
//	public static void main(String[] args) {
//		ObjectLinkedList<X> xs = new ObjectLinkedList<X>();
//		for(int i = 0; i < 20; i++) {
//			xs.addFirst(new X(i));
////			xs.addLast(new X(i));
//		}
//		xs.removeLast();
//		xs.removeFirst();
//		xs.removeLast();
//		xs.removeFirst();
//		X first = xs.first();
//		System.out.println("tag = "+first.tag);
//		X temp = first.get_next();
//		while(temp != null) {
//			System.out.println("tag = "+temp.tag);
//			temp = temp.get_next();
//		}
//	}
	
	public interface ILinkedObject<T extends ILinkedObject<?>> {
		
		public void _set_next(T next);
		
		public void _set_prev(T prev);
		
		public T get_next();
		
		public T get_prev();
	
	}
	
	private static class ObjectLinkedListIterator<T extends ILinkedObject<?>> implements Iterator<T>, Iterable<T> {
		private final ObjectLinkedList<T> list;
		private final boolean allowRemove;
		private ILinkedObject currentObject, prevObject;
		boolean valid = true;
		public ObjectLinkedListIterator (ObjectLinkedList<T> list) {
			this(list, true);
		}

		public ObjectLinkedListIterator (ObjectLinkedList<T> list, boolean allowRemove) {
			this.list = list;
			this.allowRemove = allowRemove;
			currentObject = this.list.first();
		}

		public boolean hasNext () {
			if (!valid) {
				throw new RuntimeException("#iterator() cannot be used nested.");
			}
			return currentObject != null;
		}

		public T next () {
			if (currentObject == null) throw new NoSuchElementException();
			if (!valid) {
				throw new RuntimeException("#iterator() cannot be used nested.");
			}
			prevObject = currentObject;
			currentObject =  currentObject.get_next();
			return (T) prevObject;
		}

		public void remove () {
			if (!allowRemove) throw new RuntimeException("Remove not allowed.");
			//next之后，需要移除前一个而不是currentObject
			this.list.remove(prevObject);
		}

		public void reset () {
			currentObject = list.first();
		}

		public Iterator<T> iterator () {
			return this;
		}
	}
	
	
	
//	LinkedList ll;
	ILinkedObject first = null;
	ILinkedObject last = null;
	int size;
	ObjectLinkedListIterator iterable = null;
	
	
	public final boolean addLast(final ILinkedObject c) {
		return add(c);
	}
	
	public final boolean addFirst(final ILinkedObject c) {
		if( c.get_prev() != null || c.get_next() != null) {return false;}
		
		c._set_next(first);	
		
		if(first == null) {
			last = c;			//
		}
		else {
			first._set_prev(c);	//first.list_prev = c;
		}
		
		first = c;
	    ++size;
	    return true;
	}
	
	/**
	 * 添加链表元素
	 * @return 是否添加成功
	 * */
	public final boolean add(final ILinkedObject c) {
		if( c.get_prev() != null || c.get_next() != null) {return false;}
		
		c._set_prev(last);//c.list_prev = last;
		
		if(last == null) {
			first = c;
		}
		else {
			last._set_next(c);	//last.list_next = c;
		}
		
		last = c;
	    ++size;
	    return true;
	}
	
	public final T removeFirst() {
		final ILinkedObject ret = first;
		if(ret == null) {return null;}
		remove(first);
		return (T) ret;
	}
	
	public final T removeLast() {
		final ILinkedObject ret = last;
		if(ret == null) {return null;}
		remove(last);
		return (T) ret;
	}
	
	/**
	 * 移除链表元素
	 */
	public final void remove(final ILinkedObject c) {
        final ILinkedObject next = c.get_next();
        final ILinkedObject prev = c.get_prev();
        
        if (prev == null) {
            first = next;
        } else {
            prev._set_next(next);//prev.list_next = next;
            c._set_prev(null);//c.list_prev = null;
        }

        if (next == null) {
            last = prev;
        } else {
            next._set_prev(prev);//next.list_prev = prev;
            c._set_next(null);//c.list_next = null;
        }

        --size;
	}
	
	/**清空链表 */
	public final void clear() {
		for(ILinkedObject curr = first; curr != null; ) {
			ILinkedObject next = curr.get_next();//curr.list_next;
			curr._set_next(null);//curr.list_next = null;
			curr._set_prev(null);//curr.list_prev = null;
			curr = next;
		}
		first = last = null;
		size = 0;
	}
	
	public final boolean isEmpty() {
		return size <= 0;
	}
	
	public final int size() {
		return size;
	}
	
	public final T first() {
		return (T) first;
	}
	
	public final T last() {
		return (T)last;
	}
	
	public String toSimpleString() {
		return "size = " + size + "\n";
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("size = ").append(size).append('\n');
		
		ILinkedObject temp = first;
		for(int i = 0; i < size; ++i) {
			sb.append("["+i+"] ").append(temp).append('\n');
			temp = temp.get_next();
		}
		return sb.toString();
	}

	@Override
	public Iterator<T> iterator() {
		if (iterable == null) iterable = new ObjectLinkedListIterator(this);
		iterable.reset();
		return iterable;
	}
}
