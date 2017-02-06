package com.cocos2dj.s2d;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.cocos2dj.renderer.RenderCommand;
import com.cocos2dj.renderer.Renderer;
import com.cocos2dj.renderer.RenderCommand.ShapeCommandCallback;

/**
 * DrawNode.java
 * <p>
 * 
 * 图元绘制指令缓存：
 * Type, color, pointLen, x0, y1, x1, y1, ...
 * 
 * @author Copyright(c) 2017 xu jun
 */
public class DrawNode extends Node implements ShapeCommandCallback {
	
	static final int DEFAULT_LINE_WIDTH = 1;
	
	/** creates and initialize a DrawNode node.
    *
    * @return Return an autorelease object.
    */
   public static DrawNode create() {
   		return new DrawNode();
   }
   
    /** creates and initialize a DrawNode node.
     *
     * @return Return an autorelease object.
     */
    public static DrawNode create(int defaultLineWidth) {
    	DrawNode ret = new DrawNode();
    	ret.setLineWidth(defaultLineWidth);
    	return ret;
    }

    
    public DrawNode() {
    	 _shapeCommand = new RenderCommand.ShapeCommand(this);
    }
    
    /** Draw a point.
     *
     * @param point A Vector2 used to point.
     * @param pointSize The point size.
     * @param color The point color.
     * @js NA
     */
    public void drawPoint(Vector2 point, float pointSize, Color color) {
    	
    }
    
    /** Draw a group point.
     *
     * @param position A Vector2 pointer.
     * @param numberOfPoints The number of points.
     * @param color The point color.
     * @js NA
     */
    public void drawPoints(Vector2 position, int numberOfPoints, Color color) {
    	
    }
    
    /** Draw a group point.
     *
     * @param position A Vector2 pointer.
     * @param numberOfPoints The number of points.
     * @param pointSize The point size.
     * @param color The point color.
     * @js NA
     */
    public void drawPoints(Vector2 position, int numberOfPoints,  float pointSize,  Color color) {
    	
    }
    
    /** Draw an line from origin to destination with color. 
     * 
     * @param origin The line origin.
     * @param destination The line destination.
     * @param color The line color.
     * @js NA
     */
    public void drawLine( Vector2 origin,  Vector2 destination,  Color color) {
    	
    }
    
    /** Draws a rectangle given the origin and destination point measured in points.
     * The origin and the destination can not have the same x and y coordinate.
     *
     * @param origin The rectangle origin.
     * @param destination The rectangle destination.
     * @param color The rectangle color.
     */
    public void drawRect( Vector2 origin,  Vector2 destination,  Color color) {
    	
    }
    
    /** Draws a polygon given a pointer to point coordinates and the number of vertices measured in points.
     * The polygon can be closed or open.
     *
     * @param poli A pointer to point coordinates.
     * @param numberOfPoints The number of vertices measured in points.
     * @param closePolygon The polygon can be closed or open.
     * @param color The polygon color.
     */
    public void drawPoly( Vector2[] poli, boolean closePolygon,  Color color) {
    	
    }
    
    /** Draws a circle given the center, radius and number of segments.
     *
     * @param center The circle center point.
     * @param radius The circle rotate of radius.
     * @param angle  The circle angle.
     * @param segments The number of segments.
     * @param drawLineToCenter Whether or not draw the line from the origin to center.
     * @param scaleX The scale value in x.
     * @param scaleY The scale value in y.
     * @param color Set the circle color.
     */
    public void drawCircle(  Vector2 center, float radius, float angle,  int segments, boolean drawLineToCenter, float scaleX, float scaleY,  Color color) {
    	
    }
    
    /** Draws a circle given the center, radius and number of segments.
     *
     * @param center The circle center point.
     * @param radius The circle rotate of radius.
     * @param angle  The circle angle.
     * @param segments The number of segments.
     * @param drawLineToCenter Whether or not draw the line from the origin to center.
     * @param color Set the circle color.
     */
    public void drawCircle( Vector2 center, float radius, float angle,  int segments, boolean drawLineToCenter,  Color color) {
    	
    }
    
    /** Draws a quad bezier path.
     *
     * @param origin The origin of the bezier path.
     * @param control The control of the bezier path.
     * @param destination The destination of the bezier path.
     * @param segments The number of segments.
     * @param color Set the quad bezier color.
     */
    public void drawQuadBezier( Vector2 origin,  Vector2 control,  Vector2 destination,  int segments,  Color color) {
    	
    }

    /** Draw a cubic bezier curve with color and number of segments
     *
     * @param origin The origin of the bezier path.
     * @param control1 The first control of the bezier path.
     * @param control2 The second control of the bezier path.
     * @param destination The destination of the bezier path.
     * @param segments The number of segments.
     * @param color Set the cubic bezier color.
     */
    public void drawCubicBezier( Vector2 origin,  Vector2 control1,  Vector2 control2,  Vector2 destination,  int segments,  Color color) {
    	
    }
    
