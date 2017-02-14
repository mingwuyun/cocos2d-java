package com.cocos2dj.s2d;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.cocos2dj.renderer.RenderCommand;
import com.cocos2dj.renderer.Renderer;
import com.cocos2dj.utils.MessUtils;
import com.cocos2dj.renderer.RenderCommand.ShapeCommandCallback;

/**
 * DrawNode.java
 * <p>
 * 
 * 图元绘制指令缓存：
 * Type, color, borderWidth, pointLen, x0, y1, x1, y1, ...
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
    	drawDot(point, color);
    }
    
    /** Draw a group point.
     *
     * @param position A Vector2 pointer.
     * @param numberOfPoints The number of points.
     * @param color The point color.
     * @js NA
     */
//    public void drawPoints(Vector2 position, int numberOfPoints, Color color) { }
    
    /** Draw a group point.
     *
     * @param position A Vector2 pointer.
     * @param numberOfPoints The number of points.
     * @param pointSize The point size.
     * @param color The point color.
     * @js NA
     */
//    public void drawPoints(Vector2 position, int numberOfPoints,  float pointSize,  Color color) { }
    
    /** Draw an line from origin to destination with color. 
     * 
     * @param origin The line origin.
     * @param destination The line destination.
     * @param color The line color.
     * @js NA
     */
    public void drawLine( Vector2 origin,  Vector2 destination,  Color color) {
    	//暂时这样
    	drawSegment(origin, destination, 1f, color);
    }
    
    /** Draws a rectangle given the origin and destination point measured in points.
     * The origin and the destination can not have the same x and y coordinate.
     *
     * @param origin The rectangle origin.
     * @param destination The rectangle destination.
     * @param color The rectangle color.
     */
    public void drawRect( Vector2 origin,  Vector2 destination,  Color color) {
    	drawSolidRect(origin.x, origin.y, destination.x, destination.y, color);
    }
    
    public void drawRect(float fromX, float fromY, float toX, float toY, Color color) {
    	if(!batchCommand) {	
    		clear();
    	}
    	pushShapeCommandCell(DrawType.Rect, color, ShapeType.Line, lineWidth, 
    			fromX, fromY, toX, toY);
    }
    
    /** Draws a polygon given a pointer to point coordinates and the number of vertices measured in points.
     * The polygon can be closed or open.
     *
     * @param poli A pointer to point coordinates.
     * @param numberOfPoints The number of vertices measured in points.
     * @param closePolygon The polygon can be closed or open.
     * @param color The polygon color.
     */
