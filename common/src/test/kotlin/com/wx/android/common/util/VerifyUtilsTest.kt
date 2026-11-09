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

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar
import java.util.GregorianCalendar

/**
 * 纯 JVM 测试。
 *
 * 身份证样例来自 GB 11643-1999 附录 A / B 的实例（转录见 VerifyUtils 的 KDoc）：
 * 11010519491231002X（A1、B1）、440524188001010014（A2、B2）。
 */
class VerifyUtilsTest {

    /** 按标准公式 W_i = 2^(i-1) mod 11（i 为从右数的位置）独立计算校验码，与实现里的权重表互相校验 */
    private fun withCheckCode(body17: String): String {
        var sum = 0
        for (idx in 0 until 17) {
            val i = 18 - idx // 位置序号，最右为 1，校验码位为 1，本体第一位为 18
            var w = 1
            repeat(i - 1) { w = w * 2 % 11 }
            sum += (body17[idx] - '0') * w
        }
        // a1 + sum ≡ 1 (mod 11) -> a1 = (12 - sum % 11) % 11
        val a1 = (12 - sum % 11) % 11
        return body17 + if (a1 == 10) "X" else a1.toString()
    }

    private fun day(y: Int, m: Int, d: Int): Calendar = GregorianCalendar(y, m - 1, d)

    @Test
    fun idCardStandardExamples() {
        val today = day(2026, 1, 1)
        assertTrue(VerifyUtils.isIdCard("11010519491231002X", today))
        assertTrue(VerifyUtils.isIdCard("440524188001010014", today))
    }

    @Test
    fun idCardAcceptsLowercaseX() {
        assertTrue(VerifyUtils.isIdCard("11010519491231002x", day(2026, 1, 1)))
    }

    @Test
    fun independentFormulaAgreesWithStandardExamples() {
        assertTrue(withCheckCode("11010519491231002") == "11010519491231002X")
        assertTrue(withCheckCode("44052418800101001") == "440524188001010014")
    }

    @Test
    fun idCardRejectsWrongCheckCode() {
        val today = day(2026, 1, 1)
        assertFalse(VerifyUtils.isIdCard("110105194912310021", today))
        assertFalse(VerifyUtils.isIdCard("440524188001010010", today))
    }

    @Test
    fun idCardRejectsBadLengthAndChars() {
        val today = day(2026, 1, 1)
        assertFalse(VerifyUtils.isIdCard(null, today))
        assertFalse(VerifyUtils.isIdCard("", today))
        assertFalse(VerifyUtils.isIdCard("11010519491231002", today)) // 17 位
        assertFalse(VerifyUtils.isIdCard("11010519491231002XX", today)) // 19 位
        assertFalse(VerifyUtils.isIdCard("110105491231002", today)) // 15 位旧号码
        assertFalse(VerifyUtils.isIdCard("1101051949123100aX", today)) // 本体含字母
    }

    @Test
    fun idCardRejectsImpossibleBirthdayEvenWithValidCheckCode() {
        val today = day(2026, 1, 1)
        assertFalse(VerifyUtils.isIdCard(withCheckCode("11010519900230002"), today)) // 2 月 30 日
        assertFalse(VerifyUtils.isIdCard(withCheckCode("11010519901301002"), today)) // 13 月
        assertFalse(VerifyUtils.isIdCard(withCheckCode("11010519900000002"), today)) // 0 月 0 日
        assertFalse(VerifyUtils.isIdCard(withCheckCode("11010519990229002"), today)) // 1999 非闰年
        assertTrue(VerifyUtils.isIdCard(withCheckCode("11010520000229002"), today)) // 2000 闰年 2 月 29 日
    }

    @Test
    fun idCardRejectsFutureBirthday() {
        val today = day(2026, 6, 15)
        assertTrue(VerifyUtils.isIdCard(withCheckCode("11010520260615002"), today)) // 今天
        assertFalse(VerifyUtils.isIdCard(withCheckCode("11010520260616002"), today)) // 明天
        assertFalse(VerifyUtils.isIdCard(withCheckCode("11010529990101002"), today))
    }

    @Test
    fun idCardPublicEntryUsesRealToday() {
        assertTrue(VerifyUtils.isIdCard("11010519491231002X"))
        assertFalse(VerifyUtils.isIdCard(null))
    }

    @Test
    fun mobileNumber() {
        assertTrue(VerifyUtils.isMobileNumber("13800138000"))
        assertTrue(VerifyUtils.isMobileNumber("19912345678"))
        assertTrue(VerifyUtils.isMobileNumber("14712345678"))
        assertFalse(VerifyUtils.isMobileNumber("12345678901")) // 第二位不是 3-9
        assertFalse(VerifyUtils.isMobileNumber("23800138000")) // 不以 1 开头
        assertFalse(VerifyUtils.isMobileNumber("1380013800")) // 10 位
        assertFalse(VerifyUtils.isMobileNumber("138001380000")) // 12 位
        assertFalse(VerifyUtils.isMobileNumber("1380013800a"))
        assertFalse(VerifyUtils.isMobileNumber(""))
        assertFalse(VerifyUtils.isMobileNumber(null))
    }

    @Test
    fun chinese() {
        assertTrue(VerifyUtils.isChinese("中文"))
        assertTrue(VerifyUtils.isChinese("𠀀")) // 扩展 B 区汉字（代理对）
        assertFalse(VerifyUtils.isChinese("中a"))
        assertFalse(VerifyUtils.isChinese("αβγ")) // 希腊字母不是汉字
        assertFalse(VerifyUtils.isChinese(""))
        assertFalse(VerifyUtils.isChinese(null))
    }

    @Test
    fun containsChinese() {
        assertTrue(VerifyUtils.isContainsChinese("ab中"))
        assertTrue(VerifyUtils.isContainsChinese("𠀀x"))
        assertFalse(VerifyUtils.isContainsChinese("abc123"))
        assertFalse(VerifyUtils.isContainsChinese(""))
        assertFalse(VerifyUtils.isContainsChinese(null))
    }
}
