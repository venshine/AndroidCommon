package com.wx.android.common.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * clean the data
 *
 * @author fengwx
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
