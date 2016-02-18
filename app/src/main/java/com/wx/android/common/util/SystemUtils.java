package com.wx.android.common.util;

import android.os.Build;

/**
 * System info
 *
 * @author fengwx
 */
public class SystemUtils {

    /**
     * The brand (e.g., Xiaomi) the software is customized for, if any.
     *
     * @return
     */
    public static String getBrand() {
        return Build.BRAND;
    }

    /**
     * The name of the underlying board, like "MSM8660_SURF".
     *
     * @return
     */
    public static String getBoard() {
        return Build.BOARD;
    }

    /**
     * The end-user-visible name for the end product, like "MI-ONE Plus".
     *
     * @return
     */
    public static String getModel() {
        return Build.MODEL;
    }

    /**
     * Either a changelist number, or a label like "JZO54K".
     *
     * @return
     */
    public static String getID() {
        return Build.ID;
    }

    /**
     * The user-visible version string, like "4.1.2".
     *
     * @return
     */
    public static String getVersionRelease() {
        return Build.VERSION.RELEASE;
    }

    /**
     * The user-visible SDK version of the framework.
     *
     * @return
     */
    public static int getVersionSDK() {
        return Build.VERSION.SDK_INT;
    }

    /**
     * A string that uniquely identifies this build. Do not attempt to parse this value.
     *
     * @return
     */
    public static String getFingerPrint() {
        return Build.FINGERPRINT;
    }

    /**
     * The name of the overall product, like "mione_plus".
     *
     * @return
     */
    public static String getProduct() {
        return Build.PRODUCT;
    }

    /**
     * The manufacturer of the product/hardware, like "Xiaomi".
     *
     * @return
     */
    public static String getManufacturer() {
        return Build.MANUFACTURER;
    }

    /**
     * The name of the industrial design, like "mione_plus".
     *
     * @return
     */
    public static String getDevice() {
        return Build.DEVICE;
    }

    /**
     * The name of the instruction set (CPU type + ABI convention) of native code, like "armeabi-v7a".
     *
     * @return
     */
    public static String getCpuAbi() {
        return Build.CPU_ABI;
    }

    /**
     * The name of the second instruction set (CPU type + ABI convention) of native code, like "armeabi".
     *
     * @return
     */
    public static String getCpuAbi2() {
        return Build.CPU_ABI2;
    }

    /**
     * A build ID string meant for displaying to the user, like "JZO54K".
     *
     * @return
     */
    public static String getDisplay() {
        return Build.DISPLAY;
    }

}
