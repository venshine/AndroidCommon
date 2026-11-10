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

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import java.net.Inet4Address
import java.net.Inet6Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.Enumeration

/**
 * 网络状态工具，需要 `ACCESS_NETWORK_STATE` 权限（IP 查询不需要）。
 *
 * 不再提供 MAC 地址、网关、子网掩码：新版本系统已不允许应用读取 MAC，
 * 网关等信息请通过 `ConnectivityManager.getLinkProperties` 自行获取。
 *
 * @author venshine
 */
object NetworkUtils {

    private fun manager(context: Context): ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    /** 当前是否有可访问互联网的网络连接 */
    @JvmStatic
    @Suppress("DEPRECATION")
    @SuppressLint("MissingPermission") // 需要使用方声明 ACCESS_NETWORK_STATE，见类注释
    fun isNetworkAvailable(context: Context): Boolean {
        val cm = manager(context)
        if (Build.VERSION.SDK_INT >= 23) {
            val network = cm.activeNetwork ?: return false
            val caps = cm.getNetworkCapabilities(network) ?: return false
            return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        }
        return cm.activeNetworkInfo?.isConnected == true
    }

    /** 本机第一个非回环 IPv4 地址，没有返回 null */
    @JvmStatic
    fun getLocalIPv4(): String? = localAddresses().firstOrNull { it is Inet4Address }?.hostAddress

    /** 本机第一个非回环、非链路本地 IPv6 地址（不含 zone 后缀），没有返回 null */
    @JvmStatic
    fun getLocalIPv6(): String? =
        localAddresses().firstOrNull { it is Inet6Address }?.hostAddress?.substringBefore('%')

    /** 当前网络的 DNS 服务器列表，取不到返回空列表（API 23+） */
    @JvmStatic
    @SuppressLint("MissingPermission") // 需要使用方声明 ACCESS_NETWORK_STATE，见类注释
    fun getDNS(context: Context): List<String> {
        if (Build.VERSION.SDK_INT < 23) return emptyList()
        val cm = manager(context)
        val network = cm.activeNetwork ?: return emptyList()
        return cm.getLinkProperties(network)?.dnsServers?.mapNotNull { it.hostAddress } ?: emptyList()
    }

    @JvmStatic
    fun getDNS1(context: Context): String? = getDNS(context).getOrNull(0)

    @JvmStatic
    fun getDNS2(context: Context): String? = getDNS(context).getOrNull(1)

    private fun localAddresses(): List<InetAddress> = try {
        addressesOf(NetworkInterface.getNetworkInterfaces())
    } catch (e: java.net.SocketException) {
        emptyList()
    }

    /** [interfaces] 为 null 表示系统没有返回任何网络接口（JDK 文档允许，Android 17 真机上出现过） */
    internal fun addressesOf(interfaces: Enumeration<NetworkInterface>?): List<InetAddress> =
        interfaces?.toList().orEmpty()
            .filter { it.isUp }
            .flatMap { it.inetAddresses.toList() }
            .filter { !it.isLoopbackAddress && !it.isLinkLocalAddress }
}
