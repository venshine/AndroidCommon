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

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
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

@RunWith(RobolectricTestRunner::class)
class NotificationUtilsTest {

    private val context: Context = RuntimeEnvironment.getApplication()
    private val nm get() = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private fun allowPost() {
        shadowOf(RuntimeEnvironment.getApplication()).grantPermissions("android.permission.POST_NOTIFICATIONS")
    }

    @Test
    fun withoutPostNotificationsPermissionReturnsFalse() {
        assertFalse(NotificationUtils.create(context, 1, "t", "c", android.R.drawable.ic_dialog_info))
        assertEquals(0, shadowOf(nm).size())
    }

    @Test
    fun createPostsNotificationAndDefaultChannel() {
        allowPost()
        val ok = NotificationUtils.create(context, 7, "title", "content", android.R.drawable.ic_dialog_info)
        assertTrue(ok)
        val n = shadowOf(nm).getNotification(7)
        assertNotNull(n)
        assertEquals("title", n.extras.getCharSequence(Notification.EXTRA_TITLE).toString())
        assertEquals("content", n.extras.getCharSequence(Notification.EXTRA_TEXT).toString())
        assertEquals(NotificationUtils.DEFAULT_CHANNEL_ID, n.channelId)
        assertNotNull(nm.getNotificationChannel(NotificationUtils.DEFAULT_CHANNEL_ID)) // API 26+ 自动建渠道
        assertTrue(n.flags and Notification.FLAG_AUTO_CANCEL != 0) // 默认可点击清除
        assertTrue(n.flags and Notification.FLAG_ONGOING_EVENT == 0)
    }

    @Test
    fun ongoingAndCustomChannel() {
        allowPost()
        NotificationUtils.create(
            context, 3, "t", "c", android.R.drawable.ic_dialog_info,
            ongoing = true, autoCancel = false, channelId = "custom"
        )
        val n = shadowOf(nm).getNotification(3)
        assertEquals("custom", n.channelId)
        assertTrue(n.flags and Notification.FLAG_ONGOING_EVENT != 0)
        assertTrue(n.flags and Notification.FLAG_AUTO_CANCEL == 0)
    }

    @Test
    fun sameIdReplacesNotification() {
        allowPost()
        NotificationUtils.create(context, 1, "first", "c", android.R.drawable.ic_dialog_info)
        NotificationUtils.create(context, 1, "second", "c", android.R.drawable.ic_dialog_info)
        assertEquals(1, shadowOf(nm).size())
        assertEquals("second", shadowOf(nm).getNotification(1).extras.getCharSequence(Notification.EXTRA_TITLE).toString())
    }

    @Test
    fun cancelAndCancelAll() {
        allowPost()
        NotificationUtils.create(context, 1, "a", "c", android.R.drawable.ic_dialog_info)
        NotificationUtils.create(context, 2, "b", "c", android.R.drawable.ic_dialog_info)
        NotificationUtils.cancel(context, 1)
        assertNull(shadowOf(nm).getNotification(1))
        assertNotNull(shadowOf(nm).getNotification(2))
        NotificationUtils.cancelAll(context)
        assertEquals(0, shadowOf(nm).size())
    }

    @Test
    fun contentIntentIsAttached() {
        allowPost()
        val pi = NotificationUtils.getPendingIntentForActivity(context, Intent("demo.OPEN"))
        NotificationUtils.create(context, 5, "t", "c", android.R.drawable.ic_dialog_info, pi)
        assertEquals(pi, shadowOf(nm).getNotification(5).contentIntent)
    }

    @Test
    fun pendingIntentsAreImmutableByDefault() {
        val intent = Intent("demo.ACTION")
        val flags = listOf(
            NotificationUtils.getPendingIntentForActivity(context, intent),
            NotificationUtils.getPendingIntentForBroadcast(context, intent),
            NotificationUtils.getPendingIntentForService(context, intent)
        ).map { shadowOf(it).flags }
        flags.forEach {
            assertTrue(it and PendingIntent.FLAG_IMMUTABLE != 0)
            assertTrue(it and PendingIntent.FLAG_UPDATE_CURRENT != 0)
        }
    }

    @Test
    fun explicitMutableFlagIsRespected() {
        val mutable = 1 shl 25 // PendingIntent.FLAG_MUTABLE
        val pi = NotificationUtils.getPendingIntentForBroadcast(context, Intent("demo.ACTION"), mutable)
        val flags = shadowOf(pi).flags
        assertTrue(flags and mutable != 0)
        assertTrue(flags and PendingIntent.FLAG_IMMUTABLE == 0)
    }

    @Test
    fun requestCodeIsPassedThrough() {
        val pi = NotificationUtils.getPendingIntentForActivity(context, Intent("demo.A"), PendingIntent.FLAG_UPDATE_CURRENT, 42)
        assertEquals(42, shadowOf(pi).requestCode)
    }
}
