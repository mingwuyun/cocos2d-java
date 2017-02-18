package com.cocos2dj.module.base2d.framework;

import com.cocos2dj.module.base2d.framework.common.AABB;
import com.cocos2dj.module.base2d.framework.common.TimeInfo;
import com.cocos2dj.module.base2d.framework.common.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.cocos2dj.macros.CCLog;
import com.cocos2dj.module.base2d.framework.callback.OnContactCallback;
import com.cocos2dj.module.base2d.framework.callback.UpdateListener;
import com.cocos2dj.module.base2d.framework.callback.VelocityLimitListener;
import com.cocos2dj.module.base2d.framework.collision.Shape;
import com.cocos2dj.module.base2d.framework.collision.AABBShape;
import com.cocos2dj.module.base2d.framework.collision.Polygon;
import com.cocos2dj.module.base2d.framework.collision.Circle;
import com.cocos2dj.module.base2d.framework.collision.Contact;
import com.cocos2dj.module.base2d.framework.collision.TileShape;
import com.cocos2dj.module.base2d.framework.collision.ContactAttach;
import com.cocos2dj.module.base2d.framework.collision.ContactFilter;
import com.cocos2dj.module.base2d.framework.collision.ContactCollisionData;

import static com.cocos2dj.module.base2d.framework.collision.CollideAlgorithms.*;

/**Card2DPhysicsObject<p>
 * 
 * card2d提供的简化物理引擎的物理对象,但是Card2D物理对象不涉及物体的旋转求解<br>
 * card2d物理对象的模拟可以看做仅对质心进行计算.
 * card2D物理对象可以创建多个形状组合为一个物体.这些形状共用一个sweepAABB
 * <b>sweepAABB计算的是所有的shape包围体扫过的AABB</b><br>
 * 物理对象的类型必须在创建时指定：<br>
 * 动力物理对象<br>
 * 移动物理对象<br>
 * 静态物理对象<br>
 * 探测物理对象<br>
 * 
 * @author Copyright (c) 2012-2016 xu jun */
public class PhysicsObject {
	
	private static final int FLAG_UNDO    = 0x01;
	private static final int FLAG_REMOVE  = 0x02;	/**object的删除标志*/
	private static final int FLAG_COLLIDE = 0x04;
	private static final int FLAG_ADVANCE = 0x08;	/**用于碰撞检测的一个标志*/
	public static final int FIX_NULL = Integer.MAX_VALUE;
	public static final int NULL_PROXY = -1;
	
	public static UpdateListener nullUpdateListener = 
			new UpdateListener() {public void onUpdatePosition(PhysicsObject o) {}};
	//pool
	static final AABB poolAABB = new AABB();
	static final AABB poolAABB2 = new AABB();
	static final Vector2 poolV1 = new Vector2();
	static final Vector2 poolVector2 = new Vector2(); 
	
	
	/**用户信息*/
	private Object userData;
	PhysicsScene scene;
	
	/**物理对象实现的类型*/
	protected final PhysicsObjectType type;
	protected IPhysicsObject physicsImpl;
	private Shape shapeList;
	private int shapeCount;
	public final AABB sweepAABB;
	
	private int proxyID = NULL_PROXY; 
	private int groupID = -1;		//这个不是-1会将其添加到对应group中
	private int[] targetGroupID;	//这个不是null会添加到groupCheckObjects中
	private int stateFlag;	//状态位标识
	private boolean sensor;
	
	/**碰撞过滤器*/
	private final ContactFilter contactFilter;
	/**接触链表*/
	private ContactAttach contactList;
	/**注意设置其他参数应该在设置睡眠之前以及设置唤醒之后完成*/
	public boolean sleep;
	
	/**迭代位移 在sweep方法中更新*/
	final Vector2 clipPosition = new Vector2();
//	private float clipZ = 0;
	
	/**更新监听器*/
	public UpdateListener 			listener;
	private VelocityLimitListener 	customListener;
	
	public float 					friction = 0f;		
	public float 					staticFriction = 0f;
//	/**与该对象绑定的物理对象如果不进行任何绑定该对象不会实例化 */
//	private LinkedList<PhysicsObject> bindPhysicsObjects;
//	/**依附对象的列表*/
//	private LinkedList<PhysicsObject> attachPhysicsObjects;
	
	
	
	/**默认构造器创建动力物理对象
	 * 设置后对象类型不可改变 创建其他的类型应调用<br>
	 * {@link #C2PhysObject(PhysicsObjectType)}*/
	public PhysicsObject(){
		this(PhysicsObjectType.Dynamic);
	}
	
	/**按指定的类型创建一个物理对象
	 * 这个方法会创建一个空的监听器
	 * @param type 对象类型PhysicsObjectType */
	public PhysicsObject(PhysicsObjectType type){
		this(type, nullUpdateListener);
	}
	
	/**
	 * 仅用于其继承对象的构造器
	 * @param type
	 * @param noUse
	 */
	PhysicsObject(PhysicsObjectType type, boolean noUse) {
		this.type = type;
		contactFilter = new ContactFilter();
		sweepAABB = new AABB();
	}
	
	/**创建指定类型的物理对象
	 * @param type 对象类型PhysicsObjectType
	 * @param listener 监听器*/
	public PhysicsObject(PhysicsObjectType type,UpdateListener listener){
		contactFilter = new ContactFilter();
		this.type = type;
		sweepAABB = new AABB();
		switch(type){
		case Move:
			physicsImpl = new PhysicsObjectMove();
			break;
		case Dynamic:
			physicsImpl = new PhysicsObjectDynamic();
			break;
		case Static:
			physicsImpl = new PhysicsObjectStatic(this);
			break;
		case Detect:
			physicsImpl = new PhysicsObjectDynamic();
			break;
		}
		this.listener = listener;
	}
	
