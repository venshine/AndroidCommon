package com.wx.android.common.log;

/**
 * Log level
 *
 * @author fengwx
 */
public final class Level {

    /**
     * Lowest level, turn on all logging
     */
    public static final int ALL = 1;

    /**
     * Priority constant for the println method; use Logger.v().
     */
    public static final int VERBOSE = 2;

    /**
     * Priority constant for the println method; use Logger.d().
     */
    public static final int DEBUG = 3;

    /**
     * Priority constant for the println method; use Logger.i().
     */
    public static final int INFO = 4;

    /**
     * Priority constant for the println method; use Logger.w().
     */
    public static final int WARN = 5;

    /**
     * Priority constant for the println method; use Logger.e().
     */
    public static final int ERROR = 6;

    /**
     * Priority constant for the println method.
     */
    public static final int ASSERT = 7;

    /**
     * Highest level, turn off loading
     */
    public static final int OFF = 8;

}
