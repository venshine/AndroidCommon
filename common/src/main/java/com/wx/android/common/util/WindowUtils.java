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

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

/**
 * Window操作
 * @author venshine
 */
public class WindowUtils {

    private static WindowManager mWindowManager;

    private static WindowManager getWindowManager(Context context) {
        if (mWindowManager == null) {
            mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }
        return mWindowManager;
    }

    private static WindowManager.LayoutParams createDefaultLayoutParams() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();

        int type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            type = WindowManager.LayoutParams.TYPE_TOAST;
        }
        params.type = type;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        params.format = PixelFormat.TRANSLUCENT;

        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;

        params.gravity = Gravity.CENTER;
        return params;
    }

    /**
     * Adds a child view with the specified layout parameters.
     *
     * @param context
     * @param view
     */
    public static void addView(Context context, View view) {
        addView(context, view, createDefaultLayoutParams());
    }

    /**
     * Adds a child view with the specified layout parameters.
     *
     * @param context
     * @param view
     * @param params
     */
    public static void addView(Context context, View view, WindowManager.LayoutParams params) {
        if (context == null || view == null) {
            throw new IllegalArgumentException("argument cannot be null.");
        }
        mWindowManager = getWindowManager(context);
        mWindowManager.addView(view, params);
    }

    /**
     * Removes a view during layout.
     *
     * @param context
     * @param view
     */
    public static void removeView(Context context, View view) {
        if (context == null || view == null) {
            throw new IllegalArgumentException("argument cannot be null.");
        }
        mWindowManager = getWindowManager(context);
        mWindowManager.removeView(view);
    }

    /**
     * Update a view by params
     *
     * @param context
     * @param view
     * @param params
     */
    public static void updateView(Context context, View view, WindowManager.LayoutParams params) {
        if (context == null || view == null || params == null) {
            throw new IllegalArgumentException("argument cannot be null.");
        }
        mWindowManager = getWindowManager(context);
        mWindowManager.updateViewLayout(view, params);
    }

}
