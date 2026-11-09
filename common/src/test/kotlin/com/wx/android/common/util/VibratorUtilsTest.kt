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
import android.os.VibratorManager
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class VibratorUtilsTest {

    private val context: Context = RuntimeEnvironment.getApplication()
    private val vibrator
        get() = shadowOf((context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator)

    @Test
    fun defaultVibratesOneSecond() {
        VibratorUtils.vibrate(context)
        assertTrue(vibrator.isVibrating)
        assertEquals(1000L, vibrator.milliseconds)
    }

    @Test
    fun vibrateGivenMilliseconds() {
        VibratorUtils.vibrate(context, 250L)
        assertEquals(250L, vibrator.milliseconds)
    }

    @Test
    fun vibratePattern() {
        val pattern = longArrayOf(0, 100, 50, 100)
        VibratorUtils.vibrate(context, pattern, -1)
        assertTrue(vibrator.isVibrating)
        assertArrayEquals(pattern, vibrator.pattern)
    }

    @Test
    fun cancelStopsVibration() {
        VibratorUtils.vibrate(context)
        VibratorUtils.cancel(context)
        assertFalse(vibrator.isVibrating)
        assertTrue(vibrator.isCancelled)
    }
}