	public final void setPositionUpdateListener(UpdateListener listener) {
		this.listener = listener == null ? nullUpdateListener : listener;
	}
	
	public final boolean isSleep() {return sleep;}
	/**
	 * 设置物体休眠状态 休眠状态的物体会取消所有接触
	 * 不会继续模拟 直到将sleep设置为false
	 * @param sleep
	 */
	public void setSleep(boolean sleep) {this.sleep = sleep;}
	/**
	 * 设置自定义速度限制 
	 * @param listener (nullable)
	 */
	public final void setCustomVelocityLimitListener(VelocityLimitListener listener) {customListener = listener;}
	public boolean isRemoved() {return scene == null;}
	/**
	 * 从对应的scene中删除该对象
	 */
	public final void removeSelf() {
		if(scene != null) {
			scene.remove(this);
		}
	}
	
	/**
	 * 设置滑动摩擦系数 <br>
	 * 0 不受摩擦修正 1 完全摩擦修正
	 * @param friction [0, 1] 
	 */
	public final void setFriction(float friction) {
		if(friction > 1f) friction = 1f; else if(friction < 0) friction = 0f;
		this.friction = friction;
	}
	
	/**
	 * 设置静态摩擦系数 <br>
	 * 0 不会静态修正 1 完全静态修正
	 * @param staticFriction [0, 1]
	 */
	public final void setStaticFriction(float staticFriction) {
		if(staticFriction > 1f) staticFriction = 1f; else if(staticFriction < 0) staticFriction = 0f;
		this.staticFriction = staticFriction;
	}
	
	public final void setGroupID(final int id) {this.groupID = id;}
	public final void setTargetGroupID(final int...ids) {this.targetGroupID = ids;}
	public final void clearGroupDetectData() {
		this.groupID = -1;
		this.targetGroupID = null;
	}
	public final boolean isGroupObject() {
		return this.groupID != -1;
	}
	public final boolean isCollideToGroupObject() {
		return this.targetGroupID != null;
	}
	/**@return 返回用户信息 */
	public Object getUserData(){
		return userData;
	}
	/**@param userData 设置用户信息 */
	public void setUserData(Object userData){
		this.userData = userData;
	}
	/**获取物理对象连接形状（Shape）的数量
	 * @return shapeCount */
	public final int getShapeCount(){
		return this.shapeCount;
	}
	/**返回世界坐标下的包围体（紧凑AABB）
	 * 结果存在aabb中*/
	public final void getWorldAABB(AABB aabb){
		this.shapeList.computeAABB(aabb, getPosition());
	}
	/**获取物理对象类型
	 * @return*/
	public final PhysicsObjectType getPhysicsObjectType(){
		return type;
	}
	/**设置碰撞过滤<br>
	 * 这个方法传入的是filter的参数而非filter引用
	 * @param filter*/
	public void setContactFilter(final ContactFilter filter){
		this.contactFilter.setContactFilter(filter);
	}
	/**获取碰撞过滤<br>
	 * 获取后也可以设置碰撞参数
	 * @return contactFilter*/
	public final ContactFilter getContactFilter(){
		return contactFilter;
	}
	/**获取物体的碰撞等级<br>
	 * contactLevel的最低级为0 <br> 假设物体A与物体B碰撞且物体A的contactLevel是2
	 * 而物体B的contactLevel是3 	 由于2<3     则当A与B均为可碰物体时物体B会推开物体A<br>
	 * 如果同等级的物体则不会发生完全推开的情况
	 * @return contactLevel */
	public final int getContactLevel(){
		return physicsImpl.getCollisionLevel();
	}
	/**设置物体的碰撞等级<br>
	 * 高等级会推开低等级对象<br>
	 * contactLevel的最低级为0 <br> 假设物体A与物体B碰撞且物体A的contactLevel是2
	 * 而物体B的contactLevel是3 	 由于2<3     则当A与B均为可碰物体时物体B会推开物体A<br>
	 * 如果同等级的物体则不会发生完全推开的情况
	 * @return contactLevel */
	public final void setContactLevel(final int contactLevel){
		physicsImpl.setCollisionLevel(contactLevel);
//		if(physicsImpl.getClass() == PhysicsObjectDynamic.class)
//				((PhysicsObjectDynamic)physicsImpl).setCollisionLevel(contactLevel);
	}
	/**获取传感器属性
	 * @return <code>true</code> 物体不进行碰撞修正
	 * <code>false</code> 物体进行碰撞修正*/
	public final boolean isSensor(){
		return sensor;
	}
	/**设置传感器属性
	 * @param sensor  <code>true</code> 物体不进行碰撞修正
	 * <code>false</code> 物体进行碰撞修正*/
	public final void setSensor(boolean sensor){
		this.sensor = sensor;
	}
	
	
	
//	/**绑定物理对象<br>
//	 * <b>注意 绑定过程不会检查是否重复绑定，需要自行维护。
//	 * 两个物体只需绑定一次就可以了 </b>
//	 * @param obj */
//	public void bindPhysicsObject(PhysicsObject obj){
//		if(bindPhysicsObjects==null){
//			bindPhysicsObjects=new LinkedList<PhysicsObject>();
//		}
//		bindPhysicsObjects.add(obj);
//		if(obj.bindPhysicsObjects==null){
//			obj.bindPhysicsObjects=new LinkedList<PhysicsObject>();
//		}
//		obj.bindPhysicsObjects.add(this);
//	}
//	
//	/**解除所有绑定对象 */
//	public void unbindPhysicsObject(){
//		if(bindPhysicsObjects==null)return;
//		
//	}
//	
//	/**接触对指定物体的绑定
//	 * @param obj 指定的物体*/
//	public void unbindPhysicsObject(PhysicsObject obj){
//		if(bindPhysicsObjects==null)return;
//		
//	}
//	
//	/**依附于指定物理对象 <p>
//	 * 依附是指该物体将单方面受被依附物体的影响而不对被依附物体产生影响
//	 * <br>引擎允许重复添加同一物体 需自行维护是否
//	 * <br>相互依附即为bind
//	 * <br>可用于模拟在移动物体上的情况  本引擎没有摩擦力所以只能靠attach来模拟 
//	 * @param obj 被依附物体 */
//	public final void attachPhysicsObject(final PhysicsObject obj){
//		if(obj.attachPhysicsObjects==null){
//			obj.attachPhysicsObjects=new LinkedList<PhysicsObject>();
//		}
//		obj.attachPhysicsObjects.add(this);
//	}
//	
//	/**解除对指定物体的依附 
//	 * <br>依附对象需要自行保留
//	 * @param obj 需要解除依附的被依附物体 
//	 * @return <code>true 解除成功 false 接触失败 */
//	public final boolean unattachPhysicsObject(final PhysicsObject obj){
//		if(obj.attachPhysicsObjects == null) return false;
//		return obj.attachPhysicsObjects.remove(this);
//	}
//	
//	/**解除所有依附于该物体的对象 */
//	public final void unattachAllPhysicsObject(){
//		if(attachPhysicsObjects==null)return;
//		attachPhysicsObjects.clear();
//	}
	/**设置位置 <br><br>
	 * @param positionX
	 * @param positionY */
	public final void setPosition(final float positionX,final float positionY){
		physicsImpl.setPosition(positionX, positionY);
		listener.onUpdatePosition(this);
	}
	
