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

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import com.wx.android.common.util.ClipboardUtils
import com.wx.android.common.util.InputMethodUtils
import com.wx.android.common.util.IntentUtils
import com.wx.android.common.util.NotificationUtils
import com.wx.android.common.util.ToastUtils
import com.wx.android.common.util.VibratorUtils

/**
 * 系统能力：每个按钮调一个工具方法，返回值记在下方，缺权限 / 缺应用时看得到是 false 而不是崩溃
 *
 * @author venshine
 */
class SystemActivity : BaseActivity() {

    private val logs = ArrayDeque<String>()

    // 通知权限（API 33+）授予后自动补发一次
    private val notificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) sendNotification() else log("NotificationUtils.create()", false)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_system)
        setToolbar(R.string.demo_system, "XxxUtils")

        click(R.id.btn_toast) {
            ToastUtils.showToast(getString(R.string.system_toast_text))
            log("ToastUtils.showToast()", "已显示")
        }
        click(R.id.btn_clipboard) {
            ClipboardUtils.copy(this, getString(R.string.system_clipboard_text))
            log("ClipboardUtils.copy() → getText()", ClipboardUtils.getText(this) ?: "null")
        }
        click(R.id.btn_vibrate) {
            VibratorUtils.vibrate(this, 300)
            log("VibratorUtils.vibrate(300)", "已调用")
        }
        click(R.id.btn_notification) { sendNotification() }
        click(R.id.btn_settings) { log("IntentUtils.openSettings()", IntentUtils.openSettings(this)) }
        click(R.id.btn_app_detail) {
            log("IntentUtils.openAppDetail()", IntentUtils.openAppDetail(packageName, this))
        }

        val input = findViewById<EditText>(R.id.keyboard_input)
        click(R.id.btn_keyboard_show) {
            input.requestFocus()
            log("InputMethodUtils.showSoftInput()", InputMethodUtils.showSoftInput(input))
        }
        click(R.id.btn_keyboard_hide) {
            log("InputMethodUtils.hideSoftInput()", InputMethodUtils.hideSoftInput(input))
        }
        renderLog()
    }

    private fun sendNotification() {
        val sent = NotificationUtils.create(
            this, NOTIFICATION_ID, getString(R.string.system_notification_title),
            getString(R.string.system_notification_content), R.drawable.ic_logo
        )
        if (!sent && Build.VERSION.SDK_INT >= 33) {
            // create 返回 false 的原因是没有 POST_NOTIFICATIONS：先申请，授予后会再发一次
            log("NotificationUtils.create()", false)
            notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            log("NotificationUtils.create()", sent)
        }
    }

    private fun click(id: Int, action: () -> Unit) = findViewById<View>(id).setOnClickListener { action() }

    private fun log(api: String, result: Any) {
        logs.addFirst("$api → $result")
        while (logs.size > MAX_LOGS) logs.removeLast()
        renderLog()
    }

    private fun renderLog() {
        findViewById<TextView>(R.id.system_log).text =
            if (logs.isEmpty()) getString(R.string.system_log_empty) else logs.joinToString("\n")
    }

    private companion object {
        const val NOTIFICATION_ID = 1001
        const val MAX_LOGS = 6
    }
}
