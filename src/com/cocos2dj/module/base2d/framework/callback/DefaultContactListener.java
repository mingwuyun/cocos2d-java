package com.cocos2dj.module.base2d.framework.callback;

import com.badlogic.gdx.math.Vector2;
import com.cocos2dj.module.base2d.framework.PhysicsObject;
import com.cocos2dj.module.base2d.framework.collision.ContactCollisionData;

public class DefaultContactListener implements ContactListener {

	@Override
	public boolean cancelContact(PhysicsObject o1, PhysicsObject o2) {
		return o1.cancelContact(o2) || o2.cancelContact(o1);
	}

	@Override
	public void contactCreated(PhysicsObject o1, PhysicsObject o2, Vector2 MTD, ContactCollisionData data) {
		o1.contactCreated(o2, MTD, data);
		o2.contactCreated(o1, MTD, data);
	}

	@Override
	public void contactPersisted(PhysicsObject o1, PhysicsObject o2, Vector2 MTD, ContactCollisionData data) {
		o1.contactPersisted(o2, MTD, data);
		o2.contactPersisted(o1, MTD, data);
	}

	@Override
	public void contactDestroyed(PhysicsObject o1, PhysicsObject o2, Vector2 MTD, ContactCollisionData data) {
		o1.contactDestroyed(o2, MTD, data);
		o2.contactDestroyed(o1, MTD, data);
	}


}