	/**获取physicsObject位置 
	 * @return position*/
	public final Vector2 getPosition(){
		return physicsImpl.getPosition();
	}
	
	/**获取碰撞列表<p>
	 * 可以通过 {@link ContactAttach}的连接个数判断与本对象的接触对象个数
	 * <pre>
	 * ContactAttach ca = contactList;
	 * C2PhysicsObject other_1;
	 * C2PhysicsObject other_2;
	 * 
	 * if(ca.next != null) {
	 *   other_1 = ca.next.other;
	 *   ca = ca.next;
	 *   if(ca.next != null) {
	 *     other_2 = ca.next.other;
	 *     //... 按这个方式递归可以访问所有的接触对象
	 *   }
	 * }
	 * </pre>
	 * @return contactList */
	public final ContactAttach getContactList(){
		return contactList;
	}
	
	/**编号0 Shape 的包围盒 */
	public final float getMaxY() {
		return physicsImpl.getPosition().y + shapeList.aabb.upperBound.y;
	}
	
	public final float getMinY() {
		return physicsImpl.getPosition().y + shapeList.aabb.lowerBound.y;
	}
	
	public final float getMaxX() {
		return physicsImpl.getPosition().x + shapeList.aabb.upperBound.x;
	}
	
	public final float getMinX() {
		return physicsImpl.getPosition().x + shapeList.aabb.lowerBound.x;
	}
	
	
//	public final float 
	/**设置碰撞链表的头结点<br>
	 * <b>注意这个不是插入到链表的方法 仅仅设置节点 <br>
	 * 另外不要调用这个方法！ </b>
	 * @param attach*/
	public final void _setContactList(ContactAttach attach){
		this.contactList = attach;
	}
	
	public final void move(final TimeInfo time) {
		physicsImpl.move(time, poolVector2, this); //generator 位移修正
	}
	
	/**更新AABB以及返回时间步内移动的距离，调用updateListener接口方法
	 * @return displace 实际移动的距离*/
	public Vector2 sweep(final TimeInfo time) {
		
		Shape shape = shapeList;
		
		//记录移动前的位置
		poolV1.set(getPosition());
		shape.computeAABB(sweepAABB, poolV1);
		//update position
		physicsImpl.move(time, poolVector2, this);	//generator 位移修正
		
		shape.computeAABB(poolAABB, getPosition());
		//记录迭代位移
		clipPosition.set(poolVector2);
		clipPosition.scl(time.inv_iteration);
		/*计算AABB的扫掠AABB*/
		sweepAABB.combine(poolAABB);
		
		shape = shape.next;
		while(shape!=null){
			//计算下一个连接形状移动前后的AABB
			shape.computeAABB(poolAABB, poolV1);
			shape.computeAABB(poolAABB2, getPosition());
			
			/*重新计算AABB的扫掠AABB*/
			sweepAABB.combine(poolAABB);
			sweepAABB.combine(poolAABB2);
					
			shape = shape.next;
		}
		
		listener.onUpdatePosition(this);
		
		
		//TODO 这里添加临时的依附物体处理代码
//		if(attachPhysicsObjects != null) {
//			final Iterator<PhysicsObject> it=attachPhysicsObjects.iterator();
//			while(it.hasNext()){
//				it.next().getPosition().add(poolVector2);
//			}
//		}
		
		return poolVector2;
	}
	
	/**推进迭代时间步 用于contact的迭代求解
	 * @param time 时间信息*/
	public void advance(final TimeInfo time) {
		physicsImpl.getPosition().add(clipPosition);
	}
	
	/**求解接触迭代前物体位置重置 */
	public final void initAdvance() {
		physicsImpl.initAdvance();
	}
	
	/** 获取该Object的sweepAABB
	 * @return sweepAABB 代理需要的AABB*/
	public final AABB getSweepAABB(){
		return sweepAABB;
	}
	
	/**获取object的形状包围AABB(非世界坐标)
	 * @return AABB 形状包围AABB*/
	public final AABB getShapeAABB(){
		return shapeList.aabb;
	}
	
