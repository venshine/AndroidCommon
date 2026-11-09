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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class TelephonyUtilsTest {

    private val context: Context = RuntimeEnvironment.getApplication()
    private val shadow get() = shadowOf(context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager)

    @Test
    fun operatorInfo() {
        shadow.setSimOperator("46000")
        shadow.setSimOperatorName("CMCC")
        shadow.setSimCountryIso("cn")
        shadow.setNetworkOperator("46001")
        shadow.setNetworkOperatorName("UNICOM")
        shadow.setNetworkCountryIso("cn")
        assertEquals("46000", TelephonyUtils.getSimOperator(context))
        assertEquals("CMCC", TelephonyUtils.getSimOperatorName(context))
        assertEquals("cn", TelephonyUtils.getSimCountryIso(context))
        assertEquals("46001", TelephonyUtils.getNetworkOperator(context))
        assertEquals("UNICOM", TelephonyUtils.getNetworkOperatorName(context))
        assertEquals("cn", TelephonyUtils.getNetworkCountryIso(context))
    }

    @Test
    fun simStateAndHasIccCard() {
        shadow.setSimState(TelephonyManager.SIM_STATE_READY)
        assertEquals(TelephonyManager.SIM_STATE_READY, TelephonyUtils.getSimState(context))
        assertTrue(TelephonyUtils.hasIccCard(context))
        shadow.setSimState(TelephonyManager.SIM_STATE_ABSENT)
        assertFalse(TelephonyUtils.hasIccCard(context))
        shadow.setSimState(TelephonyManager.SIM_STATE_UNKNOWN)
        assertFalse(TelephonyUtils.hasIccCard(context))
        shadow.setSimState(TelephonyManager.SIM_STATE_PIN_REQUIRED) // 有卡但被锁，仍算有卡
        assertTrue(TelephonyUtils.hasIccCard(context))
    }

    @Test
    fun roaming() {
        shadow.setIsNetworkRoaming(true)
        assertTrue(TelephonyUtils.isNetworkRoaming(context))
        shadow.setIsNetworkRoaming(false)
        assertFalse(TelephonyUtils.isNetworkRoaming(context))
    }
}
