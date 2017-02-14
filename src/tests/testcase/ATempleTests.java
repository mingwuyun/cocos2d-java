package tests.testcase;

import tests.TestCase;
import tests.TestSuite;

public class ATempleTests extends TestSuite {
	
	public ATempleTests() {
		addTestCase("Test", ()->{return new Test();});
	}
	
	static class TestDemo extends TestCase {
		
	}
	
	static class Test extends TestDemo {
		
		public void onEnter() {
			super.onEnter();
		}
	}
	
}
