package com.wx.android.common.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Bitmap
 *
 * @author fengwx
 */
public class BitmapUtils {

    public static final int UNSPECIFIED = 0;

    /**
     * Convert resId to drawable
     *
     * @param context
     * @param resId
     * @return
     */
    public static Drawable resToDrawable(Context context, int resId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return context.getDrawable(resId);
        }
        return context.getResources().getDrawable(resId);
    }

    /**
     * Convert Bitmap to byte array
     *
     * @param b
     * @return
     */
    public static byte[] bitmapToByte(Bitmap b) {
        if (b == null) {
            return null;
        }
        ByteArrayOutputStream o = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.PNG, 100, o);
        return o.toByteArray();
    }

    /**
     * Convert byte array to Bitmap
     *
     * @param b
     * @return
     */
    public static Bitmap byteToBitmap(byte[] b) {
        return (b == null || b.length == 0) ? null : BitmapFactory.decodeByteArray(b, 0, b.length);
    }

    /**
     * Convert Drawable to Bitmap
     *
     * @param d
     * @return
     */
    public static Bitmap drawableToBitmap(Drawable d) {
        return d == null ? null : ((BitmapDrawable) d).getBitmap();
    }

    /**
     * Convert Bitmap to Drawable
     *
     * @param b
     * @return
     */
    public static Drawable bitmapToDrawable(Bitmap b) {
        return b == null ? null : new BitmapDrawable(b);
    }

    /**
     * Convert Drawable to byte array
     *
     * @param d
     * @return
     */
    public static byte[] drawableToByte(Drawable d) {
        return bitmapToByte(drawableToBitmap(d));
    }

    /**
     * Convert byte array to Drawable
     *
     * @param b
     * @return
     */
    public static Drawable byteToDrawable(byte[] b) {
        return bitmapToDrawable(byteToBitmap(b));
    }

    /**
     * Convert view to bitmap
     *
     * @param view
     * @param width
     * @param height
     * @return
     */
    public static Bitmap convertViewToBitmap(View view, int width, int height) {
        view.measure(View.MeasureSpec.makeMeasureSpec(width, (width == UNSPECIFIED) ? View.MeasureSpec.UNSPECIFIED :
                        View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(height, (height == UNSPECIFIED) ? View.MeasureSpec.UNSPECIFIED :
                        View.MeasureSpec.EXACTLY));
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
     * Resize image by width and height
     *
     * @param originalBitmap
     * @param w
     * @param h
     * @return
     */
    public static Bitmap resizeImage(Bitmap originalBitmap, int w, int h) {
        if (originalBitmap == null) {
            return null;
        }
        int width = originalBitmap.getWidth();
        int height = originalBitmap.getHeight();
        if (width <= w && height <= h) {
            return originalBitmap;
        }
        float screenRatio = (float) w / h;
        float ratio = (float) width / height;
        if (screenRatio >= ratio) {
            width = (int) (h * ratio);
            height = h;
        } else {
            height = (int) (w / ratio);
            width = w;
        }
        return Bitmap.createScaledBitmap(originalBitmap, width, height, true);
    }

    /**
     * Decode bitmap
     *
     * @param is
     * @param context
     * @return
     * @throws IOException
     */
    public static Bitmap decodeBitmap(InputStream is, Context context) throws IOException {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 设置成了true,不占用内存，只获取bitmap宽高
        byte[] data = isToByte(is);//将InputStream转为byte数组，可以多次读取
//        BitmapFactory.decodeStream(is, null, options);InputStream流只能被读取一次，下次读取就为空了。
        BitmapFactory.decodeByteArray(data, 0, data.length, options);
        options.inSampleSize = calculateInSampleSize(options, context); // 调用上面定义的方法计算inSampleSize值
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }

    /**
     * Calculate inSampleSize
     *
     * @param options
     * @param context
     * @return
     */
    private static int calculateInSampleSize(BitmapFactory.Options options, Context context) {
        // 源图片的高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        int h = DisplayUtils.getScreenHeight(context);
        int w = DisplayUtils.getScreenWidth(context);
        if (height > h || width > w) {
            // 计算出实际宽高和目标宽高的比率
            final int heightRatio = Math.round((float) height / (float) h);
            final int widthRatio = Math.round((float) width / (float) w);
            // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
            // 一定都会大于等于目标的宽和高。
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    /**
     * Convert InputStream to byte array
     *
     * @param is
     * @return
     * @throws IOException
     */
    private static byte[] isToByte(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buff = new byte[1024];
        int len = 0;
        while ((len = is.read(buff)) != -1) {
            baos.write(buff, 0, len);
        }
        is.close();
        baos.close();
        return baos.toByteArray();
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
