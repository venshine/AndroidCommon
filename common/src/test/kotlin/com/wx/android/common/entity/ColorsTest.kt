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
package com.wx.android.common.entity

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** 纯 JVM 测试 */
class ColorsTest {

    @Test
    fun argbValues() {
        assertEquals(0x00000000, Colors.TRANSPARENT)
        assertEquals(0xFF000000.toInt(), Colors.BLACK)
        assertEquals(0xFFFFFFFF.toInt(), Colors.WHITE)
        assertEquals(0xFFFF0000.toInt(), Colors.RED)
        assertEquals(0x80000000.toInt(), Colors.TRANSLUCENT)
    }

    @Test
    fun allColorsAreOpaqueExceptTransparentOnes() {
        val opaque = listOf(
            Colors.BLACK, Colors.WHITE, Colors.RED, Colors.RED_DARK, Colors.GREEN, Colors.BLUE, Colors.BLUE_DARK,
            Colors.BLUE_LIGHT, Colors.BLUE_SKY, Colors.YELLOW, Colors.GRAY, Colors.GRAY_DARK, Colors.GRAY_LIGHT,
            Colors.CYAN, Colors.ORANGE, Colors.MAGENTA, Colors.PINK, Colors.PURPLE, Colors.CHOCOLATE, Colors.TOMATO,
            Colors.BROWN, Colors.SILVER, Colors.GOLD, Colors.OLIVE, Colors.VIOLET, Colors.KHAKI
        )
        opaque.forEach { assertEquals(0xFF, it ushr 24) }
        assertTrue(Colors.TRANSPARENT ushr 24 == 0 && Colors.TRANSLUCENT ushr 24 == 0x80)
    }
}
