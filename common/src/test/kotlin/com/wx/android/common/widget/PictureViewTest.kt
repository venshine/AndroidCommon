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
package com.wx.android.common.widget

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.ColorDrawable
import android.os.SystemClock
import android.view.MotionEvent
import android.widget.ImageView
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

/** 默认测试屏幕 320 x 470（mdpi），PictureView 以屏幕尺寸为基准缩放和居中 */
@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w320dp-h470dp-mdpi")
class PictureViewTest {

    private val context = RuntimeEnvironment.getApplication()

    private fun bitmap(w: Int, h: Int) = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888).also { it.eraseColor(Color.RED) }

    /** ImageView 布局之前 getImageMatrix 一直返回单位矩阵，所以先按屏幕尺寸布局 */
    private fun newView(): PictureView = PictureView(context).also {
        it.measure(
            android.view.View.MeasureSpec.makeMeasureSpec(320, android.view.View.MeasureSpec.EXACTLY),
            android.view.View.MeasureSpec.makeMeasureSpec(470, android.view.View.MeasureSpec.EXACTLY)
        )
        it.layout(0, 0, 320, 470)
    }

    private fun values(view: PictureView): FloatArray = FloatArray(9).also { view.imageMatrix.getValues(it) }

    private fun touch(view: PictureView, action: Int, x: Float, y: Float, time: Long) {
        val e = MotionEvent.obtain(0, time, action, x, y, 0)
        view.dispatchTouchEvent(e)
        e.recycle()
    }

    private fun doubleTap(view: PictureView, x: Float, y: Float, start: Long) {
        touch(view, MotionEvent.ACTION_DOWN, x, y, start)
        touch(view, MotionEvent.ACTION_UP, x, y, start + 50)
        touch(view, MotionEvent.ACTION_DOWN, x, y, start + 150)
        touch(view, MotionEvent.ACTION_UP, x, y, start + 200)
    }

    @Test
    fun usesMatrixScaleType() {
        assertEquals(ImageView.ScaleType.MATRIX, PictureView(context).scaleType)
    }

    @Test
    fun smallBitmapIsCenteredWithoutScaling() {
        val view = newView()
        view.setImageBitmap(bitmap(100, 100))
        val v = values(view)
        assertEquals(1f, v[Matrix.MSCALE_X], 1e-4f)
        assertEquals((320 - 100) / 2f, v[Matrix.MTRANS_X], 1e-3f)
        assertEquals((470 - 100) / 2f, v[Matrix.MTRANS_Y], 1e-3f)
    }

    @Test
    fun largeBitmapIsScaledDownToFitScreen() {
        val view = newView()
        view.setImageBitmap(bitmap(640, 940))
        val v = values(view)
        assertEquals(0.5f, v[Matrix.MSCALE_X], 1e-4f)
        assertEquals(0f, v[Matrix.MTRANS_X], 1e-3f)
        assertEquals(0f, v[Matrix.MTRANS_Y], 1e-3f)
    }

    @Test
    fun bitmapDrawableViaSetImageDrawableIsHandledToo() {
        val view = newView()
        view.setImageDrawable(android.graphics.drawable.BitmapDrawable(context.resources, bitmap(640, 940)))
        assertEquals(0.5f, values(view)[Matrix.MSCALE_X], 1e-4f)
    }

    @Test
    fun nonBitmapDrawableAndNullDoNotCrash() {
        val view = PictureView(context)
        view.setImageDrawable(ColorDrawable(Color.BLUE)) // 旧实现这里会 ClassCastException
        view.setImageDrawable(null)
        view.setImageBitmap(null)
        // 没有位图时触摸事件按普通 ImageView 处理，不崩溃
        touch(view, MotionEvent.ACTION_DOWN, 10f, 10f, SystemClock.uptimeMillis())
        touch(view, MotionEvent.ACTION_UP, 10f, 10f, SystemClock.uptimeMillis() + 10)
    }

    @Test
    fun replacingBitmapRecomputesMatrix() {
        val view = newView()
        view.setImageBitmap(bitmap(640, 940))
        view.setImageBitmap(bitmap(100, 100))
        assertEquals(1f, values(view)[Matrix.MSCALE_X], 1e-4f)
    }

    @Test
    fun doubleTapZoomsInAndOut() {
        val view = newView()
        view.setImageBitmap(bitmap(640, 940))
        val before = values(view)[Matrix.MSCALE_X]
        assertEquals(0.5f, before, 1e-4f)
        doubleTap(view, 160f, 235f, 1000)
        assertEquals(1.0f, values(view)[Matrix.MSCALE_X], 1e-4f) // 放大 2 倍
        doubleTap(view, 160f, 235f, 5000)
        assertEquals(before, values(view)[Matrix.MSCALE_X], 1e-4f) // 再次双击还原
    }

    @Test
    fun dragMovesZoomedImageWithinBounds() {
        val view = newView()
        view.setImageBitmap(bitmap(640, 940))
        doubleTap(view, 160f, 235f, 1000)
        val x0 = values(view)[Matrix.MTRANS_X]
        touch(view, MotionEvent.ACTION_DOWN, 100f, 100f, 9000)
        touch(view, MotionEvent.ACTION_MOVE, 70f, 60f, 9050)
        touch(view, MotionEvent.ACTION_UP, 70f, 60f, 9100)
        assertEquals(x0 - 30f, values(view)[Matrix.MTRANS_X], 1e-3f)
        assertNotNull(view.drawable)
        assertTrue(values(view)[Matrix.MSCALE_X] > 0f)
    }

    @Test
    fun srcAttributeSetInConstructorDoesNotCrash() {
        // ImageView 构造器里设置 src 会回调被重写的 setImageDrawable，此时子类属性还没初始化
        val attrs = org.robolectric.Robolectric.buildAttributeSet()
            .addAttribute(android.R.attr.src, "@android:drawable/ic_menu_add")
            .build()
        PictureView(context, attrs)
    }
}
