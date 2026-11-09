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

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * 日历 / 日期工具，基于 [Calendar]（所有 API 级别可用，无需 desugaring）。
 *
 * 月份参数统一为 1-12；越界值（0 或 13）会按日历规则滚动到上一年 / 下一年。
 * 内部固定格式使用 [Locale.US]，避免部分语言环境把数字输出成非 ASCII 数字。
 *
 * @author venshine
 */
object CalendarUtils {

    /** 日期格式 yyyyMMdd */
    const val DATE_FORMAT = "yyyyMMdd"

    /** 时间格式 yyyy-MM-dd HH:mm:ss:SSS */
    const val TIME_FORMAT = "yyyy-MM-dd HH:mm:ss:SSS"

    /** 一天的毫秒数 */
    const val DAY = 24 * 60 * 60 * 1000

    @JvmField
    val MONTHS = arrayOf("JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC")

    @JvmField
    val MONTHS2 = arrayOf(
        "January", "February", "March", "April", "May", "June", "July", "August",
        "September", "October", "November", "December"
    )

    @JvmField
    val WEEKS = arrayOf("SUN", "MON", "TUES", "WED", "THUR", "FRI", "SAT")

    @JvmField
    val WEEKS2 = arrayOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")

    /** 格式化时间戳；[pattern] 为空时用 [DATE_FORMAT]，时间戳小于等于 0 返回空串 */
    @JvmStatic
    @JvmOverloads
    fun getTime(time: Long, pattern: String? = null, locale: Locale = Locale.getDefault()): String {
        if (time <= 0) return ""
        return SimpleDateFormat(if (pattern.isNullOrEmpty()) DATE_FORMAT else pattern, locale).format(time)
    }

    /** 解析时间字符串；[pattern] 为空时用 [DATE_FORMAT]，失败返回 0 */
    @JvmStatic
    @JvmOverloads
    fun getTime(time: String?, pattern: String? = null, locale: Locale = Locale.getDefault()): Long {
        if (time == null) return 0
        return try {
            SimpleDateFormat(if (pattern.isNullOrEmpty()) DATE_FORMAT else pattern, locale).parse(time)?.time ?: 0
        } catch (e: ParseException) {
            0
        }
    }

    /** 当前时间，格式 [TIME_FORMAT] */
    @JvmStatic
    fun getTime(): String = SimpleDateFormat(TIME_FORMAT, Locale.US).format(System.currentTimeMillis())

    /** 当前日期，格式 [DATE_FORMAT] */
    @JvmStatic
    fun getCurrentDate(): String = getDate(System.currentTimeMillis())

    /** 时间戳对应的日期，格式 [DATE_FORMAT] */
    @JvmStatic
    fun getDate(time: Long): String = SimpleDateFormat(DATE_FORMAT, Locale.US).format(time)

    /** 拼接 yyyyMMdd，月、日补零 */
    @JvmStatic
    fun getDate(year: Int, month: Int, day: Int): String =
        String.format(Locale.US, "%d%02d%02d", year, month, day)

    /** 某年某月 1 日的日期，格式 [DATE_FORMAT] */
    @JvmStatic
    fun getDate(year: Int, month: Int): String = getDate(year, month, 1)

    @JvmStatic
    fun getCurrentYear(): Int = Calendar.getInstance().get(Calendar.YEAR)

    /** 当前月份，1-12 */
    @JvmStatic
    fun getCurrentMonth(): Int = Calendar.getInstance().get(Calendar.MONTH) + 1

    @JvmStatic
    fun getCurrentDay(): Int = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

    /** 某月的天数 */
    @JvmStatic
    fun getMonthDays(year: Int, month: Int): Int = firstDayOf(year, month).getActualMaximum(Calendar.DAY_OF_MONTH)

    @JvmStatic
    fun getCurrentMonthDays(): Int = getMonthDays(getCurrentYear(), getCurrentMonth())

    @JvmStatic
    fun getLastMonthDays(): Int = getMonthDays(getCurrentYear(), getCurrentMonth() - 1)

