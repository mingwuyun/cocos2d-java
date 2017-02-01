package com.cocos2dj.module.gdxui;

import com.badlogic.gdx.Input.Keys;

import java.util.LinkedList;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.cocos2dj.basic.BaseInput;
import com.cocos2dj.basic.BaseUpdater;
import com.cocos2dj.basic.Engine;
import com.cocos2dj.basic.IDisposable;
import com.cocos2dj.macros.CCLog;

/**
 * 控制台 <p>
 * 
 * 调用scene.addConsole(console);即可添加控制台对象
 * 控制台只能添加一个
 * 
 * 场景中按下～按钮呼叫控制台，此时可以键盘输入指令，回车确定指令；
 * 指令通过监听 {@link #addConsoleHandle(ConsoleHandle)} 执行处理
 * 
 * 再次按下～之后控制台会被隐藏
 * 
 * 发行版本中，可以替换添加consoleListener来内容来屏蔽控制台
 * 
 * @author xu jun
 * Copyright (c) 2016. All rights reserved.
 */
public class GdxUIConsole extends GdxUIStage {
	
	//static>>
	private static GlobalConsoleHandles globalHandles = new GlobalConsoleHandles();
	/**
	 * 添加一个控制台处理器
	 * @param handleKey
	 * @param handle
	 */
	public static final void addGlobalConsoleHandle(ConsoleHandle handle) {
		for(int i = 0; i < globalHandles.handles.size; ++i) {
			ConsoleHandle curr = globalHandles.handles.get(i);
			if(curr.consoleHandleKey.equals(handle.consoleHandleKey)) {
				//相同名称的handle会被替换
				CCLog.engine("Global SUIConsole", "handle reset : " + curr.consoleHandleKey);
				//替换
				globalHandles.handles.set(i, handle);
				return;
			}
		}
		//添加
		globalHandles.handles.add(handle);
	}
	
	public static final void clearGlobalConsoleHandle() {
		globalHandles.handles.clear();
	}
	
