package com.cocos2dj.utils;

import java.lang.reflect.Array;

/**
 * Card2D Engine �����<p>
 * 
 * ͨ������ {@link #onCreated(Object)},{@link #onPop(Object)},{@link #onPush(Object)}
 * ����������Ӧ�������
 * <p>
 * ������������Ҫʵ�ֶ���ÿ������ĸ���,ÿ�ΰ��̶��ĸ�������<br>
 * <b>���������������ᰴ1����
 * 
 * @author xu jun
 * Copyright (c) 2013. All rights reserved.
 */
public class ObjectPoolLinear<T> implements IObjectPool<T> {
	
	/**�����Ѵ�������ĸ��� */
	  private T[] objects;
	  
	  protected T[] stack;
	  protected int index;
	  private int size;
	  
	  /**���ݳ��� */
	  private final int extendConstants;
	  
	  private final Class<T> sClass;

	  private final Class<?>[] params;
	  private final Object[] args;

	  
	  public ObjectPoolLinear(Class<T> argClass, int argInitSize) {
	    this(argClass, argInitSize, 1);
	  }

	  public ObjectPoolLinear(Class<T> argClass, int argInitSize, int extendConstants) {
		    this(argClass, argInitSize, extendConstants, null, null);
	  }
	  
	  /**����ع�����
	   * @param argClass ��������
	   * @param argInitSize ����ջ�ĳ�ʼ�ߴ�
	   * @param extendConstants ÿ�����ӵĸ���
	   * @param argParam ����������Ĳ�������
	   * @param argArgs ����������Ĳ��� */
	  public ObjectPoolLinear(Class<T> argClass, int argInitSize, int extendConstants, Class<?>[] argParam, Object[] argArgs) {
	    index = 0;
	    this.extendConstants = extendConstants;
	    sClass = argClass;
	    params = argParam;
	    args = argArgs;

	    stack = null;
	    extendStack(argInitSize);
	  }

	  
	  
	  @SuppressWarnings("unchecked")
	  private void extendStack(final int argSize) {
	    T[] newStack = (T[]) Array.newInstance(sClass, argSize);
	    if (stack != null) {
	      System.arraycopy(stack, 0, newStack, 0, size);
	    }
	    //TODO �����иĶ� 
//	    for (int i = 0; i < newStack.length; i++) {
	    for (int i = index; i < newStack.length; i++) {
	      try {
	        if (params != null) {
	          newStack[i] = sClass.getConstructor(params).newInstance(args);
	        } else {
	          newStack[i] = sClass.newInstance();
	        }
	        onCreated(newStack[i]);
	      } catch(Exception e) {
//	    	  System.out.println(" "+i);
	    	  e.printStackTrace();
	      }
	      /*catch (InstantiationException e) {
	    	  C2Log.error("Error creating pooled object " + sClass.getSimpleName(), e);
	        assert (false) : "Error creating pooled object " + sClass.getCanonicalName();
	      } catch (IllegalAccessException e) {
	        C2Log.error("Error creating pooled object " + sClass.getSimpleName(), e);
	        assert (false) : "Error creating pooled object " + sClass.getCanonicalName();
	      } catch (IllegalArgumentException e) {
	    	  C2Log.error("Error creating pooled object " + sClass.getSimpleName(), e);
	        assert (false) : "Error creating pooled object " + sClass.getCanonicalName();
	      } catch (SecurityException e) {
	    	  C2Log.error("Error creating pooled object " + sClass.getSimpleName(), e);
	        assert (false) : "Error creating pooled object " + sClass.getCanonicalName();
	      } catch (InvocationTargetException e) {
	    	  C2Log.error("Error creating pooled object " + sClass.getSimpleName(), e);
	        assert (false) : "Error creating pooled object " + sClass.getCanonicalName();
	      } catch (NoSuchMethodException e) {
	    	  C2Log.error("Error creating pooled object " + sClass.getSimpleName(), e);
	        assert (false) : "Error creating pooled object " + sClass.getCanonicalName();
	      }*/
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
//			  size+=extendConstants;
			  extendStack(size + extendConstants);
//			  extendStack(size );
		  }
		  T t = stack[index++];
		  onPop(t);
		  return t;
	  }
	  
	  /**��pop����һ�� ��֮ͬ�����ڲ������onPop����*/
	  public final T get() {
		  if (index >= size) {
			  extendStack(size+extendConstants);
		  }
		  T t = stack[index++];
		  return t;
	  }

	  public final void push(T object) {
		 onPush(object);
//		 System.out.println("class = " + this.sClass);
		 //TODO debug
		 if(stack == null) {
			 return;
		 }
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
		sb.append("| type : "); sb.append("add_pool");
		sb.append("| class : "); sb.append(ss[ss.length - 1]); 
		sb.append("| size : "); sb.append(size);
		sb.append("| exConst : "); sb.append(extendConstants);
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

