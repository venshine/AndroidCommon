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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** 纯 JVM 测试 */
class UrlUtilsTest {

    @Test
    fun parseFullUrl() {
        val p = UrlUtils.parse("http://example.com:8080/a/b?x=1&y=&z&name=%E4%B8%AD#frag")
        assertEquals("http", p.protocol)
        assertEquals("example.com", p.host)
        assertEquals(8080, p.port)
        assertEquals("/a/b", p.path)
        assertEquals("1", p.params["x"])
        assertNull(p.params["y"]) // 空值
        assertTrue(p.params.containsKey("y"))
        assertTrue(p.params.containsKey("z")) // 无等号的参数
        assertNull(p.params["z"])
        assertEquals("中", p.params["name"]) // 参数值被解码
        assertEquals(4, p.params.size) // 片段 #frag 不算参数
    }

    @Test
    fun parseWithoutPortPathOrQuery() {
        val p = UrlUtils.parse("https://example.com")
        assertEquals("https", p.protocol)
        assertEquals("example.com", p.host)
        assertEquals(-1, p.port)
        assertEquals("", p.path)
        assertTrue(p.params.isEmpty())
    }

    @Test(expected = IllegalArgumentException::class)
    fun parseEmptyThrows() {
        UrlUtils.parse("")
    }

    @Test(expected = IllegalArgumentException::class)
    fun parseIllegalThrows() {
        UrlUtils.parse("http://exa mple.com")
    }

    @Test
    fun encodeAndDecode() {
        assertEquals("a+b%26%E4%B8%AD", UrlUtils.encode("a b&中"))
        assertEquals("a b&中", UrlUtils.decode("a+b%26%E4%B8%AD"))
    }

    @Test
    fun decodeIllegalReturnsOriginal() {
        assertEquals("%zz", UrlUtils.decode("%zz"))
    }
}
