package com.cocos2dj.s2d;

import java.util.Comparator;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.cocos2dj.base.ActionManager;
import com.cocos2dj.base.AffineTransform;
import com.cocos2dj.base.Director;
import com.cocos2dj.base.Director.MATRIX_STACK_TYPE;
import com.cocos2dj.macros.CCLog;
import com.cocos2dj.base.EventDispatcher;
import com.cocos2dj.base.Rect;
import com.cocos2dj.base.Scheduler;
import com.cocos2dj.base.Size;
import com.cocos2dj.base.Touch;
import com.cocos2dj.protocol.IFunctionOneArgRet;
import com.cocos2dj.protocol.INode;
import com.cocos2dj.protocol.IUpdater;
import com.cocos2dj.renderer.Renderer;

/** 
 * Node.java
 * <p>
 * 
 * <b>The main features of a Node are:</b>
 * <li>They can contain other Node objects (`addChild`, `getChildByTag`, `removeChild`, etc)
 * <li>They can schedule periodic callback (`schedule`, `unschedule`, etc)
 * <li>They can execute actions (`runAction`, `stopAction`, etc)
 * 
 * <br><b>Subclassing a Node usually means (one/all) of:</b>
 * <li>overriding init to initialize resources and schedule callbacks
 * <li>create callbacks to handle the advancement of time
 * <li>overriding `draw` to render the node
 * <br><b>Properties of Node:</b>
 * <li>position (default: x=0, y=0)
 * <li>scale (default: x=1, y=1)
 * <li>rotation (in degrees, clockwise) (default: 0)
 * <li>anchor point (default: x=0, y=0)
 * <li>contentSize (default: width=0, height=0)
 * <li>visible (default: true)
 * 
 * @author Copyright (c) 2017 xu jun
 */
public class Node implements INode, IUpdater {
	
	public static final int INVALID_TAG = -1;

	public static final int FLAGS_TRANSFORM_DIRTY = (1 << 0);
	public static final int FLAGS_CONTENT_SIZE_DIRTY = (1 << 1);
	public static final int FLAGS_DIRTY_MASK = (FLAGS_TRANSFORM_DIRTY | FLAGS_CONTENT_SIZE_DIRTY);

	
    ///////////////////////////////////
    /// @name Constructor, Destructor and Initializers
	//ctor>>
	public Node() {
//    	setNormalizedPosition
//		System.out.println("caonima  " + _director);
		// 保证init执行，不要添加其他的ctor
		init();
    }
	
	/** release this node */
	protected void release() {
		this._userData = null;
		
		this._parent = null;
		
		removeAllComponents();
		stopAllActions();
		unscheduleAllCallbacks();
		
		_director.getEventDispatcher().removeEventListenersForTarget(this);
//		_director = null;
//		_additionalTransform = null;
	}
	
    /**
     * Allocates and initializes a node.
     * @return A initialized node which is marked as "autorelease".
     */
    public static Node create() {
    	return new Node();
    }

    /**
     * Gets the description string. It makes debugging easier.
     * @return A string
     */
    public String getDescription() {
    	return "<node>";
    }
    ////////////////////////////////////////
    
    
    
    
    //TODO Setters & Getters for Graphic Peroperties
    /**
     LocalZOrder is the 'key' used to sort the node relative to its siblings.
     The Node's parent will sort all its children based ont the LocalZOrder value.
     If two nodes have the same LocalZOrder, then the node that was added first to the children's array will be in front of the other node in the array.
     Also, the Scene Graph is traversed using the "In-Order" tree traversal algorithm ( http://en.wikipedia.org/wiki/Tree_traversal#In-order )
     And Nodes that have LocalZOder values < 0 are the "left" subtree
     While Nodes with LocalZOder >=0 are the "right" subtree.
     @see `setGlobalZOrder`
     */
    public void setLocalZOrder(int localZOrder) {
    	if(_localZOrder == localZOrder) {return;}
    	
    	_localZOrder = localZOrder;
    	if(_parent != null) {
    		_parent.reorderChild(this, localZOrder);
    	}
    	_director.getEventDispatcher().setDirtyForNode(this);
    }

    public void setZOrder(int localZOrder) { setLocalZOrder(localZOrder); }
    /* Helper function used by `setLocalZOrder`. Don't use it unless you know what you are doing.
     */
    final void _setLocalZOrder(int z) {
    	_localZOrder = z;
    }
    
    /**
     * Gets the local Z order of this node.
     *
     * @see `setLocalZOrder(int)`
     *
     * @return The local (relative to its siblings) Z order.
     */
    public int getLocalZOrder() { return _localZOrder; }
    public int getZOrder() { return getLocalZOrder(); }

    /**
     Defines the oder in which the nodes are renderer.
     Nodes that have a Global Z Order lower, are renderer first.
     
     In case two or more nodes have the same Global Z Order, the oder is not guaranteed.
     The only exception if the Nodes have a Global Z Order == 0. In that case, the Scene Graph order is used.
     
     By default, all nodes have a Global Z Order = 0. That means that by default, the Scene Graph order is used to render the nodes.
     
     Global Z Order is useful when you need to render nodes in an order different than the Scene Graph order.
     
     Limitations: Global Z Order can't be used used by Nodes that have SpriteBatchNode as one of their acenstors.
     And if ClippingNode is one of the ancestors, then "global Z order" will be relative to the ClippingNode.
     @see `setLocalZOrder()`
     */
    public void setGlobalZOrder(float globalZOrder) {
    	if(_globalZOrder != globalZOrder) {
    		_globalZOrder = globalZOrder;
    		_director.getEventDispatcher().setDirtyForNode(this);
    	}
    }
    
    /**
     * Returns the Node's Global Z Order.
     * @see `setGlobalZOrder(int)`
     * @return The node's global Z order
     */
    public float getGlobalZOrder() { return _globalZOrder; }
    /////////////////////////////////////////
    //TODO 变换相关
    public void setScaleX(float scaleX) {
    	if (_scaleX == scaleX)
            return;
        
        _scaleX = scaleX;
        _transformUpdated = _transformDirty = _inverseDirty = true;
    }
    public float getScaleX() {return _scaleX;}
    
    public void setScaleY(float scaleY) {
    	if (_scaleY == scaleY)
            return;
        
        _scaleY = scaleY;
        _transformUpdated = _transformDirty = _inverseDirty = true;
    }
    public float getScaleY(){return _scaleY; }

    /**
     * Changes the scale factor on Z axis of this node
     *
     * The Default value is 1.0 if you haven't changed it before.
     *
     * @param scaleY   The scale factor on Y axis.
     *
     * @warning The physics body doesn't support this.
     */
    public void setScaleZ(float scaleZ) {
    	 if (_scaleZ == scaleZ)
    	        return;
    	    
    	 _scaleZ = scaleZ;
    	 _transformUpdated = _transformDirty = _inverseDirty = true;
    }
    
    /**
     * Returns the scale factor on Z axis of this node
     *
     * @see `setScaleZ(float)`
     *
     * @return The scale factor on Z axis.
     */
    public float getScaleZ() {
    	return _scaleZ;
    }

    /**
     * Sets the scale (x,y,z) of the node.
     *
     * It is a scaling factor that multiplies the width, height and depth of the node and its children.
     *
     * @param scale     The scale factor for both X and Y axis.
     *
     * @warning The physics body doesn't support this.
     */
    public void setScale(float scale) {
    	if (_scaleX == scale && _scaleY == scale && _scaleZ == scale)
            return;
        
        _scaleX = _scaleY = _scaleZ = scale;
        _transformUpdated = _transformDirty = _inverseDirty = true;
    }
    
    /**
     * Gets the scale factor of the node,  when X and Y have the same scale factor.
     *
     * @warning Assert when `_scaleX != _scaleY`
     * @see setScale(float)
     *
     * @return The scale factor of the node.
     */
    public float getScale() {
    	assert _scaleX == _scaleY : "CCNode#scale. ScaleX != ScaleY. Don't know which one to return";
        return _scaleX;
    }

     /**
     * Sets the scale (x,y) of the node.
     *
     * It is a scaling factor that multiplies the width and height of the node and its children.
     *
     * @param scaleX     The scale factor on X axis.
     * @param scaleY     The scale factor on Y axis.
     *
     * @warning The physics body doesn't support this.
     */
    public void setScale(float scaleX, float scaleY) {
    	if (_scaleX == scaleX && _scaleY == scaleY)
            return;
        
        _scaleX = scaleX;
        _scaleY = scaleY;
        _transformUpdated = _transformDirty = _inverseDirty = true;
    }

    /**
     * Sets the position (x,y) of the node in its parent's coordinate system.
     *
     * Usually we use `Vector2(x,y)` to compose Vector2 object.
     * This code snippet sets the node in the center of screen.
     @code
     Size size = Director::getInstance()->getWinSize();
     node->setPosition( Vector2(size.width/2, size.height/2) )
     @endcode
     *
     * @param position  The position (x,y) of the node in OpenGL coordinates
     */
    public void setPosition(final Vector2 position) {
    	setPosition(position.x, position.y);
    }

    /**
     * 设置百分比坐标<p> 
     * Sets the position (x,y) using values between 0 and 1.
     The positions in pixels is calculated like the following:
     @code
     // pseudo code
     void setNormalizedPosition(Vector2 pos) {
       Size s = getParent()->getContentSize();
       _position = pos * s;
     }
     @endcode
     */
    public void setNormalizedPosition(final Vector2 position) {
    	if (_normalizedPosition == null) {
    		_normalizedPosition = new Vector2();
    	}
    	if (_normalizedPosition.equals(position)) {
            return;
    	}

        _normalizedPosition = position;
        _usingNormalizedPosition = true;
        _normalizedPositionDirty = true;
        _transformUpdated = _transformDirty = _inverseDirty = true;
    }

