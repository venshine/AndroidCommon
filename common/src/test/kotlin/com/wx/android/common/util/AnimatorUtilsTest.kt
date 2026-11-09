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
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.app.Application
import android.os.Looper
import android.view.View
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadow.api.Shadow
import org.robolectric.shadows.ShadowValueAnimator
import java.time.Duration

@RunWith(RobolectricTestRunner::class)
class AnimatorUtilsTest {

    private val view = View(RuntimeEnvironment.getApplication() as Application)

    private fun advance(ms: Long) = shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(ms))

    @Test
    fun alphaReachesTargetAndNotifiesListener() {
        var ended = false
        val animator = AnimatorUtils.alpha(view, 0f, 0.5f, 200, object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                ended = true
            }
        })
        assertTrue(animator.isRunning)
        advance(500)
        assertEquals(0.5f, view.alpha, 1e-3f)
        assertTrue(ended)
        assertFalse(animator.isRunning)
    }

    @Test
    fun defaultDurationOverload() {
        AnimatorUtils.alpha(view, 1f, 0f)
        advance(1000)
        assertEquals(0f, view.alpha, 1e-3f)
    }

    @Test
    fun translationRotationAndScale() {
        AnimatorUtils.translationX(view, 0f, 30f, 100)
        AnimatorUtils.translationY(view, 0f, 40f, 100)
        AnimatorUtils.rotationX(view, 0f, 10f, 100)
        AnimatorUtils.rotationY(view, 0f, 20f, 100)
        AnimatorUtils.scaleX(view, 1f, 2f, 100)
        AnimatorUtils.scaleY(view, 1f, 3f, 100)
        advance(500)
        assertEquals(30f, view.translationX, 1e-3f)
        assertEquals(40f, view.translationY, 1e-3f)
        assertEquals(10f, view.rotationX, 1e-3f)
        assertEquals(20f, view.rotationY, 1e-3f)
        assertEquals(2f, view.scaleX, 1e-3f)
        assertEquals(3f, view.scaleY, 1e-3f)
    }

    @Test
    fun shakeReturnsToOrigin() {
        val x = AnimatorUtils.shakeX(view, 10f, 400, 3f)
        val y = AnimatorUtils.shakeY(view, 8f, 400, 2f)
        assertTrue(x.isRunning && y.isRunning)
        advance(1000)
        assertEquals(0f, view.translationX, 1e-3f) // CycleInterpolator 整数周期后回到起点
        assertEquals(0f, view.translationY, 1e-3f)
    }

    @Test
    fun breathRepeatsForeverUntilCancelled() {
        val animator = AnimatorUtils.breath(view, 0.2f, 1f, 100)
        assertTrue(animator.isRunning)
        assertEquals(ValueAnimator.REVERSE, animator.repeatMode)
        // Robolectric 会把无限循环的 repeatCount 改成 1 以免测试卡死，真实值要从 shadow 读取
        assertEquals(ValueAnimator.INFINITE, Shadow.extract<ShadowValueAnimator>(animator).actualRepeatCount)
        animator.cancel()
        assertFalse(animator.isRunning)
    }
}
