package com.wx.android.common.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Image
 *
 * @author fengwx
 */
public class ImageUtils {

    public static final int UNSPECIFIED = 0;

    /**
     * Convert view to bitmap
     *
     * @param view
     * @param width
     * @param height
     * @return
     */
    public static Bitmap convertViewToBitmap(View view, int width, int height) {
        view.measure(View.MeasureSpec.makeMeasureSpec(width, (width == UNSPECIFIED) ? View.MeasureSpec.UNSPECIFIED : View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(height, (height == UNSPECIFIED) ? View.MeasureSpec.UNSPECIFIED : View.MeasureSpec.EXACTLY));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        view.draw(new Canvas(bitmap));
        return bitmap;
    }

    /**
     * Convert view to bitmap
     *
     * @param view
     * @return
     */
    public static Bitmap convertViewToBitmap(View view) {
        return convertViewToBitmap(view, UNSPECIFIED, UNSPECIFIED);
    }

    /**
     * take a screenshot
     *
     * @param activity
     * @param filePath
     * @return
     */
    public static boolean screenshot(Activity activity, String filePath) {
        View decorView = activity.getWindow().getDecorView();
        decorView.setDrawingCacheEnabled(true);
        decorView.buildDrawingCache();
        Bitmap bitmap = decorView.getDrawingCache();
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
            decorView.destroyDrawingCache();
            decorView.setDrawingCacheEnabled(false);
        }
        return false;
    }

}
