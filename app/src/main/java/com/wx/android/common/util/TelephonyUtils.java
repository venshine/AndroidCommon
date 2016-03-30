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

import android.content.Context;
import android.telephony.TelephonyManager;

/**
 * TelePhony, Requires Permission "READ_PHONE_STATE"
 * 
 * @author venshine
 */
public class TelephonyUtils {

	/**
	 * Returns a constant indicating the call state (cellular) on the device, includes CALL_STATE_IDLE,
	 * CALL_STATE_OFFHOOK, CALL_STATE_RINGING.
	 * 
	 * @param context
	 * @return
	 */
	public static int getCallState(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getCallState();
	}

	/**
	 * Returns a constant indicating the type of activity on a data connection.
	 * 
	 * @param context
	 * @return
	 */
	public static int getDataActivity(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getDataActivity();
	}

	/**
	 * Returns a constant indicating the current data connection state.
	 * 
	 * @param context
	 * @return
	 */
	public static int getDataState(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getDataState();
	}

	/**
	 * Returns the unique device ID, for example, the IMEI for GSM and the MEID or ESN for CDMA phones. Return null if
	 * device ID is not available.
	 * 
	 * @param context
	 * @return
	 */
	public static String getIMEI(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getDeviceId();
	}

	/**
	 * Returns the phone number string for line 1, for example, the MSISDN for a GSM phone. Return null if it is
	 * unavailable.
	 * 
	 * @param context
	 * @return
	 */
	public static String getLine1Number(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getLine1Number();
	}

	/**
	 * Returns the ISO country code equivalent of the current registered operator's MCC (Mobile Country Code).
	 * 
	 * @param context
	 * @return
	 */
	public static String getNetworkCountryIso(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getNetworkCountryIso();
	}

	/**
	 * Returns the numeric name (MCC+MNC) of current registered operator.
	 * 
	 * @param context
	 * @return
	 */
	public static String getNetworkOperator(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getNetworkOperator();
	}

	/**
	 * Returns the alphabetic name of current registered operator.
	 * 
	 * @param context
	 * @return
	 */
	public static String getNetworkOperatorName(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getNetworkOperatorName();
	}

	/**
	 * Returns a constant indicating the radio technology (network type) currently in use on the device for data
	 * transmission.
	 * 
	 * @param context
	 * @return
	 */
	public static int getNetworkType(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getNetworkType();
	}

	/**
	 * Returns the ISO country code equivalent for the SIM provider's country code.
	 * 
	 * @param context
	 * @return
	 */
	public static String getSimCountryIso(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getSimCountryIso();
	}

	/**
	 * Returns the MCC+MNC (mobile country code + mobile network code) of the provider of the SIM. 5 or 6 decimal
	 * digits.
	 * 
	 * @param context
	 * @return
	 */
	public static String getSimOperator(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getSimOperator();
	}

	/**
	 * Returns the Service Provider Name (SPN).
	 * 
	 * @param context
	 * @return
	 */
	public static String getSimOperatorName(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getSimOperatorName();
	}

	/**
	 * Returns the serial number of the SIM, if applicable. Return null if it is unavailable.
	 * 
	 * @param context
	 * @return
	 */
	public static String getSimSerialNumber(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getSimSerialNumber();
	}

	/**
	 * Returns a constant indicating the state of the device SIM card.
	 * 
	 * @param context
	 * @return
	 */
	public static int getSimState(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getSimState();
	}

	/**
	 * Returns the unique subscriber ID, for example, the IMSI for a GSM phone. Return null if it is unavailable.
	 * 
	 * @param context
	 * @return
	 */
	public static String getIMSI(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getSubscriberId();
	}

	/**
	 * Return true if a ICC card is present.
	 * 
	 * @param context
	 * @return
	 */
	public static boolean hasIccCard(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.hasIccCard();
	}

	/**
	 * Returns true if the device is considered roaming on the current network, for GSM purposes.
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkRoaming(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.isNetworkRoaming();
	}

}
