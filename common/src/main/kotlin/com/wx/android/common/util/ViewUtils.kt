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

import android.view.View
import android.view.ViewGroup
import android.widget.TextView

/**
 * View 测量工具。
 *
 * @author venshine
 */
object ViewUtils {

    /** 测量 View（宽度 MATCH_PARENT、高度 WRAP_CONTENT 的布局参数）的高度，单位 px */
    @JvmStatic
    fun measureViewHeight(view: View): Int {
        view.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        view.measure(spec, spec)
        return view.measuredHeight
    }

    /** 文本在屏幕宽度下占用的高度，单位 px */
    @JvmStatic
    fun getTextViewHeight(textView: TextView): Int =
        getTextViewHeight(textView, DisplayUtils.getScreenWidth(textView.context))

    /** 文本在 [width] 宽度（至多）下占用的高度，单位 px；TextView 没有 LayoutParams 时会设置默认的 WRAP_CONTENT */
    @JvmStatic
    fun getTextViewHeight(textView: TextView, width: Int): Int {
        // 未加入布局的 TextView 没有 LayoutParams，部分文本换行路径会因此 NPE，这里补一个默认值
        if (textView.layoutParams == null) {
            textView.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        textView.measure(
            View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.AT_MOST),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        return textView.measuredHeight
    }

    /** 沿第一个子 View 一路向下找到 TextView，找不到返回 null */
    @JvmStatic
    fun findTextView(view: View?): TextView? {
        var current = view
        while (current != null) {
            if (current is TextView) return current
            current = if (current is ViewGroup && current.childCount > 0) current.getChildAt(0) else null
        }
        return null
    }
}
