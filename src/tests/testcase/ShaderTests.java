package tests.testcase;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
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
			spriteShader = Sprite.create("powered.png");
			spriteShader.setPosition(300, 320);
			addChild(spriteShader);
			
			spriteNormal = Sprite.create("powered.png");
			spriteNormal.setPosition(800, 320);
			addChild(spriteNormal);
				
			ShaderProgram shader = GLProgramCache.getInstance().loadGLProgramFromStringAndFile(
					GLProgramCache.getInstance().getSpriteBatchDefaultProgram().getVertexShaderSource(), 
					"shader/ice.fragment.glsl");
			spriteShader.setShaderProgram(shader);
		}
		
		public String subtitle() {
			return "shader test";
		}
	}
}
