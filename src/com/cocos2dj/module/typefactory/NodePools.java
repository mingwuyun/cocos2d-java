package com.cocos2dj.module.typefactory;

import com.badlogic.gdx.utils.Array;
import com.cocos2dj.basic.IDisposable;

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
 * @author xu jun
 * Copyright (c) 2014. All rights reserved.
 * */
public class NodePools implements IDisposable {
	
//	public static class EntityPoolDef {
//		Class<? extends C2Entity> clazz = null;
//		int poolType = 1;
//		int argInitSize = 1;
//		int argAddCount = 1;
//		Class<?>[] argParam = null;
//		Object[] argArgs = null;
//		
//		final void clear() {
//			clazz = null;
//			poolType = 1;
//			argInitSize = 1;
//			argAddCount = 1;
//			argParam = null;
//			argArgs = null;
//		}
//		
//		final boolean validate() {
//			if(clazz == null || argInitSize == 0 || argAddCount == 0 )
//				return false;
//			return true;
//		}
//	}
	
	private static SPools instance;
//	private static EntityPoolDef def = new EntityPoolDef();
	private static Array<SObjectPool<?>> pools = new Array<SObjectPool<?>>(); 
	
	public static final SPools getPools() {
		if(instance == null) {
			instance = new SPools();
		}
		return instance;
	}
	
	
	
//	public static EntityPoolDef popEntityPoolDef() {
//		def.clear();
//		return def;
//	}
	
	/**调用 {@link #popEntityPoolDef()} 获取 {@link EntityPoolDef} 对象
	 * 使用此方法可以重复使用之前的def设定创建对象池 */
	@SuppressWarnings("rawtypes")
//	public static C2EntityPool pushEntityPoolDef() {
//		return createEntityPool(def);
//	}
	
//	@SuppressWarnings({ "rawtypes", "unchecked" })
//	public static final C2EntityPool createEntityPool(EntityPoolDef def) {
//		C2EntityPool pool = null;
//		C2Scene scene = C2SceneManager.getCurrentScene();
//		if(def.validate()) {
//			pool = new C2EntityPool(scene, def.poolType, def.clazz, 
//					def.argInitSize, def.argAddCount, 
//					def.argParam, def.argArgs);
//		}
//		else C2Log.d("C2Pools", "定义文件验证失败 ");
//		return pool;
//	}
//	
	/**获取指定编号的 对象池 */
//	@SuppressWarnings("rawtypes")
	public static final SObjectPool getObjectPool(int index) {
		return pools.get(index);
	}
	
	/**为所有EntityPool中的对象切换场景<p>
	 * 
	 * 调用这个方法可以在场景的切换中不必重新建立池对象.
	 * 目前这个方法有待验证. */
	public static final void changeScene(final SScene scene) {
//		for(int i = 0, j = 0; i < pools.size; ++i) {
//			C2Entity[] all = pools.get(i).getAll();
//			for(j = 0; j < all.length; ++j) {
//				C2Entity e = all[j];
////				e.remove(); //不用移除 因为旧scene会清空数据
//				//这里不再调用onInitialize()
//				e.addToScene(scene); //重新添加
//			}
//		}
	}
	
	public static final void addObjectPool(SObjectPool<?> pool) {
		if(instance == null) {  //实例化对象 为了注册dispose方法的调用
			instance = new SPools();
		}
		pools.add(pool);
		pool.setPoolID(pools.size - 1);
	
		SLog.debug("C2Pools", "add || poolCount: "+ pools.size);
	}
	
	/**删除对象池
	 * @param pool 指定的对象池
	 * @param dispose 是否释放对象 <code>true</code> 调用对象的dispose方法释放对象池
	 * @return 删除成功返回true */
	public static final boolean removeObjectPool(final SObjectPool<?> pool, boolean dispose) {
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
			SObjectPool<?> p = pools.get(i);
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
		SLog.debug("C2Pools", "dispose");
		clearObjectPools();
		instance = null;
	}
	
	private SPools() {
		StormEngine.registerDisposable(this);
	}
}