	/** 获取Object的形状 代理的存储对象
	 * @return shape链表的头结点*/
	public final Shape getShapeList(){
		return shapeList;
	}
	
	/**设置Object的代理用于  broadPhase
 	 * @param proxy DynamicTreeNode*/
	public final void setProxyID(final int proxyID){
		this.proxyID = proxyID;
	}
	
	/**获取该Object在broadPhase中的代理
	 * @return proxy 动态树节点*/
	public final int getProxy(){
		return this.proxyID;
	}
	
	/**获取迭代位置
	 * @return Vector2 */
	public final Vector2 getIterationPosition(){
		return physicsImpl.getPrevPosition();
	}
	
	public final void _clearFlag() {
		stateFlag = 0;
	}
	
	/**设置推进标志标志*/
	public final void setAdvanceFlag(){
		stateFlag|=PhysicsObject.FLAG_ADVANCE;
	}

	/**清除推进标志*/
	public final void clearAdvanceFlag(){
		stateFlag&=~PhysicsObject.FLAG_ADVANCE;
	}
	
	/**true 已经推进<br>
	 * false 未推进*/
	public final boolean checkAdvanceFlag(){
		return (stateFlag & PhysicsObject.FLAG_ADVANCE) == PhysicsObject.FLAG_ADVANCE;
	}
	
	/**
	 * 设置可撤销标志
	 */
	public final void setUndoFlag(){
		stateFlag|=PhysicsObject.FLAG_UNDO;
	}

	/**
	 * 清除可撤销标志
	 */
	public final void clearUndoFlag(){
		stateFlag&=~PhysicsObject.FLAG_UNDO;
	}
	
	/**
	 * true 可撤销<br>
	 * false 不可撤销
	 */
	public final boolean checkUndoFlag(){
		return (stateFlag&PhysicsObject.FLAG_UNDO)==PhysicsObject.FLAG_UNDO;
	}
	
	public final void setRemoveFlag(){
		stateFlag|=PhysicsObject.FLAG_REMOVE;
	}

	public final void clearRemoveFlag(){
		stateFlag&=~PhysicsObject.FLAG_REMOVE;
	}
	
	/**
	 * true 移除<br>
	 * false 不移除
	 */
	public final boolean checkRemoveFlag(){
		return (stateFlag&PhysicsObject.FLAG_REMOVE)==PhysicsObject.FLAG_REMOVE;
	}
	
	public final void setCollideFlag(){
		stateFlag|=PhysicsObject.FLAG_COLLIDE;
	}

	public final void clearCollideFlag(){
		stateFlag&=~PhysicsObject.FLAG_COLLIDE;
	}
	
	/**
	 * true 已被碰撞<br>
	 * false 未被碰撞
	 */
	public final boolean checkCollideFlag(){
		return (stateFlag&PhysicsObject.FLAG_COLLIDE)==PhysicsObject.FLAG_COLLIDE;
	}

	//TODO 关于形状的操作
	/**添加一个shape到PhysicsObject
	 * @param shape*/
	public final void addShape(final Shape shape){
		shape.setPhysicsObject(this);
		
		if(shapeList == null){
			//第一个形状
			shapeList=shape;
			shape.computeAABB(sweepAABB, getPosition());
			shape.next=null;
		}else{
			shape.next = this.shapeList;
			shape.computeAABB(poolAABB, getPosition());
			sweepAABB.combine(poolAABB);
			this.shapeList=shape;
		}
		
		++shapeCount;
	}

	/**创建 Tile 形状
	 * 
	 * @param rows 行(y)
	 * @param cols 列(x)
	 * @param cellWidth
	 * @param cellHeight */
	public final TileShape createTileShape(int rows, int cols, float cellWidth, float cellHeight) {
		TileShape tileShape = new TileShape();
		tileShape.createTiles(rows, cols, cellWidth, cellHeight);
		this.addShape(tileShape);
		return tileShape;
	}
	
//	public final TileShape getTileShape(int index) {
//		
//	}
	/**获取TileShape 会将头一个shape强制转换为tileShape返回 
	 * 不做判断，类型错误直接异常 */
	public final TileShape getAsTileShape() {
		return (TileShape)shapeList;
	}
	
	/**添加多个shape到PhysicsObject
	 * @param shapes*/
	public final void addShapeList(Shape...shapes){
		final int n=shapes.length;
		
		shapeList=shapes[0];
		
		for(int i=0;i<n-1;++i){
			shapes[i].setPhysicsObject(this);
			shapes[i].next=shapes[i+1];
		}
		
		shapes[n-1].setPhysicsObject(this);
		shapes[n-1].next=null;
		
		shapeCount+=n;
	}
	
	/**删除指定位置的shape
	 * @param shapePosition shape在physicsObject中的位置
	 * 注意shape从1开始计数*/
	public final void removeShape(final int shapePosition){
		//当位置不存在时自动返回
		if(shapePosition>this.shapeCount||shapePosition<=0){
			//TODO Log
			return;
		}
		
		//处理移除头结点的情况
		if(shapePosition==1) {
			this.shapeList = this.shapeList.next;
			--shapeCount;
			return;
		}
		
		//处理移除其他顶点的情况
		Shape s1=this.shapeList;
		Shape s2=null;
		for(int i=1;i<shapePosition;i++){
			s2=s1;
			s1=s1.next;
		}
		s2.next=s1.next;
		--shapeCount;
	}
	
	//TODO 未完成
	/**移除所有的shape <b>未完成</b>
	 * @param removeFirst <code>true</code> 连shapeList一起删除
	 * <code>false</code> shapeList不删除*/
	public void removeAllShapes(boolean removeFirst){
		
	}
	
	
	
	/**
	 * 查询该对象AABB范围内的指定个数的物体
	 * @param aabb
	 * @return
	 */
	public final PhysicsObject[] getNearPhysicsObject(final int count){
		return null;
	}
	
