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

import android.view.Gravity
import android.widget.Toast
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadows.ShadowLooper
import org.robolectric.shadows.ShadowToast

@RunWith(RobolectricTestRunner::class)
class ToastUtilsTest {

    @Test
    fun showToastDisplaysText() {
        ToastUtils.init(RuntimeEnvironment.getApplication())
        ToastUtils.showToast("hello")
        assertEquals("hello", ShadowToast.getTextOfLatestToast())
        assertEquals(Toast.LENGTH_SHORT, ShadowToast.getLatestToast().duration)
    }

    @Test
    fun customDurationAndReuseSingleToast() {
        ToastUtils.init(RuntimeEnvironment.getApplication())
        ToastUtils.showToast("one", Toast.LENGTH_LONG)
        val first = ShadowToast.getLatestToast()
        assertEquals(Toast.LENGTH_LONG, first.duration)
        ToastUtils.showToast("two")
        assertEquals("two", ShadowToast.getTextOfLatestToast())
        assertTrue(first === ShadowToast.getLatestToast()) // 复用同一个 Toast 对象
    }

    @Test
    fun backgroundThreadPostsToMainThread() {
        ToastUtils.init(RuntimeEnvironment.getApplication())
        ShadowToast.reset()
        val t = Thread { ToastUtils.showToast("from bg") }
        t.start()
        t.join()
        ShadowLooper.idleMainLooper()
        assertEquals("from bg", ShadowToast.getTextOfLatestToast())
    }

    @Test
    fun positionOverload() {
        ToastUtils.init(RuntimeEnvironment.getApplication())
        ToastUtils.showToast("top", Toast.LENGTH_SHORT, Gravity.TOP, 5, 7)
        assertEquals("top", ShadowToast.getTextOfLatestToast())
        // 新版本系统下文本 Toast 的位置会被忽略，所以这里只验证调用不抛异常、文本正确
    }

    @Test
    fun cancelRemovesToast() {
        ToastUtils.init(RuntimeEnvironment.getApplication())
        ToastUtils.showToast("bye")
        val toast = ShadowToast.getLatestToast()
        ToastUtils.cancel()
        assertTrue(shadowOf(toast).isCancelled)
    }

    @Test
    fun invalidAfterNewInstanceStillWorks() {
        ToastUtils.init(RuntimeEnvironment.getApplication())
        ToastUtils.cancel()
        ToastUtils.showToast("again")
        assertEquals("again", ShadowToast.getTextOfLatestToast())
    }
}
