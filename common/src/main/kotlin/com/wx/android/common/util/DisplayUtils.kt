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

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.util.TypedValue
import android.view.WindowInsets

/**
 * 屏幕与单位换算工具。
 *
 * 换算结果四舍五入为 Int。
 *
 * @author venshine
 */
object DisplayUtils {

    @JvmStatic
    fun isLandscape(context: Context): Boolean =
        context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    @JvmStatic
    fun isPortrait(context: Context): Boolean =
        context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    /** 屏幕（当前 Resources 的显示区域）宽度，单位 px */
    @JvmStatic
    fun getScreenWidth(context: Context): Int = context.resources.displayMetrics.widthPixels

    /** 屏幕（当前 Resources 的显示区域）高度，单位 px */
    @JvmStatic
    fun getScreenHeight(context: Context): Int = context.resources.displayMetrics.heightPixels

    @JvmStatic
    fun getScreenDensity(context: Context): Float = context.resources.displayMetrics.density

    @JvmStatic
    fun getScreenDensityDPI(context: Context): Int = context.resources.displayMetrics.densityDpi

    /**
     * 状态栏高度，单位 px。优先取窗口的真实 insets（窗口未附加时取不到），
     * 取不到时回退到系统资源 `status_bar_height`。
     */
    @JvmStatic
    @Suppress("DEPRECATION")
    @SuppressLint("InternalInsetResource", "DiscouragedApi") // 兜底读取系统公开的 status_bar_height 资源
    fun getStatusBarHeight(activity: Activity): Int {
        val insets = if (Build.VERSION.SDK_INT >= 23) activity.window.decorView.rootWindowInsets else null
        if (insets != null) {
            val top = if (Build.VERSION.SDK_INT >= 30) {
                insets.getInsets(WindowInsets.Type.statusBars()).top
            } else {
                insets.systemWindowInsetTop
            }
            if (top > 0) return top
        }
        val res = Resources.getSystem()
        val id = res.getIdentifier("status_bar_height", "dimen", "android")
        return if (id > 0) res.getDimensionPixelSize(id) else 0
    }

    /** dp 转 px */
    @JvmStatic
    fun dip2px(context: Context, dp: Float): Int =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics).roundInt()

    /** px 转 dp */
    @JvmStatic
    fun px2dip(context: Context, px: Float): Int = (px / context.resources.displayMetrics.density).roundInt()

    /** sp 转 px */
    @JvmStatic
    fun sp2px(context: Context, sp: Float): Int =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.resources.displayMetrics).roundInt()

    /** px 转 sp */
    @JvmStatic
    @Suppress("DEPRECATION")
    fun px2sp(context: Context, px: Float): Int {
        val dm = context.resources.displayMetrics
        return if (Build.VERSION.SDK_INT >= 34) {
            TypedValue.deriveDimension(TypedValue.COMPLEX_UNIT_SP, px, dm).roundInt()
        } else {
            (px / dm.scaledDensity).roundInt()
        }
    }

    private fun Float.roundInt(): Int = (this + 0.5f).toInt()
}
