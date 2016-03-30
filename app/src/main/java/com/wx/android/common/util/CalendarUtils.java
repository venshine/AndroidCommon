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
package com.wx.android.common.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期操作
 *
 * @author venshine
 */
public class CalendarUtils {

    public static final String DATE_FORMAT = "yyyyMMdd";

    public static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss:ms";

    public static final String[] MONTHS = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT",
            "NOV", "DEC"};

    public static final String[] MONTHS2 = {"January", "February", "March", "April", "May", "June", "July", "August",
            "September", "October", "November", "December"};

    public static final String[] WEEKS = {"SUN", "MON", "TUES", "WED", "THUR", "FRI", "SAT"};

    public static final String[] WEEKS2 = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday",
            "Saturday"};

    public static final int DAY = 24 * 60 * 60 * 1000;

    /**
     * Get date by year, month, day.
     *
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static String getDate(int year, int month, int day) {
        return "" + year + (month > 9 ? month : ("0" + month)) + (day > 9 ? day : ("0" + day));
    }

    /**
     * Get current date
     *
     * @return
     */
    public static String getCurrentDate() {
        Date now = new Date();
        SimpleDateFormat f = new SimpleDateFormat(DATE_FORMAT);
        return f.format(now);
    }


    /**
     * Get date by time
     *
     * @param time
     * @return
     */
    public static String getDate(long time) {
        SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);
        Date now = new Date(time);
        return df.format(now);
    }

    /**
     * Get date by year, month
     *
     * @param year
     * @param month
     * @return
     */
    public static String getDate(int year, int month) {
        String datetime = "" + year + (month > 9 ? month : ("0" + month));
        String parrern = "yyyyMM";
        SimpleDateFormat df = new SimpleDateFormat(parrern);
        try {
            Date date = df.parse(datetime);
            SimpleDateFormat df2 = new SimpleDateFormat(DATE_FORMAT);
            return df2.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Get current day
     *
     * @return
     */
    public static int getCurrentDay() {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }

    /**
     * Get current month
     *
     * @return
     */
    public static int getCurrentMonth() {
        return Calendar.getInstance().get(Calendar.MONTH) + 1;
    }

    /**
     * Get current year
     *
     * @return
     */
    public static int getCurrentYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    /**
     * Get current time, yyyy-MM-dd hh:mm:ss.
     *
     * @return
     */
    public static String getTime() {
        Date now = new Date();
        SimpleDateFormat df = new SimpleDateFormat(TIME_FORMAT);
        return df.format(now);
    }

    /**
     * Convert long time to String time
     *
     * @param time
     * @param format
     * @return
     */
    public static String getTime(long time, String format) {
        if (time > 0) {
            if (StringUtils.isEmpty(format)) {
                format = DATE_FORMAT;
            }
            SimpleDateFormat sf = new SimpleDateFormat(format);
            Date date = new Date(time);
            return sf.format(date);
        }
        return "";
    }

    /**
     * Convert String time to long time
     *
     * @param time
     * @param format
     * @return
     */
    public static long getTime(String time, String format) {
        try {
            if (null != time) {
                if (format == null) {
                    format = DATE_FORMAT;
                }
                SimpleDateFormat sf = new SimpleDateFormat(format);
                return sf.parse(time).getTime();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Get month days by year, month
     *
     * @param year
     * @param month
     * @return
     */
    public static int getMonthDays(int year, int month) {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, year);
        a.set(Calendar.MONTH, month - 1);
        a.set(Calendar.DATE, 1);
        a.roll(Calendar.DATE, -1);
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }

    /**
     * Get current month days
     */
    public static int getCurrentMonthDays() {
        return getMonthDays(getCurrentYear(), getCurrentMonth());
    }

    /**
     * Get last month days
     *
     * @return
     */
    public static int getLastMonthDays() {
        return getMonthDays(getCurrentYear(), getCurrentMonth() - 1);
    }

    /**
     * Get next month days
     *
     * @return
     */
    public static int getNextMonthDays() {
        return getMonthDays(getCurrentYear(), getCurrentMonth() + 1);
    }

    /**
     * Get week by datetime, datetime format must be contracted with DATE_FORMAT.
     *
     * @param datetime
     * @return
     */
    public static String getWeek(String datetime) {
        Calendar cal = Calendar.getInstance();
        DateFormat f = new SimpleDateFormat(DATE_FORMAT);
        try {
            cal.setTime(f.parse(datetime));
            int week_index = cal.get(Calendar.DAY_OF_WEEK) - 1;
            if (week_index < 0) {
                week_index = 0;
            }
            return WEEKS[week_index];
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Get last month first day
     */
    public static int getLastMonthFirstDay() {
        Calendar lastDate = Calendar.getInstance();
        lastDate.set(Calendar.DATE, 1);
        lastDate.add(Calendar.MONTH, -1);
        return lastDate.getActualMinimum(Calendar.DAY_OF_MONTH);
    }

    /**
     * Get last month end day
     */
    public static int getLastMonthEndDay() {
        Calendar lastDate = Calendar.getInstance();
        lastDate.add(Calendar.MONTH, -1);
        lastDate.set(Calendar.DATE, 1);
        lastDate.roll(Calendar.DATE, -1);
        return lastDate.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * Get next month first day
     */
    public static int getNextMonthFirstDay() {
        Calendar lastDate = Calendar.getInstance();
        lastDate.add(Calendar.MONTH, 1);
        lastDate.set(Calendar.DATE, 1);
        return lastDate.getActualMinimum(Calendar.DAY_OF_MONTH);
    }

    /**
     * Get next month end day
     *
     * @return
     */
    public static int getNextMonthEndDay() {
        Calendar lastDate = Calendar.getInstance();
        lastDate.add(Calendar.MONTH, 1);
        lastDate.set(Calendar.DATE, 1);
        lastDate.roll(Calendar.DATE, -1);
        return lastDate.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * Get current month first day
     *
     * @return
     */
    public static int getCurrentMonthFirstDay() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
    }

    /**
     * Get current month end day
     *
     * @return
     */
    public static int getCurrentMonthEndDay() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * Get current month day of week
     */
    public static int getCurrentMonthDayOfWeek() {
        return getMonthDayOfWeek(getCurrentYear(), getCurrentMonth());
    }

    /**
     * Get last month day of week
     *
     * @return
     */
    public static int getLastMonthDayOfWeek() {
        return getMonthDayOfWeek(getCurrentYear(), getCurrentMonth() - 1);
    }

    /**
     * Get next month day of week
     *
     * @return
     */
    public static int getNextMonthDayOfWeek() {
        return getMonthDayOfWeek(getCurrentYear(), getCurrentMonth() + 1);
    }

    /**
     * Get month day of week by year, month
     *
     * @param year
     * @param month
     * @return
     */
    public static int getMonthDayOfWeek(int year, int month) {
        Calendar cal = Calendar.getInstance();
        String dateString = year + "-" + (month > 9 ? month : ("0" + month))
                + "-01";
        String pattern = "yyyy-MM-dd";
        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            date = sdf.parse(dateString);
        } catch (ParseException e) {
        }
        if (date != null) {
            cal.setTime(date);
            int week_index = cal.get(Calendar.DAY_OF_WEEK) - 1;
            if (week_index < 0) {
                week_index = 0;
            }
            return week_index;
        }
        return -1;
    }

    /**
     * Judge whether dateTime is today by sysTime and dateTime
     *
     * @param sysTime  server time
     * @param dateTime publish time
     * @return
     */
    public static boolean isToday(long sysTime, long dateTime) {
        String s = getTime(sysTime, DATE_FORMAT); // 2015-06-15，2015-06-15 14:44:02
        long l = getTime(s, DATE_FORMAT); // 1434297600000， 2015-06-15 00:00:00
        return dateTime - l >= 0;
    }

    /**
     * Judge whether dateTime is yesterday by sysTime and dateTime
     *
     * @param sysTime  server time
     * @param dateTime publish time
     * @return
     */
    public static boolean isYesterday(long sysTime, long dateTime) {
        long yesterday = sysTime - DAY;
        String s = getTime(yesterday, DATE_FORMAT); // 2015-06-14，2015-06-14 14:44:02
        long l = getTime(s, DATE_FORMAT); // 1434297600000， 2015-06-14 00:00:00
        return dateTime - l >= 0;
    }

    /**
     * Print date time by sysTime and dateTime
     *
     * @param sysTime  server time
     * @param dateTime publish time
     * @return
     */
    public static String printDate(long sysTime, long dateTime) {
        long min = 60 * 1000;
        long hour = 60 * min;
        if (sysTime - dateTime <= hour) { // one hour
            if (sysTime - dateTime <= min) { // one minute
                return "just now";
            }
            return ((sysTime - dateTime) / min) + "minutes ago";
        }
        if (isToday(sysTime, dateTime)) {// today
            SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm");
            Date date1 = new Date(dateTime);
            return "today " + sdf1.format(date1);
        }
        if (isYesterday(sysTime, dateTime)) {// yesterday
            SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
            Date date2 = new Date(dateTime);
            return "yesterday " + sdf2.format(date2);
        }
        // other：MM-dd hh:mm
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
        Date date = new Date(dateTime);
        return sdf.format(date);
    }

}
