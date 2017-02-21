package com.cocos2dj.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

import com.badlogic.gdx.utils.Array;
import com.cocos2dj.macros.CCLog;
import com.cocos2dj.protocol.ICamera;
import com.cocos2dj.protocol.IFunctionOneArg;
import com.cocos2dj.protocol.IFunctionOneArgRet;
import com.cocos2dj.protocol.INode;
import com.cocos2dj.protocol.IScene;

/**
 * EventDispatcher.java
 * <br>DirtyFlag
 * <p>
 * 
 * @author Copyright (c) 2017 xu jun
 */
public class EventDispatcher {
	
 // Adds event listener
    
    /** Adds a event listener for a specified event with the priority of scene graph.
     *  @param listener The listener of a specified event.
     *  @param node The priority of the listener is based on the draw order of this node.
     *  @note  The priority of scene graph will be fixed value 0. So the order of listener item
     *          in the vector will be ' <0, scene graph (0 priority), >0'.
     */
    public void addEventListenerWithSceneGraphPriority(EventListener listener, INode node) {
    	assert listener!= null && node != null: "Invalid parameters.";
        assert !listener.isRegistered(): "The listener has been registered.";
        
        if (!listener.checkAvailable()) {
        	CCLog.engine("EventDisptcher", "The listener not Available");
            return;
        }
        
        listener.setAssociatedNode(node);
        listener.setFixedPriority(0);
        listener.setRegistered(true);
        
        addEventListener(listener);
    }

    /** Adds a event listener for a specified event with the fixed priority.
     *  @param listener The listener of a specified event.
     *  @param fixedPriority The fixed priority of the listener.
     *  @note A lower priority will be called before the ones that have a higher value.
     *        0 priority is forbidden for fixed priority since it's used for scene graph based priority.
     */
    public void addEventListenerWithFixedPriority(EventListener listener, int fixedPriority) {
    	assert listener != null: "Invalid parameters.";
        assert !listener.isRegistered(): "The listener has been registered.";
        assert fixedPriority != 0: "0 priority is forbidden for fixed priority since it's used for scene graph based priority.";
        
        if (!listener.checkAvailable()) {
        	CCLog.engine("EventDisptcher", "The listener not Available");
            return;
        }
        
        listener.setAssociatedNode(null);
        listener.setFixedPriority(fixedPriority);
        listener.setRegistered(true);
        listener.setPaused(false);

        addEventListener(listener);
    }

    /** Adds a Custom event listener.
     It will use a fixed priority of 1.
     @return the generated event. Needed in order to remove the event from the dispather
     */
    public EventListenerCustom addCustomEventListener(String eventName, IFunctionOneArg<EventCustom> callback) {
    	EventListenerCustom listener = EventListenerCustom.create(eventName, callback);
    	addEventListenerWithFixedPriority(listener, 1);
    	return listener;
    }

    /////////////////////////////////////////////
    
    // Removes event listener
    //return isFound
    private boolean removeListenerInVector(Array<EventListener> listeners, EventListener listener) {
    	if(listeners == null) {return false;}
    	
    	for(int i = listeners.size - 1; i >= 0; --i) {
    		EventListener l = listeners.get(i);
    		if(l == listener) {
    			l.setRegistered(false);
    			if(l.getAssociatedNode() != null) {
    				dissociateNodeAndEventListener(l.getAssociatedNode(), l);
    				l.setAssociatedNode(null);
    			}
    			if(_inDispatch <= 0) {
    				listeners.removeIndex(i);
    			} else {
    				_toRemovedListeners.add(l);
    			}
    			return true;
    		}
    	}
    	return false;
    }
    
    /** Remove a listener
     *  @param listener The specified event listener which needs to be removed.
     */
    public void removeEventListener(EventListener listener) {
    	if(listener == null) {return;}
    	
    	if(_toRemovedListeners.contains(listener)) {return;}
    	
    	boolean isFound = false;
    	
    	Iterator<Entry<String, EventListenerVector>> it = _listenerMap.entrySet().iterator();
    	while(it.hasNext()) {
    		EventListenerVector listeners = it.next().getValue();
    		Array<EventListener> fixedPriorityListeners = listeners.getFixedPriorityListeners();
    		Array<EventListener> sceneGraphPriorityListeners = listeners.getSceneGraphPriorityListeners();
    		
    		if(removeListenerInVector(sceneGraphPriorityListeners, listener)) {
    			isFound = true;
    		}
    		if(isFound) {
                // fixed #4160: Dirty flag need to be updated after listeners were removed.
    			setDirty(listener.getListenerID(), DirtyFlag.SCENE_GRAPH_PRIORITY);
    		} else {
    			if(removeListenerInVector(fixedPriorityListeners, listener)) {
    				isFound = true;
    			}
    			if(isFound) {
    				setDirty(listener.getListenerID(), DirtyFlag.FIXED_PRIORITY);
    			}
    		}
//    #if CC_NODE_DEBUG_VERIFY_EVENT_LISTENERS
//            CCASSERT(_inDispatch != 0 ||
//                     !sceneGraphPriorityListeners ||
//                     std::count(sceneGraphPriorityListeners->begin(), sceneGraphPriorityListeners->end(), listener) == 0,
//                     "Listener should be in no lists after this is done if we're not currently in dispatch mode.");
//                
//            CCASSERT(_inDispatch != 0 ||
//                     !fixedPriorityListeners ||
//                     std::count(fixedPriorityListeners->begin(), fixedPriorityListeners->end(), listener) == 0,
//                     "Listener should be in no lists after this is done if we're not currently in dispatch mode.");
//    #endif
            if (listeners.empty()) {
                _priorityDirtyFlagMap.remove(listener.getListenerID());
                it.remove();
            }
            
            if (isFound) {break;}
        }

        if (isFound) {
//            releaseListener(listener);
        } else {
        	if(_toAddedListeners.remove(listener)) {
	        	listener.setRegistered(false);
        	}
        }
    }

