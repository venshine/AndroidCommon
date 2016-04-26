/*
 * Copyright (C) 2016 venshine.cn@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wx.android.common.util;

import android.app.Service;
import android.content.Context;
import android.os.Vibrator;

/**
 * 震动
 *
 * @author venshine
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
