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

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Callable
import java.util.concurrent.Future
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 * 线程工具：后台线程池和主线程调度。
 *
 * 后台线程池固定最多 3 个线程，任务排队执行，不会丢弃任务。
 *
 * @author venshine
 */
object ThreadUtils {

    private const val POOL_SIZE = 3
    private const val KEEP_ALIVE_SECONDS = 1L

    private val mainHandler = Handler(Looper.getMainLooper())

    private val threadFactory = object : ThreadFactory {
        private val count = AtomicInteger(1)
        override fun newThread(r: Runnable): Thread = Thread(r, "[Async Pool]#" + count.getAndIncrement())
    }

    @Volatile
    private var pool: ThreadPoolExecutor? = null

    @Synchronized
    private fun pool(): ThreadPoolExecutor {
        var p = pool
        if (p == null || p.isShutdown) {
            p = ThreadPoolExecutor(
                POOL_SIZE, POOL_SIZE, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
                LinkedBlockingQueue(), threadFactory
            ).apply { allowCoreThreadTimeOut(true) }
            pool = p
        }
        return p
    }

    /** 在后台线程池执行 */
    @JvmStatic
    fun execute(runnable: Runnable) {
        pool().execute(runnable)
    }

    /** 在后台线程池提交带返回值的任务 */
    @JvmStatic
    fun <T> submit(task: Callable<T>): Future<T> = pool().submit(task)

    /** 关闭线程池；之后再次调用 [execute] / [submit] 会重新创建 */
    @JvmStatic
    @Synchronized
    fun shutdown() {
        pool?.shutdown()
        pool = null
    }

    /** 当前是否在主线程 */
    @JvmStatic
    fun isMainThread(): Boolean = Looper.myLooper() == Looper.getMainLooper()

    /** 主线程线程 id */
    @JvmStatic
    fun getUIThreadId(): Long = Looper.getMainLooper().thread.id

    /** 投递到主线程执行 */
    @JvmStatic
    fun runOnUiThread(runnable: Runnable) {
        mainHandler.post(runnable)
    }

    /** 延迟 [delayMillis] 毫秒后在主线程执行 */
    @JvmStatic
    fun runOnUiThreadDelayed(runnable: Runnable, delayMillis: Long) {
        mainHandler.postDelayed(runnable, delayMillis)
    }

    /** 移除尚未执行的主线程任务 */
    @JvmStatic
    fun removeCallbacks(runnable: Runnable) {
        mainHandler.removeCallbacks(runnable)
    }
}
