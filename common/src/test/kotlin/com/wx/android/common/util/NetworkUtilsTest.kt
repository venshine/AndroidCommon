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
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadows.ShadowNetworkCapabilities
import java.net.InetAddress

@RunWith(RobolectricTestRunner::class)
class NetworkUtilsTest {

    private val context: Context = RuntimeEnvironment.getApplication()
    private val cm get() = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    /** 取 Robolectric 默认就有的活动网络（已连接的 Wi-Fi），按需替换它的能力 / 链路属性 */
    private fun activeNetwork(capabilities: NetworkCapabilities?, link: LinkProperties? = null): Network {
        val shadow = shadowOf(cm)
        val network = requireNotNull(cm.activeNetwork)
        if (capabilities != null) shadow.setNetworkCapabilities(network, capabilities)
        if (link != null) shadow.setLinkProperties(network, link)
        return network
    }

    private fun linkWithDns(vararg dns: String): LinkProperties {
        val lp = LinkProperties()
        val add = LinkProperties::class.java.getMethod("addDnsServer", InetAddress::class.java) // 隐藏 API，仅测试里用反射
        dns.forEach { add.invoke(lp, InetAddress.getByName(it)) }
        return lp
    }

    @Test
    fun availableWhenActiveNetworkHasInternet() {
        val caps = ShadowNetworkCapabilities.newInstance().also {
            shadowOf(it).addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        }
        activeNetwork(caps)
        assertTrue(NetworkUtils.isNetworkAvailable(context))
    }

    @Test
    fun unavailableWhenNetworkLacksInternetCapability() {
        activeNetwork(ShadowNetworkCapabilities.newInstance())
        assertFalse(NetworkUtils.isNetworkAvailable(context))
    }

    @Test
    fun unavailableWithoutActiveNetwork() {
        shadowOf(cm).clearAllNetworks()
        assertFalse(NetworkUtils.isNetworkAvailable(context))
    }

    @Test
    fun dnsServersFromLinkProperties() {
        activeNetwork(null, linkWithDns("8.8.8.8", "1.1.1.1"))
        assertEquals(listOf("8.8.8.8", "1.1.1.1"), NetworkUtils.getDNS(context))
        assertEquals("8.8.8.8", NetworkUtils.getDNS1(context))
        assertEquals("1.1.1.1", NetworkUtils.getDNS2(context))
    }

    @Test
    fun singleDnsLeavesSecondNull() {
        activeNetwork(null, linkWithDns("9.9.9.9"))
        assertEquals("9.9.9.9", NetworkUtils.getDNS1(context))
        assertNull(NetworkUtils.getDNS2(context))
    }

    @Test
    fun noDnsWithoutActiveNetwork() {
        shadowOf(cm).clearAllNetworks()
        assertTrue(NetworkUtils.getDNS(context).isEmpty())
        assertNull(NetworkUtils.getDNS1(context))
    }

    // 回归：Android 17 真机上 NetworkInterface.getNetworkInterfaces() 返回 null（JDK 文档允许），
    // 旧实现直接 toList() 抛 NPE，示例首页一启动就崩溃
    @Test
    fun noNetworkInterfacesMeansNoAddress() {
        assertTrue(NetworkUtils.addressesOf(null).isEmpty())
    }

    @Test
    fun localAddressesAreNeverLoopbackOrLinkLocal() {
        // 结果取决于运行测试的机器：有地址就必须是非回环、非链路本地且格式正确，没有就返回 null
        NetworkUtils.getLocalIPv4()?.let {
            val addr = InetAddress.getByName(it)
            assertTrue(addr is java.net.Inet4Address)
            assertFalse(addr.isLoopbackAddress || addr.isLinkLocalAddress)
        }
        NetworkUtils.getLocalIPv6()?.let {
            assertFalse(it.contains('%')) // 已去掉 zone 后缀
            val addr = InetAddress.getByName(it)
            assertTrue(addr is java.net.Inet6Address)
            assertFalse(addr.isLoopbackAddress || addr.isLinkLocalAddress)
        }
    }
}
