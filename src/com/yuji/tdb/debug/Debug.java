package com.yuji.tdb.debug;

public class Debug {
	public static int getLineNo() {
		StackTraceElement[] stackTrace = new Exception().getStackTrace();
		StackTraceElement info = stackTrace[stackTrace.length - 2];
		return info.getLineNumber();
	}
}