    @JvmStatic
    fun getNextMonthDays(): Int = getMonthDays(getCurrentYear(), getCurrentMonth() + 1)

    /** 本月第一天（日），恒为 1 */
    @JvmStatic
    fun getCurrentMonthFirstDay(): Int = 1

    /** 本月最后一天（日） */
    @JvmStatic
    fun getCurrentMonthEndDay(): Int = getCurrentMonthDays()

    @JvmStatic
    fun getLastMonthFirstDay(): Int = 1

    @JvmStatic
    fun getLastMonthEndDay(): Int = getLastMonthDays()

    @JvmStatic
    fun getNextMonthFirstDay(): Int = 1

    @JvmStatic
    fun getNextMonthEndDay(): Int = getNextMonthDays()

    /** 某月 1 日是星期几：0 为星期日，6 为星期六；用于排月历第一行的偏移 */
    @JvmStatic
    fun getMonthDayOfWeek(year: Int, month: Int): Int = firstDayOf(year, month).get(Calendar.DAY_OF_WEEK) - 1

    @JvmStatic
    fun getCurrentMonthDayOfWeek(): Int = getMonthDayOfWeek(getCurrentYear(), getCurrentMonth())

    @JvmStatic
    fun getLastMonthDayOfWeek(): Int = getMonthDayOfWeek(getCurrentYear(), getCurrentMonth() - 1)

    @JvmStatic
    fun getNextMonthDayOfWeek(): Int = getMonthDayOfWeek(getCurrentYear(), getCurrentMonth() + 1)

    /** 日期（[DATE_FORMAT]）对应的星期缩写，取自 [WEEKS]；解析失败返回空串 */
    @JvmStatic
    fun getWeek(date: String?): String {
        val millis = getTime(date, DATE_FORMAT, Locale.US)
        if (date == null || millis == 0L) return ""
        val cal = Calendar.getInstance().apply { timeInMillis = millis }
        return WEEKS[cal.get(Calendar.DAY_OF_WEEK) - 1]
    }

    /** [dateTime] 是否与 [sysTime] 在同一天（本地时区） */
    @JvmStatic
    fun isToday(sysTime: Long, dateTime: Long): Boolean {
        val start = startOfDay(sysTime)
        return dateTime >= start && dateTime < nextDay(start)
    }

    /** [dateTime] 是否在 [sysTime] 的前一天（本地时区） */
    @JvmStatic
    fun isYesterday(sysTime: Long, dateTime: Long): Boolean {
        val start = startOfDay(sysTime)
        val yesterdayStart = Calendar.getInstance().apply {
            timeInMillis = start
            add(Calendar.DAY_OF_MONTH, -1)
        }.timeInMillis
        return dateTime >= yesterdayStart && dateTime < start
    }

    /**
     * 友好时间：一分钟内 "just now"，一小时内 "N minutes ago"，当天 "today HH:mm"，
     * 昨天 "yesterday HH:mm"，其余 "MM-dd HH:mm"。
     */
    @JvmStatic
    fun printDate(sysTime: Long, dateTime: Long): String {
        val min = 60 * 1000L
        val diff = sysTime - dateTime
        if (diff <= 60 * min) {
            if (diff <= min) return "just now"
            return "${diff / min} minutes ago"
        }
        return when {
            isToday(sysTime, dateTime) -> "today " + getTime(dateTime, "HH:mm", Locale.US)
            isYesterday(sysTime, dateTime) -> "yesterday " + getTime(dateTime, "HH:mm", Locale.US)
            else -> getTime(dateTime, "MM-dd HH:mm", Locale.US)
        }
    }

    private fun firstDayOf(year: Int, month: Int): Calendar =
        Calendar.getInstance().apply {
            clear()
            set(year, month - 1, 1)
        }

    private fun startOfDay(time: Long): Long =
        Calendar.getInstance().apply {
            timeInMillis = time
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

    private fun nextDay(startOfDay: Long): Long =
        Calendar.getInstance().apply {
            timeInMillis = startOfDay
            add(Calendar.DAY_OF_MONTH, 1)
        }.timeInMillis
}
