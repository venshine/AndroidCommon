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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "xxhdpi") // density = 3.0
class DisplayUtilsTest {

    private val context: Context = RuntimeEnvironment.getApplication()

    @Test
    fun dipAndPxConversions() {
        assertEquals(3f, DisplayUtils.getScreenDensity(context), 0f)
        assertEquals(480, DisplayUtils.getScreenDensityDPI(context))
        assertEquals(30, DisplayUtils.dip2px(context, 10f))
        assertEquals(10, DisplayUtils.px2dip(context, 30f))
        assertEquals(5, DisplayUtils.dip2px(context, 1.5f)) // 4.5 四舍五入
        assertEquals(0, DisplayUtils.dip2px(context, 0f))
    }

    @Test
    fun spConversionsFollowFontScale() {
        assertEquals(36, DisplayUtils.sp2px(context, 12f)) // fontScale = 1
        assertEquals(12, DisplayUtils.px2sp(context, 36f))
        context.resources.displayMetrics.scaledDensity = 4.5f // 用户把字体放大到 1.5 倍
        assertEquals(54, DisplayUtils.sp2px(context, 12f))
    }

    @Test
    fun screenSizeMatchesDisplayMetrics() {
        val dm = context.resources.displayMetrics
        assertEquals(dm.widthPixels, DisplayUtils.getScreenWidth(context))
        assertEquals(dm.heightPixels, DisplayUtils.getScreenHeight(context))
        assertTrue(DisplayUtils.getScreenWidth(context) > 0)
    }

    @Test
    fun orientationPortrait() {
        assertTrue(DisplayUtils.isPortrait(context))
        assertFalse(DisplayUtils.isLandscape(context))
    }

    @Test
    @Config(qualifiers = "land-xxhdpi")
    fun orientationLandscape() {
        assertTrue(DisplayUtils.isLandscape(context))
        assertFalse(DisplayUtils.isPortrait(context))
    }

    @Test
    fun statusBarHeightFallsBackToSystemResource() {
        val activity = Robolectric.buildActivity(Activity::class.java).create().get()
        // 窗口未附加时取不到 insets，回退到系统资源 status_bar_height
        assertTrue(DisplayUtils.getStatusBarHeight(activity) > 0)
    }
}
