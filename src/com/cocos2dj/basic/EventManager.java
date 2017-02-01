package com.cocos2dj.basic;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.badlogic.gdx.utils.Array;

/**
 * 事件管理器
 * 
 * @author xujun
 *		
 */
public class EventManager {
	
	static final String TAG = "EventManager";
	
	/***/
	public static interface OnEventCallback {
		public void onEvent(String event, Object user, Object...args);
	}
	
	public static class StructEvent {
		Object user;
		OnEventCallback callback;
		StructEvent(Object user, OnEventCallback callback) {
			this.user = user;
			this.callback = callback;
		}
		final void call(String event, Object...args) {
			callback.onEvent(event, user, args);
		}
	}
	
	private EventManager() {
		
	}
	private static EventManager _instance;
	public static EventManager instance() {
		if(_instance == null) {
			_instance = new EventManager();
		}
		return _instance;
	}
	
	
	//fields>>
	private final HashMap<String, Array<StructEvent>> eventMap = new HashMap<>();
	//fields<<
	
	
	//methods>>
	/**注册使用者为null的事件 */
	public boolean register(String event, OnEventCallback callback) {
		return this.register(event, null, callback);
	}
	
	/*指定使用者user之注册事件 */
	public boolean register(String event, Object user, OnEventCallback callback) {
		Array<StructEvent> events = eventMap.get(event);
		if(events == null) {
			events = new Array<StructEvent>();
			eventMap.put(event, events);
		}
		
		StructEvent newEvent;
		if(user == null) {
			newEvent = new StructEvent(user, callback);
		} else {
			for(StructEvent e : events) {
				if(user == e.user) {
					BaseLog.warning(TAG, "event already in map : " + event + " " + user);
					return false;
				}
			}
			newEvent = new StructEvent(user, callback);
		}
		events.add(newEvent);
		return true;
	}
	
	/**删除使用者为null的事件*/
	public boolean unregister(String event) {
		return unregister(event, null);
	}
	
	/**指定使用者删除事件*/
	public boolean unregister(String event, Object user) {
		Array<StructEvent> events = eventMap.get(event);
		if(events == null) {
//			SLog.warning(TAG, "event not found : " + event);
			return false;
		}
		
		if(user == null) {
			boolean ret = false;
			for(int i = events.size - 1; i >= 0; --i) {
				StructEvent e = events.get(i);
				if(e.user == null) {
					ret = true;
					events.removeIndex(i);
				}
			}
			if(ret) {
				if(events.size <= 0) {
					BaseLog.debug(TAG, "events empty removeIt" + event);
					eventMap.remove(event);
				}
				return true;
			} else {
				BaseLog.warning(TAG, "null object not found : " + event);
				return false;
			}
		}
		for(int i = events.size - 1; i >= 0; --i) {
			StructEvent e = events.get(i);
			if(e.user == user) {
				events.removeIndex(i);
				if(events.size <= 0) {
					BaseLog.debug(TAG, "events empty removeIt" + event);
					eventMap.remove(event);
				}
				return true;
			}
		}
		BaseLog.warning(TAG, "object not found : " + event + " " + user);
		return false;
	}
	
	/**删除指定名称下所有的事件 */
	public boolean unregisterAll(String event) {
		Array<StructEvent> events = eventMap.get(event);
		if(events == null) {
			BaseLog.warning(TAG, "event not found : " + event);
			return false;
		}
		events.clear();
		eventMap.remove(event);
		return true;
	}
	
	/**触发事件*/
	public boolean trigger(String event, Object...args) {
		Array<StructEvent> events = eventMap.get(event);
		if(events == null) {
//			SLog.warning(TAG, "event not found : " + event);
			return false;
		}
		final int len = events.size;
		//使用副本来执行call —— 支持在执行过程中修改源数据
		StructEvent[] structEvents = new StructEvent[len];
		for(int i = 0; i < len; ++i) {
			structEvents[i] = events.get(i);
		}
		for(int i = 0; i < len; ++i) {
			structEvents[i].call(event, args);
		}
		structEvents = null;
		return true;
	}
	
	/**清空事件 */
	public void clear() {
		Iterator<Entry<String, Array<StructEvent>>> it = eventMap.entrySet().iterator();
		while(it.hasNext()) {
			it.next().getValue().clear();
		}
		eventMap.clear();
	}
	
	/**获取事件管理器中的事件状态 格式类似：
	 * <pre>
	 * events:
		 * ["msg_event_1"]:6
		 * ["msg_event_2"]:5
	 * </pre>
	 * @return
	 */
	public String getEventStatus() {
		StringBuilder sb = new StringBuilder();
		/*
		 * events:
		 * ["msg_event"]:6
		 * ["msg_gonggao"]:5
		 */
		sb.append("events\n");
		Iterator<Entry<String, Array<StructEvent>>> it = eventMap.entrySet().iterator();
		while(it.hasNext()) {
			Entry<String, Array<StructEvent>> e = it.next();
			sb.append('[').append(e.getKey()).append("] : ").append(e.getValue().size).append('\n');
		}
		return sb.toString();
	}
	//methods<<
}
