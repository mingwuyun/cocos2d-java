package com.cocos2dj.module.base2d.framework.common;

import java.util.Arrays;
import java.util.Collection;
/**
 * 缓存ArrayList<p>
 * 
 * 基本功能与arrayList相同，在执行删除操作是，采用标记的方式一次性更新内容，
 * 而非一个一个删除。<p><pre>
 * Object temp = tempArray.beginRemove();//获取副本开始修改
 * tempArray.keepElement(0);//保留元素0、1
 * tempArray.keepElement(1);
 * tempArray.endRemove();//保存修改
 * 
 * </pre>
 */
public class TempArray<E> {
	
    private Object[] elementData;
    
    private Object[] tempElementData;
    
    private int size;

    /**获取建议TempArray信息 */
    public String toSimpleString() {
    	StringBuilder sb = new StringBuilder();
    	sb.append("TempArray [size = " + size +"]").append('\n');
    	if(tempElementData == null) {
    		sb.append("no tempElementData").append('\n');
    	}
    	else {
    		sb.append("tempElementData : ").append('\n');
        	for(int i = 0; i < tempElementData.length; ++i) {
        		sb.append("["+i+"] ").append(tempElementData[i]).append('\n');
        	}
    	}
    	return sb.toString();
    }
    
    
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	sb.append("TempArray [size = " + size +"]").append('\n');
    	for(int i = 0; i < size; ++i) {
    		sb.append("["+i+"] ").append(elementData[i]).append('\n');
    	}
    	if(tempElementData == null) {
    		sb.append("no tempElementData").append('\n');
    	}
    	else {
    		sb.append("tempElementData : ").append('\n');
        	for(int i = 0; i < tempElementData.length; ++i) {
        		sb.append("["+i+"] ").append(tempElementData[i]).append('\n');
        	}
    	}
    	return sb.toString();
    }
    
    public TempArray(int initialCapacity) {
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal Capacity: "+
                                               initialCapacity);
        this.elementData = new Object[initialCapacity];
    }

    public TempArray() {
        this(8);
    }

    
    private boolean removing = false;
    private int tempCount = 0;
    
    public final Object[] beginRemove() {
    	if(!removing) {
    		removing = true;
    		tempCount = 0;
    		initTempElementData();
    		
    		//替换缓存
    		final Object[] temp = elementData;
    		elementData = tempElementData;
    		tempElementData = temp;
    		
    		return temp;
    	}
    	else throw new RuntimeException("TempArray正处于移除状态，无法执行beginRemove");
    }
    
    public final void keepElement(final int index) {
//    	tempElementData
    	if(removing) {
    		elementData[tempCount++] = tempElementData[index];
    	}
    	else throw new RuntimeException("TempArray没有处于移除状态，无法执行keepElement");
    }
    
    public final void endRemove() {
    	if(removing) {
    		removing = false;
    		size = tempCount;
    		
    		//在这里清空数据 let gc work
    		for(int i = 0, n = tempElementData.length; i < n; ++i) {
    			tempElementData[i] = null;
    		}
    	}
    	else throw new RuntimeException("TempArray没有处于移除状态，无法执行endRemove");
    }
    
    /**初始化 缓存元素数据 */
    final void initTempElementData() {
    	if(tempElementData == null) {
    		tempElementData = new Object[elementData.length];
    	}
    	else if(elementData.length > tempElementData.length) {
    		tempElementData = new Object[elementData.length];
    	}
    }
    
    
    public final void trimToSize() {
        int oldCapacity = elementData.length;
        if (size < oldCapacity) {
            elementData = Arrays.copyOf(elementData, size);
        }
    }

    public void ensureCapacity(int minCapacity) {
        if (minCapacity > 0)
            ensureCapacityInternal(minCapacity);
    }

    private void ensureCapacityInternal(int minCapacity) {
        // overflow-conscious code
        if (minCapacity - elementData.length > 0)
            grow(minCapacity);
    }

    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    
    private void grow(int minCapacity) {
        // overflow-conscious code
        int oldCapacity = elementData.length;
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(minCapacity);
        // minCapacity is usually close to size, so this is a win:
        elementData = Arrays.copyOf(elementData, newCapacity);
    }

    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) // overflow
            throw new OutOfMemoryError();
        return (minCapacity > MAX_ARRAY_SIZE) ?
            Integer.MAX_VALUE :
            MAX_ARRAY_SIZE;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }

    public int indexOf(Object o) {
        if (o == null) {
            for (int i = 0; i < size; i++)
                if (elementData[i]==null)
                    return i;
        } else {
            for (int i = 0; i < size; i++)
                if (o.equals(elementData[i]))
                    return i;
        }
        return -1;
    }
    
    // Positional Access Operations
    @SuppressWarnings("unchecked")
    final E elementData(final int index) {
        return (E) elementData[index];
    }

    public final E get(final int index) {
        return elementData(index);
    }

//    public final E set(int index, E element) {
//        E oldValue = elementData(index);
//        elementData[index] = element;
//        return oldValue;
//    }

    /**
     * Appends the specified element to the end of this list.
     *
     * @param e element to be appended to this list
     * @return <tt>true</tt> (as specified by {@link Collection#add})
     */
    public boolean add(E e) {
        ensureCapacityInternal(size + 1);  // Increments modCount!!
        elementData[size++] = e;
        return true;
    }

    /**
     * Removes the element at the specified position in this list.
     * Shifts any subsequent elements to the left (subtracts one from their
     * indices).
     *
     * @param index the index of the element to be removed
     * @return the element that was removed from the list
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public E remove(int index) {
        E oldValue = elementData(index);

        int numMoved = size - index - 1;
        if (numMoved > 0)
            System.arraycopy(elementData, index+1, elementData, index,
                             numMoved);
        elementData[--size] = null; // Let gc do its work

        return oldValue;
    }


    public void clear() {
        // Let gc do its work
    	removing = false;
        for (int i = 0; i < size; i++) {
            elementData[i] = null;
        }
        tempElementData = null;
        size = 0;
    }
    

//	public static void main(String[] s) {
//		TempArray ta = new TempArray();;
//		
//		System.out.println(ta);
//		
//		Object[] src = new Object[10];
//		for(int i = 0; i < src.length; ++i) {
//			final int n = i;
//			src[i] = new Object() {
//				public String toString() {
//					return " "+n;
//				}
//			};
//		}
//		
//		ta.add(src[3]); ta.add(src[6]); ta.add(src[9]);
//		ta.add(src[1]); ta.add(src[0]); ta.add(src[2]);
//		System.out.println(ta);
//		
//		Object[] elements = ta.beginRemove();	//读入新的object缓存，将源数据剔除
//		
////		for(int i = 0; i < ta.size; ++i) {
////			if(elements[i] == null) {
////				ta.keepElement(i);
////			}
////		}
//		ta.keepElement(0);
//		ta.keepElement(2);
//		ta.keepElement(3);
//		ta.keepElement(4);
//		
//		ta.endRemove(); //将新的数据写入, 将temp缓存交换
//		System.out.println(ta);
//		
//		elements = ta.beginRemove();	//读入新的object缓存，将源数据剔除
//		
////		for(int i = 0; i < ta.size; ++i) {
////			if(elements[i] == null) {
////				ta.keepElement(i);
////			}
////		}
//		ta.keepElement(0);
//		ta.keepElement(2);
//		
//		ta.endRemove(); //将新的数据写入, 将temp缓存交换
//		System.out.println(ta);
//		
//		ta.clear();
//		System.out.println(ta);
//	}
    
}