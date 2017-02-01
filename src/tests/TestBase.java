package tests;

import com.badlogic.gdx.utils.Array;

/**
 * 测试用例
 * 
 * @author xujun
 */
public abstract class TestBase {
	
	public void backsUpOneLevel() {
		if(_parentTest != null) {
			_parentTest.runThisTest();
		}
	}
	
	public abstract void runThisTest();
	
	public boolean isTestList() {return _isTestList;}
	public int getChildTestCount() {return _childTestNames.size;}
	
	public void _setTestParent(TestBase parent) {_parentTest = parent;} 
	TestBase getTestParent() {return _parentTest;}
	
	public void setTestName(String name) {_testName = name;}
	public String getTestName() {return _testName;}
	
	//fields>>
	String _testName;
	TestBase _parentTest = null;
	boolean _isTestList = false;
	Array<String> _childTestNames = new Array<>();
	//fields<<
}
