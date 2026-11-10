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

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import com.wx.android.common.util.DigestUtils
import com.wx.android.common.util.VerifyUtils

/**
 * 校验与摘要：输入一变，[VerifyUtils] 的校验结果和 [DigestUtils] 的摘要立刻跟着变
 *
 * @author venshine
 */
class VerifyActivity : BaseActivity() {

    private lateinit var mobile: EditText
    private lateinit var idCard: EditText
    private lateinit var text: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify)
        setToolbar(R.string.demo_verify, "VerifyUtils")

        mobile = findViewById(R.id.input_mobile)
        idCard = findViewById(R.id.input_idcard)
        text = findViewById(R.id.input_text)
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

            override fun afterTextChanged(s: Editable?) = render()
        }
        listOf(mobile, idCard, text).forEach { it.addTextChangedListener(watcher) }

        findViewById<TextView>(R.id.fill_valid).setOnClickListener { fill(VALID_MOBILE, VALID_ID_CARD, "hello") }
        findViewById<TextView>(R.id.fill_invalid).setOnClickListener { fill(INVALID_MOBILE, INVALID_ID_CARD, "hello!") }
        fill(VALID_MOBILE, VALID_ID_CARD, "hello")
    }

    /** 一次填满三个输入框，最后统一渲染一遍 */
    private fun fill(mobileValue: String, idCardValue: String, textValue: String) {
        mobile.setText(mobileValue)
        idCard.setText(idCardValue)
        text.setText(textValue)
        render()
    }

    private fun render() {
        badge(findViewById(R.id.result_mobile), VerifyUtils.isMobileNumber(mobile.text.toString()))
        badge(findViewById(R.id.result_idcard), VerifyUtils.isIdCard(idCard.text.toString()))
        val input = text.text.toString()
        findViewById<TextView>(R.id.md5_value).text = DigestUtils.md5(input)
        findViewById<TextView>(R.id.sha256_value).text = DigestUtils.sha256(input)
    }

    private fun badge(view: TextView, valid: Boolean) {
        view.setText(if (valid) R.string.verify_ok else R.string.verify_bad)
        view.setTextColor(colorOf(if (valid) R.color.ok else R.color.bad))
        view.setBackgroundResource(if (valid) R.drawable.bg_badge_ok else R.drawable.bg_badge_bad)
    }

    private companion object {
        const val VALID_MOBILE = "13800138000"
        const val INVALID_MOBILE = "12345"

        // GB 11643-1999 附录里的实例号码；无效示例只改了末位校验码
        const val VALID_ID_CARD = "11010519491231002X"
        const val INVALID_ID_CARD = "110105194912310021"
    }
}
