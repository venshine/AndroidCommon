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

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

/**
 * 软键盘工具。
 *
 * @author venshine
 */
object InputMethodUtils {

    private fun manager(context: Context): InputMethodManager =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    /** 为 [view] 弹出软键盘（会先让其获取焦点） */
    @JvmStatic
    fun showSoftInput(view: View): Boolean {
        view.requestFocus()
        return manager(view.context).showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    /** 为当前焦点 View 弹出软键盘，没有焦点 View 返回 false */
    @JvmStatic
    fun showSoftInput(activity: Activity): Boolean {
        val focus = activity.currentFocus ?: return false
        return manager(activity).showSoftInput(focus, InputMethodManager.SHOW_IMPLICIT)
    }

    /** 隐藏软键盘 */
    @JvmStatic
    fun hideSoftInput(view: View): Boolean = manager(view.context).hideSoftInputFromWindow(view.windowToken, 0)

    /** 隐藏当前焦点 View 的软键盘，没有焦点 View 返回 false */
    @JvmStatic
    fun hideSoftInput(activity: Activity): Boolean {
        val focus = activity.currentFocus ?: return false
        return manager(activity).hideSoftInputFromWindow(focus.windowToken, 0)
    }

    /** 输入法是否处于激活状态 */
    @JvmStatic
    fun isActive(context: Context): Boolean = manager(context).isActive
}
