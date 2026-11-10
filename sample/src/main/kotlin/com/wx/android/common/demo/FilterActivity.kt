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
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Shader
import android.os.Bundle
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import com.wx.android.common.util.BitmapFilters

/**
 * 图片滤镜：同一张图左边原图、右边套用 [BitmapFilters] 的结果，切换滤镜即时对比
 *
 * @author venshine
 */
class FilterActivity : BaseActivity() {

    /** 一个可选滤镜：[adjustable] 的滤镜由滑块给出强度，[apply] 的第二个参数就是滑块值 */
    private class Filter(
        @IdRes val chip: Int,
        @StringRes val name: Int,
        val code: String,
        val adjustable: Boolean = false,
        val apply: (Bitmap, Int) -> Bitmap,
    )

    private val filters = listOf(
        Filter(R.id.filter_none, R.string.filter_none, "// 原图，不做处理") { src, _ -> src },
        Filter(R.id.filter_grey, R.string.filter_grey, "BitmapFilters.convertGreyImg(bitmap)") { src, _ ->
            BitmapFilters.convertGreyImg(src)
        },
        Filter(R.id.filter_nostalgic, R.string.filter_nostalgic, "BitmapFilters.nostalgic(bitmap)") { src, _ ->
            BitmapFilters.nostalgic(src)
        },
        Filter(R.id.filter_film, R.string.filter_film, "BitmapFilters.film(bitmap)") { src, _ -> BitmapFilters.film(src) },
        Filter(R.id.filter_soften, R.string.filter_soften, "BitmapFilters.soften(bitmap)") { src, _ ->
            BitmapFilters.soften(src)
        },
        Filter(R.id.filter_sharpen, R.string.filter_sharpen, "BitmapFilters.sharpen(bitmap)") { src, _ ->
            BitmapFilters.sharpen(src)
        },
        Filter(R.id.filter_emboss, R.string.filter_emboss, "BitmapFilters.emboss(bitmap)") { src, _ ->
            BitmapFilters.emboss(src)
        },
        Filter(R.id.filter_sunshine, R.string.filter_sunshine, "BitmapFilters.sunshine(bitmap, cx, cy)") { src, _ ->
            BitmapFilters.sunshine(src, lightX, lightY)
        },
        Filter(R.id.filter_saturation, R.string.filter_saturation, "BitmapFilters.saturation(bitmap, value)", true) { src, v ->
            BitmapFilters.saturation(src, v)
        },
        Filter(R.id.filter_lum, R.string.filter_lum, "BitmapFilters.lum(bitmap, value)", true) { src, v ->
            BitmapFilters.lum(src, v)
        },
        Filter(R.id.filter_hue, R.string.filter_hue, "BitmapFilters.hue(bitmap, value)", true) { src, v ->
            BitmapFilters.hue(src, v)
        },
    )

    private lateinit var source: Bitmap
    private lateinit var result: ImageView
    private var current = filters.first()
    private var strength = BitmapFilters.NEUTRAL
    private var lightX = SIZE / 2
    private var lightY = SIZE / 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)
        setToolbar(R.string.demo_filter, "BitmapFilters")

        source = sampleBitmap()
        findViewById<ImageView>(R.id.image_original).setImageBitmap(source)
        result = findViewById(R.id.image_result)
        initSunshineTouch()

        filters.forEach { filter -> findViewById<TextView>(filter.chip).setOnClickListener { select(filter) } }
        findViewById<SeekBar>(R.id.filter_seek).setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                strength = progress
                render()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) = Unit

            override fun onStopTrackingTouch(seekBar: SeekBar) = Unit
        })
        select(filters[1])
    }

    private fun select(filter: Filter) {
        current = filter
        filters.forEach { findViewById<TextView>(it.chip).isSelected = it === filter }
        val seek = findViewById<SeekBar>(R.id.filter_seek)
        strength = BitmapFilters.NEUTRAL
        seek.progress = strength
        findViewById<android.view.View>(R.id.filter_seek_panel).visibility =
            if (filter.adjustable) android.view.View.VISIBLE else android.view.View.GONE
        findViewById<TextView>(R.id.filter_hint).setText(
            when {
                filter.adjustable -> R.string.filter_hint_seek
                filter.chip == R.id.filter_sunshine -> R.string.filter_hint_sunshine
                else -> R.string.filter_hint_plain
            }
        )
        findViewById<TextView>(R.id.filter_name).setText(filter.name)
        findViewById<TextView>(R.id.code_text).text =
            CodeHighlighter.highlight(this, filter.code, listOf("BitmapFilters"))
        render()
    }

    private fun render() {
        findViewById<TextView>(R.id.filter_seek_value).text = getString(R.string.filter_seek_value, strength)
        result.setImageBitmap(current.apply(source, strength))
    }

    /** 光照滤镜的光源跟着手指走：把触点从 ImageView 坐标换算到位图坐标 */
    @SuppressLint("ClickableViewAccessibility") // 触摸用于移动光源，不是点击
    private fun initSunshineTouch() {
        result.setOnTouchListener { view, event ->
            if (current.chip != R.id.filter_sunshine || view.width == 0 || view.height == 0) return@setOnTouchListener false
            if (event.actionMasked == MotionEvent.ACTION_DOWN || event.actionMasked == MotionEvent.ACTION_MOVE) {
                lightX = (event.x / view.width * SIZE).toInt().coerceIn(1, SIZE - 1)
                lightY = (event.y / view.height * SIZE).toInt().coerceIn(1, SIZE - 1)
                render()
            }
            true
        }
    }

    /** 画一张日落山景：渐变天空、太阳、两层山和湖面，颜色丰富，各种滤镜的效果都看得出来 */
    private fun sampleBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(SIZE, SIZE, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val s = SIZE.toFloat()
        paint.shader = LinearGradient(0f, 0f, 0f, s * 0.62f, Color.rgb(54, 76, 160), Color.rgb(255, 170, 110), Shader.TileMode.CLAMP)
        canvas.drawRect(0f, 0f, s, s * 0.62f, paint)
        paint.shader = null
        paint.color = Color.rgb(255, 232, 150)
        canvas.drawCircle(s * 0.68f, s * 0.38f, s * 0.11f, paint)
        paint.color = Color.rgb(96, 60, 120)
        canvas.drawPath(mountain(s, 0.0f, 0.34f, 0.7f, 0.62f), paint)
        paint.color = Color.rgb(46, 40, 90)
        canvas.drawPath(mountain(s, 0.4f, 0.78f, 1.2f, 0.62f), paint)
        paint.shader = LinearGradient(0f, s * 0.62f, 0f, s, Color.rgb(240, 140, 110), Color.rgb(30, 60, 120), Shader.TileMode.CLAMP)
        canvas.drawRect(0f, s * 0.62f, s, s, paint)
        paint.shader = null
        paint.color = Color.argb(160, 255, 232, 150)
        canvas.drawRect(s * 0.62f, s * 0.64f, s * 0.74f, s * 0.9f, paint)
        return bitmap
    }

    private fun mountain(s: Float, left: Float, peak: Float, right: Float, base: Float): Path = Path().apply {
        moveTo(s * left, s * base)
        lineTo(s * peak, s * (base - 0.3f))
        lineTo(s * right, s * base)
        close()
    }

    private companion object {
        const val SIZE = 192
    }
}