	/**查询该对象指定范围内的指定个数的物体
	 * @param range
	 * @param count
	 * @return */
	public final PhysicsObject[] getNearPhysicsObject(final AABB range,final int count){
		return null;
	}
	
	/**
	 * 查询该对象指定范围内的指定个数的物体
	 * @param range
	 * @param count
	 * @return
	 */
	public final PhysicsObject[] getNearPhysicsObject(final float halfW,final float halfH,final int count){
		return null;
	}
	
	/**查询该物体的静止属性
	 * @return <code>true</code> 不具有移动控制的物体
	 * <code>false</code> 具有移动控制的物体*/
	public final boolean isStaticObject(){
		return this.type==PhysicsObjectType.Static;
	}
	
	/**查询该物体的碰撞属性
	 * @return	<code>true</code> 具有碰撞控制
	 * 	<code>false</code> 没有碰撞控制*/
	public final boolean isCollisionObject(){
		return this.type!=PhysicsObjectType.Move;
	}

	
	
	//TODO 设置形状的方法
	
	/**替换指定位置的形状
	 * @param shape 将要替换的形状
	 * @param position 指定的位置*/
	public final void replaceShape(Shape shape, final int position){
		if(position>shapeCount){
			System.err.println("替换形状越界");
			return;
		}
		Shape s=this.shapeList;
		for(int i=0;i<position;i++){
			s=s.next;
		}
	}
	
	/**为physicsObject创建一个多边形 
	 * points为点集 坐标为本地坐标 <br>注意应该顺时针设置点<br>
	 * <b>这个方法直接将points设置为points而不是复制数据</b>
	 * @param points  Vector2[] */
	public final void createPolygon(Vector2...points){
		Shape shape=new Polygon(this);
		((Polygon)shape).setPoints(points);
		this.addShape(shape);
	}
	
	/**为physicsObject创建一个多边形 
	 * points为点集 坐标为本地坐标   <b>点集的方向为逆时针方向
	 * @param points float[] 长度为偶数（奇数会忽略最后一个数）*/
	public final void createPolygon(float...points){
		Shape shape=new Polygon(this);
		((Polygon)shape).setPoints(points);
		this.addShape(shape);
	}
	
	/**为physicsObject创建多边形 并将此多边形设置为box
	 * 使用坐标为本地坐标  box的范围是(x0,y0)-(x1,y1)
	 * @param x0
	 * @param y0
	 * @param x1
	 * @param y1 */
	public final void createPolygonAsBox(final float x0,final float y0,
			final float x1,final float y1){
		Shape shape=new Polygon(this);
		((Polygon)shape).setAsBox(x0, y0, x1, y1);
		this.addShape(shape);
	}
	
	/**为physicsObject创建多边形 并将此多边形设置为box
	 * 使用坐标为本地坐标   参数为box的半宽与半长
	 * @param halfWidth
	 * @param halfHeight */
	public final void createPolygonAsBox(final float halfWidth,final float halfHeight){
		Shape shape=new Polygon(this);
		((Polygon)shape).setAsBox(halfWidth, halfHeight);
		this.addShape(shape);
	}
	
	/**physicsObject创建AABB形状 
	 * 所用坐标为本地坐标<br>
	 * 创建后添加到shapeList中*/
	public final void createShapeAsAABB(final float x0,final float y0,final float x1,final float y1){
		Shape shape=new AABBShape(this);
		((AABBShape)shape).setAABBShape(x0, y0, x1, y1);
		this.addShape(shape);
	}
	
	/**按照世界坐标创建AABB形状 
	 * 中心坐标为aabb的中点
	 * @param x0 aabb的左下角x坐标（世界坐标）
	 * @param y0 aabb的左下角y坐标（世界坐标）
	 * @param x1 aabb的右上角x坐标（世界坐标）
	 * @param y1 aabb的右上角y坐标（世界坐标）*/
	public final void createShapeAsAABBWorld(final float x0,final float y0,final float x1,final float y1){
//		Shape shape=new AABBShape(this);
//		((AABBShape)shape).setAABBShape(x0, y0, x1, y1);
//		this.addShape(shape);
		this.createShapeAsAABBWorld(x0, y0, x1, y1, (x0+x1)/2, (y0+y1)/2);
	}
	
	/**按世界坐标为物理对象 创建AABB形状
	 * @param x0 aabb的左下角x坐标（世界坐标）
	 * @param y0 aabb的左下角y坐标（世界坐标）
	 * @param x1 aabb的右上角x坐标（世界坐标）
	 * @param y1 aabb的右上角y坐标（世界坐标）
	 * @param centerX aabb的中心x坐标（世界坐标）
	 * @param centerY aabb的中心y坐标（世界坐标）*/
	public final void createShapeAsAABBWorld(final float x0,final float y0,
			final float x1,final float y1,final float centerX, final float centerY){
		Shape shape=new AABBShape(this);
		((AABBShape)shape).setAABBShape(x0-centerX, y0-centerY, x1-centerX, y1-centerY);
		this.addShape(shape);
		this.setPosition(centerX, centerY);
	}
	
	/**physicsObject创建AABB形状
	 * 以半宽与半高创建AABB并指定AABB的中心为position
	 * @param halfWidth
	 * @param halfHeight */
	public final void createShapeAsAABB(final float halfWidth,final float halfHeight){
		Shape shape=new AABBShape(this);
		((AABBShape)shape).setAABBShape(halfWidth,halfHeight);
		this.addShape(shape);
	}
	
	/**为physicsObject创建Circle形状
	 * @param radius 圆的半径 */
	public final void createShapeCircle(final float radius){
		Shape shape=new Circle(this);
		((Circle)shape).setCircleRadius(radius);
		this.addShape(shape);
	}
	
