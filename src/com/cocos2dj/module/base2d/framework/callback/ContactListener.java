package com.cocos2dj.module.base2d.framework.callback;

import com.badlogic.gdx.math.Vector2;
import com.cocos2dj.module.base2d.framework.PhysicsObject;
import com.cocos2dj.module.base2d.framework.collision.ContactCollisionData;

/**
 * ContactListener.java
 * <p>
 * @author Copyright(c) 2015 xu jun
 */
public interface ContactListener {
	
	/**
	 * 碰撞处理开始时调用 返回true取消本次碰撞
	 * @param o1
	 * @param o2
	 * @return false 继续碰撞 true 取消碰撞 */
	public boolean cancelContact(PhysicsObject o1, PhysicsObject o2);
	
	/**碰撞开始时调用
	 * @param o1
	 * @param o2
	 * @param MTD 修正向量 */
	public void contactCreated(PhysicsObject o1, PhysicsObject o2, Vector2 MTD, ContactCollisionData data);
	
	/**碰撞持续时调用
	 * @param o1
	 * @param o2
	 * @param MTD 修正向量 */
	public void contactPersisted(PhysicsObject o1, PhysicsObject o2, Vector2 MTD, ContactCollisionData data);
	
	/**碰撞结束调用
	 * @param o1
	 * @param o2
	 * @param MTD 修正向量 */
	public void contactDestroyed(PhysicsObject o1, PhysicsObject o2, Vector2 MTD, ContactCollisionData data);
}
