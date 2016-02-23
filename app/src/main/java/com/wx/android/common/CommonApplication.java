package com.wx.android.common;

import android.app.Application;

import com.wx.android.common.util.ToastUtils;

/**
 * Common Application
 *
 * @author fengwx
 */
public class CommonApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ToastUtils.init(this);
    }
}
