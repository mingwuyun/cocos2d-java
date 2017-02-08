package tests.testcase;

import com.cocos2dj.s2d.ActionInterval.MoveBy;
import com.cocos2dj.s2d.ActionInterval.Repeat;
import com.cocos2dj.s2d.ActionInterval.RepeatForever;
import com.cocos2dj.s2d.ActionInterval.RotateBy;
import com.cocos2dj.s2d.ActionInterval.Sequence;
import com.cocos2dj.s2d.ActionInterval.Spawn;
import com.cocos2dj.s2d.Sprite;

import tests.TestCase;
import tests.TestSuite;

/**
 * 动作测试
 * 
 * @author xujun
 */
public class ActionManagerTests extends TestSuite {
	
	public ActionManagerTests() {
		addTestCase("SpawnRotateTest", ()->{return new ActionSpawnTest();});
		addTestCase("RepeatTest", ()->{return new ActionRepeatTest();});
		addTestCase("MoveBy/MoveTo", ()->{return new ActionMoveTest();});
	}
	
	static class ActionManagerTest extends TestCase {
		
	}
	
	/////////////////////////////////////
	//TODO 移动相关动作测试
	static class ActionMoveTest extends ActionManagerTest {
		
		public void onEnter() {
			super.onEnter();
			
			Sprite sprite1 = (Sprite) Sprite.create("powered.png").addTo(this);
			sprite1.setPosition(100, 320);
			
			sprite1.runAction(Sequence.create(
					MoveBy.create(1, 900, 0),
					MoveBy.create(1, 0, 300),
					MoveBy.create(1, -900, 0),
					MoveBy.create(1, 0, -300),
					MoveBy.create(1, 900, 300),
					MoveBy.create(1, -900, -300)
					));
//			//check count			
//			sprite1.setOnUpdateCallback((n, dt)->{
//				System.out.println("actionCount = " + 
//						sprite1.getNumberOfRunningActions());
//			});
//			sprite1.scheduleUpdate();
//			//stop
//			scheduleOnce((t)->{
//				System.out.println("stop");
//				sprite1.stopAllActions();
//				return false;
//			}, 1.5f);
		}
	}
	
	//TODO 移动相关动作测试
	static class ActionRepeatTest extends ActionManagerTest {
		
		public void onEnter() {
			super.onEnter();
			
			//repeat
			Sprite sprite1 = (Sprite) Sprite.create("powered.png").addTo(this);
			sprite1.setRect(0, 0, 100, 120);
			sprite1.setPosition(100, 320);
//			
			sprite1.runAction(Repeat.create(Sequence.create(
					MoveBy.create(1, 900, 300),
					MoveBy.create(1, -900, -300)
					), 2));
			
			Sprite sprite2 = (Sprite) Sprite.create("powered.png").addTo(this);
			sprite2.setRect(0, 0, 100, 120);
			sprite2.setPosition(1000, 320);
			
			//组合测试
			sprite2.runAction(
					Sequence.create(
					MoveBy.create(1.5f, -900, -100),
					Repeat.create(Sequence.create(
							MoveBy.create(0.2f, 0, 100),
							MoveBy.create(0.2f, 50, -100)
							), 5),
					MoveBy.create(1, 600, 100)
					));
			
			Sprite sprite3 = (Sprite) Sprite.create("powered.png").addTo(this);
			sprite3.setRect(0, 0, 100, 120);
			sprite3.setPosition(500, 100);
			
			//forever
			sprite3.runAction(RepeatForever.create(
					Sequence.create(
							MoveBy.create(0.2f, 120, 120),
							MoveBy.create(0.2f, -120, 120),
							MoveBy.create(0.2f, -120, -120),
							MoveBy.create(0.5f, 120, -120)
							)
					));
			sprite3.scheduleUpdate();
			sprite3.scheduleOnce((dt)->{
				sprite3.stopAllActions();
				return false;
			}, 5f);
		}
	}
	
	//TODO rotation 和 spawn 相关动作测试
	static class ActionSpawnTest extends ActionManagerTest {
		
		public void onEnter() {
			super.onEnter();
			
			//repeat
			Sprite sprite1 = (Sprite) Sprite.create("powered.png").addTo(this);
			sprite1.setRect(0, 0, 100, 120);
			sprite1.setPosition(100, 320);
//			
			sprite1.runAction(RotateBy.create(1, 360));
			
			Sprite sprite2 = (Sprite) Sprite.create("powered.png").addTo(this);
			sprite2.setRect(0, 0, 100, 120);
			sprite2.setPosition(1000, 320);
			
			sprite2.runAction(Spawn.create(
					MoveBy.create(2, -800, -50),
					RotateBy.create(3, 2000)
					));
			
			
			
//			//组合测试
//			sprite2.runAction(
//					Sequence.create(
//					MoveBy.create(1.5f, -900, -100),
//					Repeat.create(Sequence.create(
//							MoveBy.create(0.2f, 0, 100),
//							MoveBy.create(0.2f, 50, -100)
//							), 5),
//					MoveBy.create(1, 600, 100)
//					));
//			
			Sprite sprite3 = (Sprite) Sprite.create("powered.png").addTo(this);
			sprite3.setRect(0, 0, 100, 120);
			sprite3.setPosition(500, 100);
//			
//			//forever
			sprite3.runAction(Repeat.create(
					Spawn.create(
						Sequence.create(  
								MoveBy.create(1, 100, 100),
								MoveBy.create(2, -100, 100),
								MoveBy.create(1, -100, -100),
								MoveBy.create(2, 100, -100)
						)
//						Repeat.create(Sequence.create(
//								RotateBy.create(0.5f, 120),
//								RotateBy.create(0.5f, -120)
//						), 10)
					), 1
			));
			sprite3.runAction(RepeatForever.create(
					Sequence.create(
							RotateBy.create(0.5f, 120),
							RotateBy.create(0.5f, -120)
						)
					));
//			sprite3.scheduleUpdate();
//			sprite3.scheduleOnce((dt)->{
//				sprite3.stopAllActions();
//				return false;
//			}, 5f);
		}
	}	
}
