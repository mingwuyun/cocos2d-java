package com.cocos2dj.utils;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.Rectangle;

/**
 * �Ĳ����ṹ<p>
 * 
 * ��������Renderable������Ⱦ ����insert������Ӷ���
 * ����build���������Ĳ���<p>
 * 
 * ����֮���Ĳ����ṹ����仯������µ�����ֻ����½ڵ�����
 * ����query���ڲ��ҷ�Χ�ڶ���<p>
 * 
 * 
 * ʹ�õ�����:
 * <pre>
 * QuadTree qt = new QuadTree();
 * qt.setMaxDepth(5);		//�趨�Ĳ���������Ϊ5
 * qt.setMaxObject(8);		//�趨�����������Ϊ8
 * 
 * for(QuadTreeObject r : objList) {		//��Ӷ���
 * 	qt.insert(r);
 * }
 * 
 * qt.build();		//�����Ĳ���
 * 
 * qt.query(rect, queryListener);	//��ѯ
 * 
 * </pre>
 * @author xu jun
 * Copyright (c) 2015. All rights reserved.
 */
public class QuadTree {
	
	public static interface IQuadTreeObject {
		public Rectangle getRectangle();
	}
	
	/**����Ľڵ� */
//	public static interface IQuadTreeNode {
//		public void build(int currDepth);
//		public void query();
//		public boolean insert(IQuadTreeObject obj);
//		public void destroy();
//	}
	
	/**�߽ڵ� */
	public static class QuadTreeSide {
		
		private float min, max;	//�ߵķ�Χ
		private int type;		//�ڵ�����
		private float centerX, centerY;//�ָ����ĵ�
		
		private Array<IQuadTreeObject> objs = new Array<IQuadTreeObject>();
		private QuadTreeSide[] children;
		final QuadTree tree;
		
		
		public QuadTreeSide(QuadTree tree, int type, float centerX, float centerY, float min, float max) {
			this.tree = tree;
			this.type = type;
			
			this.max = max;
			this.min = min;
			this.centerX = centerX;
			this.centerY = centerY;
		}
		
		
		private void _addObject(IQuadTreeObject obj, int currDepth) {
			objs.add(obj);
		}
		
		public void build(int currDepth) {
//			System.out.println("QuadTree side build begin---------" + objs.size);
			if(objs.size > maxObject && currDepth <= tree.maxDepth) {
//				System.out.println("QuadTree side build---------");
				if(children == null) {
					children = new QuadTreeSide[2];
					switch(type) {
					case 0:
					case 2:
						//x
						children[0] = new QuadTreeSide(tree, 2, (min+max)/2, this.centerY, min, (min+max)/2);
						children[1] = new QuadTreeSide(tree, 0, (min+max)/2, this.centerY, (min+max)/2, max);
						break;
					case 1:
					case 3:
						//y
						children[0] = new QuadTreeSide(tree, 3, this.centerX, (min+max)/2, min, (min+max)/2);
						children[1] = new QuadTreeSide(tree, 1, this.centerX, (min+max)/2, (min+max)/2, max);
						break;
					}
				}
				
				//�������������
				for(int n = objs.size - 1; n >= 0; --n) {
					IQuadTreeObject obj = objs.get(n);
					
					for(int i = 0; i < children.length; ++i) {
						if(children[i].insert(obj, currDepth + 1)) {
							//��ӳɹ�
//							System.out.println("###������obj" + n + obj + " ��ӵ�[�߶�]�ڵ� [" + i + "]");
							objs.removeIndex(n);
							break;
						}
					}
				}
				
				final int nextDepth = currDepth + 1;
				//�ӽڵ㹹����
				for(int i = 0; i < children.length; ++i) {
					children[i].build(nextDepth);
				}
				
				System.out.println("QuadTree side build end --------- last obj = " + objs.size);
			}
		}
		
		/**��Ӷ���
		 * @return �ɹ�����true ʧ�ܷ���false */
		public boolean insert(IQuadTreeObject obj, int currDepth) {
//			System.out.println("caonima" + obj.getBoundsString() + type + " " + centerX + " " + centerY);
			final Rectangle rect = obj.getRectangle();
			switch(type) {
			case 0:
				if(rect.y + rect.height >= centerY && rect.y < centerY) {
					if(rect.x > centerX) {
						_addObject(obj, currDepth + 1);
						return true;
					}
				}
				break;
			case 1:
				if(rect.x + rect.width >= centerX && rect.x < centerX) {
					if(rect.y > centerX) {
						_addObject(obj, currDepth + 1);
						return true;
					}
				}
				break;
			case 2:
				if(rect.y + rect.height >= centerY && rect.y < centerY) {
					if(rect.x + rect.width < centerX) {
						_addObject(obj, currDepth + 1);
						return true;
					}
				}
				break;
			case 3:
				if(rect.x + rect.width >= centerX && rect.x < centerX) {
					if(rect.y + rect.height < centerY) {
						_addObject(obj, currDepth + 1);
						return true;
					}
				}
				break;
			}
			return false;
		}
		
