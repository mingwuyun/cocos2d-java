package tests.testcase;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.cocos2dj.base.Director;
import com.cocos2dj.basic.BaseInput;
import com.cocos2dj.module.base2d.Base2dNode;
import com.cocos2dj.module.base2d.ComponentPhysics;
import com.cocos2dj.module.base2d.ComponentPhysics.ContactCallback;
import com.cocos2dj.module.base2d.ModuleBase2d;
import com.cocos2dj.module.base2d.framework.PhysicsGenerator;
import com.cocos2dj.module.base2d.framework.PhysicsObject;
import com.cocos2dj.module.base2d.framework.callback.OnContactCallback;
import com.cocos2dj.module.base2d.framework.collision.Contact;
import com.cocos2dj.module.base2d.framework.collision.ContactCollisionData;
import com.cocos2dj.module.base2d.framework.collision.Polygon;
import com.cocos2dj.module.base2d.framework.common.MathUtils;
import com.cocos2dj.protocol.INode;
import com.cocos2dj.s2d.ActionInterval.JumpBy;
import com.cocos2dj.s2d.ActionInterval.Repeat;
import com.cocos2dj.s2d.Camera;
import com.cocos2dj.s2d.DrawNode;
import com.cocos2dj.s2d.Node;
import com.cocos2dj.s2d.Scene;
import com.cocos2dj.s2d.Sprite;

import tests.TestCase;
import tests.TestSuite;

/**
 * Base2D 物理引擎测试
 * 
 * @author xu jun
 */
public class Base2dTests extends TestSuite {
	
	public Base2dTests() {
		addTestCase("Base2dNodeTest", ()->{return new Base2DNodeTest();});
		addTestCase("GeneratorTest", ()->{return new Base2DGeneratorTest();});
		addTestCase("Base2DTest2", ()->{return new Base2DTest2();});
		addTestCase("Base2DTest1", ()->{return new Base2DTest1();});
	}
	
	static class Base2DDemo extends TestCase {
		
	}
	
	
	/////////////////////
	
	//Base2D运行测试 测试了碰撞监听和休眠
	// test contactListener and sleep/awake
	static class Base2DTest1 extends Base2DDemo {
		
		Sprite spr1;		ComponentPhysics phy1;
		Sprite spr2;		ComponentPhysics phy2;
		Sprite sprGround;	ComponentPhysics phyGround;		//地面
		
		ModuleBase2d moduleBase2d;
		
		public void onEnter() {
			super.onEnter();
			
			// 添加moduleBase2d插件
			moduleBase2d = (ModuleBase2d) addModule(new ModuleBase2d());	
			
			spr1 = Sprite.create("powered.png"); //addChild(spr1);
			spr2 = Sprite.create("powered.png"); 
			addChild(spr2);
			sprGround = Sprite.create("powered.png"); 
			addChild(sprGround);
			
			Node spr1Node = Node.create();
			addChild(spr1Node);
			spr1Node.addChild(spr1);
			spr1Node.setPosition(200, 200);
			
			phy1 = moduleBase2d.createDynamicObjectWithAABB(-50, -50, 50, 50).bindNode(spr1);
			//spr1.addComponent(phy1);
			spr1.setContentSize(100, 100);
			phy1.setVelocityX(5);
			phy1.setAccelerateY(-0.5f);
			spr1.setPosition(400, 400);		//位置同步，设置node会自动同步到phy对象

			
			phy2 = moduleBase2d.createDynamicObjectWithAABB(-50, -50, 50, 50).bindNode(spr2);
			spr2.setContentSize(100, 100);
			phy2.setAccelerateY(-0.5f);
			phy2.setPosition(800, 300);		//直接设置phy也可以
			
			phyGround = moduleBase2d.createStaticObjectWithAABB(0, 0, 1200, 100);
			sprGround.setRect(0, 0, 1200, 100);
			
			schedule((t)->{
				phy1.setPosition(500, 200);
				return false;
			}, 2);
			schedule((t)->{
				//唤醒 phy2
				phy2.setSleep(false);
				spr2.setVisible(true);
				phy2.setPosition(800, 300);
				return false;
			}, 3);
			
			//碰撞监听
			phy1.setContactCallback(new ContactCallback() {
				@Override
				public void onContactDestroyed(ComponentPhysics self, ComponentPhysics other, Vector2 MTD, ContactCollisionData data) {
					System.out.println("onContactDestroyed");
				}
				@Override
				public void onContactPersisted(ComponentPhysics self, ComponentPhysics other, Vector2 MTD, ContactCollisionData data) {
					
				}
				@Override
				public void onContactCreated(ComponentPhysics self, ComponentPhysics other, Vector2 MTD, ContactCollisionData data) {
					Node otherNode = other.getOwner();
					//phy2休眠
					if(otherNode != null) {
						otherNode.setVisible(false);
						other.setSleep(true);
					}
				}
			});
		}
		
		
		public String subtitle() {
			return "contactListener and sleep/awake";
		}
	}
	
