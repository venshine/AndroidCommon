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
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class InputMethodUtilsTest {

    private fun setUp(): Triple<Activity, EditText, InputMethodManager> {
        val activity = Robolectric.buildActivity(Activity::class.java).setup().get()
        val edit = EditText(activity)
        activity.setContentView(edit)
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        return Triple(activity, edit, imm)
    }

    @Test
    fun showAndHideByView() {
        val (_, edit, imm) = setUp()
        InputMethodUtils.showSoftInput(edit)
        assertTrue(shadowOf(imm).isSoftInputVisible)
        InputMethodUtils.hideSoftInput(edit)
        assertFalse(shadowOf(imm).isSoftInputVisible)
    }

    @Test
    fun showRequestsFocus() {
        val (_, edit, _) = setUp()
        assertFalse(edit.hasFocus())
        InputMethodUtils.showSoftInput(edit)
        assertTrue(edit.isFocused)
    }

    @Test
    fun activityOverloadsNeedFocusedView() {
        val activity = Robolectric.buildActivity(Activity::class.java).setup().get()
        assertFalse(InputMethodUtils.showSoftInput(activity)) // 没有焦点 View
        assertFalse(InputMethodUtils.hideSoftInput(activity))
    }

    @Test
    fun isActiveDoesNotThrow() {
        val (activity, _, _) = setUp()
        InputMethodUtils.isActive(activity)
    }
}
