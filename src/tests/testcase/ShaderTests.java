package tests.testcase;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.cocos2dj.module.base2d.framework.common.MathUtils;
import com.cocos2dj.renderer.GLProgramCache;
import com.cocos2dj.s2d.Sprite;

import tests.TestCase;
import tests.TestSuite;

/**
 * 着色器测试
 * 
 * @author xujun
 */
public class ShaderTests extends TestSuite {
	
	public ShaderTests() {
		addTestCase("ShaderTest1", ()->{return new ShaderTest1();});
	}
	
	static class ShaderTestBase extends TestCase {
		
	}
	
	static class ShaderTest1 extends ShaderTestBase {
		
		Sprite spriteShader;
		Sprite spriteNormal;
		
		public void onEnter() {
			super.onEnter();
			
//			for(int i = 0; i < 2000; ++i) {
//				spriteNormal = Sprite.create("powered.png");
//				spriteNormal.setContentSize(20, 20);
//				spriteNormal.setPosition(MathUtils.randomFloat(0, 1200), MathUtils.randomFloat(0, 650));
//				addChild(spriteNormal);
//			}
			
			spriteShader = Sprite.create("powered.png");
			spriteShader.setPosition(300, 320);
			addChild(spriteShader);
			
			spriteNormal = Sprite.create("powered.png");
			spriteNormal.setPosition(800, 320);
			addChild(spriteNormal);
				
			ShaderProgram shader = GLProgramCache.getInstance().loadGLProgramFromStringAndFile(
					GLProgramCache.getInstance().getSpriteBatchDefaultProgram().getVertexShaderSource(), 
					"shader/flip.fragment.glsl");
//			shader.setUniformi("", 1);
//			spriteShader.setShaderProgram(shader);
			
//			spriteShader.getGLProgramShader().set;
			
			
			scheduleUpdate();
		}
		
		public boolean update(float dt) {
//			for(int i = 0; i < 1000000; ++i) {
//				Math.sqrt(Math.random());
//			}
			return false;
		}
		
		public String subtitle() {
			return "shader test";
		}
	}
}
