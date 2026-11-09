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
package com.wx.android.common.widget

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputConnectionWrapper
import android.widget.EditText

/**
 * 可监听软键盘删除键、返回键的输入框。
 *
 * 部分输入法的删除键不走 `onKeyDown`，所以通过 [InputConnection] 拦截。
 *
 * @author venshine
 */
class CommonEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = android.R.attr.editTextStyle
) : EditText(context, attrs, defStyle) {

    private var onDelKeyEventListener: OnDelKeyEventListener? = null
    private var onBackKeyEventListener: OnBackKeyEventListener? = null

    /** 设置删除键监听；有监听时删除键事件由监听处理，不再删除文字 */
    fun setOnDelKeyEventListener(listener: OnDelKeyEventListener?) {
        onDelKeyEventListener = listener
    }

    /** 设置软键盘收起（返回键）监听 */
    fun setOnBackKeyEventListener(listener: OnBackKeyEventListener?) {
        onBackKeyEventListener = listener
    }

    override fun onKeyPreIme(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            onBackKeyEventListener?.onBackClick()
        }
        return super.onKeyPreIme(keyCode, event)
    }

    override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection? {
        val connection = super.onCreateInputConnection(outAttrs) ?: return null
        return DelKeyInputConnection(connection)
    }

    private inner class DelKeyInputConnection(target: InputConnection) : InputConnectionWrapper(target, true) {

        override fun sendKeyEvent(event: KeyEvent): Boolean {
            val listener = onDelKeyEventListener
            if (listener != null && event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_DEL) {
                listener.onDeleteClick()
                return true
            }
            return super.sendKeyEvent(event)
        }

        override fun deleteSurroundingText(beforeLength: Int, afterLength: Int): Boolean {
            // 部分输入法的退格用 deleteSurroundingText(1, 0) 发出，转成按键事件统一处理
            if (beforeLength == 1 && afterLength == 0) {
                return sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL)) &&
                    sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL))
            }
            return super.deleteSurroundingText(beforeLength, afterLength)
        }
    }

    fun interface OnDelKeyEventListener {
        fun onDeleteClick()
    }

    fun interface OnBackKeyEventListener {
        fun onBackClick()
    }
}