    /** Removes all listeners with the same event listener type */
    public void removeEventListenersForType(EventListener.Type listenerType) {
    	switch(listenerType) {
    	case TOUCH_ONE_BY_ONE:
    		removeEventListenersForListenerID(EventListenerTouchOneByOne.LISTENER_ID);
    		break;
    	case TOUCH_ALL_AT_ONCE:
    		removeEventListenersForListenerID(EventListenerTouchAllAtOnce.LISTENER_ID);
    		break;
    	case MOUSE:
//    		removeEventListenersForListenerID(EventListener);
    		break;
    	case ACCELERATION:
//    		removeEventListenersForListenerID(listenerID);//    		
    		break;
    	case KEYBOARD:
    		removeEventListenersForListenerID(EventListenerKeyboard.LISTENER_ID);
    		break;
		default:
			assert false: "Invalid listener type!";	
    	}
    }
    
    /** Removes all listeners which are associated with the specified target. */
    public void removeEventListenersForTarget(INode target) {
    	removeEventListenersForTarget(target, false);
    }
    
    /** Removes all listeners which are associated with the specified target. */
    public void removeEventListenersForTarget(INode target, boolean recursive) {
    	_nodePriorityMap.remove(target);
    	_dirtyNodes.remove(target);
    	
    	ArrayList<EventListener> listeners = _nodeListenersMap.get(target);
    	if(listeners != null) {
//    		for(EventListen)
    		//TODO need copy !
    		_poolArray.clear();
    		_poolArray.addAll(listeners);		//copy to stack
    		for(int i = 0; i < _poolArray.size(); ++i) {
    			EventListener l = (EventListener) _poolArray.get(i);
    			removeEventListener(l);
    		}
    	}
    	
    	// Bug fix: ensure there are no references to the node in the list of listeners to be added.
        // If we find any listeners associated with the destroyed node in this list then remove them.
        // This is to catch the scenario where the node gets destroyed before it's listener
        // is added into the event dispatcher fully. This could happen if a node registers a listener
        // and gets destroyed while we are dispatching an event (touch etc.)
    	for(int i = _toAddedListeners.size() - 1; i >= 0; --i) {
    		EventListener listener = _toAddedListeners.get(i);
    		if(listener.getAssociatedNode() == target) {
    			listener.setAssociatedNode(null);
    			listener.setRegistered(false);
    			_toAddedListeners.remove(i);
    		}
    	}
    	
    	if(recursive) {
    		final int len = target.getChildrenCount();
    		for(int i = 0; i < len; ++i) {
    			removeEventListenersForTarget(target.getChild(i), true);
    		}
    	}
    }
    
    /** Removes all custom listeners with the same event name */
    public void removeCustomEventListeners(String customEventName) {
    	removeEventListenersForListenerID(customEventName);
    }

    /** Removes all listeners */
    public void removeAllEventListeners() {
    	boolean cleanMap =  true;
    	//TODO stack
    	_poolArray.clear();
    	ArrayList<Object> types = _poolArray;
    	
    	Iterator<Entry<String, EventListenerVector>> it = _listenerMap.entrySet().iterator();
    	while(it.hasNext()) {
    		String key = it.next().getKey();
    		if(_internalCustomListenerIDs.contains(key)) {
    			cleanMap = false;
    		} else {
    			types.add(key);
    		}
    	}
    	
    	for(Object o : types) {
    		String type = (String) o;
    		removeEventListenersForListenerID(type);
    	}
    	
    	if(_inDispatch <= 0 && cleanMap) {
    		_listenerMap.clear();
    	}
    }

    /////////////////////////////////////////////
    
    // Pauses / Resumes event listener
    
    /** Pauses all listeners which are associated the specified target. */
    public void pauseEventListenersForTarget(INode target) {
    	pauseEventListenersForTarget(target, false);
    }
    
    /** Resumes all listeners which are associated the specified target. */
    public void resumeEventListenersForTarget(INode target) {
    	resumeEventListenersForTarget(target, false);
    }
    
    /** Pauses all listeners which are associated the specified target. */
    public void pauseEventListenersForTarget(INode target, boolean recursive) {
    	ArrayList<EventListener> listenerIter = _nodeListenersMap.get(target);
        if (listenerIter != null) {
            for(EventListener l : listenerIter) {
            	l.setPaused(true);
            }
        }

        for (EventListener listener : _toAddedListeners) {
            if (listener.getAssociatedNode() == target) {
                listener.setPaused(true);
            }
        }
        
        if (recursive) {
        	final int len = target.getChildrenCount();
        	for(int i = 0; i < len; ++i) {
        		INode child = target.getChild(i);
        		pauseEventListenersForTarget(child, true);
        	}
        }
    }
    
    /** Resumes all listeners which are associated the specified target. */
    public void resumeEventListenersForTarget(INode target, boolean recursive) {
    	ArrayList<EventListener> listenerIter = _nodeListenersMap.get(target);
        if (listenerIter != null) {
            for(EventListener l : listenerIter) {
            	l.setPaused(false);
            }
        }

        for (EventListener listener : _toAddedListeners) {
            if (listener.getAssociatedNode() == target) {
                listener.setPaused(false);
            }
        }
        
        if (recursive) {
        	final int len = target.getChildrenCount();
        	for(int i = 0; i < len; ++i) {
        		INode child = target.getChild(i);
        		resumeEventListenersForTarget(child, true);
        	}
        }
    }
    
    /////////////////////////////////////////////
    
    /** Sets listener's priority with fixed value. */
    public void setPriority(EventListener listener, int fixedPriority) {
    	if (listener == null) {
            return;
    	}
        
    	Iterator<Entry<String, EventListenerVector>> it = _listenerMap.entrySet().iterator();
    	while(it.hasNext()) {
    		EventListenerVector vector = it.next().getValue();
    		Array<EventListener> fixedPriorityListeners = vector.getFixedPriorityListeners();
    		if(fixedPriorityListeners != null) {
    			if(fixedPriorityListeners.contains(listener, true)) {
    				assert listener.getAssociatedNode() == null: "Can't set fixed priority with scene graph based listener.";
                    if (listener.getFixedPriority() != fixedPriority) {
                        listener.setFixedPriority(fixedPriority);
                        setDirty(listener.getListenerID(), DirtyFlag.FIXED_PRIORITY);
                    }
                    return;
    			}
    		}
    	}
    }

