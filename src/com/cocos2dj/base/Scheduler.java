package com.cocos2dj.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.badlogic.gdx.utils.Array;
import com.cocos2dj.basic.BaseScheduler;
import com.cocos2dj.basic.BaseUpdater;
import com.cocos2dj.basic.BaseUpdater.BaseUpdateType;
import com.cocos2dj.macros.CCLog;
import com.cocos2dj.protocol.IUpdater;

/**
 * Scheduler.java <br>
 * Timer <br>
 * TimerTargetSelector <br>
 * TimerTargetCallback <br>
 * <p>
 * 
 * Cocos2d 版本调度器 基于使用 {@link #BaseSchedule} 实现<br>
 * 主要添加面向target的调度功能，以及定时器功能。可以基于对象调用更新
 * 
 * @author Copyright (c) 2017 xu jun
 */
public class Scheduler {
	
	//const>>
	public static final int CC_REPEAT_FOREVER = Integer.MAX_VALUE - 1;
	public static final String TAG = "Scheduler";
	//const<<
	
	
	//////////////////////
	//class>>
	final static class ScheduleUpdater extends BaseUpdater {
		IUpdater 			callback;
		Object				target;
		Scheduler			scheduler;
//		boolean				forceEndFlag = false;		//区分自移除和scheduler主动移除
		
		public ScheduleUpdater(Scheduler scheduler, IUpdater func, Object target) {
			callback = func;
			this.target = target;
			this.scheduler = scheduler;
//			forceEndFlag = false;
		}
		
		@Override
		protected boolean onUpdate(float dt) {
			return callback.update(dt);
		}

		@Override
		protected void onEnd() {
			if(target != null) {
//				if(!forceEndFlag) {
//					forceEndFlag = true;
					// 直接移除 
				scheduler.removeHashForUpdates(this);
//				}
			}
			callback = null;
			target = null;
			scheduler = null;
		}
	}

	//////////////////////
	
	final static class TimerUpdater extends BaseUpdater {
		IUpdater 				callback;
		Object					target;
		String 					key;
		Scheduler				scheduler;
//		boolean					forceEndFlag = false;		//区分自移除和scheduler主动移除
		private Timer 			timer = new Timer() {
			@Override
			public void trigger(float dt) {
				callback.update(dt);
			}
			@Override
			public void cancel() {
				kill();		//stop updater
			}
		};
		
		public TimerUpdater(Scheduler scheduler, IUpdater func, Object target, String key) {
			callback = func;
			this.target = target;
			this.key = key;
			this.scheduler = scheduler;
//			this.forceEndFlag = false;
		}
		
		public boolean initWithSelector(float seconds, int repeat, float delay) {
	        timer.setupTimerWithInterval(seconds, repeat, delay);
	        return true;
	    }
		
		@Override
		protected boolean onUpdate(float dt) {
//			System.out.println("update timer >>>");
			timer.update(dt / 1000);		// ms -> second
			return false;
		}

		@Override
		protected void onEnd() {
			if(target != null) {
//				if(!forceEndFlag) {
				scheduler.removeHashForTimers(this);
//					scheduler.unschedule(target, key);
//				}
			}
			scheduler = null;
			key = null;
			target = null;
			callback = null;
		}
	}

	//////////////////////
	
	/**该对象中所有方法都不操作Updater状态 */
	final static class struct_TargetUpdaterEntry {
		
		Object              	target;
	    Array<ScheduleUpdater>	schedules;
	    Array<TimerUpdater>		timers;
	    boolean 				pause;
	    
	    final boolean isPause() {
	    	return pause;
	    }
	    
	    final void setPause(boolean pause) {
	    	this.pause = pause;
	    	if(this.pause) {
	    		if(schedules != null) {
	    			for(int i = 0; i < schedules.size; ++i) {
	    				schedules.get(i).pause();
	    			}
	    		}
	    		if(timers != null) {
	    			for(int i = 0; i < timers.size; ++i) {
	    				timers.get(i).pause();
	    			}
	    		}
	    	} else {
	    		if(schedules != null) {
	    			for(int i = 0; i < schedules.size; ++i) {
	    				schedules.get(i).clearPaused();
	    			}
	    		}
	    		if(timers != null) {
	    			for(int i = 0; i < timers.size; ++i) {
	    				timers.get(i).clearPaused();
	    			}
	    		}
	    	}
	    }
	    
