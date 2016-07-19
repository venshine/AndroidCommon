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

import android.Manifest;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;

/**
 * Intent操作
 *
 * @author venshine
 */
public class IntentUtils {

    /**
     * Search a word in a browser
     *
     * @param context
     * @param string
     */
    public static void search(Context context, String string) {
        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
        intent.putExtra(SearchManager.QUERY, string);
        context.startActivity(intent);
    }

    /**
     * Open url in a browser
     *
     * @param context
     * @param url
     */
    public static void openUrl(Context context, String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }

    /**
     * Open map in a map app
     *
     * @param context
     * @param parh
     */
    public static void openMap(Context context, String parh) {
        Uri uri = Uri.parse(parh);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }

    /**
     * Open dial
     *
     * @param context
     */
    public static void openDial(Context context) {
        Intent intent = new Intent(Intent.ACTION_CALL_BUTTON);
        context.startActivity(intent);
    }

    /**
     * Open dial with a number
     *
     * @param context
     * @param number
     */
    public static void openDial(Context context, String number) {
        Uri uri = Uri.parse("tel:" + number);
        Intent intent = new Intent(Intent.ACTION_DIAL, uri);
        context.startActivity(intent);
    }

    /**
     * Call up, requires Permission "android.permission.CALL_PHONE"
     *
     * @param context
     * @param number
     */
    public static void call(Context context, String number) {
        Uri uri = Uri.parse("tel:" + number);
        Intent intent = new Intent(Intent.ACTION_CALL, uri);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        context.startActivity(intent);
    }

    /**
     * Send message
     *
     * @param context
     * @param sendNo
     * @param sendContent
     */
    public static void sendMessage(Context context, String sendNo, String sendContent) {
        Uri uri = Uri.parse("smsto:" + sendNo);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra("sms_body", sendContent);
        context.startActivity(intent);
    }

    /**
     * Open contact person
     *
     * @param context
     */
    public static void openContacts(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW, ContactsContract.Contacts.CONTENT_URI);
        context.startActivity(intent);
    }

    /**
     * Open system settings
     *
     * @param context
     */
    public static void openSettings(Context context) {
        openSettings(context, Settings.ACTION_SETTINGS);
    }

    /**
     * Open system settings
     *
     * @param context
     * @param action  The action contains global system-level device preferences.
     */
    public static void openSettings(Context context, String action) {
        if (!StringUtils.isEmpty(action)) {
            Intent intent = new Intent(action);
            context.startActivity(intent);
        } else {
            openSettings(context);
        }
    }

    /**
     * Open camera
     *
     * @param context
     */
    public static void openCamera(Context context) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        context.startActivity(intent);
    }

    /**
     * Take camera, this photo data will be returned in onActivityResult()
     *
     * @param activity
     * @param requestCode
     */
    public static void takeCamera(Activity activity, int requestCode) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * Choose photo, this photo data will be returned in onActivityResult()
     *
     * @param activity
     * @param requestCode
     */
    public static void choosePhoto(Activity activity, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * Open App Detail page
     *
     * @param packageName
     * @param context
     */
    public static void openAppDetail(String packageName, Context context) {
        Intent intent = new Intent();
        final int apiLevel = Build.VERSION.SDK_INT;
        if (apiLevel >= 9) {
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", packageName, null);
            intent.setData(uri);
        } else {
            final String appPkgName = (apiLevel == 8 ? "pkg" : "com.android.settings.ApplicationPkgName");
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            intent.putExtra(appPkgName, packageName);
        }
        context.startActivity(intent);
    }

}
