package com.wx.android.common.util;

/**
 * Object
 *
 * @author fengwx
 */
public class ObjectUtils {

    /**
     * Returns true if a and b are equal.
     *
     * @param a
     * @param b
     * @return
     */
    public static boolean equals(Object a, Object b) {
        return a == b || (a == null ? b == null : a.equals(b));
    }

}
