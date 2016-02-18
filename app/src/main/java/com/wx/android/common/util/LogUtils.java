/**
 * Copyright (C) 2016 Baidu, Inc. All Rights Reserved.
 */
package com.wx.android.common.util;

import android.util.Log;

/**
 * Log
 *
 * @author fengwx
 */
public class LogUtils {

    private static final String TAG = "fengwx";

    /**
     * debug log
     *
     * @param msg
     */
    public static void d(String msg) {
        Log.d(TAG, msg);
    }

    /**
     * error log
     *
     * @param msg
     */
    public static void e(String msg) {
        Log.e(TAG, msg);
    }

}
