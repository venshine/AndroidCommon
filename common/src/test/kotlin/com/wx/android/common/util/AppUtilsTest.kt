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

import android.app.Application
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.ResolveInfo
import android.content.pm.Signature
import android.content.pm.SigningInfo
import android.net.Uri
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadow.api.Shadow
import org.robolectric.shadows.ShadowSigningInfo

@RunWith(RobolectricTestRunner::class)
class AppUtilsTest {

    private val app = RuntimeEnvironment.getApplication() as Application
    private val pm get() = app.packageManager
    private val pkg = "com.example.demo"

    private fun install(
        name: String = pkg, versionName: String? = "1.2.3", versionCode: Long = 123L,
        flags: Int = 0, permissions: Array<String>? = null, signatures: Array<Signature>? = null
    ) {
        val info = PackageInfo().apply {
            packageName = name
            this.versionName = versionName
            longVersionCode = versionCode
            requestedPermissions = permissions
            applicationInfo = ApplicationInfo().apply {
                packageName = name
                this.flags = flags
                nonLocalizedLabel = "Demo App"
            }
            if (signatures != null) {
                signingInfo = Shadow.newInstanceOf(SigningInfo::class.java).also {
                    Shadow.extract<ShadowSigningInfo>(it).setSignatures(signatures)
                }
            }
        }
        shadowOf(pm).installPackage(info)
    }

    @Test
    fun ownPackageInformation() {
        assertEquals(app.packageName, AppUtils.getPackageName(app))
        // Robolectric 默认安装了当前应用（版本信息为空），验证能查到自己、不抛异常
        assertNotNull(AppUtils.getVersionName(app))
        assertTrue(AppUtils.getVersionCode(app) >= 0)
        assertTrue(AppUtils.isInstalled(app, app.packageName))
    }

    @Test
    fun otherPackageVersionAndName() {
        install()
        assertEquals("1.2.3", AppUtils.getAppVersionName(app, pkg))
        assertEquals(123L, AppUtils.getAppVersionCode(app, pkg))
        assertEquals("Demo App", AppUtils.getAppName(app, pkg))
    }

    @Test
    fun versionCodeBeyondIntRange() {
        install(versionCode = 3_000_000_000L)
        assertEquals(3_000_000_000L, AppUtils.getAppVersionCode(app, pkg)) // 返回 Long，不会溢出
    }

    @Test
    fun missingPackageReturnsNullOrMinusOne() {
        val missing = "com.example.missing"
        assertNull(AppUtils.getAppVersionName(app, missing))
        assertEquals(-1L, AppUtils.getAppVersionCode(app, missing))
        assertNull(AppUtils.getAppName(app, missing))
        assertNull(AppUtils.getAppIcon(app, missing))
        assertNull(AppUtils.getAppPermission(app, missing))
        assertTrue(AppUtils.getAppSignatures(app, missing).isEmpty())
        assertNull(AppUtils.getAppSignatureSha256(app, missing))
        assertFalse(AppUtils.isInstalled(app, missing))
        assertFalse(AppUtils.isSystemApp(app, missing))
        assertFalse(AppUtils.isApkDebuggable(app, missing))
    }

    @Test
    fun appIconIsLoaded() {
        install()
        assertNotNull(AppUtils.getAppIcon(app, pkg)) // 无自定义图标时返回系统默认图标
    }

    @Test
    fun permissions() {
        install(permissions = arrayOf("android.permission.INTERNET", "android.permission.CAMERA"))
        assertArrayEquals(
            arrayOf("android.permission.INTERNET", "android.permission.CAMERA"),
            AppUtils.getAppPermission(app, pkg)
        )
    }

    @Test
    fun signaturesAndSha256() {
        val sig = Signature(byteArrayOf(1, 2, 3, 4))
        install(signatures = arrayOf(sig))
        val list = AppUtils.getAppSignatures(app, pkg)
        assertEquals(1, list.size)
        assertArrayEquals(byteArrayOf(1, 2, 3, 4), list[0])
        assertEquals(DigestUtils.sha256(byteArrayOf(1, 2, 3, 4)), AppUtils.getAppSignatureSha256(app, pkg))
    }

    @Test
    fun noSignatureMeansEmpty() {
        install()
        assertTrue(AppUtils.getAppSignatures(app, pkg).isEmpty())
        assertNull(AppUtils.getAppSignatureSha256(app, pkg))
    }

    @Test
    fun debuggableAndSystemFlags() {
        install(flags = ApplicationInfo.FLAG_DEBUGGABLE)
        assertTrue(AppUtils.isApkDebuggable(app, pkg))
        assertFalse(AppUtils.isSystemApp(app, pkg))
        install(name = "com.example.sys", flags = ApplicationInfo.FLAG_SYSTEM)
        assertTrue(AppUtils.isSystemApp(app, "com.example.sys"))
        assertFalse(AppUtils.isApkDebuggable(app, "com.example.sys"))
    }

    @Test
    fun ownDebuggableFlag() {
        app.applicationInfo.flags = app.applicationInfo.flags or ApplicationInfo.FLAG_DEBUGGABLE
        assertTrue(AppUtils.isApkDebuggable(app))
        app.applicationInfo.flags = app.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE.inv()
        assertFalse(AppUtils.isApkDebuggable(app))
    }

    @Test
    fun installedAndListed() {
        install()
        assertTrue(AppUtils.isInstalled(app, pkg))
        assertTrue(AppUtils.getInstalledPackages(app).any { it.packageName == pkg })
    }

    @Test
    fun checkPermission() {
        assertFalse(AppUtils.checkPermission(app, "android.permission.CAMERA"))
        shadowOf(app).grantPermissions("android.permission.CAMERA")
        assertTrue(AppUtils.checkPermission(app, "android.permission.CAMERA"))
    }

    @Test
    fun startAppLaunchesLauncherActivity() {
        install()
        val launch = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER).setPackage(pkg)
        val resolve = ResolveInfo().apply {
            activityInfo = ActivityInfo().apply {
                packageName = pkg
                name = "$pkg.MainActivity"
            }
        }
        shadowOf(pm).addResolveInfoForIntent(launch, resolve)
        assertTrue(AppUtils.startApp(app, pkg))
        val started = shadowOf(app).nextStartedActivity
        assertEquals(pkg, started.component?.packageName)
        assertTrue(started.flags and Intent.FLAG_ACTIVITY_NEW_TASK != 0)
    }

    @Test
    fun startAppWithoutLauncherActivityReturnsFalse() {
        install(name = "com.example.nolauncher")
        assertFalse(AppUtils.startApp(app, "com.example.nolauncher"))
        assertFalse(AppUtils.startApp(app, "com.example.missing"))
    }

    @Test
    fun uninstallSendsDeleteIntent() {
        assertTrue(AppUtils.uninstall(app, pkg))
        val i = shadowOf(app).nextStartedActivity
        assertEquals(Intent.ACTION_DELETE, i.action)
        assertEquals(Uri.parse("package:$pkg"), i.data)
        assertFalse(AppUtils.uninstall(app, ""))
    }
}
