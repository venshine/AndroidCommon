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

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

/** 纯 JVM 测试：目录大小、体积格式化、移动文件（清理方法见 FileUtilsCleanTest） */
class FileUtilsTest {

    @get:Rule
    val tmp = TemporaryFolder()

    @Test
    fun folderSizeIsRecursive() {
        val dir = tmp.newFolder("a")
        File(dir, "1.bin").writeBytes(ByteArray(100))
        val sub = File(dir, "sub").apply { mkdirs() }
        File(sub, "2.bin").writeBytes(ByteArray(250))
        assertEquals(350, FileUtils.getFolderSize(dir))
    }

    @Test
    fun folderSizeOfFileAndMissing() {
        val f = tmp.newFile("x.bin").also { it.writeBytes(ByteArray(42)) }
        assertEquals(42, FileUtils.getFolderSize(f))
        assertEquals(0, FileUtils.getFolderSize(File(tmp.root, "missing")))
        assertEquals(0, FileUtils.getFolderSize(tmp.newFolder("empty")))
    }

    @Test
    fun formatSize() {
        assertEquals("0Byte", FileUtils.formatSize(0))
        assertEquals("1023Byte", FileUtils.formatSize(1023))
        assertEquals("1.00KB", FileUtils.formatSize(1024))
        assertEquals("1.50KB", FileUtils.formatSize(1536))
        assertEquals("1.00MB", FileUtils.formatSize(1024L * 1024))
        assertEquals("2.00GB", FileUtils.formatSize(2L * 1024 * 1024 * 1024))
        assertEquals("1.00TB", FileUtils.formatSize(1L shl 40))
        assertEquals("1024.00TB", FileUtils.formatSize(1L shl 50)) // 超过 TB 仍以 TB 显示
    }

    @Test
    fun moveFileRenames() {
        val src = tmp.newFile("src.txt").also { it.writeText("hello") }
        val dest = File(tmp.root, "deep/er/dest.txt") // 目标父目录不存在时自动创建
        assertTrue(FileUtils.moveFile(src, dest))
        assertFalse(src.exists())
        assertEquals("hello", dest.readText())
    }

    @Test
    fun moveFileOverwritesExistingTarget() {
        val src = tmp.newFile("src.txt").also { it.writeText("new") }
        val dest = tmp.newFile("dest.txt").also { it.writeText("old") }
        assertTrue(FileUtils.moveFile(src, dest))
        assertEquals("new", dest.readText())
    }

    @Test
    fun moveMissingFileFails() {
        assertFalse(FileUtils.moveFile(File(tmp.root, "none"), File(tmp.root, "dest")))
    }
}
