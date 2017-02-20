package com.cocos2dj.basic;

import java.util.Arrays;
import java.util.Comparator;

/**
 * BaseScheduler.java
 * <br>BaseUpdaterSort
 * <br>UpdateLinkedArray
 * <p>
 * 
 * 基础调度器 提供执行Updater的功能，并提供优先级支持 优先级越低，越先执行 <br>
 * 
 * 由于使用了 {@link UpdateLinkedArray} 更新对象，避免了迭代更新中修改原始数据的问题，
 * 因此没有添加锁对象来防止原始数据修改
 * 
 * Updater类型：Main，Render。
 * 分别对应：
 * <li>Main:   逻辑线程中执行
 * <li>Render：渲染线程中执行
 * 
 * @author Copyright (c) 2017 xu jun
 */
public class BaseScheduler implements IDisposable {
	
	//class>>
	/**排序 */
	public static class BaseUpdaterSort implements Comparator<BaseUpdater> {
		@Override
		public final int compare(final BaseUpdater a, final BaseUpdater b) {
			if(a == null || b == null) {
				return 0;
			}
//			return b.getPriority() - a.getPriority();
			return a.getPriority() - b.getPriority();
		}

	}
	
	/*
	 * 为Updatable对象特别定制的链表数组
	 */
	static final BaseUpdaterSort sort = new BaseUpdaterSort();
	
	static final class UpdateLinkedArray {
		boolean needModified;
		boolean needSort;
		private BaseUpdater first, last;
		private BaseUpdater updates[] = new BaseUpdater[32];
		private int size;
		
		public final void clear() {
			needModified = false;
			needSort = false;
			updates = new BaseUpdater[32];

			for(BaseUpdater curr = first; curr != null; ) {
				BaseUpdater next = curr._list_next;//curr.list_next;
				curr._list_next = null;
				curr._list_prev = null;
				curr.reset();
				curr = next;
			}
			first = last = null;
			size = 0;
		}
		
		public void removeUpdatable(BaseUpdater update) {
			update.end();
			update.reset();
			this.remove(update);
		}
		
		public void addUpdatable(BaseUpdater update) {
			update.attach();
			//当前的优先级小于最后的元素，需要重写排列
			if(last != null && update.getPriority() < this.last.getPriority()) {
				requestSort();
			}
			this.add(update);
		}
		
		public final int size() {
			return size;
		}
		
		public final void requestSort() {
			this.needSort = true;
		}
		
		
		final void add(final BaseUpdater update) {
			this.needModified = true;
			
			update._list_prev = last;
			
			if(last == null) {
				first = update;
			} else {
				last._list_next = update;
			}
			last = update;
			
			++size;
		}
		
		/**
		 * 把一个Updatable从列表中移除
		 * 注意这个方法仅仅移除对象而已
		 * 
		 * @param target
		 */
		final void remove(final BaseUpdater update) {
			this.needModified = true;
			
			final BaseUpdater next = update._list_next;
	        final BaseUpdater prev = update._list_prev;
	        
	        if (prev == null) {
	            first = next;
	        } else {
	            prev._list_next = next;
	            update._list_prev = null;
	        }

	        if (next == null) {
	            last = prev;
	        } else {
	            next._list_prev = prev;
	            update._list_next = null;
	        }
	        
			--size;
		}
		
		/**
		 * 更新Updatable 如果这个updatable有后续updatable则返回
		 * 否则返回null
		 * 
		 * @param updatable
		 * @param dt
		 * @return
		 */
		final BaseUpdater handleUpdatable(final BaseUpdater updatable, float dt) {
			if(updatable.isStop()) {
				updatable.reset();
				remove(updatable);
				return null;
			}
			
			if(updatable.isKill()) {
				updatable.end();
				final BaseUpdater ret;
				
				if(updatable.hasNext()) {ret = updatable.next;}
				else 					{ret = null;}
				
				updatable.reset();
				remove(updatable);
				return ret;
			}
			
			if(!updatable.isPaused()) {
				if(updatable.execute(dt)) {
					updatable.end();
					BaseUpdater ret = updatable.getNext();
					updatable.reset();
					remove(updatable);
					return ret;
				}
			}
			return null;
		}
		
