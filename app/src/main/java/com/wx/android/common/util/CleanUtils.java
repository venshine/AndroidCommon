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

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * 清理数据
 *
 * @author venshine
 */
public class CleanUtils {

    /**
     * clean internal cache(/data/data/com.xxx.xxx/cache)
     *
     * @param context
     */
    public static void cleanInternalCache(Context context) {
        deleteFilesInDirectory(context.getCacheDir());
    }

    /**
     * clean databases(/data/data/com.xxx.xxx/databases)
     *
     * @param context
     */
    public static void cleanDatabases(Context context) {
        deleteFilesInDirectory(new File(context.getFilesDir().getPath()
                + context.getPackageName() + "/databases"));
    }

    /**
     * clean databases by name
     *
     * @param context
     * @param dbName
     */
    public static void cleanDatabases(Context context, String dbName) {
        context.deleteDatabase(dbName);
    }

    /**
     * clean SharedPreference(/data/data/com.xxx.xxx/shared_prefs)
     *
     * @param context
     */
    public static void cleanSharedPreferences(Context context) {
        deleteFilesInDirectory(new File(context.getFilesDir().getPath()
                + context.getPackageName() + "/shared_prefs"));
    }

    /**
     * clean file(/data/data/com.xxx.xxx/files)
     *
     * @param context
     */
    public static void cleanFiles(Context context) {
        deleteFilesInDirectory(context.getFilesDir());
    }

    /**
     * clean external cache(/mnt/sdcard/android/data/com.xxx.xxx/cache)
     *
     * @param context
     */
    public static void cleanExternalCache(Context context) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            deleteFilesInDirectory(context.getExternalCacheDir());
        }
    }

    /**
     * clean custom cache
     *
     * @param filePath
     */
    public static void cleanCustomCache(String filePath) {
        deleteFilesInDirectory(new File(filePath));
    }

    /**
     * clean application datas
     *
     * @param context
     * @param filePath
     */
    public static void cleanApplicationDatas(Context context, String... filePath) {
        cleanInternalCache(context);
        cleanExternalCache(context);
        cleanDatabases(context);
        cleanSharedPreferences(context);
        cleanFiles(context);
        for (String fp : filePath) {
            cleanCustomCache(fp);
        }
    }

    /**
     * delete files in directory
     *
     * @param directory
     */
    private static void deleteFilesInDirectory(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                if (item.isDirectory()) {
                    deleteFilesInDirectory(item);
                } else {
                    item.delete();
                }
            }
        }
    }

}
