package tests.testcase;

import com.badlogic.gdx.graphics.Color;
import com.cocos2dj.base.Rect;
import com.cocos2dj.s2d.ActionInterval.MoveTo;
import com.cocos2dj.s2d.ActionInterval.RotateBy;
import com.cocos2dj.s2d.DrawNode;
import com.cocos2dj.s2d.Node;
import com.cocos2dj.s2d.Sprite;

import tests.TestCase;
import tests.TestSuite;

/**
 * 节点相关测试
 * 
 * @author xujun
 */
public class NodeTests extends TestSuite {
	
	public NodeTests() {
		addTestCase("BoundingBoxTest", ()->{return new NodeTest2();});
		addTestCase("NodeTest4", ()->{return new NodeTest4();});
	}
	
	static class NodeTestDemo extends TestCase {
		
	}
	
	static class NodeTest2 extends NodeTestDemo {
		
		public void onEnter() {
			super.onEnter();
			
			Node node = Sprite.create("powered.png");
			addChild(node);
			
			node.setOnTransformCallback((n)->{
//				System.out.println("updateTransform = " + node.getPosition());
			});
			
			DrawNode dn = new DrawNode();
			addChild(dn);
			
			node.setContentSize(100, 100);
			
			DrawNode debugDraw = (DrawNode) DrawNode.create().addTo(this);
			
			node.setOnUpdateCallback((n, t)->{
				Rect r = node.getBoundingBox();
				debugDraw.drawRect(r.x, r.y, r.x + r.width, r.y + r.height, null);
			});
			node.scheduleUpdate();
			node.setAnchorPoint(0f, 0f);
			node.runAction(MoveTo.create(2, 1000, 500));
			node.runAction(RotateBy.create(2, 1000));
			
			
//			node.runAction(Move.create(2, 0, 100));
//			node.runAction(MoveBy.create(0.5f, 500, 100));
			
			node.setColor(Color.RED);
			node.setOpacity(0.5f);
		}
		
		
		public String subtitle() {
			return "anchorPoint and children";
		}
	}
	
	static class NodeTest4 extends NodeTestDemo {
		public void onEnter() {
			super.onEnter();
//			System.out.println("debug enter >>>>>> ");
		}
		
		public String subtitle() {
			return "tags";
		}
	}
}
