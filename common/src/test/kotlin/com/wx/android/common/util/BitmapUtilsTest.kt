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
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.media.ExifInterface
import android.view.View
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode
import org.robolectric.RuntimeEnvironment
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.Random

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE) // 位图绘制、ColorMatrix 滤镜需要真实的 native 图形实现
class BitmapUtilsTest {

    private val context: Context = RuntimeEnvironment.getApplication()

    @get:Rule
    val tmp = TemporaryFolder()

    private fun solid(w: Int, h: Int, color: Int): Bitmap =
        Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888).also { it.eraseColor(color) }

    private fun png(bitmap: Bitmap): ByteArray =
        ByteArrayOutputStream().also { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }.toByteArray()

    @Test
    fun drawableToBitmap() {
        assertNull(BitmapUtils.drawableToBitmap(null))
        val bmp = solid(4, 4, Color.RED)
        assertSame(bmp, BitmapUtils.drawableToBitmap(BitmapDrawable(context.resources, bmp)))
    }

    @Test
    fun drawableToBitmapDrawsNonBitmapDrawables() {
        val shape = ShapeDrawable(RectShape()).apply {
            intrinsicWidth = 20
            intrinsicHeight = 10
            paint.color = Color.RED
        }
        val bmp = BitmapUtils.drawableToBitmap(shape)!!
        assertEquals(20, bmp.width)
        assertEquals(10, bmp.height)
        assertEquals(Color.RED, bmp.getPixel(10, 5))
        // 没有固有尺寸的 Drawable（如 ColorDrawable）退化为 1x1
        val color = BitmapUtils.drawableToBitmap(ColorDrawable(Color.BLUE))!!
        assertEquals(1, color.width)
        assertEquals(Color.BLUE, color.getPixel(0, 0))
    }

    @Test
    fun resizeImageKeepsAspectRatioAndNeverEnlarges() {
        val wide = solid(200, 100, Color.RED)
        BitmapUtils.resizeImage(wide, 100, 100).let { assertEquals(100 to 50, it.width to it.height) }
        BitmapUtils.resizeImage(solid(100, 200, Color.RED), 100, 100).let { assertEquals(50 to 100, it.width to it.height) }
        assertSame(wide, BitmapUtils.resizeImage(wide, 300, 300)) // 已经够小，原样返回
        assertSame(wide, BitmapUtils.resizeImage(wide, 200, 100))
    }

    @Test
    fun zoomAndScale() {
        val src = solid(20, 20, Color.RED)
        BitmapUtils.zoom(src, 10, 40).let { assertEquals(10 to 40, it.width to it.height) }
        BitmapUtils.scale(src, 2f).let { assertEquals(40 to 40, it.width to it.height) }
        BitmapUtils.scale(src, 0.5f, 2f).let { assertEquals(10 to 40, it.width to it.height) }
        BitmapUtils.scale(src, android.graphics.Matrix().apply { postScale(1.5f, 1f) }).let {
            assertEquals(30 to 20, it.width to it.height)
        }
    }

    @Test
    fun rotateNinetyDegreesMovesTopLeftToTopRight() {
        val src = solid(20, 10, Color.WHITE).also { it.setPixel(0, 0, Color.RED) }
        val r = BitmapUtils.rotate(src, 90)
        assertEquals(10 to 20, r.width to r.height)
        assertEquals(Color.RED, r.getPixel(9, 0)) // 顺时针 90 度：左上角 -> 右上角
        assertEquals(Color.WHITE, r.getPixel(0, 0))
    }

    @Test
    fun flips() {
        val src = solid(20, 10, Color.WHITE).also { it.setPixel(0, 0, Color.RED) }
        val h = BitmapUtils.reverseHorizontal(src)
        assertEquals(Color.RED, h.getPixel(19, 0))
        assertEquals(Color.WHITE, h.getPixel(0, 0))
        val v = BitmapUtils.reverseVertical(src)
        assertEquals(Color.RED, v.getPixel(0, 9))
        assertEquals(Color.WHITE, v.getPixel(0, 0))
        assertEquals(20 to 10, v.width to v.height)
    }

    @Test
    fun roundedCornerClearsCornersKeepsCenter() {
        val out = BitmapUtils.createRoundedCornerBitmap(solid(40, 40, Color.GREEN), 15f)
        assertEquals(40 to 40, out.width to out.height)
        assertEquals(0, Color.alpha(out.getPixel(0, 0)))
        assertEquals(0, Color.alpha(out.getPixel(39, 39)))
        assertEquals(Color.GREEN, out.getPixel(20, 20))
        assertEquals(Color.GREEN, out.getPixel(20, 0)) // 边的中点不受圆角影响
    }

    @Test
    fun roundBitmapCropsCenterSquareIntoCircle() {
        val src = solid(30, 20, Color.GREEN).also {
            for (y in 0 until 20) {
                for (x in 0 until 5) it.setPixel(x, y, Color.RED) // 左右各 5 像素是会被裁掉的红色
                for (x in 25 until 30) it.setPixel(x, y, Color.RED)
            }
        }
        val out = BitmapUtils.createRoundBitmap(src)
        assertEquals(20 to 20, out.width to out.height) // 边长取较小边
        assertEquals(Color.GREEN, out.getPixel(10, 10))
        assertEquals(0, Color.alpha(out.getPixel(0, 0)))
        assertEquals(0, Color.alpha(out.getPixel(19, 19)))
        for (y in 0 until 20) for (x in 0 until 20) {
            if (Color.alpha(out.getPixel(x, y)) == 255) assertEquals(Color.GREEN, out.getPixel(x, y)) // 红边已被裁掉
        }
    }

    @Test
    fun reflectionAddsHalfHeightAndFades() {
        val src = solid(20, 40, Color.RED)
        val out = BitmapUtils.createReflectionBitmap(src)
        assertEquals(20 to 60, out.width to out.height)
        assertEquals(Color.RED, out.getPixel(10, 10)) // 原图部分不变
        val nearGap = Color.alpha(out.getPixel(10, 45))
        val bottom = Color.alpha(out.getPixel(10, 59))
        assertTrue("倒影应半透明，实际 alpha=$nearGap", nearGap in 1..0x80)
        assertTrue("倒影应向下渐隐", bottom < nearGap)
    }

    @Test
    fun thumbnailScalesDownCentersAndKeepsExactSize() {
        val icon = context.resources.getDimension(android.R.dimen.app_icon_size).toInt()
        val big = BitmapUtils.createThumbnailBitmap(solid(icon * 2, icon, Color.RED), context)
        assertEquals(icon to icon, big.width to big.height)
        assertEquals(Color.RED, big.getPixel(icon / 2, icon / 2))
        assertEquals(0, Color.alpha(big.getPixel(icon / 2, 1))) // 等比缩小后上下留白

        val small = BitmapUtils.createThumbnailBitmap(solid(icon / 2, icon / 2, Color.BLUE), context)
        assertEquals(icon to icon, small.width to small.height)
        assertEquals(Color.BLUE, small.getPixel(icon / 2, icon / 2))
        assertEquals(0, Color.alpha(small.getPixel(0, 0)))

        val exact = solid(icon, icon, Color.GREEN)
        assertSame(exact, BitmapUtils.createThumbnailBitmap(exact, context))
    }

    @Test
    fun watermarkIsInsideBottomRightCorner() {
        val src = solid(20, 20, Color.WHITE)
        val mark = solid(4, 4, Color.BLUE)
        val flush = BitmapUtils.createWatermarkBitmap(src, mark)
        assertEquals(Color.BLUE, flush.getPixel(19, 19)) // 旧实现偏出边缘 5 像素，右下角看不到完整水印
        assertEquals(Color.BLUE, flush.getPixel(16, 16))
        assertEquals(Color.WHITE, flush.getPixel(15, 15))
        val padded = BitmapUtils.createWatermarkBitmap(src, mark, 2)
        assertEquals(Color.WHITE, padded.getPixel(19, 19))
        assertEquals(Color.BLUE, padded.getPixel(17, 17))
        assertEquals(Color.BLUE, padded.getPixel(14, 14))
    }

    @Test
    fun combineUsesLargerCanvasAndSrcAtop() {
        val out = BitmapUtils.combineBitmap(solid(10, 10, Color.RED), solid(20, 5, Color.BLUE))
        assertEquals(20 to 10, out.width to out.height)
        assertEquals(Color.BLUE, out.getPixel(0, 0)) // 前景盖在有背景的位置
        assertEquals(Color.RED, out.getPixel(0, 8)) // 前景没覆盖到
        assertEquals(0, Color.alpha(out.getPixel(15, 0))) // SRC_ATOP：背景为空的位置保持透明
    }

    @Test
    fun combineInSameSizeScalesToSmaller() {
        val out = BitmapUtils.combineBitmapInSameSize(solid(10, 10, Color.RED), solid(20, 20, Color.BLUE))
        assertEquals(10 to 10, out.width to out.height)
        assertEquals(Color.BLUE, out.getPixel(5, 5))
        assertEquals(Color.BLUE, out.getPixel(0, 0))
    }

    @Test
    fun compressTerminatesEvenWhenTargetUnreachable() {
        val rnd = Random(1)
        val noise = Bitmap.createBitmap(120, 120, Bitmap.Config.ARGB_8888)
        for (y in 0 until 120) for (x in 0 until 120) noise.setPixel(x, y, Color.rgb(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)))
        val unreachable = BitmapUtils.compressBitmap(noise, 0) // 旧实现里质量会降到负数而抛异常
        assertNotNull(unreachable)
        assertEquals(120 to 120, unreachable!!.width to unreachable.height)
        assertNotNull(BitmapUtils.compressBitmap(noise)) // 默认 100KB
    }

    @Test
    fun convertViewToBitmapExactAndWrap() {
        val view = View(context).apply {
            setBackgroundColor(Color.RED)
            minimumWidth = 15
            minimumHeight = 10
        }
        val exact = BitmapUtils.convertViewToBitmap(view, 30, 20)
        assertEquals(30 to 20, exact.width to exact.height)
        assertEquals(Color.RED, exact.getPixel(10, 10))
        val wrap = BitmapUtils.convertViewToBitmap(view)
        assertEquals(15 to 10, wrap.width to wrap.height)
    }

    @Test
    fun decodeBitmapKeepsSmallImages() {
        val bmp = BitmapUtils.decodeBitmap(ByteArrayInputStream(png(solid(100, 80, Color.RED))), context)!!
        assertEquals(100 to 80, bmp.width to bmp.height)
    }

    @Test
    fun decodeBitmapSamplesDownLargeImages() {
        val dm = context.resources.displayMetrics
        val bmp = BitmapUtils.decodeBitmap(
            ByteArrayInputStream(png(solid(dm.widthPixels * 3, dm.heightPixels * 3, Color.RED))), context
        )!!
        assertTrue(bmp.width < dm.widthPixels * 3) // 按屏幕尺寸降采样
    }

    @Test
    fun sampleSizeCalculation() {
        fun options(w: Int, h: Int) = android.graphics.BitmapFactory.Options().also { it.outWidth = w; it.outHeight = h }
        assertEquals(1, BitmapUtils.calculateInSampleSize(options(100, 100), 320, 470))
        assertEquals(1, BitmapUtils.calculateInSampleSize(options(320, 470), 320, 470))
        assertEquals(2, BitmapUtils.calculateInSampleSize(options(640, 940), 320, 470))
        assertEquals(3, BitmapUtils.calculateInSampleSize(options(960, 1410), 320, 470))
        // 一边超标另一边很小时，旧实现会算出 0；现在至少为 1
        assertEquals(1, BitmapUtils.calculateInSampleSize(options(100, 2000), 320, 470))
    }

    private fun jpegWithOrientation(orientation: Int?): File {
        val file = tmp.newFile()
        file.outputStream().use { solid(8, 8, Color.RED).compress(Bitmap.CompressFormat.JPEG, 90, it) }
        if (orientation != null) {
            @Suppress("DEPRECATION")
            ExifInterface(file.path).apply {
                setAttribute(ExifInterface.TAG_ORIENTATION, orientation.toString())
                saveAttributes()
            }
        }
        return file
    }

    @Test
    fun imageDegreeFromPathAndStream() {
        val cases = mapOf(
            ExifInterface.ORIENTATION_NORMAL to 0,
            ExifInterface.ORIENTATION_ROTATE_90 to 90,
            ExifInterface.ORIENTATION_ROTATE_180 to 180,
            ExifInterface.ORIENTATION_ROTATE_270 to 270
        )
        for ((orientation, degree) in cases) {
            val file = jpegWithOrientation(orientation)
            assertEquals("path $orientation", degree, BitmapUtils.getImageDegree(file.path))
            file.inputStream().use { assertEquals("stream $orientation", degree, BitmapUtils.getImageDegree(it)) }
        }
    }

    @Test
    fun imageDegreeWithoutExifOrMissingFileIsZero() {
        assertEquals(0, BitmapUtils.getImageDegree(jpegWithOrientation(null).path))
        assertEquals(0, BitmapUtils.getImageDegree(File(tmp.root, "missing.jpg").path))
    }
}