    /**
     * Gets the position (x,y) of the node in its parent's coordinate system.
     *
     * @see setPosition( Vector2&)
     *
     * @return The position (x,y) of the node in OpenGL coordinates
     * @code
     * In js and lua return value is table which contains x,y
     * @endcode
     */
    public final Vector2 getPosition() {
    	return _position;
    }

    /** returns the normalized position */
    public Vector2 getNormalizedPosition() {
    	return _normalizedPosition;
    }

    /**
     * Sets the position (x,y) of the node in its parent's coordinate system.
     *
     * Passing two numbers (x,y) is much efficient than passing Vector2 object.
     * This method is bound to Lua and JavaScript.
     * Passing a number is 10 times faster than passing a object from Lua to c++
     *
     @code
     // sample code in Lua
     local pos  = node::getPosition()  -- returns Vector2 object from C++
     node:setPosition(x, y)            -- pass x, y coordinate to C++
     @endcode
     *
     * @param x     X coordinate for position
     * @param y     Y coordinate for position
     */
    public void setPosition(float x, float y) {
    	if (_position.x == x && _position.y == y)
            return;
        
        _position.x = x;
        _position.y = y;
        
        _transformUpdated = _transformDirty = _inverseDirty = true;
        _usingNormalizedPosition = false;
    }
    
    /**
     * Gets position in a more efficient way, returns two number instead of a Vector2 object
     *
     * @see `setPosition(float, float)`
     * In js,out value not return
     */
    public Vector2 getPosition(final Vector2 ret) {
    	ret.x = _position.x;
    	ret.y = _position.y;
    	return ret;
    }
    
    /**
     * Gets/Sets x or y coordinate individually for position.
     * These methods are used in Lua and Javascript Bindings
     */
    public void  setPositionX(float x) {
    	setPosition(x, _position.y);
    }
    public float getPositionX() {
    	return _position.x;
    }
    
    public void  setPositionY(float y) {
    	setPosition(_position.x, y);
    }
    
    public float getPositionY() {
    	return _position.y;
    }

    /**
     * Sets the position (X, Y, and Z) in its parent's coordinate system
     */
    public void setPosition3D(Vector3 position) {
    	setPositionZ(position.z);
        setPosition(position.x, position.y);
    }
    
    /**
     * returns the position (X,Y,Z) in its parent's coordinate system
     */
    public Vector3 getPosition3D(Vector3 ret) {
    	return ret.set(_position.x, _position.y, _positionZ);
    }
    
    /**
     * returns the position (X,Y,Z) in its parent's coordinate system
     */
    public Vector3 getPosition3D() {
    	return new Vector3(_position.x, _position.y, _positionZ);
    }

    /**
     * Sets the 'z' coordinate in the position. It is the OpenGL Z vertex value.
     *
     * The OpenGL depth buffer and depth testing are disabled by default. You need to turn them on
     * in order to use this property correctly.
     *
     * `setPositionZ()` also sets the `setGlobalZValue()` with the positionZ as value.
     *
     * @see `setGlobalZValue()`
     *
     * @param vertexZ  OpenGL Z vertex of this node.
     */
    public void setPositionZ(float positionZ) {
    	if (_positionZ == positionZ)
            return;
        
        _transformUpdated = _transformDirty = _inverseDirty = true;

        _positionZ = positionZ;
    }

    /**
     * Gets position Z coordinate of this node.
     */
    public float getPositionZ() {
    	return _positionZ;
    }
    
    /**
     * Sets the anchor point in percent.
     *
     * anchorPoint is the point around which all transformations and positioning manipulations take place.
     * It's like a pin in the node where it is "attached" to its parent.
     * The anchorPoint is normalized, like a percentage. (0,0) means the bottom-left corner and (1,1) means the top-right corner.
     * But you can use values higher than (1,1) and lower than (0,0) too.
     * The default anchorPoint is (0.5,0.5), so it starts in the center of the node.
     * @note If node has a physics body, the anchor must be in the middle, you cann't change this to other value.
     *
     * @param anchorPoint   The anchor point of node.
     */
    public void setAnchorPoint(Vector2 anchorPoint) {
    	setAnchorPoint(anchorPoint.x, anchorPoint.y);
    }
    public void setAnchorPoint(float anchorPointX, float anchorPointY) {
    	if(anchorPointX != _anchorPointX || anchorPointY != _anchorPointY) {
    		_anchorPointX = anchorPointX;
    		_anchorPointY = anchorPointY;
    		_anchorPointInPoints.set(_contentSize.width * _anchorPointX, _contentSize.height * _anchorPointY);
    		_transformUpdated = _transformDirty = _inverseDirty = true;
    	}
    }
    
    
    /**
     * Returns the anchor point in percent. 通过pool传出
     *
     * @see `setAnchorPoint( Vector2&)`
     *
     * @return The anchor point of node.
     */
    public Vector2 getAnchorPoint() {
    	return poolVector2_1.set(_anchorPointX, _anchorPointY);
    }
    public float getAnchorPointX() {return _anchorPointX;}
    public float getAnchorPointY() {return _anchorPointY;}
    
    
    /**
     * Returns the anchorPoint in absolute pixels.
     *
     * @warning You can only read it. If you wish to modify it, use anchorPoint instead.
     * @see `getAnchorPoint()`
     *
     * @return The anchor point in absolute pixels.
     */
    public Vector2 getAnchorPointInPoints() {
    	return _anchorPointInPoints;
    }


    /**
     * Sets the untransformed size of the node.
     *
     * The contentSize remains the same no matter the node is scaled or rotated.
     * All nodes has a size. Layer and Scene has the same size of the screen.
     *
     * @param contentSize   The untransformed size of the node.
     */
    public void setContentSize(Size contentSize) {
    	setContentSize(contentSize.width, contentSize.height);
    }
    public void setContentSize(float w, float h) {
    	if (_contentSize.width != w || _contentSize.height != h) {
            _contentSize.width = w;
            _contentSize.height = h;

            _anchorPointInPoints.set(_contentSize.width * _anchorPointX, _contentSize.height * _anchorPointY);
            _transformUpdated = _transformDirty = _inverseDirty = _contentSizeDirty = true;
        }
    }
    
    /**
     * Returns the untransformed size of the node.
     *
     * @see `setContentSize( Size&)`
     *
     * @return The untransformed size of the node.
     */
    public  Size getContentSize() {
    	return _contentSize;
    }

    /**
     * Sets the rotation (angle) of the node in degrees.
     *
     * 0 is the default rotation angle.
     * Positive values rotate node clockwise, and negative values for anti-clockwise.
     *
     * @param rotation     The rotation of the node in degrees.
     */
    public void setRotation(float rotation) {
    	if (_rotationZ == rotation)
    	        return;
    	    
    	_rotationZ = rotation;
	    _transformUpdated = _transformDirty = _inverseDirty = true;
	    
	    updateRotationQuat();
    }
    
    /**
     * Returns the rotation of the node in degrees.
     *
     * @see `setRotation(float)`
     *
     * @return The rotation of the node in degrees.
     */
    public float getRotation() {
        return _rotationZ;
    }

    /**
     * Sets the rotation (X,Y,Z) in degrees.
     * Useful for 3d rotations
     *
     * @warning The physics body doesn't support this.
     */
    public void setRotation3D(Vector3 rotation) {
    	if (_rotationX == rotation.x && _rotationY == rotation.y && _rotationZ == rotation.z)
    	        return;
    	    
    	    _transformUpdated = _transformDirty = _inverseDirty = true;

    	    _rotationX = rotation.x;
    	    _rotationY = rotation.y;
    	    _rotationZ = rotation.z;
    	    
    	    updateRotationQuat();
    }
    
    /**
     * returns the rotation (X,Y,Z) in degrees.
     */
    public Vector3 getRotation3D() {
        return new Vector3(_rotationX,_rotationY,_rotationZ);
    }
    
    public Vector3 getRotation3D(final Vector3 ret) {
    	return ret.set(_rotationX, _rotationY, _rotationZ);
    }
    
    public final void updateRotationQuat() {
//        _rotationQuat.setEulerAngles(_rotationX, _rotationY, _rotationZ);
    }

    public final void updateRotation3D() {
        //convert quaternion to Euler angle
//    	_rotationX =  _rotationQuat.getPitch();
//    	_rotationY = _rotationQuat.getYaw();
//    	_rotationZ = _rotationQuat.getRoll();
    }
    
    /////////////////////////////////////////////////////
    
    
    
    /**
     * Sets whether the node is visible
     * The default value is true, a node is default to visible
     * @param visible   true if the node is visible, false if the node is hidden.
     */
    public void setVisible(boolean visible) {
    	if(visible != _visible) {
    		_visible = visible;
    		if(_visible) {
    			_transformUpdated = _transformDirty = _inverseDirty = true;
    		}
    	}
    }
    
