package com.cocos2dj.basic;

/**
 * BaseUpdater.java
 * <br>BaseUpdateType
 * <p>
 * 
 * 调度器的基本执行单位
 * 
 * 通过调用 {@link #attachManager()} 将该动作放入执行器<br>
 * 调用 {@link #setRenderThreadFlag(boolean)} 设置或在构造器设置是否在渲染线程中执行<br>
 * 当一个动作执行时可以通过sendMessage与getMessage方法获取设定的状态
 * 
 * @author Copyright (c) 2016 xu jun
 */
public abstract class BaseUpdater {
	
	private static final int FLAG_KILL     = 0x0001;     
	private static final int FLAG_STOP     = 0x0002;     
	private static final int FLAG_PAUSED   = 0x0004;     
	private static final int FLAG_NOT_INIT = 0x0008;     //不是首次执行标志
	private static final int FLAG_ATTACHED = 0x0010;     //该动作的联系标志
	
	public static enum BaseUpdateType {
		Main,
		
		/**GL线程 场景绘制前调用*/
		RenderBefore,
		
		/**GL线程 场景绘制完毕后调用 */
		RenderAfter,
	}
	
	/////////////////////////
	
	BaseUpdateType type = BaseUpdateType.Main;
	
	protected BaseUpdater next;
	private int ProcessFlag;
	protected int msg;               
	
	
	BaseUpdater _list_next;
	BaseUpdater _list_prev;
	
	private int priority;
	
	//////////////////////////
	
	public BaseUpdater() {}
	
	public BaseUpdater(BaseUpdateType type) {
		setUpdateType(type);
	}
	
	public void setUpdateType(BaseUpdateType type) {
		this.type = type;
	}
	
	public int getPriority() {
		return this.priority;
	}
	
	/**
	 * 优先级越高 越后执行
	 * @param p
	 */
	public void setPriority(int p) {
		
		this.priority = p;
	}
	
	/**向动作发送消息
	 * @param msg 消息  
	 * 该值可以在actionExecute等方法中访问msg变量获得 */
	public void sendMessage(final int msg){
		this.msg = msg;
	}
	
	public int getMessage(){
		return msg;
	}
	
	/**@return <code>true</code> 在mainThread中执行 <code>false</code> 在renderThread中执行 */
	public final boolean isInRenderThread() {
		return type == BaseUpdateType.RenderAfter || type == BaseUpdateType.RenderBefore;
	}
	
	//////////////////////
	/**强制结束动作
	 * 不管该动作是否在管理器中都会标记删除 */
	public void forceKill(){
		ProcessFlag|=BaseUpdater.FLAG_KILL;
	}
	/**删除本动作（如果不在动作管理器中则不会执行）
	 * 如果有后续动作会在下一个处理周期执行下一个动作 */
	public final void kill(){
		if((ProcessFlag&BaseUpdater.FLAG_ATTACHED)==BaseUpdater.FLAG_ATTACHED)
			ProcessFlag|=BaseUpdater.FLAG_KILL;
	}
	public final void clearKill(){
		ProcessFlag&=~BaseUpdater.FLAG_KILL;
	}
	public final boolean isKill() {
		return (ProcessFlag&BaseUpdater.FLAG_KILL)==BaseUpdater.FLAG_KILL;
	}
	/**将动作停止 
	 * 停止的动作会将后续动作一并删除 */
	public final void stop(){
		ProcessFlag|=BaseUpdater.FLAG_STOP;
	}
	/**清除停止动作标志
	 * <b>由系统调用 */
	public final void clearStop(){
		ProcessFlag&=~BaseUpdater.FLAG_STOP;
	}
	public final boolean isStop(){
		return (ProcessFlag&BaseUpdater.FLAG_STOP)==BaseUpdater.FLAG_STOP;
	}
	public final void pause(){
		ProcessFlag|=BaseUpdater.FLAG_PAUSED;
	}
	public final void clearPaused(){
		ProcessFlag&=~BaseUpdater.FLAG_PAUSED;
	}
	public final boolean isPaused(){
		return (ProcessFlag&BaseUpdater.FLAG_PAUSED)==BaseUpdater.FLAG_PAUSED;
	}
	public final void notInitialize(){
		ProcessFlag|=BaseUpdater.FLAG_NOT_INIT;
	}
	public final void clearNotInitialize(){
		ProcessFlag&=~BaseUpdater.FLAG_NOT_INIT;
	}
	public final boolean isNotInitialize(){
		return (ProcessFlag&BaseUpdater.FLAG_NOT_INIT)==BaseUpdater.FLAG_NOT_INIT;
	}
	final void attach(){
		ProcessFlag|=BaseUpdater.FLAG_ATTACHED;
	}
	public final void clearAttach(){
		ProcessFlag&=~BaseUpdater.FLAG_ATTACHED;
	}
	public final boolean isAttached(){
		return (ProcessFlag&BaseUpdater.FLAG_ATTACHED)==BaseUpdater.FLAG_ATTACHED;
	}

