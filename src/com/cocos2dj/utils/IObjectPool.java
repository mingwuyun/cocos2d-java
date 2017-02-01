package com.cocos2dj.utils;

public interface IObjectPool<T> {
	public T pop();
	public T get();
	public void push(T t);
	public T[] getAll();
	public void dispose();
}
