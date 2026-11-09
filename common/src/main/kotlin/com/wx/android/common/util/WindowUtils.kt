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
package com.wx.android.common.util

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.WindowManager

/**
 * 悬浮窗工具。
 *
 * 悬浮窗需要在清单声明 `SYSTEM_ALERT_WINDOW`，且用户在系统设置里授予"显示在其他应用上层"，
 * 没有授权时 [addView] 返回 false（不抛异常）。
 *
 * @author venshine
 */
object WindowUtils {

    private fun manager(context: Context): WindowManager =
        context.applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    /** 悬浮窗权限是否已授予（API 23 以下安装即授予） */
    @JvmStatic
    fun canDrawOverlays(context: Context): Boolean =
        Build.VERSION.SDK_INT < 23 || Settings.canDrawOverlays(context)

    /** 默认参数：全屏、不可触摸、不抢焦点、半透明 */
    @JvmStatic
    @Suppress("DEPRECATION")
    fun createDefaultLayoutParams(): WindowManager.LayoutParams = WindowManager.LayoutParams().apply {
        type = if (Build.VERSION.SDK_INT >= 26) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }
        flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        format = PixelFormat.TRANSLUCENT
        width = WindowManager.LayoutParams.MATCH_PARENT
        height = WindowManager.LayoutParams.MATCH_PARENT
        gravity = Gravity.CENTER
    }

    /** 添加悬浮窗，没有悬浮窗权限时返回 false */
    @JvmStatic
    @JvmOverloads
    fun addView(context: Context, view: View, params: WindowManager.LayoutParams = createDefaultLayoutParams()): Boolean {
        if (!canDrawOverlays(context)) return false
        manager(context).addView(view, params)
        return true
    }

    @JvmStatic
    fun removeView(context: Context, view: View) {
        manager(context).removeView(view)
    }

    @JvmStatic
    fun updateView(context: Context, view: View, params: WindowManager.LayoutParams) {
        manager(context).updateViewLayout(view, params)
    }
}
