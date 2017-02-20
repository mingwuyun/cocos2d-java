package tests.testcase;

import com.badlogic.gdx.files.FileHandle;
import com.cocos2dj.module.visui.ModuleVisUI;
import com.cocos2dj.module.visui.VisUIHelper;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileTypeFilter;
import com.kotcrab.vis.ui.widget.file.StreamingFileChooserListener;
import tests.TestCase;
import tests.TestSuite;

/**
 * VisUI 测试
 * 
 * @author xujun
 */
public class VisUITests extends TestSuite {
	
	public VisUITests() {
		addTestCase("FileChooser", ()->{return new FileChooserTest();});
	}
	
	static class VisUITestDemo extends TestCase {
		protected ModuleVisUI	_visUI;
		public void onEnter() {
			super.onEnter();
			_visUI = createModule(ModuleVisUI.class);
		}
	}
	
	static class FileChooserTest extends VisUITestDemo {
		
		FileChooser chooser;
		
		public void onEnter() {
			super.onEnter();
			
			chooser = VisUIHelper.createOpenFileChooser(800, 450);
			_visUI.getGdxUI().addUIDefault(chooser);
			
			chooser.setListener(new StreamingFileChooserListener() {
				@Override
				public void selected (FileHandle file) {
					switch(chooser.getMode()) {
					case OPEN:
						System.out.println("openFile:" + file.name());
						break;
					case SAVE:
						System.out.println("saveFile:" + file.name());
						break;
					}
				}
				public void canceled() {
					System.out.println("cancel");
				}
			});
			final FileTypeFilter typeFilter = new FileTypeFilter(true);
			typeFilter.addRule("Image files (*.png, *.jpg, *.gif)", "png", "jpg", "gif");
			typeFilter.addRule("Text files (*.txt)", "txt");
			typeFilter.addRule("Audio files (*.mp3, *.wav, *.ogg)", "mp3", "wav", "ogg");
			chooser.setFileTypeFilter(typeFilter);
			
			VisWindow window = VisUIHelper.createWindow("testWindow", 600, 388, true);
			_visUI.getGdxUI().addUIDefault(window);
		}
		
		
		public String subtitle() {
			return "anchorPoint and children";
		}
	}
}