    /**
     * Determines if the node is visible
     * @see `setVisible(bool)`
     * @return true if the node is visible, false if the node is hidden.
     */
    public boolean isVisible() {
    	return _visible;
    }

    
    /**
     * Sets the arrival order when this node has a same ZOrder with other children.
     * A node which called addChild subsequently will take a larger arrival order,
     * If two children have the same Z order, the child with larger arrival order will be drawn later.
     * @warning This method is used internally for localZOrder sorting, don't change this manually
     * @param orderOfArrival   The arrival order.
     */
    public void setOrderOfArrival(int orderOfArrival) {
    	assert orderOfArrival >= 0 : "Invalid orderOfArrival";
    	_orderOfArrival = orderOfArrival;
    }
    /**
     * Returns the arrival order, indicates which children is added previously.
     *
     * @see `setOrderOfArrival(unsigned int)`
     *
     * @return The arrival order.
     */
    public int getOrderOfArrival() {
    	return _orderOfArrival;
    }

    /**
     * Sets whether the anchor point will be (0,0) when you position this node.
     * This is an internal method, only used by Layer and Scene. Don't call it outside framework.
     * The default value is false, while in Layer and Scene are true
     * @param ignore    true if anchor point will be (0,0) when you position this node
     * @todo This method should be renamed as setIgnoreAnchorPointForPosition(bool) or something with "set"
     */
    final void ignoreAnchorPointForPosition(boolean ignore) {
    	if(ignore != _ignoreAnchorPointForPosition) {
    		_ignoreAnchorPointForPosition = ignore;
    		_transformUpdated = _transformDirty = _inverseDirty = true;
    	}
    }
    
    void setIgnoreAnchorPointForPosition(boolean ignore) {
    	ignoreAnchorPointForPosition(ignore);
    }
    
    /**
     * Gets whether the anchor point will be (0,0) when you position this node.
     * @see `ignoreAnchorPointForPosition(bool)`
     * @return true if the anchor point will be (0,0) when you position this node.
     */
    public boolean isIgnoreAnchorPointForPosition() {
    	return _ignoreAnchorPointForPosition;
    }

    ///////////////////////////////////////////////
    //TODO Children and Parent
    /**
     * Adds a child to the container with z-order as 0.
     *
     * If the child is added to a 'running' node, then 'onEnter' and 'onEnterTransitionDidFinish' will be called immediately.
     *
     * @param child A child node
     */
    public void addChild(Node child) {
    	assert child != null: "Argument must be non-nil";
    	this.addChild(child, child._localZOrder, child._name);
    }
    
    /**
     * Adds a child to the container with a local z-order
     *
     * If the child is added to a 'running' node, then 'onEnter' and 'onEnterTransitionDidFinish' will be called immediately.
     *
     * @param child     A child node
     * @param zOrder    Z order for drawing priority. Please refer to `setLocalZOrder(int)`
     */
    public void addChild(Node child, int localZOrder) {
    	assert child != null: "Argument must be non-nil";
    	this.addChild(child, localZOrder, child._name);
    }
    
    /**
     * Adds a child to the container with z order and tag
     *
     * If the child is added to a 'running' node, then 'onEnter' and 'onEnterTransitionDidFinish' will be called immediately.
     *
     * @param child     A child node
     * @param zOrder    Z order for drawing priority. Please refer to `setLocalZOrder(int)`
     * @param tag       An integer to identify the node easily. Please refer to `setTag(int)`
     * 
     * Please use `addChild(Node* child, int localZOrder,  std::string &name)` instead.
     */
     public void addChild(Node child, int localZOrder, int tag) {
    	 assert child != null: "Argument must be non-nil";
    	 assert child._parent == null: "child already added. It can't be added again";
    	 addChildHelper(child, localZOrder, tag, "", true);
     }
     
    /**
     * Adds a child to the container with z order and tag
     *
     * If the child is added to a 'running' node, then 'onEnter' and 'onEnterTransitionDidFinish' will be called immediately.
     *
     * @param child     A child node
     * @param zOrder    Z order for drawing priority. Please refer to `setLocalZOrder(int)`
     * @param name      A string to identify the node easily. Please refer to `setName(int)`
     *
     */
    public void addChild(Node child, int localZOrder,  String name) {
    	assert child != null: "Argument must be non-nil";
   	 	assert child._parent == null: "child already added. It can't be added again";
   	 	addChildHelper(child, localZOrder, INVALID_TAG, name, false);
    }
    
    /**
     * Gets a child from the container with its tag
     *
     * @param tag   An identifier to find the child node.
     *
     * @return a Node object whose tag equals to the input parameter
     *
     * Please use `getChildByName()` instead
     */
     public Node getChildByTag(int tag) {
    	 assert tag != Node.INVALID_TAG: "Invalid tag";
    	 
    	 for(Node node : _children) {
    		 if(node._tag == tag) {
    			 return node;
    		 }
    	 }
    	 return null;
     }
     
    /**
     * Gets a child from the container with its name
     *
     * @param name   An identifier to find the child node.
     *
     * @return a Node object whose name equals to the input parameter
     *
     * @since v3.2
     */
    public Node getChildByName(String name) {
    	for(Node node : _children) {
   		 	if(name.equals(node._name)) {
   		 		return node;
   		 	}
   	 	}
   	 	return null;
    }
    
    /**
     * Gets a child from the container with its name that can be cast to Type T
     *
     * @param name   An identifier to find the child node.
     *
     * @return a Node with the given name that can be cast to Type T
    */
//    template <typename T>
//    public <T> T getChildByNameCast(String name)  { return (T)(getChildByName(name)); }
    /** Search the children of the receiving node to perform processing for nodes which share a name.
     *
     * @param name The name to search for, supports c++11 regular expression.
     * Search syntax options:
     * `//`: Can only be placed at the begin of the search string. This indicates that it will search recursively.
     * `..`: The search should move up to the node's parent. Can only be placed at the end of string
     * `/` : When placed anywhere but the start of the search string, this indicates that the search should move to the node's children.
     *
     * @code
     * enumerateChildren("//MyName", ...): This searches the children recursively and matches any node with the name `MyName`.
     * enumerateChildren("[[:alnum:]]+", ...): This search string matches every node of its children.
     * enumerateChildren("A[[:digit:]]", ...): This searches the node's children and returns any child named `A0`, `A1`, ..., `A9`
     * enumerateChildren("Abby/Normal", ...): This searches the node's grandchildren and returns any node whose name is `Normal`
     * and whose parent is named `Abby`.
     * enumerateChildren("//Abby/Normal", ...): This searches recursively and returns any node whose name is `Normal` and whose
     * parent is named `Abby`.
     * @endcode
     *
     * @warning Only support alpha or number for name, and not support unicode
     *
     * @param callback A callback function to execute on nodes that match the `name` parameter. The function takes the following arguments:
     *  `node` 
     *      A node that matches the name
     *  And returns a boolean result. Your callback can return `true` to terminate the enumeration.
     *
     * @since v3.2
     */
    public void enumerateChildren( String name, IFunctionOneArgRet<Node, Boolean> callback) {
    	assert false : "no implements";
    }
    
    /**
     * Returns the array of the node's children
     *
     * @return the array the node's children
     */
    public Array<Node> getChildren() { return _children; }
//    public Array<Node> getChildren() { return _children; }
    
    
    /** 
     * Returns the amount of children
     *
     * @return The amount of children.
     */
    @Override
    public int getChildrenCount() {
    	return _children.size;
    }
    
    @Override
    public INode getChild(final int index) {
    	return _children.get(index);
    }
    

    /**
     * Sets the parent node
     *
     * @param parent    A pointer to the parent node
     */
    public void setParent(Node parent) {
    	this._parent = parent;
    	_transformUpdated = _transformDirty = _inverseDirty = true;
    }
    
    /**
     * Returns a pointer to the parent node
     *
     * @see `setParent(Node*)`
     *
     * @returns A pointer to the parent node
     */
    public  Node getParent()  { return _parent; }
    
    /**
     * Removes this node itself from its parent node with a cleanup.
     * If the node orphan, then nothing happens.
     * @see `removeFromParentAndCleanup(bool)`
     */
    public void removeFromParent() {
    	if(_parent != null) {
    		_parent.removeChild(this, true);
    	}
    }
    
    /**
     * Removes this node itself from its parent node.
     * If the node orphan, then nothing happens.
     * @param cleanup   true if all actions and callbacks on this node should be removed, false otherwise.
     * @js removeFromParent
     * @lua removeFromParent
     */
    public void removeFromParentAndCleanup(boolean cleanup) {
    	if(_parent != null) {
    		_parent.removeChild(this, cleanup);
    	}
    }

    /**
     * Removes a child from the container. It will also cleanup all running actions depending on the cleanup parameter.
     *
     * @param child     The child node which will be removed.
     * @param cleanup   true if all running actions and callbacks on the child node will be cleanup, false otherwise.
     */
    public void removeChild(Node child, boolean cleanup) {
    	if(_children.size <= 0) {return;}
    	
    	int index = _children.indexOf(child, true);
    	if(index != -1) {
    		this.detachChild(child, index, cleanup);
    	}
    }

    /**
     * Removes a child from the container by tag value. It will also cleanup all running actions depending on the cleanup parameter
     * @param tag       An interger number that identifies a child node
     * @param cleanup   true if all running actions and callbacks on the child node will be cleanup, false otherwise.
     * Please use `removeChildByName` instead.
     */
     public void removeChildByTag(int tag, boolean cleanup) {
    	 assert tag != Node.INVALID_TAG: "Invalid tag";
    	 
    	 Node child = getChildByTag(tag);
    	 if(child == null) {
    		 CCLog.engine("Node", "cocos2d: removeChildByTag(tag = " + tag + "): child not found!");
    	 } else {
    		 removeChild(child, cleanup);
    	 }
     }
     
