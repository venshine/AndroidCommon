package com.wx.android.common.util;

import android.app.Service;
import android.content.Context;
import android.os.Vibrator;

/**
 * Vibrator
 *
 * @author fengwx
 */
public class VibratorUtils {

    private static Vibrator vibrator = null;

    private static final long[] PATTERN = new long[]{10, 1000, 10, 1000};

    private static Vibrator getInstance(Context context) {
        if (vibrator == null) {
            vibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        }
        return vibrator;
    }

    /**
     * vibrate
     *
     * @param context
     */
    public static void vibrate(Context context) {
        vibrate(context, PATTERN, 0);
    }

    /**
     * vibrate
     *
     * @param context
     * @param pattern
     * @param repeat
     */
    public static void vibrate(Context context, long[] pattern, int repeat) {
        Vibrator vibrator = getInstance(context);
        vibrator.vibrate(pattern, repeat);
    }

    /**
     * cancel vibrate
     *
     * @param context
     */
    public static void cancel(Context context) {
        Vibrator vibrator = getInstance(context);
        vibrator.cancel();
    }

}
