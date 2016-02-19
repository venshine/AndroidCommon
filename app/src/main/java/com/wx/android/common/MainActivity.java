package com.wx.android.common;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.wx.android.common.web.WebActivity;

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
        WebActivity.start(this, "http://www.sina.com.cn");
    }

}