		/**�߿ռ�����*/
		public void query(Rectangle rect, Array<IQuadTreeObject> ret) {
//			System.out.println("query side begin ---------- ");
//			System.out.println("rect = " + rect + " type = " + type + "min, max, centerX, centerY = " 
//					+ min + " " + max + " " + centerX + " " + centerY);
			boolean flag = false;
			switch(type) {
			case 0:
				if(rect.contains(min, centerY)) {
					flag = true;
				}
				break;
			case 1:
				if(rect.contains(centerX, min)) {
					flag = true;
				}
				break;
			case 2:
				if(rect.contains(max, centerY)) {
					flag = true;
				}
				break;
			case 3:
				if(rect.contains(centerX, max)) {
					flag = true;
				}
				break;
			}
			
			if(!flag) {return;}
			
//			System.out.println("query side success ---------- ");
			if(children != null) {
//				System.out.println("query child side begin ---------- ");
				children[0].query(rect, ret);
				children[1].query(rect, ret);
//				System.out.println("query child side end ---------- ");
			}
			
			ret.addAll(objs); 
//			System.out.println(this + " ret = " + objs.size);
		}
		
		public void visit(int depth) {
			
			StringBuilder sb = new StringBuilder();
			for(int i = 0; i < depth; ++i) {
				sb.append("	");
			}
			sb.append("[side] depth = " + depth + " objs = " + objs.size);
			System.out.println(sb);
			
			if(children != null) {
				children[0].visit(depth + 1);
				children[1].visit(depth + 1);
			}
			
		}
	}
	
	
	//���
	public static class QuadTreeNode {
		
		private float x0, y0, x1, y1;
		private float centerX, centerY;
		private int type = -1;
		Rectangle _rect;
		final QuadTree tree;
		
		private QuadTreeNode[] children; //= new QuadTreeNode[4];
		private QuadTreeSide[] sides; //=new QuadTreeSide[4];
		//��������һ�ֲ��ԣ���ȥȫ�����룬��Ҫ�ָ��ʱ��
		//�ٱ������󣬿��ܷ������Ӷ�����
		Array<IQuadTreeObject> objs = new Array<IQuadTreeObject>();
		
		
		
		public QuadTreeNode(QuadTree tree, float x0, float y0, float x1, float y1) {
			this(tree,-1, x0, y0, x1, y1);
		}
		
		public QuadTreeNode(QuadTree tree, int type, float x0, float y0, float x1, float y1) {
			this.tree = tree;
					
			this.x0 = x0;
			this.y0 = y0;
			this.x1 = x1;
			this.y1 = y1;
			_rect = new Rectangle(x0, y0, x1 - x0, y1 - y0);
			this.type = type;
			centerX = (x0 + x1) / 2;
			centerY = (y0 + y1) / 2; 
		}
		
		
		public boolean insert(IQuadTreeObject obj, int currDepth) {
			Rectangle rect = obj.getRectangle();
			switch(type) {
			case 0:
				if(rect.x > x0 && rect.y > y0) {
					objs.add(obj);
					return true;
				}
				break;
			case 1:
				if(rect.x + rect.width < x1 && rect.y > y0) {
					objs.add(obj);
					return true;
				}
				break;
			case 2:
				if(rect.x + rect.width < x1 && rect.y + rect.height < y1) {
					objs.add(obj);
					return true;
				}
				break;
			case 3:
				if(rect.x > x0 && rect.y + rect.height < y1) {
					objs.add(obj);
					return true;
				}
				break;
			}
			return false;
		}
		
		public void _addObject(IQuadTreeObject obj) {
			objs.add(obj);
		}
		
