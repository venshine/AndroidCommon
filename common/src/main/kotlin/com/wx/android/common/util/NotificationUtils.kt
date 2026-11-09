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
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Process

/**
 * 通知工具。
 *
 * - API 26 起通知必须属于某个渠道，[create] 会自动创建默认渠道；
 * - 新版本系统发通知需要 `POST_NOTIFICATIONS` 运行时权限，没有权限时 [create] 返回 false；
 * - [PendingIntent] 默认带 `FLAG_IMMUTABLE`，需要可变时自行传入 `FLAG_MUTABLE`。
 *
 * @author venshine
 */
object NotificationUtils {

    /** 默认通知渠道 id */
    const val DEFAULT_CHANNEL_ID = "common_default"

    private const val FLAG_MUTABLE = 1 shl 25
    private const val POST_NOTIFICATIONS = "android.permission.POST_NOTIFICATIONS"

    private fun manager(context: Context): NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private fun withImmutable(flags: Int): Int =
        if (Build.VERSION.SDK_INT >= 23 && flags and FLAG_MUTABLE == 0) flags or PendingIntent.FLAG_IMMUTABLE else flags

    @JvmStatic
    @JvmOverloads
    fun getPendingIntentForActivity(
        context: Context, intent: android.content.Intent, flags: Int = PendingIntent.FLAG_UPDATE_CURRENT,
        requestCode: Int = 0
    ): PendingIntent = PendingIntent.getActivity(context, requestCode, intent, withImmutable(flags))

    @JvmStatic
    @JvmOverloads
    fun getPendingIntentForBroadcast(
        context: Context, intent: android.content.Intent, flags: Int = PendingIntent.FLAG_UPDATE_CURRENT,
        requestCode: Int = 0
    ): PendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, withImmutable(flags))

    @JvmStatic
    @JvmOverloads
    fun getPendingIntentForService(
        context: Context, intent: android.content.Intent, flags: Int = PendingIntent.FLAG_UPDATE_CURRENT,
        requestCode: Int = 0
    ): PendingIntent = PendingIntent.getService(context, requestCode, intent, withImmutable(flags))

    /**
     * 发送通知。
     *
     * @param id 通知 id，相同 id 会覆盖
     * @param channelId 通知渠道，默认使用本库的默认渠道（API 26+ 自动创建）
     * @return 已提交返回 true；缺少 `POST_NOTIFICATIONS` 权限（API 33+）返回 false
     */
    @JvmStatic
    @JvmOverloads
    fun create(
        context: Context, id: Int, title: CharSequence, content: CharSequence, smallIcon: Int,
        pendingIntent: PendingIntent? = null, ongoing: Boolean = false, autoCancel: Boolean = true,
        largeIcon: Bitmap? = null, channelId: String = DEFAULT_CHANNEL_ID
    ): Boolean {
        if (Build.VERSION.SDK_INT >= 33 &&
            context.checkPermission(POST_NOTIFICATIONS, Process.myPid(), Process.myUid()) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }
        val nm = manager(context)
        @Suppress("DEPRECATION")
        val builder = if (Build.VERSION.SDK_INT >= 26) {
            if (nm.getNotificationChannel(channelId) == null) {
                nm.createNotificationChannel(
                    NotificationChannel(channelId, context.applicationInfo.loadLabel(context.packageManager),
                        NotificationManager.IMPORTANCE_DEFAULT)
                )
            }
            Notification.Builder(context, channelId)
        } else {
            Notification.Builder(context).setDefaults(Notification.DEFAULT_ALL)
        }
        builder.setSmallIcon(smallIcon)
            .setContentTitle(title)
            .setContentText(content)
            .setWhen(System.currentTimeMillis())
            .setAutoCancel(autoCancel)
            .setOngoing(ongoing)
        if (largeIcon != null) builder.setLargeIcon(largeIcon)
        if (pendingIntent != null) builder.setContentIntent(pendingIntent)
        nm.notify(id, builder.build())
        return true
    }

    @JvmStatic
    fun cancel(context: Context, id: Int) = manager(context).cancel(id)

    @JvmStatic
    fun cancelAll(context: Context) = manager(context).cancelAll()
}