	    final boolean isEmpty() {
	    	return (schedules == null || schedules.size <= 0) &&
	    			(timers == null || timers.size <= 0);
	    }
	    
//	    final boolean findScheduleUpdater(IUpdater updater) {
//	    	if(schedules == null) {
//	    		return false;
//	    	}
//	    	
//	    	return schedules.contains(updater, true);
//	    }
//	    
//	    final boolean findTimerUpdater(IUpdater timer) {
//	    	return timers.contains(timer, true);
//	    }
	    
	    final void addSchedule(ScheduleUpdater schedule) {
	    	if(schedules == null) schedules = new Array<>();
	    	schedules.add(schedule);
	    }
	    
	    final ScheduleUpdater removeSchedule(ScheduleUpdater schedule) {
	    	if(schedules == null) return null;
	    	
	    	if(schedules.removeValue(schedule, true)) {
	    		return schedule;
	    	} else {
	    		return null;
	    	}
	    }
	    
	    final void addTimer(TimerUpdater timer) {
	    	if(timers == null) timers = new Array<>();
	    	timers.add(timer);
	    }
	    
	    final TimerUpdater removeTimer(TimerUpdater timer) {
	    	if(timers == null) return null;
	    	
	    	if(timers.removeValue(timer, true)) {
	    		return timer;
	    	} else {
	    		return null;
	    	}
	    }
	    
	    final void release() {
	    	if(timers != null) timers.clear();
	    	if(schedules != null) schedules.clear();
	    	timers = null;
	    	schedules = null;
	    }
	}
	
	//////////////////////
	
	/** 
	 * Timer
	 * @brief Light-weight timer
	 */
	public abstract static class Timer {
		public Timer() {}
		/** get interval in seconds */
		public float getInterval() { return _interval; }
		/** set interval in seconds */
		public void setInterval(float interval) { _interval = interval; };
		
		public void setupTimerWithInterval(float seconds, int repeat, float delay) {
			_elapsed = -1;
			_interval = seconds;
			_delay = delay;
			_useDelay = (_delay > 0.0f) ? true : false;
			_repeat = repeat;
			_runForever = (_repeat == CC_REPEAT_FOREVER) ? true : false;
		}
    
		public abstract void trigger(float dt);
		public abstract void cancel();
    
	    /** triggers the timer */
	    public void update(float dt) {
	    	if (_elapsed == -1) {
	            _elapsed = 0;
	            _timesExecuted = 0;
	            return;
	        }
	        _elapsed += dt;
	        // deal with delay
	        if (_useDelay) {
	            if (_elapsed < _delay) {return;}
	            trigger(_delay);
	            _elapsed = _elapsed - _delay;
	            _timesExecuted += 1;
	            _useDelay = false;
	            // after delay, the rest time should compare with interval
	            if (!_runForever && _timesExecuted > _repeat) {    
	                cancel();
	                return;
	            }
	        }
	        // if _interval == 0, should trigger once every frame
	        float interval = (_interval > 0) ? _interval : _elapsed;
	        while (_elapsed >= interval) {
	            trigger(interval);
	            _elapsed -= interval;
	            _timesExecuted += 1;

	            if (!_runForever && _timesExecuted > _repeat) {
	                cancel();
	                break;
	            }

	            if (_elapsed <= 0.f) {break;}
	        }
	    }
    
	    protected Scheduler _scheduler; // weak ref
	    float _elapsed;
	    boolean _runForever;
	    boolean _useDelay;
	    int _timesExecuted;
	    int _repeat; //0 = once, 1 is 2 x executed
	    float _delay;
	    float _interval;
	}
	//class<<

	//////////////////////
	
	//static>>
    /**Priority level reserved for system services.*/
    public static final int PRIORITY_SYSTEM = Integer.MIN_VALUE;
    /** Minimum priority level for user scheduling.*/
    public static final int PRIORITY_NON_SYSTEM_MIN = PRIORITY_SYSTEM + 1;
//    public static Scheduler instance() {
//    	if(_instance == null) {
//    		_instance = new Scheduler();
//    	}
//    	return _instance;
//    }
//    private static Scheduler _instance;
    //static<<
    
