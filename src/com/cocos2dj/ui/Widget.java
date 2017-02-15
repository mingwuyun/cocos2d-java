package com.cocos2dj.ui;

import com.cocos2dj.s2d.ProtectedNode;
import com.cocos2dj.ui.LayoutParameter.ILayoutParameter;

public class Widget extends ProtectedNode implements ILayoutParameter {

	@Override
	public LayoutParameter getLayoutParameter() {
		return null;
	}

	 /**
     * Widget position type for layout.
     */
    public static enum PositionType {
        ABSOLUTE,
        PERCENT
    }
    
    /**
     * Widget size type for layout.
     */
    public static enum SizeType {
        ABSOLUTE,
        PERCENT
    }
    
    /**
     * Touch event type.
     */
    public static enum TouchEventType {
        BEGAN,
        MOVED,
        ENDED,
        CANCELED
    }
    
    /**
     * Texture resource type.
     * - LOCAL:  It means the texture is loaded from image.
     * - PLIST: It means the texture is loaded from texture atlas.
     */
    public static enum TextureResType {
        LOCAL,
        PLIST
    }
    
    /**
     * Widget bright style.
     */
    public static enum BrightStyle {
        NONE,
        NORMAL,
        HIGHLIGHT
    }
    
    /**
     * Widget touch event callback.
     */
    public static interface WidgetTouchCallback {
    	public void callback(Object ref, TouchEventType type);
    }
    /**
     * Widget click event callback.
     */
    public static interface WidgetClickCallback {
    	public void callback(Object ref);
    }
//    typedef std::function<void(Ref*)> ccWidgetClickCallback;
    /**
     * Widget custom event callback.
     * It is mainly used together with Cocos Studio.
     */
    public static interface WidthEventCallback {
    	public void callback(Object ref, int event);
    }
//    typedef std::function<void(Ref*, int)> ccWidgetEventCallback;
    
    
    
}
