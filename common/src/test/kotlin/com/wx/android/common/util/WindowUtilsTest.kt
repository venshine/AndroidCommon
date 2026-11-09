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
import android.view.View
import android.view.WindowManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.shadow.api.Shadow
import org.robolectric.shadows.ShadowSettings
import org.robolectric.shadows.ShadowWindowManagerImpl

@RunWith(RobolectricTestRunner::class)
class WindowUtilsTest {

    private val context: Context = RuntimeEnvironment.getApplication()

    private fun windowViews(): List<View> {
        val wm = context.applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        return Shadow.extract<ShadowWindowManagerImpl>(wm).views
    }

    @Test
    fun defaultParamsUseApplicationOverlay() {
        val p = WindowUtils.createDefaultLayoutParams()
        assertEquals(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, p.type)
        assertEquals(WindowManager.LayoutParams.MATCH_PARENT, p.width)
        assertTrue(p.flags and WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE != 0)
        assertTrue(p.flags and WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE != 0)
    }

    @Test
    fun addViewWithoutPermissionReturnsFalse() {
        ShadowSettings.setCanDrawOverlays(false)
        val view = View(context)
        assertFalse(WindowUtils.canDrawOverlays(context))
        assertFalse(WindowUtils.addView(context, view))
        assertFalse(windowViews().contains(view))
    }

    @Test
    fun addUpdateRemoveWithPermission() {
        ShadowSettings.setCanDrawOverlays(true)
        val view = View(context)
        assertTrue(WindowUtils.addView(context, view))
        assertTrue(windowViews().contains(view))
        val params = WindowUtils.createDefaultLayoutParams().apply { width = 100 }
        WindowUtils.updateView(context, view, params)
        WindowUtils.removeView(context, view)
        assertFalse(windowViews().contains(view))
    }
}
