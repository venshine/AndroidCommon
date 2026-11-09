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
import android.app.AppOpsManager
import android.content.Context
import android.media.AudioManager
import android.provider.Settings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class DeviceUtilsTest {

    private val context: Context = RuntimeEnvironment.getApplication()

    private fun allowWriteSettings(allowed: Boolean) {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        shadowOf(appOps).setMode(
            "android:write_settings", android.os.Process.myUid(), context.packageName,
            if (allowed) AppOpsManager.MODE_ALLOWED else AppOpsManager.MODE_ERRORED
        )
    }

    @Test
    fun brightnessReadDefaultsTo255() {
        assertEquals(255, DeviceUtils.getScreenBrightness(context))
    }

    @Test
    fun brightnessWriteNeedsWriteSettingsPermission() {
        allowWriteSettings(false)
        assertFalse(DeviceUtils.setScreenBrightness(context, 100))
        assertEquals(255, DeviceUtils.getScreenBrightness(context)) // 没写进去
    }

    @Test
    fun brightnessWriteWithPermissionIsClamped() {
        allowWriteSettings(true)
        assertTrue(DeviceUtils.setScreenBrightness(context, 100))
        assertEquals(100, DeviceUtils.getScreenBrightness(context))
        assertTrue(DeviceUtils.setScreenBrightness(context, 0)) // 下限 1
        assertEquals(1, DeviceUtils.getScreenBrightness(context))
        assertTrue(DeviceUtils.setScreenBrightness(context, 999)) // 上限 255
        assertEquals(255, DeviceUtils.getScreenBrightness(context))
    }

    @Test
    fun brightnessMode() {
        allowWriteSettings(true)
        assertTrue(DeviceUtils.isScreenBrightnessModeAuto(context)) // 默认自动
        assertTrue(DeviceUtils.setScreenBrightnessMode(context, auto = true)) // 已是目标状态，无需权限
        assertTrue(DeviceUtils.setScreenBrightnessMode(context, auto = false))
        assertFalse(DeviceUtils.isScreenBrightnessModeAuto(context))
        assertEquals(Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL, DeviceUtils.getScreenBrightnessMode(context))
    }

    @Test
    fun brightnessModeChangeWithoutPermissionFails() {
        allowWriteSettings(false)
        assertFalse(DeviceUtils.setScreenBrightnessMode(context, auto = false))
        assertTrue(DeviceUtils.isScreenBrightnessModeAuto(context))
        assertTrue(DeviceUtils.setScreenBrightnessMode(context, auto = true)) // 目标状态相同直接成功
    }

    @Test
    fun dormantTime() {
        assertEquals(30000, DeviceUtils.getScreenDormantTime(context))
        allowWriteSettings(false)
        assertFalse(DeviceUtils.setScreenDormantTime(context, 60000))
        allowWriteSettings(true)
        assertTrue(DeviceUtils.setScreenDormantTime(context, 60000))
        assertEquals(60000, DeviceUtils.getScreenDormantTime(context))
    }

    @Test
    fun windowBrightnessDoesNotNeedPermission() {
        allowWriteSettings(false)
        val activity = Robolectric.buildActivity(Activity::class.java).create().get()
        DeviceUtils.setWindowBrightness(activity, 255)
        assertEquals(1f, activity.window.attributes.screenBrightness, 1e-6f)
        DeviceUtils.setWindowBrightness(activity, 0) // 下限 1/255
        assertEquals(1f / 255, activity.window.attributes.screenBrightness, 1e-6f)
    }

    @Test
    fun applyAppliesWindowEvenWhenSystemWriteFails() {
        allowWriteSettings(false)
        val activity = Robolectric.buildActivity(Activity::class.java).create().get()
        assertFalse(DeviceUtils.setScreenBrightnessAndApply(activity, 51))
        assertEquals(51f / 255, activity.window.attributes.screenBrightness, 1e-6f)
        allowWriteSettings(true)
        assertTrue(DeviceUtils.setScreenBrightnessAndApply(activity, 102))
        assertEquals(102, DeviceUtils.getScreenBrightness(activity))
    }

    @Test
    fun volumes() {
        val audio = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val max = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        DeviceUtils.setMediaVolume(context, 3)
        assertEquals(3, DeviceUtils.getMediaVolume(context))
        DeviceUtils.setMediaVolume(context, max + 100) // 超过上限按系统规则截断
        assertEquals(max, DeviceUtils.getMediaVolume(context))
        assertTrue(DeviceUtils.setRingVolume(context, 2))
        assertEquals(2, DeviceUtils.getRingVolume(context))
    }
}