		/**���Ѿ�����Ķ��󹹽��Ĳ��� */
		public boolean build(int currDepth) {
//			System.out.println("depth = " + currDepth + "size = " + objs.size);
			if(objs.size >= maxObject && currDepth <= tree.maxDepth) {
				System.out.println("node insert : ��Ҫ�и�[" + centerX + " " + centerY+"]");
				if(children == null) {		
					children = new QuadTreeNode[4];
					children[0] = new QuadTreeNode(tree, 0, centerX, centerY, x1, y1);
					children[1] = new QuadTreeNode(tree, 1, x0, centerY, centerX, y1);
					children[2] = new QuadTreeNode(tree, 2, x0, y0, centerX, centerY);
					children[3] = new QuadTreeNode(tree, 3, centerX, y0, x1, centerY);			
				}
				if(sides == null) {	//�и��
					sides = new QuadTreeSide[4];
					sides[0] = new QuadTreeSide(tree, 0, centerX, centerY, centerX, x1);	//x +
					sides[1] = new QuadTreeSide(tree, 1, centerX, centerY, centerY, y1);	//y +
					sides[2] = new QuadTreeSide(tree, 2, centerX, centerY, x0, centerX);	//- x
					sides[3] = new QuadTreeSide(tree, 3, centerX, centerY, y0, centerY);	//- y
				}
				
				//�������������
				for(int n = objs.size - 1; n >= 0; --n) {
					IQuadTreeObject obj = objs.get(n);
					
					boolean ret = false;
					
					for(int i = 0; i < sides.length; ++i) {
						if(sides[i].insert(obj, currDepth)) {
							//��ӳɹ�
//							System.out.println("������obj" + n + obj + " ��ӵ�[�߶�]�ڵ� [" + i + "]");
							objs.removeIndex(n);
							ret = true;
							break;
						}
					}
					//��ӵ��߶νڵ�Ͳ��ü�����
					if(ret) {continue;}
					
					for(int i = 0; i < children.length; ++i) {
						if(children[i].insert(obj, currDepth)) {
							//��ӳɹ�
//							System.out.println("������obj" + n + obj +" ��ӵ�[�ռ�]�ڵ� [" + i + "]");
							objs.removeIndex(n);
							break;
						}
					}
				}
				
				//�ӽڵ㹹����
				final int nextDepth = currDepth + 1;
				for(int i = 0; i < sides.length; ++i) {
					sides[i].build(nextDepth);
				}
				for(int i = 0; i < children.length; ++i) {
					children[i].build(nextDepth);
				}
			}
			
//			System.out.println("���������� �ýڵ�ʣ��������� = " + objs.size);
			return false;
		}
		
	
		public void query(Rectangle rect, Array<IQuadTreeObject> ret) {
			if(type == -1) {
				// this is root node
				ret.addAll(objs);
			}
			
			if(!rect.overlaps(_rect)) {		//������ýڵ㲻�ཻ��ֱ���˳�
				
				return;
			}
			
//			System.out.println("query node : success -------------------");
			
			if(this.children == null || sides == null) {
				//û���ӽڵ� ������ж�����˳�
				ret.addAll(objs);
				return;
			}
			
			if(sides != null) {
				//�����߿ռ�
				for(int i = 0; i < sides.length; ++i) {
					//��Ҫ�߶��Լ�ȥ�ж��Ƿ���������
					sides[i].query(rect, ret);
				}
			}
			
			if(children != null) {
				for(int i = 0; i < children.length; ++i) {
					children[i].query(rect, ret);
				}
			}
		}
		
		public void visit(int currDepth) {
			if(type == -1) {
				// this is root node
				currDepth = 0;
			}
			
			StringBuilder sb = new StringBuilder();
			for(int i = 0; i < currDepth; ++i) {
				sb.append("	");
			}
			sb.append("[node] depth = "+ currDepth + " objs = " + objs.size);
			System.out.println(sb);
			
			if(sides != null) {
				//�����߿ռ�
				for(int i = 0; i < sides.length; ++i) {
					//��Ҫ�߶��Լ�ȥ�ж��Ƿ���������
					sides[i].visit(currDepth + 1);
				}
			}
			
			if(children != null) {
				for(int i = 0; i < children.length; ++i) {
					children[i].visit(currDepth + 1);
				}
			}
		}
	}
	
	//��ʱʹ�������Ϊ����
	public static class QuadTreeObject implements IQuadTreeObject {
		public float x0, y0, x1, y1;
		Rectangle r = new Rectangle();
		public QuadTreeObject(float x0, float y0, float x1, float y1) {
			this.x0 = x0;
			this.y0 = y0;
			this.x1 = x1;
			this.y1 = y1;
			r.set(x0, y0, x1-x0, y1-y0);
		}
		public String toString() {
			return r.toString();
		}
		
		@Override
		public Rectangle getRectangle() {
			return r;
		}
	}
	
	public static interface QuadTreeQueryListener {
		
	}
	
	
	QuadTreeNode root;
	static int maxDepth = 5;//��������5
	static int maxObject = 8;//һ�������ڳ���8������Ҫ�����ӽڵ�
	
	Array<IQuadTreeObject> tempObjects = new Array<IQuadTreeObject>();
	
