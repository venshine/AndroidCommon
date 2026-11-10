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
package com.wx.android.common.demo

import android.app.Activity
import android.app.NotificationManager
import android.content.ClipboardManager
import android.content.Intent
import android.graphics.Bitmap
import android.os.Looper
import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import com.wx.android.common.widget.PictureView
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RuntimeEnvironment
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.GraphicsMode
import org.robolectric.shadows.ShadowToast
import java.time.Duration
import java.util.Calendar

/**
 * 示例页冒烟测试：每个页面在浅色、深色下都能启动，关键交互的结果符合预期
 *
 * 位图滤镜和位图绘制需要原生图形实现，所以整个类用 NATIVE 模式。
 * 每个用例结束时销毁启动过的 Activity，避免无限循环的呼吸动画拖住主线程。
 *
 * @author venshine
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class DemoSmokeTest {

    private val launched = mutableListOf<org.robolectric.android.controller.ActivityController<*>>()

    @After
    fun tearDown() {
        launched.forEach { it.pause().stop().destroy() }
        launched.clear()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    private fun idle() {
        shadowOf(Looper.getMainLooper()).idleFor(Duration.ofSeconds(1))
    }

    private fun <T : Activity> launch(type: Class<T>): T {
        val controller = Robolectric.buildActivity(type).also { launched += it }
        val activity = controller.setup().get()
        idle()
        return activity
    }

    private val screens = listOf(
        MainActivity::class.java, FilterActivity::class.java, VerifyActivity::class.java,
        SystemActivity::class.java, ViewActivity::class.java, PictureActivity::class.java,
        CalendarActivity::class.java,
    )

    @Test
    fun allScreensLaunchInLightMode() {
        screens.forEach { type ->
            val activity = launch(type)
            assertFalse("${type.simpleName} 应为浅色", DemoTheme.isNight(activity))
        }
    }

    @Test
    fun allScreensLaunchInDarkMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        screens.forEach { type ->
            val activity = launch(type)
            assertTrue("${type.simpleName} 应为深色", DemoTheme.isNight(activity))
        }
    }

    @Test
    fun themeToggleSwitchesToDark() {
        val activity = launch(MainActivity::class.java)
        activity.findViewById<View>(R.id.theme_toggle).performClick()
        idle()
        assertEquals(AppCompatDelegate.MODE_NIGHT_YES, AppCompatDelegate.getDefaultNightMode())
    }

    private fun text(activity: Activity, id: Int) = activity.findViewById<TextView>(id).text.toString()

    // ---- 首页 ----

    @Test
    fun homeShowsVersionAndDeviceInfo() {
        val activity = launch(MainActivity::class.java)
        assertEquals("v2.0.0", text(activity, R.id.version_chip))
        assertEquals("AndroidCommon", text(activity, R.id.info_app_value))
        assertEquals("2.0.0 (1)", text(activity, R.id.info_version_value))
        assertEquals("com.wx.android.common.demo", text(activity, R.id.info_package_value))
        assertTrue(text(activity, R.id.info_screen_value).contains("px"))
        assertTrue(text(activity, R.id.info_density_value).contains("dpi"))
        assertTrue(text(activity, R.id.install_code).contains("implementation 'com.github.venshine:AndroidCommon:2.0.0'"))
    }

    @Test
    fun homeCopyDependencyWritesClipboard() {
        val activity = launch(MainActivity::class.java)
        activity.findViewById<View>(R.id.copy_dependency).performClick()
        val clipboard = activity.getSystemService(ClipboardManager::class.java)
        assertEquals("implementation 'com.github.venshine:AndroidCommon:2.0.0'",
            clipboard.primaryClip?.getItemAt(0)?.text.toString())
    }

    @Test
    fun homeCardsOpenEachDemo() {
        val activity = launch(MainActivity::class.java)
        val expected = mapOf(
            R.id.card_filter to FilterActivity::class.java, R.id.card_verify to VerifyActivity::class.java,
            R.id.card_system to SystemActivity::class.java, R.id.card_view to ViewActivity::class.java,
            R.id.card_calendar to CalendarActivity::class.java,
        )
        expected.forEach { (id, target) ->
            activity.findViewById<View>(id).performClick()
            val intent = shadowOf(activity).nextStartedActivity
            assertEquals(target.name, intent.component?.className)
        }
    }

    // ---- 图片滤镜 ----

    private fun resultBitmap(activity: Activity): Bitmap =
        (activity.findViewById<ImageView>(R.id.image_result).drawable as android.graphics.drawable.BitmapDrawable).bitmap

    @Test
    fun filterChangesResultBitmap() {
        val activity = launch(FilterActivity::class.java)
        val original = (activity.findViewById<ImageView>(R.id.image_original).drawable as android.graphics.drawable.BitmapDrawable).bitmap
        // 默认选中灰度：每个像素 r == g == b
        val grey = resultBitmap(activity)
        val pixel = grey.getPixel(10, 10)
        assertEquals(android.graphics.Color.red(pixel), android.graphics.Color.green(pixel))
        assertEquals(android.graphics.Color.green(pixel), android.graphics.Color.blue(pixel))
        // 切到底片：与原图不同，且标题跟着变
        activity.findViewById<View>(R.id.filter_film).performClick()
        assertEquals("底片", text(activity, R.id.filter_name))
        assertFalse(original.sameAs(resultBitmap(activity)))
        // 切回原图：结果与原图逐像素相同
        activity.findViewById<View>(R.id.filter_none).performClick()
        assertTrue(original.sameAs(resultBitmap(activity)))
    }

    @Test
    fun filterSeekBarAppearsForAdjustableFilters() {
        val activity = launch(FilterActivity::class.java)
        val panel = activity.findViewById<View>(R.id.filter_seek_panel)
        assertEquals(View.GONE, panel.visibility)
        activity.findViewById<View>(R.id.filter_hue).performClick()
        assertEquals(View.VISIBLE, panel.visibility)
        val before = resultBitmap(activity)
        activity.findViewById<SeekBar>(R.id.filter_seek).progress = 200
        assertEquals("强度 200", text(activity, R.id.filter_seek_value))
        assertFalse(before.sameAs(resultBitmap(activity)))
    }

    // ---- 校验与摘要 ----

    @Test
    fun verifyShowsResultsAndDigests() {
        val activity = launch(VerifyActivity::class.java)
        assertEquals("有效", text(activity, R.id.result_mobile))
        assertEquals("有效", text(activity, R.id.result_idcard))
        assertEquals("5d41402abc4b2a76b9719d911017c592", text(activity, R.id.md5_value))
        assertEquals("2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824", text(activity, R.id.sha256_value))

        activity.findViewById<View>(R.id.fill_invalid).performClick()
        assertEquals("无效", text(activity, R.id.result_mobile))
        assertEquals("无效", text(activity, R.id.result_idcard))
        assertNotEquals("5d41402abc4b2a76b9719d911017c592", text(activity, R.id.md5_value))

        // 手动输入也实时生效：把手机号改回合法值
        activity.findViewById<EditText>(R.id.input_mobile).setText("13912345678")
        assertEquals("有效", text(activity, R.id.result_mobile))
        activity.findViewById<EditText>(R.id.input_text).setText("")
        assertEquals("d41d8cd98f00b204e9800998ecf8427e", text(activity, R.id.md5_value))
    }

    // ---- 系统能力 ----

    @Test
    fun systemClipboardRoundTrip() {
        val activity = launch(SystemActivity::class.java)
        assertTrue(text(activity, R.id.system_log).contains("点击上方按钮"))
        activity.findViewById<View>(R.id.btn_clipboard).performClick()
        assertTrue(text(activity, R.id.system_log).contains("ClipboardUtils.copy() → getText() → AndroidCommon 剪贴板示例"))
    }

    @Test
    fun systemNotificationNeedsPermission() {
        val activity = launch(SystemActivity::class.java)
        val manager = activity.getSystemService(NotificationManager::class.java)
        activity.findViewById<View>(R.id.btn_notification).performClick()
        assertTrue(text(activity, R.id.system_log).contains("NotificationUtils.create() → false"))
        assertEquals(0, shadowOf(manager).size())

        shadowOf(RuntimeEnvironment.getApplication()).grantPermissions("android.permission.POST_NOTIFICATIONS")
        activity.findViewById<View>(R.id.btn_notification).performClick()
        assertEquals(1, shadowOf(manager).size())
        assertTrue(text(activity, R.id.system_log).startsWith("NotificationUtils.create() → true"))
    }

    @Test
    fun systemActionsLogReturnValues() {
        val activity = launch(SystemActivity::class.java)
        activity.findViewById<View>(R.id.btn_toast).performClick()
        // ToastUtils 由 DemoApp 初始化，这里验证整条链路
        assertEquals("Hello AndroidCommon", ShadowToast.getTextOfLatestToast())
        activity.findViewById<View>(R.id.btn_vibrate).performClick()
        activity.findViewById<View>(R.id.btn_settings).performClick()
        val log = text(activity, R.id.system_log)
        assertTrue(log.contains("ToastUtils.showToast()"))
        assertTrue(log.contains("VibratorUtils.vibrate(300)"))
        assertTrue(log.contains("IntentUtils.openSettings() → true"))
        assertEquals("android.settings.SETTINGS", shadowOf(activity).nextStartedActivity.action)
    }

    @Test
    fun systemKeyboardButtonsLogResults() {
        val activity = launch(SystemActivity::class.java)
        activity.findViewById<View>(R.id.btn_keyboard_show).performClick()
        activity.findViewById<View>(R.id.btn_keyboard_hide).performClick()
        val log = text(activity, R.id.system_log)
        assertTrue(log.contains("InputMethodUtils.showSoftInput() →"))
        assertTrue(log.contains("InputMethodUtils.hideSoftInput() →"))
    }

    // ---- 视图与动画 ----

    @Test
    fun viewShakeMovesTargetAndBreathToggles() {
        val activity = launch(ViewActivity::class.java)
        val target = activity.findViewById<View>(R.id.anim_target)
        activity.findViewById<View>(R.id.btn_shake).performClick()
        shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(120))
        assertNotEquals("抖动开始后应偏离原位", 0f, target.translationX)

        val breath = activity.findViewById<TextView>(R.id.btn_breath)
        breath.performClick()
        assertEquals("停止呼吸", breath.text.toString())
        shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(300))
        assertTrue(target.alpha < 1f)
        breath.performClick()
        assertEquals("呼吸", breath.text.toString())
        assertEquals(1f, target.alpha, 0f)
    }

    @Test
    fun viewTripleClickFiresOnce() {
        val activity = launch(ViewActivity::class.java)
        val button = activity.findViewById<View>(R.id.click_target)
        repeat(2) { button.performClick() }
        assertEquals("已触发 0 次", text(activity, R.id.click_count))
        assertEquals("按下 2 次", text(activity, R.id.click_taps))
        button.performClick()
        assertEquals("已触发 1 次", text(activity, R.id.click_count))
    }

    @Test
    fun viewOpensPictureViewer() {
        val activity = launch(ViewActivity::class.java)
        activity.findViewById<View>(R.id.btn_picture).performClick()
        val intent: Intent = shadowOf(activity).nextStartedActivity
        assertEquals(PictureActivity::class.java.name, intent.component?.className)
    }

    @Test
    fun pictureViewDoubleTapZooms() {
        val activity = launch(PictureActivity::class.java)
        val picture = activity.findViewById<PictureView>(R.id.picture_view)
        assertNotNull(picture.drawable)
        val before = text(activity, R.id.picture_scale)
        val down = SystemClock.uptimeMillis()
        // 两次轻点间隔 100ms，落在 GestureDetector 的双击窗口内
        fun tap(at: Long, id: Int) {
            val kind = if (id == 0) MotionEvent.ACTION_DOWN else MotionEvent.ACTION_UP
            val event = MotionEvent.obtain(down, down + at, kind, 100f, 100f, 0)
            picture.dispatchTouchEvent(event)
            event.recycle()
        }
        tap(0, 0); tap(20, 1); tap(100, 0); tap(120, 1)
        idle()
        assertNotEquals("双击后缩放比例应变化", before, text(activity, R.id.picture_scale))
    }

    // ---- 日期 ----

    @Test
    fun calendarShowsCurrentMonthAndNavigates() {
        val activity = launch(CalendarActivity::class.java)
        val now = Calendar.getInstance()
        assertEquals("${now.get(Calendar.YEAR)} 年 ${now.get(Calendar.MONTH) + 1} 月", text(activity, R.id.calendar_title))
        assertTrue(text(activity, R.id.calendar_detail).contains("今天"))

        activity.findViewById<View>(R.id.calendar_next).performClick()
        val next = (now.clone() as Calendar).apply { add(Calendar.MONTH, 1) }
        assertEquals("${next.get(Calendar.YEAR)} 年 ${next.get(Calendar.MONTH) + 1} 月", text(activity, R.id.calendar_title))

        activity.findViewById<View>(R.id.calendar_today).performClick()
        assertEquals("${now.get(Calendar.YEAR)} 年 ${now.get(Calendar.MONTH) + 1} 月", text(activity, R.id.calendar_title))
    }

    @Test
    fun calendarClampsSelectedDayWhenMonthIsShorter() {
        val activity = launch(CalendarActivity::class.java)
        // 点一个靠后的日期后连续翻月，选中日不会超出当月天数
        val grid = activity.findViewById<android.view.ViewGroup>(R.id.calendar_grid)
        val cells = (0 until grid.childCount).flatMap { row ->
            val line = grid.getChildAt(row) as android.view.ViewGroup
            (0 until line.childCount).map { line.getChildAt(it) as TextView }
        }
        cells.last { it.text.isNotEmpty() }.performClick()
        repeat(12) {
            activity.findViewById<View>(R.id.calendar_next).performClick()
            val selected = cells.count { it.isSelected }
            assertEquals("每个月都应恰有一个选中日", 1, selected)
        }
    }
}
