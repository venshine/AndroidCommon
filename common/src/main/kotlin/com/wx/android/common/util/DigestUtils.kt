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

import java.security.MessageDigest

/**
 * 摘要工具：MD5、SHA-1、SHA-256，结果为小写十六进制字符串。
 *
 * 注意：MD5 和 SHA-1 已被证明存在碰撞攻击，只适合缓存 key、文件校验和等非安全场景，
 * 不要用于密码存储、签名或防篡改；需要安全摘要请用 [sha256]，存密码请用 PBKDF2 / bcrypt / Argon2。
 *
 * @author venshine
 */
object DigestUtils {

    /** MD5（非安全场景使用） */
    @JvmStatic
    fun md5(data: ByteArray): String = digest("MD5", data)

    /** MD5（非安全场景使用），字符串按 UTF-8 编码 */
    @JvmStatic
    fun md5(text: String): String = md5(text.toByteArray(Charsets.UTF_8))

    /** SHA-1（非安全场景使用） */
    @JvmStatic
    fun sha1(data: ByteArray): String = digest("SHA-1", data)

    /** SHA-1（非安全场景使用），字符串按 UTF-8 编码 */
    @JvmStatic
    fun sha1(text: String): String = sha1(text.toByteArray(Charsets.UTF_8))

    /** SHA-256 */
    @JvmStatic
    fun sha256(data: ByteArray): String = digest("SHA-256", data)

    /** SHA-256，字符串按 UTF-8 编码 */
    @JvmStatic
    fun sha256(text: String): String = sha256(text.toByteArray(Charsets.UTF_8))

    /** 字节数组转小写十六进制 */
    @JvmStatic
    fun toHex(bytes: ByteArray): String {
        val chars = CharArray(bytes.size * 2)
        for (i in bytes.indices) {
            val v = bytes[i].toInt() and 0xFF
            chars[i * 2] = HEX[v ushr 4]
            chars[i * 2 + 1] = HEX[v and 0x0F]
        }
        return String(chars)
    }

    private fun digest(algorithm: String, data: ByteArray): String =
        toHex(MessageDigest.getInstance(algorithm).digest(data))

    private val HEX = "0123456789abcdef".toCharArray()
}
