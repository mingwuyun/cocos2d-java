package tests;

import com.cocos2dj.base.Director;
import com.cocos2dj.base.Event;
import com.cocos2dj.base.EventListenerTouchOneByOne;
import com.cocos2dj.base.Touch;

import tests.testcase.ActionManagerTests;
import tests.testcase.Base2dTests;
import tests.testcase.Base2dTests2;
import tests.testcase.BehaviorTreeTests;
import tests.testcase.CameraTests;
import tests.testcase.DrawNodeTests;
import tests.testcase.EventTests;
import tests.testcase.FiniteActionTests;
import tests.testcase.InfiniteActionTests;
import tests.testcase.NodeTests;
import tests.testcase.ShaderTests;
import tests.testcase.SpriteTests;
import tests.testcase.TextureAtlasTests;
import tests.testcase.TiledMapTests;
import tests.testcase.TypeFactoryTests;
import tests.testcase.VisUITests;

/**
 * 测试
 * 
 * @author xujun
 */
public class TestController {
	
	private static TestController _instance; 
    public static TestController getInstance() {
    	if(_instance == null) {
    		_instance = new TestController();
    	}
    	return _instance;
    }
    
    public static void destroyInstance() {
    	
    }

    
    public void startAutoTest() {
    	
    }
    public void stopAutoTest() {
    	
    }

    public void handleCrash() {
    	
    }

    public void onEnterBackground() {
    	
    }
    public void onEnterForeground() {
    	
    }

    public boolean blockTouchBegan(Touch touch, Event event) {
    	
    	return false;
    }

    public void setCurrTestSuite(TestSuite testSuite) { _testSuite = testSuite;}
    TestSuite getCurrTestSuite() { return _testSuite; }
    
    private TestController() {
    	
    }


    private void traverseThreadFunc() {
    	
    }
    private void traverseTestList(TestList testList) {
    	
    }
    private void traverseTestSuite(TestSuite testSuite) {
    	
    }
    
    private boolean checkTest(TestCase testCase) {
    	
    	return true;
    }

    private void logEx(String msg) {
    	
    }

    private boolean _stopAutoTest;
    private boolean _isRunInBackground;

    private TestList _rootTestList;
    private TestSuite _testSuite;

    private Thread _autoTestThread;

//    std::condition_variable _sleepCondition;
//    std::unique_lock<std::mutex>* _sleepUniqueLock;

    private Director _director;
    private EventListenerTouchOneByOne _touchListener;
    private String _logIndentation;

    
    /**载入初始场景*/
    public void start() {
    	_rootTestList = new RootTests();
    	_rootTestList.runThisTest();
    }
    
    ////////////////////////////////////
    //TODO RootTests
    static class RootTests extends TestList {
    	public RootTests() {
//    		System.out.println("d = " + this + "  " + _director);
    		addTest("TiledMapTests", ()->{return new TiledMapTests();});
    		addTest("TextureAtlasTests", ()->{return new TextureAtlasTests();});
    		addTest("Base2dTestsNormal", ()->{return new Base2dTests2();});
    		addTest("NodeTests", ()->{return new NodeTests();});
    		addTest("EventTests", ()->{return new EventTests();});
    		addTest("ActionManagerTests", ()->{return new ActionManagerTests();});
    		addTest("FiniteActionTests", ()->{return new FiniteActionTests();});
    		addTest("InfiniteActionTests", ()->{return new InfiniteActionTests();});
    		addTest("SpriteTests", ()->{return new SpriteTests();});
    		addTest("CameraTests", ()->{return new CameraTests();});
    		addTest("ShaderTests", ()->{return new ShaderTests();});
    		addTest("Base2DPhysicsTests", ()->{return new Base2dTests();});
    		addTest("DrawNodeTests", ()->{return new DrawNodeTests();});
    		addTest("TypeFactoryTests", ()->{return new TypeFactoryTests();});
    		addTest("VisUITests", ()->{return new VisUITests();});
    		addTest("BehaviorTreeTests", ()->{return new BehaviorTreeTests();});
    		addTest("test_test7", ()->{return new TestCaseSimple();});
    	}
    	
    }
    
    static class MyTestCase extends TestCase {
    	public void onEnter() {
    		System.out.println("MyTestCase ");
    		super.onEnter();
    	}
    }
    
    static class TestCaseSimple extends TestSuite {
    	
    	public TestCaseSimple() {
    		addTestCase("test1", ()->{return new MyTestCase();});
    	}
    	
		@Override
		public void runThisTest() {
			System.out.println("run this test .....");
			super.runThisTest();
		}
    	
    }
}
