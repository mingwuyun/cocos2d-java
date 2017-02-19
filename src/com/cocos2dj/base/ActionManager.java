package com.cocos2dj.base;

import java.util.HashMap;
import java.util.Iterator;

import com.badlogic.gdx.utils.Array;
import com.cocos2dj.macros.CCLog;
import com.cocos2dj.protocol.IAction;
import com.cocos2dj.protocol.INode;
import com.cocos2dj.protocol.IUpdater;
import com.cocos2dj.utils.ObjectLinkedList;

/**
 * ActionManager.java
 * <p>
 * 
 * @author Copyright (c) 2017 xu jun
 */
public class ActionManager implements IUpdater {
	/** Adds an action with a target. 
    If the target is already present, then the action will be added to the existing target.
    If the target is not present, a new instance of this target will be created either paused or not, and the action will be added to the newly created target.
    When the target is paused, the queued actions won't be 'ticked'.
    *
    * @param action    A certain action.
    * @param target    The target which need to be added an action.
    * @param paused    Is the target paused or not.
    */
   public void addAction(IAction action, INode target, boolean paused) {
	   assert action != null : "action can't be nullptr!";
	   assert target != null: "target can't be nullptr!";
	   if(action == null || target == null) {return;}
		
	   struct_hashElement element = null;
		// we should convert it to Ref*, because we save it as Ref*
	   element = _targets.get(target);
	   if(element == null) {
		   element = new struct_hashElement();
		   element.paused = paused;
		   element.target = target;
		   _targets.put(target, element);
		   
		   //TODO elements
		   _elements.add(element);
	   }
	   
	   actionAllocWithHashElement(element);
	   
	   assert !element.actions.contains(action, true) :"action already be added!";
	   
	   //TODO action attach
	   action.setAttached(true);
	   
	   if(_lock) {		//添加到缓存队列
		   _toAddElements.add(action);
		   action.startWithTarget(target);
	   } else {
		   element.actions.add(action);
		   action.startWithTarget(target);
	   }
   }

   /** Removes all actions from all the targets.
    */
   public void removeAllActions() {
	   INode[] nodes = (INode[]) java.lang.reflect.Array.newInstance(INode.class, _targets.size());
	   _targets.keySet().toArray(nodes);
	   for(INode node : nodes) {
		   removeAllActionsFromTarget(node);
	   }
   }
   
   /** Removes all actions from a certain target.
    All the actions that belongs to the target will be removed.
    *
    * @param target    A certain target.
    */
   public void removeAllActionsFromTarget(INode target) {
	   if(target == null) {
		   return;
	   }
	   
	   struct_hashElement element = _targets.get(target);
	   if(element != null) {
		   if(element.actions.contains(element.currentAction, true)
				   && !element.currentActionSalvaged) {
			   element.currentActionSalvaged = true;
		   }
		   
		   for(IAction action : element.actions) {
			 //TODO action detach
			   action.setAttached(false);
		   }
		   
		// removeTo
		   if(_lock) {		//handle remove until next update
			   return;
		   }
		   
		   element.actions.clear();
//		   if(_currentTarget == element) {
//			   _currentTargetSalvaged = true;
//		   } else {
			   deleteHashElement(element);
//		   }
	   }
   }

   /** Removes an action given an action reference.
    *
    * @param action    A certain target.
    */
   public void removeAction(IAction action) {
	   if(action == null) {return;}
	   
	   // removeTo
	   if(_lock) {
		   action.setAttached(false);
//		   _toRemoveElements.add(action);
		   return;
	   }
	   
	   unsafeRemoveAction(action);
//	   struct_hashElement element = null;
//	   element = _targets.get(action.getOriginalTarget());
//	   if(element != null) {
//		   int index = element.actions.indexOf(action, true);
//		   if(index > -1) {
//			   removeActionAtIndex(index, element);
//		   }
//	   }
   }
   
   /**移除action 无论是否正在遍历 */
   final void unsafeRemoveAction(IAction action) {
	   struct_hashElement element = null;
	   element = _targets.get(action.getOriginalTarget());
	   if(element != null) {
		   int index = element.actions.indexOf(action, true);
		   if(index > -1) {
			   removeActionAtIndex(index, element);
		   }
	   }
   }

   /** Removes an action given its tag and the target.
    *
    * @param tag       The action's tag.
    * @param target    A certain target.
    */
   public void removeActionByTag(int tag, INode target) {
	   assert tag != IAction.INVALID_TAG: "Invalid tag value!";
	   assert target != null: "target can't be nullptr!";
	   if (target == null) {return;}
	   
	   struct_hashElement element = _targets.get(target);
	   if(element != null) {
		   Array<IAction> actions = element.actions;
		   for(int i = actions.size - 1; i >= 0; --i) {
			   if(actions.get(i).getTag() == tag 
					   && actions.get(i).getOriginalTarget() == target) {
				   
				  // removeTo
				   if(_lock) {
					   IAction action = actions.get(i);
					   action.setAttached(false);
//					   _toRemoveElements.add(action);
					   return;
				   }
				   
				   removeActionAtIndex(i, element);
				   break;
			   }
		   }
	   }
   }
   