		/**
		 * 更新改列表
		 * @param dt
		 */
		public final void update(final BaseScheduler schedule, final float dt) {
			if(this.needModified) {
				needModified = false;
				needSort = true;
				
				BaseUpdater temp = first;
				if(updates.length < size) {//创建数据副本
					updates = new BaseUpdater[size];
				}
				else {//删除多余元素
					for(int i = size, len = updates.length; i < len; ++i) {
						updates[i] = null;
					}
				}
				
				//遍历中不可直接处理对象——涉及到链表的遍历问题
				//遍历统一到后面的数组中执行
				for(int i = 0; i < size; ++i) {
					updates[i] = temp;
					temp = temp._list_next;
				}
			}
			
			// 优先级排序
			if(needSort) {
				needSort = false;
				Arrays.sort(updates, sort);
			}
			
			//使用副本锁定size尺寸否则在updatable中添加新的update后size会直接更改导致错误
			final int _size = size;	
			for(int i = 0; i < _size; ++i) {
				final BaseUpdater updatable = updates[i];
//				System.out.println("system herer >>> " + i + " " + updatable.getPriority());
				BaseUpdater ret = this.handleUpdatable(updatable, dt);
				if(ret != null) {
					schedule.add(ret, false);
				}
			}
		}
	}
	//class<<
	
	////////////////////////////
	
	//fields>>
	//scene同样放到main中执行，因此取消first相关内容
//	private final UpdateLinkedArray updates_first = new UpdateLinkedArray();
	private final UpdateLinkedArray updates_main = new UpdateLinkedArray();
	private final UpdateLinkedArray updates_render_before = new UpdateLinkedArray();
	private final UpdateLinkedArray updates_render_after = new UpdateLinkedArray();
	
	float timeScale = 1f;
	//fields<<
	
	
	private static BaseScheduler instance;
	public static final BaseScheduler instance() {
		if(instance == null) {
			instance = new BaseScheduler();
			Engine.registerDisposable(instance);
		}
		return instance;
	}
	private BaseScheduler(){}
	
	
	
	///////////////////////////
	
	//methods>>
	public final void setTimeScale(float timeScale) {
		this.timeScale = timeScale;
	}
	
	public final float getTimeScale() {
		return timeScale;
	}
	
    /**
     * 添加更新对象，参数forceAdd：如果action已经被设置了移除状态
     * 但是没有unattached（kill遍历时生效），会强制取消kill状态，即取消本次移除。
     * 
     * 如果是false，则不会取消移除，返回添加失败
     * 
     * @param action
     * @param forceAdd 强制添加
     * @return 添加是否成功
     */
    public final boolean add(final BaseUpdater action, boolean forceAdd) {
    	if(!action.isAttached()) {	//该对象没有被添加
			switch(action.type) {
//			case First:
//				this.updates_first.addUpdatable(action);
//				break;
			case Main:
				this.updates_main.addUpdatable(action);
				break;
			case RenderBefore:
				this.updates_render_before.addUpdatable(action);
				break;
			case RenderAfter:
				this.updates_render_after.addUpdatable(action);
				break;
			}
			return true;
		} else if(forceAdd) {
			if(action.isKill()) {
				action.end();
				action.clearKill();
				return true;
			}
		}
		return false;
    }
    
    
    public final boolean remove(final BaseUpdater update) {
    	if(!update.isAttached()) {
    		return false;
    	}
    	switch(update.type) {
//    	case First:
//    		this.updates_first.removeUpdatable(update);
//    		break;
    	case Main:
    		this.updates_main.removeUpdatable(update);
    		break;
    	case RenderBefore:
    		this.updates_render_before.removeUpdatable(update);
    		break;
    	case RenderAfter:
    		this.updates_render_after.removeUpdatable(update);
    		break;
    	}
    	return true;
    }
//    public final 
    
//    public final void requ
//    public final void updateFirst(float dt) {
//    	dt *= timeScale;
//    	this.updates_first.update(this, dt);
//    }
    
    public final void updateMain(float dt) {
    	dt *= timeScale;
    	this.updates_main.update(this, dt);
    }
    
    public final void updateRenderBefore(float dt) {
    	dt *= timeScale;
    	this.updates_render_before.update(this, dt);
    }
    
    public final void updateRenderAfter(float dt) {
    	dt *= timeScale;
    	this.updates_render_after.update(this, dt);
    }

	/**清除所有动作 */
	public final void clearSchedule() {
		
//		updates_first.clear();
		updates_main.clear();
		updates_render_before.clear();
		updates_render_after.clear();
		
	}
	
	/**清除数据*/
	public final void dispose(){
//		updates_first.clear();
		updates_main.clear();
		updates_render_before.clear();
		updates_render_after.clear();
		instance = null;
	}
	//methods<<
	
	///////////////////////
	/**Debug方法 */
	public void showState(){
		System.out.println("ActionManager:");
//		System.out.println("first: " + updates_first.size);
		System.out.println("main: " + updates_main.size);
		System.out.println("render before" + updates_render_before.size);
		System.out.println("render after" + updates_render_after.size);
//		System.out.println("RactionList: " + renderThreadUpdateList.size);
//		System.out.println("tempStack: " + mainThreadTempUpdateList.size);
//		System.out.println("actionList: " + mainThreadUpdateList.size);
//		System.out.println("RtempStack: " + renderThreadTempUpdateList.size);
//		System.out.println("RactionList: " + renderThreadUpdateList.size);
	}
}
