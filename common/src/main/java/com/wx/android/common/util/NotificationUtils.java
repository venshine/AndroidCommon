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
package com.wx.android.common.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;

/**
 * 通知栏操作
 *
 * @author venshine
 */
public class NotificationUtils {

    /**
     * Get PendingIntent
     *
     * @param context
     * @return
     */
    public static PendingIntent getPendingIntentForActivity(Context context) {
        return getPendingIntentForActivity(context, new Intent());
    }

    /**
     * Get PendingIntent
     *
     * @param context
     * @param intent
     * @return
     */
    public static PendingIntent getPendingIntentForActivity(Context context, Intent intent) {
        return getPendingIntentForActivity(context, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Get PendingIntent
     *
     * @param context
     * @param intent
     * @param flags
     * @return
     */
    public static PendingIntent getPendingIntentForActivity(Context context, Intent intent, int flags) {
        return getPendingIntentForActivity(context, 0, intent, flags);
    }

    /**
     * Get PendingIntent
     *
     * @param context
     * @param requestCode
     * @param intent
     * @param flags
     * @return
     */
    public static PendingIntent getPendingIntentForActivity(Context context, int requestCode, Intent intent, int
            flags) {
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, requestCode, intent,
                flags);
        return pendingIntent;
    }

    /**
     * Get PendingIntent
     *
     * @param context
     * @param requestCode
     * @param intent
     * @param flags
     * @return
     */
    public static PendingIntent getPendingIntentForBroadcast(Context context, int requestCode, Intent intent, int
            flags) {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, requestCode, intent,
                flags);
        return pendingIntent;
    }

    /**
     * Get PendingIntent
     *
     * @param context
     * @param requestCode
     * @param intent
     * @param flags
     * @return
     */
    public static PendingIntent getPendingIntentForService(Context context, int requestCode, Intent intent, int flags) {
        PendingIntent pendingIntent = PendingIntent.getService(
                context, requestCode, intent,
                flags);
        return pendingIntent;
    }

    /**
     * Create Notification
     *
     * @param context
     * @param title
     * @param content
     * @param smallIcon
     */
    public static void create(Context context, CharSequence title, CharSequence content, int smallIcon) {
        create(context, 0, title, content, smallIcon, getPendingIntentForActivity(context));
    }

    /**
     * Create Notification
     *
     * @param context
     * @param id
     * @param title
     * @param content
     * @param smallIcon
     * @param pendingIntent
     */
    public static void create(Context context, int id, CharSequence title, CharSequence content, int smallIcon,
                              PendingIntent pendingIntent) {
        create(context, id, title, content, smallIcon, false, true, pendingIntent);
    }

    /**
     * Create Notification
     *
     * @param context
     * @param id
     * @param title
     * @param content
     * @param smallIcon
     * @param ongoing
     * @param autoCancel
     * @param pendingIntent
     */
    public static void create(Context context, int id, CharSequence title, CharSequence content, int smallIcon,
                              boolean ongoing, boolean autoCancel,
                              PendingIntent pendingIntent) {
        create(context, id, title, content, null, smallIcon, smallIcon, ongoing, autoCancel, Notification
                .VISIBILITY_PUBLIC, pendingIntent);
    }

    /**
     * Create Notification
     *
     * @param context
     * @param id
     * @param title
     * @param content
     * @param ticker
     * @param smallIcon
     * @param largeIcon
     * @param ongoing
     * @param autoCancel
     * @param pendingIntent
     */
    public static void create(Context context, int id, CharSequence title, CharSequence content, CharSequence ticker,
                              int smallIcon, int largeIcon, boolean ongoing, boolean autoCancel, int visibility,
                              PendingIntent pendingIntent) {
        create(context, id, title, content, ticker, smallIcon, largeIcon, 0, ongoing, autoCancel, Notification
                .PRIORITY_DEFAULT, visibility, pendingIntent);
    }

    /**
     * Create Notification
     *
     * @param context
     * @param id
     * @param title
     * @param content
     * @param ticker
     * @param smallIcon
     * @param largeIcon
     * @param number
     * @param ongoing
     * @param autoCancel
     * @param priority
     * @param pendingIntent
     */
    public static void create(Context context, int id, CharSequence title, CharSequence content, CharSequence ticker,
                              int smallIcon, int largeIcon, int number, boolean ongoing, boolean autoCancel, int
                                      priority, int visibility,
                              PendingIntent pendingIntent) {
        // Notification.Builder
        Notification.Builder builder = new Notification.Builder(
                context);
        if (pendingIntent != null) {
            builder.setContentIntent(pendingIntent);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Show controls on lock screen even when user hides sensitive content.
            builder.setVisibility(visibility);
        }
        builder.setSmallIcon(smallIcon);
        builder.setLargeIcon(BitmapFactory.decodeResource(
                context.getResources(), largeIcon));
        builder.setWhen(System.currentTimeMillis());
        builder.setAutoCancel(autoCancel);
        builder.setContentTitle(title);
        builder.setContentText(content);
        builder.setTicker(ticker);
        builder.setNumber(number);
        builder.setDefaults(Notification.DEFAULT_ALL);
        builder.setOngoing(ongoing);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            builder.setPriority(priority);
        }
        // Notification
        Notification notification = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notification = builder.build();
        } else {
            notification = builder.getNotification();
        }
        notification.flags = Notification.FLAG_SHOW_LIGHTS;
        // notify
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context
                .NOTIFICATION_SERVICE);
        notificationManager.notify(id, notification);
    }

    /**
     * Cancel a previously shown notification.
     *
     * @param context
     * @param id
     */
    public static void cancel(Context context, int id) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context
                .NOTIFICATION_SERVICE);
        notificationManager.cancel(id);
    }

    /**
     * Cancel all previously shown notifications.
     *
     * @param context
     */
    public static void cancelAll(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context
                .NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

}
