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
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.provider.Settings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class IntentUtilsTest {

    private val app = RuntimeEnvironment.getApplication()
    private val context: Context = app

    private fun started(): Intent = requireNotNull(shadowOf(app).nextStartedActivity)

    @Test
    fun openUrl() {
        assertTrue(IntentUtils.openUrl(context, "https://example.com/a"))
        val i = started()
        assertEquals(Intent.ACTION_VIEW, i.action)
        assertEquals(Uri.parse("https://example.com/a"), i.data)
    }

    @Test
    fun applicationContextAddsNewTaskFlag() {
        IntentUtils.openUrl(context, "https://example.com")
        assertTrue(started().flags and Intent.FLAG_ACTIVITY_NEW_TASK != 0)
    }

    @Test
    fun activityContextDoesNotAddNewTaskFlag() {
        val activity = Robolectric.buildActivity(Activity::class.java).create().get()
        assertTrue(IntentUtils.openUrl(activity, "https://example.com"))
        val i = requireNotNull(shadowOf(activity).nextStartedActivity)
        assertEquals(0, i.flags and Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    @Test
    fun searchOpenMapDial() {
        assertTrue(IntentUtils.search(context, "kotlin"))
        val search = started()
        assertEquals(Intent.ACTION_WEB_SEARCH, search.action)
        assertEquals("kotlin", search.getStringExtra(SearchManager.QUERY))

        assertTrue(IntentUtils.openMap(context, "geo:39.9,116.4"))
        assertEquals(Uri.parse("geo:39.9,116.4"), started().data)

        assertTrue(IntentUtils.openDial(context))
        assertEquals(Intent.ACTION_CALL_BUTTON, started().action)

        assertTrue(IntentUtils.openDial(context, "10086"))
        val dial = started()
        assertEquals(Intent.ACTION_DIAL, dial.action)
        assertEquals(Uri.parse("tel:10086"), dial.data)
    }

    @Test
    fun sendMessage() {
        assertTrue(IntentUtils.sendMessage(context, "10086", "hi"))
        val i = started()
        assertEquals(Intent.ACTION_SENDTO, i.action)
        assertEquals(Uri.parse("smsto:10086"), i.data)
        assertEquals("hi", i.getStringExtra("sms_body"))
    }

    @Test
    fun callNeedsPermission() {
        assertFalse(IntentUtils.call(context, "10086"))
        assertNull(shadowOf(app).nextStartedActivity)
        shadowOf(app).grantPermissions(android.Manifest.permission.CALL_PHONE)
        assertTrue(IntentUtils.call(context, "10086"))
        val i = started()
        assertEquals(Intent.ACTION_CALL, i.action)
        assertEquals(Uri.parse("tel:10086"), i.data)
    }

    @Test
    fun settingsContactsCameraAppDetail() {
        assertTrue(IntentUtils.openSettings(context))
        assertEquals(Settings.ACTION_SETTINGS, started().action)

        assertTrue(IntentUtils.openSettings(context, Settings.ACTION_WIFI_SETTINGS))
        assertEquals(Settings.ACTION_WIFI_SETTINGS, started().action)

        assertTrue(IntentUtils.openSettings(context, "")) // 空 action 回退到总设置
        assertEquals(Settings.ACTION_SETTINGS, started().action)

        assertTrue(IntentUtils.openContacts(context))
        assertEquals(Intent.ACTION_VIEW, started().action)

        assertTrue(IntentUtils.openCamera(context))
        assertEquals(MediaStore.ACTION_IMAGE_CAPTURE, started().action)

        assertTrue(IntentUtils.openAppDetail("com.example.app", context))
        val detail = started()
        assertEquals(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, detail.action)
        assertEquals(Uri.fromParts("package", "com.example.app", null), detail.data)
    }

    @Test
    fun failureReturnsFalseInsteadOfThrowing() {
        // 没有任何应用能处理时，真实系统会抛 ActivityNotFoundException；这里模拟 SecurityException 分支
        val denied = object : android.content.ContextWrapper(context) {
            override fun startActivity(intent: Intent) {
                throw SecurityException("denied")
            }
        }
        assertFalse(IntentUtils.openUrl(denied, "https://example.com"))
        val missing = object : android.content.ContextWrapper(context) {
            override fun startActivity(intent: Intent) {
                throw android.content.ActivityNotFoundException()
            }
        }
        assertFalse(IntentUtils.openUrl(missing, "https://example.com"))
    }
}