    //ctor>>
    public Scheduler() {
    	_functionsToPerform.ensureCapacity(32);
    }
    
    public void release() {
    	unscheduleAll();
    	
    	_functionsToPerform.clear();
    	tempFunctions.clear();
    	
    	_hashForTimers = null;
    	_hashForUpdates = null;
    	_functionsToPerform = null;
    }
    //ctor<<
    
    
    //fields>>
//    protected struct_hashUpdateEntry _hashForUpdates; // hash used to fetch quickly the list entries for pause,delete,etc
    HashMap<Object, struct_TargetUpdaterEntry> _hashForUpdates = new HashMap<>();
    // Used for "selectors with interval"
    HashMap<Object, struct_TargetUpdaterEntry> _hashForTimers = new HashMap<>();
    protected boolean _updateHashLocked;
    protected ArrayList<Runnable> _functionsToPerform = new ArrayList<>(); // java use Runnable
    final ArrayList<Runnable> tempFunctions = new ArrayList<>();
    //fields<<
    
    
    
    //methods>>
    public final float getTimeScale() { return _baseScheduler.getTimeScale();}
    /** Modifies the time of all scheduled callbacks.
    You can use this property to create a 'slow motion' or 'fast forward' effect.
    Default is 1.0. To create a 'slow motion' effect, use values below 1.0.
    To create a 'fast forward' effect, use values higher than 1.0.
    @since v0.8
    @warning It will affect EVERY scheduled selector / action.
    */
    public void setTimeScale(float timeScale) { _baseScheduler.setTimeScale(timeScale); }
    
    /** 'update' the scheduler.
     You should NEVER call this method, unless you know what you are doing.
     * @js NA
     * @lua NA
     */
    public final void update(float dt) {
        //
        // Functions allocated from another thread
        //

        // Testing size is faster than locking / unlocking.
        // And almost never there will be functions scheduled to be called.
        synchronized(_functionsToPerform) {
        	if( !_functionsToPerform.isEmpty() ) {
//                _performMutex.lock();
                // fixed #4123: Save the callback functions, they must be invoked after '_performMutex.unlock()', otherwise if new functions are added in callback, it will cause thread deadlock.
//                _performMutex.unlock();
        		//添加到缓存队列，在run中可以再次调用添加方法
        		tempFunctions.clear();
        		for(int i = _functionsToPerform.size() - 1; i >= 0; --i) {
        			tempFunctions.add(_functionsToPerform.remove(i));
        		}
            }
        }
        
        for(int i = tempFunctions.size() - 1; i >= 0; --i) {
        	tempFunctions.remove(i).run();
        }
        tempFunctions.clear();
    }
    
    /////////////////////////////////////
    
    // schedule
    /** Calls scheduleCallback with kRepeatForever and a 0 delay
    @since v3.0
    */
   public void schedule(IUpdater callback, Object target, float interval, boolean paused, final String key) {
	   	this.schedule(callback, target, interval, CC_REPEAT_FOREVER, 0.0f, paused, key);
   }
   
    /** The scheduled method will be called every 'interval' seconds.
     If paused is true, then it won't be called until it is resumed.
     If 'interval' is 0, it will be called every frame, but if so, it's recommended to use 'scheduleUpdate' instead.
     If the 'callback' is already scheduled, then only the interval parameter will be updated without re-scheduling it again.
     repeat let the action be repeated repeat + 1 times, use kRepeatForever to let the action run continuously
     delay is the amount of time the action will wait before it'll start
     <br>使用 render线程
     */
    public void schedule(IUpdater callback, Object target, float interval,
    		int repeat, float delay, boolean paused, final String key) {
    	//默认使用render线程 防止glThread错误
    	renderSchedule(callback, target, interval, repeat, delay, paused, key);
    }

    
    /** The scheduled method will be called every 'interval' seconds.
     If paused is true, then it won't be called until it is resumed.
     If 'interval' is 0, it will be called every frame, but if so, it's recommended to use 'scheduleUpdate' instead.
     If the selector is already scheduled, then only the interval parameter will be updated without re-scheduling it again.
     repeat let the action be repeated repeat + 1 times, use kRepeatForever to let the action run continuously
     delay is the amount of time the action will wait before it'll start
     
     <br>使用 render线程
     */
    public void schedule(IUpdater selector, Object target, float interval, 
    		int repeat, float delay, boolean paused) {
    	//默认使用render线程 防止glThread错误
    	renderSchedule(selector, target, interval, repeat, delay, paused, null);
    }
    
