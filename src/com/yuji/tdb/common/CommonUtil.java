package com.yuji.tdb.common;

import java.util.ArrayList;
import java.util.List;

public class CommonUtil {
	public static String replaceString(String text, String src, String dst) {
		return text.replaceAll(src, dst);
	}

	public static String escapeHtml(String text) {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < text.length(); i++) {
			switch (text.charAt(i)) {
			case '&':
				result.append("&amp;");
				break;
			case '<':
				result.append("&lt;");
				break;
			case '>':
				result.append("&gt;");
				break;
			default:
				result.append(text.charAt(i));
				break;
			}
		}
		return result.toString();
	}

	public static boolean isNull(String str){
		return str == null || str.length() == 0;
	}
	
	public static List<String> split(String str, String delm){
		int l = str.length();
		List<String> list = new ArrayList<String>();
		
		int pos = 0;
		String s;
		while (pos < l){
			int n = str.indexOf(delm, pos);
			if (n < 0){
				s = str.substring(pos);
				n = l;
			}
			else {
				s = str.substring(pos, n);
			}
			list.add(s);
			pos = n + 1;
		}
		
		return list;
	}

}
