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

import android.app.Activity
import android.app.SearchManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Process
import android.provider.ContactsContract
import android.provider.MediaStore
import android.provider.Settings

/**
 * 常用系统 Intent。
 *
 * 所有方法返回是否成功启动：没有能处理的应用、缺少权限都返回 false，不抛异常。
 * 在非 Activity 的 Context 里调用时自动加 `FLAG_ACTIVITY_NEW_TASK`。
 *
 * 拍照、选图请使用 Activity Result API（`ActivityResultContracts.TakePicture` / `PickVisualMedia`）。
 *
 * @author venshine
 */
object IntentUtils {

    private fun start(context: Context, intent: Intent): Boolean {
        if (context !is Activity) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return try {
            context.startActivity(intent)
            true
        } catch (e: ActivityNotFoundException) {
            false
        } catch (e: SecurityException) {
            false
        }
    }

    /** 网页搜索 */
    @JvmStatic
    fun search(context: Context, query: String): Boolean =
        start(context, Intent(Intent.ACTION_WEB_SEARCH).putExtra(SearchManager.QUERY, query))

    /** 用浏览器打开 URL */
    @JvmStatic
    fun openUrl(context: Context, url: String): Boolean =
        start(context, Intent(Intent.ACTION_VIEW, Uri.parse(url)))

    /** 打开地图，[uri] 形如 `geo:39.9,116.4?q=...` */
    @JvmStatic
    fun openMap(context: Context, uri: String): Boolean =
        start(context, Intent(Intent.ACTION_VIEW, Uri.parse(uri)))

    /** 打开拨号盘 */
    @JvmStatic
    fun openDial(context: Context): Boolean = start(context, Intent(Intent.ACTION_CALL_BUTTON))

    /** 打开拨号盘并填入号码（不直接拨出） */
    @JvmStatic
    fun openDial(context: Context, number: String): Boolean =
        start(context, Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number")))

    /** 直接拨打电话，需要 `CALL_PHONE` 权限，没有权限返回 false */
    @JvmStatic
    fun call(context: Context, number: String): Boolean {
        if (context.checkPermission(android.Manifest.permission.CALL_PHONE, Process.myPid(), Process.myUid()) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }
        return start(context, Intent(Intent.ACTION_CALL, Uri.parse("tel:$number")))
    }

    /** 打开短信编辑界面 */
    @JvmStatic
    fun sendMessage(context: Context, number: String, content: String): Boolean =
        start(context, Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$number")).putExtra("sms_body", content))

    /** 打开通讯录 */
    @JvmStatic
    fun openContacts(context: Context): Boolean =
        start(context, Intent(Intent.ACTION_VIEW, ContactsContract.Contacts.CONTENT_URI))

    /** 打开系统设置页，[action] 为设置页 action，默认总设置 */
    @JvmStatic
    @JvmOverloads
    fun openSettings(context: Context, action: String? = null): Boolean =
        start(context, Intent(if (action.isNullOrEmpty()) Settings.ACTION_SETTINGS else action))

    /** 打开系统相机（不返回结果） */
    @JvmStatic
    fun openCamera(context: Context): Boolean = start(context, Intent(MediaStore.ACTION_IMAGE_CAPTURE))

    /** 打开应用详情设置页 */
    @JvmStatic
    fun openAppDetail(packageName: String, context: Context): Boolean =
        start(
            context,
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", packageName, null))
        )
}
