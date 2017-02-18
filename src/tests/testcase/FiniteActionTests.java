package tests.testcase;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.cocos2dj.macros.CC;
import com.cocos2dj.renderer.Texture;
import com.cocos2dj.renderer.TextureRegion;
import com.cocos2dj.s2d.ActionInstant.CallFunc;
import com.cocos2dj.s2d.ActionInterval.ActionFloat;
import com.cocos2dj.s2d.ActionInterval.Animate;
import com.cocos2dj.s2d.ActionInterval.BezierBy;
import com.cocos2dj.s2d.ActionInterval.BezierTo;
import com.cocos2dj.s2d.ActionInterval.DelayTime;
import com.cocos2dj.s2d.ActionInterval.FadeIn;
import com.cocos2dj.s2d.ActionInterval.FadeOut;
import com.cocos2dj.s2d.ActionInterval.FadeTo;
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
import com.cocos2dj.s2d.ActionInterval.TargetedAction;
import com.cocos2dj.s2d.ActionInterval.TintBy;
import com.cocos2dj.s2d.ActionInterval.TintTo;
import com.cocos2dj.s2d.Sprite;

import tests.TestCase;
import tests.TestSuite;

/**
 * 有限时间动作测试
 * 
 * @author xujun
 */
public class FiniteActionTests extends TestSuite {
	
