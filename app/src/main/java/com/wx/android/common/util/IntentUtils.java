package com.wx.android.common.util;

import android.Manifest;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;

/**
 * Intent
 *
 * @author fengwx
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
        if (!StringUtils.equalsNull(action)) {
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

}
