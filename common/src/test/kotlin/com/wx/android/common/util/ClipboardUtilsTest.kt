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

import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class ClipboardUtilsTest {

    private val context: Context = RuntimeEnvironment.getApplication()

    @Test
    fun emptyClipboardReturnsNull() {
        assertNull(ClipboardUtils.getText(context))
        assertNull(ClipboardUtils.getUri(context))
        assertNull(ClipboardUtils.getIntent(context))
    }

    @Test
    fun copyAndReadText() {
        ClipboardUtils.copy(context, "hello 中文")
        assertEquals("hello 中文", ClipboardUtils.getText(context))
        assertNull(ClipboardUtils.getUri(context)) // 类型不符的读取返回 null
    }

    @Test
    fun copyAndReadUri() {
        val uri = Uri.parse("content://com.example/item/1")
        ClipboardUtils.copy(context, uri)
        assertEquals(uri, ClipboardUtils.getUri(context))
    }

    @Test
    fun copyAndReadIntent() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://example.com"))
        ClipboardUtils.copy(context, intent)
        assertEquals(Intent.ACTION_VIEW, ClipboardUtils.getIntent(context)?.action)
    }

    @Test
    fun sensitiveFlagIsWritten() {
        ClipboardUtils.copy(context, "secret", sensitive = true)
        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val extras = cm.primaryClip!!.description.extras
        assertTrue(extras!!.getBoolean(ClipDescription.EXTRA_IS_SENSITIVE))
    }

    @Test
    fun nonSensitiveCopyHasNoFlag() {
        ClipboardUtils.copy(context, "plain")
        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val extras = cm.primaryClip!!.description.extras
        assertTrue(extras == null || !extras.getBoolean(ClipDescription.EXTRA_IS_SENSITIVE))
    }
}
