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
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.widget.Toast

/**
 * Toast 工具：复用同一个 Toast，保证在主线程显示。
 *
 * 使用前需调用一次 [init]（建议在 `Application.onCreate`）。只保存 ApplicationContext，不会泄漏 Activity。
 * 新版本系统对后台应用的自定义 Toast 有限制，位置参数（gravity / 偏移）在较新系统上可能被忽略，
 * 需要完全可控的提示请改用 Snackbar 或自定义 View。
 *
 * @author venshine
 */
object ToastUtils {

    private val handler = Handler(Looper.getMainLooper())
    private var appContext: Context? = null
    private var toast: Toast? = null

    /** 初始化，保存 ApplicationContext */
    @JvmStatic
    fun init(context: Context) {
        appContext = context.applicationContext
    }

    @JvmStatic
    @JvmOverloads
    fun showToast(msg: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
        runOnMain { show(msg, duration, null) }
    }

    /** 显示 Toast，[yOffset] 为 [Int.MAX_VALUE] 时保持默认的纵向偏移 */
    @JvmStatic
    fun showToast(msg: CharSequence, duration: Int, gravity: Int, xOffset: Int, yOffset: Int) {
        runOnMain { show(msg, duration, Position(gravity, xOffset, yOffset)) }
    }

    /** 取消当前显示的 Toast */
    @JvmStatic
    fun cancel() {
        runOnMain {
            toast?.cancel()
            toast = null
        }
    }

    private class Position(val gravity: Int, val xOffset: Int, val yOffset: Int)

    @Suppress("DEPRECATION")
    private fun show(msg: CharSequence, duration: Int, position: Position?) {
        val context = checkNotNull(appContext) { "Please invoke init method first." }
        val t = toast ?: Toast.makeText(context, "", Toast.LENGTH_SHORT).also { toast = it }
        if (position != null) {
            val y = if (position.yOffset == Int.MAX_VALUE) t.yOffset else position.yOffset
            t.setGravity(position.gravity, position.xOffset, y)
        } else {
            t.setGravity(Gravity.BOTTOM, 0, t.yOffset)
        }
        t.setText(msg)
        t.duration = duration
        t.show()
    }

    private fun runOnMain(block: () -> Unit) {
        if (Looper.myLooper() == Looper.getMainLooper()) block() else handler.post(block)
    }
}
