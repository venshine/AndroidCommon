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

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * 位图颜色滤镜与卷积效果。所有方法返回新的 ARGB_8888 位图，不修改入参，并保留原像素的 alpha。
 *
 * 卷积类效果（柔化、锐化、浮雕等）不处理最外一圈像素，该圈保持原样。
 * 这些都是 CPU 逐像素计算，大图请放到后台线程。
 *
 * @author venshine
 */
object BitmapFilters {

    /** 亮度 / 饱和度 / 色相调节的中间值：127 表示保持不变 */
    const val NEUTRAL = 127

    /** 灰度化（0.3R + 0.59G + 0.11B） */
    @JvmStatic
    fun convertGreyImg(src: Bitmap): Bitmap = mapPixels(src) { r, g, b ->
        val grey = (r * 0.3 + g * 0.59 + b * 0.11).toInt()
        intArrayOf(grey, grey, grey)
    }

    /** 怀旧（棕褐色调） */
    @JvmStatic
    fun nostalgic(src: Bitmap): Bitmap = mapPixels(src) { r, g, b ->
        intArrayOf(
            clamp((0.393 * r + 0.769 * g + 0.189 * b).toInt()),
            clamp((0.349 * r + 0.686 * g + 0.168 * b).toInt()),
            clamp((0.272 * r + 0.534 * g + 0.131 * b).toInt())
        )
    }

    /** 底片（反色） */
    @JvmStatic
    fun film(src: Bitmap): Bitmap = mapPixels(src) { r, g, b -> intArrayOf(255 - r, 255 - g, 255 - b) }

    /**
     * 高斯式色调调节：3x3 高斯核按 [delta] 归一化，核权重和为 16，所以 16 表示仅模糊不变亮，
     * 小于 16 变亮、大于 16 变暗。[delta] 必须在 1..23，否则返回 null。
     */
    @JvmStatic
    fun adjustTone(src: Bitmap, delta: Int): Bitmap? {
        if (delta >= 24 || delta <= 0) return null
        return convolve(src, GAUSS, delta, 0f)
    }

    /** 柔化（3x3 高斯模糊） */
    @JvmStatic
    fun soften(src: Bitmap): Bitmap = convolve(src, GAUSS, 16, 0f)

    /**
     * 锐化：用 3x3 拉普拉斯核（权重和为 1，平坦区域不变）算出锐化结果，再与原图按 0.3 的强度混合，
     * 即 `原值 + 0.3 * (锐化值 - 原值)`。
     */
    @JvmStatic
    fun sharpen(src: Bitmap): Bitmap {
        val w = src.width
        val h = src.height
        val original = IntArray(w * h)
        src.getPixels(original, 0, w, 0, 0, w, h)
        val sharpened = IntArray(w * h)
        convolve(src, LAPLACIAN, 1, 1f).getPixels(sharpened, 0, w, 0, 0, w, h)
        val strength = 0.3f
        for (i in original.indices) {
            val o = original[i]
            val s = sharpened[i]
            original[i] = Color.argb(
                Color.alpha(o),
                blend(Color.red(o), Color.red(s), strength),
                blend(Color.green(o), Color.green(s), strength),
                blend(Color.blue(o), Color.blue(s), strength)
            )
        }
        return fromPixels(original, w, h)
    }

    private fun blend(from: Int, to: Int, t: Float): Int = clamp((from + t * (to - from)).roundToInt())

    /** 浮雕：右邻像素减当前像素再加 127 */
    @JvmStatic
    fun emboss(src: Bitmap): Bitmap {
        val w = src.width
        val h = src.height
        val pixels = IntArray(w * h)
        src.getPixels(pixels, 0, w, 0, 0, w, h)
        val out = pixels.copyOf()
        for (i in 1 until h - 1) {
            for (k in 1 until w - 1) {
                val pos = i * w + k
                val c = pixels[pos]
                val n = pixels[pos + 1]
                out[pos] = Color.argb(
                    Color.alpha(c),
                    clamp(Color.red(n) - Color.red(c) + 127),
                    clamp(Color.green(n) - Color.green(c) + 127),
                    clamp(Color.blue(n) - Color.blue(c) + 127)
                )
            }
        }
        return fromPixels(out, w, h)
    }

    /**
     * 光照：以 ([centerX], [centerY]) 为中心提亮，半径取 `min(centerX, centerY)`，
     * 中心增亮 150，向半径边缘线性衰减到 0。
     */
    @JvmStatic
    fun sunshine(src: Bitmap, centerX: Int, centerY: Int): Bitmap {
        val w = src.width
        val h = src.height
        val pixels = IntArray(w * h)
        src.getPixels(pixels, 0, w, 0, 0, w, h)
        val radius = minOf(centerX, centerY)
        val strength = 150f
        for (i in 1 until h - 1) {
            for (k in 1 until w - 1) {
                val pos = i * w + k
                val c = pixels[pos]
                val dy = (centerY - i).toDouble()
                val dx = (centerX - k).toDouble()
                val distance = dy * dy + dx * dx
                if (distance < radius.toDouble() * radius) {
                    val add = (strength * (1.0 - sqrt(distance) / radius)).toInt()
                    pixels[pos] = Color.argb(
                        Color.alpha(c),
                        clamp(Color.red(c) + add), clamp(Color.green(c) + add), clamp(Color.blue(c) + add)
                    )
                }
            }
        }
        return fromPixels(pixels, w, h)
    }