    /**
     * Removes a child from the container by tag value. It will also cleanup all running actions depending on the cleanup parameter
     *
     * @param name       A string that identifies a child node
     * @param cleanup   true if all running actions and callbacks on the child node will be cleanup, false otherwise.
     */
    public void removeChildByName(String name, boolean cleanup) {
    	Node child = getChildByName(name);
	   	if(child == null) {
	   		CCLog.engine("Node", "cocos2d: removeChildByName(name = " + name + "): child not found!");
	   	} else {
	   		removeChild(child, cleanup);
	   	}
    }
    
    /**
     * Removes all children from the container with a cleanup.
     * @see `removeAllChildrenWithCleanup(bool)`
     */
    public void removeAllChildren() {
    	removeAllChildrenWithCleanup(true);
    }
    
    /**
     * Removes all children from the container, and do a cleanup to all running actions depending on the cleanup parameter.
     *
     * @param cleanup   true if all running actions on all children nodes should be cleanup, false oterwise.
     * @js removeAllChildren
     * @lua removeAllChildren
     */
    public void removeAllChildrenWithCleanup(boolean cleanup) {
    	for(Node child : _children) {
    		if(_running) {
    			child.onExitTransitionDidStart();
    			child.onExit();
    		}
    		
    		if(cleanup) {
    			child.cleanup();
    		}
    		
    		child.setParent(null);
    	}
    	
    	_children.clear();
    }

    /**
     * Reorders a child according to a new z value.
     * @param child     An already added child node. It MUST be already added.
     * @param localZOrder Z order for drawing priority. Please refer to setLocalZOrder(int)
     */
    public void reorderChild(Node child, int zOrder) {
    	assert child != null: "Child must be non-nil";
        _reorderChildDirty = true;
        child.setOrderOfArrival(s_globalOrderOfArrival++);
        child._localZOrder = zOrder;
    }

    /**
     * Sorts the children array once before drawing, instead of every time when a child is added or reordered.
     * This appraoch can improves the performance massively.
     * @note Don't call this manually unless a child added needs to be removed in the same frame
     */
    public void sortAllChildren() {
    	if(_reorderChildDirty) {
    		_reorderChildDirty = false;
    		_children.sort(nodeComparisonLess);
    	}
    }
    
  /// helper that reorder a child
  	protected void insertChild(Node child, int z) {
  		_transformUpdated = true;
  	    _reorderChildDirty = true;
  	    _children.add(child);
  	    child._localZOrder = z;
  	}
  	
  	private void addChildHelper(Node child, int localZOrder, int tag, String name, boolean setTag) {
  		this.insertChild(child, localZOrder);
  		if (setTag) {
  			child.setTag(tag);
  		} else {
  			child.setName(name);
  		}
      
  		child.setParent(this);
  		child.setOrderOfArrival(s_globalOrderOfArrival++);
      
  		if(_running) {
  			child.onEnter();
  			// prevent onEnterTransitionDidFinish to be called twice when a node is added in onEnter
  			if (_isTransitionFinished) {
  				child.onEnterTransitionDidFinish();
  			}
  		}
      
  		if (_cascadeColorEnabled) {
  			updateCascadeColor();
  		}
  		if (_cascadeOpacityEnabled) {
  			updateCascadeOpacity();
  		}
  	}

      /// Removes a child, call child->onExit(), do cleanup, remove it from children array.
  	protected void detachChild(Node child, int index, boolean doCleanup) {
  		if(_running) {
  			child.onExitTransitionDidStart();
  			child.onExit();
  		}
  		
  		if(doCleanup) {
  			child.cleanup();
  		}
  		
  		child.setParent(null);
  		
  		_children.removeIndex(index);
  	}
  	
  	////////////////////////////////////////////////////////
    /**
     * Returns a tag that is used to identify the node easily.
     */
     public int getTag() {
    	 return _tag;
     }
     
    /**
     * Changes the tag that is used to identify the node easily.
     */
     public void setTag(int tag) {
    	 this._tag = tag;
     }
    
    /** Returns a string that is used to identify the node.
     * @return A string that identifies the node.
     * 
     * @since v3.2
     */
    public String getName() {
    	return this._name;
    }
    
    /** Changes the name that is used to identify the node easily.
     * @param name A string that identifies the node.
     *
     * @since v3.2
     */
    public void setName(String name) {
    	this._name = name;
    }
    
    /**
     * Returns a custom user data pointer
     */
    public Object getUserData() { return _userData; }

    /**
     * Sets a custom user data pointer
     */
    public void setUserData(Object userData) {
    	this._userData = userData;
    }
    ////////////////////////////////////////////////////////


    /// @{
    /// @name GLProgram
    /**
     * Return the GLProgram (shader) currently used for this node
     *
     * @return The GLProgram (shader) currently used for this node
     */
//    Shad getGLProgram() ;
//    CC_DEPRECATED_ATTRIBUTE GLProgram* getShaderProgram()  { return getGLProgram(); }

//    GLProgramState *getGLProgramState() ;
//    void setGLProgramState(GLProgramState *glProgramState);
//    ShaderProgram d;

    /**
     * Sets the shader program for this node
     *
     * Since v2.0, each rendering node must set its shader program.
     * It should be set in initialize phase.
     @code
     node->setGLrProgram(GLProgramCache::getInstance()->getProgram(GLProgram::SHADER_NAME_POSITION_TEXTURE_COLOR));
     @endcode
     *
     * @param shaderProgram The shader program
     */
//    public void setGLProgram(GLProgram *glprogram);
//    CC_DEPRECATED_ATTRIBUTE void setShaderProgram(GLProgram *glprogram) { setGLProgram(glprogram); }
    /// @} end of Shader Program


    /**
     * Returns whether or not the node is "running".
     *
     * If the node is running it will accept event callbacks like onEnter(), onExit(), update()
     *
     * @return Whether or not the node is running.
     */
    public boolean isRunning() {
    	return _running;
    }
    //////////////////////////////////////////
    //TODO Event Callbacks

    /**
     * Event callback that is invoked every time when Node enters the 'stage'.
     * If the Node enters the 'stage' with a transition, this event is called when the transition starts.
     * During onEnter you can't access a "sister/brother" node.
     * If you override onEnter, you shall call its parent's one, e.g., Node::onEnter().
     * @js NA
     * @lua NA
     */
    public void onEnter() {
    	if(_onEnterCallback != null) {
    		_onEnterCallback.onEnter(this);
    	}	
    	
    	//TODO component
    	if (_componentContainer != null && !_componentContainer.isEmpty()) {
            _componentContainer.onEnter();
        }
    	
    	_isTransitionFinished = false;
    	
    	for(int i = 0; i < _children.size; ++i) {
    		_children.get(i).onEnter();
    	}
    	
    	this.resume();
    	
    	_running = true;
    }

    /** Event callback that is invoked when the Node enters in the 'stage'.
     * If the Node enters the 'stage' with a transition, this event is called when the transition finishes.
     * If you override onEnterTransitionDidFinish, you shall call its parent's one, e.g. Node::onEnterTransitionDidFinish()
     * @js NA
     * @lua NA
     */
    public void onEnterTransitionDidFinish() {
    	if(_onEnterTransitionDidFinishCallback != null) {
    		_onEnterTransitionDidFinishCallback.onEnterTransitionDidFinish(this);
    	}
    	
    	_isTransitionFinished = true;
        for(int i = 0; i < _children.size; ++i) {
            _children.get(i).onEnterTransitionDidFinish();
        }
    }

    /**
     * Event callback that is invoked every time the Node leaves the 'stage'.
     * If the Node leaves the 'stage' with a transition, this event is called when the transition finishes.
     * During onExit you can't access a sibling node.
     * If you override onExit, you shall call its parent's one, e.g., Node::onExit().
     * @js NA
     * @lua NA
     */
    public void onExit() {
    	if (_onExitCallback != null) {
    		_onExitCallback.onExit(this);
    	}
	    if (_componentContainer != null && !_componentContainer.isEmpty()) {
	        _componentContainer.onExit();
	    }
	    
    	this.pause();
    	
    	 _running = false;
    	 
    	for(int i = 0; i < _children.size; ++i) {
    		_children.get(i).onExit();
    	}
    	
    	
    }

    /**
     * Event callback that is called every time the Node leaves the 'stage'.
     * If the Node leaves the 'stage' with a transition, this callback is called when the transition starts.
     * @js NA
     * @lua NA
     */
    public void onExitTransitionDidStart() {
    	
    }

    //////////////////////////////////
    //TODO renderer 
    final protected boolean isVisitableByVisitingCamera() {
    	//TODO unfinsh
    	Camera camera = Camera.getVisitingCamera();
//    	boolean visibleByCamera = camera == null ? true : (camera.getCameraFlag() & _cameraMask) != 0;
//    	return visibleByCamera;
    	return true;
    }
    
    /**
     * Override this method to draw your own node.
     * The following GL states will be enabled by default:
     * - `glEnableClientState(GL_VERTEX_ARRAY);`
     * - `glEnableClientState(GL_COLOR_ARRAY);`
     * - `glEnableClientState(GL_TEXTURE_COORD_ARRAY);`
     * - `glEnable(GL_TEXTURE_2D);`
     * AND YOU SHOULD NOT DISABLE THEM AFTER DRAWING YOUR NODE
     * But if you enable any other GL state, you should disable it after drawing your node.
     */
    public void draw(Renderer renderer, Matrix4 transform, int flags) {
    	
    }
    
