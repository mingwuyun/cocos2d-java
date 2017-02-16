package com.cocos2dj.module.base2d;

import com.badlogic.gdx.math.Vector2;
import com.cocos2dj.module.base2d.framework.PhysicsObject;
import com.cocos2dj.module.base2d.framework.callback.UpdateListener;
import com.cocos2dj.module.base2d.framework.common.V2;
import com.cocos2dj.protocol.IComponent;
import com.cocos2dj.s2d.Node;

/**
 * Base2dNode.java
 * <p>
 * 
 * 对ComponentPhysics组件对集成进行了特殊处理，防止位置同步问题
 * 
 * <pre>
 * //create mode:
 * ComponentPhysics phy = new ComponentPhysics();
 * Node node = Base2dNode.create(phy);
 * 
 * // extends mode:
 * class NewNode extends Base2dNode {
 * 	public void onEnter() {
 * 		super.onEnter();
 * 		ComponentPhysics physics = new ComponentPhysics();
 * 		physics.createShapeAsAABB(0,0,10,10);
 * 		addComponent(physics);
 * 	}
 * }
 * </pre>
 * @author Copyright(c) 2017 xujun
 */
public class Base2dNode extends Node implements UpdateListener {

	public static Base2dNode create() {
		return new Base2dNode();
	}
	
	public static Base2dNode create(ComponentPhysics physics) {
		Base2dNode ret = new Base2dNode();
		ret.initWithPhysics(physics);
		return ret;
	}
	
	
	protected ComponentPhysics		_physics;
	
	
	protected Base2dNode() {}
	
	protected int initWithPhysics(ComponentPhysics physics) {
		_physics = physics;
		_physics.setNodeModifer(false);		//close node modifer
		_physics.setPhysicsModifer(false);	//close physics modifer
		_physics.setPositionUpdateListener(this);
		return super.addComponent(_physics);
	}
	
	/**
     *   adds a component
     */
    public int addComponent(IComponent component) {
    	if(component instanceof ComponentPhysics) {	//该节点特殊处理physicsObject的添加
    		return initWithPhysics((ComponentPhysics) component);
    	}
    	return super.addComponent(component);
    }
    
	public void setPosition(float x, float y) {
		setPosition(V2.stackVec2.set(x, y));
	}
	
	public void setPosition(Vector2 position) {
		//转换到world坐标中设置
		if(_parent != null) {
			Vector2 temp = _parent.convertToWorldSpace(position);
			_physics.setPosition(temp.x, temp.y);
		} else {
			Vector2 temp = position;
			_physics.setPosition(temp.x, temp.y);
		}
	}
	
	public final void superSetPosition(final Vector2 pos) {
		float x = pos.x;
		float y = pos.y;
		if (_position.x == x && _position.y == y)
        	return;
    
	    _position.x = x;
	    _position.y = y;
	    
	    _transformUpdated = _transformDirty = _inverseDirty = true;
	    _usingNormalizedPosition = false;
	    _physicsCallFlag = false;
	}
	
	public void onUpdatePosition(PhysicsObject o) {
		//需要转换为world坐标再设置更新
		if(_parent != null) {
			superSetPosition(_parent.convertToNodeSpace(o.getPosition()));
		} else {
			superSetPosition(o.getPosition());
		}
	}
}
