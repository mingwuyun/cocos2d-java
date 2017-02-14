package com.cocos2dj.s2d;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;
import com.cocos2dj.base.Director.MATRIX_STACK_TYPE;
import com.cocos2dj.macros.CCLog;
import com.cocos2dj.renderer.Renderer;

/**
 * ProtectedNode.java
 * <p>
 * 
 *@brief A inner node type mainly used for UI module.
 * It is useful for composing complex node type and it's children are protected.
 */
public class ProtectedNode extends Node {
    /**
     * Creates a ProtectedNode with no argument.
     *@return A instance of ProtectedNode.
     */
    public static ProtectedNode create() {
    	ProtectedNode ret = new ProtectedNode();
    	return ret;
    }
    
    protected void release() {
    	super.release();
    	removeAllProtectedChildren();
    }
    
    /// @{
    /// @name Children and Parent
    /**
     * Adds a child to the container with z-order as 0.
     *
     * If the child is added to a 'running' node, then 'onEnter' and 'onEnterTransitionDidFinish' will be called immediately.
     *
     * @param child A child node
     */
    public void addProtectedChild(Node child) {
    	addProtectedChild(child, child.getZOrder());
    }
    
    /**
     * Adds a child to the container with a local z-order.
     *
     * If the child is added to a 'running' node, then 'onEnter' and 'onEnterTransitionDidFinish' will be called immediately.
     *
     * @param child     A child node
     * @param localZOrder    Z order for drawing priority. Please refer to `setLocalZOrder(int)`
     */
    public void addProtectedChild(Node child, int localZOrder) {
    	addProtectedChild(child, localZOrder, child.getTag());
    }
    /**
     * Adds a child to the container with z order and tag.
     *
     * If the child is added to a 'running' node, then 'onEnter' and 'onEnterTransitionDidFinish' will be called immediately.
     *
     * @param child     A child node
     * @param localZOrder    Z order for drawing priority. Please refer to `setLocalZOrder(int)`
     * @param tag       An integer to identify the node easily. Please refer to `setTag(int)`
     */
    public void addProtectedChild(Node child, int zOrder, int tag) {
    	assert  child != null: "Argument must be non-nil";
        assert  child.getParent() == null: "child already added. It can't be added again";
        
        this.insertProtectedChild(child, zOrder);
        
        child.setTag(tag);
        
        child.setParent(this);
        child.setOrderOfArrival(s_globalOrderOfArrival++);
        
        if( _running ) {
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
    /**
     * Gets a child from the container with its tag.
     *
     * @param tag   An identifier to find the child node.
     *
     * @return a Node object whose tag equals to the input parameter.
     */
    public Node  getProtectedChildByTag(int tag) {
    	assert tag != INVALID_TAG: "Invalid tag";
    	for(int i = 0; i < _protectedChildren.size; ++i) {
    		Node temp = _protectedChildren.get(i);
    		if(temp.getTag() == tag) {
    			return temp;
    		}
    	}
    	return null;
    }
    
    ////// REMOVES //////
    
    /**
     * Removes a child from the container. It will also cleanup all running actions depending on the cleanup parameter.
     *
     * @param child     The child node which will be removed.
     * @param cleanup   true if all running actions and callbacks on the child node will be cleanup, false otherwise.
     */
    public void removeProtectedChild(ProtectedNode child, boolean cleanup) {
    	// explicit nil handling
        if (_protectedChildren.size <= 0) {
            return;
        }
        int index = _protectedChildren.indexOf(child, true);
        if( index != -1) {
            // IMPORTANT:
            //  -1st do onExit
            //  -2nd cleanup
            if (_running) {
                child.onExitTransitionDidStart();
                child.onExit();
            }
            
            // If you don't do cleanup, the child's actions will not get removed and the
            // its scheduledSelectors_ dict will not get released!
            if (cleanup) {
                child.cleanup();
            }
            
            // set parent nil at the end
            child.setParent(null);
            _protectedChildren.removeIndex(index);
        }
    }
    public void removeProtectedChild(ProtectedNode child) {
    	removeProtectedChild(child, true);
    }
    
    /**
     * Removes a child from the container by tag value. It will also cleanup all running actions depending on the cleanup parameter.
     *
     * @param tag       An integer number that identifies a child node.
     * @param cleanup   true if all running actions and callbacks on the child node will be cleanup, false otherwise.
     */
    public void removeProtectedChildByTag(int tag, boolean cleanup) {
    	assert tag != INVALID_TAG: "Invalid tag";
        
    	for(int i = 0; i < _protectedChildren.size; ++i) {
    		Node temp = _protectedChildren.get(i);
    		if(temp.getTag() == tag) {
    			_protectedChildren.removeIndex(i);
    			if(cleanup) {
    				temp.cleanup();
    			}
    			return;
    		}
    	}
    	CCLog.debug("ProtectedNode", "cocos2d: removeChildByTag(tag = " + tag + "): child not found!");
    }
    
    public void removeProtectedChildByTag(int tag) {
    	removeProtectedChildByTag(tag, true);
    }
    /**
     * Removes all children from the container with a cleanup.
     *
     * @see `removeAllChildrenWithCleanup(boolean)`.
     */
    public void removeAllProtectedChildren() {
    	removeAllChildrenWithCleanup(true);
    }
    /**
     * Removes all children from the container, and do a cleanup to all running actions depending on the cleanup parameter.
     *
     * @param cleanup   true if all running actions on all children nodes should be cleanup, false otherwise.
     * @js removeAllChildren
     * @lua removeAllChildren
     */
    public void removeAllProtectedChildrenWithCleanup(boolean cleanup) {
        // not using detachChild improves speed here
        for (int i = 0; i < _protectedChildren.size; ++i) {
        	Node child = _protectedChildren.get(i);
            // IMPORTANT:
            //  -1st do onExit
            //  -2nd cleanup
            if(_running) {
                child.onExitTransitionDidStart();
                child.onExit();
            }
            
            if (cleanup) {
                child.cleanup();
            }
            // set parent nil at the end
            child.setParent(null);
        }
        _protectedChildren.clear();
    }

    /**
     * Reorders a child according to a new z value.
     *
     * @param child     An already added child node. It MUST be already added.
     * @param localZOrder Z order for drawing priority. Please refer to setLocalZOrder(int)
     */
    public void reorderProtectedChild(Node  child, int localZOrder) {
    	assert child != null: "Child must be non-nil";
        _reorderProtectedChildDirty = true;
        child.setOrderOfArrival(s_globalOrderOfArrival++);
        child.setLocalZOrder(localZOrder);
    }
    
    /**
     * Sorts the children array once before drawing, instead of every time when a child is added or reordered.
     * This approach can improves the performance massively.
     * @note Don't call this manually unless a child added needs to be removed in the same frame
     */
    public void sortAllProtectedChildren() {
    	if( _reorderProtectedChildDirty ) {
    		_protectedChildren.sort(nodeComparisonLess);
            _reorderProtectedChildDirty = false;
        }
    }
    /// @} end of Children and Parent
    
    /**
     * @js NA
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
        
        int i = 0;      // used by _children
        int j = 0;      // used by _protectedChildren
        
        sortAllChildren();
        sortAllProtectedChildren();
        
        boolean visibleByCamera = isVisitableByVisitingCamera();
        //修改：如果父节点的camera可见检测失败，直接返回
        //避免大量的重复遍历
        if(!visibleByCamera) {
        	_director.popMatrix(MATRIX_STACK_TYPE.MATRIX_STACK_MODELVIEW);
        	return;
        }
        
        //
        // draw children and protectedChildren zOrder < 0
        //
        for( ; i < _children.size; i++ ) {
            Node node = _children.get(i);
            if (node.getLocalZOrder() < 0 ) {
                node.visit(renderer, _modelViewTransform, flags);
            } else {
                break;
            }
        }
        
        for( ; j < _protectedChildren.size; j++ ) {
            Node node = _protectedChildren.get(j);
            
            if (node.getLocalZOrder() < 0 ) {
                node.visit(renderer, _modelViewTransform, flags);
            } else {
                break;
            }
        }
        
        //
        // draw children and protectedChildren zOrder >= 0
        //
        for(int it = j; it < _protectedChildren.size; ++it) {
        	_protectedChildren.get(it).visit(renderer, _modelViewTransform, flags);
        }
        for(int it = i; it < _protectedChildren.size; ++it) {
        	_children.get(it).visit(renderer, _modelViewTransform, flags);
        }
        // FIX ME: Why need to set _orderOfArrival to 0??
        // Please refer to https://github.com/cocos2d/cocos2d-x/pull/6920
        // setOrderOfArrival(0);
        _director.popMatrix(MATRIX_STACK_TYPE.MATRIX_STACK_MODELVIEW);
    }
    
    public void cleanup() {
    	super.cleanup();
    	for(int i = 0; i < _protectedChildren.size; ++i) {
    		_protectedChildren.get(i).cleanup();
    	}
    }
    
    public void onEnter() {
    	super.onEnter();
    	for(int i = 0; i < _protectedChildren.size; ++i) {
    		_protectedChildren.get(i).onEnter();
    	}
    }
    
    /** Event callback that is invoked when the Node enters in the 'stage'.
     * If the Node enters the 'stage' with a transition, this event is called when the transition finishes.
     * If you override onEnterTransitionDidFinish, you shall call its parent's one, e.g. Node.onEnterTransitionDidFinish()
     * @js NA
     * @lua NA
     */
    public void onEnterTransitionDidFinish() {
    	super.onEnterTransitionDidFinish();
    	for(int i = 0; i < _protectedChildren.size; ++i) {
    		_protectedChildren.get(i).onEnterTransitionDidFinish();
    	}
    }
    
    /**
     * Event callback that is invoked every time the Node leaves the 'stage'.
     * If the Node leaves the 'stage' with a transition, this event is called when the transition finishes.
     * During onExit you can't access a sibling node.
     * If you override onExit, you shall call its parent's one, e.g., Node.onExit().
     * @js NA
     * @lua NA
     */
    public void onExit() {
    	super.onExit();
    	for(int i = 0; i < _protectedChildren.size; ++i) {
    		_protectedChildren.get(i).onExit();
    	}
    }
    
    /**
     * Event callback that is called every time the Node leaves the 'stage'.
     * If the Node leaves the 'stage' with a transition, this callback is called when the transition starts.
     * @js NA
     * @lua NA
     */
    public void onExitTransitionDidStart() {
    	super.onExitTransitionDidStart();
    	for(int i = 0; i < _protectedChildren.size; ++i) {
    		_protectedChildren.get(i).onExitTransitionDidStart();
    	}
    }

    public void updateDisplayedOpacity(float parentOpacity) {
    	super.updateDisplayedOpacity(parentOpacity);
    	
		for(int i = 0; i < _protectedChildren.size; ++i) {
			_protectedChildren.get(i).updateDisplayedOpacity(_displayColor.a);
		}
    }

    protected void updateDisplayedColor(float r, float g, float b) {
    	super.updateDisplayedColor(r, g, b);
        for(int i = 0; i < _protectedChildren.size; ++i) {
			_protectedChildren.get(i).updateDisplayedColor(_displayColor.r, 
					_displayColor.g, _displayColor.b);
		}
    }

    protected void disableCascadeColor() {
    	super.disableCascadeColor();
    	for(int i = 0; i < _protectedChildren.size; ++i) {
			_protectedChildren.get(i).updateDisplayedColor(1f, 1f, 1f);
		}
    }

    protected void disableCascadeOpacity() {
    	super.disableCascadeOpacity();
    	for(int i = 0; i < _protectedChildren.size; ++i) {
			_protectedChildren.get(i).updateDisplayedOpacity(1f);
		}
    }

    public void setCameraMask(int mask, boolean applyChildren) {
    	super.setCameraMask(mask, applyChildren);
        if (applyChildren) {
        	for(int i = 0; i < _protectedChildren.size; ++i) {
    			_protectedChildren.get(i).setCameraMask(mask, true);
    		}
        }
    }
    
    public ProtectedNode() {
    	
    }
    
    
    /// helper that reorder a child
    protected void insertProtectedChild(Node child, int z) {
    	_reorderProtectedChildDirty = true;
        _protectedChildren.add(child);
        child.setLocalZOrder(z);
    }
    
    protected Array<Node> _protectedChildren = new Array<>(0);        ///< array of children nodes
    protected boolean _reorderProtectedChildDirty;
}
