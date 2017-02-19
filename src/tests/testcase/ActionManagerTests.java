package tests.testcase;

import com.cocos2dj.s2d.Action;
import com.cocos2dj.s2d.ActionInstant.CallFunc;
import com.cocos2dj.s2d.ActionInterval.DelayTime;
import com.cocos2dj.s2d.ActionInterval.MoveBy;
import com.cocos2dj.s2d.ActionInterval.MoveTo;
import com.cocos2dj.s2d.ActionInterval.Repeat;
import com.cocos2dj.s2d.ActionInterval.RotateBy;
import com.cocos2dj.s2d.ActionInterval.Sequence;
import com.cocos2dj.s2d.Sprite;

import tests.TestCase;
import tests.TestSuite;

/**
 * 动作管理器测试
 * 
 * @author xujun
 */
public class ActionManagerTests extends TestSuite {
	
	public ActionManagerTests() {
		addTestCase("StopAction", ()->{return new ActionStopTest();});
		addTestCase("StopActionTest", ()->{return new ActionStopActionTest();});
		addTestCase("StopActionByTagTest", ()->{return new ActionStopByTagTest();});
	}
	
	static class ActionManagerTest extends TestCase {
		
	}
	
	/////////////////////////////////////
	//TODO 移动stopAllActions
	static class ActionStopActionTest extends ActionManagerTest {
		
		public void onEnter() {
			super.onEnter();
			
			Sprite sprite1 = (Sprite) Sprite.create("powered.png").addTo(this);
			sprite1.setPosition(100, 320);
			
			sprite1.runAction(Sequence.create(
					MoveBy.create(1, 900, 0),
					CallFunc.create(()->{sprite1.stopAllActions();}),		//remove when update
					MoveBy.create(1, 0, 300),
//					MoveBy.create(1, -900, 0),
					MoveBy.create(1, 0, -300),
					MoveBy.create(1, 900, 300),
					MoveBy.create(1, -900, -300)
					));
			sprite1.runAction(Sequence.create(
					RotateBy.create(3, 900),
					RotateBy.create(3, -900)
					));
			
//			//restart
			scheduleOnce((t)->{
				sprite1.runAction(Sequence.create(
						RotateBy.create(3, 900),
						RotateBy.create(3, -900)
						));
				return false;
			}, 3f);
			
			Sprite sprite2 = (Sprite) Sprite.create("powered.png").addTo(this);
			sprite2.setRect(0, 0, 100, 120);
			sprite2.runAction(
			Sequence.create(
					MoveTo.create(2f, 600, 200),
					MoveTo.create(2f, 700, 300),
					MoveTo.create(2f, 500, 300),
					MoveTo.create(2f, 600, 200)
					)
			);
		}
	}
	
	//TODO 移动相关动作测试
	static class ActionStopByTagTest extends ActionManagerTest {
		
		public void onEnter() {
			super.onEnter();
			
			//repeat
			Sprite sprite1 = (Sprite) Sprite.create("powered.png").addTo(this);
			sprite1.setRect(0, 0, 100, 120);
			sprite1.setPosition(100, 320);
//			
			// action1:100; action2:100 remove:100
			sprite1.runAction(Repeat.create(Sequence.create(
					MoveBy.create(2, 900, 300),
					MoveBy.create(2, -900, -300)
					), 2)).setTag(100);
			sprite1.runAction(RotateBy.create(5, 1000)).setTag(100);;
			
			sprite1.runAction(Sequence.create(DelayTime.create(1), CallFunc.create(()->{sprite1.stopAllActionsByTag(100);})));
			
			// 3s后开始测试2
			scheduleOnce((t)-> {
				sprite1.stopAllActions();
				// action1:99; action2:100 remove:100 (result: all stop)
				sprite1.setPosition(100, 320);
				sprite1.runAction(Repeat.create(Sequence.create(
						MoveBy.create(2, 900, 300),
						MoveBy.create(2, -900, -300)
						), 2)).setTag(99);
				sprite1.runAction(RotateBy.create(5, 1000)).setTag(100);;
				
				sprite1.runAction(Sequence.create(DelayTime.create(1), CallFunc.create(()->{sprite1.stopAllActionsByTag(100);})));
				return false;
			}, 3);
			
			// 6s后开始测试3
			scheduleOnce((t)-> {
				sprite1.stopAllActions();
				
				// action1:99; action2:100 remove:100 (result: stop 1)
				sprite1.setPosition(100, 320);
				sprite1.runAction(Repeat.create(Sequence.create(
						MoveBy.create(2, 900, 300),
						MoveBy.create(2, -900, -300)
						), 2)).setTag(99);
				sprite1.runAction(RotateBy.create(5, 1000)).setTag(100);;
				
				sprite1.runAction(Sequence.create(DelayTime.create(1), CallFunc.create(()->{sprite1.stopActionByTag(100);})));
				return false;
			}, 6);
		}
	}
	
	
	//TODO 移动相关动作测试
	static class ActionStopTest extends ActionManagerTest {
		
		public void onEnter() {
			super.onEnter();
			
			//repeat
			Sprite sprite1 = (Sprite) Sprite.create("powered.png").addTo(this);
			sprite1.setRect(0, 0, 100, 120);
			sprite1.setPosition(100, 320);
//				
			// action1:100; action2:100 remove:100
			Action a = sprite1.runAction(Repeat.create(Sequence.create(
					MoveBy.create(2, 900, 300),
					MoveBy.create(2, -900, -300)
					), 2));
			sprite1.runAction(RotateBy.create(5, 1000)).setTag(100);
			
			sprite1.runAction(Sequence.create(DelayTime.create(1), CallFunc.create(()->{sprite1.stopAction(a);})));
			
			// 3s后开始测试2
			scheduleOnce((t)-> {
				sprite1.stopAllActions();
				sprite1.setPosition(100, 320);
				
				Action aa = sprite1.runAction(Repeat.create(Sequence.create(
						MoveBy.create(1, 900, 300),
						MoveBy.create(1, -900, -300)
						), 1));
				sprite1.runAction(RotateBy.create(5, 1000)).setTag(100);
				
				//移除失败——已经执行完毕
				sprite1.runAction(Sequence.create(DelayTime.create(3), CallFunc.create(()->{sprite1.stopAction(aa);})));
				return false;
			}, 3);
			
		}
	}
}
