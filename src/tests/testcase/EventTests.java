package tests.testcase;

import com.cocos2dj.base.Event;
import com.cocos2dj.base.EventDispatcher;
import com.cocos2dj.base.EventListenerKeyboard;
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
			
			//add touch listener
			EventListenerTouchOneByOne l = EventListenerTouchOneByOne.create();
			l.setOnTouchBeganCallback((touch, event)->{
				System.out.println("began" + touch.getLocation());
				return true;
			});
			l.setOnTouchMovedCallback((touch, event)->{
				System.out.println("moved > touch = " + touch);
			});
			l.setOnTouchEndedCallback((touch, event)->{
				System.out.println("end > touch = " + touch.getLocation());
			});
			ed.addEventListenerWithSceneGraphPriority(l, this);
			
			//add keylistener
			EventListenerKeyboard kl = EventListenerKeyboard.create();
			kl.setOnKeyPressedCallback((key, e)->{
				System.out.println("key is " + key);
			});
			ed.addEventListenerWithSceneGraphPriority(kl, this);
			
			DrawNode dn = new DrawNode();
			addChild(dn);
		}
		
		
		public String subtitle() {
			return "anchorPoint and children";
		}
	}
}