	//////////////////////
	/**是否有子动作
	 * @return */
	public final boolean hasNext(){
		return next != null;
	}
	
	/**
	 * 设置下一个动作<br>
	 * 
	 * <b>这个方法也可以用在执行动作更新的过程中, 总之在kill之前调用就有效</b>
	 * @param process 
	 * */
	public final void setNext(BaseUpdater action){
		next = action;
	}
	
	/** 获取子动作
	 * @return */
	public final BaseUpdater getNext(){
		return next;
	}

	///////////////////////
	
	/**
	 * 执行动作<p>
	 * 
	 * init回调的执行与处理放在这个方法中
	 */
	final boolean execute(final float dt) {
//		if(!isNotInitialize()) {
//			notInitialize();
//			init();
//		}
		if(update(dt)) {
			kill();
			return true;
		}
		return false;
	}
	
	/**重置动作的状态 清空ProcessFlag */
	final void reset() {
		this.ProcessFlag = 0;
//		this.next = null;
	}
	
	/**
	 * 放入Schedule  推荐此方法<p>
	 * 
	 * 也可以用 {@link #SSchedule}的<code>add()</code> 
	 * 注意同一方法不可重复添加到{@link #SSchedule}中
	 * 该函数会返回动作是否添加成功
	 * 
	 * @return <code>true 添加成功 false 添加不成功
	 * */
	public final boolean attachSchedule() {
		return BaseScheduler.instance().add(this, false);
	}
	
	/**
	 * 强制放入Schedule  推荐此方法<p>
	 * 
	 * 调用这个方法如果updatable处于移除状态会强制重新添加到schedule
	 * 
	 * */
	public final boolean forceAttachSchedule(BaseUpdateType type) {
		this.type = type;
		return BaseScheduler.instance().add(this, true);
	}
	
	/**
	 * 放入Schedule  推荐此方法<p>
	 * 
	 * 调用这个方法必须等待updatable完全移除出schedule才可以添加成功
	 * 
	 * 该方法可以通过type参数设置 更新类型
	 * */
	public final boolean attachSchedule(BaseUpdateType type) {
		this.type = type;
		return BaseScheduler.instance().add(this, false);
	}
	
	/**将更新提交到渲染线程 （场景渲染前调用）*/
	public final boolean attachScheduleToRenderBefore() {
		type = BaseUpdateType.RenderBefore;
		return BaseScheduler.instance().add(this, false);
	}
	
	/**将更新提交到渲染线程 （场景渲染后调用）*/
	public final boolean attachScheduleToRenderAfter() {
		type = BaseUpdateType.RenderAfter;
		return BaseScheduler.instance().add(this, false);
	}

	/**
	 * 强制移除自身<p>
	 * 
	 * 这个方法与 {@link #removeSelf()} 主要区别于该方法会立刻移除对象
	 * 而不会延迟到遍历<p>
	 */
	public final void forceRemoveSelf() {
		 BaseScheduler.instance().remove(this);
	}
	
	/**
	 * 移除自身<p>
	 * `
	 * 这个方法会等待遍历时自动移除，没有添加到执行器则不执行
	 */
	public final void removeSelf() {
		this.kill();
	}
	
	//实践中，发现init方法用处不大 删除相关内容
//	protected final void init() {
//		this.onInit();
//	}
	
	protected final boolean update(final float dt) {
		return this.onUpdate(dt);
	}
	
	protected final void end() {
		this.onEnd();
	}
	
	
	////////////////////////////
	
	//abstract>>
	/**首次执行调用Action时调用 */
//	protected abstract void onInit();
	
	/**动作执行时调用*/
	protected abstract boolean onUpdate(float dt);
	
	/**动作结束时调用（调用 {@link #kill()} 之后如果lock则直接调用否则下一次处理时调用）*/
	protected abstract void onEnd();
	//abstract<<
}