    /** Whether to enable dispatching events */
    public void setEnabled(boolean isEnabled) {
    	this._isEnabled = isEnabled;
    }

    /** Checks whether dispatching events is enabled */
    public boolean isEnabled() {
    	return _isEnabled;
    }

    /////////////////////////////////////////////
    class OnEventCallback implements IFunctionOneArgRet<EventListener, Boolean> {
    	Event event;
    	public void init(Event event) {
    		this.event = event;
    	}
    	
		@Override
		public Boolean callback(EventListener listener) {
			event.setCurrentTarget(listener.getAssociatedNode());
			listener._onEvent.callback(event);
			return event._isStopped;
		}
    }
    private OnEventCallback onEventCallback = new OnEventCallback();
    
    //TODO Dispatches
    /** Dispatches the event
     *  Also removes all EventListeners marked for deletion from the
     *  event dispatcher list.
     */
    public void dispatchEvent(final Event event) {
    	if(!_isEnabled) {
    		return;
    	}
    	
    	updateDirtyFlagForSceneGraph();
    	
    	_inDispatch += 1;
    	
    	if(event.getType() == Event.Type.TOUCH) {
    		dispatchTouchEvent((EventTouch) event);
    		_inDispatch -= 1;
    		return;
    	}
    	
    	String listenerID = __getListenerID(event);
    	
    	sortEventListeners(listenerID);
    	
    	// inner class
		onEventCallback.init(event);
		
    	if(event.getType() == Event.Type.MOUSE) {
    		EventListenerVector ev = _listenerMap.get(listenerID);
    		if(ev != null) {
    			dispatchTouchEventToListeners(ev, onEventCallback);
    		}
    	} else {
    		EventListenerVector ev = _listenerMap.get(listenerID);
    		if(ev != null) {
    			dispatchEventToListeners(ev, onEventCallback);
    		}
    	}
    	
    	updateListeners(event);
    	_inDispatch -= 1;
    }
    
    /** Dispatches a Custom Event with a event name an optional user data */
    public void dispatchCustomEvent(String eventName) {
    	dispatchCustomEvent(eventName, null);
    }
    
    /** Dispatches a Custom Event with a event name an optional user data */
    public void dispatchCustomEvent(String eventName, Object optionalUserData) {
    	EventCustom ev = new EventCustom(eventName);
    	ev.setUserData(optionalUserData);
    	dispatchEvent(ev);
    	ev = null;
    }

    /////////////////////////////////////////////
    
    /** Constructor of EventDispatcher */
    public EventDispatcher() {
    	//TODO unfinshed
    }
//    
    /** Destructor of EventDispatcher */
//    ~EventDispatcher();
//#if CC_NODE_DEBUG_VERIFY_EVENT_LISTENERS && COCOS2D_DEBUG > 0
//    
//    /**
//     * To help track down event listener issues in debug builds.
//     * Verifies that the node has no event listeners associated with it when destroyed.
//     */
//    void debugCheckNodeHasNoEventListenersOnDestruction(Node* node);
//    
//#endif

//protected:
//    friend class Node;
    
    /** Sets the dirty flag for a node. */
    public void setDirtyForNode(INode node) {
    	// Mark the node dirty only when there is an eventlistener associated with it. 
    	if(_nodeListenersMap.containsKey(node)) {
    		_dirtyNodes.add(node);
    	}
    	
        // Also set the dirty flag for node's children
    	int len = node.getChildrenCount();
    	for(int i = 0; i < len; ++i) {
    		INode child = node.getChild(i);
    		setDirtyForNode(child);
    	}
    }
    
    /**
     *  The vector to store event listeners with scene graph based priority and fixed priority.
     */
    static class EventListenerVector {
	    public EventListenerVector() {}
	    
	    public int size() {
	    	int ret = 0;
	    	if(_sceneGraphListeners != null) {
	    		ret += _sceneGraphListeners.size;
	    	}
	    	if(_fixedListeners != null) {
	    		ret += _fixedListeners.size;
	    	}
	    	return ret;
	    }
	    
	    public boolean empty() {
	    	return (_sceneGraphListeners == null || _sceneGraphListeners.size <= 0)
	    			&& (_fixedListeners == null || _fixedListeners.size <= 0);
	    }
	    
        public void push_back(EventListener listener) {
        	if(listener.getFixedPriority() == 0) {
        		if(_sceneGraphListeners == null) {
        			_sceneGraphListeners = new Array<>(64);
        		}
        		_sceneGraphListeners.add(listener);
        	} else {
        		if(_fixedListeners == null) {
        			_fixedListeners = new Array<>(64);
        		}
        		_fixedListeners.add(listener);
        	}
        }
        public void clearSceneGraphListeners() {
        	if(_sceneGraphListeners != null) {
        		_sceneGraphListeners.clear();
        		_sceneGraphListeners = null;
        	}
        }
        public void clearFixedListeners() {
        	if(_fixedListeners != null) {
        		_fixedListeners.clear();
        		_fixedListeners = null;
        	}
        }
        public void clear() {
        	clearSceneGraphListeners();
        	clearFixedListeners();
        }
        
        public final Array<EventListener> getFixedPriorityListeners() {return _fixedListeners;}
        public final Array<EventListener> getSceneGraphPriorityListeners() {return _sceneGraphListeners;}
        public final int getGt0Index() { return _gt0Index; }
        public final void setGt0Index(int index) { _gt0Index = index; }
        private Array<EventListener> _fixedListeners;
    	private Array<EventListener> _sceneGraphListeners;
        private int _gt0Index;
    };
    
