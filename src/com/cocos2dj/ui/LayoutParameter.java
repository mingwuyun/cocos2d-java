package com.cocos2dj.ui;

public class LayoutParameter {

	/**
	 * Protocol for getting a LayoutParameter.
	 * Every element want to have layout parameter should inherit from this class.
	 */
	public static interface ILayoutParameter {
//		public void release();
	    /**
	     *
	     *@return A LayoutParameter and its descendant pointer.
	     */
	    public LayoutParameter getLayoutParameter();
	};
}
