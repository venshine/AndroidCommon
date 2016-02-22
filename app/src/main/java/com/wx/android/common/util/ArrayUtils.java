package com.wx.android.common.util;

/**
 * Array
 *
 * @author fengwx
 */
public class ArrayUtils {

    /**
     * Judge whether a array is null.
     *
     * @param array
     * @return
     */
    public static <T> boolean isEmpty(T[] array) {
        return (array != null && array.length > 0);
    }

}