    /** 饱和度，[value] 为 0..255，[NEUTRAL] 不变，0 为灰度 */
    @JvmStatic
    fun saturation(src: Bitmap, value: Int): Bitmap = applyMatrix(src, saturationMatrix(value))

    /** 亮度，[value] 为 0..255，[NEUTRAL] 不变，0 为全黑 */
    @JvmStatic
    fun lum(src: Bitmap, value: Int): Bitmap = applyMatrix(src, lumMatrix(value))

    /** 色相，[value] 为 0..255，[NEUTRAL] 不变，两端各旋转 180 度（亮度保持，灰色不变） */
    @JvmStatic
    fun hue(src: Bitmap, value: Int): Bitmap = applyMatrix(src, hueMatrix(value))

    /** 同时调节亮度、色相、饱和度（三个矩阵依次叠加） */
    @JvmStatic
    fun lumAndHueAndSaturation(src: Bitmap, lumValue: Int, hueValue: Int, saturationValue: Int): Bitmap {
        val matrix = ColorMatrix()
        matrix.postConcat(saturationMatrix(saturationValue))
        matrix.postConcat(lumMatrix(lumValue))
        matrix.postConcat(hueMatrix(hueValue))
        return applyMatrix(src, matrix)
    }

    private fun saturationMatrix(value: Int) = ColorMatrix().apply { setSaturation(value.toFloat() / NEUTRAL) }

    private fun lumMatrix(value: Int): ColorMatrix {
        val lum = value.toFloat() / NEUTRAL
        return ColorMatrix().apply { setScale(lum, lum, lum, 1f) }
    }

    /**
     * 色相旋转矩阵，系数取自 W3C Filter Effects 的 feColorMatrix `hueRotate`
     * （https://www.w3.org/TR/filter-effects-1/ ，Filter Effects Module Level 1，feColorMatrix 一节）：
     * `M = A + cos(θ) * B + sin(θ) * C`。灰色在任何角度下都保持不变。
     */
    private fun hueMatrix(value: Int): ColorMatrix {
        val radians = Math.toRadians(((value - NEUTRAL).toFloat() / NEUTRAL * 180).toDouble())
        val cos = cos(radians).toFloat()
        val sin = sin(radians).toFloat()
        return ColorMatrix(
            floatArrayOf(
                0.213f + cos * 0.787f - sin * 0.213f, 0.715f - cos * 0.715f - sin * 0.715f, 0.072f - cos * 0.072f + sin * 0.928f, 0f, 0f,
                0.213f - cos * 0.213f + sin * 0.143f, 0.715f + cos * 0.285f + sin * 0.140f, 0.072f - cos * 0.072f - sin * 0.283f, 0f, 0f,
                0.213f - cos * 0.213f - sin * 0.787f, 0.715f - cos * 0.715f + sin * 0.715f, 0.072f + cos * 0.928f + sin * 0.072f, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            )
        )
    }

    private fun applyMatrix(src: Bitmap, matrix: ColorMatrix): Bitmap {
        val out = Bitmap.createBitmap(src.width, src.height, Bitmap.Config.ARGB_8888)
        val paint = Paint().apply { colorFilter = ColorMatrixColorFilter(matrix) }
        Canvas(out).drawBitmap(src, 0f, 0f, paint)
        return out
    }

    private inline fun mapPixels(src: Bitmap, transform: (Int, Int, Int) -> IntArray): Bitmap {
        val w = src.width
        val h = src.height
        val pixels = IntArray(w * h)
        src.getPixels(pixels, 0, w, 0, 0, w, h)
        for (i in pixels.indices) {
            val c = pixels[i]
            val rgb = transform(Color.red(c), Color.green(c), Color.blue(c))
            pixels[i] = Color.argb(Color.alpha(c), clamp(rgb[0]), clamp(rgb[1]), clamp(rgb[2]))
        }
        return fromPixels(pixels, w, h)
    }

    /** 3x3 卷积：每个通道 = clamp(sum(核 * 像素) * scale / divisor)，最外一圈保持原样 */
    private fun convolve(src: Bitmap, kernel: IntArray, divisor: Int, scale: Float): Bitmap {
        val w = src.width
        val h = src.height
        val pixels = IntArray(w * h)
        src.getPixels(pixels, 0, w, 0, 0, w, h)
        val out = pixels.copyOf()
        for (i in 1 until h - 1) {
            for (k in 1 until w - 1) {
                var r = 0f
                var g = 0f
                var b = 0f
                var idx = 0
                for (m in -1..1) {
                    for (n in -1..1) {
                        val c = pixels[(i + m) * w + k + n]
                        val weight = kernel[idx++] * (if (scale == 0f) 1f else scale)
                        r += Color.red(c) * weight
                        g += Color.green(c) * weight
                        b += Color.blue(c) * weight
                    }
                }
                out[i * w + k] = Color.argb(
                    Color.alpha(pixels[i * w + k]),
                    clamp((r / divisor).toInt()), clamp((g / divisor).toInt()), clamp((b / divisor).toInt())
                )
            }
        }
        return fromPixels(out, w, h)
    }

    private fun fromPixels(pixels: IntArray, w: Int, h: Int): Bitmap {
        val out = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        out.setPixels(pixels, 0, w, 0, 0, w, h)
        return out
    }

    private fun clamp(v: Int): Int = if (v < 0) 0 else if (v > 255) 255 else v

    private val GAUSS = intArrayOf(1, 2, 1, 2, 4, 2, 1, 2, 1)
    private val LAPLACIAN = intArrayOf(-1, -1, -1, -1, 9, -1, -1, -1, -1)
}