    /** Draws a Cardinal Spline path.
     *
     * @param config A array point.
     * @param tension The tension of the spline.
     * @param segments The number of segments.
     * @param color Set the Spline color.
     */
//    public void drawCardinalSpline(PointArray *config, float tension,   int segments,  Color color) {
//    }
    
    /** Draws a Catmull Rom path.
     *
     * @param points A point array  of control point.
     * @param segments The number of segments.
     * @param color The Catmull Rom color.
     */
//    void drawCatmullRom(PointArray *points,  int segments,  Color color);
    
    /** draw a dot at a position, with a given radius and color. 
     *
     * @param pos The dot center.
     * @param radius The dot radius.
     * @param color The dot color.
     */
    public void drawDot(Vector2 pos, float radius,  Color color) {
    	
    }
    
    /** Draws a rectangle with 4 points.
     *
     * @param p1 The rectangle vertex point.
     * @param p2 The rectangle vertex point.
     * @param p3 The rectangle vertex point.
     * @param p4 The rectangle vertex point.
     * @param color The rectangle color.
     */
    public void drawRect( Vector2 p1,  Vector2 p2,  Vector2 p3,  Vector2 p4,  Color color) {
    	
    }
    
    /** Draws a solid rectangle given the origin and destination point measured in points.
     * The origin and the destination can not have the same x and y coordinate.
     *
     * @param origin The rectangle origin.
     * @param destination The rectangle destination.
     * @param color The rectangle color.
     * @js NA
     */
    public void drawSolidRect( Vector2 origin,  Vector2 destination,  Color color) {
    	
    }
    
    /** Draws a solid polygon given a pointer to CGPoint coordinates, the number of vertices measured in points, and a color.
     *
     * @param poli A solid polygon given a pointer to CGPoint coordinates.
     * @param color The solid polygon color.
     * @js NA
     */
    public void drawSolidPoly( Vector2[] poli, Color color) {
    	
    }
    
    /** Draws a solid circle given the center, radius and number of segments.
     * @param center The circle center point.
     * @param radius The circle rotate of radius.
     * @param angle  The circle angle.
     * @param segments The number of segments.
     * @param scaleX The scale value in x.
     * @param scaleY The scale value in y.
     * @param color The solid circle color.
     * @js NA
     */
    public void drawSolidCircle( Vector2 center, float radius, float angle,  int segments, float scaleX, float scaleY,  Color color) {
    	
    }
    
    /** Draws a solid circle given the center, radius and number of segments.
     * @param center The circle center point.
     * @param radius The circle rotate of radius.
     * @param angle  The circle angle.
     * @param segments The number of segments.
     * @param color The solid circle color.
     * @js NA
     */
    public void drawSolidCircle( Vector2 center, float radius, float angle,  int segments,  Color color) {
    	
    }
    
    /** draw a segment with a radius and color. 
     *
     * @param from The segment origin.
     * @param to The segment destination.
     * @param radius The segment radius.
     * @param color The segment color.
     */
    public void drawSegment( Vector2 from,  Vector2 to, float radius,  Color color) {
    	
    }
    public void drawSegment( float fromX, float fromY, float toX, float toY, float radius,  Color color) {
    	if(!batchCommand) {	
    		clear();
    	}
    	pushShapeCommandCell(DrawType.Segment, color, ShapeType.Filled,
    			radius, fromX, fromY, toX, toY);
    	/*
    	 * 格式：
    	 * width : if 0 draw line else draw rectLine
    	 * x0, y0, x1, y1 
    	 */
    }
    
    /** draw a polygon with a fill color and line color
    * @code
    * When this function bound into js or lua,the parameter will be changed
    * In js: var drawPolygon(var Arrayofpoints, var fillColor, var width, var borderColor)
    * In lua:local drawPolygon(local pointTable,local tableCount,local fillColor,local width,local borderColor)
    * @endcode
    * @param verts A pointer to point coordinates.
    * @param fillColor The color will fill in polygon.
    * @param borderWidth The border of line width.
    * @param borderColor The border of line color.
    * @js NA
    */
    public void drawPolygon(Vector2[] verts, Color fillColor, float borderWidth,  Color borderColor) {
    	
    }
	
    /** draw a triangle with color. 
     *
     * @param p1 The triangle vertex point.
     * @param p2 The triangle vertex point.
     * @param p3 The triangle vertex point.
     * @param color The triangle color.
     * @js NA
     */
    public void drawTriangle( Vector2 p1,  Vector2 p2,  Vector2 p3,  Color color) {
    	
    }

    /** draw a quadratic bezier curve with color and number of segments, use drawQuadBezier instead.
     *
     * @param from The origin of the bezier path.
     * @param control The control of the bezier path.
     * @param to The destination of the bezier path.
     * @param segments The number of segments.
     * @param color The quadratic bezier color.
     * @js NA
     */
//    CC_DEPRECATED_ATTRIBUTE void drawQuadraticBezier( Vector2 from,  Vector2 control,  Vector2 to,  int segments,  Color color);
    
    /** Clear the geometry in the node's buffer. */
    public void clear() {
    	shapeCommandQueues.clear();
    }
    
