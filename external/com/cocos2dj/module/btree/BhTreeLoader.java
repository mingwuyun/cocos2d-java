package com.cocos2dj.module.btree;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.cocos2dj.macros.CCLog;
import com.cocos2dj.module.btree.BhTreeModel.StructBHTNode;

public class BhTreeLoader<T> {
	
	static final String TAG = "BhTreeLoader";
	
	public BhTree<T> parse(String path) {
		return parseJsonModelAndCreate(
				new JsonReader().parse(Gdx.files.internal(path)).child);
	}
	
	public BhTree<T> parseJsonModelAndCreate(JsonValue jv_root) {
		BhTree<T> bht = new BhTree<T>();
		BhTreeModel model = parseJsonModel(jv_root);
		CCLog.debug(TAG, model.toString());
		bht.setup(model);
		return bht;
	}
	
	public BhTreeModel parseJsonModel(JsonValue jv_root) {
		BhTreeModel model = new BhTreeModel();
		model.root = parseBHTNode(0, jv_root);
		return model;
	}
	
	final StructBHTNode parseBHTNode(int depth, JsonValue jv) {
		String name = jv.name;
		String args = jv.getString("args", null);
		String key = jv.getString("key", null);
		StructBHTNode BHTNode = new StructBHTNode();
		BHTNode.type = name;
		BHTNode.args = args;
		BHTNode.key = key;
		BHTNode.depth = depth;
		
		int childrenCount = jv.size;
		if(args != null) {
			childrenCount -= 1;
		}
		if(key != null) {
			childrenCount -= 1;
		}
		if(childrenCount > 0) {
			BHTNode.children = new StructBHTNode[childrenCount];
		}
		
		for(int i = 0, count = 0; i < jv.size; ++i) {
			JsonValue jv_child = jv.get(i);
			if(jv_child.name == null) {
				CCLog.error(TAG, "child name cannot be null!");
				throw new GdxRuntimeException("child name cannot be null!");
			}
			if(jv_child.name.equals("args") || jv_child.name.equals("key")) {
				continue;
			}
			
			BHTNode.children[count++] = parseBHTNode(depth+1, jv_child);
		}
		return BHTNode;
	}
}
