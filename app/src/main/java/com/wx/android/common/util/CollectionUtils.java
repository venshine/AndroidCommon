package com.wx.android.common.util;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Collection Operations
 *
 * @author fengwx
 */
public class CollectionUtils {

    private static final String DELIMITER = ",";

    /**
     * Judge whether a collection is null or size is 0
     *
     * @param c
     * @param <V>
     * @return
     */
    public static <V> boolean isEmpty(Collection<V> c) {
        return (c == null || c.size() == 0);
    }

    /**
     * Join collection to string, separator is {@link #DELIMITER}
     *
     * @param tokens
     * @return
     */
    public static String join(Iterable tokens) {
        return tokens == null ? "" : TextUtils.join(DELIMITER, tokens);
    }

    /**
     * Convert array to list
     *
     * @param array
     * @param <T>
     * @return
     */
    public static <T> List<T> arrayToList(T... array) {
        return Arrays.asList(array);
    }

    /**
     * Convert array to set
     *
     * @param array
     * @param <T>
     * @return
     */
    public static <T> Set<T> arrayToSet(T... array) {
        return new HashSet<T>(arrayToList(array));
    }

    /**
     * Convert list to array
     *
     * @param list
     * @return
     */
    public static Object[] listToArray(List<?> list) {
        if (!isEmpty(list)) {
            return list.toArray();
        }
        return null;
    }

    /**
     * Convert set to array
     *
     * @param set
     * @return
     */
    public static Object[] setToArray(Set<?> set) {
        if (!isEmpty(set)) {
            return set.toArray();
        }
        return null;
    }

    /**
     * Convert list to set
     *
     * @param list
     * @param <T>
     * @return
     */
    public static <T> Set<T> listToSet(List<T> list) {
        return new HashSet<T>(list);
    }

    /**
     * Convert set to list
     *
     * @param set
     * @param <T>
     * @return
     */
    public static <T> List<T> setToList(Set<T> set) {
        return new ArrayList<T>(set);
    }

}
