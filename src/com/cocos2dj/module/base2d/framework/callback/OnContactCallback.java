package com.cocos2dj.module.base2d.framework.callback;

import com.cocos2dj.module.base2d.framework.PhysicsObject;
import com.cocos2dj.module.base2d.framework.collision.Contact;

public interface OnContactCallback {
	
	public boolean onContact(Contact c, PhysicsObject other);
	
}
