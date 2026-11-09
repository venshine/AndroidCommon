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

import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PersistableBundle

/**
 * 剪贴板工具。
 *
 * 新版本系统对剪贴板有隐私限制：后台应用读取会被拒绝，前台读取时系统可能弹出提示，
 * 因此 `getXxx` 在读不到内容时返回 null，不要假设一定有值。
 *
 * @author venshine
 */
object ClipboardUtils {

    private fun manager(context: Context): ClipboardManager =
        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    /** 复制文本；[sensitive] 为 true 时标记为敏感内容（支持的系统会在预览中隐藏） */
    @JvmStatic
    @JvmOverloads
    fun copy(context: Context, text: CharSequence?, sensitive: Boolean = false) {
        val clip = ClipData.newPlainText(null, text)
        if (sensitive && Build.VERSION.SDK_INT >= 33) {
            clip.description.extras = PersistableBundle().apply {
                putBoolean(ClipDescription.EXTRA_IS_SENSITIVE, true)
            }
        }
        manager(context).setPrimaryClip(clip)
    }

    @JvmStatic
    fun copy(context: Context, uri: Uri) {
        manager(context).setPrimaryClip(ClipData.newUri(context.contentResolver, null, uri))
    }

    @JvmStatic
    fun copy(context: Context, intent: Intent) {
        manager(context).setPrimaryClip(ClipData.newIntent(null, intent))
    }

    /** 读取文本，剪贴板没有文本或无权读取时返回 null */
    @JvmStatic
    fun getText(context: Context): String? =
        firstItem(context, ClipDescription.MIMETYPE_TEXT_PLAIN)?.coerceToText(context)?.toString()

    @JvmStatic
    fun getUri(context: Context): Uri? = firstItem(context, ClipDescription.MIMETYPE_TEXT_URILIST)?.uri

    @JvmStatic
    fun getIntent(context: Context): Intent? = firstItem(context, ClipDescription.MIMETYPE_TEXT_INTENT)?.intent

    private fun firstItem(context: Context, mimeType: String): ClipData.Item? {
        val cm = manager(context)
        if (!cm.hasPrimaryClip()) return null
        if (cm.primaryClipDescription?.hasMimeType(mimeType) != true) return null
        val clip = cm.primaryClip ?: return null
        return if (clip.itemCount > 0) clip.getItemAt(0) else null
    }
}
