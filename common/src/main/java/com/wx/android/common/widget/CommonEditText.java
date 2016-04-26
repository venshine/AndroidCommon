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
package com.wx.android.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.widget.EditText;

/**
 * 通用EditText，包含返回和删除操作
 *
 * @author venshine
 */
public class CommonEditText extends EditText {

    public CommonEditText(Context context) {
        super(context);
    }

    public CommonEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CommonEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private OnDelKeyEventListener mOnDelKeyEventListener;

    private OnBackKeyEventListener mOnBackKeyEventListener;

    /**
     * @param onDelKeyEventListener
     */
    public void setOnDelKeyEventListener(OnDelKeyEventListener onDelKeyEventListener) {
        this.mOnDelKeyEventListener = onDelKeyEventListener;
    }

    /**
     * @param onBackKeyEventListener
     */
    public void setOnBackKeyEventListener(OnBackKeyEventListener onBackKeyEventListener) {
        this.mOnBackKeyEventListener = onBackKeyEventListener;
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (null != mOnBackKeyEventListener) {
                mOnBackKeyEventListener.onBackClick();
            }
            // return true;
        }
        return super.onKeyPreIme(keyCode, event);
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        InputConnection connection = super.onCreateInputConnection(outAttrs);
        if (null != connection) {
            return new ZanyInputConnection(connection, true);
        }
        return connection;
    }

    private class ZanyInputConnection extends InputConnectionWrapper {

        public ZanyInputConnection(InputConnection target, boolean mutable) {
            super(target, mutable);
        }

        @Override
        public boolean sendKeyEvent(KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
                if (mOnDelKeyEventListener != null) {
                    mOnDelKeyEventListener.onDeleteClick();
                    return true;
                }
            }
            return super.sendKeyEvent(event);
        }

        @Override
        public boolean deleteSurroundingText(int beforeLength, int afterLength) {
            if (beforeLength == 1 && afterLength == 0) {
                return sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
                        && sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
            }
            return super.deleteSurroundingText(beforeLength, afterLength);
        }
    }

    public interface OnDelKeyEventListener {
        void onDeleteClick();
    }

    public interface OnBackKeyEventListener {
        void onBackClick();
    }

}
