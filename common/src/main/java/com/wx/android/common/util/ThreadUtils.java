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
package com.wx.android.common.util;

import android.os.AsyncTask;
import android.os.Looper;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程信息
 * 
 * @author venshine
 */
public class ThreadUtils {

	private static final int CORE_POOL_SIZE = 3;
	private static final int MAXIMUM_POOL_SIZE = 5;
	private static final int KEEP_ALIVE = 1;
	private static final int BLOCKING_QUEUE_CAPACITY = 5;

	private static BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue<Runnable>(BLOCKING_QUEUE_CAPACITY);

	private static final ThreadFactory sThreadFactory = new ThreadFactory() {
		private final AtomicInteger mCount = new AtomicInteger(1);

		public Thread newThread(Runnable r) {
			return new Thread(r, "[Async Pool]#" + mCount.getAndIncrement());
		}
	};

	private static ThreadPoolExecutor mPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE,
			TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory, new ThreadPoolExecutor.DiscardOldestPolicy());

	/**
	 * Executes the task, this method must be invoked on the UI thread.
	 */
	public static void execute(Runnable runnable) {
		if (runnable == null) {
			return;
		}
		new Task(runnable).executeOnExecutor(mPool);
	}

	@Deprecated
	public static <T> Future<T> submit(Callable<T> task) {
		return mPool.submit(task);
	}

	/**
	 * Shutdown thread pool
	 */
	public static void shutdown() {
		if (mPool != null) {
			mPool.shutdown();
			mPool = null;
		}
	}

	/**
	 * Get UI thread's id
	 * 
	 * @return
	 */
	public static long getUIThreadId() {
		return Looper.getMainLooper().getThread().getId();
	}

	private static class Task extends AsyncTask<Void, Void, Void> {
		private Runnable runnable;

		public Task(Runnable run) {
			runnable = run;
		}

		protected Void doInBackground(Void... params) {
			if (runnable != null) {
				runnable.run();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}

	}

}
