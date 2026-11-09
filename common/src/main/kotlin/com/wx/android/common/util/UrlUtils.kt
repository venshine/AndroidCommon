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

import java.net.URI
import java.net.URISyntaxException
import java.net.URLDecoder
import java.net.URLEncoder

/**
 * URL 工具：编码、解码和解析。
 *
 * @author venshine
 */
object UrlUtils {

    /** URL 解析结果，缺失的部分为 null（端口缺失为 -1） */
    data class ParsedUrl(
        val protocol: String?,
        val host: String?,
        val port: Int,
        val path: String?,
        /** 查询参数；无值的参数（如 `a`）对应 null，没有查询串时为空 Map */
        val params: Map<String, String?>
    )

    /** UTF-8 编码 */
    @JvmStatic
    fun encode(value: String): String = URLEncoder.encode(value, Charsets.UTF_8.name())

    /** UTF-8 解码，格式非法时返回原字符串 */
    @JvmStatic
    fun decode(value: String): String = try {
        URLDecoder.decode(value, Charsets.UTF_8.name())
    } catch (e: IllegalArgumentException) {
        value
    }

    /**
     * 解析 URL。空字符串或无法解析时抛 [IllegalArgumentException]。
     */
    @JvmStatic
    fun parse(url: String): ParsedUrl {
        require(url.isNotEmpty()) { "url is empty" }
        val uri = try {
            URI(url)
        } catch (e: URISyntaxException) {
            throw IllegalArgumentException("illegal url: $url", e)
        }
        val params = LinkedHashMap<String, String?>()
        uri.rawQuery?.split('&')?.forEach { pair ->
            if (pair.isEmpty()) return@forEach
            val idx = pair.indexOf('=')
            if (idx < 0) {
                params[decode(pair)] = null
            } else {
                val value = pair.substring(idx + 1)
                params[decode(pair.substring(0, idx))] = if (value.isEmpty()) null else decode(value)
            }
        }
        return ParsedUrl(uri.scheme, uri.host, uri.port, uri.rawPath, params)
    }
}
