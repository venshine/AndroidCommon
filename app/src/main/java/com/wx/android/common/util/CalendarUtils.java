package com.wx.android.common.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Calendar
 *
 * @author fengwx
 */
public class CalendarUtils {

    public static final String PATTERN = "yyyyMMdd";

    public static final String PATTERN2 = "yyyy-MM-dd HH:mm:ss:ms";

    public static final String[] MONTHS = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};

    public static final String[] MONTHS2 = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

    public static final String[] WEEKS = {"SUN", "MON", "TUES", "WED", "THUR", "FRI", "SAT"};

    public static final String[] WEEKS2 = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

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
        SimpleDateFormat f = new SimpleDateFormat(PATTERN);
        return f.format(now);
    }


    /**
     * Get date by time
     *
     * @param time
     * @return
     */
    public static String getDate(long time) {
        SimpleDateFormat df = new SimpleDateFormat(PATTERN);
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
        SimpleDateFormat df = new SimpleDateFormat(PATTERN);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        return df.format(cal.getTime());
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
        SimpleDateFormat df = new SimpleDateFormat(PATTERN2);
        return df.format(now);
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
     * Get week by datetime, datetime format must be contracted with PATTERN.
     *
     * @param datetime
     * @return
     */
    public static String getWeek(String datetime) {
        Calendar cal = Calendar.getInstance();
        DateFormat f = new SimpleDateFormat(PATTERN);
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

}