    /** calls scheduleSelector with kRepeatForever and a 0 delay 
     * <br>使用 render线程*/
    public void schedule(IUpdater selector, Object target, 
    		float interval, boolean paused) {
    	//默认使用render线程 防止glThread错误
    	renderSchedule(selector, target, interval, CC_REPEAT_FOREVER, 0, paused, null);
    }
    
    /** Schedules the 'update' selector for a given target with a given priority.
     The 'update' selector will be called every frame.
     The lower the priority, the earlier it is called.
     <br>使用 main线程
     */
    public BaseUpdater scheduleUpdate(IUpdater scheduleFunc, int priority, boolean paused) {
    	//默认使用main线程 
    	return mainSchedulePerFrame(scheduleFunc, priority, paused);
    }
    
    public BaseUpdater scheduleUpdate(IUpdater scheduleFunc, Object target, int priority, boolean paused) {
    	//默认使用main线程
    	return mainSchedulePerFrame(scheduleFunc, target, priority, paused);
    }
    
    /////////////////////////////////////
    
    // unschedule

    /** Unschedules a callback for a key and a given target.
     If you want to unschedule the 'callbackPerFrame', use unscheduleUpdate.
     */
    public void unschedule(Object target, final String key) {
    	struct_TargetUpdaterEntry e = _hashForTimers.get(target);
    	if(e == null) {
    		CCLog.engine(TAG, "unschedule() not found updater");
    		return;
    	}
    	
    	Array<TimerUpdater> timers = e.timers;
    	if(timers == null) {
    		return;
    	}
    	
    	for(int i = timers.size - 1; i >= 0; --i) {
    		TimerUpdater curr = timers.get(i);
    		if(curr.key.equals(key)) {
    			curr.removeSelf();		//延后移除，否则timers可能改变导致出错
    		}
    	}
    }

    /** Unschedule a selector for a given target.
     If you want to unschedule the "update", use unscheudleUpdate.
     */
    public void unschedule(Object target, IUpdater selector) {
    	struct_TargetUpdaterEntry e = _hashForTimers.get(target);
    	if(e == null) {
    		CCLog.engine(TAG, "unschedule() not found updater");
    		return;
    	}
    	
    	Array<TimerUpdater> timers = e.timers;
    	if(timers == null) {
    		return;
    	}
    	
    	for(int i = timers.size - 1; i >= 0; --i) {
    		TimerUpdater curr = timers.get(i);
    		if(curr.callback == selector) {
    			curr.removeSelf();	//延后移除，否则timers可能改变导致出错
    		}
    	}
    }
    
    /** Unschedules the update selector for a given target
     @since v0.99.3
     */
    public void unscheduleUpdate(Object target) {
    	struct_TargetUpdaterEntry e = _hashForUpdates.get(target);
    	if(e == null) {
    		CCLog.engine(TAG, "unscheduleUpdate() not found updater");
    		return;
    	}
    	
    	Array<ScheduleUpdater> schedules = e.schedules;
    	if(schedules == null) {
    		return;
    	}
    	
    	for(int i = schedules.size - 1; i >= 0; --i) {
    		schedules.get(i).removeSelf();
    	}
    }
    
    
    
    /** Unschedules all selectors for a given target.
     This also includes the "update" selector.
     @since v0.99.3
     @js  unscheduleCallbackForTarget
     @lua NA
     */
    public void unscheduleAllForTarget(Object target) {
    	unscheduleUpdate(target);
    	
    	struct_TargetUpdaterEntry e = _hashForTimers.get(target);
    	if(e == null) {
    		CCLog.engine(TAG, "unscheduleAllForTarget() not found updater");
    		return;
    	}
    	
    	Array<TimerUpdater> timers = e.timers;
    	if(timers == null) {
    		return;
    	}
    	
    	for(int i = timers.size - 1; i >= 0; --i) {
    		timers.get(i).removeSelf();
    	}
    }
    
