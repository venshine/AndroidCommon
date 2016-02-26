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

    private static final int BORDER_COLOR = 0;
    private static final int BORDER_WIDTH = 0;

    private final Paint mPaint;
    private final Paint mBorderPaint;

    private final RectF mRectF;
    private final RectF mBorderRectF;

    private int mBitmapWidth;
    private int mBitmapHeight;

    private final int mBorderWidth;
    private final boolean mIsCircle;

    public RoundDrawable(Bitmap bitmap) {
        this(bitmap, BORDER_COLOR, BORDER_WIDTH, false);
    }

    public RoundDrawable(Bitmap bitmap, int borderColor, int borderWidth, boolean isCircle) {
        if (bitmap == null) {
            throw new IllegalArgumentException("bitmap cannot be null.");
        }
        // bitmap
        mBitmapWidth = bitmap.getWidth();
        mBitmapHeight = bitmap.getHeight();
        mIsCircle = isCircle;
        mBorderWidth = borderWidth;
        mRectF = new RectF();
        mBorderRectF = new RectF();
        Bitmap bm = null;
        if (mIsCircle) {
            bm = getSquareBitmap(bitmap);
        }

        // paint
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setFilterBitmap(true);
        BitmapShader shader = new BitmapShader(bm == null ? bitmap : bm, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mPaint.setShader(shader);

        // border paint
        mBorderPaint = new Paint();
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setDither(true);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setColor(borderColor);
        mBorderPaint.setStrokeWidth(borderWidth);
    }

    /**
     * Get bitmap of square
     *
     * @param bitmap
     * @return
     */
    private Bitmap getSquareBitmap(Bitmap bitmap) {
        Bitmap bm;
        if (mBitmapWidth > mBitmapHeight) {
            bm = Bitmap.createBitmap(bitmap, (mBitmapWidth - mBitmapHeight) / 2, 0, mBitmapHeight,
                    mBitmapHeight);
            mBitmapWidth = mBitmapHeight;
        } else if (mBitmapWidth < mBitmapHeight) {
            bm = Bitmap.createBitmap(bitmap, 0, (mBitmapHeight - mBitmapWidth) / 2, mBitmapWidth, mBitmapWidth);
            mBitmapHeight = mBitmapWidth;
        } else {
            bm = bitmap;
        }
        return bm;
    }

    @Override
    public void draw(Canvas canvas) {
        if (mIsCircle) {
            canvas.drawCircle(mRectF.centerX(), mRectF.centerY(), mRectF.centerX(), mPaint);
            canvas.drawCircle(mBorderRectF.centerX(), mBorderRectF.centerY(), mBorderRectF.centerX() - mBorderWidth /
                            2.0f,
                    mBorderPaint);
        } else {
            canvas.drawOval(mRectF, mPaint);
            canvas.drawOval(mBorderRectF, mBorderPaint);
        }
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        mRectF.set(bounds);
        bounds.inset(mBorderWidth / 2, mBorderWidth / 2);   // FIXME fine tuning, [bounds.inset(mBorderWidth, mBorderWidth);]
        mBorderRectF.set(bounds);
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
