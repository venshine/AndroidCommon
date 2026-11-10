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

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.wx.android.common.util.AnimatorUtils
import com.wx.android.common.util.ClickUtils

/**
 * 视图与动画：[AnimatorUtils] 抖动 / 呼吸、[ClickUtils.clickMultiple] 连击，以及进入 [PictureActivity] 看双击缩放
 *
 * @author venshine
 */
class ViewActivity : BaseActivity() {

    private lateinit var target: View
    private var breath: ObjectAnimator? = null
    private var taps = 0
    private var fired = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view)
        setToolbar(R.string.demo_view, "AnimatorUtils")

        target = findViewById(R.id.anim_target)
        findViewById<View>(R.id.btn_shake).setOnClickListener { AnimatorUtils.shakeX(target) }
        findViewById<TextView>(R.id.btn_breath).setOnClickListener { toggleBreath(it as TextView) }

        findViewById<View>(R.id.click_target).setOnClickListener { view ->
            taps++
            ClickUtils.clickMultiple(view, CLICKS) {
                fired++
                renderClicks()
            }
            renderClicks()
        }
        findViewById<View>(R.id.btn_picture).setOnClickListener { startActivity(Intent(this, PictureActivity::class.java)) }
        renderClicks()
    }

    /** 呼吸是无限循环的，所以第二次点击要停掉并把透明度还原 */
    private fun toggleBreath(button: TextView) {
        val running = breath
        if (running != null) {
            running.cancel()
            breath = null
            target.alpha = 1f
            button.setText(R.string.view_breath)
        } else {
            breath = AnimatorUtils.breath(target, 0.2f, 1f, 900)
            button.setText(R.string.view_breath_stop)
        }
    }

    private fun renderClicks() {
        findViewById<TextView>(R.id.click_taps).text = getString(R.string.view_click_taps, taps)
        findViewById<TextView>(R.id.click_count).text = getString(R.string.view_click_fired, fired)
    }

    override fun onDestroy() {
        // 无限循环的动画不随 View 销毁自动停止，离开页面时手动取消
        breath?.cancel()
        super.onDestroy()
    }

    private companion object {
        const val CLICKS = 3
    }
}
