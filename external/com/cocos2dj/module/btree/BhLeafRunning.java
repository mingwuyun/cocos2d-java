package com.cocos2dj.module.btree;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

public class BhLeafRunning<E> extends LeafTask<E> {

	@Override
	public com.badlogic.gdx.ai.btree.Task.Status execute() {
		return Status.RUNNING;
	}

	@Override
	protected Task<E> copyTo(Task<E> task) {
		return task;
	}

}