    /** Adds an event listener with item
     *  @note if it is dispatching event, the added operation will be delayed to the end of current dispatch
     *  @see forceAddEventListener
     */
    final void addEventListener(EventListener listener) {
    	if (_inDispatch == 0) {
            forceAddEventListener(listener);
        } else {
            _toAddedListeners.add(listener);
        }
    }
    
    /** Force adding an event listener
     *  @note force add an event listener which will ignore whether it's in dispatching.
     *  @see addEventListener
     */
    public void forceAddEventListener(EventListener listener) {
    	EventListenerVector listeners = null;
        String listenerID = listener.getListenerID();
        
        listeners =  _listenerMap.get(listenerID);
        if(listeners == null) {
        	listeners = new EventListenerVector();
        	_listenerMap.put(listenerID, listeners);
        }
        listeners.push_back(listener);
        
        if (listener.getFixedPriority() == 0) {
            setDirty(listenerID, DirtyFlag.SCENE_GRAPH_PRIORITY);
            
            INode node = listener.getAssociatedNode();
            assert node != null: "Invalid scene graph priority!";
            
            associateNodeAndEventListener(node, listener);
            
            if (node.isRunning()) {
                resumeEventListenersForTarget(node);
            }
        } else {
            setDirty(listenerID, DirtyFlag.FIXED_PRIORITY);
        }
    }
    
    /** Gets event the listener list for the event listener type. */
    EventListenerVector getListeners(final String listenerID) {
    	return _listenerMap.get(listenerID);
    }
    
    /** Update dirty flag */
    public void updateDirtyFlagForSceneGraph() {
    	if (!_dirtyNodes.isEmpty()) {
            for (INode node : _dirtyNodes) {
            	
            	ArrayList<EventListener> listeners = _nodeListenersMap.get(node);
            	if(listeners != null) {
            		for(EventListener l : listeners) {
            			setDirty(l.getListenerID(), DirtyFlag.SCENE_GRAPH_PRIORITY);
            		}
            	}
            }
            _dirtyNodes.clear();
        }
    }
    
    private void removeAllListenersInVector(Array<EventListener> listenerVector) {
    	if (listenerVector == null) {return;}
        
        for (int i = listenerVector.size - 1; i >= 0; --i) {
        	EventListener l = listenerVector.get(i);
        	INode node;
        	if((node = l.getAssociatedNode()) != null) {
        		dissociateNodeAndEventListener(node, l);
        		l.setAssociatedNode(null);
        	}
        	
        	if(_inDispatch == 0) {
        		listenerVector.removeIndex(i);
        	}
        }
    }
    
    /** Removes all listeners with the same event listener ID */
    public void removeEventListenersForListenerID(final String listenerID) {
    	EventListenerVector listeners = _listenerMap.get(listenerID);
    	
        if (listeners != null) {
            Array<EventListener> fixedPriorityListeners = listeners.getFixedPriorityListeners();
            Array<EventListener> sceneGraphPriorityListeners = listeners.getSceneGraphPriorityListeners();
            
            removeAllListenersInVector(sceneGraphPriorityListeners);
            removeAllListenersInVector(fixedPriorityListeners);
            
            // Remove the dirty flag according the 'listenerID'.
            // No need to check whether the dispatcher is dispatching event.
            _priorityDirtyFlagMap.remove(listenerID);
            
            if (_inDispatch == 0) {
                listeners.clear();
                _listenerMap.remove(listenerID);
            }
        }
        
        for(int i = _toAddedListeners.size() - 1; i >= 0; --i) {
        	EventListener curr = _toAddedListeners.get(i);
        	if(listenerID.equals(curr.getListenerID())) {
        		curr.setRegistered(false);
        		_toAddedListeners.remove(i);
        	}
        }
    }
    
    /** Sort event listener */
    public void sortEventListeners(final String listenerID) {
    	int dirtyFlag = DirtyFlag.NONE;
        
    	Integer retDirtyFlag = _priorityDirtyFlagMap.get(listenerID);
    	if(retDirtyFlag != null) {
    		dirtyFlag = retDirtyFlag;
    	}
        
        if (dirtyFlag != DirtyFlag.NONE) {
            // Clear the dirty flag first, if `rootNode` is nullptr, then set its dirty flag of scene graph priority
            _priorityDirtyFlagMap.put(listenerID, DirtyFlag.NONE);

            if ((dirtyFlag & DirtyFlag.FIXED_PRIORITY) != 0) {
                sortEventListenersOfFixedPriority(listenerID);
            }
            
            if ((dirtyFlag & DirtyFlag.SCENE_GRAPH_PRIORITY) != 0) {
                IScene rootNode = Director.getInstance().getRunningScene();
                if (rootNode != null) {
                    sortEventListenersOfSceneGraphPriority(listenerID, rootNode);
                } else {
                	_priorityDirtyFlagMap.put(listenerID, DirtyFlag.SCENE_GRAPH_PRIORITY);
                }
            }
        }
    }
    
    /** Sorts the listeners of specified type by scene graph priority */
    public void sortEventListenersOfSceneGraphPriority(final String listenerID, INode rootNode) {
        EventListenerVector listeners = getListeners(listenerID);
        
        if (listeners == null) {return;}
        Array<EventListener> sceneGraphListeners = listeners.getSceneGraphPriorityListeners();
        
        if (sceneGraphListeners == null) {return;}

        // Reset priority index
        _nodePriorityIndex = 0;
        _nodePriorityMap.clear();

        visitTarget(rootNode, true);
        
        // After sort: priority < 0, > 0
        sceneGraphListeners.sort(new Comparator<EventListener>() {
			@Override
			public int compare(EventListener l1, EventListener l2) {
				int p1 = _nodePriorityMap.get(l1._node);
				int p2 = _nodePriorityMap.get(l2._node);
				return p1 - p2;
			}
		});
//        std::sort(sceneGraphListeners->begin(), sceneGraphListeners->end(), [this](const EventListener* l1, const EventListener* l2) {
//            return _nodePriorityMap[l1->getAssociatedNode()] > _nodePriorityMap[l2->getAssociatedNode()];
//        });
        
//    #if DUMP_LISTENER_ITEM_PRIORITY_INFO
//        log("-----------------------------------");
//        for (auto& l : *sceneGraphListeners)
//        {
//            log("listener priority: node ([%s]%p), priority (%d)", typeid(*l->_node).name(), l->_node, _nodePriorityMap[l->_node]);
//        }
//    #endif
    }
    
