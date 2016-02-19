package com.wx.android.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Number format
 *
 * @author fengwx
 */
public class NumberUtils {

    /**
     * Keep one decimal
     *
     * @param number
     * @return
     */
    public static String formatOneDecimal(float number) {
        DecimalFormat oneDec = new DecimalFormat("##0.0");
        return oneDec.format(number);
    }

    /**
     * Keep two decimal
     *
     * @param number
     * @return
     */
    public static String formatTwoDecimal(float number) {
        DecimalFormat twoDec = new DecimalFormat("##0.00");
        return twoDec.format(number);
    }

    /**
     * Keep two decimal percentage
     *
     * @param number
     * @return
     */
    public static String formatTwoDecimalPercent(float number) {
        return formatTwoDecimal(number) + "%";
    }

    /**
     * Rounding
     *
     * @param number
     * @param scale  scale of the result returned.
     * @return
     */
    public static double roundingNumber(float number, int scale) {
        return roundingNumber(number, scale, RoundingMode.HALF_UP);
    }

    /**
     * Rounding
     *
     * @param number
     * @param scale        scale of the result returned.
     * @param roundingMode rounding mode to be used to round the result.
     * @return
     */
    public static double roundingNumber(float number, int scale, RoundingMode roundingMode) {
        BigDecimal b = new BigDecimal(number);
        return b.setScale(scale, roundingMode).doubleValue();
    }

}
