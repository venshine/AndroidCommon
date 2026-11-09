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
import android.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode

/**
 * 校验关键像素的计算结果（期望值按文档里的公式手算），不追求逐像素全覆盖。
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE) // 位图绘制、ColorMatrix 滤镜需要真实的 native 图形实现
class BitmapFiltersTest {

    private fun solid(w: Int, h: Int, color: Int): Bitmap =
        Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888).also { it.eraseColor(color) }

    private fun assertRgb(r: Int, g: Int, b: Int, pixel: Int, tolerance: Int = 0) {
        assertEquals("red", r.toDouble(), Color.red(pixel).toDouble(), tolerance.toDouble())
        assertEquals("green", g.toDouble(), Color.green(pixel).toDouble(), tolerance.toDouble())
        assertEquals("blue", b.toDouble(), Color.blue(pixel).toDouble(), tolerance.toDouble())
    }

    @Test
    fun greyUsesLumaWeights() {
        val out = BitmapFilters.convertGreyImg(solid(3, 3, Color.rgb(100, 200, 50)))
        // 0.3*100 + 0.59*200 + 0.11*50 = 153.5 -> 153
        assertRgb(153, 153, 153, out.getPixel(1, 1))
        assertRgb(153, 153, 153, out.getPixel(0, 0)) // 边缘像素也要处理
    }

    @Test
    fun filtersReturnNewBitmapAndKeepSource() {
        val src = solid(3, 3, Color.rgb(100, 200, 50))
        val out = BitmapFilters.convertGreyImg(src)
        assertNotSame(src, out)
        assertEquals(Color.rgb(100, 200, 50), src.getPixel(1, 1))
        assertEquals(Bitmap.Config.ARGB_8888, out.config)
    }

    @Test
    fun alphaIsPreserved() {
        val src = Bitmap.createBitmap(3, 3, Bitmap.Config.ARGB_8888)
        src.eraseColor(Color.argb(128, 100, 200, 50))
        assertEquals(128, Color.alpha(BitmapFilters.convertGreyImg(src).getPixel(1, 1)))
        assertEquals(128, Color.alpha(BitmapFilters.nostalgic(src).getPixel(1, 1)))
        assertEquals(128, Color.alpha(BitmapFilters.film(src).getPixel(0, 0)))
    }

    @Test
    fun nostalgicSepiaMatrixWithClamping() {
        val out = BitmapFilters.nostalgic(solid(3, 3, Color.rgb(100, 200, 50)))
        // R = .393*100+.769*200+.189*50 = 202.55, G = .349*100+.686*200+.168*50 = 180.5, B = .272*100+.534*200+.131*50 = 140.55
        assertRgb(202, 180, 140, out.getPixel(1, 1))
        val white = BitmapFilters.nostalgic(solid(3, 3, Color.WHITE))
        assertRgb(255, 255, 238, white.getPixel(1, 1)) // R、G 超过 255 被截断，B = 0.937*255 = 238.9
    }

    @Test
    fun filmInvertsEveryPixelIncludingBorder() {
        val out = BitmapFilters.film(solid(4, 4, Color.rgb(100, 200, 50)))
        assertRgb(155, 55, 205, out.getPixel(1, 1))
        assertRgb(155, 55, 205, out.getPixel(0, 0)) // 旧实现不处理最外一圈
        assertRgb(155, 55, 205, out.getPixel(3, 3))
    }

    private fun impulse(background: Int, center: Int): Bitmap =
        solid(5, 5, Color.rgb(background, background, background)).also { it.setPixel(2, 2, Color.rgb(center, center, center)) }

    @Test
    fun softenIsGaussianBlur() {
        val out = BitmapFilters.soften(impulse(0, 255))
        assertRgb(63, 63, 63, out.getPixel(2, 2)) // 255*4/16
        assertRgb(31, 31, 31, out.getPixel(2, 1)) // 255*2/16
        assertRgb(15, 15, 15, out.getPixel(1, 1)) // 255*1/16
        assertRgb(0, 0, 0, out.getPixel(0, 0)) // 最外一圈保持原样
        // 总亮度不变：平坦区域保持原值
        assertRgb(77, 77, 77, BitmapFilters.soften(solid(5, 5, Color.rgb(77, 77, 77))).getPixel(2, 2))
    }

    @Test
    fun adjustToneScalesBrightnessByDelta() {
        assertRgb(63, 63, 63, BitmapFilters.adjustTone(impulse(0, 255), 16)!!.getPixel(2, 2)) // 16 等同于 soften
        assertRgb(127, 127, 127, BitmapFilters.adjustTone(impulse(0, 255), 8)!!.getPixel(2, 2)) // 越小越亮
        assertRgb(255, 255, 255, BitmapFilters.adjustTone(solid(5, 5, Color.rgb(200, 200, 200)), 8)!!.getPixel(2, 2)) // 截断
        assertNull(BitmapFilters.adjustTone(impulse(0, 255), 0))
        assertNull(BitmapFilters.adjustTone(impulse(0, 255), 24))
        assertNull(BitmapFilters.adjustTone(impulse(0, 255), -3))
    }

    @Test
    fun sharpenKeepsFlatAreasAndBoostsEdges() {
        // 拉普拉斯核权重和为 1，平坦区域不变（旧实现把平坦区域压到 30% 亮度）
        assertRgb(100, 100, 100, BitmapFilters.sharpen(solid(5, 5, Color.rgb(100, 100, 100))).getPixel(2, 2))
        val out = BitmapFilters.sharpen(impulse(100, 183))
        // 中心：核结果 9*183 - 8*100 = 847 -> 截断 255，混合 183 + 0.3*(255-183) = 204.6 -> 205
        assertRgb(205, 205, 205, out.getPixel(2, 2))
        // 正上方邻居：核结果 9*100 - (7*100 + 183) = 17，混合 100 + 0.3*(17-100) = 75.1 -> 75
        assertRgb(75, 75, 75, out.getPixel(2, 1))
        assertRgb(100, 100, 100, out.getPixel(0, 0)) // 最外一圈保持原样
    }

    @Test
    fun embossIsRightNeighbourDifference() {
        val ramp = Bitmap.createBitmap(5, 4, Bitmap.Config.ARGB_8888)
        for (y in 0 until 4) for (x in 0 until 5) ramp.setPixel(x, y, Color.rgb(10 * (x + 1), 10 * (x + 1), 10 * (x + 1)))
        val out = BitmapFilters.emboss(ramp)
        assertRgb(137, 137, 137, out.getPixel(1, 1)) // 右邻 30 - 当前 20 + 127
        assertRgb(137, 137, 137, out.getPixel(2, 2)) // 其余内部像素同理
        assertRgb(10, 10, 10, out.getPixel(0, 0)) // 最外一圈保持原样
        // 突变处截断：255 -> 0 的下降沿 0-255+127 < 0 -> 0
        val edge = solid(5, 4, Color.BLACK).also { for (y in 0 until 4) it.setPixel(1, y, Color.WHITE) }
        assertRgb(0, 0, 0, BitmapFilters.emboss(edge).getPixel(1, 1))
    }

    @Test
    fun sunshineBrightensTowardCenter() {
        val out = BitmapFilters.sunshine(solid(9, 9, Color.rgb(100, 100, 100)), 4, 4) // 半径 min(4, 4) = 4
        assertRgb(250, 250, 250, out.getPixel(4, 4)) // 中心 +150
        assertRgb(175, 175, 175, out.getPixel(6, 4)) // 距离 2：150 * (1 - 2/4) = 75
        assertRgb(143, 143, 143, out.getPixel(2, 2)) // 距离 sqrt(8)：150 * (1 - 2.828/4) = 43.9 -> 43
        assertRgb(100, 100, 100, out.getPixel(1, 1)) // 距离² = 18 > 16，半径外不变
        assertRgb(100, 100, 100, out.getPixel(0, 4)) // 最外一圈不变
    }

    @Test
    fun saturationZeroGivesGrey() {
        val out = BitmapFilters.saturation(solid(3, 3, Color.rgb(200, 100, 50)), 0)
        val p = out.getPixel(1, 1)
        assertEquals(Color.red(p), Color.green(p))
        assertEquals(Color.green(p), Color.blue(p))
        assertRgb(118, 118, 118, p, 2) // 0.213*200 + 0.715*100 + 0.072*50
    }

    @Test
    fun neutralValuesKeepColors() {
        val src = solid(3, 3, Color.rgb(200, 100, 50))
        assertRgb(200, 100, 50, BitmapFilters.saturation(src, BitmapFilters.NEUTRAL).getPixel(1, 1), 1)
        assertRgb(200, 100, 50, BitmapFilters.lum(src, BitmapFilters.NEUTRAL).getPixel(1, 1), 1)
        assertRgb(200, 100, 50, BitmapFilters.hue(src, BitmapFilters.NEUTRAL).getPixel(1, 1), 1)
        assertRgb(
            200, 100, 50,
            BitmapFilters.lumAndHueAndSaturation(src, BitmapFilters.NEUTRAL, BitmapFilters.NEUTRAL, BitmapFilters.NEUTRAL)
                .getPixel(1, 1), 1
        )
    }

    @Test
    fun lumScalesBrightness() {
        val src = solid(3, 3, Color.rgb(100, 60, 40))
        assertRgb(0, 0, 0, BitmapFilters.lum(src, 0).getPixel(1, 1))
        assertRgb(200, 120, 80, BitmapFilters.lum(src, 254).getPixel(1, 1), 2) // 约 2 倍
        assertRgb(255, 240, 160, BitmapFilters.lum(solid(3, 3, Color.rgb(200, 120, 80)), 254).getPixel(1, 1), 2) // 超过 255 截断
    }

    @Test
    fun hueRotatesRedTowardGreenAndKeepsGrey() {
        // value = 127 + 84.7 -> 约 120 度：按 W3C hueRotate 矩阵，纯红变成偏绿（r、b 被截断为 0，g 约 113）
        val out = BitmapFilters.hue(solid(3, 3, Color.RED), 212).getPixel(1, 1)
        assertTrue("g 应明显大于 r 和 b：$out", Color.green(out) > 90 && Color.red(out) < 20 && Color.blue(out) < 20)
        assertRgb(128, 128, 128, BitmapFilters.hue(solid(3, 3, Color.rgb(128, 128, 128)), 255).getPixel(1, 1), 2)
        assertTrue(BitmapFilters.hue(solid(3, 3, Color.RED), 255).getPixel(1, 1) != Color.RED) // 旋转约 180 度后红色一定变化
    }

    @Test
    fun combinedAdjustmentAppliesAllThree() {
        // 旧实现用 set* 互相覆盖，实际只有最后一个生效；现在依次叠加
        val src = solid(3, 3, Color.rgb(200, 100, 50))
        val grey = BitmapFilters.lumAndHueAndSaturation(src, BitmapFilters.NEUTRAL, BitmapFilters.NEUTRAL, 0).getPixel(1, 1)
        assertEquals(Color.red(grey), Color.blue(grey)) // 饱和度 0 起了作用
        assertRgb(0, 0, 0, BitmapFilters.lumAndHueAndSaturation(src, 0, BitmapFilters.NEUTRAL, BitmapFilters.NEUTRAL).getPixel(1, 1))
    }
}
