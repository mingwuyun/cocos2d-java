package com.cocos2dj.module.visui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileChooser.Mode;

/**
 * VisUIhelper.java
 * <p>
 * 
 * @author Copyright(c) 2017 xujun
 */
public class VisUIHelper {
	
	public static VisScrollPane warpScrollPane(Actor widget, float width, float height) {
		VisScrollPane scrollPane = new VisScrollPane(widget);
		scrollPane.setFlickScroll(false);
		scrollPane.setFadeScrollBars(false);
		scrollPane.setSize(width, height);
		return scrollPane;
	}
	
	public static VisTable createTableWithDefaultBg() {
		VisTable table = new VisTable();
		table.background("window-bg");
		return table;
	}
	
	public static VisWindow createWindow(String windowName, float width, float height, boolean closeButton) {
		VisWindow ret = new VisWindow(windowName);
		ret.setSize(width, height);
		TableUtils.setSpacingDefaults(ret);
		if(closeButton) {
			ret.addCloseButton();
		}
		return ret;
	}
	
	public static FileChooser createOpenFileChooser(float width, float height) {
		FileChooser chooser;
		FileChooser.setDefaultPrefsName("com.kotcrab.vis.ui.test.manual");
		FileChooser.setSaveLastDirectory(true);
		chooser = new FileChooser(Mode.OPEN);
		chooser.setSelectionMode(FileChooser.SelectionMode.FILES_AND_DIRECTORIES);
		chooser.setMultiSelectionEnabled(true);
		chooser.setFavoriteFolderButtonVisible(true);
		
		chooser.setSize(width, height);
		return chooser;
	}
	
	public static FileChooser createSaveFileChooser(float width, float height) {
		FileChooser chooser;
		FileChooser.setDefaultPrefsName("com.kotcrab.vis.ui.test.manual");
		FileChooser.setSaveLastDirectory(true);
		chooser = new FileChooser(Mode.SAVE);
		chooser.setSelectionMode(FileChooser.SelectionMode.FILES_AND_DIRECTORIES);
		chooser.setMultiSelectionEnabled(true);
		chooser.setFavoriteFolderButtonVisible(true);
		
		chooser.setSize(width, height);
		return chooser;
	}
	
	
}
