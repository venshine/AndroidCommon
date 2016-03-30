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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

/**
 * 网络信息
 *
 * @author venshine
 */
public class NetworkUtils {

    private static final String ETHERNET = "eth0";
    private static final String WLAN = "wlan0";

    private static final String DNS1 = "[net.dns1]";
    private static final String DNS2 = "[net.dns2]";
    private static final String ETHERNET_GATEWAY = "[dhcp.eth0.gateway]";
    private static final String WLAN_GATEWAY = "[dhcp.wlan0.gateway]";
    private static final String ETHERNET_MASK = "[dhcp.eth0.mask]";
    private static final String WLAN_MASK = "[dhcp.wlan0.mask]";


    /**
     * Judge whether current network is available
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cwjManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cwjManager.getActiveNetworkInfo();
        if (info != null) {
            return info.isAvailable();
        }
        return false;
    }

    /**
     * Get local ipv4 address
     *
     * @return
     */
    public static String getLocalIPv4() {
        return getLocalIp(true);
    }

    /**
     * Get local ipv6 address
     *
     * @return
     */
    public static String getLocalIPv6() {
        return getLocalIp(false);
    }

    /**
     * Get local ip address
     *
     * @param useIPv4
     * @return
     */
    private static String getLocalIp(boolean useIPv4) {
        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()) {
                NetworkInterface nif = en.nextElement();
                Enumeration<InetAddress> inet = nif.getInetAddresses();
                while (inet.hasMoreElements()) {
                    InetAddress addr = inet.nextElement();
                    if (!addr.isLoopbackAddress()) {
                        String ip = StringUtils.toUpperCase(addr.getHostAddress());
                        boolean isIPv4 = addr instanceof Inet4Address;
                        if (useIPv4) {
                            if (isIPv4) {
                                return ip;
                            }
                        } else {
                            if (!isIPv4) {
                                int delim = ip.indexOf('%');
                                return delim < 0 ? ip : ip.substring(0, delim);
                            }
                        }
                    }
                }

            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Get wlan mac address
     *
     * @return
     */
    public static String getWlanMacAddress() {
        return getMacAddress(WLAN);
    }

    /**
     * Get ethernet mac address
     *
     * @return
     */
    public static String getEthernetMacAddress() {
        return getMacAddress(ETHERNET);
    }

    /**
     * Get mac address
     *
     * @param interfaceName
     * @return
     */
    private static String getMacAddress(String interfaceName) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (interfaceName != null) {
                    if (!intf.getName().equalsIgnoreCase(interfaceName))
                        continue;
                }
                byte[] mac;
                mac = intf.getHardwareAddress();
                if (mac == null)
                    return "";
                StringBuilder buf = new StringBuilder();
                for (int idx = 0; idx < mac.length; idx++)
                    buf.append(String.format("%02X:", mac[idx]));
                if (buf.length() > 0)
                    buf.deleteCharAt(buf.length() - 1);
                return buf.toString();
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Get dns1
     *
     * @return
     */
    public static String getDNS1() {
        return getPropInfo(DNS1);
    }

    /**
     * Get dns2
     *
     * @return
     */
    public static String getDNS2() {
        return getPropInfo(DNS2);
    }

    /**
     * Get ethernet gateway
     *
     * @return
     */
    public static String getEthernetGateway() {
        return getPropInfo(ETHERNET_GATEWAY);
    }

    /**
     * Get wlan gateway
     *
     * @return
     */
    public static String getWlanGateway() {
        return getPropInfo(WLAN_GATEWAY);
    }

    /**
     * Get ethernet mask
     *
     * @return
     */
    public static String getEthernetMask() {
        return getPropInfo(ETHERNET_MASK);
    }

    /**
     * Get wlan mask
     *
     * @return
     */
    public static String getWlanMask() {
        return getPropInfo(WLAN_MASK);
    }

    /**
     * Get prop information by different interface name
     *
     * @param interfaceName
     * @return
     */
    private static String getPropInfo(String interfaceName) {
        String re = "";
        try {
            Process process = Runtime.getRuntime().exec("getprop");
            Properties pr = new Properties();
            pr.load(process.getInputStream());
            re = pr.getProperty(interfaceName, "");
            if (!StringUtils.isEmpty(re) && re.length() > 6) {
                re = re.substring(1, re.length() - 1);
                return re;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return re;
    }

}