    public final void draw() {
    	Renderer renderer = _director.getRenderer();
    	Matrix4 _modelViewTransform = _director.getMatrix(MATRIX_STACK_TYPE.MATRIX_STACK_MODELVIEW);
    	draw(renderer, _modelViewTransform, 1);
    }

    /**
     * Visits this node's children and draw them recursively.
     */
    public void visit(Renderer renderer,  Matrix4 parentTransform, int parentFlags) {
    	// quick return if not visible. children won't be drawn.
        if (!_visible) {
            return;
        }
        
        int flags = processParentFlags(parentTransform, parentFlags);

        // IMPORTANT:
        // To ease the migration to v3.0, we still support the Mat4 stack,
        // but it is deprecated and your code should not rely on it
        _director.pushMatrix(MATRIX_STACK_TYPE.MATRIX_STACK_MODELVIEW, _modelViewTransform);
        
        boolean visibleByCamera = isVisitableByVisitingCamera();

        int i = 0;

//        System.out.println("node =" + _scaleX);
        
        if(_children.size > 0) {
            sortAllChildren();
            // draw children zOrder < 0
            for( ; i < _children.size; i++ )
            {
                Node node = _children.get(i);
                if (node != null && node._localZOrder < 0) {
                    node.visit(renderer, _modelViewTransform, flags);
                } else {
                    break;
                }
            }
            
            // self draw
            if (visibleByCamera) {
                this.draw(renderer, _modelViewTransform, flags);
        	}
            
            for(; i < _children.size; ++i) {
            	_children.get(i).visit(renderer, _modelViewTransform, flags);
            }
        } else if (visibleByCamera) {
            this.draw(renderer, _modelViewTransform, flags);
        }
        
        _director.popMatrix(MATRIX_STACK_TYPE.MATRIX_STACK_MODELVIEW);
        // FIX ME: Why need to set _orderOfArrival to 0??
        // Please refer to https://github.com/cocos2d/cocos2d-x/pull/6920
        // reset for next frame
        // _orderOfArrival = 0;
    }
    
    public final void visit() {
    	Renderer renderer = _director.getRenderer();
        Matrix4 parentTransform = _director.getMatrix(MATRIX_STACK_TYPE.MATRIX_STACK_MODELVIEW);
        visit(renderer, parentTransform, 1);
    }

    ////////////////////////////////
    /**
     * Stops all running actions and schedulers
     */
    public void cleanup() {
    	stopAllActions();
    	unscheduleAllCallbacks();
    	
    	for(int i = 0; i < _children.size; ++i) {
    		_children.get(i).cleanup();
    	}
    }
    
    /** Returns the Scene that contains the Node.
     It returns `nullptr` if the node doesn't belong to any Scene.
     This function recursively calls parent->getScene() until parent is a Scene object. The results are not cached. It is that the user caches the results in case this functions is being used inside a loop.
     */
    public Scene getScene() {
    	if (_parent == null) {
            return null;
    	}
        
        Node sceneNode = _parent;
        while (sceneNode._parent != null) {
            sceneNode = sceneNode._parent;
        }
    	return (Scene) sceneNode;
    }

    /**
     * Returns an AABB (axis-aligned bounding-box) in its parent's coordinate system.
     *
     * @return An AABB (axis-aligned bounding-box) in its parent's coordinate system
     */
    public Rect getBoundingBox() {
    	
    	return null;
    }

//    /** @deprecated Use getBoundingBox instead */
//    CC_DEPRECATED_ATTRIBUTE inline publi.c Rect boundingBox()  { return getBoundingBox(); }

    public void setEventDispatcher(EventDispatcher dispatcher) {
//    	if(dispatcher != getEventDispatcher()) {
//    		_director.setEventDispatcher(dispatcher);
//    	}
    	assert false : "don't use this method";
    }
    
    public EventDispatcher getEventDispatcher()  { 
    	return _director.getEventDispatcher(); 
    }

    
    ////////////////////////////////////////
    //TODO Actions

    /**
     * Sets the ActionManager object that is used by all actions.
     *
     * @warning If you set a new ActionManager, then previously created actions will be removed.
     *
     * @param actionManager     A ActionManager object that is used by all actions.
     */
    public void setActionManager(ActionManager actionManager) {
    	assert false : "don't use this method";
    }
    
    /**
     * Gets the ActionManager object that is used by all actions.
     * @see setActionManager(ActionManager*)
     * @return A ActionManager object.
     */
    public ActionManager getActionManager() { 
    	return _director.getActionManager(); 
   }
    
//    public  ActionManager* getActionManager()  { return _actionManager; }

    /**
     * Executes an action, and returns the action that is executed.
     *
     * This node becomes the action's target. Refer to Action::getTarget()
     * @warning Actions don't retain their target.
     *
     * @return An Action pointer
     */
    public Action runAction(Action action) {
    	assert action != null : "Argument must be non-nil";
    	_director.getActionManager().addAction(action, this, !_running);
    	return action;
    }

    /**
     * Stops and removes all actions from the running action list .
     */
    public void stopAllActions() {
    	_director.getActionManager().removeAllActionsFromTarget(this);
    }

    /**
     * Stops and removes an action from the running action list.
     * @param action    The action object to be removed.
     */
    public void stopAction(Action action) {
    	_director.getActionManager().removeAction(action);
    }

    /**
     * Removes an action from the running action list by its tag.
     * @param tag   A tag that indicates the action to be removed.
     */
    public void stopActionByTag(int tag) {
    	assert tag != INVALID_TAG : "Invalid tag";
    	_director.getActionManager().removeActionByTag(tag, this);
    }

    /**
     * Removes all action from the running action list by its tag.
     * @param tag   A tag that indicates the action to be removed.
     */
    public void stopAllActionsByTag(int tag) {
    	assert tag != INVALID_TAG : "Invalid tag";
    	_director.getActionManager().removeAllActionsByTag(tag, this);
    }

    /**
    * Removes all actions from the running action list by its flags.
    * @param flags   A flag field that removes actions based on bitwise AND.
    */
    public void stopActionsByFlags(int flags) {
        if (flags > 0) {
            _director.getActionManager().removeActionsByFlags(flags, this);
        }
    }
    
    /**
     * Gets an action from the running action list by its tag.
     * @see setTag(int), getTag().
     * @return The action object with the given tag.
     */
    public Action getActionByTag(int tag) {
    	assert tag != INVALID_TAG : "Invalid tag";
    	return (Action) _director.getActionManager().getActionByTag(tag, this);
    }

    /**
     * Returns the numbers of actions that are running plus the ones that are schedule to run (actions in actionsToAdd and actions arrays).
     *
     * Composable actions are counted as 1 action. Example:
     *    If you are running 1 Sequence of 7 actions, it will return 1.
     *    If you are running 7 Sequences of 2 actions, it will return 7.
     * @todo Rename to getNumberOfRunningActions()
     *
     * @return The number of actions that are running plus the ones that are schedule to run
     */
    public int getNumberOfRunningActions() {
    	return _director.getActionManager().getNumberOfRunningActionsInTarget(this);
    }


    ////////////////////////////////////////
    //TODO Scheduler and Timer

    /**
     * Sets a Scheduler object that is used to schedule all "updates" and timers.
     *
     * @warning If you set a new Scheduler, then previously created timers/update are going to be removed.
     * @param scheduler     A Shdeduler object that is used to schedule all "update" and timers.
     */
    public void setScheduler(Scheduler scheduler) {
    	_director.setScheduler(scheduler);
    }
    
    /**
     * Gets a Sheduler object.
     *
     * @see setScheduler(Scheduler*)
     * @return A Scheduler object.
     */
    public Scheduler getScheduler() {
    	return _director.getScheduler();
    }


    /**
     * Checks whether a selector is scheduled.
     *
     * @param selector      A function selector
     * @return Whether the funcion selector is scheduled.
     * @js NA
     * @lua NA
     */
    public boolean isScheduled(IUpdater selector) {
    	return _director.getScheduler().isScheduled(selector, this);
    }

    /**
     * Schedules the "update" method.
     *
     * It will use the order number 0. This method will be called every frame.
     * Scheduled methods with a lower order value will be called before the ones that have a higher order value.
     * Only one "update" method could be scheduled per node.
     * @js NA
     * @lua NA
     */
    public void scheduleUpdate() {
    	this.scheduleUpdateWithPriority(0);
    }
    
    /**
     * Schedules the "update" method with a custom priority.
     *
     * This selector will be called every frame.
     * Scheduled methods with a lower priority will be called before the ones that have a higher value.
     * Only one "update" selector could be scheduled per node (You can't have 2 'update' selectors).
     * @js NA
     * @lua NA
     */
    public void scheduleUpdateWithPriority(int priority) {
    	_director.getScheduler().scheduleUpdate(this, this, priority, !_running);
    }

    /*
     * Unschedules the "update" method.
     * @see scheduleUpdate();
     */
    public void unscheduleUpdate() {
    	_director.getScheduler().unscheduleUpdate(this);
    }

