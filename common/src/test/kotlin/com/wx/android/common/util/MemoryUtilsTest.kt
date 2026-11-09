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

import android.app.ActivityManager
import android.content.Context
import android.os.Environment
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadows.ShadowEnvironment
import org.robolectric.shadows.ShadowStatFs

@RunWith(RobolectricTestRunner::class)
class MemoryUtilsTest {

    private val context: Context = RuntimeEnvironment.getApplication()

    @Test
    fun ramInfoComesFromActivityManager() {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val info = ActivityManager.MemoryInfo().apply {
            totalMem = 4_000_000_000L
            availMem = 1_500_000_000L
        }
        shadowOf(am).setMemoryInfo(info)
        assertEquals(4_000_000_000L, MemoryUtils.getTotalMemory(context))
        assertEquals(1_500_000_000L, MemoryUtils.getAvailableMemory(context))
    }

    @Test
    fun internalStorage() {
        // ShadowStatFs 的块大小固定为 4096
        ShadowStatFs.registerStats(Environment.getDataDirectory(), 1000, 400, 300)
        assertEquals(1000L * 4096, MemoryUtils.getTotalInternalMemorySize())
        assertEquals(300L * 4096, MemoryUtils.getAvailableInternalMemorySize())
    }

    @Test
    fun externalStorageWhenMounted() {
        ShadowEnvironment.setExternalStorageState(Environment.MEDIA_MOUNTED)
        val dir = requireNotNull(context.getExternalFilesDir(null))
        ShadowStatFs.registerStats(dir, 2000, 900, 800)
        assertTrue(MemoryUtils.isExternalMemoryAvailable())
        assertEquals(2000L * 4096, MemoryUtils.getTotalExternalMemorySize(context))
        assertEquals(800L * 4096, MemoryUtils.getAvailableExternalMemorySize(context))
    }

    @Test
    fun externalStorageUnavailableReturnsMinusOne() {
        ShadowEnvironment.setExternalStorageState(Environment.MEDIA_REMOVED)
        assertFalse(MemoryUtils.isExternalMemoryAvailable())
        assertEquals(-1L, MemoryUtils.getTotalExternalMemorySize(context))
        assertEquals(-1L, MemoryUtils.getAvailableExternalMemorySize(context))
    }
}