    /** Sorts the listeners of specified type by fixed priority */
    public void sortEventListenersOfFixedPriority(final String listenerID) {
    	EventListenerVector listeners = getListeners(listenerID);

        if (listeners == null) {return;}
        
        
        Array<EventListener> fixedListeners = listeners.getFixedPriorityListeners();
        if (fixedListeners == null) {return;}
        
        fixedListeners.sort(new Comparator<EventListener>() {
			@Override
			public int compare(EventListener o1, EventListener o2) {
				return o2.getFixedPriority() - o1.getFixedPriority();
			}
		});
        
        // After sort: priority < 0, > 0
//        std::sort(fixedListeners->begin(), fixedListeners->end(), [](const EventListener* l1, const EventListener* l2) {
//            return l1->getFixedPriority() < l2->getFixedPriority();
//        });
        
        // FIXME: Should use binary search
        int index = 0;
        for (EventListener listener : fixedListeners) {
            if (listener.getFixedPriority() >= 0)
                break;
            ++index;
        }
        
        listeners.setGt0Index(index);
        
//    #if DUMP_LISTENER_ITEM_PRIORITY_INFO
//        log("-----------------------------------");
//        for (auto& l : *fixedListeners)
//        {
//            log("listener priority: node (%p), fixed (%d)", l->_node, l->_fixedPriority);
//        }    
//    #endif
    }
    
    private void onUpdateListeners(String listenerID) {
//    	auto listenersIter = _listenerMap.find(listenerID);
    	EventListenerVector listeners = _listenerMap.get(listenerID);
        if (listeners == null) 	{return;}
        
        Array<EventListener> fixedPriorityListeners = listeners.getFixedPriorityListeners();
        Array<EventListener> sceneGraphPriorityListeners = listeners.getSceneGraphPriorityListeners();
        
        if (sceneGraphPriorityListeners != null) {
        	for(int iter = sceneGraphPriorityListeners.size - 1; iter >= 0; --iter) {
        		EventListener l = sceneGraphPriorityListeners.get(iter);
        		if(!l._isRegistered) {
        			sceneGraphPriorityListeners.removeIndex(iter);
        			_toRemovedListeners.remove(l);
        		}
        	}
        }
        
        if (fixedPriorityListeners != null) {
        	for(int i = fixedPriorityListeners.size - 1; i >= 0; --i) {
        		EventListener l = fixedPriorityListeners.get(i);
        		if(!l._isRegistered) {
        			fixedPriorityListeners.removeIndex(i);
        			_toRemovedListeners.remove(l);
        		}
        	}
        }
        
        if (sceneGraphPriorityListeners != null && sceneGraphPriorityListeners.size <= 0) {
            listeners.clearSceneGraphListeners();
        }

        if (fixedPriorityListeners != null && fixedPriorityListeners.size <= 0) {
            listeners.clearFixedListeners();
        }
    }
    
    private void cleanToRemovedListeners() {
    	for(EventListener l : _toRemovedListeners) {
    		
    		EventListenerVector listeners = _listenerMap.get(l.getListenerID());
    		if(listeners == null) {
    			continue;
    		}

            boolean find = false;
            Array<EventListener> fixedPriorityListeners = listeners.getFixedPriorityListeners();
            Array<EventListener> sceneGraphPriorityListeners = listeners.getSceneGraphPriorityListeners();

            if (sceneGraphPriorityListeners != null) {
            	if(sceneGraphPriorityListeners.removeValue(l, true)) {find = true;}
            }
            
            if (fixedPriorityListeners != null) {
            	if(fixedPriorityListeners.removeValue(l, true)) {find = true;}
            }

            if (find) {
                if (sceneGraphPriorityListeners != null && sceneGraphPriorityListeners.size <= 0) {
                    listeners.clearSceneGraphListeners();
                }
                if (fixedPriorityListeners != null && fixedPriorityListeners.size <= 0) {
                    listeners.clearFixedListeners();
                }
            }
    	}
    	_toRemovedListeners.clear();
    }
    
    /** Updates all listeners
     *  1) Removes all listener items that have been marked as 'removed' when dispatching event.
     *  2) Adds all listener items that have been marked as 'added' when dispatching event.
     */
    public void updateListeners(Event event) {
    	assert _inDispatch > 0: "If program goes here, there should be event in dispatch.";

        if (_inDispatch > 1) {return;}

        if (event.getType() == Event.Type.TOUCH) {
            onUpdateListeners(EventListenerTouchOneByOne.LISTENER_ID);
            onUpdateListeners(EventListenerTouchAllAtOnce.LISTENER_ID);
        } else {
            onUpdateListeners(__getListenerID(event));
        }
        
        assert _inDispatch == 1: "_inDispatch should be 1 here.";
        
        Iterator<Entry<String, EventListenerVector>> it = _listenerMap.entrySet().iterator();
        while(it.hasNext()) {
        	if(it.next().getValue().empty()) {
        		it.remove();
        	}
        }
        
        if (!_toAddedListeners.isEmpty()) {
            for (EventListener listener : _toAddedListeners) {
                forceAddEventListener(listener);
            }
            _toAddedListeners.clear();
        }

        if (!_toRemovedListeners.isEmpty()) {
            cleanToRemovedListeners();
        }
    }
    
    //inner class
    class OnTouchEvent implements IFunctionOneArgRet<EventListener, Boolean> {
    	
