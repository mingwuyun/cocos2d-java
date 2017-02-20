package com.cocos2dj.basic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputEventQueue;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;

/**
 * BaseInput.java
 * <p>
 * 封装了gdx中的输入事件系统 由engine负责调用驱动
 * 
 * @author Copyright (c) 2017 xu jun
 */
public class BaseInput implements IDisposable {
	
	private static BaseInput _instance;
	public static BaseInput instance() {
		if(_instance == null) {
			_instance = new BaseInput();
			_instance.init();
			Engine.registerDisposable(_instance);
		}
		return _instance;
	}
	private BaseInput() {}

	@Override
	public void dispose() {
		_instance = null;
	}
	
	
	InputEventQueue 	_inputQueue;
	InputMultiplexer 	_multiplexer;
	InputMultiplexer	_rootMultiPlexer;
	
	
	final void init() {
		_rootMultiPlexer = new InputMultiplexer();
		_multiplexer = new InputMultiplexer();
		_inputQueue = new InputEventQueue(_multiplexer);
		
		_rootMultiPlexer.addProcessor(_inputQueue);
		Gdx.input.setInputProcessor(_rootMultiPlexer);
	}
	
	final void update() {
		_inputQueue.drain();
	}
	
	
	public final void clearInputProcessor() {
		_multiplexer.clear();
	}
	
	public final int addInputProcessor(int index, InputProcessor processor) {
		_multiplexer.addProcessor(index, processor);
		return index;
	}
	
	public final int pushInputProcessor(InputProcessor processor) {
		_multiplexer.addProcessor(0, processor);
		return 0;
	}
	
	public final int addInputProcessor(InputProcessor processor) {
		int ret = _multiplexer.size();
		_multiplexer.addProcessor(processor);
		return ret;
	}
	
	public final void removeInputProcessor(InputProcessor processor) {
		_multiplexer.removeProcessor(processor);
	}
	
	public final void removeInputProcessor(int index) {
		_multiplexer.removeProcessor(index);
	}

}
