package com.cocos2dj.module.base2d.framework.pipeline;

import com.cocos2dj.module.base2d.framework.PhysicsScene;
import com.cocos2dj.module.base2d.framework.callback.ContactListener;
import com.cocos2dj.module.base2d.framework.collision.Contact;
import com.cocos2dj.module.base2d.framework.common.AABB;
import com.cocos2dj.module.base2d.framework.common.TimeInfo;
import com.cocos2dj.module.base2d.jbox2d.BroadPhase;

/**接触处理<p>
 * 
 * @author xujun
 * Copyright (c) 2015. All rights reserved. */
public class ContactSolver {
	
	/**
	 * 从缓存的角度来讲这样并不好，但是实际上该物理引擎处理对象的性能问题
	 * 主要出现在broadPhase的算法上而不是contact的计算因此这里不做优化了
	 * @param listener
	 * @param time
	 * @param scene
	 */
	public final void solve(final ContactListener listener, final TimeInfo time, final PhysicsScene scene) {
		/*处理碰撞 需进行迭代*/
		final BroadPhase broadPhase = scene._getBroadPhase();
		Contact contact = scene._getContacts();
//		System.out.println("start contact");
		//验证contact是否需要(移动后可能该contact已经不存在)
		//并且如果contact被设为需要删除则会执行删除方法
		while(contact != null) {
			
			boolean overlap;
			if(contact.isAABBTest()) {
				overlap = AABB.testOverlap(contact.getPhysicsObject1().sweepAABB, 
						contact.getPhysicsObject2().sweepAABB);
			}
			else {
				overlap = broadPhase.testOverlap(contact.getPhysicsObject1().getProxy(),
						contact.getPhysicsObject2().getProxy());
			}

//			if(contact.isAABBTest()) {
//				if(!AABB.testOverlap(contact.getPhysicsObject1().sweepAABB, 
//						contact.getPhysicsObject2().sweepAABB)
//						||contact.isWillRemove()
//						||contact.getPhysicsObject1().isSleep()
//						||contact.getPhysicsObject2().isSleep()) {
//					
//					//需要删除该contact
//					Contact next = contact.list_next;
//					scene._destroyContact(contact);
//					contact = next;
//				}
//				else {
//					contact.o1.initAdvance();
//					contact.o2.initAdvance();
//					
//					contact = contact.list_next;
//				}
//			}
			if(!overlap
					||contact.isWillRemove()
					||contact.getPhysicsObject1().isSleep()
					||contact.getPhysicsObject2().isSleep()) {
				
				//需要删除该contact
				Contact next = contact.list_next;
				scene._destroyContact(contact);
				contact = next;
			}
			else {
				contact.o1.initAdvance();
				contact.o2.initAdvance();
				
				contact = contact.list_next;
			}
		}
		
		//迭代指定的次数检测碰撞可以在一定程度上防止隧道效应
		for(int i = 1; i <= time.iteration; ++i) {
			contact = scene._getContacts();
			//处理contact 可能删除
			final boolean last = (i == time.iteration);
			
			while(contact != null) {
				contact.handle(last, time, listener);
				contact = contact.list_next;
			}
			
			contact = scene._getContacts();
			while(contact != null) {
				contact.clearAdvanceFlag();
				contact = contact.list_next;
			}
		}
	}
}