	float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE, maxX = 0, maxY = 0;
	
	public void addObject(IQuadTreeObject obj) {
		tempObjects.add(obj);
		
		//update max aabb
		final Rectangle rect = obj.getRectangle();
		final float x0 = rect.x;
		final float y0 = rect.y;
		final float x1 = rect.x + rect.width;
		final float y1 = rect.y + rect.height;
		
		minX = minX > x0 ? x0 : minX;
		minY = minY > y0 ? y0 : minY;
		maxX = maxX < x1 ? x1 : maxX;
		maxY = maxY < y1 ? y1 : maxY;
		
		System.out.println("QuadTree : updateRootBounds [" + 
		minX +"," + minY + "," + maxX + "," + maxY + "]");
	}
	
	public void build() {
		root = new QuadTreeNode(this, minX, minY, maxX, maxY);
//		while(tempObjects.size > 0) {
//			IQuadTreeObject obj = tempObjects.pop();
//			root._addObject(obj);
//		}
		for(int i = 0; i < tempObjects.size; ++i) {
			IQuadTreeObject obj = tempObjects.get(i);
			root._addObject(obj);
		}
		
		root.build(0);
	}
	
//	public void query() {
//		
//	}
	//����һ������
	void insert(QuadTreeObject obj) {
		System.out.println("insertObject : " + obj);
		
	}
	
	public void destroy() {
		
	}
	
	public void insert() {
		
	}
	
	public void remove() {
		
	}
	
	public void query(final float x0, final float y0, final float x1, final float y1, final QuadTreeQueryListener listener) {
		
	}
	
	public void query(Rectangle rect, final Array<IQuadTreeObject> ret) {
		root.query(rect, ret);
	}
	
	public void slowQuery(Rectangle rect, final Array<IQuadTreeObject> ret) {
		for(int i = 0, n = tempObjects.size; i < n; ++i) {
			final IQuadTreeObject o = tempObjects.get(i);
			if(rect.overlaps(o.getRectangle())) {
				ret.add(o);
			}
		}
	}
	
	public void visit() {
		root.visit(0);
	}
	
	static float b_half_w = 5;
	static float b_half_h = 5;
	public static QuadTreeObject createQuadTreeObject(float x, float y) {
		return new QuadTreeObject(x - b_half_w, y - b_half_h, x + b_half_w, y + b_half_h);
	}
	
	public static void main(String[] args) {
		QuadTree qt = new QuadTree();
		for(int i = 0; i <50; ++i) {
			for(int j = 0; j < 50; ++j) {
				qt.addObject(createQuadTreeObject(0 + 12 * i, 0 + 12 * j));
			}
		}
//		qt.addObject(createQuadTreeObject(-50,0));
//		qt.addObject(createQuadTreeObject(-40,0));
//		qt.addObject(createQuadTreeObject(-30,0));
//		qt.addObject(createQuadTreeObject(-20,0));
//		qt.addObject(createQuadTreeObject(-10,0));
//		qt.addObject(createQuadTreeObject(0,0));
//		qt.addObject(createQuadTreeObject(10,0));
//		qt.addObject(createQuadTreeObject(20,0));
//		qt.addObject(createQuadTreeObject(30,0));
//		qt.addObject(createQuadTreeObject(40,0));
//		
//		qt.addObject(createQuadTreeObject(10,-20));
//		qt.addObject(createQuadTreeObject(20,20));
//		qt.addObject(createQuadTreeObject(30,20));
//		qt.addObject(createQuadTreeObject(40,20));
		
		qt.build();
		
//		qt.visit();
		
//		if(true) return;
		
		Array<IQuadTreeObject> ret = new Array<IQuadTreeObject>(256);
		Rectangle rect = new Rectangle(200, 100, 100, 80);
		//ʮ���Ĳ�����ѯ
		long time = 0;
		int n = 1000;
		
		//ʮ�����Բ�ѯ
//		time = System.nanoTime();
//		for(int i = 0; i < n; i++) {
//			qt.slowQuery(rect, ret);
//			if(i < n-1) { 
//				ret.clear();
//			}
//		}
//		System.out.println("���Բ�ѯ���� ��� = " + ret.size + " ʱ�� = " + (System.nanoTime() - time));
		
		time = System.currentTimeMillis();
		
		for(int i = 0; i < n; i++) {
			qt.query(rect, ret);
			if(i < n-1) { 
				ret.clear();
			}
		}
		System.out.println("�Ĳ�����ѯ���� ��� = " + ret.size + 
				" ʱ�� = " + (System.currentTimeMillis() - time));
		ret.clear();
	}
}