    /**
     * Schedules a custom selector.
     *
     * If the selector is already scheduled, then the interval parameter will be updated without scheduling it again.
     @code
     // firstly, implement a schedule function
     void MyNode::TickMe(float dt);
     // wrap this function into a selector via schedule_selector marco.
     this->schedule(schedule_selector(MyNode::TickMe), 0, 0, 0);
     @endcode
     *
     * @param selector  The SEL_SCHEDULE selector to be scheduled.
     * @param interval  Tick interval in seconds. 0 means tick every frame. If interval = 0, it's recommended to use scheduleUpdate() instead.
     * @param repeat    The selector will be excuted (repeat + 1) times, you can use kRepeatForever for tick infinitely.
     * @param delay     The amount of time that the first tick will wait before execution.
     * @lua NA
     */
    public void schedule(IUpdater selector, float interval, int repeat, float delay) {
    	assert selector != null: "Argument must be non-nil";
        assert interval >=0: "Argument must be positive";
    	_director.getScheduler().schedule(selector, this, interval, repeat, delay, !_running);
    }

    /**
     * Schedules a custom selector with an interval time in seconds.
     * @see `schedule(SEL_SCHEDULE, float, unsigned int, float)`
     *
     * @param selector      The SEL_SCHEDULE selector to be scheduled.
     * @param interval      Callback interval time in seconds. 0 means tick every frame,
     * @lua NA
     */
    public void schedule(IUpdater selector, float interval) {
    	this.schedule(selector, interval, Scheduler.CC_REPEAT_FOREVER, 0f);
    }

    /**
     * Schedules a custom selector, the scheduled selector will be ticked every frame
     * @see schedule(SEL_SCHEDULE, float, unsigned int, float)
     *
     * @param selector      A function wrapped as a selector
     * @lua NA
     */
    public void schedule(IUpdater selector) {
    	this.schedule(selector, 0f, Scheduler.CC_REPEAT_FOREVER, 0f);
    }
    
    /**
     * Schedules a selector that runs only once, with a delay of 0 or larger
     * @see `schedule(SEL_SCHEDULE, float, unsigned int, float)`
     *
     * @param selector      The SEL_SCHEDULE selector to be scheduled.
     * @param delay         The amount of time that the first tick will wait before execution.
     * @lua NA
     */
    public void scheduleOnce(IUpdater selector, float delay) {
    	this.schedule(selector, 0f, 0, delay);
    }

    /**
     * Schedules a lambda function that runs only once, with a delay of 0 or larger
     *
     * @param callback      The lambda function to be scheduled.
     * @param delay         The amount of time that the first tick will wait before execution.
     * @param key           The key of the lambda function. To be used if you want to unschedule it.
     * @lua NA
     */
    public void scheduleOnce(IUpdater callback, float delay, String key) {
    	_director.getScheduler().schedule(callback, this, 0f, 0, delay, !_running, key);
    }

    
    /**
     * Unschedules a custom selector.
     * @see `schedule(SEL_SCHEDULE, float, unsigned int, float)`
     *
     * @param selector      A function wrapped as a selector
     * @lua NA
     */
    public void unschedule(IUpdater selector) {
    	if(selector == null) {
    		return;
    	}
    	
    	_director.getScheduler().unschedule(this, selector);
    }
   
    /**
     * Unschedules a lambda function.
     *
     * @param key      The key of the lambda function to be unscheduled.
     * @lua NA
     */
    public void unschedule(String key) {
    	_director.getScheduler().unschedule(this, key);
    }
    
    /**
     * Unschedule all scheduled selectors: custom selectors, and the 'update' selector.
     * Actions are not affected by this method.
     * @lua NA
     */
    public void unscheduleAllCallbacks() {
    	_director.getScheduler().unscheduleAllForTarget(this);
    }
    
    

    /**
     * Resumes all scheduled selectors, actions and event listeners.
     * This method is called internally by onEnter
     */
    public void resume() {
    	_director.getScheduler().resumeTarget(this);
    	_director.getActionManager().resumeTarget(this);
    	_director.getEventDispatcher().resumeEventListenersForTarget(this);
    }
    
    /**
     * Pauses all scheduled selectors, actions and event listeners..
     * This method is called internally by onExit
     */
    public void pause() {
    	_director.getScheduler().pauseTarget(this);
    	_director.getActionManager().pauseTarget(this);
    	_director.getEventDispatcher().pauseEventListenersForTarget(this);
    }

    /**
     * Resumes all scheduled selectors, actions and event listeners.
     * This method is called internally by onEnter
     */
//    CC_DEPRECATED_ATTRIBUTE void resumeSchedulerAndActions();
    /**
     * Pauses all scheduled selectors, actions and event listeners..
     * This method is called internally by onExit
     */
//    CC_DEPRECATED_ATTRIBUTE void pauseSchedulerAndActions();

    /*
     * Update method will be called automatically every frame if "scheduleUpdate" is called, and the node is "live"
     */
    public boolean update(float delta) {
    	if(_onUpdateCallback != null) {
    		_onUpdateCallback.onUpdate(this, delta);
    	}
    	
    	if(_componentContainer != null && !_componentContainer.isEmpty()) {
    		_componentContainer.visit(delta);
    	}
    	return false;
    }
    
    ///////////////////////////////////////////////////////////
    
    //TODO Transformations
    /**
     * Calls children's updateTransform() method recursively.
     *
     * This method is moved from Sprite, so it's no longer specific to Sprite.
     * As the result, you apply SpriteBatchNode's optimization on your customed Node.
     * e.g., `batchNode->addChild(myCustomNode)`, while you can only addChild(sprite) before.
     */
    public void updateTransform() {
    	// Recursively iterate over children
        for(int i = 0; i < _children.size; ++i) {
            _children.get(i).updateTransform();
        }
    }

    /**
     * Returns the matrix that transform the node's (local) space coordinates into the parent's space coordinates.
     * The matrix is in Pixels.
     * Note: If ancestor is not a valid ancestor of the node, the API would return the same value as @see getNodeToWorldTransform
     *
     * @param ancestor The parent's node pointer.
     * @since v3.7
     * @return The transformation matrix. <b>(pool object)</b>
     */
    public final Matrix4 getNodeToParentTransform(Node ancestor) {
    	final Matrix4 t = poolMatrix_1;
    	t.set(getNodeToParentTransform());
//        Mat4 t(this->getNodeToParentTransform());
        for (Node p = _parent;  p != null && p != ancestor ; p = p._parent) {
//            t = p->getNodeToParentTransform() * t;
            t.mulLeft(p.getNodeToParentTransform());
        }
        return t;
    }
    
    /**
     * Returns the matrix that transform the node's (local) space coordinates into the parent's space coordinates.
     * The matrix is in Pixels.
     */
    public Matrix4 getNodeToParentTransform() {
    	if (_transformDirty) {
    		
            float x = _position.x, y = _position.y, z = _positionZ;
            
            if (_ignoreAnchorPointForPosition) {
                x += _anchorPointInPoints.x;
                y += _anchorPointInPoints.y;
            }
            
            final float anchorPointX = _anchorPointInPoints.x * _scaleX;
            final float anchorPointY = _anchorPointInPoints.y * _scaleY;
            
            // Build Transform Matrix = translation * rotation * scale
            final Matrix4 translation = poolMatrix_1.idt();
            //move to anchor point first, then rotate
            translation.setTranslation(x + anchorPointX, y + anchorPointY, z);
            
            _transform.setFromEulerAngles(_rotationX, _rotationY, _rotationZ);
//            _transform.set(_rotationQuat);
            
            _transform.mulLeft(translation);
            
            
            //move by (-anchorPoint.x, -anchorPoint.y, 0) after rotation
            _transform.val[Matrix4.M03] -= anchorPointX;
            _transform.val[Matrix4.M13] -= anchorPointY;
            
            
            if (_scaleX != 1.f) {
//            	_tr
            	_transform.val[Matrix4.M00] *= _scaleX; 
            	_transform.val[Matrix4.M01] *= _scaleX; 
            	_transform.val[Matrix4.M02] *= _scaleX;
            }
            if (_scaleY != 1.f) {
            	_transform.val[Matrix4.M10] *= _scaleY; 
            	_transform.val[Matrix4.M11] *= _scaleY; 
            	_transform.val[Matrix4.M12] *= _scaleY;
            }
            if (_scaleZ != 1.f) {
            	_transform.val[Matrix4.M20] *= _scaleZ;
            	_transform.val[Matrix4.M21] *= _scaleZ; 
            	_transform.val[Matrix4.M22] *= _scaleZ;
            }
        }

        if (_additionalTransform != null) {
        	//TODO unfinished
//            // This is needed to support both Node::setNodeToParentTransform() and Node::setAdditionalTransform()
//            // at the same time. The scenario is this:
//            // at some point setNodeToParentTransform() is called.
//            // and later setAdditionalTransform() is called every time. And since _transform
//            // is being overwritten everyframe, _additionalTransform[1] is used to have a copy
//            // of the last "_trasform without _additionalTransform"
//            if (_transformDirty)
////                _additionalTransform[1] = _transform;
//
//            if (_transformUpdated)
//                _transform = _additionalTransform[1] * _additionalTransform[0];
        }

        _transformDirty = _additionalTransformDirty = false;

        return _transform;
    }
    
    public AffineTransform getNodeToParentAffineTransform() {
    	
    	return null;
    }

    /** 
     * Sets the Transformation matrix manually.
     */
    public void setNodeToParentTransform( Matrix4 transform) {
    	
    	return;
    }

    /** @deprecated use getNodeToParentTransform() instead */
//    CC_DEPRECATED_ATTRIBUTE inline public AffineTransform nodeToParentTransform()  { return getNodeToParentAffineTransform(); }

    /**
     * Returns the matrix that transform parent's space coordinates to the node's (local) space coordinates.
     * The matrix is in Pixels.
     */
    public  Matrix4 getParentToNodeTransform() {
    	
    	return null;
    }
    
