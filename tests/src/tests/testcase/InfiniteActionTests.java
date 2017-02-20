package tests.testcase;

import com.cocos2dj.s2d.ActionCondition.Bezier;
import com.cocos2dj.s2d.ActionCondition.Jump;
import com.cocos2dj.s2d.ActionCondition.Move;
import com.cocos2dj.s2d.Node;
import com.cocos2dj.s2d.Sprite;

import tests.TestCase;
import tests.TestSuite;

/**
 * 不限制结束时间对动作测试
 * 
 * @author xujun
 */
public class InfiniteActionTests extends TestSuite {
	
	public InfiniteActionTests() {
		addTestCase("InfiniteMove", ()->{return new InfiniteMoveTest();});
	}
	
	static class TestDemo extends TestCase {
		
	}
	
	static class InfiniteMoveTest extends TestDemo {
		
		public void onEnter() {
			super.onEnter();
			
			Node node = Sprite.create("powered.png");
			node.setContentSize(50, 50);
			addChild(node);
			
			//move
			Move action = Move.create(-1, 600, 0);
			// move when position > 600
			action.setUpdateCallback((self, t)->{
//				System.out.println("t = " + t);
				if(node.getPositionX() > 600) {
					return true;
				}
				return false;
			});
			node.runAction(action);
			
			//bezier
			Bezier bz = new Bezier();
			bz.initWithEndTime(-1, 5, 500, 0, 250, 200, 250, 200);
			Node node2 = Sprite.create("powered.png").addTo(this);
			node2.setContentSize(50, 50);
			node2.setPosition(100, 200);
			node2.runAction(bz);
			node2.runAction(Move.create(-1, 100, 0));		// 叠加move
			
			//jump
			Jump jp = new Jump();
			jp.initWithEndTime(-1, 3f, -1000, 0, 540);
			Node node3 = Sprite.create("powered.png").addTo(this);
			node3.setContentSize(50, 50);
			node3.setPosition(1000, 100);
			node3.runAction(jp);
		}
	}
	
}
