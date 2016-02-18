package com.wx.android.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Property
 *
 * @author fengwx
 */
public class PropertyUtils {

    /**
     * Get property value
     *
     * @param is
     * @param key
     * @return
     */
    public static String getPropertyValue(InputStream is, String key) {
        return getPropertyValue(is, key, null);
    }

    /**
     * Get property value
     *
     * @param is
     * @param key
     * @param defaultValue
     * @return
     */
    public static String getPropertyValue(InputStream is, String key, String defaultValue) {
        try {
            Properties pro = new Properties();
            pro.load(is);
            return pro.getProperty(key, defaultValue);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
