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
import android.widget.LinearLayout
import android.widget.TextView
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class ViewUtilsTest {

    private val context: Context = RuntimeEnvironment.getApplication()

    @Test
    fun textHeightGrowsWithLineCount() {
        // Robolectric 的文本测量不会按宽度折行，所以这里用显式换行验证"行数越多越高"
        val tv = TextView(context).apply { textSize = 16f }
        tv.text = "one line"
        val oneLine = ViewUtils.getTextViewHeight(tv, 2000)
        assertTrue(oneLine > 0)
        tv.text = "1\n2\n3\n4"
        assertTrue(ViewUtils.getTextViewHeight(tv, 2000) > oneLine * 3)
    }

    @Test
    fun textHeightWorksWithoutLayoutParams() {
        val tv = TextView(context).apply { text = "x".repeat(200) }
        assertNull(tv.layoutParams)
        assertTrue(ViewUtils.getTextViewHeight(tv, 100) > 0) // 没有 LayoutParams 时不再 NPE
    }

    @Test
    fun textHeightWithoutWidthUsesScreenWidth() {
        val tv = TextView(context).apply { text = "hello" }
        assertEquals(
            ViewUtils.getTextViewHeight(tv, DisplayUtils.getScreenWidth(context)),
            ViewUtils.getTextViewHeight(tv)
        )
    }

    @Test
    fun measureViewHeightUsesWrapContent() {
        val tv = TextView(context).apply { text = "hello" }
        assertTrue(ViewUtils.measureViewHeight(tv) > 0)
        assertEquals(ViewUtils.measureViewHeight(tv), tv.measuredHeight)
    }

    @Test
    fun findTextViewWalksFirstChildren() {
        val tv = TextView(context)
        val inner = LinearLayout(context).apply { addView(tv) }
        val outer = LinearLayout(context).apply { addView(inner) }
        assertSame(tv, ViewUtils.findTextView(outer))
        assertSame(tv, ViewUtils.findTextView(tv))
    }

    @Test
    fun findTextViewReturnsNullWhenMissing() {
        assertNull(ViewUtils.findTextView(null))
        assertNull(ViewUtils.findTextView(View(context)))
        assertNull(ViewUtils.findTextView(LinearLayout(context))) // 空容器不再崩溃
    }
}
