package com.wx.android.common.util;

import android.os.Handler;
import android.os.Looper;

/**
 * Handler
 *
 * @author fengwx
 */
public class HandlerUtils {

    private static final Handler HANDLER = new Handler(Looper.getMainLooper());

    /**
     * Run on ui thread
     *
     * @param runnable
     */
    public static void runOnUiThread(Runnable runnable) {
        HANDLER.post(runnable);
    }

    /**
     * Run on ui thread delay
     *
     * @param runnable
     * @param delayMillis
     */
    public static void runOnUiThreadDelay(Runnable runnable, long delayMillis) {
        HANDLER.postDelayed(runnable, delayMillis);
    }

    /**
     * Remove runnable
     *
     * @param runnable
     */
    public static void removeRunable(Runnable runnable) {
        HANDLER.removeCallbacks(runnable);
    }

}