    public AffineTransform getParentToNodeAffineTransform() {
    	
    	return null;
    }

    /** @deprecated Use getParentToNodeTransform() instead */
//    CC_DEPRECATED_ATTRIBUTE inline public AffineTransform parentToNodeTransform()  { return getParentToNodeAffineTransform(); }

    /**
     * Returns the world affine transform matrix. The matrix is in Pixels.
     * @return <b>(pool object)</b>
     */
    public Matrix4 getNodeToWorldTransform() {
    	return getNodeToParentTransform(null);
    }
    
    public AffineTransform getNodeToWorldAffineTransform() {
    	return null;
    }

    /** @deprecated Use getNodeToWorldTransform() instead */
//    CC_DEPRECATED_ATTRIBUTE inline public AffineTransform nodeToWorldTransform()  { return getNodeToWorldAffineTransform(); }

    /**
     * Returns the inverse world affine transform matrix. The matrix is in Pixels.
     */
    public Matrix4 getWorldToNodeTransform() {
    	
    	return null;
    }
    
    public AffineTransform getWorldToNodeAffineTransform() {
    	return null;
    }
    
    

    ////////////////////////////////////////////////////////////////


    /// @{
    /// @name Coordinate Converters

    /**
     * Converts a Vector2 to node (local) space coordinates. The result is in Points.
     */
    public Vector2 convertToNodeSpace( Vector2 worldPoint) {
    	
    	return null;
    }

    /**
     * Converts a Vector2 to world space coordinates. The result is in Points.
     */
    public Vector2 convertToWorldSpace( Vector2 nodePoint) {
    	
    	return null;
    }

    /**
     * Converts a Vector2 to node (local) space coordinates. The result is in Points.
     * treating the returned/received node point as anchor relative.
     */
    public Vector2 convertToNodeSpaceAR( Vector2 worldPoint) {
    	return null;
    }

    /**
     * Converts a local Vector2 to world space coordinates.The result is in Points.
     * treating the returned/received node point as anchor relative.
     */
    public Vector2 convertToWorldSpaceAR( Vector2 nodePoint) {
    	return null;
    }

    /**
     * convenience methods which take a Touch instead of Vector2
     */
    public Vector2 convertTouchToNodeSpace(Touch  touch) {
    	return null;
    }

    /**
     * converts a Touch (world coordinates) into a local coordinate. This method is AR (Anchor Relative).
     */
    public Vector2 convertTouchToNodeSpaceAR(Touch  touch) {
    	return null;
    }

	/**
     *  Sets an additional transform matrix to the node.
     *
     *  In order to remove it, call it again with the argument `nullptr`
     *
     *  @note The additional transform will be concatenated at the end of getNodeToParentTransform.
     *        It could be used to simulate `parent-child` relationship between two nodes (e.g. one is in BatchNode, another isn't).
     */
    public void setAdditionalTransform(Matrix4 additionalTransform) {
    	
    }
    
    public void setAdditionalTransform( AffineTransform additionalTransform) {
    	
    }

    ///////////////////////////////////////////////////
    //TODO component functions
    /**
     *   gets a component by its name
     */
    public Component getComponent(String name) {
    	if(_componentContainer != null) {
    		return _componentContainer.get(name);
    	}
    	return null;
    }
    
    /**
     *   gets a component by its index
     */
    public Component getComponent(int index) {
    	if(_componentContainer != null) {
    		return _componentContainer.get(index);
    	}
    	return null;
    }

    /**
     *   adds a component
     */
    public int addComponent(Component component) {
    	if(_componentContainer == null) {
    		_componentContainer = new ComponentContainer(this);
    	}
    	// should enable schedule update, then all components can receive this call back
        scheduleUpdate();
    	
    	return _componentContainer.add(component);
    }

    /**
     *   removes a component by its name
     */
    public boolean removeComponent(String name) {
    	if(_componentContainer != null) {
    		return _componentContainer.remove(name);
    	}
    	return false;
    }
    
    public boolean removeComponent(Component comp) {
    	if(_componentContainer != null) {
    		return _componentContainer.remove(comp);
    	}
    	return false;
    }

    /**
     *   removes all components
     */
    public void removeAllComponents() {
    	if(_componentContainer != null) {
    		_componentContainer.removeAll();
    	}
    }
    
    /////////////////////////////////////
    
    // overrides
//    public GLubyte getOpacity() ;
//    public GLubyte getDisplayedOpacity() ;
//    public void setOpacity(GLubyte opacity);
//    public void updateDisplayedOpacity(GLubyte parentOpacity);
//    public boolean isCascadeOpacityEnabled() ;
//    public void setCascadeOpacityEnabled(boolean cascadeOpacityEnabled);
//    
//    public  Color3B& getColor() ;
//    public  Color3B& getDisplayedColor() ;
//    public void setColor( Color3B& color);
//    public void updateDisplayedColor( Color3B& parentColor);
//    public boolean isCascadeColorEnabled() ;
//    public void setCascadeColorEnabled(boolean cascadeColorEnabled);
//    
//    public void setOpacityModifyRGB(boolean value) {CC_UNUSED_PARAM(value);}
//    public boolean isOpacityModifyRGB()  { return false; };

    public final void setOnEnterCallback(OnEnterCallback callback) { _onEnterCallback = callback; }
    public final OnEnterCallback getOnEnterCallback()  { return _onEnterCallback; }   
    public final void setOnExitCallback(OnExitCallback callback) { _onExitCallback = callback; }
    public final OnExitCallback getOnExitCallback()  { return _onExitCallback; }   
    public final void setonEnterTransitionDidFinishCallback(OnEnterTransitionDidFinishCallback callback) { _onEnterTransitionDidFinishCallback = callback; }
    public final void setOnUpdateCallback(OnUpdateCallback callback) {_onUpdateCallback = callback;}
    public final OnUpdateCallback getOnUpdateCallback() {return _onUpdateCallback;}
    public final OnEnterTransitionDidFinishCallback getonEnterTransitionDidFinishCallback()  { return _onEnterTransitionDidFinishCallback; }   
    public final void setonExitTransitionDidStartCallback(OnExitTransitionDidStartCallback callback) { _onExitTransitionDidStartCallback = callback; }
    public final OnExitTransitionDidStartCallback getonExitTransitionDidStartCallback()  { return _onExitTransitionDidStartCallback; }   
    
    //java ext
    public final void setNodeCallback(NodeCallback callback) {
    	this._onEnterCallback = callback;
    	this._onExitCallback = callback;
    	this._onUpdateCallback = callback;
    }
    
    public final void setNodeWithTransitionCallback(NodeWithTransitionCallback callback) {
    	this._onEnterCallback = callback;
    	this._onExitCallback = callback;
    	this._onUpdateCallback = callback;
    	this._onEnterTransitionDidFinishCallback = callback;
    	this._onExitTransitionDidStartCallback = callback;
    }
    
//CC_CONSTRUCTOR_ACCESS:
    // Nodes should be created using create();
//    Node();
//    public ~Node();
    
    //override
    public void init() {
    	
    }

    
    /// lazy allocs
//	protected void childrenAlloc() {
//		_children.ensureCapacity(4);
//	}
    
    

    /// Convert cocos2d coordinates to UI windows coordinate.
	protected Vector2 convertToWindowSpace(Vector2 nodePoint) {
		
		return null;
	}

	protected Matrix4 transform(final Matrix4 parentTransform) {
		//TODO transform method>>>
//		System.out.println(getDescription() + "parent trans"+ "\n" + parentTransform);
		Matrix4 trans = getNodeToParentTransform();
//		System.out.println(getDescription() + "local trans"+ "\n" + trans);
		Matrix4 ret = _modelViewTransform.set(trans).mulLeft(parentTransform);
//		System.out.println(getDescription() + "ret trans"+ "\n" + ret);
		
		// 2d rotation transform
		_modelRotationZ = _rotationZ + (_parent != null ? _parent._modelRotationZ : 0);
//		_
//		_modelRotationZ
		
		return ret;
	}
	
	protected int processParentFlags( Matrix4 parentTransform, int parentFlags) {
		if(_usingNormalizedPosition) {
	        assert _parent != null: "setNormalizedPosition() doesn't work with orphan nodes";
	        if ((parentFlags & FLAGS_CONTENT_SIZE_DIRTY) != 0 || _normalizedPositionDirty) {
	            Size s = _parent.getContentSize();
	            _position.x = _normalizedPosition.x * s.width;
	            _position.y = _normalizedPosition.y * s.height;
	            _transformUpdated = _transformDirty = _inverseDirty = true;
	            _normalizedPositionDirty = false;
	        }
	    }

	    //remove this two line given that isVisitableByVisitingCamera should not affect the calculation of transform given that we are visiting scene
	    //without involving view and projection matrix.
	    
//	    if (!isVisitableByVisitingCamera())
//	        return parentFlags;
	    
	    int flags = parentFlags;
	    flags |= (_transformUpdated ? FLAGS_TRANSFORM_DIRTY : 0);
	    flags |= (_contentSizeDirty ? FLAGS_CONTENT_SIZE_DIRTY : 0);
	    

	    if((flags & FLAGS_DIRTY_MASK) != 0) {	//
//	    	System.out.println("update >>>>> trans" + getDescription() + _position
//	    			+ "\n" + parentTransform);
	        _modelViewTransform = this.transform(parentTransform);
//	        Vector3 d = new Vector3();
//	        _modelViewTransform.getTranslation(d);
//	        System.out.println("transro m = \n" + _modelViewTransform);
	    }
	    
	    _transformUpdated = false;
	    _contentSizeDirty = false;

	    return flags;
	}