    	EventTouch 		event;
    	Touch 			currTouch;
    	boolean 		isSwallowed;
    	boolean 		isNeedsMutableSet;
    	Array<Touch> 	mutableTouches;
    	int				mutableTouchesIter;
    	
    	public void init(EventTouch event, Touch currTouch, boolean isNeedMutableSet, boolean isSwallowed,
    			Array<Touch> mutabletouches, int mutableTouchesIter) {
    		this.isSwallowed = isSwallowed;
    		this.event = event;
    		this.currTouch = currTouch;
    		this.isNeedsMutableSet = isNeedMutableSet;
    		this.mutableTouches = mutabletouches;
    		this.mutableTouchesIter = mutableTouchesIter;
    	}
    	
		@Override
		public Boolean callback(EventListener l) {
			EventListenerTouchOneByOne listener = (EventListenerTouchOneByOne)l;
            
            // Skip if the listener was removed.
            if (!listener._isRegistered) {
                return false;
            }
         
            event.setCurrentTarget(listener._node);
            boolean isClaimed = false;
            
            int removedIter = 0;
            EventTouch.EventCode eventCode = event.getEventCode();
            
            if (eventCode == EventTouch.EventCode.BEGAN) {
            	isClaimed = listener.onTouchBegan(currTouch, event);
            	if(isClaimed && listener._isRegistered) {
            		listener._claimedTouches.add(currTouch);
            	}
            } else if (listener._claimedTouches.size > 0  && 
            		(removedIter = listener._claimedTouches.indexOf(currTouch, true)) >= 0) {
                isClaimed = true;
                switch (eventCode)
                {
                    case MOVED:
                    	listener.onTouchMoved(currTouch, event); 
                    	break;
                    case ENDED:
                    	listener.onTouchEnded(currTouch, event);
                    	if(listener._isRegistered) {
                    		listener._claimedTouches.removeIndex(removedIter);
                    	}
                        break;
                    case CANCELLED:
                    	listener.onTouchCancelled(currTouch, event);
                    	listener._claimedTouches.removeIndex(removedIter);
                        break;
                    default:
                        assert false: "The eventcode is invalid.";
                        break;
                }
            }
            
            // If the event was stopped, return directly.
            if (event.isStopped()) {
                updateListeners(event);
                return true;
            }
            
            Touch currMutableTouches = mutableTouches.get(mutableTouchesIter);
            assert currTouch.getID() == currMutableTouches.getID() :"touchesIter ID should be equal to mutableTouchesIter's ID.";
            if (isClaimed && listener._isRegistered && listener._needSwallow) {
                if (isNeedsMutableSet) {
                	mutableTouches.removeIndex(mutableTouchesIter--);
                    isSwallowed = true;
                }
                return true;
            }
            return false;
		}
    }
    private final OnTouchEvent onTouchEvent= new OnTouchEvent();
    
    
    class OnTouchesEvent implements IFunctionOneArgRet<EventListener, Boolean> {
    	EventTouch 		event;
    	Array<Touch> 	mutableTouches;
    	
    	public void init(EventTouch event, Array<Touch> mutableTouches) {
    		this.event = event;
    		this.mutableTouches = mutableTouches;
    	}
    	
		@Override
		public Boolean callback(EventListener l) {
			EventListenerTouchAllAtOnce listener = (EventListenerTouchAllAtOnce)(l);
            // Skip if the listener was removed.
            if (!listener._isRegistered) {return false;}
            event.setCurrentTarget(listener._node);
            
            switch (event.getEventCode())
            {
                case BEGAN:
                	listener.onTouchesBegan(mutableTouches, event);
                    break;
                case MOVED:
                	listener.onTouchesMoved(mutableTouches, event);
                    break;
                case ENDED:
                	listener.onTouchesEnded(mutableTouches, event);
                    break;
                case CANCELLED:
                	listener.onTouchesCancelled(mutableTouches, event);
                    break;
                default:
                    assert false: "The eventcode is invalid.";
                    break;
            }
            // If the event was stopped, return directly.
            if (event.isStopped()) {
                updateListeners(event);
                return true;
            }
            return false;
		}
    }
    private final OnTouchesEvent onTouchesEvent = new OnTouchesEvent();
    
    
    /** Touch event needs to be processed different with other events since it needs support ALL_AT_ONCE and ONE_BY_NONE mode. */
    public void dispatchTouchEvent(EventTouch event) {
    	sortEventListeners(EventListenerTouchOneByOne.LISTENER_ID);
    	sortEventListeners(EventListenerTouchAllAtOnce.LISTENER_ID);
    	
    	EventListenerVector oneByOneListeners = getListeners(EventListenerTouchOneByOne.LISTENER_ID);
    	EventListenerVector allAtOnceListeners = getListeners(EventListenerTouchAllAtOnce.LISTENER_ID);
        
        // If there aren't any touch listeners, return directly.
        if (null == oneByOneListeners && null == allAtOnceListeners) {return;}
        
        boolean isNeedsMutableSet = (oneByOneListeners != null && allAtOnceListeners != null);
        
        final Array<Touch> originalTouches = event.getTouches();
        //TODO stack
        _poolTouchArray.clear();
        Array<Touch> mutableTouches =  _poolTouchArray;
        mutableTouches.clear();
        mutableTouches.addAll(originalTouches);

        //
        // process the target handlers 1st
        //
        if (oneByOneListeners != null) {
        	int mutableTouchesIter = 0;
        	int touchesIter = 0;
            
            for (; touchesIter < originalTouches.size; ++touchesIter)
            {
                boolean isSwallowed = false;
                Touch currTouch = originalTouches.get(touchesIter);
                
                //
                onTouchEvent.init(event, currTouch, isNeedsMutableSet, isSwallowed, 
                		mutableTouches, mutableTouchesIter);
                
                dispatchTouchEventToListeners(oneByOneListeners, onTouchEvent);
                
                isSwallowed = onTouchEvent.isSwallowed;
                mutableTouchesIter = onTouchEvent.mutableTouchesIter;
                
                if (event.isStopped()) 	{return;}
                if (!isSwallowed) 		{++mutableTouchesIter;}
            }
        }
        
        //
        // process standard handlers 2nd
        //
        if (allAtOnceListeners != null && mutableTouches.size > 0) { 
        	
        	onTouchesEvent.init(event, mutableTouches);
            
            dispatchTouchEventToListeners(allAtOnceListeners, onTouchesEvent);
            if (event.isStopped()) {return;}
        }
        updateListeners(event);
    }
    
