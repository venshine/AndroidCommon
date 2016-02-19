package com.wx.android.common.util;

import android.text.format.Time;

/**
 * Time Information
 * <p/>
 * @deprecated Use {@link CalendarUtils} instead.
 * @author fengwx
 */
@Deprecated
public class TimeUtils {

    public static final String WEEK_NAME_ABBR = "%a";   // Abbreviated weekday name. Thu
    public static final String WEEK_NAME_FULL = "%A";   // Full weekday name. Thursday
    public static final String MONTH_NAME_ABBR = "%b";  // Abbreviated month name. Aug
    public static final String MONTH_NAME_FULL = "%B";  // Full month name. August
    public static final String DATE_AND_TIME_STANDARD = "%c";   // Date and time representation. Thu Aug 23 14:55:02 2001
    public static final String YEAR_FIRST_TWO_DIGITS = "%C";   // Year divided by 100 and truncated to integer (00-99). 20
    public static final String DAY_OF_MONTH = "%d";   // Day of the month, zero-padded (01-31). 23
    public static final String DAY_OF_MONTH2 = "%e";   // Day of the month, space-padded ( 1-31). 23
    public static final String YEAR_MONTH_DAY = "%F";   // Short YYYY-MM-DD date, equivalent to %Y-%m-%d. 2001-08-23
    public static final String YEAR_BASED_LAST_TWO_DIGITS = "%g";   // Week-based year, last two digits (00-99). 01
    public static final String WEEK_BASED_YEAR = "%G";   // Week-based year. 2001
    public static final String MONTH_NAME_ABBR2 = "%h";   // Abbreviated month name * (same as %b). Aug
    public static final String HOUR_24 = "%H";   // Hour in 24h format (00-23). 14
    public static final String HOUR_12 = "%I";   // Hour in 12h format (01-12).	02
    public static final String DAY_OF_YEAR = "%j";   // Day of the year (001-366). 235
    public static final String MONTH = "%m";   // Month as a decimal number (01-12). 08
    public static final String MINUTE = "%M";   // Minute (00-59). 55
    public static final String NEW_LINE = "%n";   // New-line character ('\n')
    public static final String AM_OR_PM = "%p";   // AM or PM designation. PM
    public static final String TIME_12 = "%r";   // 12-hour clock time. 02:55:02 pm
    public static final String HOUR_MINUTE = "%R";   // 24-hour HH:MM time, equivalent to %H:%M. 14:55
    public static final String SECOND = "%S";   // Second (00-61). 02
    public static final String TAB = "%t";   // Horizontal-tab character ('\t')
    public static final String HOUR_MINUTE_SECOND = "%T";   // ISO 8601 time format (HH:MM:SS), equivalent to %H:%M:%S. 14:55:02
    public static final String DAY_OF_WEEK = "%u";   // ISO 8601 weekday as number with Monday as 1 (1-7). 4
    public static final String WEEK_OF_YEAR_SUNDAY = "%U";   // Week number with the first Sunday as the first day of week one (00-53). 33
    public static final String WEEK_BASED_OF_YEAR = "%V";   // ISO 8601 week number (00-53). 34
    public static final String WEEK = "%w";   // Weekday as a decimal number with Sunday as 0 (0-6). 4
    public static final String WEEK_OF_WEEK_MONDAY = "%W";   // Week number with the first Monday as the first day of week one (00-53). 34
    public static final String DATE = "%x";   // Date representation. 08/23/01
    public static final String TIME = "%X";   // Time representation. 14:55:02
    public static final String YEAR_LAST_TWO_DIGITS = "%y";   // Year, last two digits (00-99). 01
    public static final String YEAR = "%Y";   // Year. 2001
    public static final String TIME_ZONE = "%z";   // ISO 8601 offset from UTC in timezone (1 minute=1, 1 hour=100)
    public static final String TIME_ZONE2 = "%Z";   // Timezone name or abbreviation
    public static final String PERCENT = "%%";   // A % sign. %

    /**
     * Print the current value given the format string provided, and you can set the time zone.
     *
     * @param format
     * @param timezone
     * @return
     */
    public static String getCurrentTime(String format, String timezone) {
        Time t;
        if (null != timezone) {
            t = new Time(timezone);
        } else {
            t = new Time();
        }
        t.setToNow();
        return t.format(format);
    }

    /**
     * Print the current value given the format string provided.
     *
     * @param format
     * @return
     */
    public static String getCurrentTime(String format) {
        return getCurrentTime(format, null);
    }

    /**
     * Returns the current system time in milliseconds since January 1, 1970 00:00:00 UTC.
     *
     * @return
     */
    public static long getCurrentTimeMillis() {
        return System.currentTimeMillis();
    }

}