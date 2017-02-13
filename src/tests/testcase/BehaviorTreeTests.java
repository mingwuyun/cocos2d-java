package tests.testcase;

import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.Task.Status;
import com.badlogic.gdx.ai.btree.branch.Selector;
import com.badlogic.gdx.ai.btree.branch.Sequence;
import com.cocos2dj.basic.BaseTimer;
import com.cocos2dj.module.btree.BhLeafTask;
import com.cocos2dj.module.btree.BhLeafTask.DebugTask;
import com.cocos2dj.module.btree.BhLeafTask.TaskListener;
import com.cocos2dj.module.btree.BhTree;
import com.cocos2dj.module.btree.BhTreeLoader;
import tests.TestCase;
import tests.TestSuite;

/**
 * 行为树插件测试
 * 
 * @author xu jun
 */
public class BehaviorTreeTests extends TestSuite {
	
	public BehaviorTreeTests() {
		addTestCase("BehaviorTreeTest1", ()->{return new BehaviorTreeTest1();});
	}
	
	static class BehaviorTreeTestDemo extends TestCase {
		
	}
	
	static class BehaviorTreeTest1 extends BehaviorTreeTestDemo {
		
		BehaviorTree<Object> bhController;
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public void onEnter() {
			super.onEnter();
			
			//loader from file 
			BhTreeLoader loader = new BhTreeLoader();
			BhTree bht = loader.parse("Resource/bh_test.json");
			
			bhController = new BehaviorTree<Object>();
			
			DebugTask checkA = (DebugTask) bht.getLeaf("checkA");// DebugTask("checkA");
			DebugTask checkB = (DebugTask) bht.getLeaf("checkB");
			DebugTask a = (DebugTask) bht.getLeaf("a");
			DebugTask b = (DebugTask) bht.getLeaf("b");
			DebugTask ALL1 = (DebugTask) bht.getLeaf("ALL1");
			DebugTask ALL2 = (DebugTask) bht.getLeaf("ALL2");
			
			
			checkB.setTaskListener(new TaskListener() {
				public void onStart(BhLeafTask task) {
					
				}
				public Status onExecute(BhLeafTask task) {
					return Status.SUCCEEDED;
				}
				public void onEnd(BhLeafTask task) {}
			});
			
			checkA.setTaskListener(new TaskListener() {
				public void onStart(BhLeafTask task) {
					
				}
				public Status onExecute(BhLeafTask task) {
					if(Math.random() < 0.2) {
						return Status.SUCCEEDED;
					}
					return Status.FAILED;
				}
				public void onEnd(BhLeafTask task) {}
			});
			a.setTaskListener(new TaskListener() {
				public void onStart(BhLeafTask task) {
					
				}
				public Status onExecute(BhLeafTask task) {
					return Status.SUCCEEDED;
				}
				public void onEnd(BhLeafTask task) {}
			});
			b.setTaskListener(new TaskListener() {
				public void onStart(BhLeafTask task) {
					
				}
				public Status onExecute(BhLeafTask task) {
					return Status.SUCCEEDED;
				}
				public void onEnd(BhLeafTask task) {}
			});
			ALL1.setTaskListener(new TaskListener() {
				BaseTimer t = new BaseTimer(200);
				public void onStart(BhLeafTask task) {
					t.restart();
				}
				public Status onExecute(BhLeafTask task) {
					if(t.overTime()) {
						task.forceSuccess();
					}
					return Status.RUNNING;
				}
				public void onEnd(BhLeafTask task) {}
			});
			ALL2.setTaskListener(new TaskListener() {
				BaseTimer t = new BaseTimer(200);
				public void onStart(BhLeafTask task) {
					t.restart();
				}
				public Status onExecute(BhLeafTask task) {
					if(t.overTime()) {
						task.forceSuccess();
//						task.forceFail();
					}
					return Status.RUNNING;
				}
				public void onEnd(BhLeafTask task) {}
			});
			
			
			//code create tree
			Selector select = new Selector();
				Sequence sequenceA = new Sequence();
					sequenceA.addChild(checkA);
					sequenceA.addChild(a);
					sequenceA.addChild(ALL1);
					sequenceA.addChild(ALL2);
					
				Sequence sequenceB = new Sequence();
					sequenceB.addChild(checkB);
					sequenceB.addChild(b);
					sequenceB.addChild(ALL1);
					sequenceB.addChild(ALL2);
					
			select.addChild(sequenceA);
			select.addChild(sequenceB);
			
			bhController.addChild(select);
		}
		
		
		public String subtitle() {
			return "anchorPoint and children";
		}
	}
}