	/////////////////////
	//TODO some one
	static class Mario extends InputAdapter implements INode.NodeCallback {
		Node 				aim;
		DrawNode			debugDraw;
		ComponentPhysics	body;
		
		static final float WIDTH = 50;
		static final float HEIGHT = 50;
		static final float SPEED = 10f;
		static final float JUMP = 20f;
		
		boolean leftFlag = false;
		boolean rightFlag = false;
		boolean jumpFlag = false;
		
		public void init(Scene scene) {
			ModuleBase2d base2d = (ModuleBase2d) scene.getModule(ModuleBase2d.class);
			aim = Base2dNode.create().addTo(scene);
			aim.setNodeCallback(this);
			aim.scheduleUpdate();
			
			debugDraw = (DrawNode) DrawNode.create().addTo(aim);
			debugDraw.drawSolidRect(-WIDTH/2, 0, WIDTH/2, HEIGHT, Color.GREEN);
			debugDraw.setPosition(0, 0);
			body = base2d.createDynamicObjectWithAABB(-WIDTH/2, 0, WIDTH/2, HEIGHT).bindNode(aim);
			body.setAccelerateY(-2f);
			body.setFriction(1.0f);
			body.setStaticFriction(1.0f);
			
			BaseInput.instance().addInputProcessor(this);
			
			body.setContactCallback(new ContactCallback() {
				public void onContactCreated(ComponentPhysics self, ComponentPhysics other, Vector2 MTD,
						ContactCollisionData data) {
					
				}
				public void onContactPersisted(ComponentPhysics self, ComponentPhysics other, Vector2 MTD,
						ContactCollisionData data) {
					
				}
				public void onContactDestroyed(ComponentPhysics self, ComponentPhysics other, Vector2 MTD,
						ContactCollisionData data) {
				}
			});
		}
		
		@Override
		public void onEnter(INode n) {
			
		}

		@Override
		public void onExit(INode n) {
			BaseInput.instance().removeInputProcessor(this);
		}

		private OnContactCallback contactHandle = new OnContactCallback() {
			@Override
			public boolean onContact(Contact c, PhysicsObject other) {
				if(other.getPosition().y < body.getPosition().y) {
					if(MathUtils.abs(c.MTD.y) > 0.01f) {
						canJump = true;
						return true;
					}
				}
				return false;
			}
		};
		
		boolean canJump = false;
		boolean lastJump;
		
		@Override
		public void onUpdate(INode n, float dt) {
			lastJump = canJump;
			canJump = false;
			body.forContactList(contactHandle);
			
			if(lastJump != canJump) {
				System.out.println("jump ok" + canJump);
			}
			
			if(leftFlag) {
				body.setVelocityX(-SPEED);
				if(body.getVelocity().y <= 0 && body.getVelocity().y > -1f) {
					body.setVelocityY(-2f);		//添加一个初始速度，确保贴在斜面上
				}
			} else if(rightFlag) {
				body.setVelocityX(SPEED);
				if(body.getVelocity().y <= 0 && body.getVelocity().y > -1f) {
					body.setVelocityY(-2f);		//添加一个初始速度，确保贴在斜面上
				}
			} else {
				body.setVelocityX(0f);
			}
			
			if(jumpFlag) {
				if(canJump) {
					body.setVelocityY(JUMP);
				}
			}
		}

		private static Vector2 temp = new Vector2();
		public boolean touchDown(int x, int y, int pointer, int button) {
			Vector2 ret = Director.getInstance().convertToGL(temp.set(x, y));
			if(ret.x < 300) {
				leftFlag = true;
			} else if(ret.x < 600) {
				rightFlag = true;
			} else {
				jumpFlag = true;
			}
			return false;
		}

		public boolean touchUp(int x, int y, int pointer, int button) {
			Vector2 ret = Director.getInstance().convertToGL(temp.set(x, y));
			if(ret.x < 300) {
				leftFlag = false;
			} else if(ret.x < 600) {
				rightFlag = false;
			} else {
				jumpFlag = false;
			}
			return false;
		}

		public boolean keyDown(int keycode) {
			switch(keycode) {
			case Keys.A:case Keys.LEFT: leftFlag = true; break;
			case Keys.D:case Keys.RIGHT:rightFlag = true;break;
			case Keys.SPACE:jumpFlag = true;break;
			}
			return false;
		}
		
		public boolean keyUp(int keycode) {
			switch(keycode) {case Keys.A:case Keys.LEFT:leftFlag = false;break;
			case Keys.D:case Keys.RIGHT:rightFlag = false;break;
			case Keys.SPACE:jumpFlag = false;break;
			}
			return false;
		}
	}

	/////////////////////////////////////////////
	//Base2D运行测试 测试 多边形和地面检测
	// test polygon and ground check
	static class Base2DTest2 extends Base2DDemo {
		
		ModuleBase2d 	moduleBase2d;
		Mario 			mario;
		
