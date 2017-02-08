package com.cocos2dj.s2d;

import com.badlogic.gdx.utils.Array;
import com.cocos2dj.macros.CCLog;
import com.cocos2dj.protocol.IComponent;

/**
 * IComponentContainer.java
 * <p>
 *
 * @author Copyright (c) 2017 xu jun
 */
public class ComponentContainer {
	
	protected ComponentContainer(Node node) {
		this._owner = node;
	}
	
	public IComponent get(int index) {
		return _components.get(index);
	}
	
	public IComponent get(String name) {
		IComponent ret = null;
		for(int i = 0; i < _components.size; ++i) {
			IComponent curr = _components.get(i);
			if(name.equals(curr.getName())) {
				ret = curr;
				break;
			}
		}
		return ret;
	}
	
	
	/**检测组件能否添加 */
	public boolean checkComponent(IComponent com) {
		if(com.getOwner() != null) {
			CCLog.error("ComponentCantainer", "component added in other node. ");
			return false;
		} else if(com.getOwner() == _owner) {
			CCLog.error("ComponentCantainer", "component already in the node. ");
			return false;
		}
		return true;
	}
	
	/**
	 * 返回components的当前尺寸，可以保存后使用index
	 * @param com
	 * @return
	 */
	public int add(IComponent com) {
		int ret = _components.size;
		assert com != null: "IComponent must be non-nil";
	    assert com.getOwner() == null: "IComponent already added. It can't be added again";
	    
	    if(com.getName() != null && get(com.getName()) != null) {
	    	assert false: "IComponentContainer already have this kind of component";
	    	return -1;
	    }
	    
	    if(!checkComponent(com)) {
	    	return -1;
	    }
	    
	    _components.add(com);
	    com.setOwner(_owner);
	    com.onAdd();
	    
		return ret;
	}
	
	/**
	 * 移除指定名称的组件 小心这个方法会重置所有的组件下标
	 * 不建议调用
	 * @param name
	 * @return
	 */
	public boolean remove(String name) {
		for(int i = 0; i < _components.size; ++i) {
			IComponent curr = _components.get(i);
			if(name.equals(curr.getName())) {
				_components.removeIndex(i);
				curr.onRemove();
				curr.setOwner(null);
				return true;
			}
		}
		return false;
	}
	
	public boolean remove(IComponent com) {
		return remove(com.getName());
	}
	
	public void removeAll() {
		for(int i = _components.size - 1; i >= 0; --i) {
			IComponent curr = _components.get(i);
			curr.onRemove();
			curr.setOwner(null);
			_components.removeIndex(i);
		}
		_components.clear();
		_owner.unscheduleUpdate();
	}
	
	public void visit(float delta) {
		for(int i = 0; i < _components.size; ++i) {
			_components.get(i).update(delta);
		}
	}
	
	public void onEnter() {
		for(int i = 0; i < _components.size; ++i) {
			_components.get(i).onEnter();
		}
	}
	
	public void onExit() {
		for(int i = 0; i < _components.size; ++i) {
			_components.get(i).onExit();
		}
	}
	
	public boolean isEmpty() {
		return _components.size <= 0;
	}
	
	//pools
	public void onSleep() {
		for(int i = 0; i < _components.size; ++i) {
			_components.get(i).onSleep();
		}
	}
	
	public void onAwake() {
		for(int i = 0; i < _components.size; ++i) {
			_components.get(i).onAwake();
		}
	}
	
	//fields>>
	private Array<IComponent> 	_components = new Array<>(2);
	private final Node 			_owner;
	//fields<<
}
