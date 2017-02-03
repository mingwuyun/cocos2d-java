package com.cocos2dj.renderer;

import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteCache;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * RenderCommand.java
 * <br>BatchCommand
 * <br>CacheCommand
 * <br>DrawCommand
 * <br>ShapeCommand
 * <p>
 * @author Copyright(c) 2017 xujun
 */
public interface RenderCommand {
	
	public static enum RenderCommandType {
		Batch,
		Cache,
		Draw,
	}
	
	public RenderCommandType getCommandType();
	public void execute(Renderer render);		
	
	
	public final class BatchCommand implements RenderCommand {
		final BatchCommandCallback _callback;
		public BatchCommand(BatchCommandCallback callback) {
			this._callback = callback;
		}
		@Override
		public RenderCommandType getCommandType() {
			return RenderCommandType.Batch;
		}
		@Override
		public void execute(Renderer render) {
			PolygonSpriteBatch batch = render.batch;
			if(!batch.isDrawing()) {
				batch.begin();
			}
			this._callback.onCommand(render.batch);
		}
	}
	
	public final class CacheCommand implements RenderCommand {
		final CacheCommandCallback _callback;
		public CacheCommand(CacheCommandCallback callback) {
			this._callback = callback;
		}
		@Override
		public RenderCommandType getCommandType() {
			return RenderCommandType.Cache;
		}
		@Override
		public void execute(Renderer render) {
			PolygonSpriteBatch batch = render.batch;
			if(batch.isDrawing()) {
				batch.end();
			}
//			this._callback.onCommand(render.c);
		}
	}
	
	public final class DrawCommand implements RenderCommand {
		final DrawCommandCallback _callback;
		public DrawCommand(DrawCommandCallback callback) {
			this._callback = callback;
		}
		@Override
		public RenderCommandType getCommandType() {
			return RenderCommandType.Draw;
		}
		@Override
		public void execute(Renderer render) {
			PolygonSpriteBatch batch = render.batch;
			if(batch.isDrawing()) {
				batch.end();
			}
			this._callback.onCommand();
		}
	}
	
	public final class ShapeCommand implements RenderCommand {
		final ShapeCommandCallback _callback;
		public ShapeCommand(ShapeCommandCallback callback) {
			this._callback = callback;
		}
		@Override
		public RenderCommandType getCommandType() {
			return RenderCommandType.Draw;
		}
		@Override
		public void execute(Renderer render) {
			ShapeRenderer shapeRender = render.getShapeRenderer();
			if(!shapeRender.isDrawing()) {
				shapeRender.begin();
			}
			this._callback.onCommand(shapeRender);
		}
	}
	
	public static interface BatchCommandCallback {
		public void onCommand(PolygonSpriteBatch batch);
	}
	
	public static interface CacheCommandCallback {
		public void onCommand(SpriteCache cache);
	}
	
	public static interface DrawCommandCallback {
		public void onCommand();
	}
	
	public static interface ShapeCommandCallback {
		public void onCommand(ShapeRenderer shapeRenderer);
	}
}
