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
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Toast操作
 *
 * @author venshine
 */
public class ToastUtils {

    private static final int DEFAULT_TIME = Toast.LENGTH_SHORT;

    private static final int DEFAULT_POSITION = Gravity.BOTTOM;

    private static final int DEFAULT_XOFFSET = 0;

    private static final int DEFAULT_YOFFSET = Integer.MAX_VALUE;

    private static Toast mToast = null;

    private static Context mContext = null;

    private static Object mTN = null;

    private static Handler mHandler = new Handler(Looper.getMainLooper());

    /**
     * Init method, always by invoked in Application
     *
     * @param context
     */
    public static void init(Context context) {
        if (null == context) {
            throw new IllegalArgumentException("context cannot be null.");
        }
        mContext = context.getApplicationContext();
    }

    /**
     * Get toast
     */
    private static Toast getToast() {
        if (null == mContext) {
            throw new IllegalStateException("Please invoke init method first.");
        }
        if (null != mToast) {
            return mToast;
        }
        return Toast.makeText(mContext.getApplicationContext(), "", Toast.LENGTH_SHORT);
    }

    /**
     * Show toast
     *
     * @param msg
     */
    public static void showToast(String msg) {
        showToast(msg, DEFAULT_TIME);
    }

    /**
     * Show toast
     *
     * @param msg
     * @param duration
     */
    public static void showToast(String msg, int duration) {
        showToast(msg, duration, DEFAULT_POSITION, DEFAULT_XOFFSET, DEFAULT_YOFFSET);
    }

    /**
     * Show toast
     *
     * @param msg
     * @param duration
     * @param gravity
     * @param xOffset
     * @param yOffset
     */
    public static void showToast(final String msg, final int duration, final int gravity, final int xOffset, final int yOffset) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            makeToast(msg, duration, gravity, xOffset, yOffset);
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    makeToast(msg, duration, gravity, xOffset, yOffset);
                }
            });
        }
    }

    /**
     * Show toast
     *
     * @param msg
     * @param duration
     * @param gravity
     * @param xOffset
     * @param yOffset
     */
    private static void makeToast(String msg, int duration, int gravity, int xOffset, int yOffset) {
        mToast = getToast();
        mToast.setGravity(gravity, xOffset, (yOffset == Integer.MAX_VALUE ? mToast.getYOffset() : yOffset));
        mToast.setText(msg);
        mToast.setDuration(duration);
        mToast.show();
    }

    /**
     * Show toast, the toast will be showing always if you are not calling hideToast().
     *
     * @param context
     * @param msg
     */
    public static void showToastAlways(Context context, String msg) {
        if (mTN != null) { // toast is showing
            return;
        }
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        try {
            Field field = toast.getClass().getDeclaredField("mTN");
            field.setAccessible(true);
            mTN = field.get(toast);
            // start, only used by 4.x
            Field mNextViewField = mTN.getClass().getDeclaredField("mNextView");
            mNextViewField.setAccessible(true);
            mNextViewField.set(mTN, toast.getView());
            // end
            Method method = mTN.getClass().getDeclaredMethod("show", new Class<?>[0]);
            method.invoke(mTN, new Object[]{});
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    /**
     * Hide toast, only used after showToast(context, msg), hide the toast of having shown.
     */
    public static void hideToast() {
        if (mTN == null) { // the method of showToast is not called
            return;
        }
        try {
            Method method = mTN.getClass().getDeclaredMethod("hide", new Class<?>[0]);
            method.invoke(mTN, new Object[]{});
            mTN = null;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
