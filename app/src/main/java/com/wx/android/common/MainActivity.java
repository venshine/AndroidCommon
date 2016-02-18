package com.wx.android.common;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.wx.android.common.log.Logger;
import com.wx.android.common.util.ToastUtils;

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
    }

    public void click(View v) {
    }

}
