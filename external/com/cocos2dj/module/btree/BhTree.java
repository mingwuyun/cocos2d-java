package com.cocos2dj.module.btree;

import java.util.HashMap;

import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.branch.Parallel;
import com.badlogic.gdx.ai.btree.branch.Parallel.Policy;
import com.badlogic.gdx.ai.btree.branch.Selector;
import com.badlogic.gdx.ai.btree.branch.Sequence;
import com.cocos2dj.macros.CCLog;
import com.cocos2dj.module.btree.BhTreeModel.StructBHTNode;
import com.cocos2dj.module.btree.BhLeafTask.DebugTask;

/**
 * RcBHT.java
 * 
 * 不用管类型
 * 
 * @author xujun
 *
 */
public class BhTree<T> extends BehaviorTree<T> {
	
	static final String TAG = "RcBHT";
	
	//fields>>
	HashMap<String, BhLeafTask<T>> tasksMap = new HashMap<>();
	private boolean pauseFlag;
	//fields<<
	
	public void pause() {
		pauseFlag = true;
	}
	
	public void resume() {
		pauseFlag = false;
	}
	
	//func>>
	@SuppressWarnings("unchecked")
	Task<T> createTask(String type, String name, String args) {
		switch(type) {
		case "leaf":
			BhLeafTask<T> leaf;// = new RcLeafTask<T>();
			//相同的name会缓存
			if(name != null) {
				leaf = tasksMap.get(name);
				if(leaf != null) {
					CCLog.debug(TAG, "find same leaf task : " + name);
					return leaf;
				}
				if("debug".equals(args)) {
					leaf = new DebugTask(name);
				} else {
					leaf = new BhLeafTask<T>();
				}
				tasksMap.put(name, leaf);
				return leaf;
			} else {
				if("debug".equals(args)) {
					leaf = new DebugTask(name);
				} else {
					leaf = new BhLeafTask<T>();
				}
			}
			return leaf;
		case "parallel":
			if(args == null) {
				return new Parallel<T>();
			} else {
				switch(args) {
				case "selector":
					return new Parallel<T>(Policy.Selector);
				case "sequence":
					return new Parallel<T>(Policy.Sequence);
				}
				CCLog.error(TAG, "pattern fail args = " + args + "  need selector or sequence");
				return null;
			}
		case "selector":
			return new Selector<T>();
		case "sequence":
			return new Sequence<T>();
		}
		CCLog.error(TAG, "not found type : " + type);
		return null;
	}
	
	final Task<T> createTask(StructBHTNode node) {
		Task<T> ret = createTask(node.type, node.key, node.args);
		if(ret == null) {
			CCLog.error(TAG, "createTask fail ");
		}
		if(node.children != null) {
			int len = node.children.length;
			for(int i = 0; i < len; ++i) {
				Task<T> child = createTask(node.children[i]);
				ret.addChild(child);
			}
		}
		return ret;
	}
	//func<<
	
	
	//methods>>
	public void setup(BhTreeModel model) {
		Task<T> root = createTask(model.root);
		addChild(root);
	}
	
	public void step() {
		if(!pauseFlag) {
			super.step();
		}
	}
	
	public BhLeafTask<T> getLeaf(String key) {
		BhLeafTask<T> ret = tasksMap.get(key);
//		System.out.println("map = " + tasksMap.toString());
		if(ret == null) {
			CCLog.error(TAG, "task not found : " + key);
		}
		return ret;
	}
	//methods<<
	
	
}
