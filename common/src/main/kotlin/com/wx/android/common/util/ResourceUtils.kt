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

import android.annotation.SuppressLint
import android.content.Context
import java.io.IOException
import java.nio.charset.Charset

/**
 * 资源工具：按名称查资源 id，读取 assets / raw 中的文本。
 *
 * 取字符串、颜色、尺寸、Drawable 请直接使用 `Context` / `ContextCompat` 的对应方法。
 *
 * @author venshine
 */
object ResourceUtils {

    /** 按名称查资源 id（如 `getResourceByName(ctx, "ic_launcher", "mipmap")`），不存在返回 0 */
    @JvmStatic
    @SuppressLint("DiscouragedApi") // 按名称查资源本身就是这个方法的用途
    fun getResourceByName(context: Context, name: String, defType: String): Int =
        context.resources.getIdentifier(name, defType, context.packageName)

    /** 读取 assets 下的文本文件，读取失败返回 null */
    @JvmStatic
    @JvmOverloads
    fun getTextFromAssets(context: Context, fileName: String, charset: Charset = Charsets.UTF_8): String? = try {
        context.assets.open(fileName).use { it.readBytes().toString(charset) }
    } catch (e: IOException) {
        null
    }

    /** 读取 res/raw 下的文本文件，读取失败返回 null */
    @JvmStatic
    @JvmOverloads
    fun getRawText(context: Context, id: Int, charset: Charset = Charsets.UTF_8): String? = try {
        context.resources.openRawResource(id).use { it.readBytes().toString(charset) }
    } catch (e: IOException) {
        null
    } catch (e: android.content.res.Resources.NotFoundException) {
        null
    }
}
