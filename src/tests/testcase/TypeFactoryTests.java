package tests.testcase;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.cocos2dj.module.base2d.ComponentPhysics;
import com.cocos2dj.module.base2d.ModuleBase2d;
import com.cocos2dj.module.gdxui.GdxUIConsole;
import com.cocos2dj.module.gdxui.GdxUIConsole.ConsoleHandle;
import com.cocos2dj.module.gdxui.GdxUIDebugInfo;
import com.cocos2dj.module.typefactory.ModuleTypeFactory;
import com.cocos2dj.module.typefactory.NodeFactory;
import com.cocos2dj.module.typefactory.NodePools;
import com.cocos2dj.module.typefactory.NodeType;
import com.cocos2dj.module.typefactory.NodeType.InstanceType;
import com.cocos2dj.module.typefactory.PoolListener;
import com.cocos2dj.protocol.INode;
import com.cocos2dj.s2d.DrawNode;
import com.cocos2dj.s2d.Node;

import tests.TestCase;
import tests.TestSuite;

/**
 * NodePool TypeFactory 相关测试
 * 
 * @author xujun
 */
public class TypeFactoryTests extends TestSuite {
	
	public TypeFactoryTests() {
		addTestCase("test", ()->{return new NodeTest2();});
	}
	
	static class TypeTestDemo extends TestCase {
		
		public void onEnter() {
			super.onEnter();
			GdxUIDebugInfo debug = (GdxUIDebugInfo) _gdxui.getUIStage(GdxUIDebugInfo.class);
			debug.addDebugListener(()->{
				return NodePools.getPoolsState();
			});
		}
		
		public void onExit() {
			super.onExit();
		}
	}
	
	static class NodeTest2 extends TypeTestDemo {
		
		//测试node池对象
		public static class TestPoolNode implements NodeProxy {
			Node root;
			@Override
			public void onSleep(INode n) {
				System.out.println("sleep");
			}

			@Override
			public void onAwake(INode n) {
				System.out.println("awake");
				phy.setAccelerateY(-1f);
				phy.setVelocityX(MathUtils.random(-3f, 3f));
			}

			@Override
			public void onEnter(INode n) {
				System.out.println("enter");
				root = (Node) n;
				
				DrawNode draw = new DrawNode();
				draw.drawRect(0, 0, 100, 100, null);
				root.addChild(draw);
				
				phy = base2d.createDynamicObjectWithAABB(0, 0, 100, 100).bindNode(root);
			}

			@Override
			public void onExit(INode n) {
				System.out.println("exit");
			}

			ComponentPhysics phy;
			
			@Override
			public void onUpdate(INode n, float dt) {
			}
		}
		
		Array<Node> 	nodes = new Array<>();
		static ModuleBase2d	base2d;
		ModuleTypeFactory	typeFactory;
		
		PoolListener poolListener = new PoolListener() {
			@Override
			public boolean onObjectEvent(PoolEvent eventType, Node obj) {
				System.out.println("evnt trigger : "+ eventType);
				return false;
			}
		};
		
		public void onEnter() {
			super.onEnter();
			
			base2d = createModule(ModuleBase2d.class);
			typeFactory = createModule(ModuleTypeFactory.class);
			
			GdxUIConsole console = _gdxui.createConsole();
			console.addConsoleHandle(new ConsoleHandle("typeFactoryTest") {
				@Override
				public String handle(String cmd) {
					if(cmd.equals("gc")) {
						System.gc();
					}
					return null;
				}
			});
			
			NodeType nodeType = NodeType.create("testNodeType1").
			setInstanceType(InstanceType.ADDING_POOL).
			setParent(this).
			setNodeProxy(TestPoolNode.class);//.addPoolListener(poolListener);
			
			nodeType.setPoolListener(poolListener);
			
			typeFactory.addNodeType(nodeType);
			//end
			
			base2d.createStaticObjectWithAABBWorld(0, 0, 1200, 100);
			DrawNode ground = (DrawNode) DrawNode.create().addTo(this);
			ground.drawSolidRect(0, 0, 1200, 100, null);
			
			
			schedule((t)->{
//				System.out.println("schedule 1");
				Node root = nodeType.getInstance();
				root.setPosition(600 + MathUtils.random(-500, 500), 320 + MathUtils.random(-200, 200));
				nodes.add(root);
				return false;
			}, .5f);
//			
			schedule((t)->{
				for(Node node : nodes) {
					node.pushBack();
				}
//				unscheduleAllCallbacks();
				nodes.clear();
				return false;
			}, 5f);
		}
		
		
		public String subtitle() {
			return "anchorPoint and children";
		}
	}
}