    /** Associates node with event listener */
    public void associateNodeAndEventListener(INode node, EventListener listener) {
    	ArrayList<EventListener> listeners = null;
    	listeners = _nodeListenersMap.get(node);
    	if(listeners == null) {
    		listeners = new ArrayList<>();
    		_nodeListenersMap.put(node, listeners);
    	}
    	
    	listeners.add(listener);
    }
    
    /** Dissociates node with event listener */
    public void dissociateNodeAndEventListener(INode node, EventListener listener) {
    	ArrayList<EventListener> listeners = null;
    	listeners = _nodeListenersMap.get(node);
    	if(listeners != null) {
    		listeners.remove(listener);
    		if(listeners.isEmpty()) {
    			_nodeListenersMap.remove(node);
    		}
    	}
    }
    
    /** Dispatches event to listeners with a specified listener type */
    public void dispatchEventToListeners(EventListenerVector listeners, IFunctionOneArgRet<EventListener, Boolean> onEvent) {
    	
    	boolean shouldStopPropagation = false;
        Array<EventListener> fixedPriorityListeners = listeners.getFixedPriorityListeners();
        Array<EventListener> sceneGraphPriorityListeners = listeners.getSceneGraphPriorityListeners();
        
        int i = 0;
        // priority < 0
        if (fixedPriorityListeners != null) {
            assert listeners.getGt0Index() <= fixedPriorityListeners.size: "Out of range exception!";
            
            if (fixedPriorityListeners.size > 0) {
                for (; i < listeners.getGt0Index(); ++i)
                {
                    EventListener l = fixedPriorityListeners.get(i);
                    if (l.isEnabled() && !l.isPaused() && l.isRegistered() 
                    		&& onEvent.callback(l)) {
                        shouldStopPropagation = true;
                        break;
                    }
                }
            }
        }
        
        if (sceneGraphPriorityListeners != null) {
            if (!shouldStopPropagation) {
                // priority == 0, scene graph priority
                for (EventListener l : sceneGraphPriorityListeners)
                {
                    if (l.isEnabled() && !l.isPaused() && l.isRegistered() 
                    		&& onEvent.callback(l)) {
                        shouldStopPropagation = true;
                        break;
                    }
                }
            }
        }
        
        if (fixedPriorityListeners != null) {
            if (!shouldStopPropagation) {
                // priority > 0
                int size = fixedPriorityListeners.size;
                for (; i < size; ++i) {
                    EventListener l = fixedPriorityListeners.get(i);
                    
                    if (l.isEnabled() && !l.isPaused() && l.isRegistered() 
                    		&& onEvent.callback(l)) {
                        shouldStopPropagation = true;
                        break;
                    }
                }
            }
        }
    }
    
    public void dispatchTouchEventToListeners(EventListenerVector listeners, IFunctionOneArgRet<EventListener, Boolean> onEvent) {
    	boolean shouldStopPropagation = false;
        Array<EventListener> fixedPriorityListeners = listeners.getFixedPriorityListeners();
        Array<EventListener> sceneGraphPriorityListeners = listeners.getSceneGraphPriorityListeners();
        
        int i = 0;
        // priority < 0
        if (fixedPriorityListeners != null) {
            assert listeners.getGt0Index() <= fixedPriorityListeners.size: "Out of range exception!";
            
            if (fixedPriorityListeners.size > 0) {
                for (; i < listeners.getGt0Index(); ++i)
                {
                    EventListener l = fixedPriorityListeners.get(i);
                    if (l.isEnabled() && !l.isPaused() && l.isRegistered() 
                    		&& onEvent.callback(l)) {
                        shouldStopPropagation = true;
                        break;
                    }
                }
            }
        }
        
        IScene scene = Director.getInstance().getRunningScene();
        if (scene != null && sceneGraphPriorityListeners != null) {
            if (!shouldStopPropagation) {
                // priority == 0, scene graph priority
                
                // first, get all enabled, unPaused and registered listeners
            	//TODO stack
            	_poolArray.clear();
                ArrayList<Object> sceneListeners = _poolArray;
                
                for (EventListener l : sceneGraphPriorityListeners) {
                    if (l.isEnabled() && !l.isPaused() && l.isRegistered()) {
                        sceneListeners.add(l);
                    }
                }
                // second, for all camera call all listeners
                // get a copy of cameras, prevent it's been modified in listener callback
                // if camera's depth is greater, process it earlier
                final int len = scene.getCamerasCount();
                for (int j = 0; j < len; ++j) {
                    ICamera camera = scene.getCamera(j);
                    if (camera.isVisible() == false) {
                        continue;
                    }
                    
                    CameraManager._visitingCamera = camera;
                    int cameraFlag = camera.getCameraFlag();
                    for (Object o : sceneListeners) {
                    	EventListener l = (EventListener) o;	//cast class
                    	
                        if (null == l.getAssociatedNode() || 0 == (l.getAssociatedNode().getCameraMask() & cameraFlag)) {
                            continue;
                        }
                        if (onEvent.callback(l)) {
                            shouldStopPropagation = true;
                            break;
                        }
                    }
                    if (shouldStopPropagation) {
                        break;
                    }
                }
                CameraManager._visitingCamera = null;
                sceneListeners = null;
            }
        }
        
        if (fixedPriorityListeners != null) {
            if (!shouldStopPropagation) {
                // priority > 0
                int size = fixedPriorityListeners.size;
                for (; i < size; ++i)
                {
                    EventListener l = fixedPriorityListeners.get(i);
                    if (l.isEnabled() && !l.isPaused() && l.isRegistered() && 
                    		onEvent.callback(l)) {
                        shouldStopPropagation = true;
                        break;
                    }
                }
            }
        }
    }
    
