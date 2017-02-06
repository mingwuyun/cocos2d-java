package tests.testcase;

import com.badlogic.gdx.graphics.Color;
import com.cocos2dj.s2d.DrawNode;

import tests.TestCase;
import tests.TestSuite;

/**
 * 图元绘制测试
 * 
 * @author xujun
 */
public class DrawNodeTests extends TestSuite {
	
	public DrawNodeTests() {
		addTestCase("drawNodeTest1", ()->{return new DrawNodeTest1();});
	}
	
	static class DrawNodeBase extends TestCase {
		
	}
	
	
	/////////////////////////////////////
	//TODO Test2
	static class DrawNodeTest1 extends DrawNodeBase {
		
		DrawNode drawNode1;
		DrawNode drawNode2;
		
		public void onEnter() {
			super.onEnter();
			
			drawNode1 = (DrawNode) DrawNode.create().addTo(this);
			drawNode2 = (DrawNode) DrawNode.create().addTo(this);
			
			
			drawNode1.startBatch();	//链接绘制命令（draw后自动结束，不用调用end）
			drawNode1.drawSegment(100, 100, 500, 500, 0, Color.GOLD);
			drawNode1.drawSegment(100, 500, 500, 100, 5, Color.GREEN);
		}		
		
	}
}
