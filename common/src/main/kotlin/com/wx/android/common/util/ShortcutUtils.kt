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
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build

/**
 * 桌面快捷方式工具。
 *
 * API 26 起系统不再允许静默添加快捷方式，只能"请求"固定快捷方式并由用户确认；
 * API 26 以下不支持（旧的广播方式已被移除），[requestPin] 直接返回 false。
 *
 * @author venshine
 */
object ShortcutUtils {

    /** 当前系统和桌面是否支持固定快捷方式 */
    @JvmStatic
    fun isRequestPinSupported(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < 26) return false
        return context.getSystemService(ShortcutManager::class.java)?.isRequestPinShortcutSupported == true
    }

    /**
     * 请求把快捷方式固定到桌面。
     *
     * @param id 快捷方式 id，相同 id 视为同一个
     * @param intent 点击后启动的 Intent，必须设置 action
     * @return 请求已发出返回 true（用户可能随后拒绝）；系统不支持返回 false
     */
    @JvmStatic
    fun requestPin(context: Context, id: String, label: CharSequence, iconRes: Int, intent: Intent): Boolean {
        if (Build.VERSION.SDK_INT < 26) return false
        val manager = context.getSystemService(ShortcutManager::class.java) ?: return false
        if (!manager.isRequestPinShortcutSupported) return false
        val info = ShortcutInfo.Builder(context, id)
            .setShortLabel(label)
            .setIcon(Icon.createWithResource(context, iconRes))
            .setIntent(intent)
            .build()
        return manager.requestPinShortcut(info, null)
    }
}
