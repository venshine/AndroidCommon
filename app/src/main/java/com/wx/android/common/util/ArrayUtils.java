package com.wx.android.common.util;

/**
 * Array
 *
 * @author fengwx
 */
public class ArrayUtils {

    private static final String DELIMITER = ",";

    /**
     * Judge whether a array is null.
     *
     * @param array
     * @return
     */
    public static <T> boolean isEmpty(T[] array) {
        return (array == null || array.length == 0);
    }

    /**
     * Traverse array
     *
     * @param array
     * @param <T>
     * @return
     */
    public static <T> String traverseArray(T[] array) {
        if (!isEmpty(array)) {
            int len = array.length;
            StringBuilder builder = new StringBuilder(len);
            int i = 0;
            for (T t : array) {
                if (t == null) {
                    continue;
                }
                builder.append(t.toString());
                i++;
                if (i < len) {
                    builder.append(DELIMITER);
                }
            }
            return builder.toString();
        }
        return null;
    }

}
