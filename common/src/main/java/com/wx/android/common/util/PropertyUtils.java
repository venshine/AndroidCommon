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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Property
 *
 * @author venshine
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