		public void onEnter() {
			super.onEnter();
			
			// 添加moduleBase2d插件
			moduleBase2d = createModule(ModuleBase2d.class);	
			
			DrawNode ground = (DrawNode) DrawNode.create().addTo(this);
			ground.drawRect(0, 0, 1500, 100, null);
			moduleBase2d.createStaticObjectWithAABBWorld(0, 0, 1500, 100);
			
			mario = new Mario();
			mario.init(this);
			
			mario.aim.setPosition(50, 300);
			
			DrawNode groundPolygon = (DrawNode) DrawNode.create().addTo(this);
			
			float[] points = new float[]{
				-400, -10,
				400, -10,
				400, 200
			};
			groundPolygon.drawPolygon(points, null);
			groundPolygon.setPosition(500, 100);
			Polygon shape = new Polygon();
			shape.setPoints(points);
			
			PhysicsObject groundBody = moduleBase2d.createStaticObject(shape, 500, 100);
			groundBody.setStaticFriction(1.0f);
			groundBody.setFriction(1.0f);
			
			DrawNode ground2 = (DrawNode) DrawNode.create().addTo(this);
			ground2.drawSolidRect(0, 0, 500, 100, null);
			ground2.setPosition(1000, 180);
			moduleBase2d.createStaticObjectWithAABBWorld(1000, 180, 1500, 280);
			
			camera = getDefaultCamera();
			scheduleUpdate();
		}
		Camera camera;
		public boolean update(float dt) {
			super.update(dt);
			camera.setPosition(mario.aim.getPositionX() - 500, mario.aim.getPositionY() - 220);
			return false;
		}
		
		public String subtitle() {
			return "contactListener and sleep/awake";
		}
	}
	
	
	
	/////////////////////////////////////////////
	//Base2D运行测试 测试 Generator
	// test generator check
	static class Base2DGeneratorTest extends Base2DDemo {
		
		ModuleBase2d 	moduleBase2d;
		Mario 			mario;
		
		public void onEnter() {
			super.onEnter();
			
			// 添加moduleBase2d插件
			moduleBase2d = createModule(ModuleBase2d.class);	
			
			DrawNode ground = (DrawNode) DrawNode.create().addTo(this);
			ground.drawRect(0, 0, 1500, 100, null);
			moduleBase2d.createStaticObjectWithAABBWorld(0, 0, 1500, 100);
			
			mario = new Mario();
			mario.init(this);
//			
			mario.aim.setPosition(50, 300);
			
			
			PhysicsGenerator testG = mario.body.addGenerator(PhysicsGenerator.TEST, true);
			scheduleOnce((t)->{
				testG.stop();
				return false;
			}, 2);
			
			
			
			Node node = Node.create().addTo(this);
			DrawNode test = (DrawNode) DrawNode.create().addTo(node);
			test.drawSolidRect(0, 0, 100, 100, Color.GREEN);
			ComponentPhysics phy = moduleBase2d.createDynamicObjectWithAABB(0, 0, 100, 100).bindNode(node);
			
			//部分action对physics有效
			node.setPosition(100, 200);
			JumpBy jb = JumpBy.create(2f, 0, 0, 300, 1);
			node.runAction(jb);
			phy.setVelocityX(2f);
		}
		
		public boolean update(float dt) {
			super.update(dt);
			return false;
		}
		
		public String subtitle() {
			return "contactListener and sleep/awake";
		}
	}
	
	///////////////////////////////////
	// Base2DNode测试	
	static class Base2DNodeTest extends Base2DDemo {
		
		ModuleBase2d 	moduleBase2d;
		Mario 			mario;
		
		public void onEnter() {
			super.onEnter();
			
			// 添加moduleBase2d插件
			moduleBase2d = createModule(ModuleBase2d.class);	
			
			DrawNode ground = (DrawNode) DrawNode.create().addTo(this);
			ground.drawRect(0, 0, 1500, 100, null);
			moduleBase2d.createStaticObjectWithAABBWorld(0, 0, 1500, 100);
			
			mario = new Mario();
			mario.init(this);
//			
			mario.aim.setPosition(50, 300);
			
			
			PhysicsGenerator testG = mario.body.addGenerator(PhysicsGenerator.TEST, true);
			scheduleOnce((t)->{
				testG.stop();
				return false;
			}, 2);
			
			
			//如果绑定物理对象，建议使用Base2dNode绑定而不是Node
			Node node = Base2dNode.create().addTo(this);
			DrawNode test = (DrawNode) DrawNode.create().addTo(node);
			test.drawSolidRect(0, 0, 100, 100, Color.GREEN);
			ComponentPhysics phy = moduleBase2d.createDynamicObjectWithAABB(0, 0, 100, 100).bindNode(node);
			
			//部分action对physics有效
			node.setPosition(100, 150);
			JumpBy jb = JumpBy.create(2f, 0, 0, 300, 1);
			node.runAction(Repeat.create(jb, 5));
			phy.setVelocityX(2f);
		}
		
		public boolean update(float dt) {
			super.update(dt);
			return false;
		}
		
		public String subtitle() {
			return "contactListener and sleep/awake";
		}
	}
}
