package com.wx.android.common;

import android.app.Application;

import com.wx.android.common.util.ToastUtils;

/**
 * @author fengwenxuan
 */
public class CommonApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ToastUtils.init(this);
    }
}
