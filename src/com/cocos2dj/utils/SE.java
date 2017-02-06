package com.cocos2dj.utils;

/**
 * Storm Engine
 * <br>
 * now use CC
 * 
 * @author xu jun
 *
 */
public class SE {
	
//	/**��ȡ ����ģ��ʵ�ʱ���
//	 * 
//	 * @return */
//	public static float getRatio() {
//		return SCoreTimer.delta / PhysicsCard2D.instance().getStepDelta();
//	}
//	
//	/**���������ƶ����� (ʹ���ڲ��������) <br>
//	 * <b>�������������GLThread�е��� */
//	public static final Texture loadTexture(final String name, final String path){
//		return GraphicsStorm2D.getTextureManager().loadNamedTexture(name, path);
//	}
//	/**ֱ����path��Ϊkey�������� (ʹ���ڲ��������)<br>
//	 * <b>�������������GLThread�е��� */
//	public static final Texture loadTexture(final String path){
//		return loadTexture(path, path);
//	}
//	
//	public static final EventManager eventManager() {
//		return EventManager.instance();
//	}
//	
//	/**���������ƶ����� (ʹ���ڲ��������) <br>
//	 * <b>�������������GLThread�е��� 
//	 * 
//	 * @param name ͼƬ�ļ�ֵ
//	 * @param path ͼƬ·��
//	 * @param absolute �Ƿ��Ǿ���·��
//	 * @return ���غõ�����
//	 */
//	public static final Texture loadTexture(final String name, final String path, boolean absolute){
//		if(absolute) {
//			Texture t = GraphicsStorm2D.getTextureManager().getNamedTexture(name);
//			if(t == null) {
//				t = new Texture(Gdx.files.absolute(path));
//				GraphicsStorm2D.getTextureManager().addNamedTexture(name, t);
//			}
//			return t;
//		}
//		
//		return GraphicsStorm2D.getTextureManager().loadNamedTexture(name, path);
//	}
//	
//	/**ֱ����path��Ϊkey�������� (ʹ���ڲ��������)<br>
//	 * <b>�������������GLThread�е��� 
//	 * 
//	 * @param path ͼƬ·��
//	 * @param absolute �Ƿ��Ǿ���·��
//	 * @return ���غõ�����
//	 */
//	public static final Texture loadTexture(final String path, boolean absolute){
//		return loadTexture(path, path, absolute);
//	}
//	
//	
//	/**������������
//	 * @param src Դ��������
//	 * @param x �Ƿ�ˮƽ��ת
//	 * @param y �Ƿ���ֱ��ת
//	 * @return */
//	public static final TextureRegion flip(final TextureRegion src, final boolean x, final boolean y) {
//		TextureRegion result = new TextureRegion(src);
//		result.flip(x, y);
//		return result;
//	}
//	
//	
//	public static final TextureAtlas loadAtlas(final String path) {
//		return loadAtlas(path, false);
//	}
//	
//	public static final TextureAtlas loadAtlas(final String path, boolean absolute) {
//		return loadAtlas(path, path, absolute);
//	}
//	
//	/**��ȡatlas�ļ�
//	 * 
//	 * @param path
//	 * @return
//	 */
//	public static final TextureAtlas loadAtlas(final String name, final String path, final boolean absolute) {
//		TextureAtlas ret = GraphicsStorm2D.getTextureManager().getNamedTextureAtlas(name);
//		if(ret == null) {
//			FileHandle f;
//			if(absolute) {
//				f = Gdx.files.absolute(path);
//			}
//			else {
//				f = Gdx.files.internal(path);
//			}
//			ret = GraphicsStorm2D.getTextureManager().loadNamedTextureAtlas(name, f);
//		}
//		return ret;
//	}
}
