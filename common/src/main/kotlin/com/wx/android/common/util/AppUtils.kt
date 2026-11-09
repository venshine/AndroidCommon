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
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Process

/**
 * 应用信息工具：版本、名称、图标、签名、权限、安装状态等。
 *
 * 查询"其他应用"的方法受包可见性限制（API 30+）：目标包不在可见范围内时按"未安装"处理，
 * 需要在清单里声明 `<queries>` 或 `QUERY_ALL_PACKAGES`。查询失败统一返回 null / -1 / false。
 *
 * @author venshine
 */
object AppUtils {

    @Suppress("DEPRECATION")
    private fun packageInfo(context: Context, packageName: String, flags: Int): PackageInfo? = try {
        if (Build.VERSION.SDK_INT >= 33) {
            context.packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(flags.toLong()))
        } else {
            context.packageManager.getPackageInfo(packageName, flags)
        }
    } catch (e: PackageManager.NameNotFoundException) {
        null
    }

    @Suppress("DEPRECATION")
    private fun applicationInfo(context: Context, packageName: String): ApplicationInfo? = try {
        if (Build.VERSION.SDK_INT >= 33) {
            context.packageManager.getApplicationInfo(packageName, PackageManager.ApplicationInfoFlags.of(0))
        } else {
            context.packageManager.getApplicationInfo(packageName, 0)
        }
    } catch (e: PackageManager.NameNotFoundException) {
        null
    }

    @JvmStatic
    fun getPackageName(context: Context): String = context.packageName

    /** 当前应用的版本名，取不到返回空串 */
    @JvmStatic
    fun getVersionName(context: Context): String = getAppVersionName(context, context.packageName) ?: ""

    /** 当前应用的版本号（Long，对应 `longVersionCode`），取不到返回 0 */
    @JvmStatic
    fun getVersionCode(context: Context): Long =
        getAppVersionCode(context, context.packageName).takeIf { it >= 0 } ?: 0

    /** 当前应用图标 */
    @JvmStatic
    fun getIcon(context: Context): Drawable? = getAppIcon(context, context.packageName)

    /** 当前应用名称 */
    @JvmStatic
    fun getAppName(context: Context): String? = getAppName(context, context.packageName)

    @JvmStatic
    fun getAppIcon(context: Context, packageName: String): Drawable? =
        applicationInfo(context, packageName)?.loadIcon(context.packageManager)

    @JvmStatic
    fun getAppName(context: Context, packageName: String): String? =
        applicationInfo(context, packageName)?.loadLabel(context.packageManager)?.toString()

    @JvmStatic
    fun getAppVersionName(context: Context, packageName: String): String? =
        packageInfo(context, packageName, 0)?.versionName

    /** 应用版本号（Long），未安装返回 -1 */
    @JvmStatic
    @Suppress("DEPRECATION")
    fun getAppVersionCode(context: Context, packageName: String): Long {
        val info = packageInfo(context, packageName, 0) ?: return -1
        return if (Build.VERSION.SDK_INT >= 28) info.longVersionCode else info.versionCode.toLong()
    }

    /** 应用声明的权限列表，未安装或没有声明返回 null */
    @JvmStatic
    fun getAppPermission(context: Context, packageName: String): Array<String>? =
        packageInfo(context, packageName, PackageManager.GET_PERMISSIONS)?.requestedPermissions

    /** 应用的签名证书（DER 编码）列表；取不到返回空列表 */
    @JvmStatic
    @Suppress("DEPRECATION")
    fun getAppSignatures(context: Context, packageName: String): List<ByteArray> {
        if (Build.VERSION.SDK_INT >= 28) {
            val info = packageInfo(context, packageName, PackageManager.GET_SIGNING_CERTIFICATES) ?: return emptyList()
            val signing = info.signingInfo ?: return emptyList()
            val signatures = if (signing.hasMultipleSigners()) signing.apkContentsSigners else signing.signingCertificateHistory
            return signatures?.map { it.toByteArray() } ?: emptyList()
        }
        val info = packageInfo(context, packageName, PackageManager.GET_SIGNATURES) ?: return emptyList()
        return info.signatures?.map { it.toByteArray() } ?: emptyList()
    }

    /** 第一个签名证书的 SHA-256（小写十六进制），取不到返回 null */
    @JvmStatic
    fun getAppSignatureSha256(context: Context, packageName: String): String? =
        getAppSignatures(context, packageName).firstOrNull()?.let { DigestUtils.sha256(it) }

    /** 当前应用是否为 debuggable 构建 */
    @JvmStatic
    fun isApkDebuggable(context: Context): Boolean =
        context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0

    /** 指定应用是否为 debuggable 构建，未安装返回 false */
    @JvmStatic
    fun isApkDebuggable(context: Context, packageName: String): Boolean =
        (applicationInfo(context, packageName)?.flags ?: 0) and ApplicationInfo.FLAG_DEBUGGABLE != 0

    /** 指定应用是否已安装（受包可见性限制） */
    @JvmStatic
    fun isInstalled(context: Context, packageName: String): Boolean = applicationInfo(context, packageName) != null

    /** 指定应用是否为系统应用 */
    @JvmStatic
    fun isSystemApp(context: Context, packageName: String): Boolean =
        (applicationInfo(context, packageName)?.flags ?: 0) and ApplicationInfo.FLAG_SYSTEM != 0

    /** 当前进程是否已被授予 [permission] */
    @JvmStatic
    fun checkPermission(context: Context, permission: String): Boolean =
        context.checkPermission(permission, Process.myPid(), Process.myUid()) == PackageManager.PERMISSION_GRANTED

    /** 启动指定应用的启动页，应用不存在或没有启动页返回 false */
    @JvmStatic
    fun startApp(context: Context, packageName: String): Boolean {
        val intent = context.packageManager.getLaunchIntentForPackage(packageName) ?: return false
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        return true
    }

    /** 弹出系统卸载确认；API 28+ 需要在清单声明 `REQUEST_DELETE_PACKAGES` */
    @JvmStatic
    fun uninstall(context: Context, packageName: String): Boolean {
        if (packageName.isEmpty()) return false
        val intent = Intent(Intent.ACTION_DELETE, Uri.parse("package:$packageName"))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        return true
    }

    /** 已安装的应用列表（受包可见性限制） */
    @JvmStatic
    @Suppress("DEPRECATION")
    @SuppressLint("QueryPermissionsNeeded") // 受包可见性限制，已在类注释和方法注释说明
    fun getInstalledPackages(context: Context): List<PackageInfo> =
        if (Build.VERSION.SDK_INT >= 33) {
            context.packageManager.getInstalledPackages(PackageManager.PackageInfoFlags.of(0))
        } else {
            context.packageManager.getInstalledPackages(0)
        }
}
