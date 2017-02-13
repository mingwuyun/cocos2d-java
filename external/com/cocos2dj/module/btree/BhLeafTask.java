package com.cocos2dj.module.btree;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

/**
 * 包装任务——方便先构建结构再写逻辑
 * @author xj
 *
 */
public class BhLeafTask<E> extends LeafTask<E> {
	
	@SuppressWarnings("rawtypes")
	public static class DebugTask extends BhLeafTask {
		
		final String name;
		public DebugTask(String name) {
			this.name = name;
		}
		
		public void start() {
			System.out.println("[" + name + "]" + " start");
			super.start();
		}
		
		@Override
		public Status execute() {
			Status ret = super.execute();
//			System.out.println("[" + name + "]" + " start");
			return ret;
		}
		
		public void end() {
			System.out.println("[" + name + "]" + " end");
			super.end();
		}
		
	}
	
	public static interface TaskListener<E> {
		public void onStart(BhLeafTask<E>  task);
		public Status onExecute(BhLeafTask<E>  task);
		public void onEnd(BhLeafTask<E>  task);
//		public void onUpdate();		//常规更新，无论是否激活都会调用
		
		@SuppressWarnings("rawtypes")
		public static final TaskListener NULL = new TaskListener() {
			public void onStart(BhLeafTask task) {}
			public Status onExecute(BhLeafTask task) {return Status.FAILED;}
			public void onEnd(BhLeafTask task) {}
		};
	}
	
	
	@SuppressWarnings("unchecked")
	private TaskListener<E> listener = TaskListener.NULL;
//	protected boolean 		taskEndFlag = false;
	private Status			forceStatus = null;
	
	@SuppressWarnings("unchecked")
	public void setTaskListener(TaskListener<E>  listener) {
		this.listener = listener == null ? TaskListener.NULL : listener;
	}
	
//	public final void setEnd(boolean flag) {this.taskEndFlag = flag;}
//	public void setTaskEndFlag(boolean flag) {this.taskEndFlag = flag;}
//	public final boolean getEnd() {return taskEndFlag;}
//	public boolean getTaskEndFlag() {return taskEndFlag;}
	/**强制结束 */
//	public void () {taskEndFlag = true;}
	public void forceFail() {
		forceStatus = Status.FAILED;
	}
	public void forceSuccess() {
		forceStatus = Status.SUCCEEDED;
	}
	
	public void start() {
		forceStatus = null;
//		taskEndFlag = false;
		this.listener.onStart(this);
	}
	
	@Override
	public Status execute() {
		Status ret = listener.onExecute(this);
		if(forceStatus != null) {
			return forceStatus;
		}
		return ret;
	}
	
	public void end() {
		listener.onEnd(this);
	}

	@Override
	protected Task<E> copyTo(Task<E> arg0) {
		return arg0;
	}

}
