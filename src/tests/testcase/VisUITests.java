package tests.testcase;

import com.badlogic.gdx.files.FileHandle;
import com.cocos2dj.module.visui.ModuleVisUI;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileTypeFilter;
import com.kotcrab.vis.ui.widget.file.StreamingFileChooserListener;
import com.kotcrab.vis.ui.widget.file.FileChooser.Mode;

import tests.TestCase;
import tests.TestSuite;

/**
 * 节点相关测试
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
			
			FileChooser.setDefaultPrefsName("com.kotcrab.vis.ui.test.manual");
			FileChooser.setSaveLastDirectory(true);
			chooser = new FileChooser(Mode.OPEN);
			chooser.setSelectionMode(FileChooser.SelectionMode.FILES_AND_DIRECTORIES);
			chooser.setMultiSelectionEnabled(true);
			chooser.setFavoriteFolderButtonVisible(true);
			chooser.setSize(800, 450);
//			chooser.setSize(450, 250);
			
			_visUI.getGdxUI().addUIDefault(chooser);
			
			chooser.setListener(new StreamingFileChooserListener() {
				@Override
				public void selected (FileHandle file) {
//					SLog.debug(TAG, "select file>>>>>>>" + file.path() + " mode = " + chooser.getMode());
					switch(chooser.getMode()) {
					case OPEN:
//						BlockBuildPlugin.instance().op_openFile(file);
						break;
					case SAVE:
//						BlockBuildPlugin.instance().op_saveFile(file);
						break;
					}
				}
				
				public void canceled() {
//					BlockBuildPlugin.instance().op_cancelFileChooser();
				}
				
			});
			
			
			final FileTypeFilter typeFilter = new FileTypeFilter(true);
			typeFilter.addRule("Image files (*.png, *.jpg, *.gif)", "png", "jpg", "gif");
			typeFilter.addRule("Text files (*.txt)", "txt");
			typeFilter.addRule("Audio files (*.mp3, *.wav, *.ogg)", "mp3", "wav", "ogg");
			chooser.setFileTypeFilter(typeFilter);
//			_visUI.getGdxUI().addUIStage(stage, boolean);
		}
		
		
		public String subtitle() {
			return "anchorPoint and children";
		}
	}
	
//	static class NodeTest4 extends NodeTestDemo {
//		public void onEnter() {
//			super.onEnter();
////			System.out.println("debug enter >>>>>> ");
//		}
//		
//		public String subtitle() {
//			return "tags";
//		}
//	}
}
