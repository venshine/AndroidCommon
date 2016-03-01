package com.wx.android.common.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.wx.android.common.util.BitmapUtils;

/**
 * @author fengwx
 */
public class RoundImageView extends ImageView {

    private int mBorderColor;

    private int mBorderWidth;

    private boolean mIsCircle;

    public RoundImageView(Context context) {
        super(context);
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RoundImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * Get border color
     *
     * @return
     */
    public int getBorderColor() {
        return mBorderColor;
    }

    /**
     * Set border color, such as @code(Colors.BLACK)
     *
     * @param borderColor
     */
    public void setBorderColor(int borderColor) {
        this.mBorderColor = borderColor;
    }

    /**
     * Set border color, such as '#aarrggbb'
     *
     * @param borderColor
     */
    public void setBorderColor(String borderColor) {
        this.mBorderColor = Color.parseColor(borderColor);
    }

    /**
     * Get border width
     *
     * @return
     */
    public int getBorderWidth() {
        return mBorderWidth;
    }

    /**
     * Set border width
     *
     * @param borderWidth
     */
    public void setBorderWidth(int borderWidth) {
        this.mBorderWidth = borderWidth;
    }

    /**
     * Is circle
     *
     * @return
     */
    public boolean isCircle() {
        return mIsCircle;
    }

    /**
     * Set circle
     *
     * @param isCircle
     */
    public void setCircle(boolean isCircle) {
        this.mIsCircle = isCircle;
    }

    @Override
    public void setImageResource(int resId) {
        setImageResource(resId, mBorderColor, mBorderWidth, mIsCircle);
    }

    /**
     * Sets a drawable as the content of this ImageView.
     *
     * @param resId
     * @param borderColor
     * @param borderWidth
     * @param isCircle
     */
    public void setImageResource(int resId, int borderColor, int borderWidth, boolean isCircle) {
        setImageDrawable(getResources().getDrawable(resId), borderColor, borderWidth, isCircle);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        setImageDrawable(drawable, mBorderColor, mBorderWidth, mIsCircle);
    }

    /**
     * Sets a drawable as the content of this ImageView.
     *
     * @param drawable
     * @param borderColor
     * @param borderWidth
     * @param isCircle
     */
    public void setImageDrawable(Drawable drawable, int borderColor, int borderWidth, boolean isCircle) {
        Bitmap bm = BitmapUtils.drawableToBitmap(drawable);
        setImageBitmap(bm, borderColor, borderWidth, isCircle);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        setImageBitmap(bm, mBorderColor, mBorderWidth, mIsCircle);
    }

    /**
     * Sets a Bitmap as the content of this ImageView.
     *
     * @param bm
     * @param borderColor
     * @param borderWidth
     * @param isCircle
     */
    public void setImageBitmap(Bitmap bm, int borderColor, int borderWidth, boolean isCircle) {
        super.setImageDrawable(new RoundDrawable(bm, borderColor, borderWidth, isCircle));
    }
}