    /** Unschedules all selectors from all targets.
     You should NEVER call this method, unless you know what you are doing.
     @since v0.99.3
     */
    public void unscheduleAll() {
    	clearTargetHash(_hashForTimers);
    	clearTargetHash(_hashForUpdates);
    }
    
    /** Unschedules all selectors from all targets with a minimum priority.
     You should only call this with kPriorityNonSystemMin or higher.
     @since v2.0.0
     */
    public void unscheduleAllWithMinPriority(int minPriority) {
    	clearTargetHash(_hashForTimers, minPriority);
    	clearTargetHash(_hashForUpdates, minPriority);
    }
    
//#if CC_ENABLE_SCRIPT_BINDING
//    /** Unschedule a script entry. */
//    void unscheduleScriptEntry(unsigned int scheduleScriptEntryID);
//#endif
    
    /////////////////////////////////////
    
    // isScheduled
    
    /** Checks whether a callback associated with 'key' and 'target' is scheduled.
     @since v3.0.0
     */
    public boolean isScheduled(final String key, Object target) {
    	struct_TargetUpdaterEntry e = _hashForTimers.get(target);
    	if(e != null) {
    		Array<TimerUpdater> timers = e.timers;
        	if(timers != null) {
        		for(int i = timers.size - 1; i >= 0; --i) {
        			TimerUpdater t = timers.get(i);
        			if(key.equals(t.key)) {
        				return t.isAttached();
        			}
        		}
        	}
    	}
    	return false;
    }
    
    /** Checks whether a selector for a given taget is scheduled.
     @since v3.0
     */
    public boolean isScheduled(IUpdater selector, Object target) {
    	struct_TargetUpdaterEntry e = _hashForTimers.get(target);
    	if(e != null) {
    		Array<TimerUpdater> timers = e.timers;
        	if(timers != null) {
        		for(int i = timers.size - 1; i >= 0; --i) {
        			TimerUpdater t = timers.get(i);
        			if(selector == t.callback) {
        				return t.isAttached();
        			}
        		}
        	}
    	}
    	return false;
    }
    
    /////////////////////////////////////
    
    /** Pauses the target.
     All scheduled selectors/update for a given target won't be 'ticked' until the target is resumed.
     If the target is not present, nothing happens.
     @since v0.99.3
     */
    public void pauseTarget(Object target) {
    	struct_TargetUpdaterEntry e = _hashForUpdates.get(target);
    	if(e != null) e.setPause(true);
    	
    	e = _hashForTimers.get(target);
    	if(e != null) e.setPause(true);
    }

    /** Resumes the target.
     The 'target' will be unpaused, so all schedule selectors/update will be 'ticked' again.
     If the target is not present, nothing happens.
     @since v0.99.3
     */
    public void resumeTarget(Object target) {
    	struct_TargetUpdaterEntry e = _hashForUpdates.get(target);
    	if(e != null) e.setPause(false);
    	
    	e = _hashForTimers.get(target);
    	if(e != null) e.setPause(false);
    }

    /** Returns whether or not the target is paused
    @since v1.0.0
    * In js: var isTargetPaused(var jsObject)
    * @lua NA 
    */
    public boolean isTargetPaused(Object target) {
    	struct_TargetUpdaterEntry e = _hashForUpdates.get(target);
    	if(e != null) {
    		if(e.isPause()) {
    			return true;
    		}
    	}
    	
    	e = _hashForTimers.get(target);
    	if(e != null) {
    		if(e.isPause()) {
    			return true;
    		}
    	}
    	
    	return false;
    	
    }

    /** Pause all selectors from all targets.
      You should NEVER call this method, unless you know what you are doing.
     @since v2.0.0
      */
//    public HashSet<?> pauseAllTargets() {
//    	
//    }

    /** Pause all selectors from all targets with a minimum priority.
      You should only call this with kPriorityNonSystemMin or higher.
      @since v2.0.0
      */
//    public HashSet<?> pauseAllTargetsWithMinPriority(int minPriority) {
//    	
//    }

    /** Resume selectors on a set of targets.
     This can be useful for undoing a call to pauseAllSelectors.
     @since v2.0.0
      */
//    public void resumeTargets(HashSet<?> targetsToResume) {
//    	
//    }