	public FiniteActionTests() {
		addTestCase("TargetedAndFloatTest", ()->{return new ActionTargetedTest();});
		addTestCase("AnimationTest", ()->{return new ActionAnimaTest();});
		addTestCase("TintTest", ()->{return new ActionTintTest();});
		addTestCase("FadeTest", ()->{return new ActionFadeTest();});
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
			
			
			Sprite sprite3 = (Sprite) Sprite.create("powered.png").addTo(this);
			sprite3.setRect(0, 0, 100, 120);
			sprite3.setPosition(1100, 220);
			
			sprite3.runAction(Repeat.create(Sequence.create(
					BezierTo.create(2, 100, 220, 450, 500),
					BezierTo.create(2, 900, 300,  450, 500)
//					MoveBy.create(1, -900, -300)
					), 1));
			sprite3.runAction(MoveBy.create(4, 0, 300));
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
	
	
	//TODO FadeTest
	static class ActionFadeTest extends ActionManagerTest {
		
		public void onEnter() {
			super.onEnter();
			
			Sprite sprite1 = (Sprite) Sprite.create("powered.png").addTo(this);
			sprite1.setRect(0, 0, 100, 120);
			sprite1.setPosition(100, 220);
			sprite1.setAnchorPoint(0, 0);
//			sprite1.setCascadeOpacityEnabled(true);
			//单独运行ScaleBy
			sprite1.runAction(Repeat.create(Sequence.create(
					FadeTo.create(0.5f, 0.2f),
					FadeTo.create(1f, 1f)
					), 5));
			
			Sprite sprite2 = (Sprite) Sprite.create("powered.png").addTo(this);
			sprite2.setRect(0, 0, 100, 120);
			sprite2.setPosition(100, 420);
			
			//FadeIn
			sprite2.setOpacity(0f);
			sprite2.runAction(FadeIn.create(4));
			sprite2.runAction(Repeat.create(Sequence.create(
					BezierBy.create(2, 900, 0, 350, 100),
					BezierBy.create(2, -900, 0, -350, 100)
					), 1));
			
			
			Sprite sprite3 = (Sprite) Sprite.create("powered.png").addTo(this);
			sprite3.setRect(0, 0, 100, 120);
			sprite3.setPosition(100, 220);
			
			//FadeOut
			sprite3.setOpacity(1f);
			sprite3.runAction(Repeat.create(Sequence.create(
					BezierBy.create(2, 900, 0, 450, 300),
					BezierBy.create(2, -600, 0, -450, 300),
					FadeOut.create(1f)
					), 1));
		}
	}
	
	//TODO TintTest
	static class ActionTintTest extends ActionManagerTest {
		
		public void onEnter() {
			super.onEnter();
			
			Sprite sprite1 = (Sprite) Sprite.create("powered.png").addTo(this);
			sprite1.setRect(0, 0, 100, 120);
			sprite1.setPosition(100, 220);
			sprite1.setAnchorPoint(0, 0);
			
			//单独运行TintBy
			sprite1.runAction(Repeat.create(Sequence.create(
					TintBy.create255(1f, -255, 0f, -255),
					TintBy.create(1f, 1f, 0, 1f)
					), 5));
			
			Sprite sprite2 = (Sprite) Sprite.create("powered.png").addTo(this);
			sprite2.setRect(0, 0, 100, 120);
			sprite2.setPosition(100, 420);
//			
//			//TintTo
			sprite2.runAction(Repeat.create(Sequence.create(
					TintTo.create255(0.5f, 255, 0, 0),
					DelayTime.create(0.5f),
					TintTo.create(0.5f, 0, 1, 0),
					DelayTime.create(.5f),
					TintTo.create(0.5f, 0, 0, 1),
					DelayTime.create(.5f)
					), 5));
			sprite2.runAction(Repeat.create(Sequence.create(
					DelayTime.create(5),
					BezierBy.create(2, 900, 0, 350, 100),
					BezierBy.create(2, -900, 0, -350, 100)
					), 1));
		}
	
	}
	
	
	//TODO AnimaTest
	static class ActionAnimaTest extends ActionManagerTest {
		
		public void onEnter() {
			super.onEnter();
			
			Texture t = CC.LoadImage("walkanim.png");
			TextureRegion[] ts = t.splitRowCol(1, 4);	//
			
			for(int i = 0; i < 4; ++i) {
				Sprite.createWithSpriteFrame(ts[i]).addTo(this).setPosition(100 + 100 * i, 200);
			}
			
			Animation animation = new Animation(3/30f, ts);
			Sprite spriteAnimation = (Sprite) Sprite.create().addTo(this);
			
			//修正sprite大小等动画 size设置无效
			spriteAnimation.runAction(RepeatForever.create(
					Animate.create(animation, true)));
			spriteAnimation.setContentSize(100, 100);
			spriteAnimation.setPosition(600, 320);
			
			//不修正sprite大小的动画
			Sprite spriteAnimation2 = (Sprite) Sprite.create().addTo(this);
			spriteAnimation2.runAction(RepeatForever.create(
					Animate.create(animation)));
			
			spriteAnimation2.setContentSize(100, 100);
			spriteAnimation2.setPosition(800, 320);
		}
	
	}
	
	//TODO Targeted and Float Test
	static class ActionTargetedTest extends ActionManagerTest {
		
		public void onEnter() {
			super.onEnter();
			
			Sprite sprite1 = (Sprite) Sprite.create("powered.png").addTo(this);
			Sprite sprite2 = (Sprite) Sprite.create("powered.png").addTo(this);
			Sprite sprite3 = (Sprite) Sprite.create("powered.png").addTo(this);
			sprite1.setScale(0.5f);
			sprite2.setScale(0.5f);
			sprite3.setScale(0.5f);
			
			sprite1.setPosition(100, 200);
			sprite2.setPosition(400, 200);
			sprite3.setPosition(1000, 200);
			
			JumpTo spr1JumpTo = JumpTo.create(1f, sprite2.getPosition(), 400, 1);
			TargetedAction t1 = TargetedAction.create(sprite1, Sequence.create(spr1JumpTo, 
					FadeOut.create(0.1f)));
			
			TargetedAction t2 = 
					TargetedAction.create(sprite2, 
					Sequence.create(
						MoveTo.create(0.2f, sprite3.getPosition()),
						CallFunc.create(()->{
							sprite2.runAction(MoveTo.create(0.3f, 600, 800));
						})));
			
			TargetedAction t3 = TargetedAction.create(sprite3, MoveTo.create(0.3f, 1100, 800));
			
			this.runAction(Sequence.create(t1, t2, t3));
			
			
			runAction(ActionFloat.create(1, 0, 100, (f) -> {System.out.println("value = " + f);}));
		}
	
	}
}