//    public void drawPoly( Vector2[] poli, boolean closePolygon,  Color color) {    	}
    
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
//    public void drawCircle(  Vector2 center, float radius, float angle,  int segments, boolean drawLineToCenter, float scaleX, float scaleY,  Color color) {}
    
    /** Draws a circle given the center, radius and number of segments.
     *
     * @param center The circle center point.
     * @param radius The circle rotate of radius.
     * @param angle  The circle angle.
     * @param segments The number of segments.
     * @param drawLineToCenter Whether or not draw the line from the origin to center.
     * @param color Set the circle color.
     */
    public void drawCircle( Vector2 center, float radius, int segments, Color color) {
    	drawCircle(center.x, center.y, radius, segments, color);
    }
    
    public void drawCircle( Vector2 center, float radius, Color color) {
    	drawCircle(center.x, center.y, radius, -1, color);
    }
    
    public void drawCircle(float x, float y, float radius,  Color color) {
    	drawCircle(x, y, radius, -1, color);
    }
    
    public void drawCircle(float x, float y, float radius,int segments,  Color color) {
    	if(!batchCommand) {	
    		clear();
    	}
    	pushShapeCommandCell(DrawType.Circle, color, ShapeType.Line, 
    			radius, x, y, segments);
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
    	drawQuadBezier(origin.x, origin.y, control.x, control.y, destination.x, destination.y, segments, color);
    }
    
    public void drawQuadBezier( Vector2 origin,  Vector2 control,  Vector2 destination,  Color color) {
    	drawQuadBezier(origin, control, destination, 16, color);
    }
    
    public void drawQuadBezier(float x1, float y1, float cx, float cy, float x2, float y2,  Color color) {
    	drawQuadBezier(x1, y1, cx, cy, x2, y2, 16, color);
    }
    
    public void drawQuadBezier(float x1, float y1, float cx, float cy, float x2, float y2, int segments,  Color color) {
    	if(!batchCommand) {	
    		clear();
    	}
    	pushShapeCommandCell(DrawType.Bezier, color, ShapeType.Line, lineWidth, 
    			x1, y1, cx, cy, cx, cy, x2, y2, segments);
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
    	drawCubicBezier(origin.x, origin.y, control1.x, control1.y, 
    			control2.x, control2.y, destination.x, destination.y, segments, color);
    }
    
    public void drawCubicBezier( Vector2 origin,  Vector2 control1,  Vector2 control2,  Vector2 destination,  Color color) {
    	drawCubicBezier(origin, control1, control2, destination, 16, color);
    }
    
    public void drawCubicBezier(float x1, float y1, float c1x, float c1y, float c2x, float c2y,  float x2, float y2,  Color color) {
    	if(!batchCommand) {	
    		clear();
    	}
    	pushShapeCommandCell(DrawType.Bezier, color, ShapeType.Line, lineWidth, 
    			x1, y1, c1x, c1y, c2x, c2y, x2, y2, 16);
    }
    
    public void drawCubicBezier(float x1, float y1, float c1x, float c1y, float c2x, float c2y,  float x2, float y2,  int segments,  Color color) {
    	if(!batchCommand) {	
    		clear();
    	}
    	pushShapeCommandCell(DrawType.Bezier, color, ShapeType.Line, lineWidth, 
    			x1, y1, c1x, c1y, c2x, c2y, x2, y2, segments);
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
    public void drawDot(Vector2 pos, float radius, Color color) {
    	drawDot(pos.x, pos.y, radius, color);
    }
    
    public void drawDot(Vector2 pos, Color color) {
    	drawDot(pos.x, pos.y, color);
    }
    
    public void drawDot(float x, float y, Color color) {
    	drawDot(x, y, lineWidth, color);	// 使用默认尺寸
    }
    
    public void drawDot(float x, float y, float radius, Color color) {
    	if(!batchCommand) {	
    		clear();
    	}
    	pushShapeCommandCell(DrawType.Dot, color, ShapeType.Filled, radius, 
    			x, y);
    }
    
    /** Draws a rectangle with 4 points.
     *
     * @param p1 The rectangle vertex point.
     * @param p2 The rectangle vertex point.
     * @param p3 The rectangle vertex point.
     * @param p4 The rectangle vertex point.
     * @param color The rectangle color.
     */
//    public void drawRect( Vector2 p1,  Vector2 p2,  Vector2 p3,  Vector2 p4,  Color color) {
//    	drawSolidRect(origin.x, origin.y, destination.x, destination.y, color);
//    }
    
    /** Draws a solid rectangle given the origin and destination point measured in points.
     * The origin and the destination can not have the same x and y coordinate.
     *
     * @param origin The rectangle origin.
     * @param destination The rectangle destination.
     * @param color The rectangle color.
     * @js NA
     */
    public void drawSolidRect( Vector2 origin,  Vector2 destination,  Color color) {
    	drawSolidRect(origin.x, origin.y, destination.x, destination.y, color);
    }
    
    public void drawSolidRect(float fromX, float fromY, float toX, float toY, Color color) {
    	if(!batchCommand) {	
    		clear();
    	}
    	pushShapeCommandCell(DrawType.Rect, color, ShapeType.Filled, lineWidth, 
    			fromX, fromY, toX, toY);
    }
    
    /** Draws a solid polygon given a pointer to CGPoint coordinates, the number of vertices measured in points, and a color.
     *
     * @param poli A solid polygon given a pointer to CGPoint coordinates.
     * @param color The solid polygon color.
     * @js NA
     */
//    public void drawSolidPoly( Vector2[] poli, Color color) {
//    }
    
    /** Draws a solid circle given the center, radius and number of segments.
     * @param center The circle center point.
     * @param radius The circle rotate of radius.
     * @param color The solid circle color.
     * @js NA
     */
    public void drawSolidCircle( Vector2 center, float radius, Color color) {
    	drawSolidCircle(center.x, center.y, radius, color);
    }
    
    /** Draws a solid circle given the center, radius and number of segments.
     * @param center The circle center point.
     * @param radius The circle rotate of radius.
     * @param segments The number of segments.
     * @param color The solid circle color.
     * @js NA
     */
    public void drawSolidCircle( Vector2 center, float radius, int segments,  Color color) {
    	drawSolidCircle(center.x, center.y, radius, segments, color);
    }
    
    public void drawSolidCircle(float x, float y, float radius, Color color) {
    	drawSolidCircle(x, y, radius, -1, color);
    }

    public void drawSolidCircle(float x, float y, float radius, int segments, Color color) {
    	if(!batchCommand) {	
    		clear();
    	}
    	pushShapeCommandCell(DrawType.Circle, color, ShapeType.Filled, 
    			radius, x, y, segments);
    }
    
    /** draw a segment with a radius and color. 
     *
     * @param from The segment origin.
     * @param to The segment destination.
     * @param radius The segment radius.
     * @param color The segment color.
     */
    public void drawSegment( Vector2 from,  Vector2 to, float radius,  Color color) {
    	drawSegment(from.x, from.y, to.x, to.y, radius, color);
    }
    
    public void drawSegment( Vector2 from,  Vector2 to,  Color color) {
    	drawSegment(from.x, from.y, to.x, to.y, lineWidth, color);
    }
    
    public void drawSegment(float fromX, float fromY, float toX, float toY) {
    	drawSegment(fromX, fromY, toX, toY, lineWidth, null);
    }
    
    public void drawSegment( float fromX, float fromY, float toX, float toY, float radius,  Color color) {
    	if(!batchCommand) {	
    		clear();
    	}
    	pushShapeCommandCell(DrawType.Segment, color, ShapeType.Filled, 
    			radius, fromX, fromY, toX, toY);
    }
    
    /** draw a polygon with a fill color and line color
    * @code
    * When this function bound into js or lua,the parameter will be changed
    * In js: var drawPolygon(var Arrayofpoints, var fillColor, var width, var borderColor)
    * In lua:local drawPolygon(local pointTable,local tableCount,local fillColor,local width,local borderColor)
    * @endcode
    * @param verts A pointer to point coordinates.
    * @param fillColor The color will fill in polygon.
    * @param borderColor The border of line color.
    * @js NA
    */
    public void drawPolygon(Vector2[] verts, Color fillColor) {
    	drawPolygon(MessUtils.pointsToFloats(verts), fillColor);
    }
	
    public void drawPolygon(float[] verts, Color color) {
    	if(!batchCommand) {	
    		clear();
    	}
    	pushShapeCommandCell(DrawType.Polygon, color, ShapeType.Line, lineWidth, 
    			verts);
    }
    
    public void drawSolidTriangle( Vector2 p1,  Vector2 p2,  Vector2 p3,  Color color) {
    	if(!batchCommand) {	
    		clear();
    	}
    	pushShapeCommandCell(DrawType.Triange, color, ShapeType.Line,
    			lineWidth, MessUtils.pointsToFloats(p1, p2, p3));
    }
    
    public void drawSolidTriangle(float x0, float y0, float x1, float y1, float x2, float y2, Color color) {
    	if(!batchCommand) {	
    		clear();
    	}
    	pushShapeCommandCell(DrawType.Triange, color, ShapeType.Filled,
    			lineWidth, x0, y0, x1, y1, x2, y2);
    }
    
    /** draw a triangle with color. 
     *
     * @param p1 The triangle vertex point.
     * @param p2 The triangle vertex point.
     * @param p3 The triangle vertex point.
     * @param color The triangle color.
     */
    public void drawTriangle( Vector2 p1,  Vector2 p2,  Vector2 p3,  Color color) {
    	if(!batchCommand) {	
    		clear();
    	}
    	pushShapeCommandCell(DrawType.Triange, color, ShapeType.Line,
    			lineWidth, MessUtils.pointsToFloats(p1, p2, p3));
    }
    
    public void drawTriangle(float x0, float y0, float x1, float y1, float x2, float y2, Color color) {
    	if(!batchCommand) {	
    		clear();
    	}
    	pushShapeCommandCell(DrawType.Triange, color, ShapeType.Line,
    			lineWidth, x0, y0, x1, y1, x2, y2);
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
    
    public void setLineWidth(int lineWidth) {
    	this.lineWidth = lineWidth;
    }

    // Get CocosStudio guide lines width.
    public float getLineWidth() {
    	return lineWidth;
    }
    
    ///////////////////////////////////////
    //TODO 绘制命令相关
    static enum DrawType {
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
    float 			lineWidth = 2;
    static final Vector3 stackVec3 = new Vector3();
    private boolean _useCulling = true;
    private boolean _insideBounds = true;
    
    public final boolean isInsideBounds() {
    	return _insideBounds;
    }
    
    public final DrawNode startBatch() {
    	batchCommand = true;
    	return this;
    }
    
    /**添加shape绘制命令缓存 */
    final void pushShapeCommandCell(DrawType type, Color color, ShapeType drawType,
    		float borderWidth, float...vs) {
    	shapeCommandQueues.add(type);
//    	if(color == null) {
//    		color = Color.BLUE;
//    	}
    	shapeCommandQueues.add(color);
    	shapeCommandQueues.add(drawType);
    	shapeCommandQueues.add(borderWidth);
    	shapeCommandQueues.add(vs.length);
    	for(float v : vs) {
    		shapeCommandQueues.add(v);
    	}
    	
    	//contentSize
    	switch(type) {
    	case Circle:
    		float width = getContentSize().width;
    		float height = getContentSize().height;
    		width = borderWidth > width ? borderWidth : width;
    		height = borderWidth > height ? borderWidth : height;
    		setContentSize(borderWidth, borderWidth);	//circle special
    		break;
    	default:
    		//自动忽略最后一位数
    		calculateContentSize(vs);
    		break;
    	}
//    	System.out.println("content = " + getContentSize());
    }
    
    final StructShapeCommand popShapeCommandCell() {
    	if(currShapePos >= shapeCommandQueues.size) {
    		return null;
    	}
    	
    	stackCommand.clear();
    	stackCommand.drawType = (DrawType) shapeCommandQueues.get(currShapePos++);
    	stackCommand.color = (Color) shapeCommandQueues.get(currShapePos++);
    	// 如果color是null则使用默认颜色
    	if(stackCommand.color == null) {
    		stackCommand.color = getDisplayedColor();
    	}
    	stackCommand.shapeType = (ShapeType) shapeCommandQueues.get(currShapePos++);
    	stackCommand.borderWidth = (float) shapeCommandQueues.get(currShapePos++);
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
    	float			borderWidth;
    	Array<Float> 	vertexArray = new Array<>();
    	
    	final void clear() {
    		vertexArray.clear();
    	}
    	
    	final void drawShape(ShapeRenderer shapeRenderer, Matrix4 trans) {
    		if(shapeType != shapeRenderer.getCurrentType()) {
    			shapeRenderer.set(shapeType);
    		}
    		switch(drawType) {
    		case Segment: {
    			shapeRenderer.setColor(color);
    			float x0 = vertexArray.get(0);
    			float y0 = vertexArray.get(1);
    			float x1 = vertexArray.get(2);
    			float y1 = vertexArray.get(3);
    			
    			//坐标变换
    			stackVec3.set(x0, y0, 0).mul(trans);
    			x0 = stackVec3.x; y0 = stackVec3.y;
    			stackVec3.set(x1, y1, 0).mul(trans);
    			x1 = stackVec3.x; y1 = stackVec3.y;
    			
    			if(borderWidth <= 0) {
    				shapeRenderer.line(x0, y0, x1, y1);
    			} else {
    				shapeRenderer.rectLine(x0, y0, x1, y1, borderWidth);
    			}
    		} break;
    		case Polygon: {
    			shapeRenderer.setColor(color);
    			float[] points = new float[vertexArray.size];
    			for(int i = 0; i < points.length; i+=2) {
    				float x = vertexArray.get(i);
    				float y = vertexArray.get(i + 1);
    				stackVec3.set(x, y, 0).mul(trans);
    				points[i] = stackVec3.x;
    				points[i + 1] = stackVec3.y;
    			}
    			shapeRenderer.polygon(points);
    		} break;
    		case Triange: {
    			shapeRenderer.setColor(color);
    			float x0 = vertexArray.get(0);
    			float y0 = vertexArray.get(1);
    			float x1 = vertexArray.get(2);
    			float y1 = vertexArray.get(3);
    			float x2 = vertexArray.get(4);
    			float y2 = vertexArray.get(5);
    			
    			stackVec3.set(x0, y0, 0).mul(trans);
    			x0 = stackVec3.x; y0 = stackVec3.y;
    			stackVec3.set(x1, y1, 0).mul(trans);
    			x1 = stackVec3.x; y1 = stackVec3.y;
    			stackVec3.set(x2, y2, 0).mul(trans);
    			x2 = stackVec3.x; y2 = stackVec3.y;
    			
    			shapeRenderer.triangle(x0, y0, x1, y1, x2, y2);
    		} break;
    		case Rect: {
    			shapeRenderer.setColor(color);
    			float x0 = vertexArray.get(0);
    			float y0 = vertexArray.get(1);
    			float x2 = vertexArray.get(2);
    			float y2 = vertexArray.get(3);
    			float x1 = x2;
    			float y1 = y0;
    			float x3 = x0;
    			float y3 = y2;
    			
    			stackVec3.set(x0, y0, 0).mul(trans);
    			x0 = stackVec3.x; y0 = stackVec3.y;
    			stackVec3.set(x1, y1, 0).mul(trans);
    			x1 = stackVec3.x; y1 = stackVec3.y;
    			stackVec3.set(x2, y2, 0).mul(trans);
    			x2 = stackVec3.x; y2 = stackVec3.y;
    			stackVec3.set(x3, y3, 0).mul(trans);
    			x3 = stackVec3.x; y3 = stackVec3.y;
    			
    			if(shapeType == ShapeType.Filled) {
    				shapeRenderer.triangle(x0, y0, x1, y1, x3, y3);
    				shapeRenderer.triangle(x3, y3, x1, y1, x2, y2);
    			} else {
    				shapeRenderer.line(x0, y0, x1, y1);
    				shapeRenderer.line(x1, y1, x2, y2);
    				shapeRenderer.line(x2, y2, x3, y3);
    				shapeRenderer.line(x3, y3, x0, y0);
    			}
    		} break;
    		case Dot: {
    			shapeRenderer.setColor(color);
    			float x = vertexArray.get(0);
    			float y = vertexArray.get(1);
    			stackVec3.set(x, y, 0).mul(trans);
    			x = stackVec3.x; y = stackVec3.y;
//    			System.out.println("x = " + x + " y = " + y + " broadWidth = " + borderWidth);
    			if(borderWidth < 1) {
    				shapeRenderer.circle(x, y, 1);
    			} else {
    				shapeRenderer.circle(x, y, borderWidth);
    			}
    		} break;
    		case Circle: {
    			shapeRenderer.setColor(color);
    			
    			float x = vertexArray.get(0);
    			float y = vertexArray.get(1);
    			stackVec3.set(x, y, 0).mul(trans);
    			x = stackVec3.x; y = stackVec3.y;
    			
    			float scaleX = trans.getScaleX();
    			
    			float segmentCount = vertexArray.get(2);
    			if(segmentCount < 1) {
    				shapeRenderer.circle(x, y, borderWidth * scaleX);
    			} else {
    				shapeRenderer.circle(x, y, borderWidth * scaleX, (int)segmentCount);
    			}
    		} break;
    		
    		case Bezier: {
    			shapeRenderer.setColor(color);
    			float x1 = vertexArray.get(0);
    			float y1 = vertexArray.get(1);
    			float cx1 = vertexArray.get(2);
    			float cy1 = vertexArray.get(3);
    			float cx2 = vertexArray.get(4);
    			float cy2 = vertexArray.get(5);
    			float x2 = vertexArray.get(6);
    			float y2 = vertexArray.get(7);
    			float segments = vertexArray.get(8);
    			
    			stackVec3.set(x1, y1, 0).mul(trans);
    			x1 = stackVec3.x; y1 = stackVec3.y;
    			stackVec3.set(cx1, cy1, 0).mul(trans);
    			cx1 = stackVec3.x; cy1 = stackVec3.y;
    			stackVec3.set(cx2, cy2, 0).mul(trans);
    			cx2 = stackVec3.x; cy2 = stackVec3.y;
    			stackVec3.set(x2, y2, 0).mul(trans);
    			x2 = stackVec3.x; y2 = stackVec3.y;
    			
    			shapeRenderer.curve(x1, y1, cx1, cy1, cx2, cy2, x2, y2, (int)segments);
    		} break;
    		}
    	}
    }
    static StructShapeCommand stackCommand = new StructShapeCommand();
    
//    final void calculateContentSize(Vector2[] points) {
//    	
//    }
    final void calculateContentSize(float[] points) {
    	float x0 = Float.MAX_VALUE;
    	float y0 = Float.MAX_VALUE;
    	float x1 = Float.MIN_VALUE;
    	float y1 = Float.MIN_VALUE;
    	
    	for(int i = 0, n = points.length/2; i < n; ++i) {
    		float x = points[i*2];
    		float y = points[i*2+1];
    		
    		x0 = x < x0 ? x : x0;
    		y0 = y < y0 ? y : y0;
    		x1 = x > x1 ? x : x1;
    		y1 = y > y1 ? y : y1;
    	}
    	
    	float width = getContentSize().width;
		float height = getContentSize().height;
    	float nwidth = x1 - x0;
		float nheight = y1 - y0;
		
		width = nwidth > width ? nwidth : width;
		height = nheight > height ? nheight : height;
    	setContentSize(width, height);
    }
    
    final void calculateContentSizeCircle(float x, float y, float radious) {
    	
    }
    ///////////////////////////////////////
    
    
    public void draw(Renderer renderer, Matrix4 transform, int flags) {
    	if(_useCulling) {
			_insideBounds = renderer.checkVisibility(transform, _contentSize, _anchorPointInPoints);
    	} else {
    		_insideBounds = true;
    	}
    	
    	if(_insideBounds) {
    		renderer.addShapeCommand(_shapeCommand);
    	}
    }
    
    final RenderCommand.ShapeCommand _shapeCommand;

	@Override
	public void onCommand(ShapeRenderer shapeRenderer) {
		currShapePos = 0;
		StructShapeCommand cmd = popShapeCommandCell();
		while(cmd != null) {
			cmd.drawShape(shapeRenderer, _modelViewTransform);
			cmd = popShapeCommandCell();
		}
		batchCommand = false;	//绘制一次后，batch命令清除
	}
}
