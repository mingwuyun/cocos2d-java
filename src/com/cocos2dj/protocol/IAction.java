package com.cocos2dj.protocol;

public interface IAction {

	public static final int INVALID_TAG = -1;
	
	public void startWithTarget(INode node);
	
	public INode getOriginalTarget();
	
	public int getTag();
	
	public int getFlags();
	
	
	public void step(float dt);
	
	public void stop();
	
	public boolean isDone();
	
	/**设置是否在ActionManager中执行 */
	public void setAttached(boolean attached);
	
	/**@return 是否在ActionManager中执行 */
	public boolean isAttached();
	
}
