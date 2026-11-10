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
package com.wx.android.common.demo

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import com.wx.android.common.util.AppUtils
import com.wx.android.common.util.DisplayUtils
import com.wx.android.common.util.NetworkUtils

/**
 * 首页：设备与应用信息速览 + 示例入口 + 接入方式
 *
 * @author venshine
 */
class MainActivity : BaseActivity() {

    /** 示例 App 与库同版本发布，版本号只在 sample/build.gradle 的 versionName 维护一处 */
    private val version: String by lazy { AppUtils.getVersionName(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.source_button).setOnClickListener { openSource() }
        findViewById<TextView>(R.id.version_chip).text = "v$version"
        findViewById<View>(R.id.info_refresh).setOnClickListener { renderInfo() }
        initCards()
        initInstall()
    }

    override fun onResume() {
        super.onResume()
        renderInfo()
    }

    /** 设备与应用信息：全部来自库里的 AppUtils / DisplayUtils / NetworkUtils */
    private fun renderInfo() {
        findViewById<TextView>(R.id.info_app_value).text = AppUtils.getAppName(this) ?: getString(R.string.info_none)
        findViewById<TextView>(R.id.info_version_value).text =
            "${AppUtils.getVersionName(this)} (${AppUtils.getVersionCode(this)})"
        findViewById<TextView>(R.id.info_package_value).text = AppUtils.getPackageName(this)
        val orientation = getString(
            if (DisplayUtils.isLandscape(this)) R.string.info_orientation_landscape else R.string.info_orientation_portrait
        )
        findViewById<TextView>(R.id.info_screen_value).text =
            "${DisplayUtils.getScreenWidth(this)} × ${DisplayUtils.getScreenHeight(this)} px · $orientation"
        findViewById<TextView>(R.id.info_density_value).text =
            "${DisplayUtils.getScreenDensityDPI(this)} dpi · ${DisplayUtils.getScreenDensity(this)}x"
        findViewById<TextView>(R.id.info_network_value).text = getString(
            if (NetworkUtils.isNetworkAvailable(this)) R.string.info_network_on else R.string.info_network_off
        )
        findViewById<TextView>(R.id.info_ip_value).text = localIPv4() ?: getString(R.string.info_none)
    }

    /**
     * 真机（小米 Android 17）上 NetworkInterface.getNetworkInterfaces() 返回 null，库的 getLocalIPv4() 会抛 NPE；
     * 库问题已写入报告交回，示例先兜住，避免首页崩溃
     */
    private fun localIPv4(): String? = NetworkUtils.getLocalIPv4()

    private fun initCards() {
        findViewById<View>(R.id.card_filter).setOnClickListener { open(FilterActivity::class.java) }
        bindCard(R.id.card_verify, R.drawable.ic_shield, R.string.demo_verify, R.string.demo_verify_desc,
            VerifyActivity::class.java)
        bindCard(R.id.card_system, R.drawable.ic_bolt, R.string.demo_system, R.string.demo_system_desc,
            SystemActivity::class.java)
        bindCard(R.id.card_view, R.drawable.ic_wave, R.string.demo_view, R.string.demo_view_desc,
            ViewActivity::class.java)
        bindCard(R.id.card_calendar, R.drawable.ic_calendar, R.string.demo_calendar, R.string.demo_calendar_desc,
            CalendarActivity::class.java)
    }

    private fun bindCard(@IdRes id: Int, @DrawableRes icon: Int, @StringRes title: Int, @StringRes desc: Int,
                         target: Class<out Activity>) {
        val card = findViewById<View>(id)
        card.findViewById<ImageView>(R.id.card_icon).setImageResource(icon)
        card.findViewById<TextView>(R.id.card_title).setText(title)
        card.findViewById<TextView>(R.id.card_desc).setText(desc)
        card.setOnClickListener { open(target) }
    }

    private fun initInstall() {
        val dependency = "com.github.venshine:AndroidCommon:$version"
        val code = "// settings.gradle\nmaven { url = 'https://jitpack.io' }\n\n// build.gradle\nimplementation '$dependency'"
        findViewById<TextView>(R.id.install_code).text =
            CodeHighlighter.highlight(this, code, listOf("implementation", "maven"))
        findViewById<TextView>(R.id.copy_dependency).apply {
            setStartIcon(tintedIcon(R.drawable.ic_copy, colorOf(R.color.text), 16f))
            setOnClickListener {
                copyToClipboard("implementation '$dependency'")
                flashCopied(R.string.home_copied_dependency, R.string.home_copy_dependency)
            }
        }
    }

    private fun open(target: Class<out Activity>) = startActivity(Intent(this, target))

    private fun openSource() {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(SOURCE_URL)))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, SOURCE_URL, Toast.LENGTH_LONG).show()
        }
    }

    private companion object {
        const val SOURCE_URL = "https://github.com/venshine/AndroidCommon"
    }
}
