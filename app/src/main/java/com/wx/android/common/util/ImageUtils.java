package com.wx.android.common.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;

/**
 * Image
 *
 * @author fengwx
 */
public class ImageUtils {

    public static final int CAMERA_REQ_CODE = 0x0001;

    public static final int PICTURE_REQ_CODE = 0x0002;

    /**
     * Open camera
     *
     * @param activity
     * @param path
     */
    public void openCamera(Activity activity, String path) {
        openCamera(activity, path, "IMG_" + System.currentTimeMillis() + ".jpg");
    }

    /**
     * Open camera
     *
     * @param activity
     * @param path
     * @param fileName
     */
    public void openCamera(Activity activity, String path, String fileName) {
        FileUtils.makeDirs(path);
        File cameraFile = new File(path, fileName);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile));
        activity.startActivityForResult(intent, CAMERA_REQ_CODE);
    }

    /**
     * Open picture
     *
     * @param activity
     */
    public void openPicture(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        activity.startActivityForResult(intent, PICTURE_REQ_CODE);
    }

}
