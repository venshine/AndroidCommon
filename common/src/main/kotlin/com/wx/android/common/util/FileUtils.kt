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
package com.wx.android.common.util

import android.content.Context
import java.io.File
import java.util.Locale

/**
 * 文件工具：目录大小、体积格式化、移动文件，以及应用自身缓存 / 数据目录的清理。
 *
 * 普通的读写、复制、删除、取扩展名请直接使用 Kotlin 标准库的 `File` 扩展
 * （`readText`、`copyTo`、`deleteRecursively`、`extension` 等）。
 * 所有清理方法都只操作应用自己的目录，不涉及共享存储。
 *
 * @author venshine
 */
object FileUtils {

    /** 递归统计目录（或单个文件）的大小，单位字节；不存在返回 0 */
    @JvmStatic
    fun getFolderSize(file: File): Long {
        if (!file.exists()) return 0
        if (file.isFile) return file.length()
        return file.walkTopDown().filter { it.isFile }.sumOf { it.length() }
    }

    /** 字节数格式化为 Byte / KB / MB / GB / TB，KB 起保留两位小数（使用 [Locale.US] 的小数点） */
    @JvmStatic
    fun formatSize(size: Long): String {
        val units = arrayOf("KB", "MB", "GB", "TB")
        if (size < 1024) return "${size}Byte"
        var value = size.toDouble()
        var unit = -1
        while (value >= 1024 && unit < units.size - 1) {
            value /= 1024
            unit++
        }
        return String.format(Locale.US, "%.2f%s", value, units[unit])
    }

    /**
     * 移动文件：优先 `renameTo`，失败（如跨分区）时复制后删除源文件。
     * 目标已存在时覆盖。成功返回 true。
     */
    @JvmStatic
    fun moveFile(src: File, dest: File): Boolean {
        if (!src.exists()) return false
        dest.parentFile?.mkdirs()
        if (src.renameTo(dest)) return true
        return try {
            src.copyTo(dest, overwrite = true)
            src.delete()
        } catch (e: java.io.IOException) {
            false
        }
    }

    /** 清空内部缓存目录 `cacheDir` */
    @JvmStatic
    fun cleanInternalCache(context: Context) = cleanDirectory(context.cacheDir)

    /** 清空外部缓存目录 `externalCacheDir`（应用专属目录，无需存储权限） */
    @JvmStatic
    fun cleanExternalCache(context: Context) = cleanDirectory(context.externalCacheDir)

    /** 清空 `filesDir` */
    @JvmStatic
    fun cleanFiles(context: Context) = cleanDirectory(context.filesDir)

    /** 清空应用的 `databases` 目录 */
    @JvmStatic
    fun cleanDatabases(context: Context) = cleanDirectory(File(appRoot(context), "databases"))

    /** 删除指定名称的数据库 */
    @JvmStatic
    fun cleanDatabases(context: Context, dbName: String): Boolean = context.deleteDatabase(dbName)

    /** 清空应用的 `shared_prefs` 目录 */
    @JvmStatic
    fun cleanSharedPreferences(context: Context) = cleanDirectory(File(appRoot(context), "shared_prefs"))

    /** 清空指定目录下的内容（保留目录本身） */
    @JvmStatic
    fun cleanCustomCache(path: String) = cleanDirectory(File(path))

    /** 清理缓存、文件、数据库、SharedPreferences 以及额外指定的目录 */
    @JvmStatic
    fun cleanApplicationDatas(context: Context, vararg paths: String) {
        cleanInternalCache(context)
        cleanExternalCache(context)
        cleanDatabases(context)
        cleanSharedPreferences(context)
        cleanFiles(context)
        paths.forEach { cleanCustomCache(it) }
    }

    /** 应用私有数据根目录（`filesDir` 的上一级） */
    private fun appRoot(context: Context): File = context.filesDir.parentFile ?: context.filesDir

    private fun cleanDirectory(directory: File?) {
        if (directory == null || !directory.isDirectory) return
        directory.listFiles()?.forEach { it.deleteRecursively() }
    }
}
