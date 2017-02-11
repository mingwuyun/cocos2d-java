package com.cocos2dj.utils;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;

/**
 * ObjectPool.java
 * <p>
 * 
 * ͨ������ {@link #onCreated(Object)},{@link #onPop(Object)},{@link #onPush(Object)}
 * ����������Ӧ�������
 * <p>
 * <b>ÿһ�����ݻὫ�ض����������2��
 * @author xu jun
 * Copyright (c) 2013. All rights reserved.
 */
public class ObjectPool<T> implements IObjectPool<T> {
	
	 /**�����Ѵ�������ĸ��� */
	  private T[] objects;
	  
	  protected T[] stack;
	  protected int index;
	  private int size;
	  private final Class<T> sClass;

	  private final Class<?>[] params;
	  private final Object[] args;

	  public ObjectPool(Class<T> argClass, int argInitSize) {
	    this(argClass, argInitSize, null, null);
	  }

	  /**����ع�����
	   * @param argClass ��������
	   * @param argInitSize ����ջ�ĳ�ʼ�ߴ�
	   * @param argParam ����������Ĳ�������
	   * @param argArgs ����������Ĳ��� */
	  public ObjectPool(Class<T> argClass, int argInitSize, Class<?>[] argParam, Object[] argArgs) {
	    index = 0;
	    sClass = argClass;
	    params = argParam;
	    args = argArgs;

	    stack = null;
	    index = 0;
	    extendStack(argInitSize);
	  }

	  @SuppressWarnings("unchecked")
	  private void extendStack(int argSize) {
	    T[] newStack = (T[]) Array.newInstance(sClass, argSize);
	    if (stack != null) {
	      System.arraycopy(stack, 0, newStack, 0, size);
	    }
	    //TODO �����иĶ� for (int i = 0; i < newStack.length; i++) {
	    for (int i = index; i < newStack.length; i++) {
	      try {
	        if (params != null) {
	          newStack[i] = sClass.getConstructor(params).newInstance(args);
	        } else {
	          newStack[i] = sClass.newInstance();
	        }
	        onCreated(newStack[i]);
	      } catch (InstantiationException e) {
	    	  e.printStackTrace();
	        assert (false) : "Error creating pooled object " + sClass.getCanonicalName();
	      } catch (IllegalAccessException e) {
	       // log.error("Error creating pooled object " + sClass.getSimpleName(), e);
	        assert (false) : "Error creating pooled object " + sClass.getCanonicalName();
	      } catch (IllegalArgumentException e) {
	       // log.error("Error creating pooled object " + sClass.getSimpleName(), e);
	        assert (false) : "Error creating pooled object " + sClass.getCanonicalName();
	      } catch (SecurityException e) {
	       // log.error("Error creating pooled object " + sClass.getSimpleName(), e);
	        assert (false) : "Error creating pooled object " + sClass.getCanonicalName();
	      } catch (InvocationTargetException e) {
	       // log.error("Error creating pooled object " + sClass.getSimpleName(), e);
	        assert (false) : "Error creating pooled object " + sClass.getCanonicalName();
	      } catch (NoSuchMethodException e) {
	       // log.error("Error creating pooled object " + sClass.getSimpleName(), e);
	        assert (false) : "Error creating pooled object " + sClass.getCanonicalName();
	      }
	    }
	    stack = newStack;
	    size = newStack.length;
	    
	    //�����������ɶ���ĸ��� 
	    objects = (T[]) Array.newInstance(sClass, size);
	    System.arraycopy(stack, 0, objects, 0, size);
	  }

	  /**������pop������ȡ����ʱ�ᴥ��
	   * @param object ��ȡ�Ķ��� */
	  protected void onPop(T object){
		  
	  }
	  
	  /**������push�������ն���ʱ�ᴥ��
	   * @param object ���յĶ��� */
	  protected void onPush(T object){
		  
	  }
	  
	  /**�������ʼ��ʱ�ᴥ��
	   * @param object ���ɵĶ��� */
	  protected void onCreated(T object){
		  
	  }
	  
	  public final T pop() {
		  if (index >= size) {
			  extendStack((int) (size + size * .5f));
		  }
		  T t = stack[index++];
		  onPop(t);
		  return t;
	  }
	  
	  /**��pop����һ�� ��֮ͬ�����ڲ������onPop����*/
	  public final T get() {
		  if (index >= size) {
			  extendStack(size * 2);
		  }
		  T t = stack[index++];
		  return t;
	  }

	  public final void push(T object) {
		 onPush(object);
		 stack[--index] = (T) object;
	  }
	  
	  public final T[] getAll() {
		  return objects;
	  }

	public void dispose() {
		for(int i = 0; i < size; ++i) {
			objects[i] = null;
			stack[i] = null;
		}
		objects = stack = null;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder(64);
		final String s = sClass.getName();
		final String[] ss = s.split("\\.");
		sb.append("| type : "); sb.append("normal_pool");
		sb.append("| class : "); sb.append(ss[ss.length - 1]); 
		sb.append("| size : "); sb.append(size);
		sb.append("| index : "); sb.append(index);
		
		sb.append("| params : "); 
		if(params != null)
			for(int i=0; i<params.length; ++i) {
				sb.append(params[i].getName());
				sb.append(',');
			}
		else
			sb.append("null");
		
		return sb.toString();
	}
}
