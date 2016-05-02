package com.yuji.tdb.utility;

import java.util.regex.Pattern;

public class StringUtility {
	private static Pattern ptnAlnum = Pattern.compile("[a-zA-Z0-9]");
	private static Pattern ptnSpace = Pattern.compile("\\s");
	private static Pattern ptnAscii = Pattern.compile("\\p{ASCII}");
	
	public static String parseSubstring(String text, int length){
		int len = text.length();
		int status = -1;
		int pos = 0;
		
		for (int i = 0; i < len + 1 && i < length + 1; i++){
			if (i >= len){
				pos = i;
				break;
			}
			String ch = text.substring(i, i + 1);
			
			if (ptnAlnum.matcher(ch).matches()){
				if (status != 0){
					if (status != 1){
						pos = i;
					}
					status = 0;
				}
			}
			else if (ptnSpace.matcher(ch).matches()){
				if (status != 1){
					pos = i;
					status = 1;
				}
			}
			else if (ptnAscii.matcher(ch).matches()){
				if (status != 2){
					if (status != 1){
						pos = i;
					}
					status = 2;
				}				
			}
			else {
				if (status != 1){
					pos = i;
				}
				status = 3;
			}
		}
		if (pos == 0){
			return text.substring(0, (len < length)? len : length);			
		}
		return text.substring(0, pos);
	}
}
