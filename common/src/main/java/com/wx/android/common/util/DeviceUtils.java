/*
 * Copyright (C) 2016 venshine.cn@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wx.android.common.util;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresPermission;
import android.telephony.TelephonyManager;
import android.view.Window;
import android.view.WindowManager;

import java.util.UUID;

/**
 * 设备信息
 *
 * @author venshine
 */
public class DeviceUtils {

    /**
     * Get uuid
     *
     * @param context
     * @return
     */
    public static String getUUID(Context context) {
        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        final String tmDevice, tmSerial, tmPhone, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider
                .Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String uniqueId = deviceUuid.toString();

        return uniqueId;
    }

    /**
     * Get screen brightness mode，must declare the
     * {@link android.Manifest.permission#WRITE_SETTINGS} permission in its manifest.
     *
     * @param context
     * @return
     */
    public static int getScreenBrightnessMode(Context context) {
        return Settings.System.getInt(context.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
    }

    /**
     * Judge screen brightness mode is auto mode，must declare the
     * {@link android.Manifest.permission#WRITE_SETTINGS} permission in its manifest.
     *
     * @param context
     * @return true:auto;false: manual;default:true
     */
    public static boolean isScreenBrightnessModeAuto(Context context) {
        return getScreenBrightnessMode(context) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC ? true
                : false;
    }

    /**
     * Set screen brightness mode，must declare the
     * {@link android.Manifest.permission#WRITE_SETTINGS} permission in its manifest.
     *
     * @param context
     * @param auto
     * @return
     */
    public static boolean setScreenBrightnessMode(Context context, boolean auto) {
        boolean result = true;
        if (isScreenBrightnessModeAuto(context) != auto) {
            result = Settings.System.putInt(context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE,
                    auto ? Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
                            : Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        }
        return result;
    }

    /**
     * Get screen brightness, must declare the
     * {@link android.Manifest.permission#WRITE_SETTINGS} permission in its manifest.
     *
     * @param context
     * @return brightness:0-255; default:255
     */
    public static int getScreenBrightness(Context context) {
        return Settings.System.getInt(context.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS, 255);
    }

    /**
     * Set screen brightness, cannot change window brightness.must declare the
     * {@link android.Manifest.permission#WRITE_SETTINGS} permission in its manifest.
     *
     * @param context
     * @param screenBrightness 0-255
     * @return
     */
    public static boolean setScreenBrightness(Context context,
                                              int screenBrightness) {
        int brightness = screenBrightness;
        if (screenBrightness < 1) {
            brightness = 1;
        } else if (screenBrightness > 255) {
            brightness = screenBrightness % 255;
            if (brightness == 0) {
                brightness = 255;
            }
        }
        boolean result = Settings.System.putInt(context.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS, brightness);
        return result;
    }

    /**
     * Set window brightness, cannot change system brightness.
     *
     * @param activity
     * @param screenBrightness 0-255
     */
    public static void setWindowBrightness(Activity activity,
                                           float screenBrightness) {
        float brightness = screenBrightness;
        if (screenBrightness < 1) {
            brightness = 1;
        } else if (screenBrightness > 255) {
            brightness = screenBrightness % 255;
            if (brightness == 0) {
                brightness = 255;
            }
        }
        Window window = activity.getWindow();
        WindowManager.LayoutParams localLayoutParams = window.getAttributes();
        localLayoutParams.screenBrightness = brightness / 255.0f;
        window.setAttributes(localLayoutParams);
    }

    /**
     * Set screen brightness and change system brightness, must declare the
     * {@link android.Manifest.permission#WRITE_SETTINGS} permission in its manifest.
     *
     * @param activity
     * @param screenBrightness 0-255
     * @return
     */
    public static boolean setScreenBrightnessAndApply(Activity activity,
                                                      int screenBrightness) {
        boolean result = setScreenBrightness(activity, screenBrightness);
        if (result) {
            setWindowBrightness(activity, screenBrightness);
        }
        return result;
    }

    /**
     * Get screen dormant time, must declare the
     * {@link android.Manifest.permission#WRITE_SETTINGS} permission in its manifest.
     *
     * @param context
     * @return dormantTime:ms, default:30s
     */
    public static int getScreenDormantTime(Context context) {
        return Settings.System.getInt(context.getContentResolver(),
                Settings.System.SCREEN_OFF_TIMEOUT, 30000);
    }

    /**
     * Set screen dormant time by millis, must declare the
     * {@link android.Manifest.permission#WRITE_SETTINGS} permission in its manifest.
     *
     * @param context
     * @return
     */
    public static boolean setScreenDormantTime(Context context, int millis) {
        return Settings.System.putInt(context.getContentResolver(),
                Settings.System.SCREEN_OFF_TIMEOUT, millis);
    }

    /**
     * Get airplane mode, must declare the
     * {@link android.Manifest.permission#WRITE_APN_SETTINGS} permission in its manifest.
     *
     * @param context
     * @return 1:open, 0:close, default:close
     */
    public static int getAirplaneMode(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return Settings.System.getInt(context.getContentResolver(),
                    Settings.System.AIRPLANE_MODE_ON, 0);
        } else {
            return Settings.Global.getInt(context.getContentResolver(),
                    Settings.Global.AIRPLANE_MODE_ON, 0);
        }
    }

    /**
     * Judge whether airplane is open, must declare the
     * {@link android.Manifest.permission#WRITE_APN_SETTINGS} permission in its manifest.
     *
     * @param context
     * @return true:open, false:close, default:close
     */
    public static boolean isAirplaneModeOpen(Context context) {
        return getAirplaneMode(context) == 1 ? true : false;
    }

    /**
     * Set airplane mode, must declare the
     * {@link android.Manifest.permission#WRITE_APN_SETTINGS} permission in its manifest.
     *
     * @param context
     * @param enable
     * @return
     */
    public static boolean setAirplaneMode(Context context, boolean enable) {
        boolean result = true;
        if (isAirplaneModeOpen(context) != enable) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                result = Settings.System.putInt(context.getContentResolver(),
                        Settings.System.AIRPLANE_MODE_ON, enable ? 1 : 0);
            } else {
                result = Settings.Global.putInt(context.getContentResolver(),
                        Settings.Global.AIRPLANE_MODE_ON, enable ? 1 : 0);
            }
            context.sendBroadcast(new Intent(
                    Intent.ACTION_AIRPLANE_MODE_CHANGED));
        }
        return result;
    }

    /**
     * Get bluetooth state
     *
     * @return STATE_OFF, STATE_TURNING_OFF, STATE_ON, STATE_TURNING_ON, NONE
     * @throws Exception
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH)
    public static Integer getBluetoothState() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter
                .getDefaultAdapter();
        if (bluetoothAdapter == null) {
            return null;
        } else {
            return bluetoothAdapter.getState();
        }
    }

    /**
     * Judge bluetooth is open
     *
     * @return true:open, false:close.
     */
    public static boolean isBluetoothOpen() {
        Integer bluetoothStateCode = getBluetoothState();
        if (bluetoothStateCode == null) {
            return false;
        }
        return bluetoothStateCode == BluetoothAdapter.STATE_ON
                || bluetoothStateCode == BluetoothAdapter.STATE_TURNING_ON ? true
                : false;
    }

    /**
     * Set bluetooth
     *
     * @param enable
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_ADMIN)
    public static void setBluetooth(boolean enable) throws Exception {
        if (isBluetoothOpen() != enable) {
            if (enable) {
                BluetoothAdapter.getDefaultAdapter().enable();
            } else {
                BluetoothAdapter.getDefaultAdapter().disable();
            }
        }
    }

    /**
     * Get media volume
     *
     * @param context
     * @return volume:0-15
     */
    public static int getMediaVolume(Context context) {
        return ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE)).getStreamVolume(AudioManager
                .STREAM_MUSIC);
    }

    /**
     * Set media volume
     *
     * @param context
     * @return volume:0-15
     */
    public static void setMediaVolume(Context context, int mediaVloume) {
        if (mediaVloume < 0) {
            mediaVloume = 0;
        } else if (mediaVloume > 15) {
            mediaVloume = mediaVloume % 15;
            if (mediaVloume == 0) {
                mediaVloume = 15;
            }
        }
        ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE)).setStreamVolume(AudioManager.STREAM_MUSIC,
                mediaVloume, AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
    }

    /**
     * Get ring volume
     *
     * @param context
     * @return volume:0-7
     */
    public static int getRingVolume(Context context) {
        return ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE)).getStreamVolume(AudioManager
                .STREAM_RING);
    }

    /**
     * Set ring volume
     *
     * @param context
     * @return volume:0-7
     */
    public static void setRingVolume(Context context, int ringVloume) {
        if (ringVloume < 0) {
            ringVloume = 0;
        } else if (ringVloume > 7) {
            ringVloume = ringVloume % 7;
            if (ringVloume == 0) {
                ringVloume = 7;
            }
        }
        ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE)).setStreamVolume(AudioManager.STREAM_RING,
                ringVloume, AudioManager.FLAG_PLAY_SOUND);
    }

}
