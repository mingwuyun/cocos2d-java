package tests.testcase;

import com.badlogic.gdx.graphics.Color;
import com.cocos2dj.s2d.DrawNode;

import tests.TestCase;
import tests.TestSuite;

/**
 * 图元绘制测试
 * 
 * @author xujun
 */
public class DrawNodeTests extends TestSuite {
	
	public DrawNodeTests() {
		addTestCase("drawNodeTest1", ()->{return new DrawNodeTest1();});
		addTestCase("drawNodeTest-culling", ()->{return new DrawNodeTest2();});
	}
	
	static class DrawNodeBase extends TestCase {
		
	}
	
	
	/////////////////////////////////////
	//TODO Test2
	static class DrawNodeTest1 extends DrawNodeBase {
		
		DrawNode drawNode1;
		DrawNode drawNode2;
		
		public void onEnter() {
			super.onEnter();
			
			drawNode1 = (DrawNode) DrawNode.create().addTo(this);
			drawNode2 = (DrawNode) DrawNode.create().addTo(this);
			
			
			drawNode1.startBatch();	//链接绘制命令（draw后自动结束，不用调用end）
			drawNode1.drawSegment(100, 100, 500, 500, 0, Color.GOLD);
			drawNode1.drawSegment(100, 500, 500, 100, 5, Color.GREEN);
			
			drawNode1.setPosition(200, 200);
			drawNode1.setRotation(30);
			
			drawNode2.drawPolygon(new float[]{
					20,20,
					200,31,
					300, 200,
					600, 380,
					100, 50,
			}, Color.YELLOW);
			drawNode2.setPosition(300, 0);
			
			DrawNode triangle = (DrawNode) DrawNode.create().addTo(this);
			triangle.drawTriangle(600, 300, 700, 300, 650, 200, Color.RED);
			
			DrawNode rect = (DrawNode) DrawNode.create().addTo(this);
			rect.setPosition(800, 400);
			rect.drawSolidRect(0, 0, 150, 50, Color.YELLOW);
			
			for(int i = 0; i < 10; ++i) {
				DrawNode point = (DrawNode) DrawNode.create().addTo(this);
				point.drawDot(500 + 20 * i, 500, Color.BROWN);
			}
			
			DrawNode circleSolid = (DrawNode) DrawNode.create().addTo(this);
			circleSolid.drawSolidCircle(600, 320, 50, Color.GREEN);
			
			DrawNode circle = (DrawNode) DrawNode.create().addTo(this);
			circle.drawCircle(0, 0, 50, Color.GREEN);
			circle.setScale(2f);
			circle.setPosition(800, 320);
			
			DrawNode bezier1 = (DrawNode) DrawNode.create().addTo(this);
			bezier1.drawCubicBezier(0, 0, 100, -50, 200, 150, 300, 100, Color.WHITE);
			bezier1.setPosition(100, 200);
			
			DrawNode bezier2 = (DrawNode) DrawNode.create().addTo(this);
			bezier2.drawQuadBezier(0, 0, 100, 150, 300, 100, Color.WHITE);
			bezier2.setPosition(600, 100);
		}		
	}
	
	//特殊测试(cull test)
	static class DrawNodeTest2 extends DrawNodeBase {
		
		DrawNode drawNode1;
		DrawNode drawNode2;
		
		public void onEnter() {
			super.onEnter();
			
			drawNode1 = (DrawNode) DrawNode.create().addTo(this);
			drawNode2 = (DrawNode) DrawNode.create().addTo(this);
			
			
			drawNode1.startBatch();	//链接绘制命令（draw后自动结束，不用调用end）
			drawNode1.drawSegment(100, 100, 500, 500, 0, Color.GOLD);
			drawNode1.drawSegment(100, 500, 500, 100, 5, Color.GREEN);
			drawNode1.setPosition(200, 200);
			drawNode1.setRotation(30);
			
			drawNode1.scheduleUpdate();
			drawNode1.setOnUpdateCallback((node, t)->{
				drawNode1.setPositionY(drawNode1.getPositionY() + 5);
				System.out.println("culling drawNode1 = " + drawNode1.isInsideBounds());
			});
			
			
			drawNode2.drawPolygon(new float[]{
					20,20,
					200,31,
					300, 200,
					600, 380,
					100, 50,
			}, Color.YELLOW);
			drawNode2.setPosition(300, 0);
			
			drawNode2.scheduleUpdate();
			drawNode2.setOnUpdateCallback((node, t)->{
				drawNode2.setPositionY(drawNode2.getPositionY() + 5);
				System.out.println("culling drawNode2 2 = " + drawNode2.isInsideBounds());
			});
			
			DrawNode circle = (DrawNode) DrawNode.create().addTo(this);
			circle.drawCircle(0, 0, 50, Color.GREEN);
			circle.setScale(2f);
			circle.setPosition(800, 320);
			circle.scheduleUpdate();
			circle.setOnUpdateCallback((node, t)->{
				circle.setPositionY(circle.getPositionY() - 5);
				System.out.println("culling circle = " + circle.isInsideBounds());
			});
			
			DrawNode bezier2 = (DrawNode) DrawNode.create().addTo(this);
			bezier2.drawQuadBezier(0, 0, 100, 150, 300, 100, Color.WHITE);
			bezier2.setPosition(600, 100);
			bezier2.scheduleUpdate();
			bezier2.setOnUpdateCallback((node, t)->{
				bezier2.setPositionX(bezier2.getPositionX() + 5);
				System.out.println("culling bezier = " + bezier2.isInsideBounds());
			});
		}		
	}
	
}
