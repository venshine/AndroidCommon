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

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.CycleInterpolator

/**
 * 常用 View 属性动画。所有方法返回已启动的 [ObjectAnimator]，需要时可调用 `cancel()`。
 *
 * @author venshine
 */
object AnimatorUtils {

    const val ALPHA_MIN = 0.0f
    const val ALPHA_MAX = 1.0f

    private const val DEFAULT_DURATION = 300

    /** 透明度动画 */
    @JvmStatic
    @JvmOverloads
    fun alpha(
        v: View, fromAlpha: Float, toAlpha: Float, duration: Int = DEFAULT_DURATION,
        listener: Animator.AnimatorListener? = null
    ): ObjectAnimator = start(v, View.ALPHA, fromAlpha, toAlpha, duration, listener)

    /** X 轴平移动画 */
    @JvmStatic
    @JvmOverloads
    fun translationX(
        v: View, fromX: Float, toX: Float, duration: Int = DEFAULT_DURATION,
        listener: Animator.AnimatorListener? = null
    ): ObjectAnimator = start(v, View.TRANSLATION_X, fromX, toX, duration, listener)

    /** Y 轴平移动画 */
    @JvmStatic
    @JvmOverloads
    fun translationY(
        v: View, fromY: Float, toY: Float, duration: Int = DEFAULT_DURATION,
        listener: Animator.AnimatorListener? = null
    ): ObjectAnimator = start(v, View.TRANSLATION_Y, fromY, toY, duration, listener)

    /** X 轴旋转动画 */
    @JvmStatic
    @JvmOverloads
    fun rotationX(
        v: View, fromX: Float, toX: Float, duration: Int = DEFAULT_DURATION,
        listener: Animator.AnimatorListener? = null
    ): ObjectAnimator = start(v, View.ROTATION_X, fromX, toX, duration, listener)

    /** Y 轴旋转动画 */
    @JvmStatic
    @JvmOverloads
    fun rotationY(
        v: View, fromY: Float, toY: Float, duration: Int = DEFAULT_DURATION,
        listener: Animator.AnimatorListener? = null
    ): ObjectAnimator = start(v, View.ROTATION_Y, fromY, toY, duration, listener)

    /** X 轴缩放动画 */
    @JvmStatic
    @JvmOverloads
    fun scaleX(
        v: View, fromX: Float, toX: Float, duration: Int = DEFAULT_DURATION,
        listener: Animator.AnimatorListener? = null
    ): ObjectAnimator = start(v, View.SCALE_X, fromX, toX, duration, listener)

    /** Y 轴缩放动画 */
    @JvmStatic
    @JvmOverloads
    fun scaleY(
        v: View, fromY: Float, toY: Float, duration: Int = DEFAULT_DURATION,
        listener: Animator.AnimatorListener? = null
    ): ObjectAnimator = start(v, View.SCALE_Y, fromY, toY, duration, listener)

    /** 水平抖动：在 0 到 [offset] 之间往复 [times] 次 */
    @JvmStatic
    @JvmOverloads
    fun shakeX(v: View, offset: Float = 10f, duration: Long = 1000, times: Float = 5.0f): ObjectAnimator =
        shake(v, View.TRANSLATION_X, offset, duration, times)

    /** 垂直抖动：在 0 到 [offset] 之间往复 [times] 次 */
    @JvmStatic
    @JvmOverloads
    fun shakeY(v: View, offset: Float = 10f, duration: Long = 1000, times: Float = 5.0f): ObjectAnimator =
        shake(v, View.TRANSLATION_Y, offset, duration, times)

    /** 呼吸效果：透明度在 [fromRange] 与 [toRange] 间无限往复，调用返回值的 `cancel()` 停止 */
    @JvmStatic
    @JvmOverloads
    fun breath(v: View, fromRange: Float = 0.0f, toRange: Float = 1.0f, duration: Long = 1000): ObjectAnimator {
        val animator = ObjectAnimator.ofFloat(v, View.ALPHA, fromRange, toRange)
        animator.duration = duration
        animator.repeatMode = ValueAnimator.REVERSE
        animator.repeatCount = ValueAnimator.INFINITE
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.start()
        return animator
    }

    private fun shake(v: View, property: android.util.Property<View, Float>, offset: Float, duration: Long, times: Float): ObjectAnimator {
        val animator = ObjectAnimator.ofFloat(v, property, 0f, offset)
        animator.duration = duration
        animator.interpolator = CycleInterpolator(times)
        animator.start()
        return animator
    }

    private fun start(
        v: View, property: android.util.Property<View, Float>, from: Float, to: Float, duration: Int,
        listener: Animator.AnimatorListener?
    ): ObjectAnimator {
        val animator = ObjectAnimator.ofFloat(v, property, from, to)
        animator.duration = duration.toLong()
        if (listener != null) animator.addListener(listener)
        animator.start()
        return animator
    }
}
