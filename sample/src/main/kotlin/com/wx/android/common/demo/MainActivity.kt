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

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.wx.android.common.util.AppUtils
import com.wx.android.common.util.BitmapFilters
import com.wx.android.common.util.ClipboardUtils
import com.wx.android.common.util.DigestUtils
import com.wx.android.common.util.NetworkUtils
import com.wx.android.common.util.ToastUtils
import com.wx.android.common.util.VerifyUtils

/**
 * 示例：逐个演示库里几类常用工具，结果输出在页面下方。
 */
class MainActivity : AppCompatActivity() {

    private lateinit var output: TextView
    private lateinit var image: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        output = findViewById(R.id.output)
        image = findViewById(R.id.image)

        findViewById<android.view.View>(R.id.btn_digest).setOnClickListener {
            output.text = "md5(hello) = ${DigestUtils.md5("hello")}\nsha256(hello) = ${DigestUtils.sha256("hello")}"
        }
        findViewById<android.view.View>(R.id.btn_verify).setOnClickListener {
            output.text = "isIdCard(11010519491231002X) = ${VerifyUtils.isIdCard("11010519491231002X")}\n" +
                "isMobileNumber(13800138000) = ${VerifyUtils.isMobileNumber("13800138000")}"
        }
        findViewById<android.view.View>(R.id.btn_toast).setOnClickListener {
            ToastUtils.showToast("Hello AndroidCommon")
        }
        findViewById<android.view.View>(R.id.btn_clipboard).setOnClickListener {
            ClipboardUtils.copy(this, "AndroidCommon sample")
            output.text = "clipboard = ${ClipboardUtils.getText(this)}"
        }
        findViewById<android.view.View>(R.id.btn_bitmap).setOnClickListener {
            image.setImageBitmap(BitmapFilters.convertGreyImg(gradientBitmap()))
        }
        findViewById<android.view.View>(R.id.btn_app).setOnClickListener {
            output.text = "${AppUtils.getAppName(this)} ${AppUtils.getVersionName(this)} " +
                "(${AppUtils.getVersionCode(this)})\nnetwork available = ${NetworkUtils.isNetworkAvailable(this)}"
        }
    }

    /** 生成一张红到蓝的渐变图，用来演示滤镜 */
    private fun gradientBitmap(): Bitmap {
        val size = 96
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        for (x in 0 until size) {
            val t = x * 255 / (size - 1)
            for (y in 0 until size) {
                bitmap.setPixel(x, y, Color.rgb(255 - t, 80, t))
            }
        }
        return bitmap
    }
}
