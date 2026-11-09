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
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * 振动工具，需要 `VIBRATE` 权限。
 *
 * @author venshine
 */
@SuppressLint("MissingPermission") // 需要使用方声明 VIBRATE，见类注释
object VibratorUtils {

    private const val VIBRATE_TIME = 1000L

    @Suppress("DEPRECATION")
    private fun vibrator(context: Context): Vibrator =
        if (Build.VERSION.SDK_INT >= 31) {
            (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
        } else {
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

    /** 振动 [milliseconds] 毫秒，默认 1 秒 */
    @JvmStatic
    @JvmOverloads
    @Suppress("DEPRECATION")
    fun vibrate(context: Context, milliseconds: Long = VIBRATE_TIME) {
        val v = vibrator(context)
        if (Build.VERSION.SDK_INT >= 26) {
            v.vibrate(VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            v.vibrate(milliseconds)
        }
    }

    /** 按 [pattern]（关、开、关、开……毫秒）振动，[repeat] 为重复起点下标，-1 表示不重复 */
    @JvmStatic
    @Suppress("DEPRECATION")
    fun vibrate(context: Context, pattern: LongArray, repeat: Int) {
        val v = vibrator(context)
        if (Build.VERSION.SDK_INT >= 26) {
            v.vibrate(VibrationEffect.createWaveform(pattern, repeat))
        } else {
            v.vibrate(pattern, repeat)
        }
    }

    @JvmStatic
    fun cancel(context: Context) {
        vibrator(context).cancel()
    }
}
