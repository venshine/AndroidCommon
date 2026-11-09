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
package com.wx.android.common.demo

import android.widget.ImageView
import android.widget.TextView
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode
import org.robolectric.shadows.ShadowToast

/** 示例页冒烟测试：每个按钮点一遍，确认结果输出和库的行为一致 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class MainActivityTest {

    private val activity = Robolectric.buildActivity(MainActivity::class.java).setup().get()
    private val output get() = activity.findViewById<TextView>(R.id.output).text.toString()

    private fun click(id: Int) {
        activity.findViewById<android.view.View>(id).performClick()
    }

    @Test
    fun digestButton() {
        click(R.id.btn_digest)
        assertTrue(output.contains("5d41402abc4b2a76b9719d911017c592")) // md5(hello)
        assertTrue(output.contains("2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824"))
    }

    @Test
    fun verifyButton() {
        click(R.id.btn_verify)
        assertTrue(output.contains("isIdCard(11010519491231002X) = true"))
        assertTrue(output.contains("isMobileNumber(13800138000) = true"))
    }

    @Test
    fun toastButtonUsesToastUtilsInitializedByApplication() {
        click(R.id.btn_toast)
        assertEquals("Hello AndroidCommon", ShadowToast.getTextOfLatestToast())
    }

    @Test
    fun clipboardButton() {
        click(R.id.btn_clipboard)
        assertEquals("clipboard = AndroidCommon sample", output)
    }

    @Test
    fun bitmapButtonShowsGreyImage() {
        click(R.id.btn_bitmap)
        assertNotNull(activity.findViewById<ImageView>(R.id.image).drawable)
    }

    @Test
    fun appInfoButton() {
        click(R.id.btn_app)
        assertTrue(output.contains("AndroidCommon"))
        assertTrue(output.contains("network available ="))
    }
}
