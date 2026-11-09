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

import android.os.Looper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import java.time.Duration
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
class ThreadUtilsTest {

    @Test
    fun executeRunsOffMainThread() {
        val latch = CountDownLatch(1)
        var ranOn: Thread? = null
        ThreadUtils.execute {
            ranOn = Thread.currentThread()
            latch.countDown()
        }
        assertTrue(latch.await(5, TimeUnit.SECONDS))
        assertNotEquals(Looper.getMainLooper().thread, ranOn)
        assertTrue(ranOn!!.name.startsWith("[Async Pool]#"))
    }

    @Test
    fun submitReturnsResult() {
        val future = ThreadUtils.submit { 21 * 2 }
        assertEquals(42, future.get(5, TimeUnit.SECONDS))
    }

    @Test
    fun manyTasksAreNeverDropped() {
        val n = 50
        val latch = CountDownLatch(n)
        repeat(n) { ThreadUtils.execute { latch.countDown() } } // 旧实现队列满后会丢任务
        assertTrue(latch.await(10, TimeUnit.SECONDS))
    }

    @Test
    fun shutdownThenExecuteRecreatesPool() {
        ThreadUtils.shutdown()
        val latch = CountDownLatch(1)
        ThreadUtils.execute { latch.countDown() }
        assertTrue(latch.await(5, TimeUnit.SECONDS))
    }

    @Test
    fun mainThreadHelpers() {
        assertTrue(ThreadUtils.isMainThread())
        assertEquals(Looper.getMainLooper().thread.id, ThreadUtils.getUIThreadId())
        val latch = CountDownLatch(1)
        var isMain: Boolean? = null
        Thread { isMain = ThreadUtils.isMainThread(); latch.countDown() }.start()
        assertTrue(latch.await(5, TimeUnit.SECONDS))
        assertFalse(isMain!!)
    }

    @Test
    fun runOnUiThreadAndDelayed() {
        var a = 0
        var b = 0
        ThreadUtils.runOnUiThread { a++ }
        ThreadUtils.runOnUiThreadDelayed({ b++ }, 1000)
        shadowOf(Looper.getMainLooper()).idle()
        assertEquals(1, a)
        assertEquals(0, b)
        shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(1000))
        assertEquals(1, b)
    }

    @Test
    fun removeCallbacksCancelsPendingTask() {
        var ran = false
        val task = Runnable { ran = true }
        ThreadUtils.runOnUiThreadDelayed(task, 500)
        ThreadUtils.removeCallbacks(task)
        shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(1000))
        assertFalse(ran)
    }
}
