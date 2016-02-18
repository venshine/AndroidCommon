package com.wx.android.common.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Image
 *
 * @author fengwx
 */
public class ImageUtils {

    /**
     * take a screenshot
     *
     * @param activity
     * @param filePath
     * @return
     */
    public static boolean screenshot(Activity activity, String filePath) {
        View rootView = activity.getWindow().getDecorView();
        rootView.setDrawingCacheEnabled(true);
        rootView.buildDrawingCache();
        Bitmap bitmap = rootView.getDrawingCache();
        File imagePath = new File(filePath);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
                if (null != bitmap) {
                    bitmap.recycle();
                    bitmap = null;
                }
            } catch (Exception e) {
            }
            rootView.destroyDrawingCache();
            rootView.setDrawingCacheEnabled(false);
        }
        return false;
    }

}