   /** Removes all actions given its tag and the target.
    *
    * @param tag       The actions' tag.
    * @param target    A certain target.
    * @js NA
    */
   public void removeAllActionsByTag(int tag, INode target) {
	   assert tag != IAction.INVALID_TAG: "Invalid tag value!";
	   assert target != null: "target can't be nullptr!";
	   if (target == null) {return;}
	   
	   struct_hashElement element = _targets.get(target);
	   if(element != null) {
		   Array<IAction> actions = element.actions;
		   for(int i = actions.size - 1; i >= 0; --i) {
			   if(actions.get(i).getTag() == tag 
					   && actions.get(i).getOriginalTarget() == target) {
				   
				 // removeTo
				   if(_lock) {
					   IAction action = actions.get(i);
					   action.setAttached(false);
//					   _toRemoveElements.add(action);
					   continue;
				   }
				   
				   removeActionAtIndex(i, element);
			   }
		   }
	   }
   }

   /** Removes all actions matching at least one bit in flags and the target.
    *
    * @param flags     The flag field to match the actions' flags based on bitwise AND.
    * @param target    A certain target.
    * @js NA
    */
   public void removeActionsByFlags(int flags, INode target) {
	   if(flags == 0) {return;}
	   
	   assert target != null : "target can't be null!";
	   
	   if(target == null) {return;}
	   
	   struct_hashElement element = _targets.get(target);
	   
	   if(element != null) {
		   Array<IAction> actions = element.actions;
		   for(int i = actions.size - 1; i >= 0; --i) {
			   if((actions.get(i).getFlags() & flags) != 0
					   && actions.get(i).getOriginalTarget() == target) {
				   
				// removeTo
				   if(_lock) {
					   IAction action = actions.get(i);
					   action.setAttached(false);
//					   _toRemoveElements.add(action);
					   continue;
				   }
				   
				   removeActionAtIndex(i, element);
			   }
		   }
	   }
   }

   /** Gets an action given its tag an a target.
    *
    * @param tag       The action's tag.
    * @param target    A certain target.
    * @return  The Action the with the given tag.
    */
   public IAction getActionByTag(int tag, final INode target) {
	   assert tag != IAction.INVALID_TAG : "Invalid tag value!";
	   struct_hashElement element = _targets.get(target);
	   if (element != null) {
		   Array<IAction> actions = element.actions;
		   if(actions != null) {
			   for(int i = 0; i < actions.size; ++i) {
				   IAction action = actions.get(i);
				   if(action.getTag() == tag) {
					   return action;
				   }
			   }
		   }
	   }
	   return null;
   }

   /** Returns the numbers of actions that are running in a certain target. 
    * Composable actions are counted as 1 action. Example:
    * - If you are running 1 Sequence of 7 actions, it will return 1.
    * - If you are running 7 Sequences of 2 actions, it will return 7.
    *
    * @param target    A certain target.
    * @return  The numbers of actions that are running in a certain target.
    * @js NA
    */
   public int getNumberOfRunningActionsInTarget(final INode target) {
	   struct_hashElement element = _targets.get(target);
	   if(element != null) {
		   return element.actions == null ? 0 : element.actions.size;
	   }
	   return 0;
   }

   /** Pauses the target: all running actions and newly added actions will be paused.
    *
    * @param target    A certain target.
    */
   public void pauseTarget(INode target) {
	   struct_hashElement element = _targets.get(target);
	   if(element != null) {
		   element.paused = true;
	   }
   }

   /** Resumes the target. All queued actions will be resumed.
    *
    * @param target    A certain target.
    */
   public void resumeTarget(INode target) {
	   struct_hashElement element = _targets.get(target);
	   if(element != null) {
		   element.paused = false;
	   }
   }
   
   /** Pauses all running actions, returning a list of targets whose actions were paused.
    *
    * @return  A list of targets whose actions were paused.
    */
   public Array<INode> pauseAllRunningActions(Array<INode> ret) {
	   Iterator<struct_hashElement> it = _elements.iterator();
	   while(it.hasNext()) {
		   struct_hashElement e = it.next();
		   if(!e.paused) {
			   e.paused = true;
			   ret.add(e.target);
		   }
	   }
	   return ret;
   }
   
   /** Resume a set of targets (convenience function to reverse a pauseAllRunningActions call).
    *
    * @param targetsToResume   A set of targets need to be resumed.
    */
   public void resumeTargets(Array<INode> targetsToResume) {
	   for(INode node : targetsToResume) {
		   resumeTarget(node);
	   }
   }
   
