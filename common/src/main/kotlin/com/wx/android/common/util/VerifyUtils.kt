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

import java.util.Calendar
import java.util.GregorianCalendar

/**
 * 字符串格式校验。
 *
 * 邮箱、URL 请直接使用 `android.util.Patterns.EMAIL_ADDRESS` / `Patterns.WEB_URL`。
 *
 * @author venshine
 */
object VerifyUtils {

    private val MOBILE = Regex("^1[3-9]\\d{9}$")

    /** 身份证前 17 位的加权因子（GB 11643-1999 表 1，从左到右） */
    private val ID_WEIGHTS = intArrayOf(7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2)

    /** 加权和 mod 11 的结果（0..10）依次对应的校验码（GB 11643-1999 表 2） */
    private const val ID_CHECK_CODES = "10X98765432"

    /**
     * 是否为大陆手机号格式：11 位数字、以 1 开头、第二位 3-9。
     *
     * 只校验格式，**不校验号段是否真实分配**。
     */
    @JvmStatic
    fun isMobileNumber(number: String?): Boolean = number != null && MOBILE.matches(number)

    /**
     * 是否为合法的 18 位居民身份证号码（中华人民共和国公民身份号码）。
     *
     * 校验内容：18 位长度、前 17 位为数字、出生日期码是真实存在且不晚于今天的日期、
     * 末位校验码正确（`x` 大小写都接受）。不校验地址码是否存在，不支持 15 位旧号码。
     *
     * 依据：GB 11643-1999《公民身份号码》5.1.4 校验公式与表 1、表 2，
     * 条文转录见 https://zh.wikisource.org/wiki/GB_11643-1999_公民身份号码
     * （转录件，非官方 PDF；权重 7 9 10 5 8 4 2 1 6 3 7 9 10 5 8 4 2，
     * 加权和 mod 11 的 0..10 对应校验码 1 0 X 9 8 7 6 5 4 3 2，并用其附录 A / B 的两个实例核对过）。
     */
    @JvmStatic
    fun isIdCard(id: String?): Boolean = isIdCard(id, Calendar.getInstance())

    /** 同 [isIdCard]，[today] 用于指定"今天"，便于测试 */
    internal fun isIdCard(id: String?, today: Calendar): Boolean {
        if (id == null || id.length != 18) return false
        var sum = 0
        for (i in 0 until 17) {
            val c = id[i]
            if (c !in '0'..'9') return false
            sum += (c - '0') * ID_WEIGHTS[i]
        }
        if (!isValidBirthday(id.substring(6, 14), today)) return false
        return id[17].uppercaseChar() == ID_CHECK_CODES[sum % 11]
    }

    private fun isValidBirthday(yyyyMMdd: String, today: Calendar): Boolean {
        val year = yyyyMMdd.substring(0, 4).toInt()
        val month = yyyyMMdd.substring(4, 6).toInt()
        val day = yyyyMMdd.substring(6, 8).toInt()
        if (year < 1) return false
        val cal = GregorianCalendar(year, month - 1, day)
        cal.isLenient = false
        return try {
            // 非宽松模式下，月日越界（如 2 月 30 日）在取值时抛异常
            cal.timeInMillis <= today.timeInMillis
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    /** 汉字（Unicode Han 脚本，含扩展区）。用正则的 `\p{IsHan}` 而不是 `Character.UnicodeScript`（它需要 API 24） */
    private val ALL_HAN = Regex("^\\p{IsHan}+$")
    private val ANY_HAN = Regex("\\p{IsHan}")

    /** 是否全部由汉字组成（至少一个字符） */
    @JvmStatic
    fun isChinese(data: String?): Boolean = data != null && ALL_HAN.matches(data)

    /** 是否包含汉字 */
    @JvmStatic
    fun isContainsChinese(data: String?): Boolean = data != null && ANY_HAN.containsMatchIn(data)
}
