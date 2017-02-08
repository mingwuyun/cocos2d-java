package com.cocos2dj.module.typefactory;

import com.badlogic.gdx.utils.Array;
import com.cocos2dj.basic.Engine;
import com.cocos2dj.basic.IDisposable;
import com.cocos2dj.macros.CCLog;

/**
 * Pool管理器<p>
 * 
 * 所有创建的 {@link NodePool} 都会保存在这个管理器中<br>
 * 管理器内部采用 Array 管理对象池, 所以可以使用“位置” 以及 “名称” 查询。<p>
 * 
 * 每一个创建的实体对象池 内部也会保存其在EntityPool中的位置， 调用方法
 * C2EntityPool.getID() 可以获取这个位置。<p>
 * 
 * 另外对于Pool对象的删除应该谨慎， 由于C2Pools中保存了所有EntityPool, 
 * 必须在这个管理器中删除池对象，否则垃圾回收器无法将其回收.
 * 
 * @author Copyright (c) 2014. xu jun
 * */
public class NodePools implements IDisposable {
	
	private static NodePools instance;
	private static Array<NodePool<?>> pools = new Array<NodePool<?>>(); 
	
	public static final NodePools getPools() {
		if(instance == null) {
			instance = new NodePools();
		}
		return instance;
	}
	
	
	/**获取指定编号的 对象池 */
//	@SuppressWarnings("rawtypes")
	public static final NodePool<?> getObjectPool(int index) {
		return pools.get(index);
	}
	
	public static final void addObjectPool(NodePool<?> pool) {
		if(instance == null) {  //实例化对象 为了注册dispose方法的调用
			instance = new NodePools();
		}
		pools.add(pool);
		pool.setPoolID(pools.size - 1);
	
		CCLog.engine("NodePools", "add || poolCount: "+ pools.size);
	}
	
	/**删除对象池
	 * @param pool 指定的对象池
	 * @param dispose 是否释放对象 <code>true</code> 调用对象的dispose方法释放对象池
	 * @return 删除成功返回true */
	public static final boolean removeObjectPool(final NodePool<?> pool, boolean dispose) {
		final int index = pool.getPoolID();
		if(index < 0 || index >= pools.size) 
			return false;
		pools.removeIndex(pool.getPoolID());
		pool.setPoolID(-1);
		
		if(dispose)  pool.dispose();
		
		updatePoolsID();
		return true;
	}
	
	/**更新所有 对象池 ID */
	static final void updatePoolsID() {
		for(int i = 0; i < pools.size; ++i) {
			pools.get(i).setPoolID(i);
		}
	}
	
	/**清理所有对象池  该方法会强制回收所有池中的对象 */
	public static final void clearObjectPools() {
		for(int i = 0; i < pools.size; ++i) {
			NodePool<?> p = pools.get(i);
			p.dispose();
			p.setPoolID(-1);
		}
		pools.clear();
	}
	
	/**获取C2Pools中所有的Pool状态 */
	public static String getPoolsState() {
		StringBuilder sb = new StringBuilder(128);
		for(int i = 0; i < pools.size; ++i) {
			sb.append(pools.get(i)); sb.append('\n');
		}
		return sb.toString();
	}
	
	public void dispose() {
		clearObjectPools();
		instance = null;
	}
	
	private NodePools() {
		Engine.registerDisposable(this);
	}
}

