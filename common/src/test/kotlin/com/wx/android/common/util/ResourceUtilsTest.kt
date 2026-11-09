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
import com.wx.android.common.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

/** 夹具文件放在 src/debug/assets 与 src/debug/res/raw，只进 debug 变体，不会打进发布的 AAR */
@RunWith(RobolectricTestRunner::class)
class ResourceUtilsTest {

    private val context: Context = RuntimeEnvironment.getApplication()

    @Test
    fun assetText() {
        assertEquals("hello assets\n中文", ResourceUtils.getTextFromAssets(context, "test_text.txt"))
    }

    @Test
    fun assetTextWithOtherCharset() {
        // 把 UTF-8 的中文按 ISO-8859-1 读出来必然与原文不同，说明 charset 参数生效
        val latin = ResourceUtils.getTextFromAssets(context, "test_text.txt", Charsets.ISO_8859_1)
        assertEquals(true, latin != "hello assets\n中文")
    }

    @Test
    fun missingAssetReturnsNull() {
        assertNull(ResourceUtils.getTextFromAssets(context, "no_such_file.txt"))
    }

    @Test
    fun rawText() {
        assertEquals("hello raw", ResourceUtils.getRawText(context, R.raw.test_raw))
    }

    @Test
    fun invalidRawIdReturnsNull() {
        assertNull(ResourceUtils.getRawText(context, 0x7f0f0000))
    }

    @Test
    fun resourceByName() {
        assertEquals(R.raw.test_raw, ResourceUtils.getResourceByName(context, "test_raw", "raw"))
        assertEquals(0, ResourceUtils.getResourceByName(context, "not_exist", "raw"))
    }
}