	/** calls a function on the cocos2d thread. Useful when you need to call a cocos2d function from another thread.
	 This function is thread safe.
	 多线程可用，会在gl线程中调用
	 @since v3.0
	 */
    public void performFunctionInCocosThread(Runnable function) {
    	synchronized (_functionsToPerform) {
    		_functionsToPerform.add(function);
		}
//    	_functionsToPerform.add(()->{
//    		return null;	
//    	});
    }
    //methods<<
    ///////////////////////////////
    
    //func>> 
    /**清空大于指定priority的map数据*/
    final void clearTargetHash(final HashMap<Object, struct_TargetUpdaterEntry> map, int priority) {
    	Iterator<Entry<Object, struct_TargetUpdaterEntry>> it = map.entrySet().iterator();
    	while(it.hasNext()) {
    		struct_TargetUpdaterEntry e = it.next().getValue();
    		Array<TimerUpdater> timers = e.timers;
    		if(timers != null) {
    			for(BaseUpdater u : timers) {
    				if(u.getPriority() >= priority) {
    					u.removeSelf();
    				}
    			}
    		}
    		Array<ScheduleUpdater> schedules = e.schedules;
    		if(schedules != null) {
    			for(BaseUpdater u : schedules) {
    				if(u.getPriority() >= priority) {
    					u.removeSelf();
    				}
    			}
    		}
    		
    		if(e.isEmpty()) {
    			CCLog.engine(TAG, "clearTargetHash() release e");
    			e.release();
    			it.remove();
    		}
    	}
    }
  
    /**清空map数据*/
    final void clearTargetHash(final HashMap<Object, struct_TargetUpdaterEntry> map) {
    	clearTargetHash(map, PRIORITY_SYSTEM);
    }
    
    /**将timer对象从关联target中删除
     * */
    final void removeHashForTimers(TimerUpdater element) {
    	Object target = element.target;
    	if(target == null) {
    		CCLog.engine(TAG, "element target is null.");
    		return;
    	}
    	
    	struct_TargetUpdaterEntry e = _hashForTimers.get(target);
    	if(e == null) {
    		CCLog.engine(TAG, "element not found.");
    		return;
    	}
    	
    	if(e.removeTimer(element) == null) {
    		CCLog.engine(TAG, "element not found.");
    	}
    	
    	if(e.isEmpty()) {
			CCLog.engine(TAG, "removeHashForTimers() release e");
    		_hashForTimers.remove(target);
    	}
    }
    
    /**将schedule对象从关联target中删除 */
    final void removeHashForUpdates(ScheduleUpdater element) {
    	Object target = element.target;
    	if(target == null) {
    		CCLog.engine(TAG, "element target is null.");
    		return;
    	}
    	
    	struct_TargetUpdaterEntry e = _hashForUpdates.get(target);
    	if(e == null) {
    		CCLog.engine(TAG, "element not found.");
    		return;
    	}
    	
    	if(e.removeSchedule(element) == null) {
    		CCLog.engine(TAG, "element not found.");
    	}
    	
    	if(e.isEmpty()) {
    		CCLog.engine(TAG, "removeHashForUpdates() release e");
    		_hashForUpdates.remove(target);
    	}
    }

   /**为target关联Timer对象 */
    final void putHashForTimers(Object target, TimerUpdater updater) {
    	assert target != null: "target cannot be null.";
    	
    	struct_TargetUpdaterEntry e = _hashForTimers.get(target);
    	if(e == null) {
    		e = new struct_TargetUpdaterEntry();
    		e.target = target;
    		_hashForTimers.put(target, e);
    	}
    	
    	e.addTimer(updater);
    }
    