    /** Get the color mixed mode.
    * @lua NA
    */
//     BlendFunc getBlendFunc() ;
    /** Set the color mixed mode.
    * @code
    * When this function bound into js or lua,the parameter will be changed
    * In js: var setBlendFunc(var src, var dst)
    * @endcode
    * @lua NA
    */
//    void setBlendFunc( BlendFunc blendFunc);

//    /**
//     * @js NA
//     */
//    virtual void onDraw( Mat4 transform, uint32_t flags);
//    /**
//     * @js NA
//     */
//    virtual void onDrawGLLine( Mat4 transform, uint32_t flags);
//    /**
//     * @js NA
//     */
//    virtual void onDrawGLPoint( Mat4 transform, uint32_t flags);
//    
//    // Overrides
//    virtual void draw(Renderer *renderer,  Mat4 transform, uint32_t flags) override;
    
    public void setLineWidth(int lineWidth) {
    	
    }

    // Get CocosStudio guide lines width.
    public float getLineWidth() {
    	//TODO
    	return 0;
    }
    
    ///////////////////////////////////////
    //TODO 绘制命令相关
    static enum DrawType {
    	Line,	//直线
    	Triange,
    	Circle,
    	Segment, //线段
    	Dot,
    	Rect,
    	Bezier,
    	Polygon,
    }
    Array<Object> 	shapeCommandQueues = new Array<>();
    private int 	currShapePos;
    private boolean	batchCommand = false;
    
    public final DrawNode startBatch() {
    	batchCommand = true;
    	return this;
    }
    
    /**添加shape绘制命令缓存 */
    final void pushShapeCommandCell(DrawType type, Color color, ShapeType drawType,
    		float...vs) {
    	shapeCommandQueues.add(type);
    	if(color == null) {
    		color = Color.BLUE;
    	}
    	shapeCommandQueues.add(color);
    	shapeCommandQueues.add(drawType);
    	shapeCommandQueues.add(vs.length);
    	for(float v : vs) {
    		shapeCommandQueues.add(v);
    	}
    }
    
    final StructShapeCommand popShapeCommandCell() {
    	if(currShapePos >= shapeCommandQueues.size) {
    		return null;
    	}
    	
    	stackCommand.clear();
    	stackCommand.drawType = (DrawType) shapeCommandQueues.get(currShapePos++);
    	stackCommand.color = (Color) shapeCommandQueues.get(currShapePos++);
    	stackCommand.shapeType = (ShapeType) shapeCommandQueues.get(currShapePos++);
    	int dataLen = (int) shapeCommandQueues.get(currShapePos++);
    	for(int i = 0; i < dataLen; ++i) {
    		stackCommand.vertexArray.add((Float) shapeCommandQueues.get(currShapePos++));
    	}
    	return stackCommand;
    }

    
    /**用于临时存放图元命令的数据结构 */
    static class StructShapeCommand {
    	DrawType 		drawType;
    	ShapeType		shapeType;
    	Color			color;
    	Array<Float> 	vertexArray = new Array<>();
    	
    	final void clear() {
    		vertexArray.clear();
    	}
    	
    	final void drawShape(ShapeRenderer shapeRenderer) {
    		if(shapeType != shapeRenderer.getCurrentType()) {
    			shapeRenderer.set(shapeType);
    		}
    		switch(drawType) {
    		case Segment:
    			shapeRenderer.setColor(color);
    			float width = vertexArray.get(0);
    			float x0 = vertexArray.get(1);
    			float y0 = vertexArray.get(2);
    			float x1 = vertexArray.get(3);
    			float y1 = vertexArray.get(4);
    			if(width <= 0) {
    				shapeRenderer.line(x0, y0, x1, y1);
    			} else {
    				shapeRenderer.rectLine(x0, y0, x1, y1, width);
    			}
    			break;
    		}
    	}
    }
    static StructShapeCommand stackCommand = new StructShapeCommand();
    
//    final void calculateContentSize(Vector2[] points) {
//    	
//    }
    final void calculateContentSize(float[] points) {
    	
    }
    
    final void calculateContentSizeCircle(float x, float y, float radious) {
    	
    }
    ///////////////////////////////////////
    
    
    public void draw(Renderer renderer, Matrix4 transform, int flags) {
    	renderer.addShapeCommand(_shapeCommand);
    }
    
    final RenderCommand.ShapeCommand _shapeCommand;

	@Override
	public void onCommand(ShapeRenderer shapeRenderer) {
		currShapePos = 0;
		StructShapeCommand cmd = popShapeCommandCell();
		while(cmd != null) {
			cmd.drawShape(shapeRenderer);
			cmd = popShapeCommandCell();
		}
		batchCommand = false;	//绘制一次后，batch命令清除
		
		//test
//		shapeRenderer.set(ShapeType.Filled);
//		shapeRenderer.setColor(Color.BLUE);
//		shapeRenderer.rectLine(20, 20, 300, 300, 10);
		
//		shapeRenderer.line(20, 20, 300, 300);
//		shapeRenderer.rect(20, 100, 500, 200);
//		shapeRenderer.line(0, 200, 800, 300);
	}
}
