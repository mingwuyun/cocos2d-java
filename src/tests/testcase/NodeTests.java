package tests.testcase;

import com.cocos2dj.protocol.IComponent;
import com.cocos2dj.s2d.Node;

import tests.TestCase;
import tests.TestSuite;

/**
 * 节点相关测试
 * 
 * @author xujun
 */
public class NodeTests extends TestSuite {
	
	public NodeTests() {
		addTestCase("NodeTest2", ()->{return new NodeTest2();});
		addTestCase("NodeTest4", ()->{return new NodeTest4();});
	}
	
	static class NodeTestDemo extends TestCase {
		
	}
	
	static class NodeTest2 extends NodeTestDemo {
		
		public void onEnter() {
			super.onEnter();
			
			Node node = Node.create();
			addChild(node);
			
			node.setOnTransformCallback((n)->{
				System.out.println("updateTransform = " + node.getPosition());
			});
			
//			node.setOnUpdateCallback((n, dt) -> {
//				if(node.getTransformDirty()) {
////					System.out.println(node.getTransformDirty());
//				}
//			});
//			node.scheduleUpdate();
			
//			System.out.println("debug enter >>>>>> ");
			schedule((t)->{
				System.out.println("move");
				node.setPosition(1 + node.getPositionX(), 10);
				return false;
			}, 1);
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
