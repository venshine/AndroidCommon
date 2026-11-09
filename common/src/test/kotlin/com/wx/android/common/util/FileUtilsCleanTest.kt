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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import java.io.File

/** 应用自身缓存 / 数据目录的清理 */
@RunWith(RobolectricTestRunner::class)
class FileUtilsCleanTest {

    private val context: Context = RuntimeEnvironment.getApplication()

    /** 在 [dir] 下造一个文件和一个带文件的子目录 */
    private fun fill(dir: File?) {
        requireNotNull(dir).mkdirs()
        File(dir, "a.txt").writeText("a")
        File(dir, "sub").mkdirs()
        File(dir, "sub/b.txt").writeText("b")
    }

    private fun isEmptyDir(dir: File?) = dir != null && dir.isDirectory && dir.listFiles()!!.isEmpty()

    private val appRoot get() = requireNotNull(context.filesDir.parentFile)

    @Test
    fun cleanInternalCacheKeepsDirectory() {
        fill(context.cacheDir)
        FileUtils.cleanInternalCache(context)
        assertTrue(isEmptyDir(context.cacheDir))
    }

    @Test
    fun cleanExternalCache() {
        fill(context.externalCacheDir)
        FileUtils.cleanExternalCache(context)
        assertTrue(isEmptyDir(context.externalCacheDir))
    }

    @Test
    fun cleanFilesOnlyTouchesFilesDir() {
        fill(context.filesDir)
        fill(context.cacheDir)
        FileUtils.cleanFiles(context)
        assertTrue(isEmptyDir(context.filesDir))
        assertEquals(2, context.cacheDir.listFiles()!!.size) // 缓存目录不受影响
    }

    @Test
    fun cleanDatabasesAndSharedPrefsUseRealAppDirectories() {
        // 旧实现把路径拼成 filesDir + packageName + "/databases"，永远指向不存在的目录
        val db = File(appRoot, "databases")
        val prefs = File(appRoot, "shared_prefs")
        fill(db)
        fill(prefs)
        FileUtils.cleanDatabases(context)
        assertTrue(isEmptyDir(db))
        assertFalse(isEmptyDir(prefs))
        FileUtils.cleanSharedPreferences(context)
        assertTrue(isEmptyDir(prefs))
    }

    @Test
    fun cleanCustomCache() {
        val custom = File(context.cacheDir, "custom")
        fill(custom)
        FileUtils.cleanCustomCache(custom.path)
        assertTrue(isEmptyDir(custom))
    }

    @Test
    fun cleanMissingDirectoriesDoesNotThrow() {
        FileUtils.cleanCustomCache(File(context.cacheDir, "nope").path)
        FileUtils.cleanDatabases(context)
        FileUtils.cleanSharedPreferences(context)
    }

    @Test
    fun cleanApplicationDatasCleansEverythingIncludingExtraPaths() {
        val extra = File(context.cacheDir, "extra")
        fill(context.filesDir)
        fill(File(appRoot, "databases"))
        fill(File(appRoot, "shared_prefs"))
        fill(context.externalCacheDir)
        fill(extra)
        FileUtils.cleanApplicationDatas(context, extra.path)
        assertTrue(isEmptyDir(context.filesDir))
        assertTrue(isEmptyDir(File(appRoot, "databases")))
        assertTrue(isEmptyDir(File(appRoot, "shared_prefs")))
        assertTrue(isEmptyDir(context.externalCacheDir))
        assertTrue(context.cacheDir.listFiles()!!.isEmpty())
    }
}
