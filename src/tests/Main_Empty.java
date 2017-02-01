package tests;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.cocos2dj.platform.desktop.ApplicationStartup;
import com.cocos2dj.utils.ObjectLinkedList;

import tests.nodetest.TestNodeAppDelegate;


public class Main_Empty {

	public static void main(String[] args) {
		LwjglApplicationConfiguration conf = ApplicationStartup.getConfiguration();
//		conf.width = 	(int) (320);	conf.height = (int) (180);
//		conf.width = 	(int) (200);	conf.height = (int) (200);
		conf.width = 	800;  	conf.height = 	450; 
//		conf.width = 	1200;  	conf.height = 	680; 
//		conf.x = 		50;
//		conf.y = 		200;
//		ApplicationStartup.start(new TestAppDelegate());
		ApplicationStartup.start(new TestNodeAppDelegate());
		
//		HashMap<Float, Object> test = new HashMap<>();
//		test.put(0.1f, new Object());
//		test.put(0.3f, new Object());
//		test.put(0.5f, new Object());
//		
//		Iterator<Entry<Float, Object>> it = test.entrySet().iterator();
//		for(;it.hasNext();) {
//			it.next();
//			test.remove(0.1f);
//		}
		
//		Float[] globalZOrders = (Float[]) Array.newInstance(Float.class, test.size());
//        test.keySet().toArray(globalZOrders);
//        
//        for(float a : globalZOrders) {
//        	System.out.println("float = " + a);
//        }
//		class A implements ObjectLinkedList.ILinkedObject<A> {
//			String msg;
//			A _next;
//			A _prev;
//			public A(String msg) {
//				this.msg = msg;
//			}
//			public String toString() {
//				return msg;
//			}
//			@Override
//			public void _set_next(A next) {
//				_next = next;
//			}
//		
//			@Override
//			public void _set_prev(A prev) {
//				_prev = prev;
//			}
//		
//			@Override
//			public A get_next() {
//				return _next;
//			}
//		
//			@Override
//			public A get_prev() {
//				return _prev;
//			}
//		}
//
//		ObjectLinkedList<A> list = new ObjectLinkedList<A>();
//		list.add(new A("1"));
//		list.add(new A("2"));
//		list.add(new A("3"));
//		list.add(new A("4"));
//		list.add(new A("5"));
//		list.add(new A("6"));
//		
//		Iterator<A> it = list.iterator();
//		while(it.hasNext()) {
//			A a = it.next();
//			if(!a.msg.equals("3")) {
//				it.remove();
//			}
//			
//			System.out.println(a);
//		}
//		
//		it = list.iterator();
//		while(it.hasNext()) {
//			A a = it.next();
//			System.out.println(a);
//		}
//		
//		it = list.iterator();
//		while(it.hasNext()) {
//			A a = it.next();
//			System.out.println(a);
//		}
	}

	
	
//	static class FrameTimer {
//		int count = 1;
//		boolean pause;
//		public FrameTimer(int frame) {
//			this.count = frame;
//		}
//		public void setup(int frame) {
//			count = frame;
//		}
//		public void update() {
//			count--;
//		}
//		public boolean overTime() {
//			if(pause) return false;
//			return count < 0;
//		}
//	}
	
//	static AppDelegate appDelegate = new AppDelegate() {
//		
//		@Override
//		public void initConfiguration() {
//			
//		}
//		
//		@Override
//		public void applicationWillEnterForeground() {
//			
//		}
//		
////		FrameTimer t = new FrameTimer(200);
////		FrameTimer t2 = new FrameTimer(400);
////		Object k2 = new Object();
//		
//		@Override
//		public boolean applicationDidFinishLaunching() {
//			Scheduler _scheduler = Director.getInstance().getScheduler();
//			_scheduler.schedule((t)->{
//				System.out.println("k2s show it !");
//				return false;
//			}, this, 1, 10, 1, false);
//			
//			_scheduler.schedule((t)->{
//				System.out.println("1s show it !");
//				return false;
//			}, this, 1, 10, 1, false);
//			
//			_scheduler.scheduleUpdate((dt)->{
//				t.update();
//				t2.update();
//				
//				if(t.overTime()) {
//					t.pause = true;
//					_scheduler.unscheduleAllForTarget(this);
//				}
//				if(t2.overTime()) {
//					t2.pause = true;
//					_scheduler.unscheduleAllForTarget(k2);
////					_scheduler.pauseTarget(k2);
//				}
//				return false;
//			}, 0, false);
//		    return true;
//		}
//		
//		@Override
//		public void applicationDidEnterBackground() {
//			
//		}
//	};
}