   /** Main loop of ActionManager.
    * @param dt    In seconds.
    */
   public boolean update(float dt) {
	   updateToAddArray();
	   
	   _lock = true;
	   
	   //remove elements
	   Iterator<struct_hashElement> it = _elements.iterator();
	   while(it.hasNext()) {
		   _currentTarget = it.next();
		   if(_currentTarget.removeFlag) {
			   it.remove();
		   }
		   
		   if(!_currentTarget.paused) {
			   // 更新时候需要从后向前——可能在过程中执行删除操作
			   Array<IAction> actions = _currentTarget.actions;
//			   actions.t
			   for(int i = actions.size - 1; i >= 0; --i) {
				   IAction a = actions.get(i);
				   
				   //remove when (attach = false)
				   if(!a.isAttached()) {
					   unsafeRemoveAction(a);
//					   actions.removeIndex(i);
					   continue;
				   }
				   
				   a.step(dt * 0.001f);	//ms to s
				   
				   if(a.isDone()) {
					   a.stop();
//					   _currentTarget.currentAction = null;
					   unsafeRemoveAction(a);
				   }
////				   if(_currentTarget.currentActionSalvaged) {
////					   _currentTarget.currentAction = null;
//				   } else 
//					   if(_currentTarget.currentAction.isDone()) {
//					   a.stop();
//					   _currentTarget.currentAction = null;
//					   removeAction(a);
//				   }
				   _currentTarget.currentAction = null;
			   }
		   }
//		   if(_currentTargetSalvaged && _currentTarget.actions.size <= 0) {
//			   deleteHashElement(_currentTarget);
//		   }
	   }
	   
	   
	   _currentTarget = null;
	   _lock = false;
	   
	   return false;
   }
   
   
   
   // declared in ActionManager.m
   final void removeActionAtIndex(int index, struct_hashElement element) {
	   IAction action = element.actions.get(index);
	   
	 //TODO action detach
	   action.setAttached(false);
//	   if(action == element.currentAction && !element.currentActionSalvaged) {
//		   element.currentActionSalvaged = true;
//	   }
	   
	   element.actions.removeIndex(index);
	   
	   element.actionIndex--;
	   
	   if(element.actions.size <= 0) {
		   deleteHashElement(element);
	   }
   }
   
   final void deleteHashElement(struct_hashElement element) {
	   element.actions.clear();
	   _targets.remove(element.target);
	   element.removeFlag = true;
   }
   
   final void actionAllocWithHashElement(struct_hashElement element) {
	   if(element.actions == null) {
		   element.actions = new Array<>(5);
	   }
   }

   
   /*
    * 采用的添加策略：
    * targets记录关系，elements保存对象；
    * targets可以随时修改。update调用的是elements对象，该对象会同步targets修改。
    * 为了保持行为一致，
    * 所有的add操作会检测遍历锁，添加到待添加队列中；（add如果不加锁可能循环添加
    * 或者一些action当帧执行但另一些下一帧才执行）
    * 移除不受此限制，因为array如果采用下标遍历可以随时移除对象。（这个行为可以保证调用效果一致）
    * 
    * 因此，_currentTargetSalvaged 这个值不需要，因为删除的不是同一个东西
    */
   /**更新添加缓存队列 */
   final void updateToAddArray() {
	   for(int i = _toAddElements.size - 1; i >= 0; --i) {
		   IAction action = _toAddElements.get(i);
		   INode target = action.getOriginalTarget();
		   if(target == null) {
			   CCLog.engine("ActionManager", "action's originalTarget can't be null");
			   return;
		   }
		   
		   struct_hashElement element = _targets.get(target);
		   if(element == null) {
			   element = new struct_hashElement();
			   element.paused = !target.isRunning();
			   element.target = target;
			   _targets.put(target, element);
			   //TODO elements
			   _elements.add(element);
		   }
		   actionAllocWithHashElement(element);

//		   System.out.println("element = " + element);
//		   System.out.println(" actions = " + element.actions);
		   
		   element.actions.add(action);
	   }
	   _toAddElements.clear();
   }
   
   /**更新删除缓存队列 */
   final void updateToRemoveArray() {
	   //删除需要判断isAttached —— 可能移除之后同一帧又添加回去，此时不应该删除
	   
   }
   
   
//   protected boolean        _currentTargetSalvaged;
   HashMap<INode, struct_hashElement> 
   							_targets = new HashMap<>();
   struct_hashElement 		_currentTarget;
   ObjectLinkedList<struct_hashElement>
   							_elements = new ObjectLinkedList<>();
   Array<IAction> 			_toAddElements = new Array<>();
//   Array<IAction> 			_toRemoveElements = new Array<>();
   private volatile boolean  _lock;		
   
   
   static class struct_hashElement implements ObjectLinkedList.ILinkedObject<struct_hashElement>{
       Array<IAction>	actions;
       INode            target;
       int              actionIndex;
       IAction    		currentAction;
       boolean          currentActionSalvaged;
       boolean          paused;
       
       boolean 			removeFlag = false;	//移除标志

       struct_hashElement _next, _prev;
       public void _set_next(struct_hashElement next) {_next = next;}
       public void _set_prev(struct_hashElement prev) {_prev = prev;}
       public struct_hashElement get_next() {return _next;}
       public struct_hashElement get_prev() {return _prev;}
   }
   
}
