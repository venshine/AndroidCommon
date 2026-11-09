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
package com.wx.android.common.widget

import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class CommonEditTextTest {

    private fun newEdit(text: String = "abc") = CommonEditText(RuntimeEnvironment.getApplication()).apply { setText(text) }

    private fun connection(edit: CommonEditText) = edit.onCreateInputConnection(EditorInfo())

    @Test
    fun hasEditTextDefaultStyle() {
        // 默认的 defStyle 必须是 editTextStyle，否则用代码创建的输入框没有下划线 / 光标等外观
        val edit = newEdit()
        assertTrue(edit.isFocusable)
        assertTrue(edit.onCheckIsTextEditor())
    }

    @Test
    fun deleteKeyListenerConsumesDownEvent() {
        val edit = newEdit()
        var deletes = 0
        edit.setOnDelKeyEventListener { deletes++ }
        val ic = connection(edit)!!
        assertTrue(ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL)))
        assertEquals(1, deletes)
        assertEquals("abc", edit.text.toString()) // 事件被监听器拦截，文字没有被删
    }

    @Test
    fun deleteKeyUpEventIsNotCounted() {
        val edit = newEdit()
        var deletes = 0
        edit.setOnDelKeyEventListener { deletes++ }
        connection(edit)!!.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL))
        assertEquals(0, deletes)
    }

    @Test
    fun otherKeysAreNotIntercepted() {
        val edit = newEdit()
        var deletes = 0
        edit.setOnDelKeyEventListener { deletes++ }
        connection(edit)!!.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_A))
        assertEquals(0, deletes)
    }

    @Test
    fun deleteSurroundingTextOneBeforeIsReportedAsDeleteKey() {
        val edit = newEdit()
        var deletes = 0
        edit.setOnDelKeyEventListener { deletes++ }
        connection(edit)!!.deleteSurroundingText(1, 0)
        assertEquals(1, deletes) // 部分输入法用它实现退格
    }

    @Test
    fun deleteSurroundingTextOtherLengthsAreNotReported() {
        val edit = newEdit()
        var deletes = 0
        edit.setOnDelKeyEventListener { deletes++ }
        connection(edit)!!.deleteSurroundingText(2, 0)
        assertEquals(0, deletes)
    }

    @Test
    fun withoutListenerDeleteBehavesNormally() {
        val edit = newEdit()
        assertNotNull(connection(edit))
        edit.setOnDelKeyEventListener(null)
        val ic = connection(edit)!!
        ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL)) // 不抛异常即可
    }

    @Test
    fun backKeyListenerFiresOnDownOnly() {
        val edit = newEdit()
        var backs = 0
        edit.setOnBackKeyEventListener { backs++ }
        edit.onKeyPreIme(KeyEvent.KEYCODE_BACK, KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK))
        assertEquals(0, backs)
        edit.onKeyPreIme(KeyEvent.KEYCODE_BACK, KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK))
        assertEquals(1, backs)
        edit.onKeyPreIme(KeyEvent.KEYCODE_A, KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_A))
        assertEquals(1, backs)
    }

    @Test
    fun backKeyWithoutListenerDoesNotCrash() {
        val edit = newEdit()
        edit.onKeyPreIme(KeyEvent.KEYCODE_BACK, KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK)) // 不抛异常即可
    }
}