	public void setAccelerate(final float x,final float y) {
		physicsImpl.setAccelerate(x, y);
	}
	public final void setAccelerate(final Vector2 a){
		physicsImpl.setAccelerate(a);
	}
	public final void setAccelerateY(final float y){
		physicsImpl.setAccelerateY(y);
	}
	public final void setAccelerateX(final float x){
		physicsImpl.setAccelerateX(x);
	}
	
	/**按极坐标设置速度 */
	public final void setVelocityRad(final float velo, final float rad) {
		physicsImpl.setVelocity(velo * MathUtils.cos(rad), velo * MathUtils.sin(rad));
	}
	public final void setVelocity(final float x, final float y) {
		physicsImpl.setVelocity(x, y);
	}
	public final void setVelocity(final Vector2 velo){
		physicsImpl.setVelocity(velo);
	}
	public final void setVelocityX(final float x){
		physicsImpl.setVelocityX(x);
	}
	public final void setVelocityY(final float y){
		physicsImpl.setVelocityY(y);
	}
	
	public final Vector2 getVelocity(){
		return physicsImpl.getVelocity();
	}
	
	public final Vector2 getAccelerate(){
		return physicsImpl.getAccelerate();
	}
	
	/**
	 * 撤销本次的移动
	 * 恢复调用最近的一次move()方法前的位置
	 */
	public final void undoMove(){
//		physicsImp.undoMove();
		//检查可否撤销
		/*if(!checkUndoFlag()||moveHandler==null) return;
		
		final Vector2 speed=moveHandler.getVelo().mul(-timeClip);
		
		//撤销位置更新
		aabb.lowerBound.addThis(speed);
		aabb.upperBound.addThis(speed);
		//shape.moveShape(speed);
		
		//撤销速度更新
		moveHandler.undoVelo();
		//清理可撤销标志
		 * */
		clearUndoFlag();
	}
	
	/**更新速度*/
	public final void updateVelocity(final TimeInfo time){
		updateGenerators(time);	//generator 常规更新
		
		this.physicsImpl.updateVelocity(time);
		
		updateVelocityGenerators(time, physicsImpl.getVelocity());	//generator 速度修正
		
		if(customListener != null) {
			customListener.onVelocity(physicsImpl.getVelocity());
		} else {
			Base2D.getDefaultVelocityLimitListener().onVelocity(physicsImpl.getVelocity());
		}
	}
	
	/**按MTD修正移动*/
	public final void modifierPosition(final Vector2 MTD, final ContactCollisionData data){
//		if(bindModifierFlag)return;
//		bindModifierFlag=true;
		
		physicsImpl.modifierPosition(MTD, data);
		
		//TODO MTD修正绑定对象的代码
		/*if(bindPhysicsObjects!=null){
			final Iterator<C2PhysicsObject> it=bindPhysicsObjects.iterator();
			while(it.hasNext()){
				it.next().physicsImpl.modifierPosition(MTD);
			}
		}*/
	}
	
	
	//TODO 新增加的接口 
	/**@return 前一个时间步的位置 */
	public final Vector2 getPrevPosition(){
		return physicsImpl.getPrevPosition();
	}
	
	/**将依附的所有形状逆时针转90度 */
	public final void rotate90(){
		Shape s=shapeList;
		while(s!=null){
			s.rotate90();
			s=s.next;
		}
	}
	
	/**将指定的形状逆时针旋转90度
	 * @param n 形状序号 0为头一个形状 */
	public final void rotate90(final int n){
		Shape s=shapeList;
		for(int i=0; i<n && s!=null; ++i){
			s=s.next;
		}
		if(s!=null);s.rotate90();
	}
	
	/**将依附的所有形状逆时针转180度 */
	public final void rotate180(){
		Shape s=shapeList;
		while(s!=null){
			s.rotate180();
			s=s.next;
		}
	}
	
	/**将指定的形状逆时针旋转180度
	 * @param n 形状序号 0为头一个形状 */
	public final void rotate180(final int n){
		Shape s=shapeList;
		for(int i=0; i<n && s!=null; ++i){
			s=s.next;
		}
		if(s!=null)s.rotate180();
	}
	
	/**将依附的所有形状逆时针转270度 */
	public final void rotate270(){
		Shape s=shapeList;
		while(s!=null){
			s.rotate270();
			s=s.next;
		}
	}
	
	/**将指定的形状逆时针旋转270度
	 * @param n 形状序号 0为头一个形状 */
	public final void rotate270(final int n){
		Shape s=shapeList;
		for(int i=0; i<n && s!=null; ++i){
			s=s.next;
		}
		if(s!=null);s.rotate270();
	}
	
	/**获取依附的形状的旋转角度
	 * @return rad弧度 */
	public final float getRotate(){
		return shapeList.getRotate();
	}
	
	/**返回指定编号的形状的旋转角度
	 * @param n 形状序号 0为头一个形状 */
	public final float getRotate(int n){
		Shape s=shapeList;
		for(int i=0; i<n && s!=null; ++i){
			s=s.next;
		}
		return s.getRotate();
	}
	
	/**将依附的所有形状逆时针旋转到指定的角度
	 * <br><b>这个方法对AABB形状不起作用</b> 
	 * @param angle 旋转的角度 单位是弧度rad */
	public final void setRotate(final float angle){
		Shape s=shapeList;
		while(s!=null){
			s.setRotate(angle);
			s=s.next;
		}
	}
	
	/**将指定的形状逆时针旋转到指定的角度
	 * <br><b>这个方法对AABB形状不起作用</b> 
	 * @param n 形状序号 0为头一个形状
	 * @param angle 旋转的角度 */
	public final void setRotate(final int n, final float angle){
		Shape s=shapeList;
		for(int i=0; i<n && s!=null; ++i){
			s=s.next;
		}
//		if(s!=null);
		s.setRotate(angle);
	}
	