    /**为target关联scheduleUpdater对象 */
    final void putHashForUpdates(Object target, ScheduleUpdater updater) {
    	assert target != null: "target cannot be null.";
    	
    	struct_TargetUpdaterEntry e = _hashForUpdates.get(target);
    	if(e == null) {
    		e = new struct_TargetUpdaterEntry();
    		e.target = target;
    		_hashForUpdates.put(target, e);
    	}
    	
    	e.addSchedule(updater);
    }
    //func<<
    
    
    //////////////////////
    // cocos2d-java extension ---------------------------------------
    final BaseScheduler _baseScheduler = BaseScheduler.instance();
    
//    /**
//     * schedule in main thread before call Scene update
//     * @see #schedule(IUpdater, Object, BaseUpdateType, float, int, float, boolean, String)
//     */
//    public final BaseUpdater firstSchedule(IUpdater callback, float interval, int repeat, float delay, boolean paused) {
//    	return schedule(callback, null, BaseUpdateType.First, interval, repeat, delay, paused, null);
//    }
//    
//    /**
//     * schedule in main thread before call Scene update
//     * @see #schedule(IUpdater, Object, BaseUpdateType, float, int, float, boolean, String)
//     */
//    public final BaseUpdater firstSchedule(IUpdater callback, Object target,
//    		float interval, int repeat, float delay, boolean paused, final String key) {
//    	return schedule(callback, target, BaseUpdateType.First, interval, repeat, delay, paused, key);
//    }
    
    /**
     * schedule in main thread
     * @see #schedule(IUpdater, Object, BaseUpdateType, float, int, float, boolean, String)
     */
    public final BaseUpdater mainSchedule(IUpdater callback, float interval, int repeat, float delay, boolean paused) {
    	return schedule(callback, null, BaseUpdateType.Main, interval, repeat, delay, paused, null);
    }
    
    /**
     * schedule in main thread
     * @see #schedule(IUpdater, Object, BaseUpdateType, float, int, float, boolean, String)
     */
    public final BaseUpdater mainSchedule(IUpdater callback, Object target,
    		float interval, int repeat, float delay, boolean paused, final String key) {
    	return schedule(callback, target, BaseUpdateType.Main, interval, repeat, delay, paused, key);
    }
    
    /**
     * schedule in render thread before drawScene
     * @see #schedule(IUpdater, Object, BaseUpdateType, float, int, float, boolean, String)
     */
    public final BaseUpdater renderSchedule(IUpdater callback, float interval, int repeat, float delay, boolean paused) {
    	return schedule(callback, null, BaseUpdateType.RenderBefore, interval, repeat, delay, paused, null);
    }
    
    /**
     * schedule in render thread before drawScene
     * @see #schedule(IUpdater, Object, BaseUpdateType, float, int, float, boolean, String)
     */
    public final BaseUpdater renderSchedule(IUpdater callback, Object target,
    		float interval, int repeat, float delay, boolean paused, final String key) {
    	return schedule(callback, target, BaseUpdateType.RenderBefore, interval, repeat, delay, paused, key);
    }
    
    /**
     * schedule in render thread after drawScene
     * @see #schedule(IUpdater, Object, BaseUpdateType, float, int, float, boolean, String)
     */
    public final BaseUpdater renderAfterSchedule(IUpdater callback, float interval, int repeat, float delay, boolean paused) {
    	return schedule(callback, null, BaseUpdateType.RenderAfter, interval, repeat, delay, paused, null);
    }
    
    /**
     * schedule in render thread after drawScene
     * @see #schedule(IUpdater, Object, BaseUpdateType, float, int, float, boolean, String)
     */
    public final BaseUpdater renderAfterSchedule(IUpdater callback, Object target,
    		float interval, int repeat, float delay, boolean paused, final String key) {
    	return schedule(callback, target, BaseUpdateType.RenderAfter, interval, repeat, delay, paused, key);
    }
    
//    /**
//     * schedule in main thread before call Scene update
//     * @see #schedulePerFrame(IUpdater, Object, BaseUpdateType, int, boolean)
//     */
//    public BaseUpdater firstSchedulePerFrame(IUpdater scheduleFunc, int priority, boolean paused) {
//    	return this.schedulePerFrame(scheduleFunc, null, BaseUpdateType.First, priority, paused);
//    }
//    
//    /**
//     * schedule in main thread before call Scene update
//     * @see #schedulePerFrame(IUpdater, Object, BaseUpdateType, int, boolean)
//     */
//    public final BaseUpdater firstSchedulePerFrame(IUpdater scheduleFunc, Object target, int priority, boolean paused) {
//    	return this.schedulePerFrame(scheduleFunc, target, BaseUpdateType.First, priority, paused);
//    }
    
    /**
     * schedule in main thread
     * @see #schedulePerFrame(IUpdater, Object, BaseUpdateType, int, boolean)
     */
    public final BaseUpdater mainSchedulePerFrame(IUpdater scheduleFunc, int priority, boolean paused) {
    	return this.schedulePerFrame(scheduleFunc, null, BaseUpdateType.Main, priority, paused);
    }
    
