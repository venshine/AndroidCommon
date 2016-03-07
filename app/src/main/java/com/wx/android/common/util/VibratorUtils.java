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

    private static final int VIBRATE_TIME = 1000;
    private static final long[] PATTERN = new long[]{10, 1000, 10, 1000};

    private static Vibrator vibrator = null;

    private static Vibrator getInstance(Context context) {
        if (vibrator == null) {
            vibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        }
        return vibrator;
    }

    /**
     * Vibrate constantly in 1000ms
     *
     * @param context
     */
    public static void vibrate(Context context) {
        vibrate(context, VIBRATE_TIME);
    }

    /**
     * Vibrate constantly for the specified period of time.
     *
     * @param context
     * @param milliseconds
     */
    public static void vibrate(Context context, long milliseconds) {
        Vibrator vibrator = getInstance(context);
        vibrator.vibrate(milliseconds);
    }

    /**
     * Vibrate with a given pattern.
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
