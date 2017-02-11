package tests.testcase;

import com.cocos2dj.base.Event;
import com.cocos2dj.base.EventDispatcher;
import com.cocos2dj.base.EventListenerTouchOneByOne;
import com.cocos2dj.base.EventListenerTouchOneByOne.TouchCallback;
import com.cocos2dj.base.Touch;
import com.cocos2dj.s2d.DrawNode;
import com.cocos2dj.s2d.Node;

import tests.TestCase;
import tests.TestSuite;

/**
 * 事件测试<p>
 * 
 * @author xu jun
 */
public class EventTests extends TestSuite {
	
	public EventTests() {
		addTestCase("TouchEventTest", ()->{return new TouchEventTest();});
//		addTestCase("NodeTest4", ()->{return new NodeTest4();});
	}
	
	static class EventTestDemo extends TestCase {
		
	}
	
	static class TouchEventTest extends EventTestDemo {
		
		public void onEnter() {
			super.onEnter();
			
			Node node = Node.create();
			addChild(node);
			
			EventDispatcher ed = _director.getEventDispatcher();
			
			EventListenerTouchOneByOne l = EventListenerTouchOneByOne.create();
			l.setTouchCallback(new TouchCallback() {

				@Override
				public boolean onTouchBegan(Touch touch, Event event) {
					System.out.println("onTouchBegam!");
					return true;
				}

				@Override
				public void onTouchMoved(Touch touch, Event event) {
					
				}

				@Override
				public void onTouchEnded(Touch touch, Event event) {
					
				}

				@Override
				public void onTouchCancelled(Touch touch, Event event) {
					
				}
				
			});
			ed.addEventListenerWithSceneGraphPriority(l, this);
//			_e
//			node.setOnTransformCallback((n)->{
//				System.out.println("updateTransform = " + node.getPosition());
//			});
			
			DrawNode dn = new DrawNode();
			addChild(dn);
		}
		
		
		public String subtitle() {
			return "anchorPoint and children";
		}
	}
}