	/**将依附的所有形状逆时针旋转指定的角度
	 * <br><b>这个方法对AABB形状不起作用</b> 
	 * @param angle 旋转的角度 单位是弧度rad */
	public final void rotate(final float angle){
		Shape s=shapeList;
		while(s!=null){
			s.rotate(angle);
			s=s.next;
		}
	}
	
	/**将指定的形状逆时针旋转到指定的角度
	 * <br><b>这个方法对AABB形状不起作用</b> 
	 * @param n 形状序号 0为头一个形状
	 * @param angle 旋转的角度 */
	public final void rotate(final int n, final float angle){
		Shape s=shapeList;
		for(int i=0; i<n && s!=null; ++i){
			s=s.next;
		}
//		if(s!=null);
		s.rotate(angle);
	}
	
	/**重新设置该物体的形状为矩形 <b>注意这个方法的后两个参数是长与宽</b> <br>
	 * 如果需要设置四个点的坐标应该调用 {@link #resetShapeAsRectangle2(float, float, float, float)}
	 * <li>该方法仅可作用于aabb以及polygon物体
	 * <li>使用坐标为该physicsobject的position的相对坐标
	 * @param x 
	 * @param y
	 * @param width
	 * @param height */
	public final void resetShapeAsRectangle(float x, float y, float width,
			float height) {
		shapeList.resetShapeAsRectangle(x, y, width, height);
	}
	
	public final void resetShapeAsRectangle2(float x0, float y0, float x1,
			float y1) {
		shapeList.resetShapeAsRectangle(x0, y0, x1-x0, y1-y0);
	}
	
	/**重新设置该物体的形状为矩形
	 * <li>该方法仅可作用于aabb以及polygon物体
	 * <li>使用坐标为该physicsobject的position的相对坐标
	 * @param n 物体形状序号 
	 * 0是第一个形状 1是第二个形状...依次类推
	 * @param x 
	 * @param y
	 * @param width
	 * @param height */
	public void resetShapeAsRectangle(int n, float x, float y, float width,
			float height) {
		Shape temp=shapeList;
		for(int i=0;i<n;++i){
			temp=shapeList.next;
		}
		temp.resetShapeAsRectangle(x, y, width, height);
	}

	/**重新设置该物体的形状为多边形
	 * <li>该方法仅可作用于polygon物体
	 * <li>使用坐标为该physicsobject的position的相对坐标
	 * @param Vector2 points 逆时针相对坐标 */
	public void resetShapeAsPolygon(Vector2... points) {
		shapeList.resetShapeAsPolygon(points);
	}

	/**重新设置该物体的形状为多边形
	 * <li>该方法仅可作用于polygon物体
	 * <li>使用坐标为该physicsobject的position的相对坐标
	 * @param n 物体形状序号 
	 * 0是第一个形状 1是第二个形状...依次类推
	 * @param Vector2 points 逆时针相对坐标  */
	public void resetShapeAsRectangle(int n, Vector2[] points) {
		Shape temp=shapeList;
		for(int i=0;i<n;++i){
			temp=shapeList.next;
		}
		temp.resetShapeAsPolygon(points);
	}
	
	/**重新设置该物体的形状为圆
	 * <li>该方法仅可作用于circle物体
	 * <li>使用坐标为该physicsobject的position的相对坐标
	 * @param x
	 * @param y
	 * @param radious */
	public void resetShapeAsCircle(float x, float y, float radious) {
		shapeList.resetShapeAsCircle(x, y, radious);
	}
	
	/**重新设置该物体的形状为圆
	 * <li>该方法仅可作用于circle物体
	 * <li>使用坐标为该physicsobject的position的相对坐标
	 * @param n 物体形状序号 
	 * 0是第一个形状 1是第二个形状...依次类推
	 * @param x
	 * @param y
	 * @param radious */
	public void resetShapeAsCircle(final int n, 
			final float x, final float y, final float radious) {
		Shape temp=shapeList;
		for(int i=0;i<n;++i){
			temp=shapeList.next;
		}
		temp.resetShapeAsCircle(x, y, radious);
	}
	
	/**测试点是否在该物理对象的形状中
	 * @param x
	 * @param y
	 * @return <code>-1 测试点不在该物理对象的形状中<br>
	 * 返回大于0的值n（包括0）表示该点在该物理对象的第n个形状中 */
	public int testPointInShape(final float x, final float y){
		int temp = -1;
		Shape s = shapeList;
		while(s != null){
			++temp;
			if(s.checkPoint(x-physicsImpl.getPosition().x, 
					y-physicsImpl.getPosition().y))
			{
				return temp;
			}
			s = s.next;
		}
		return -1;
	}
	
