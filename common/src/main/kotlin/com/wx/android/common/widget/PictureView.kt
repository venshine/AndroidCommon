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

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.ImageView
import com.wx.android.common.util.DisplayUtils
import kotlin.math.min
import kotlin.math.sqrt

/**
 * 可双击缩放、单指拖动、双指缩放的图片控件。
 *
 * 只支持 [BitmapDrawable]（`setImageBitmap`、位图资源）；其他类型的 Drawable 按普通 ImageView 显示，不支持手势。
 * 缩放与居中以屏幕尺寸为基准，适合全屏查看图片。
 *
 * @author venshine
 */
class PictureView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ImageView(context, attrs, defStyle) {

    private var mode = NONE
    private var bitmap: Bitmap? = null
    private var minScale = 1f
    private val matrix = Matrix()
    private val savedMatrix = Matrix()
    private val originalMatrix = Matrix()
    private val prev = PointF()
    private val mid = PointF()
    private var dist = 1f
    private var screenW = 0
    private var screenH = 0
    private var gestureDetector: GestureDetector? = null

    // 父类构造器里设置 src 会先回调 setImageDrawable，此时本类属性还没初始化，用它挡住
    private var initialized = false

    init {
        screenW = DisplayUtils.getScreenWidth(context)
        screenH = DisplayUtils.getScreenHeight(context)
        scaleType = ScaleType.MATRIX
        gestureDetector = createGestureDetector()
        initialized = true
        initMatrixFrom(drawable)
    }

    private fun createGestureDetector(): GestureDetector {
        val detector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDown(e: MotionEvent): Boolean = false
        })
        detector.setOnDoubleTapListener(object : GestureDetector.OnDoubleTapListener {
            override fun onDoubleTapEvent(e: MotionEvent): Boolean = false

            override fun onSingleTapConfirmed(e: MotionEvent): Boolean = false

            override fun onDoubleTap(e: MotionEvent): Boolean {
                if (bitmap == null) return false
                mid.set(e.x, e.y)
                matrix.set(savedMatrix)
                if (isZoomChanged()) {
                    matrix.set(originalMatrix) // 已缩放：还原
                } else {
                    matrix.postScale(2f, 2f, mid.x, mid.y) // 放大
                }
                center()
                imageMatrix = matrix
                return true
            }
        })
        return detector
    }

    @SuppressLint("ClickableViewAccessibility") // 触摸用于拖动 / 缩放，不是点击
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (bitmap == null) return super.onTouchEvent(event)
        if (gestureDetector?.onTouchEvent(event) == true) return true
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                savedMatrix.set(matrix)
                prev.set(event.x, event.y)
                mode = DRAG
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                dist = spacing(event)
                if (dist > 10f) {
                    savedMatrix.set(matrix)
                    midPoint(mid, event)
                    mode = ZOOM
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> mode = NONE
            MotionEvent.ACTION_MOVE -> {
                if (mode == DRAG) {
                    matrix.set(savedMatrix)
                    matrix.postTranslate(event.x - prev.x, event.y - prev.y)
                } else if (mode == ZOOM && event.pointerCount >= 2) {
                    val newDist = spacing(event)
                    if (newDist > 10f) {
                        matrix.set(savedMatrix)
                        val scale = newDist / dist
                        matrix.postScale(scale, scale, mid.x, mid.y)
                    }
                }
            }
        }
        imageMatrix = matrix
        checkView()
        return true
    }

    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageBitmap(bm)
        if (initialized) initMatrix(bm)
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        if (initialized) initMatrixFrom(drawable)
    }

    private fun initMatrixFrom(drawable: Drawable?) {
        initMatrix((drawable as? BitmapDrawable)?.bitmap)
    }

    private fun initMatrix(bm: Bitmap?) {
        bitmap = bm
        if (bm == null) return
        matrix.reset()
        minZoom(bm)
        center()
        imageMatrix = matrix
        originalMatrix.set(matrix)
        savedMatrix.set(matrix)
    }

    private fun isZoomChanged(): Boolean {
        val values = FloatArray(9)
        imageMatrix.getValues(values)
        val scale = values[Matrix.MSCALE_X]
        originalMatrix.getValues(values)
        return scale != values[Matrix.MSCALE_X]
    }

    private fun checkView() {
        val p = FloatArray(9)
        matrix.getValues(p)
        if (mode == ZOOM) {
            if (p[Matrix.MSCALE_X] < minScale) matrix.setScale(minScale, minScale)
            if (p[Matrix.MSCALE_X] > MAX_SCALE) matrix.set(savedMatrix)
        }
        center()
    }

    private fun minZoom(bm: Bitmap) {
        minScale = min(screenW.toFloat() / bm.width, screenH.toFloat() / bm.height)
        if (minScale < 1.0f) {
            matrix.postScale(minScale, minScale)
        } else {
            minScale = 1.0f
        }
    }

    private fun center() {
        val bm = bitmap ?: return
        val m = Matrix(matrix)
        val rect = RectF(0f, 0f, bm.width.toFloat(), bm.height.toFloat())
        m.mapRect(rect)
        var deltaX = 0f
        var deltaY = 0f
        if (rect.height() < screenH) {
            deltaY = (screenH - rect.height()) / 2 - rect.top
        } else if (rect.top > 0) {
            deltaY = -rect.top
        } else if (rect.bottom < screenH) {
            deltaY = height - rect.bottom
        }
        if (rect.width() < screenW) {
            deltaX = (screenW - rect.width()) / 2 - rect.left
        } else if (rect.left > 0) {
            deltaX = -rect.left
        } else if (rect.right < screenW) {
            deltaX = screenW - rect.right
        }
        matrix.postTranslate(deltaX, deltaY)
    }

    private fun spacing(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return sqrt(x * x + y * y)
    }

    private fun midPoint(point: PointF, event: MotionEvent) {
        point.set((event.getX(0) + event.getX(1)) / 2, (event.getY(0) + event.getY(1)) / 2)
    }

    private companion object {
        const val NONE = 0 // 初始状态
        const val DRAG = 1 // 拖动
        const val ZOOM = 2 // 缩放
        const val MAX_SCALE = 4f // 最大缩放比例
    }
}
