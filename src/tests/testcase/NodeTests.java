package tests.testcase;

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
//			System.out.println("debug enter >>>>>> ");
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
