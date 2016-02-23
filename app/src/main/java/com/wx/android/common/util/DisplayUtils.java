package com.wx.android.common.util;

import android.content.Context;

/**
 * Display Information, includes density and so on.
 *
 * @author fengwx
 */
public class DisplayUtils {

    /**
     * Convert dp to px by the density of phone
     *
     * @param context
     * @param dp
     * @return
     */
    public static int dip2px(Context context, float dp) {
        if (context == null) {
            return -1;
        }
        return (int) (dipToPx(context, dp) + 0.5f);
    }

    /**
     * Convert dp to px
     *
     * @param context
     * @param dp
     * @return
     */
    private static float dipToPx(Context context, float dp) {
        if (context == null) {
            return -1;
        }
        float scale = context.getResources().getDisplayMetrics().density;
        return dp * scale;
    }

    /**
     * Convert px to dp by the density of phone
     *
     * @param context
     * @param px
     * @return
     */
    public static int px2dip(Context context, float px) {
        if (context == null) {
            return -1;
        }
        return (int) (pxToDip(context, px) + 0.5f);
    }

    /**
     * Convert px to dp
     *
     * @param context
     * @param px
     * @return
     */
    private static float pxToDip(Context context, float px) {
        if (context == null) {
            return -1;
        }
        float scale = context.getResources().getDisplayMetrics().density;
        return px / scale;
    }
}
