package com.cocos2dj.module.btree;

public class BhTreeModel {
	
	public static class StructBHTNode {
		public String type;
		public String key;
		public String args;
		public StructBHTNode[] children;
		public int depth;
		final String createTabs() {
			StringBuilder sb = new StringBuilder();
			for(int i = 0; i < depth; ++i) {
				for(int j = 0; j < 4; ++j) {
					sb.append(' ');
				}
			}
			return sb.toString();
		}
		public String toString() {
			StringBuilder sb = new StringBuilder();
			String tabs = createTabs();
			sb.append(tabs).append("[").append(type).append("], ");
			if(key != null) {
				sb.append("key = ").append(key).append(", ");
			}
			if(args != null) {
				sb.append("args = ").append(args);//.append("\n");
			}
			sb.append('\n');
			if(children != null) {
				for(StructBHTNode node : children) {
					sb.append(node.toString());
				}
			}
//			sb.append('\n');
			return sb.toString();
		}
	}
	
	public StructBHTNode root;
	
	public String toString() {
		return root.toString();
	}
}
