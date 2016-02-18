package com.wx.android.common.util;

import android.util.Base64;

/**
 * Base64 Operations
 * 
 * @author fengwx
 */
public class BASE64Utils {

	/**
	 * Encode base64
	 * 
	 * @param input
	 * @return
	 */
	public static byte[] encodeBase64(byte[] input) {
		return Base64.encode(input, Base64.DEFAULT);
	}

	/**
	 * Encode base64
	 * 
	 * @param s
	 * @return
	 */
	public static String encodeBase64(String s) {
		return Base64.encodeToString(s.getBytes(), Base64.DEFAULT);
	}

	/**
	 * Decode base64
	 * 
	 * @param input
	 * @return
	 */
	public static byte[] decodeBase64(byte[] input) {
		return Base64.decode(input, Base64.DEFAULT);
	}

	/**
	 * Decode base64
	 * 
	 * @param s
	 * @return
	 */
	public static String decodeBase64(String s) {
		return new String(Base64.decode(s, Base64.DEFAULT));
	}

}
