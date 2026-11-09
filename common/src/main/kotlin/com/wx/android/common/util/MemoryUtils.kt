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

import android.app.ActivityManager
import android.content.Context
import android.os.Environment
import android.os.StatFs
import java.io.File

/**
 * 内存与存储空间信息，单位均为字节。
 *
 * 外部存储部分统计的是应用专属目录（`getExternalFilesDir`）所在的卷，无需存储权限。
 *
 * @author venshine
 */
object MemoryUtils {

    private const val ERROR = -1L

    private fun memoryInfo(context: Context): ActivityManager.MemoryInfo {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        return ActivityManager.MemoryInfo().also { am.getMemoryInfo(it) }
    }

    /** 设备物理内存总量 */
    @JvmStatic
    fun getTotalMemory(context: Context): Long = memoryInfo(context).totalMem

    /** 当前可用内存 */
    @JvmStatic
    fun getAvailableMemory(context: Context): Long = memoryInfo(context).availMem

    /** 外部存储是否已挂载可用 */
    @JvmStatic
    fun isExternalMemoryAvailable(): Boolean =
        Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED

    /** 内部存储总量 */
    @JvmStatic
    fun getTotalInternalMemorySize(): Long = totalOf(Environment.getDataDirectory())

    /** 内部存储可用量 */
    @JvmStatic
    fun getAvailableInternalMemorySize(): Long = availableOf(Environment.getDataDirectory())

    /** 外部存储总量，外部存储不可用返回 -1 */
    @JvmStatic
    fun getTotalExternalMemorySize(context: Context): Long =
        externalDir(context)?.let { totalOf(it) } ?: ERROR

    /** 外部存储可用量，外部存储不可用返回 -1 */
    @JvmStatic
    fun getAvailableExternalMemorySize(context: Context): Long =
        externalDir(context)?.let { availableOf(it) } ?: ERROR

    private fun externalDir(context: Context): File? =
        if (isExternalMemoryAvailable()) context.getExternalFilesDir(null) else null

    private fun totalOf(dir: File): Long = StatFs(dir.path).let { it.blockCountLong * it.blockSizeLong }

    private fun availableOf(dir: File): Long = StatFs(dir.path).let { it.availableBlocksLong * it.blockSizeLong }
}
