package com.wx.android.common.widget;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

/**
 * Round Drawable
 *
 * @author fengwx
 */
public class RoundDrawable extends Drawable {

    private static final int BORDER_COLOR = 0xFF000000;
    private static final int BORDER_WIDTH = 5;

    private final Paint mPaint;
    private final Paint mBorderPaint;

    private final RectF mRectF;
    private final RectF mBorderRectF;

    private final int mBitmapWidth;
    private final int mBitmapHeight;

    private final int mBorderWidth;

    public RoundDrawable(Bitmap bitmap) {
        this(bitmap, BORDER_COLOR, BORDER_WIDTH);
    }

    public RoundDrawable(Bitmap bitmap, int borderColor, int borderWidth) {
        if (bitmap == null) {
            throw new IllegalArgumentException("bitmap cannot be null.");
        }
        // paint
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);

        // border paint
        mBorderPaint = new Paint();
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setDither(true);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setColor(borderColor);
        mBorderPaint.setStrokeWidth(borderWidth);
        mBorderWidth = borderWidth;

        mRectF = new RectF();
        mBorderRectF = new RectF();

        BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mPaint.setShader(shader);

        mBitmapWidth = bitmap.getWidth();
        mBitmapHeight = bitmap.getHeight();
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawOval(mRectF, mPaint);
        canvas.drawOval(mBorderRectF, mBorderPaint);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        bounds.left += mBorderWidth;
        bounds.top += mBorderWidth;
        bounds.right -= mBorderWidth;
        bounds.bottom -= mBorderWidth;
        mBorderRectF.set(bounds);
        mRectF.set(bounds);
    }

    @Override
    public void setAlpha(int alpha) {
        if (mPaint.getAlpha() != alpha) {
            mPaint.setAlpha(alpha);
            invalidateSelf();
        }
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public int getIntrinsicWidth() {
        return mBitmapWidth;
    }

    @Override
    public int getIntrinsicHeight() {
        return mBitmapHeight;
    }

    public void setAntiAlias(boolean aa) {
        mPaint.setAntiAlias(aa);
        invalidateSelf();
    }

    @Override
    public void setFilterBitmap(boolean filter) {
        mPaint.setFilterBitmap(filter);
        invalidateSelf();
    }

    @Override
    public void setDither(boolean dither) {
        mPaint.setDither(dither);
        invalidateSelf();
    }

}
