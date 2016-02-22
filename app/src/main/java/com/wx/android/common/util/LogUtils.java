/**
 * Copyright (C) 2016 Baidu, Inc. All Rights Reserved.
 */
package com.wx.android.common.util;

import android.util.Log;

import com.wx.android.common.CommonCfg;
import com.wx.android.common.log.Level;

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
        if (CommonCfg.LOG <= Level.DEBUG) {
            Log.d(TAG, msg);
        }
    }

    /**
     * error log
     *
     * @param msg
     */
    public static void e(String msg) {
        if (CommonCfg.LOG <= Level.ERROR) {
            Log.e(TAG, msg);
        }
    }

}
