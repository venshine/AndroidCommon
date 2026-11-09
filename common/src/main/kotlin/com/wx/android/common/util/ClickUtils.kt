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

import android.os.SystemClock
import android.view.View
import java.util.WeakHashMap

/**
 * 点击工具：N 连击检测。
 *
 * 每个 View 单独计数（弱引用，不会泄漏 View），在 View 的 onClick 里调用即可。
 *
 * @author venshine
 */
object ClickUtils {

    /** 默认两次点击的最大间隔（毫秒） */
    private const val MIN_CLICK_INTERVAL = 500

    private class State(var count: Int = 0, var lastTime: Long = 0L)

    private val states = WeakHashMap<View, State>()

    fun interface OnClickEvent {
        fun click(v: View)
    }

    /** 在 [clickInterval] 毫秒内连续点击 [clickNum] 次后回调一次，[clickNum] 必须不小于 2 */
    @JvmStatic
    @JvmOverloads
    fun clickMultiple(view: View, clickNum: Int, clickInterval: Int = MIN_CLICK_INTERVAL, onClickEvent: OnClickEvent) {
        require(clickNum >= 2) { "clickNum must be at least 2" }
        val now = SystemClock.uptimeMillis()
        val state = states.getOrPut(view) { State() }
        state.count = if (state.count > 0 && now - state.lastTime < clickInterval) state.count + 1 else 1
        state.lastTime = now
        if (state.count == clickNum) {
            state.count = 0
            onClickEvent.click(view)
        }
    }
}
