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

import android.os.Handler;
import android.os.Looper;

/**
 * Handler
 *
 * @author venshine
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
