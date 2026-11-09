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

import android.view.View
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.shadows.ShadowSystemClock
import java.time.Duration

@RunWith(RobolectricTestRunner::class)
class ClickUtilsTest {

    private fun newView() = View(RuntimeEnvironment.getApplication())

    @Test
    fun firesOnNthClickWithinInterval() {
        val view = newView()
        var fired = 0
        val event = ClickUtils.OnClickEvent { fired++ }
        ClickUtils.clickMultiple(view, 3, 500, event)
        ClickUtils.clickMultiple(view, 3, 500, event)
        assertEquals(0, fired)
        ClickUtils.clickMultiple(view, 3, 500, event)
        assertEquals(1, fired)
    }

    @Test
    fun countResetsAfterFiring() {
        val view = newView()
        var fired = 0
        val event = ClickUtils.OnClickEvent { fired++ }
        repeat(6) { ClickUtils.clickMultiple(view, 3, 500, event) }
        assertEquals(2, fired)
    }

    @Test
    fun slowClickRestartsCount() {
        val view = newView()
        var fired = 0
        val event = ClickUtils.OnClickEvent { fired++ }
        ClickUtils.clickMultiple(view, 2, 500, event)
        ShadowSystemClock.advanceBy(Duration.ofMillis(600)) // 超过间隔，计数重来
        ClickUtils.clickMultiple(view, 2, 500, event)
        assertEquals(0, fired)
        ClickUtils.clickMultiple(view, 2, 500, event)
        assertEquals(1, fired)
    }

    @Test
    fun viewsAreCountedIndependently() {
        val a = newView()
        val b = newView()
        var firedA = 0
        var firedB = 0
        ClickUtils.clickMultiple(a, 2, 500) { firedA++ }
        ClickUtils.clickMultiple(b, 2, 500) { firedB++ } // 点另一个 View 不影响 a 的计数
        ClickUtils.clickMultiple(a, 2, 500) { firedA++ }
        assertEquals(1, firedA)
        assertEquals(0, firedB)
    }

    @Test
    fun defaultIntervalOverload() {
        val view = newView()
        var fired = 0
        ClickUtils.clickMultiple(view, 2) { fired++ }
        ClickUtils.clickMultiple(view, 2) { fired++ }
        assertEquals(1, fired)
    }

    @Test(expected = IllegalArgumentException::class)
    fun clickNumLessThanTwoThrows() {
        ClickUtils.clickMultiple(newView(), 1) { }
    }
}
