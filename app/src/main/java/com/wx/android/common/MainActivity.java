package com.wx.android.common;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.wx.android.common.util.LogUtils;
import com.wx.android.common.util.ObjectUtils;

/**
 * Main Activity
 *
 * @author fengwx
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LogUtils.d(ObjectUtils.equals(null, null) + "");
    }

    public void click(View v) {
    }

}
