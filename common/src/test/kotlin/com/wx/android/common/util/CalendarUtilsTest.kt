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

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

/** 纯 JVM 测试：默认时区固定为 UTC、Locale 固定为 US，保证结果稳定 */
class CalendarUtilsTest {

    private lateinit var oldZone: TimeZone
    private lateinit var oldLocale: Locale

    @Before
    fun setUp() {
        oldZone = TimeZone.getDefault()
        oldLocale = Locale.getDefault()
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
        Locale.setDefault(Locale.US)
    }

    @After
    fun tearDown() {
        TimeZone.setDefault(oldZone)
        Locale.setDefault(oldLocale)
    }

    private fun millis(y: Int, m: Int, d: Int, h: Int = 0, min: Int = 0): Long =
        Calendar.getInstance().apply {
            clear()
            set(y, m - 1, d, h, min)
        }.timeInMillis

    @Test
    fun monthDays() {
        assertEquals(29, CalendarUtils.getMonthDays(2024, 2))
        assertEquals(28, CalendarUtils.getMonthDays(2023, 2))
        assertEquals(31, CalendarUtils.getMonthDays(2024, 12))
        assertEquals(30, CalendarUtils.getMonthDays(2024, 4))
        assertEquals(31, CalendarUtils.getMonthDays(2023, 13)) // 滚动到 2024 年 1 月
        assertEquals(31, CalendarUtils.getMonthDays(2024, 0)) // 滚动到 2023 年 12 月
    }

    @Test
    fun monthDayOfWeek() {
        assertEquals(0, CalendarUtils.getMonthDayOfWeek(2024, 9)) // 2024-09-01 星期日
        assertEquals(4, CalendarUtils.getMonthDayOfWeek(2024, 2)) // 2024-02-01 星期四
        assertEquals(1, CalendarUtils.getMonthDayOfWeek(2024, 4)) // 2024-04-01 星期一
        assertEquals(CalendarUtils.getMonthDayOfWeek(2023, 12), CalendarUtils.getMonthDayOfWeek(2024, 0))
    }

    @Test
    fun currentValuesMatchCalendar() {
        val now = Calendar.getInstance()
        assertEquals(now.get(Calendar.YEAR), CalendarUtils.getCurrentYear())
        assertEquals(CalendarUtils.getMonthDays(CalendarUtils.getCurrentYear(), CalendarUtils.getCurrentMonth()),
            CalendarUtils.getCurrentMonthEndDay())
        assertEquals(1, CalendarUtils.getCurrentMonthFirstDay())
        assertEquals(1, CalendarUtils.getNextMonthFirstDay())
        assertEquals(CalendarUtils.getNextMonthDays(), CalendarUtils.getNextMonthEndDay())
        assertEquals(CalendarUtils.getLastMonthDays(), CalendarUtils.getLastMonthEndDay())
    }

    @Test
    fun dateStrings() {
        assertEquals("20240305", CalendarUtils.getDate(2024, 3, 5))
        assertEquals("20241231", CalendarUtils.getDate(2024, 12, 31))
        assertEquals("20240301", CalendarUtils.getDate(2024, 3))
        assertEquals("20240305", CalendarUtils.getDate(millis(2024, 3, 5, 10)))
        assertEquals(8, CalendarUtils.getCurrentDate().length)
    }

    @Test
    fun formatAndParse() {
        assertEquals("", CalendarUtils.getTime(0L))
        assertEquals("", CalendarUtils.getTime(-1L))
        assertEquals("2023-11-14 22:13", CalendarUtils.getTime(1700000000000L, "yyyy-MM-dd HH:mm"))
        assertEquals("20231114", CalendarUtils.getTime(1700000000000L)) // 默认 yyyyMMdd
        assertEquals("20231114", CalendarUtils.getTime(1700000000000L, ""))
        val parsed = CalendarUtils.getTime("20240305")
        assertEquals(millis(2024, 3, 5), parsed)
        assertEquals(millis(2024, 3, 5, 10, 30), CalendarUtils.getTime("2024-03-05 10:30", "yyyy-MM-dd HH:mm"))
        assertEquals(0L, CalendarUtils.getTime("not a date"))
        assertEquals(0L, CalendarUtils.getTime(null as String?))
    }

    @Test
    fun timeFormatHasMillisecondField() {
        // 旧常量里的 "ms" 会被当成分钟 + 秒；现在是毫秒 SSS，结果形如 2024-03-05 10:30:15:123
        assertTrue(Regex("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}:\\d{3}").matches(CalendarUtils.getTime()))
    }

    @Test
    fun week() {
        assertEquals("TUES", CalendarUtils.getWeek("20240305"))
        assertEquals("SUN", CalendarUtils.getWeek("20240901"))
        assertEquals("", CalendarUtils.getWeek("garbage"))
        assertEquals("", CalendarUtils.getWeek(null))
    }

    @Test
    fun isTodayHasUpperBound() {
        val sys = millis(2024, 3, 5, 12)
        assertTrue(CalendarUtils.isToday(sys, millis(2024, 3, 5, 0)))
        assertTrue(CalendarUtils.isToday(sys, millis(2024, 3, 5, 23, 59)))
        assertFalse(CalendarUtils.isToday(sys, millis(2024, 3, 6, 0))) // 次日 0 点不是今天
        assertFalse(CalendarUtils.isToday(sys, millis(2024, 3, 4, 23, 59)))
    }

    @Test
    fun isYesterday() {
        val sys = millis(2024, 3, 5, 12)
        assertTrue(CalendarUtils.isYesterday(sys, millis(2024, 3, 4, 8)))
        assertTrue(CalendarUtils.isYesterday(sys, millis(2024, 3, 4, 0)))
        assertFalse(CalendarUtils.isYesterday(sys, millis(2024, 3, 5, 0))) // 今天
        assertFalse(CalendarUtils.isYesterday(sys, millis(2024, 3, 3, 23, 59)))
    }

    @Test
    fun printDate() {
        val sys = millis(2024, 3, 5, 12)
        assertEquals("just now", CalendarUtils.printDate(sys, sys - 30_000))
        assertEquals("5 minutes ago", CalendarUtils.printDate(sys, sys - 5 * 60_000))
        assertEquals("today 09:00", CalendarUtils.printDate(sys, millis(2024, 3, 5, 9)))
        assertEquals("yesterday 08:00", CalendarUtils.printDate(sys, millis(2024, 3, 4, 8)))
        assertEquals("02-20 10:30", CalendarUtils.printDate(sys, millis(2024, 2, 20, 10, 30)))
    }

    @Test
    fun constants() {
        assertEquals(12, CalendarUtils.MONTHS.size)
        assertEquals(12, CalendarUtils.MONTHS2.size)
        assertEquals(7, CalendarUtils.WEEKS.size)
        assertEquals(7, CalendarUtils.WEEKS2.size)
        assertEquals(86_400_000, CalendarUtils.DAY)
    }
}
