package com.wx.android.common.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Common information
 * 
 * @author fengwx
 */
public class CommonUtils {

	private static final int VALUE = 10000;

	private static AtomicInteger mCount = new AtomicInteger(VALUE);

	/**
	 * Generate increment id
	 * @return
	 */
	public static int generateId() {
		return mCount.getAndIncrement();
	}

}
