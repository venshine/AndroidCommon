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
package com.wx.android.common.util

import android.content.Context
import android.telephony.TelephonyManager

/**
 * SIM 卡与运营商的基础信息，不需要危险权限。
 *
 * 不再提供 IMEI、IMSI、SIM 序列号、手机号：这些标识自 Android 10 起第三方应用无法获取，
 * 设备标识请改用应用自行生成并保存的 UUID。
 *
 * @author venshine
 */
object TelephonyUtils {

    private fun manager(context: Context): TelephonyManager? =
        context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager

    /** SIM 运营商代码（MCC+MNC），无 SIM 卡时为空串或 null */
    @JvmStatic
    fun getSimOperator(context: Context): String? = manager(context)?.simOperator

    /** SIM 运营商名称 */
    @JvmStatic
    fun getSimOperatorName(context: Context): String? = manager(context)?.simOperatorName

    /** SIM 卡所属国家 ISO 码（小写，如 cn） */
    @JvmStatic
    fun getSimCountryIso(context: Context): String? = manager(context)?.simCountryIso

    /** 当前注册网络的运营商代码（MCC+MNC） */
    @JvmStatic
    fun getNetworkOperator(context: Context): String? = manager(context)?.networkOperator

    /** 当前注册网络的运营商名称 */
    @JvmStatic
    fun getNetworkOperatorName(context: Context): String? = manager(context)?.networkOperatorName

    /** 当前注册网络所在国家 ISO 码 */
    @JvmStatic
    fun getNetworkCountryIso(context: Context): String? = manager(context)?.networkCountryIso

    /** SIM 卡状态，取值见 `TelephonyManager.SIM_STATE_*`；设备无电话功能返回 `SIM_STATE_UNKNOWN` */
    @JvmStatic
    fun getSimState(context: Context): Int = manager(context)?.simState ?: TelephonyManager.SIM_STATE_UNKNOWN

    /** 是否插有 SIM 卡（状态既不是 ABSENT 也不是 UNKNOWN） */
    @JvmStatic
    fun hasIccCard(context: Context): Boolean {
        val state = getSimState(context)
        return state != TelephonyManager.SIM_STATE_ABSENT && state != TelephonyManager.SIM_STATE_UNKNOWN
    }

    /** 是否处于漫游 */
    @JvmStatic
    fun isNetworkRoaming(context: Context): Boolean = manager(context)?.isNetworkRoaming == true
}
