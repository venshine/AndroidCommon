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
import android.content.pm.ShortcutManager
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class ShortcutUtilsTest {

    private val context: Context = RuntimeEnvironment.getApplication()
    private val intent = Intent("demo.OPEN")

    @Test
    fun requestPinWhenSupported() {
        shadowOf(context.getSystemService(ShortcutManager::class.java)).setIsRequestPinShortcutSupported(true)
        assertTrue(ShortcutUtils.isRequestPinSupported(context))
        assertTrue(ShortcutUtils.requestPin(context, "id1", "label", android.R.drawable.ic_dialog_info, intent))
    }

    @Test
    fun requestPinReturnsFalseWhenLauncherDoesNotSupport() {
        shadowOf(context.getSystemService(ShortcutManager::class.java)).setIsRequestPinShortcutSupported(false)
        assertFalse(ShortcutUtils.isRequestPinSupported(context))
        assertFalse(ShortcutUtils.requestPin(context, "id1", "label", android.R.drawable.ic_dialog_info, intent))
    }
}