	protected void updateCascadeOpacity() {
		
	}
	
	protected void disableCascadeOpacity() {
		
	}
	
	protected void updateCascadeColor() {
		
	}
	
	protected void disableCascadeColor() {
		
	}
	
	protected void updateColor() {
		
	}
    
//    boolean doEnumerate(String name, std::function<boolean (Node *)> callback) ;
//    boolean doEnumerateRecursive( Node* node,  std::string &name, std::function<boolean (Node *)> callback) ;
    
//#if CC_USE_PHYSICS
//    void updatePhysicsBodyTransform(Scene* layer);
//    public void updatePhysicsBodyPosition(Scene* layer);
//    public void updatePhysicsBodyRotation(Scene* layer);
//    public void updatePhysicsBodyScale(Scene* scene);
//#endif // CC_USE_PHYSICS
    

    
    ////////////////////////////////////////////////////
    //fields>>
    protected float _rotationX;               ///< rotation on the X-axis
    protected float _rotationY;               ///< rotation on the Y-axis
    protected float _rotationZ;             ///< rotation angle on Z-axis

    protected float _scaleX = 1;                  ///< scaling factor on x-axis
    protected float _scaleY = 1;                  ///< scaling factor on y-axis
    protected float _scaleZ = 1;                  ///< scaling factor on z-axis

    protected Vector2 _position = new Vector2();                ///< position of the node
    protected float _positionZ;               ///< OpenGL real Z position
    protected Vector2 _normalizedPosition;		//lazy
    protected boolean _usingNormalizedPosition;

//    protected float _skewX;                   ///< skew angle on x-axis
//    protected float _skewY;                   ///< skew angle on y-axis
    
    protected float _anchorPointX;			//anchor point normalized
    protected float _anchorPointY;			//anchor point normalized
    
    protected Vector2 _anchorPointInPoints = new Vector2();     ///< anchor point in points
//    protected Vector2 _anchorPoint = new Vector2();             ///< anchor point normalized (NOT in points)

    protected Size _contentSize = new Size();              ///< untransformed size of the node
    protected boolean _contentSizeDirty;         ///< whether or not the contentSize is dirty

    protected Matrix4 _modelViewTransform = new Matrix4();    ///< ModelView transform of the Node.
    
    protected float	_modelRotationZ = 0;
    
    
    // "cache" variables are allowed to be mutable
    protected  Matrix4 _transform = new Matrix4();      ///< transform
    protected  boolean _transformDirty;   ///< transform dirty flag
    protected  Matrix4 _lazy_inverse;        //lazy ///< inverse transform
    protected  boolean _inverseDirty;     ///< inverse transform dirty flag
    protected  Matrix4 _additionalTransform; ///< transform
    protected boolean _useAdditionalTransform;   ///< The flag to check whether the additional transform is dirty
    protected boolean _transformUpdated;         ///< Whether or not the Transform object was updated since the last frame
    
    
    protected int _localZOrder;               ///< Local order (relative to its siblings) used to sort the node
    protected float _globalZOrder;            ///< Global order used to sort the node

    protected  Array<Node> _children = new Array<Node>(0);        ///< array of children nodes
    protected  Node _parent;                  ///< weak reference to parent node

    protected int _tag;                         ///< a tag. Can be any number you assigned just to identify this node
    
    protected String _name;               ///<a string label, an user defined string to identify this node
    protected int _hashOfName;            ///<hash value of _name, used for speed in getChildByName

    protected Object _userData;                ///< A user assingned void pointer, Can be point to any cpp object
    protected Object _userObject;               ///< A user assigned Object

//    protected GLProgramState *_glProgramState; ///< OpenGL Program State

    protected int _orderOfArrival;            ///< used to preserve sequence while sorting children with the same localZOrder
    
    protected boolean _running = false;                  ///< is running

    protected boolean _visible = true;                  ///< is this node visible

    protected boolean _ignoreAnchorPointForPosition; ///< true if the Anchor Vector2 will be (0,0) when you position the Node, false otherwise.
                                          ///< Used by Layer and Scene.

    protected boolean _reorderChildDirty;          ///< children order dirty flag
    protected boolean _isTransitionFinished;       ///< flag to indicate whether the transition was finished
    protected boolean _additionalTransformDirty;
    protected boolean _normalizedPositionDirty;
//    
    protected Director _director = Director.justInstance();
    
//#if CC_ENABLE_SCRIPT_BINDING
//    int _scriptHandler;               ///< script handler for onEnter() & onExit(), used in Javascript binding and Lua binding.
//    int _updateScriptHandler;         ///< script handler for update() callback per frame, which is invoked from lua & javascript.
//    ccScriptType _scriptType;         ///< type of script binding, lua or javascript
//#endif
    
    protected ComponentContainer _componentContainer;        ///< Dictionary of components

//#if CC_USE_PHYSICS
//    PhysicsBody* _physicsBody;        ///< the physicsBody the node have
//    float _physicsScaleStartX;         ///< the scale x value when setPhysicsBody
//    float _physicsScaleStartY;         ///< the scale y value when setPhysicsBody
//#endif
    
    // opacity controls
//    GLubyte		_displayedOpacity;
//    GLubyte     _realOpacity;
//    Color3B	    _displayedColor;
//    Color3B     _realColor;
    boolean		_cascadeColorEnabled;
    boolean        _cascadeOpacityEnabled;

    static int s_globalOrderOfArrival;
    
    protected OnEnterCallback 		_onEnterCallback;
    protected OnExitCallback 		_onExitCallback;
    protected OnUpdateCallback 		_onUpdateCallback;
    protected OnEnterTransitionDidFinishCallback 	_onEnterTransitionDidFinishCallback;
    protected OnExitTransitionDidStartCallback 		_onExitTransitionDidStartCallback;
    
    static final Matrix4 poolMatrix_1 = new Matrix4();
    static final Vector2 poolVector2_1 = new Vector2();


    private int _cameraMask;
    /**
     * get & set camera mask, the node is visible by the camera whose camera flag & node's camera mask is true
     */
    public int getCameraMask() { return _cameraMask; }
    /**
     * Modify the camera mask for current node.
     * If applyChildren is true, then it will modify the camera mask of its children recursively.
     * @param mask A unsigned short bit for mask.
     * @param applyChildren A boolean value to determine whether the mask bit should apply to its children or not.
     */
    public void setCameraMask(int mask, boolean applyChildren) {
    	
    }
    
    public void setCameraMask(int mask) {
    	setCameraMask(mask, true);
    }
    
    
    Comparator<Node> nodeComparisonLess = new Comparator<Node>() {
		@Override
		public int compare(Node n1, Node n2) {
			if((n1.getLocalZOrder() < n2.getLocalZOrder()) ||
					(n1.getLocalZOrder() == n2.getLocalZOrder() && n1.getOrderOfArrival() < n2.getOrderOfArrival())) {
				return -1;
			}
			return 1;
		}
	};
    
//	@Override
//	public int getCameraMask() {
//		return 0;
//	}

//    protected Quaternion _rotationQuat = new Quaternion();

}

// NodeRGBA

/** NodeRGBA is a subclass of Node that implements the RGBAProtocol protocol.
 
 All features from Node are valid, plus the following new features:
 - opacity
 - RGB colors
 
 Opacity/Color propagates into children that conform to the RGBAProtocol if cascadeOpacity/cascadeColor is enabled.
 @since v2.1
 */
//class CC_DLL __NodeRGBA : public Node, public __RGBAProtocol
//{
//public:
//    // overrides
//    public GLubyte getOpacity()  override { return Node::getOpacity(); }
//    public GLubyte getDisplayedOpacity()   override { return Node::getDisplayedOpacity(); }
//    public void setOpacity(GLubyte opacity) override { return Node::setOpacity(opacity); }
//    public void updateDisplayedOpacity(GLubyte parentOpacity) override { return Node::updateDisplayedOpacity(parentOpacity); }
//    public boolean isCascadeOpacityEnabled()   override { return Node::isCascadeOpacityEnabled(); }
//    public void setCascadeOpacityEnabled(boolean cascadeOpacityEnabled) override { return Node::setCascadeOpacityEnabled(cascadeOpacityEnabled); }
//
//    public  Color3B& getColor(void)  override { return Node::getColor(); }
//    public  Color3B& getDisplayedColor()  override { return Node::getDisplayedColor(); }
//    public void setColor( Color3B& color) override { return Node::setColor(color); }
//    public void updateDisplayedColor( Color3B& parentColor) override { return Node::updateDisplayedColor(parentColor); }
//    public boolean isCascadeColorEnabled()  override { return Node::isCascadeColorEnabled(); }
//    public void setCascadeColorEnabled(boolean cascadeColorEnabled) override { return Node::setCascadeColorEnabled(cascadeColorEnabled); }
//
//    public void setOpacityModifyRGB(boolean bValue) override { return Node::setOpacityModifyRGB(bValue); }
//    public boolean isOpacityModifyRGB()  override { return Node::isOpacityModifyRGB(); }
//
//CC_CONSTRUCTOR_ACCESS:
//    __NodeRGBA();
//    public ~__NodeRGBA() {}
//
//private:
//    CC_DISALLOW_COPY_AND_ASSIGN(__NodeRGBA);
//};
//
//// end of base_node group
///// @}
//
//NS_CC_END
//
//#endif // __CCNODE_H__
