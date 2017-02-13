package tests.testcase;

import com.cocos2dj.s2d.ActionInstant.CallFunc;
import com.cocos2dj.s2d.ActionInterval.BezierBy;
import com.cocos2dj.s2d.ActionInterval.JumpBy;
import com.cocos2dj.s2d.ActionInterval.JumpTo;
import com.cocos2dj.s2d.ActionInterval.MoveBy;
import com.cocos2dj.s2d.ActionInterval.MoveTo;
import com.cocos2dj.s2d.ActionInterval.Repeat;
import com.cocos2dj.s2d.ActionInterval.RepeatForever;
import com.cocos2dj.s2d.ActionInterval.RotateBy;
import com.cocos2dj.s2d.ActionInterval.RotateTo;
import com.cocos2dj.s2d.ActionInterval.ScaleBy;
import com.cocos2dj.s2d.ActionInterval.ScaleTo;
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
		addTestCase("ScaleTest", ()->{return new ActionScaleTest();});
		addTestCase("jumpTest", ()->{return new ActionJumpTest();});
		addTestCase("BezierTest", ()->{return new ActionBezierTest();});
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
			sprite1.runAction(Sequence.create(
					RotateBy.create(3, 900),
					RotateBy.create(3, -900)
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
			
			Sprite sprite2 = (Sprite) Sprite.create("powered.png").addTo(this);
			sprite2.setRect(0, 0, 100, 120);
			sprite2.runAction(
			Sequence.create(
					MoveTo.create(1f, 600, 200),
					MoveTo.create(1f, 700, 300),
					MoveTo.create(1f, 500, 300),
					MoveTo.create(1f, 600, 200)
					)
			);
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
					), 2
			));
			sprite3.runAction(RepeatForever.create(
					Sequence.create(
							RotateBy.create(0.5f, 120),
							RotateBy.create(0.5f, -120)
						)
					));
			sprite3.scheduleUpdate();
			sprite3.scheduleOnce((dt)->{
				sprite3.stopAllActions();
				return false;
			}, 5f);
			
			
			Sprite sprite4 = (Sprite) Sprite.create("powered.png").addTo(this);
			sprite4.setContentSize(200, 200);
			sprite4.setPosition(900, 400);
			sprite4.runAction(RotateTo.create(2, 120));
		}
	}	
	
	//TODO JumpTest 跳跃测试
	static class ActionJumpTest extends ActionManagerTest {
		
		public void onEnter() {
			super.onEnter();
			
			Sprite sprite1 = (Sprite) Sprite.create("powered.png").addTo(this);
			sprite1.setRect(0, 0, 100, 120);
			sprite1.setPosition(100, 420);
			
			//JumpBy
			sprite1.runAction(Repeat.create(
					Sequence.create(
					JumpBy.create(2, 900, 0, 200, 2),
//					JumpBy.create(2, 0, 0, 450, 2),
					CallFunc.create(new Runnable(){
						@Override
						public void run() {
							sprite1.runAction(MoveBy.create(4f, -900, 0));
							System.out.println("Call Func");
						}
					})
					), 1));
			sprite1.runAction(MoveBy.create(4f, 0, -300));
			
			//JumpTo
			Sprite sprite2 = (Sprite) Sprite.create("powered.png").addTo(this);
			sprite2.setRect(0, 0, 100, 120);
			sprite2.setPosition(100, 420);
			sprite2.runAction(Repeat.create(Sequence.create(
					JumpTo.create(2, 900, 500, 200, 2),
					JumpTo.create(2, 100, 100, 450, 2)
					), 1));
		}
	}
	
	//TODO BezierTest 贝塞尔曲线
	static class ActionBezierTest extends ActionManagerTest {
		
		public void onEnter() {
			super.onEnter();
			
			//repeat
			Sprite sprite1 = (Sprite) Sprite.create("powered.png").addTo(this);
			sprite1.setRect(0, 0, 100, 120);
			sprite1.setPosition(100, 220);
			
			//单独运行BezierBy
			sprite1.runAction(Repeat.create(Sequence.create(
					BezierBy.create(2, 900, 0, 450, 300),
					BezierBy.create(2, -900, 0, -450, 300)
					), 1));
			
			//组合MoveBy + BezierBy
			Sprite sprite2 = (Sprite) Sprite.create("powered.png").addTo(this);
			sprite2.setRect(0, 0, 100, 120);
			sprite2.setPosition(100, 220);
			
			sprite2.runAction(Repeat.create(Sequence.create(
					BezierBy.create(2, 900, 0, 450, 300),
					BezierBy.create(2, -900, 0, -450, 300)
//					MoveBy.create(1, -900, -300)
					), 1));
			sprite2.runAction(MoveBy.create(4, 0, 300));
		}
	}
	
	//TODO ScaleTest 放缩动作测试
	static class ActionScaleTest extends ActionManagerTest {
		
		public void onEnter() {
			super.onEnter();
			
			Sprite sprite1 = (Sprite) Sprite.create("powered.png").addTo(this);
			sprite1.setRect(0, 0, 100, 120);
			sprite1.setPosition(100, 220);
			sprite1.setAnchorPoint(0, 0);
			//单独运行ScaleBy
			sprite1.runAction(Repeat.create(Sequence.create(
					ScaleBy.create(0.25f, 2f, 2f),
					ScaleBy.create(.25f, .5f, .5f)
					), 5));
			
			Sprite sprite2 = (Sprite) Sprite.create("powered.png").addTo(this);
			sprite2.setRect(0, 0, 100, 120);
			sprite2.setPosition(100, 220);
			
			//单独运行ScaleTo + BezierBy
			sprite2.runAction(Repeat.create(Sequence.create(
					ScaleTo.create(.5f, 2f, 2f),
					ScaleTo.create(.5f, 1f, 1f),
					CallFunc.create(()->{System.out.println("scaleTo sequence end");})
					), 5));
			sprite2.runAction(Repeat.create(Sequence.create(
					BezierBy.create(2, 900, 0, 450, 300),
					BezierBy.create(2, -900, 0, -450, 300)
					), 1));
		}
	}
}