	/**直接测试两个physicsobject的形状是否相交 
	 * @return <code>true 相交     false 不相交 */
	public boolean testShapeOverlap(final PhysicsObject o2){
		//遍历所有的形状
		int shapesID=0;
		Shape s1=getShapeList();
		Shape s2=null;
		
		while(s1!=null){
			s2=o2.getShapeList();
			while(s2!=null){
				//处理形状相交
				shapesID=s1.shapeTypeID*s2.shapeTypeID;
				//仅测试碰撞不修正
				switch(shapesID){
				case 25:
					if(Intersect((Polygon)s1, (Polygon)s2, getPosition(), o2.getPosition())){
						return true;
					}break;
				case 9:
					if(Intersect((AABBShape)s1, (AABBShape)s2, getPosition(), o2.getPosition())){
						return true;
					}break;
				case 15:
					if(s1.shapeTypeID==Shape.ID_POLYGON){
						if(Intersect((Polygon)s1, (AABBShape)s2, getPosition(), o2.getPosition())){
							return true;
						}
					}else{
						if(Intersect((Polygon)s2, (AABBShape)s1 ,o2.getPosition(), getPosition())){
							return true;
						}
					}break;
				case 49:
					if(Intersect((Circle)s1, (Circle)s2, getPosition(), o2.getPosition())){
						return true;
					}break;
				case 21:
					if(s1.shapeTypeID==Shape.ID_CIRCLE){
						if(Intersect((Circle)s1, (AABBShape)s2, getPosition(), o2.getPosition())){
							return true;
						}
					}
					else{
						if(Intersect((Circle)s2, (AABBShape)s1, o2.getPosition(), getPosition())){
							return true;
						}
					}break;
				case 35:
					if(s1.shapeTypeID==Shape.ID_CIRCLE){
						if(Intersect((Circle)s1, (Polygon)s2, getPosition(), o2.getPosition())){
							return true;
						}
					}
					else{
						if(Intersect((Circle)s2, (Polygon)s1, o2.getPosition(), getPosition())){
							return true;
						}
					}break;
				}
				s2=s2.next;
			}
			s1=s1.next;
		}
		return false;
	}
	
	/**将要发生碰撞时调用
	 * @param o1
	 * @param o2
	 * @return false 碰撞继续 true取消碰撞 */
	public boolean cancelContact(PhysicsObject o) {return false;}
	/**当接触创建时调用
	 * @param o1
	 * @param o2
	 * @param MTD 碰撞面的法向量 */
	public void contactCreated(PhysicsObject o, Vector2 MTD, ContactCollisionData data) {}
	/**当接触持续存在时调用
	 * @param o1
	 * @param o2
	 * @param MTD 碰撞面的法向量 */
	public void contactPersisted(PhysicsObject o, Vector2 MTD, ContactCollisionData data) {}
	/**当接触撤销后调用
	 * @param o1
	 * @param o2
	 * @param MTD 碰撞面的法向量 */
	public void contactDestroyed(PhysicsObject o, Vector2 MTD, ContactCollisionData data) {}
	
	
	
	//////////////////////////////////////
	//TODO contact遍历
	/**
	 * 遍历所有有效的接触（已经碰撞）
	 * @param callback 返回true结束遍历
	 * @return 返回false 没有符合条件的contact
	 */
	public boolean forContactList(OnContactCallback callback) {
		ContactAttach ca = contactList;
		boolean ret = false;
		while(ca != null) {
			ret = true;
			Contact c = ca.contact;
			if(c.isContacted()) {
				if(callback.onContact(c, ca.other)) {
					break;
				}
			}
			ca = ca.next;
		}
		return ret;
	}
	
	/**
	 * 遍历所有接触（可能没有碰撞）
	 * @param callback 返回true结束遍历
	 * @return 返回false 表示当前对象没有contact
	 */
	public boolean forAllContactList(OnContactCallback callback) {
		ContactAttach ca = contactList;
		boolean ret = false;
		while(ca != null) {
			ret = true;
			if(callback.onContact(ca.contact, ca.other)) {
				break;
			}
			ca = ca.next;
		}
		return ret;
	}
	
	/**general update */
	public void updateObject() {
		
	}
	
	////////////////////////////////
	//TODO generator
	Array<PhysicsGenerator>			generatorList;
	
	public void clearGenerator() {
		if(generatorList != null) {
			generatorList.clear();
		}
	}
	
	public boolean findGenerator(PhysicsGenerator generator) {
		if(generatorList != null) {
			return generatorList.contains(generator, true);
		}
		return false;
	}
	
	public PhysicsGenerator addGenerator(PhysicsGenerator generator, boolean checkContains) {
		if(generatorList == null) {
			generatorList = new Array<PhysicsGenerator>(2);
		}
		checkGeneratorCount();
		if(checkContains) {
			if(findGenerator(generator)) {
				CCLog.engine("PhysicsObject", "generator already exist! ");
				return generator;
			}
		} 
		generatorList.add(generator);
		return generator;
	}
	
	private void checkGeneratorCount() {
		if(getGeneratorCount() > Base2D.MAX_GENERATOR) {
			CCLog.error("PhysicsObject", "generator too much ! count = " + getGeneratorCount());
			assert false : "generator too much ! count = " + getGeneratorCount();
		}
	}
	
	private int getGeneratorCount() {
		return generatorList == null ? 0 : generatorList.size;
	}
	
	final void updateGenerators(final TimeInfo time) {
		for(int i = getGeneratorCount() - 1; i >= 0; --i) {
			PhysicsGenerator curr = generatorList.get(i);
			if(curr.endFlag) {
				generatorList.removeIndex(i);
				curr.endFlag = false;
			} else if(curr.onUpdate(this, time)) {
				generatorList.removeIndex(i);
			}
		}
	}
	
	final void updateVelocityGenerators(final TimeInfo time, final Vector2 targetVelocity) {
		for(int i = getGeneratorCount() - 1; i >= 0; --i) {
			PhysicsGenerator curr = generatorList.get(i);
			if(curr.endFlag) {
				generatorList.removeIndex(i);
				curr.endFlag = false;
			} else {
				curr.onUpdateVelocity(this, time, targetVelocity);
			}
		}
	}
	
	final void updatePositionGenerators(final TimeInfo time, final Vector2 posDelta) {
		for(int i = getGeneratorCount() - 1; i >= 0; --i) {
			PhysicsGenerator curr = generatorList.get(i);
			if(curr.endFlag) {
				generatorList.removeIndex(i);
				curr.endFlag = false;
			} else {
				curr.onUpdatePosition(this, time, posDelta);
			}
		}
	}
}