package com.cocos2dj.s2d;

import com.badlogic.gdx.utils.Array;

/**
 * ComponentContainer.java
 * <p>
 *
 * @author Copyright (c) 2017 xu jun
 */
public class ComponentContainer {
	
	protected ComponentContainer(Node node) {
		this._owner = node;
	}
	
	public Component get(int index) {
		return _components.get(index);
	}
	
	public Component get(String name) {
		Component ret = null;
		for(int i = 0; i < _components.size; ++i) {
			Component curr = _components.get(i);
			if(name.equals(curr._name)) {
				ret = curr;
				break;
			}
		}
		return ret;
	}
	
	/**
	 * 返回components的当前尺寸，可以保存后使用index
	 * @param com
	 * @return
	 */
	public int add(Component com) {
		int ret = _components.size;
		assert com != null: "Component must be non-nil";
	    assert com.getOwner() == null: "Component already added. It can't be added again";
	    
	    if(get(com.getName()) != null) {
	    	assert false: "ComponentContainer already have this kind of component";
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
			Component curr = _components.get(i);
			if(name.equals(curr.getName())) {
				_components.removeIndex(i);
				curr.onRemove();
				curr.setOwner(null);
				return true;
			}
		}
		return false;
	}
	
	public boolean remove(Component com) {
		return remove(com.getName());
	}
	
	public void removeAll() {
		for(int i = _components.size - 1; i >= 0; --i) {
			Component curr = _components.get(i);
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
	
	
	//fields>>
	private Array<Component> 	_components = new Array<>(2);
	private final Node 			_owner;
	//fields<<
}
