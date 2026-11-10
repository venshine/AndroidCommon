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
package com.wx.android.common.demo

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.wx.android.common.util.CalendarUtils
import java.util.Locale
import kotlin.math.roundToInt

/**
 * 日期：用 [CalendarUtils] 的月天数、1 号星期几画出月历，点日期看星期和与今天的距离
 *
 * @author venshine
 */
class CalendarActivity : BaseActivity() {

    private var year = CalendarUtils.getCurrentYear()
    private var month = CalendarUtils.getCurrentMonth()
    private var selectedDay = CalendarUtils.getCurrentDay()

    /** 固定 6 行 × 7 列，翻月时只改文字和状态，不重建 View */
    private val cells = ArrayList<TextView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)
        setToolbar(R.string.demo_calendar, "CalendarUtils")

        buildWeekdays()
        buildGrid()
        findViewById<View>(R.id.calendar_prev).setOnClickListener { shiftMonth(-1) }
        findViewById<View>(R.id.calendar_next).setOnClickListener { shiftMonth(1) }
        findViewById<View>(R.id.calendar_today).setOnClickListener {
            year = CalendarUtils.getCurrentYear()
            month = CalendarUtils.getCurrentMonth()
            selectedDay = CalendarUtils.getCurrentDay()
            render()
        }
        render()
    }

    private fun buildWeekdays() {
        val row = findViewById<LinearLayout>(R.id.calendar_weekdays)
        WEEKDAYS.forEach { name ->
            row.addView(TextView(this).apply {
                text = name
                gravity = Gravity.CENTER
                textSize = 12f
                setTextColor(colorOf(R.color.text_dim))
            }, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f))
        }
    }

    private fun buildGrid() {
        val grid = findViewById<LinearLayout>(R.id.calendar_grid)
        repeat(ROWS) {
            val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
            repeat(COLUMNS) {
                val cell = TextView(this).apply {
                    gravity = Gravity.CENTER
                    textSize = 15f
                    setTextColor(ContextCompat.getColorStateList(this@CalendarActivity, R.color.day_text))
                    setBackgroundResource(R.drawable.bg_day_selected)
                }
                row.addView(cell, LinearLayout.LayoutParams(0, dp(46f), 1f).apply { setMargins(dp(2f), dp(2f), dp(2f), dp(2f)) })
                cells += cell
            }
            grid.addView(row, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT))
        }
    }

    private fun shiftMonth(delta: Int) {
        val index = year * 12 + (month - 1) + delta
        year = index / 12
        month = index % 12 + 1
        // 翻到天数更少的月份时，选中日要夹到月末
        selectedDay = selectedDay.coerceAtMost(CalendarUtils.getMonthDays(year, month))
        render()
    }

    private fun render() {
        findViewById<TextView>(R.id.calendar_title).text = getString(R.string.calendar_title_format, year, month)
        val days = CalendarUtils.getMonthDays(year, month)
        val offset = CalendarUtils.getMonthDayOfWeek(year, month)
        val isThisMonth = year == CalendarUtils.getCurrentYear() && month == CalendarUtils.getCurrentMonth()
        cells.forEachIndexed { index, cell ->
            val day = index - offset + 1
            if (day in 1..days) {
                cell.text = day.toString()
                cell.visibility = View.VISIBLE
                cell.isSelected = day == selectedDay
                cell.isActivated = isThisMonth && day == CalendarUtils.getCurrentDay()
                cell.setOnClickListener {
                    selectedDay = day
                    render()
                }
            } else {
                cell.text = ""
                cell.isSelected = false
                cell.isActivated = false
                cell.setOnClickListener(null)
                cell.isClickable = false
            }
        }
        renderDetail(days, offset)
    }

    private fun renderDetail(days: Int, offset: Int) {
        val date = CalendarUtils.getDate(year, month, selectedDay)
        val label = "${date.substring(0, 4)}-${date.substring(4, 6)}-${date.substring(6)} ${CalendarUtils.getWeek(date)}"
        findViewById<TextView>(R.id.calendar_detail).text =
            getString(R.string.calendar_detail_format, label, relative(date), days, WEEKDAYS[offset])
    }

    /** 选中日相对今天的说明：今天 / 昨天 / n 天前 / n 天后 */
    private fun relative(date: String): String {
        val today = CalendarUtils.getTime(CalendarUtils.getCurrentDate(), CalendarUtils.DATE_FORMAT, Locale.US)
        val selected = CalendarUtils.getTime(date, CalendarUtils.DATE_FORMAT, Locale.US)
        val diff = ((selected - today).toDouble() / CalendarUtils.DAY).roundToInt()
        return when {
            diff == 0 -> getString(R.string.calendar_relative_today)
            diff == -1 -> getString(R.string.calendar_relative_yesterday)
            diff > 0 -> getString(R.string.calendar_relative_future, diff)
            else -> getString(R.string.calendar_relative_past, -diff)
        }
    }

    private companion object {
        const val ROWS = 6
        const val COLUMNS = 7
        val WEEKDAYS = arrayOf("日", "一", "二", "三", "四", "五", "六")
    }
}
