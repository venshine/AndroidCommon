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

import org.junit.Assert.assertEquals
import org.junit.Test

/** 纯 JVM 测试。期望值由 Python hashlib 独立计算。 */
class DigestUtilsTest {

    @Test
    fun md5() {
        assertEquals("5d41402abc4b2a76b9719d911017c592", DigestUtils.md5("hello"))
        assertEquals("d41d8cd98f00b204e9800998ecf8427e", DigestUtils.md5(""))
        assertEquals("a7bac2239fcdcb3a067903d8077c4a07", DigestUtils.md5("中文")) // 按 UTF-8 编码
    }

    @Test
    fun sha1() {
        assertEquals("aaf4c61ddcc5e8a2dabede0f3b482cd9aea9434d", DigestUtils.sha1("hello"))
        assertEquals("da39a3ee5e6b4b0d3255bfef95601890afd80709", DigestUtils.sha1(""))
    }

    @Test
    fun sha256() {
        assertEquals("2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824", DigestUtils.sha256("hello"))
        assertEquals("72726d8818f693066ceb69afa364218b692e62ea92b385782363780f47529c21", DigestUtils.sha256("中文"))
    }

    @Test
    fun byteArrayOverloadMatchesString() {
        assertEquals(DigestUtils.md5("hello"), DigestUtils.md5("hello".toByteArray()))
        assertEquals(DigestUtils.sha256("hello"), DigestUtils.sha256("hello".toByteArray()))
    }

    @Test
    fun toHexIsLowerCaseAndZeroPadded() {
        assertEquals("00ff0a", DigestUtils.toHex(byteArrayOf(0x00, 0xFF.toByte(), 0x0A)))
        assertEquals("", DigestUtils.toHex(ByteArray(0)))
    }
}
