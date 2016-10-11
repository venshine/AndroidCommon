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

import android.os.SystemClock;
import android.view.View;

/**
 * 点击事件处理
 *
 * @author venshine
 */
public class ClickUtils {

    // 点击时隙(ms)
    private static final int MIN_CLICK_INTERVAL = 500;
    // 点击次数
    private static int mClickNumber = 0;
    // 点击时间
    private static long mLastClickTime = 0L;

    private static View mView;

    /**
     * 连续点击N次执行事件
     *
     * @param view
     * @param clickNum
     * @param onClickEvent
     */
    public static void clickMultiple(View view, int clickNum, OnClickEvent onClickEvent) {
        clickMultiple(view, clickNum, MIN_CLICK_INTERVAL, onClickEvent);
    }

    /**
     * 连续点击N次执行事件
     *
     * @param view
     * @param clickNum
     * @param clickInterval
     * @param onClickEvent
     */
    public static void clickMultiple(View view, int clickNum, int clickInterval, OnClickEvent onClickEvent) {
        if (clickNum < 2) {
            throw new RuntimeException("clickNum must be greater than 2");
        }
        long currentClickTime = SystemClock.uptimeMillis();
        long elapsedTime = currentClickTime - mLastClickTime;
        mLastClickTime = currentClickTime;
        if (mView == null || (mView != null && mView.getId() != view.getId())) {
            mView = view;
            mClickNumber = 1;
            return;
        }
        if (elapsedTime < clickInterval) {
            ++mClickNumber;
            if (clickNum == mClickNumber) {
                mClickNumber = 0;
                onClickEvent.click(view);
            }
        } else {
            mClickNumber = 1;
        }
    }

    public interface OnClickEvent {
        void click(View v);
    }

}
