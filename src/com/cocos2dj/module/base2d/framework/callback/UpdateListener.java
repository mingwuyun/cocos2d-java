package com.cocos2dj.module.base2d.framework.callback;

import com.cocos2dj.module.base2d.framework.PhysicsObject;

/**UpdateListener<br>
 * 当更新位置时调用以发送位置信息
 * 
 * @author xu jun
 * Copyright (c) 2012-2014. All rights reserved. */
public interface UpdateListener {
	
	/**当C2PhysObject更新完位置后调用<br>
	 * 
	 * 这个方法在C2PhysicsObject的sweep方法以及contact的handle方法中调用
	 * 由于可能重复调用， 所以不要再这个接口执行过多的操作 
	 * 
	 * @param o 更新的对象 */
	public void onUpdatePosition(final PhysicsObject o);
}