    /// Priority dirty flag
    public static class DirtyFlag {
        public static final int NONE = 0;
        public static final int FIXED_PRIORITY = 1 << 0;
        public static final int SCENE_GRAPH_PRIORITY = 1 << 1;
        public static final int ALL = FIXED_PRIORITY | SCENE_GRAPH_PRIORITY;
    }

    /** Sets the dirty flag for a specified listener ID */
    public void setDirty(final String listenerID, int flag) {
    	Integer ret = _priorityDirtyFlagMap.get(listenerID);
    	if(ret == null) {
    		_priorityDirtyFlagMap.put(listenerID, flag);
    	} else {
    		int newFlag = ret | flag;
    		_priorityDirtyFlagMap.put(listenerID, newFlag);
    	}
    }
    
    /** Walks though scene graph to get the draw order for each node, it's called before sorting event listener with scene graph priority */
    public void visitTarget(INode node, boolean isRootNode) {
    	node.sortAllChildren();
        
        int i = 0;
        int childrenCount = node.getChildrenCount();
        
        if(childrenCount > 0) {
            INode child = null;
            // visit children zOrder < 0
            for( ; i < childrenCount; i++ )
            {
                child = node.getChild(i);
                if ( child != null && child.getLocalZOrder() < 0 ) {
                    visitTarget(child, false);
                } else {
                    break;
                }
            }
            
            if (_nodeListenersMap.containsKey(node)) {
            	ArrayList<INode> ret = _globalZOrderNodeMap.get(node.getGlobalZOrder());
        		if(ret == null) {
        			ret = new ArrayList<>(2);
        		}
//                _globalZOrderNodeMap.get(node.getGlobalZOrder())
                ret.add(node);
            }
            
            for( ; i < childrenCount; i++ ) {
                child = node.getChild(i);
                if (child != null) {
                    visitTarget(child, false);
                }
            }
        } else {
        	if (_nodeListenersMap.containsKey(node)) {
        		ArrayList<INode> ret = _globalZOrderNodeMap.get(node.getGlobalZOrder());
        		if(ret == null) {
        			ret = new ArrayList<>(2);
        		}
//                _globalZOrderNodeMap.get(node.getGlobalZOrder())
                ret.add(node);
            }
        }
        
        if (isRootNode) {
//            ArrayList<INode> globalZOrders = new ArrayList<>();
            int length = _globalZOrderNodeMap.size();
            Float[] globalZOrders = (Float[]) java.lang.reflect.Array.newInstance(Float.class, length);
            _globalZOrderNodeMap.keySet().toArray(globalZOrders);
            
            Arrays.sort(globalZOrders, new Comparator<Float>(){
				@Override
				public int compare(Float o1, Float o2) {
					return (int) (o1 - o2);
				}
            });
//            std.sort(globalZOrders.begin(), globalZOrders.end(), [](const float a, const float b){
//                return a < b;
//            });
            
            for (float globalZ : globalZOrders) {
                for (INode n : _globalZOrderNodeMap.get(globalZ)) {
                    int priority = _nodePriorityMap.get(n);
                    _nodePriorityMap.put(n, priority + _nodePriorityIndex);
                }
            }
            
            _globalZOrderNodeMap.clear();
        }
    }
    
    /*
     * string - events
     * 				graphListener
     * 				fixListener
     * string - dirtyFlag
     * 
     * node - events(n1, n2, n3)
     * node - priority
     */
    /** Listeners map */
    private HashMap<String, EventListenerVector> _listenerMap = new HashMap<>();
    
    /** The map of dirty flag */
    private HashMap<String, Integer> _priorityDirtyFlagMap = new HashMap<>();
    
    /** The map of node and event listeners */
    private HashMap<INode, ArrayList<EventListener>> _nodeListenersMap = new HashMap<>();
    
    /** The map of node and its event priority */
    private HashMap<INode, Integer> _nodePriorityMap = new HashMap<>();
    
    
    /////////////
    /** key: Global Z Order, value: Sorted Nodes */
    private HashMap<Float, ArrayList<INode>> _globalZOrderNodeMap = new HashMap<>();	//temp
    
    /** The listeners to be added after dispatching event */
    private ArrayList<EventListener> _toAddedListeners = new ArrayList<>();
    
    /** The listeners to be removed after dispatching event */
    private ArrayList<EventListener> _toRemovedListeners = new ArrayList<>();
    
    /** The nodes were associated with scene graph based priority listeners */
    private HashSet<INode> _dirtyNodes = new HashSet<>();
    
    /** Whether the dispatcher is dispatching event */
    private int _inDispatch;
    
    
    /** Whether to enable dispatching event */
    private boolean _isEnabled = true;
    
    private int _nodePriorityIndex;
    
    private HashSet<String> _internalCustomListenerIDs = new HashSet<>();
    
    
    
    /////////////////////////////////////////////////
    private static ArrayList<Object> _poolArray = new ArrayList<>();
    private static Array<Touch> _poolTouchArray = new Array<>();
    /**清理缓存 */
    public static final void clearStack() {
    	_poolArray = new ArrayList<>();
    	_poolTouchArray = new Array<Touch>();
    }
    
    static String __getListenerID(Event event) {
    	String ret = null;
    	switch(event.getType()) {
    	case ACCELERATION:
    		break;
    	case CUSTOM:
    		ret = ((EventCustom)event)._eventName;
    		break;
    	case KEYBOARD:
    		ret = EventListenerKeyboard.LISTENER_ID;
    		break;
    	case MOUSE:
    		break;
    	case FOCUS:
    		ret = EventListenerFocus.LISTENER_ID;
    		break;
    	case TOUCH:
            assert false: "Don't call this method if the event is for touch.";
    		break;
    	case GAME_CONTROLLER:
    		break;
    	}
    	return ret;
    }
}
