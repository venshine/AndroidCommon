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
import android.media.AudioManager
import android.os.Build
import android.provider.Settings

/**
 * 设备设置工具：屏幕亮度、休眠时间、音量。
 *
 * 写系统设置（亮度、休眠时间）需要 `WRITE_SETTINGS` 特殊权限，API 23 起必须由用户在设置页授予
 * （`Settings.ACTION_MANAGE_WRITE_SETTINGS`），没有权限时对应方法返回 false。
 *
 * 不再提供设备唯一标识、飞行模式和蓝牙开关：新版本系统不允许第三方应用读取硬件标识或切换这些开关。
 *
 * @author venshine
 */
object DeviceUtils {

    private const val BRIGHTNESS_MIN = 1
    private const val BRIGHTNESS_MAX = 255

    private fun canWriteSettings(context: Context): Boolean =
        Build.VERSION.SDK_INT < 23 || Settings.System.canWrite(context)

    private fun putSystemInt(context: Context, name: String, value: Int): Boolean {
        if (!canWriteSettings(context)) return false
        return try {
            Settings.System.putInt(context.contentResolver, name, value)
        } catch (e: SecurityException) {
            false
        }
    }

    /** 亮度模式，取值见 `Settings.System.SCREEN_BRIGHTNESS_MODE_*` */
    @JvmStatic
    fun getScreenBrightnessMode(context: Context): Int =
        Settings.System.getInt(
            context.contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE,
            Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
        )

    @JvmStatic
    fun isScreenBrightnessModeAuto(context: Context): Boolean =
        getScreenBrightnessMode(context) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC

    /** 设置自动 / 手动亮度；没有写设置权限返回 false */
    @JvmStatic
    fun setScreenBrightnessMode(context: Context, auto: Boolean): Boolean {
        if (isScreenBrightnessModeAuto(context) == auto) return true
        return putSystemInt(
            context, Settings.System.SCREEN_BRIGHTNESS_MODE,
            if (auto) Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC else Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
        )
    }

    /** 系统亮度 1-255，读不到时返回 255 */
    @JvmStatic
    fun getScreenBrightness(context: Context): Int =
        Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS, BRIGHTNESS_MAX)

    /** 设置系统亮度（限制在 1-255）；没有写设置权限返回 false */
    @JvmStatic
    fun setScreenBrightness(context: Context, brightness: Int): Boolean =
        putSystemInt(context, Settings.System.SCREEN_BRIGHTNESS, brightness.coerceIn(BRIGHTNESS_MIN, BRIGHTNESS_MAX))

    /** 仅修改当前窗口的亮度（限制在 1-255），不改系统设置、无需权限 */
    @JvmStatic
    fun setWindowBrightness(activity: Activity, brightness: Int) {
        val attrs = activity.window.attributes
        attrs.screenBrightness = brightness.coerceIn(BRIGHTNESS_MIN, BRIGHTNESS_MAX) / BRIGHTNESS_MAX.toFloat()
        activity.window.attributes = attrs
    }

    /** 同时设置系统亮度和当前窗口亮度；系统亮度设置失败返回 false（窗口亮度仍会生效） */
    @JvmStatic
    fun setScreenBrightnessAndApply(activity: Activity, brightness: Int): Boolean {
        val result = setScreenBrightness(activity, brightness)
        setWindowBrightness(activity, brightness)
        return result
    }

    /** 屏幕休眠时间（毫秒），读不到时返回 30000 */
    @JvmStatic
    fun getScreenDormantTime(context: Context): Int =
        Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_OFF_TIMEOUT, 30000)

    /** 设置屏幕休眠时间（毫秒）；没有写设置权限返回 false */
    @JvmStatic
    fun setScreenDormantTime(context: Context, millis: Int): Boolean =
        putSystemInt(context, Settings.System.SCREEN_OFF_TIMEOUT, millis)

    private fun audio(context: Context): AudioManager =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    /** 媒体音量 */
    @JvmStatic
    fun getMediaVolume(context: Context): Int = audio(context).getStreamVolume(AudioManager.STREAM_MUSIC)

    /** 设置媒体音量，超出范围时按系统规则截断 */
    @JvmStatic
    fun setMediaVolume(context: Context, volume: Int) {
        audio(context).setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0)
    }

    /** 铃声音量 */
    @JvmStatic
    fun getRingVolume(context: Context): Int = audio(context).getStreamVolume(AudioManager.STREAM_RING)

    /**
     * 设置铃声音量。勿扰模式下，没有"勿扰访问"授权的应用修改铃声音量会抛 [SecurityException]，此时返回 false。
     */
    @JvmStatic
    fun setRingVolume(context: Context, volume: Int): Boolean = try {
        audio(context).setStreamVolume(AudioManager.STREAM_RING, volume, 0)
        true
    } catch (e: SecurityException) {
        false
    }
}
