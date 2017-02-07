package tests.testcase;

import com.cocos2dj.s2d.ActionInterval.MoveBy;
import com.cocos2dj.s2d.ActionInterval.Sequence;
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
//			scheduleOnce((t)->{
//				System.out.println("stop");
//				sprite1.stopAllActions();
//				return false;
//			}, 1.5f);
		}
	}
}
