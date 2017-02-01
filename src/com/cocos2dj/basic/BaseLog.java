package com.cocos2dj.basic;

import com.badlogic.gdx.Gdx;


public class BaseLog {
	
	public interface LogFilter {
		/**
		 * */
		public boolean moduleFilter(String module);
		/**
		 */
		public boolean tagFilter(String tag);
	}
	
	static public final int NONE = 0;
	static public final int ERROR = 1;
	static public final int INFO = 2;
	static public final int WARNING = 3;
	static public final int DEBUG = 4;
	static public final int ENGINE = 5;

	private static final LogFilter defaultLogFilter = new LogFilter() {

		public boolean moduleFilter(String module) {
			return false;
		}
		
		public boolean tagFilter(String tag) {
			return false;
		}
	
	};
	private static LogFilter logFilter = defaultLogFilter;
	
	
	private static String tag = "";
	private static int level = 5;

	private static void outputError(String module, String tag, String message) {
		System.err.println("--" + module + "--" + "[error: "+tag+"]  " + message);
	}
	private static void outputWarning(String module, String tag, String message) {
		System.err.println("--" + module + "--" + "[warning: "+tag+"]  " + message);
	}
//	private static void outputInfo(String module, String tag, String message) {
//		System.err.println("--" + module + "--" + "[info: "+tag+"]  " + message);
//	}
	private static void outputDebug(String module, String tag, String message) {
		System.out.println("--" + module + "--" + "[debug: "+tag+"]  " + message);
	}
	
	
	public static void setLogFilter(LogFilter filter) {
		if(filter == null) 
			logFilter = defaultLogFilter;
		else 
			logFilter = filter;
	}
	
	public static void removeLogFilter() {
		logFilter = defaultLogFilter;
	}
	
	
	public static void error(String tag, String message) {
		error("game", tag, message);
	}
	
	public static void error(Object obj, String message) {
		error("game", obj.getClass().getSimpleName(), message);
	}
	
	public static void error(String module, String tag, String message) {
		if(level > ERROR) {
			if(logFilter.moduleFilter(module) || logFilter.tagFilter(tag)) {
				return;
			}
			outputError(module, tag, message);
		}
	}
	
	public static void engine(String tag, String message) {
		debug("engine", tag, message);
	}
	
	public static void debug(String tag, String message) {
		debug("game", tag, message);
	}
	
	public static void debug(Object obj, String message) {
		debug("game", obj.getClass().getSimpleName(), message);
	}
	
	public static void debug(String module, String tag, String message) {
		if(level > DEBUG) {
			if(logFilter.moduleFilter(module) || logFilter.tagFilter(tag)) {
				return;
			}
			outputDebug(module, tag, message);
		}
	}
	
	public static void warning(String tag, String message) {
		warning("game", tag, message);
	}
	
	public static void warning(Object obj, String message) {
		warning("game", obj.getClass().getSimpleName(), message);
	}
	
	public static void warning(String module, String tag, String message) {
		if(level > WARNING) {
			if(logFilter.moduleFilter(module) || logFilter.tagFilter(tag)) {
				return;
			}
			outputWarning(module, tag, message);
		}
	}
	
//	public static void debug(String tag, String message) {
//		if(logFilter.tagFilter(tag)) {return;}
//		System.out.println("[debug: "+tag+"]  " + message);
//	}
//	
//	public static void debug (Object obj, String message) {
//		if(level >= DEBUG) {
//			if(logFilter.tagFilter(tag)) {return;}
//			System.out.println("[debug: "+obj.getClass().getSimpleName()+"]  " + message);
//		}
//	}

	public static void engine (String message, Exception exception) {
		if (level >= ENGINE) Gdx.app.debug(tag, message, exception);
	}
	
	public static void debug (String message, Exception exception) {
		if (level >= DEBUG) Gdx.app.debug(tag, message, exception);
	}

	public static void info (String message) {
		if (level >= INFO) Gdx.app.log(tag, message);
	}

	public static  void info (String message, Exception exception) {
		if (level >= INFO) Gdx.app.log(tag, message, exception);
	}

	public static void error (String message) {
		if (level >= ERROR) Gdx.app.error(tag, message);
	}

	public static void error (String message, Throwable exception) {
		if (level >= ERROR) Gdx.app.error(tag, message, exception);
	}

	/** Sets the log level. {@link #NONE} will mute all log output. {@link #ERROR} will only let error messages through.
	 * {@link #INFO} will let all non-debug messages through, and {@link #DEBUG} will let all messages through.
	 * @param level {@link #NONE}, {@link #ERROR}, {@link #INFO}, {@link #DEBUG}. */
	public static void setLevel (int slevel) {
		level = slevel;
	}

	public static int getLevel () {
		return level;
	}
}
