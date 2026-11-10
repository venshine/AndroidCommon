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

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Shader
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.wx.android.common.widget.PictureView

/**
 * [PictureView] 全屏查看：双击放大 / 还原，双指缩放，单指拖动。
 *
 * PictureView 以屏幕尺寸为基准居中，所以本页不套系统栏内边距，直接铺满屏幕。
 * 它是二级页面，不导出。
 *
 * @author venshine
 */
class PictureActivity : AppCompatActivity() {

    private lateinit var picture: PictureView
    private lateinit var scaleLabel: TextView

    @SuppressLint("ClickableViewAccessibility") // 只读取触摸后的缩放比例，不拦截事件
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picture)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val light = !DemoTheme.isNight(this)
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = light
            isAppearanceLightNavigationBars = light
        }

        picture = findViewById(R.id.picture_view)
        scaleLabel = findViewById(R.id.picture_scale)
        val close = findViewById<View>(R.id.picture_close)
        close.setOnClickListener { finish() }
        avoidSystemBars(close, findViewById(R.id.picture_bar))
        picture.setImageBitmap(gridBitmap())
        // 双击缩放是在触摸事件里更新矩阵的，抬手后再读一次矩阵即可拿到最新比例
        picture.setOnTouchListener { _, _ ->
            picture.post { renderScale() }
            false
        }
        // 首次布局之前矩阵还是单位矩阵（读出 1.0），等布局后再读才是真实的初始比例
        picture.post { renderScale() }
    }

    /** 图片层铺满屏幕，只让返回按钮和底部栏避开状态栏 / 导航栏 / 挖孔 */
    private fun avoidSystemBars(close: View, bar: View) {
        val closeMargin = close.layoutParams as ViewGroup.MarginLayoutParams
        val baseTop = closeMargin.topMargin
        val baseStart = closeMargin.marginStart
        val basePadding = bar.paddingBottom
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { _, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
            closeMargin.topMargin = baseTop + bars.top
            closeMargin.marginStart = baseStart + bars.left
            close.layoutParams = closeMargin
            bar.setPadding(bar.paddingLeft, bar.paddingTop, bar.paddingRight, basePadding + bars.bottom)
            insets
        }
    }

    private fun renderScale() {
        val values = FloatArray(9)
        picture.imageMatrix.getValues(values)
        scaleLabel.text = getString(R.string.picture_scale, values[Matrix.MSCALE_X])
    }

    /** 画一张带网格和刻度字的图，放大后细节（线条、文字）清楚，缩放效果一眼可见 */
    private fun gridBitmap(): Bitmap {
        val size = 1600
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.shader = LinearGradient(0f, 0f, size.toFloat(), size.toFloat(),
            Color.rgb(255, 122, 61), Color.rgb(54, 76, 160), Shader.TileMode.CLAMP)
        canvas.drawRect(0f, 0f, size.toFloat(), size.toFloat(), paint)
        paint.shader = null
        paint.color = Color.argb(90, 255, 255, 255)
        paint.strokeWidth = 3f
        val step = size / 16
        for (i in 0..16) {
            canvas.drawLine(i * step.toFloat(), 0f, i * step.toFloat(), size.toFloat(), paint)
            canvas.drawLine(0f, i * step.toFloat(), size.toFloat(), i * step.toFloat(), paint)
        }
        paint.style = Paint.Style.FILL
        paint.color = Color.WHITE
        paint.textSize = 34f
        for (col in 0 until 16 step 2) {
            for (row in 0 until 16 step 2) {
                canvas.drawText("${'A' + col / 2}${row / 2 + 1}", col * step + 14f, row * step + 48f, paint)
            }
        }
        paint.textSize = 120f
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("PictureView", size / 2f, size / 2f + 40f, paint)
        return bitmap
    }
}