	/**获取当前的所有handle信息 */
	public static String getGlobalHandlesInfo() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < globalHandles.handles.size; ++i) {
			sb.append("Global").append('[').append(i).append(']').append(globalHandles.handles.get(i).consoleHandleKey).append(',').append(' ');
		}
		String ret = sb.toString();
		sb = null;
		return ret;
	}
	
	public static final void removeGlobalConsoleHandle(String handleKey) {
		for(int i = 0; i < globalHandles.handles.size; ++i) {
			ConsoleHandle curr = globalHandles.handles.get(i);
			if(curr.consoleHandleKey.equals(handleKey)) {
				globalHandles.handles.removeIndex(i);
				return;
			}
		}
		CCLog.engine("Global SUIConsole", "remove failed : " + handleKey);
	}
	
	public static final void removeGlobalConsoleHandle(ConsoleHandle handle) {
		if(globalHandles.handles.removeValue(handle, true)) {
			
		} else {
			CCLog.engine("Global SUIConsole", "remove failed : " + handle.consoleHandleKey);
		}
	}
	//static<<
	
	
	//class>>
	/**全局global命令集合 */
	public static class GlobalConsoleHandles implements IDisposable {
		
		Array<ConsoleHandle>	handles = new Array<ConsoleHandle>();
		
		GlobalConsoleHandles() {
			Engine.registerDisposable(this);
		}
		
		/**在全局命令中查找执行 */
		public String updateGlobalHandles(String cmd, String[] params) {
			String ret = null;
			for(int i = 0, n = handles.size; i < n; ++i) {
				ret = handles.get(i)._handle(cmd, params);
				if(ret != null) {		//已经被处理
					break;
				}
			}
			return ret;
		}
		
		@Override
		public void dispose() {
			handles.clear();
			GdxUIConsole.globalHandles = null;
		}
		
	}
	
	/**控制台命令监听 */
	public static abstract class ConsoleHandle {
		/**强制附带一个handleKey参数 方便删除 */
		public ConsoleHandle(String handleKey) {
			this.consoleHandleKey = handleKey;
		}
		
		public final String consoleHandleKey;
		
		public static final String SUCCESS = "SUICONSOLE_HANDLE_SUCCESS";
		
		private String[] 	args;
		
		/*
		 * 使用以下方法将string转换为对应的方法，防止参数错误造成的崩溃
		 */
		public final int getArgInt(int index) {
			return Integer.valueOf(args[index]);
		}
		
		public final float getArgFloat(int index) {
			return Float.valueOf(args[index]);
		}
		
		public final boolean getArgBool(int index) {
			if("true".equals(args[index]) || "t".equals(args[index])) {
				return true;
			}
			if("false".equals(args[index]) || "f".equals(args[index])) {
				return false;
			}
			throw new NumberFormatException();
		}
		
		/**返回参数个数 */
		public final int getArgsCount() {
			return args == null ? 0 : args.length;
		}
		
		public final String getArgString(int index) {
			return args[index];
		}
		
		/**
		 * 输入处理命令， 返回处理结果<p>
		 * 参数放弃直接获取，改为调用getArg系列方法<p>
		 * 
		 * 返回null表示处理失败 返回 {@link #SUCCESS} 表示处理成功但无输出值
		 * 返回其他String表示处理的结果提示
		 * @param cmd
		 * @return
		 */
		public abstract String handle(String cmd);
		
		/**
		 * 处理控制台指令 —— 包装args防止笔误造成程序崩溃
		 * @param cmd
		 * @param args
		 * @return
		 */
		final String _handle(String cmd, String...args) {
			this.args = args;
			String ret = null;
			try {
				ret = handle(cmd);		//需要容错处理，不能因为控制台打错就奔溃
			}
			catch(NullPointerException e) {
				ret = cmd + ">>> args is null";
			}
			catch(ArrayIndexOutOfBoundsException e) {
				ret = cmd + ">>> array index outof bounds";
			}
			catch(NumberFormatException e) {
				ret = cmd + ">>> argType wrong";
			}
			return ret;
		}
	}
	//class<<
	
	
	//fields>>
	Array<ConsoleHandle>	handles = new Array<ConsoleHandle>();
	LinkedList<String>	outputQueue = new LinkedList<>();	//命令输出队列
	int					maxTmp = 8;		//最大缓存8个命令输出
	
	private TextField 	input;
	private Label		label;
	private BaseUpdater showTask = new BaseUpdater() {
		@Override
		protected boolean onUpdate(float dt) {
			Stage stage = input.getStage();
			if(stage != null) {
				stage.setKeyboardFocus(input);
			}
			flushOutput();
			return true;
		}
		@Override
		protected void onEnd() {}
	};
	
	private InputAdapter listenPopPush = new InputAdapter() {
		@Override
		public boolean keyDown(int keycode) {
			if(keycode == Keys.GRAVE) {
				if(isHide()) {
					show();		//隐藏不在这里添加
				}
			}
			return false;
		}

		@Override
		public boolean keyUp(int keycode) {
			return false;
		}
	};
	//fields<<
	
	
	//func>>
	final void addOutput(String ret) {
		String[] temp = ret.split("\n");		
		int lineCount = temp.length;
		
		final int removeCount = outputQueue.size() + lineCount - maxTmp;
		
		for(int i = 0; i < removeCount; ++i) {
			outputQueue.removeFirst();
		}
		for(int i = 0; i < lineCount; ++i) {
			outputQueue.addLast(temp[i]);
		}
	}
	
	final void clearOutput() {
		outputQueue.clear();
	}
	
	/**根据 控制台输入 刷新输出*/
	final void flushOutput() {
		StringBuilder sb = new StringBuilder();
		for(String s : outputQueue) {
			sb.append(s).append('\n');
		}
		
		label.setText(sb.toString());
		label.setPosition(20, getUIHeight() - label.getPrefHeight()/2f - 10);
		sb = null;
	}
	
	/**处理输入的纯字符串逻辑 
	 * @return 是否保留字符 false删除字符串 true保留cmd输入 */
	final boolean handleCmdString(String cmdString) {
		boolean keepFlag = false;
		
		String retCmd = cmdString.trim();
		
		//clear 指令直接删除对象
		if("clear".equals(retCmd)) {
			clearOutput();
			flushOutput();
			return keepFlag;
		}
		
		//查找consoleHandle来处理命令
		String[] t = retCmd.split(":");
		String[] params = null;
		if(t.length > 1) {
			params = t[1].split(",");
		}
		
		String cmd = t[0];
		if(t[0].startsWith("!")) {
			cmd = t[0].substring(1);
			keepFlag = true;
		}
		
//		System.out.println("cmd = " + cmd);
//		if(params != null)
//		for(String s : params) {
//			System.out.println("p = " + s);
//		}
		
		String ret = null;
		for(int i = 0, n = handles.size; i < n; ++i) {
			ret = handles.get(i)._handle(cmd, params);
			if(ret != null) {		//已经被处理
				break;
			}
		}
		
		//局部的处理 优先于 全局处理函数
		if(ret == null) {
			ret = globalHandles.updateGlobalHandles(cmd, params);
		}
		
		if(ret == null) {
			ret = retCmd + ">>> cmd not found";
		}
		
		if(!ConsoleHandle.SUCCESS.equals(ret)) {		//SUCCESS表示无返回值
			addOutput(ret);
		}
		
		flushOutput();
		return keepFlag;
	}
	//func<<
	
	
	//methods>>
	/**
	 * 添加一个控制台处理器
	 * @param handleKey
	 * @param handle
	 */
	public final void addConsoleHandle(ConsoleHandle handle) {
		for(int i = 0; i < handles.size; ++i) {
			ConsoleHandle curr = handles.get(i);
			if(curr.consoleHandleKey.equals(handle.consoleHandleKey)) {
				//相同名称的handle会被替换
				CCLog.engine("SUIConsole", "handle reset : " + curr.consoleHandleKey);
				//替换
				handles.set(i, handle);
				return;
			}
		}
		//添加
		handles.add(handle);
	}
	
	public final void clearConsoleHandle() {
		handles.clear();
	}
	
	/**获取当前的所有handle信息 */
	public String getHandlesInfo() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < handles.size; ++i) {
			sb.append('[').append(i).append(']').append(handles.get(i).consoleHandleKey).append(',').append(' ');
		}
		String ret = sb.toString();
		sb = null;
		return ret;
	}
	
	public final void removeConsoleHandle(String handleKey) {
		for(int i = 0; i < handles.size; ++i) {
			ConsoleHandle curr = handles.get(i);
			if(curr.consoleHandleKey.equals(handleKey)) {
				handles.removeIndex(i);
				return;
			}
		}
		CCLog.engine("SUIConsole", "remove failed : " + handleKey);
	}
	
	public final void removeConsoleHandle(ConsoleHandle handle) {
		if(handles.removeValue(handle, true)) {
			
		} else {
			CCLog.engine("SUIConsole", "remove failed : " + handle.consoleHandleKey);
		}
	}
	//methods<<
	
	
	//override>>
	@Override 
	protected void addToStage(GdxUIManager parent) {
//		System.out.println("addToStage");
		BaseInput.instance().pushInputProcessor(listenPopPush);
		super.addToStage(parent);
	}
	
	@Override
	protected void removeFromStage() {
		BaseInput.instance().removeInputProcessor(listenPopPush);
		super.removeFromStage();
	}
	
	@Override
	protected void onInitUI() {
		Skin skin = GdxUISkin.instance().getDeafult();
		input = new TextField("", skin);
		
		ClickListener mClickListener = new ClickListener() {
			public boolean keyDown (InputEvent event, int keycode) {
				if(keycode == Keys.ENTER && !isHide()) {
					if(!handleCmdString(input.getText())) {
						//clear input 
						input.setText(null);
					}
				}
				return true;
			}
		};
		input.addCaptureListener(mClickListener);
		
		input.setTextFieldFilter(new TextFieldFilter() {
			@Override
			public boolean acceptChar(TextField textField, char c) {
				if(c == '`' || c == '~') {
					hide();
					return false;
				}
				return true;
			}
		});
		label = new Label("", skin);
		
		maxTmp = GdxUIConfig.instance().consoleMaxOutputLine;
		
		input.setSize(getUIWidth()/2, 32);
		input.setPosition(20, getUIHeight() * 0.25f);
		addUI(input);
		addUI(label);
		
		skin = null;
	}

	@Override
	protected void onHideUI() {
		Stage stage = input.getStage();
		if(stage != null) {
			stage.setKeyboardFocus(null);
		}
	}

	@Override
	protected void onShowUI() {
		//通过task 延后一周期执行
		showTask.attachSchedule();
	}

	@Override
	protected void onDestroyUI() {
		
	}

	@Override
	protected void onUpdateUI(int dt) {
		
	}
	
	@Override
	protected void onChanged(Event e, Actor actor) {
		
	}
	//override<<

	
}
