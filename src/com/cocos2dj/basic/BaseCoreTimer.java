package com.cocos2dj.basic;

/**
 * SCoreTimer.java <p>
 * 
 * 引擎内部时间 会进行平滑处理
 * 
 * @author Copyright (c) 2012-2017 xu jun
 */
public final class BaseCoreTimer {
	
	//--------------smooth delta----------------------//
	static final int SMOOTH_COUNT = 32;
	static final float[] 	deltaList = new float[SMOOTH_COUNT];
	static int 		currDeltaPoint = 0;
	static float	currDeltaTotal = 0;
	static int		deltaListSize = 0;
	
	//////////////////////////////
	
	private static final void addDelta(float newDelta) {
		if(deltaListSize >= deltaList.length) {
			currDeltaTotal -= deltaList[currDeltaPoint];
			deltaList[currDeltaPoint++] = newDelta;
			currDeltaTotal += newDelta;
			if(currDeltaPoint >= deltaListSize) {
				currDeltaPoint = 0;
			}
		}
		else {
			deltaList[deltaListSize++] = newDelta;
			currDeltaTotal += newDelta;
		}
	}
	
	static final float getSmoothDelta(float newDelta) {
		addDelta(newDelta);
		float ret = currDeltaTotal / deltaListSize;
		return ret;
	}
	
	/**更新内部时间
	 * @return 平滑时间 */
	public static final float update() {
		currentTime = System.currentTimeMillis();
		final int tempdelta = ((int) (currentTime - lastTime));
		lastTime = currentTime;
//		if(delta > 28) updateCount(1);
//		else updateCount(0);
//		
////		deltaSecond = Gdx.graphics.getDeltaTime();
////		delta = (int) (deltaSecond * 1000f);
//		if(rate > 0.5f) {
//			delta = 32;
//			deltaSecond = 32 / 1000f;
//		}
//		else {
//			delta = 16;
//			deltaSecond = 16 / 1000f;
//		}
//		if(delta > 64) {delta = 64;}
//		deltaSecond = delta / 1000f;
		
		delta = getSmoothDelta(tempdelta > 100 ? 100 : tempdelta);
		delta = delta < 1f ? 1f : delta;
		deltaSecond = delta / 1000f;
		return delta;
	}
	
	/**重置时间 */
	public static final void updateLastTime() {
		lastTime = System.currentTimeMillis();
	}
	
	//////////////////////////
	private static long currentTime, lastTime;
	public static float delta;
	public static float deltaSecond;
}
