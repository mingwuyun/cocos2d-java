package tests;

import com.badlogic.gdx.utils.Array;
import com.cocos2dj.base.Director;
import com.cocos2dj.protocol.IFunctionZeroArg;
import com.cocos2dj.protocol.IScene;

/**
 * suite: [case, case,]
 * 
 * @author xujun
 */
public class TestSuite extends TestBase {

	public static TestCase getTestCase(IScene scene) {
		//TODO unfinished
		return (TestCase) scene;
	}
	
	
	public void addTestCase(String testName, IFunctionZeroArg<IScene> callback) {
		if(testName != null && callback != null) {
			_childTestNames.add(testName);
			_testCallbacks.add(callback);
		}
	}
	
	public void restartCurrTest() {
		IScene scene = _testCallbacks.get(_currTestIndex).callback();
		TestCase testCase = getTestCase(scene);
		testCase.setTestSuite(this);
		testCase.setTestCaseName(_childTestNames.get(_currTestIndex));
		Director.getInstance().replaceScene(scene);
	}
	
	public void enterNextTest() {
		_currTestIndex = (_currTestIndex + 1) % _childTestNames.size;
		
		IScene scene = _testCallbacks.get(_currTestIndex).callback();
		TestCase testCase  = getTestCase(scene);
		testCase.setTestSuite(this);
		testCase.setTestCaseName(_childTestNames.get(_currTestIndex));
		
		Director.getInstance().replaceScene(scene);
	}
	
	public void enterPreviousTest() {
		if(_currTestIndex > 0) {
			_currTestIndex -= 1;
		} else {
			_currTestIndex = _childTestNames.size - 1;
		}
		IScene scene = _testCallbacks.get(_currTestIndex).callback();
		TestCase testCase  = getTestCase(scene);
		testCase.setTestSuite(this);
		testCase.setTestCaseName(_childTestNames.get(_currTestIndex));
		
		Director.getInstance().replaceScene(scene);
	}
	
	public int getCurrTestIndex() {return _currTestIndex;}
	
	
	@Override
	public void runThisTest() {
		if(_childTestNames.size > 0) {
			TestController.getInstance().setCurrTestSuite(this);
			
			_currTestIndex = 0;
			IScene scene = _testCallbacks.get(0).callback();
			TestCase testCase = getTestCase(scene);
			
			testCase.setTestSuite(this);
			testCase.setTestCaseName(_childTestNames.get(_currTestIndex));
			Director.getInstance().replaceScene(scene);
		}
	}
	
	
	//fields>>
	Array<IFunctionZeroArg<IScene>> _testCallbacks = new Array<>();
	int _currTestIndex;
	//fields<<
}
