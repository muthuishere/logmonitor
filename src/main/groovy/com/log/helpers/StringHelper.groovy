package com.log.helpers

import java.lang.reflect.Field;


public class StringHelper {

	public static String NEW_LINE = System.getProperty("line.separator");

	public static String removeUnicodeAndEscapeChars(String input) {
		StringBuilder buffer = new StringBuilder(input.length());
		for (int i = 0; i < input.length(); i++) {
			if ((int) input.charAt(i) > 256) {
				buffer.append("\\u").append(Integer.toHexString((int) input.charAt(i)));
			} else {
				if (input.charAt(i) == '\n') {
					buffer.append("\\n");
				} else if (input.charAt(i) == '\t') {
					buffer.append("\\t");
				} else if (input.charAt(i) == '\r') {
					buffer.append("\\r");
				} else if (input.charAt(i) == '\b') {
					buffer.append("\\b");
				} else if (input.charAt(i) == '\f') {
					buffer.append("\\f");
				} else if (input.charAt(i) == '\'') {
					buffer.append("\\'");
				} else if (input.charAt(i) == '\"') {
					buffer.append("\\");
				} else if (input.charAt(i) == '\\') {
					buffer.append("\\\\");
				} else {
					buffer.append(input.charAt(i));
				}
			}
		}
		return buffer.toString();
	}

	public static String defaultString(String str) {

		if (null != str) {
			return str;
		}

		return "";
	}

	public static String toString(Object obj) {

		if (null != obj) {
		return obj.toString();	
		}

		return "";
	}

	public static boolean isEmpty(String s) {
		return (null == s || s.trim().length() <= 0);

	}
}


