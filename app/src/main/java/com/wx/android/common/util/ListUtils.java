package com.wx.android.common.util;

import java.util.List;

/**
 * String Operations
 *
 * @author fengwx
 */
public class ListUtils {

    /**
     * Judge whether a list is null.
     *
     * @param list
     * @return
     */
    public static boolean equalsNull(List<?> list) {
        if (list != null && list.size() > 0) {
            return false;
        }
        return true;
    }

}