    /**
     * schedule in main thread
     * @see #schedulePerFrame(IUpdater, Object, BaseUpdateType, int, boolean)
     */
    public final BaseUpdater mainSchedulePerFrame(IUpdater scheduleFunc, Object target, int priority, boolean paused) {
    	return this.schedulePerFrame(scheduleFunc, target, BaseUpdateType.Main, priority, paused);
    }
    
    /**
     * schedule in render thread
     * @see #schedulePerFrame(IUpdater, Object, BaseUpdateType, int, boolean)
     */
    public final BaseUpdater renderSchedulePerFrame(IUpdater scheduleFunc, int priority, boolean paused) {
    	return this.schedulePerFrame(scheduleFunc, null, BaseUpdateType.RenderBefore, priority, paused);
    }
    
    /**
     * schedule in render thread
     * @see #schedulePerFrame(IUpdater, Object, BaseUpdateType, int, boolean)
     */
    public final BaseUpdater renderSchedulePerFrame(IUpdater scheduleFunc, Object target, int priority, boolean paused) {
    	return this.schedulePerFrame(scheduleFunc, target, BaseUpdateType.RenderBefore, priority, paused);
    }
    
    /**
     * schedule in render thread
     * @see #schedulePerFrame(IUpdater, Object, BaseUpdateType, int, boolean)
     */
    public final BaseUpdater renderAfterSchedulePerFrame(IUpdater scheduleFunc, int priority, boolean paused) {
    	return this.schedulePerFrame(scheduleFunc, null, BaseUpdateType.RenderAfter, priority, paused);
    }
    
    /**
     * schedule in render thread
     * @see #schedulePerFrame(IUpdater, Object, BaseUpdateType, int, boolean)
     */
    public final BaseUpdater renderAfterSchedulePerFrame(IUpdater scheduleFunc, Object target, int priority, boolean paused) {
    	return this.schedulePerFrame(scheduleFunc, target, BaseUpdateType.RenderAfter, priority, paused);
    }
    
    
    /**
     * 添加定时执行函数<p>
     * 
     * @param scheduleFunc 执行函数
     * @param target 执行目标（做为键值），可以是null
     * @param type 更新类型：First／Main／Render（gl相关操作只能在render中进行）
     * @param interval 循环间隔
     * @param repeat 循环次数 {@link #CC_REPEAT_FOREVER}
     * @param delay 延迟执行时长 second
     * @param paused 是否暂停
     * @param key 定时器键值，移除时使用
     */
    public final BaseUpdater schedule(IUpdater callback, Object target, BaseUpdateType type,
    		float interval, int repeat, float delay, boolean paused, final String key) {
    	
    	TimerUpdater updater = new TimerUpdater(this, callback, target, key);
    	updater.initWithSelector(interval, repeat, delay);
    	if(paused) {
    		updater.pause();
    	} else {
    		updater.clearPaused();
    	}
    	updater.setUpdateType(type);
    	
    	if(target != null) {	//注册target与updater的关系
    		putHashForTimers(target, updater);
    	}
    	
    	_baseScheduler.add(updater, false);
    	return updater;
    }
    
    /**
     * 添加每帧都会执行的schedule函数<p>
     * 
     * @param scheduleFunc 执行函数
     * @param target 执行目标（做为键值），可以是null
     * @param type 更新类型：First／Main／Render（gl相关操作只能在render中进行）
     * @param priority 优先级 越低越先执行
     * @param paused 是否暂停
     * 
     * @return updater 对象
     */
    public final BaseUpdater schedulePerFrame(IUpdater scheduleFunc, Object target, BaseUpdateType type, int priority, boolean paused) {
    	ScheduleUpdater updater = new ScheduleUpdater(this, scheduleFunc, target);
    	updater.setPriority(priority);
    	if(paused) {
    		updater.pause();
    	} else {
    		updater.clearPaused();
    	}
    	updater.setUpdateType(type);
    	
    	if(target != null) {		//注册target与updater的关系
    		putHashForUpdates(target, updater);
    	}
    	
    	_baseScheduler.add(updater, false);
    	return updater;
    }
}
