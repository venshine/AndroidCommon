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
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PaintFlagsDrawFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * 位图工具：缩放、旋转、翻转、圆角 / 圆形、倒影、水印、合成、压缩、View 转位图、EXIF 角度等。
 *
 * 颜色滤镜（灰度、浮雕、锐化、怀旧等）见 [BitmapFilters]。
 * 所有方法都返回新的 Bitmap，不修改也不回收入参。
 *
 * @author venshine
 */
// 库不引入 androidx.exifinterface，EXIF 读取用平台自带的 android.media.ExifInterface
@SuppressLint("ExifInterface")
object BitmapUtils {

    /** [convertViewToBitmap] 中表示"不限制尺寸" */
    const val UNSPECIFIED = 0

    private const val ROUND_COLOR = 0xff424242.toInt()

    /**
     * Drawable 转 Bitmap。[BitmapDrawable] 直接取其位图，其他类型按固有尺寸绘制（无固有尺寸时为 1x1）。
     */
    @JvmStatic
    fun drawableToBitmap(drawable: Drawable?): Bitmap? {
        if (drawable == null) return null
        if (drawable is BitmapDrawable && drawable.bitmap != null) return drawable.bitmap
        val w = drawable.intrinsicWidth.takeIf { it > 0 } ?: 1
        val h = drawable.intrinsicHeight.takeIf { it > 0 } ?: 1
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, w, h)
        drawable.draw(canvas)
        return bitmap
    }

    /**
     * 把 View 绘制成 Bitmap。[width] / [height] 为 [UNSPECIFIED] 时按内容测量，否则按精确尺寸测量。
     */
    @JvmStatic
    @JvmOverloads
    fun convertViewToBitmap(view: View, width: Int = UNSPECIFIED, height: Int = UNSPECIFIED): Bitmap {
        view.measure(
            View.MeasureSpec.makeMeasureSpec(
                width, if (width == UNSPECIFIED) View.MeasureSpec.UNSPECIFIED else View.MeasureSpec.EXACTLY
            ),
            View.MeasureSpec.makeMeasureSpec(
                height, if (height == UNSPECIFIED) View.MeasureSpec.UNSPECIFIED else View.MeasureSpec.EXACTLY
            )
        )
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        val bitmap = Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
        view.draw(Canvas(bitmap))
        return bitmap
    }

    /** 按比例缩小到不超过 [w] x [h]；本身已不大于该尺寸时原样返回 */
    @JvmStatic
    fun resizeImage(src: Bitmap, w: Int, h: Int): Bitmap {
        var width = src.width
        var height = src.height
        if (width <= w && height <= h) return src
        val screenRatio = w.toFloat() / h
        val ratio = width.toFloat() / height
        if (screenRatio >= ratio) {
            width = (h * ratio).toInt()
            height = h
        } else {
            height = (w / ratio).toInt()
            width = w
        }
        return Bitmap.createScaledBitmap(src, max(width, 1), max(height, 1), true)
    }

    /**
     * 解码图片流，按屏幕尺寸计算采样率避免加载过大的图。会读取并关闭 [stream]。
     */
    @JvmStatic
    @Throws(IOException::class)
    fun decodeBitmap(stream: InputStream, context: Context): Bitmap? {
        val data = stream.use { it.readBytes() }
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeByteArray(data, 0, data.size, options)
        options.inSampleSize = calculateInSampleSize(
            options, DisplayUtils.getScreenWidth(context), DisplayUtils.getScreenHeight(context)
        )
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeByteArray(data, 0, data.size, options)
    }

    internal fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        if (options.outHeight > reqHeight || options.outWidth > reqWidth) {
            val heightRatio = (options.outHeight.toFloat() / reqHeight).roundToInt()
            val widthRatio = (options.outWidth.toFloat() / reqWidth).roundToInt()
            return max(1, min(heightRatio, widthRatio))
        }
        return 1
    }

    /** 以两张图中较大的宽高为画布，先画 [bg] 再画 [fg]（SRC_ATOP） */
    @JvmStatic
    fun combineBitmap(bg: Bitmap, fg: Bitmap): Bitmap =
        combine(bg, fg, max(bg.width, fg.width), max(bg.height, fg.height))

    /** 两张图先缩放为较小的宽高再合成 */
    @JvmStatic
    fun combineBitmapInSameSize(bg: Bitmap, fg: Bitmap): Bitmap {
        val width = min(bg.width, fg.width)
        val height = min(bg.height, fg.height)
        return combine(
            if (bg.width != width || bg.height != height) zoom(bg, width, height) else bg,
            if (fg.width != width || fg.height != height) zoom(fg, width, height) else fg,
            width, height
        )
    }

    private fun combine(bg: Bitmap, fg: Bitmap, width: Int, height: Int): Bitmap {
        val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        canvas.drawBitmap(bg, 0f, 0f, null)
        val paint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP) }
        canvas.drawBitmap(fg, 0f, 0f, paint)
        return result
    }

    /** 缩放到精确的 [w] x [h]（可能改变宽高比） */
    @JvmStatic
    fun zoom(src: Bitmap, w: Int, h: Int): Bitmap = scale(src, w.toFloat() / src.width, h.toFloat() / src.height)

    /** 按 [scaleX]、[scaleY] 倍数缩放 */
    @JvmStatic
    fun scale(src: Bitmap, scaleX: Float, scaleY: Float): Bitmap =
        scale(src, Matrix().apply { postScale(scaleX, scaleY) })

    /** 等比缩放 [scale] 倍 */
    @JvmStatic
    fun scale(src: Bitmap, scale: Float): Bitmap = scale(src, scale, scale)

    /** 按矩阵变换 */
    @JvmStatic
    fun scale(src: Bitmap, matrix: Matrix): Bitmap =
        Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true)

    /** 顺时针旋转 [angle] 度 */
    @JvmStatic
    fun rotate(src: Bitmap, angle: Int): Bitmap =
        Bitmap.createBitmap(src, 0, 0, src.width, src.height, Matrix().apply { postRotate(angle.toFloat()) }, true)

    /** 水平翻转 */
    @JvmStatic
    fun reverseHorizontal(src: Bitmap): Bitmap =
        Bitmap.createBitmap(src, 0, 0, src.width, src.height, Matrix().apply { preScale(-1f, 1f) }, false)

    /** 垂直翻转 */
    @JvmStatic
    fun reverseVertical(src: Bitmap): Bitmap =
        Bitmap.createBitmap(src, 0, 0, src.width, src.height, Matrix().apply { preScale(1f, -1f) }, false)

    /** 圆角位图，[roundPx] 为圆角半径 */
    @JvmStatic
    fun createRoundedCornerBitmap(src: Bitmap, roundPx: Float): Bitmap {
        val output = Bitmap.createBitmap(src.width, src.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val rect = Rect(0, 0, src.width, src.height)
        val paint = Paint().apply {
            isAntiAlias = true
            color = ROUND_COLOR
        }
        canvas.drawRoundRect(RectF(rect), roundPx, roundPx, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(src, rect, rect, paint)
        return output
    }

    /** 居中裁成正方形后再裁成圆形，边长取宽高较小值 */
    @JvmStatic
    fun createRoundBitmap(src: Bitmap): Bitmap {
        val size = min(src.width, src.height)
        val srcRect = Rect((src.width - size) / 2, (src.height - size) / 2, (src.width + size) / 2, (src.height + size) / 2)
        val dstRect = Rect(0, 0, size, size)
        val output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint().apply {
            isAntiAlias = true
            color = ROUND_COLOR
        }
        canvas.drawRoundRect(RectF(dstRect), size / 2f, size / 2f, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(src, srcRect, dstRect, paint)
        return output
    }

    /** 带倒影的位图：下方追加一半高度的镜像，并渐隐 */
    @JvmStatic
    fun createReflectionBitmap(src: Bitmap): Bitmap {
        val gap = 4
        val width = src.width
        val height = src.height
        val reflection = Bitmap.createBitmap(
            src, 0, height / 2, width, height - height / 2, Matrix().apply { preScale(1f, -1f) }, false
        )
        val result = Bitmap.createBitmap(width, height + height / 2, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        canvas.drawBitmap(src, 0f, 0f, null)
        canvas.drawRect(0f, height.toFloat(), width.toFloat(), (height + gap).toFloat(), Paint())
        canvas.drawBitmap(reflection, 0f, (height + gap).toFloat(), null)
        val paint = Paint().apply {
            shader = LinearGradient(
                0f, height.toFloat(), 0f, (result.height + gap).toFloat(),
                0x70ffffff, 0x00ffffff, Shader.TileMode.CLAMP
            )
            xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        }
        canvas.drawRect(0f, height.toFloat(), width.toFloat(), (result.height + gap).toFloat(), paint)
        return result
    }

    /**
     * 缩略图：边长为系统应用图标尺寸（`android.R.dimen.app_icon_size`）的正方形画布，
     * 图比图标大则等比缩小并居中，比图标小则原尺寸居中；尺寸恰好相等时原样返回。
     */
    @JvmStatic
    fun createThumbnailBitmap(src: Bitmap, context: Context): Bitmap {
        val icon = context.resources.getDimension(android.R.dimen.app_icon_size).toInt()
        if (icon <= 0 || (src.width == icon && src.height == icon)) return src
        val thumb = Bitmap.createBitmap(icon, icon, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(thumb)
        canvas.drawFilter = PaintFlagsDrawFilter(Paint.DITHER_FLAG, Paint.FILTER_BITMAP_FLAG)
        val paint = Paint().apply { isFilterBitmap = true }
        if (src.width > icon || src.height > icon) {
            val ratio = min(icon.toFloat() / src.width, icon.toFloat() / src.height)
            val w = max((src.width * ratio).roundToInt(), 1)
            val h = max((src.height * ratio).roundToInt(), 1)
            val left = (icon - w) / 2
            val top = (icon - h) / 2
            canvas.drawBitmap(src, Rect(0, 0, src.width, src.height), Rect(left, top, left + w, top + h), paint)
        } else {
            canvas.drawBitmap(src, ((icon - src.width) / 2).toFloat(), ((icon - src.height) / 2).toFloat(), paint)
        }
        return thumb
    }

    /** 在右下角叠加水印，[padding] 为距右、下边缘的像素 */
    @JvmStatic
    @JvmOverloads
    fun createWatermarkBitmap(src: Bitmap, watermark: Bitmap, padding: Int = 0): Bitmap {
        val result = Bitmap.createBitmap(src.width, src.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        canvas.drawBitmap(src, 0f, 0f, null)
        canvas.drawBitmap(
            watermark, (src.width - watermark.width - padding).toFloat(),
            (src.height - watermark.height - padding).toFloat(), null
        )
        return result
    }

    /**
     * 质量压缩：以 JPEG 编码，从质量 100 起每次降 10，直到不超过 [maxKb] KB 或质量降到 10，
     * 返回重新解码的位图。
     */
    @JvmStatic
    @JvmOverloads
    fun compressBitmap(src: Bitmap, maxKb: Int = 100): Bitmap? {
        val out = ByteArrayOutputStream()
        var quality = 100
        src.compress(Bitmap.CompressFormat.JPEG, quality, out)
        while (out.size() / 1024 > maxKb && quality > 10) {
            quality -= 10
            out.reset()
            src.compress(Bitmap.CompressFormat.JPEG, quality, out)
        }
        val bytes = out.toByteArray()
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    /**
     * 读取图片文件的 EXIF 旋转角度（0 / 90 / 180 / 270），读取失败返回 0。
     */
    @JvmStatic
    @Suppress("DEPRECATION")
    fun getImageDegree(path: String): Int = try {
        degreeOf(android.media.ExifInterface(path).getAttributeInt(
            android.media.ExifInterface.TAG_ORIENTATION, android.media.ExifInterface.ORIENTATION_NORMAL
        ))
    } catch (e: IOException) {
        0
    }

    /** 从图片流读取 EXIF 旋转角度；API 24 以下不支持流，返回 0。不会关闭 [stream] */
    @JvmStatic
    fun getImageDegree(stream: InputStream): Int {
        if (Build.VERSION.SDK_INT < 24) return 0
        return try {
            degreeOf(android.media.ExifInterface(stream).getAttributeInt(
                android.media.ExifInterface.TAG_ORIENTATION, android.media.ExifInterface.ORIENTATION_NORMAL
            ))
        } catch (e: IOException) {
            0
        }
    }

    private fun degreeOf(orientation: Int): Int = when (orientation) {
        android.media.ExifInterface.ORIENTATION_ROTATE_90 -> 90
        android.media.ExifInterface.ORIENTATION_ROTATE_180 -> 180
        android.media.ExifInterface.ORIENTATION_ROTATE_270 -> 270
        else -> 0
    }
}
