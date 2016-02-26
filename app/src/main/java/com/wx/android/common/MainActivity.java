package com.wx.android.common;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.wx.android.common.widget.RoundDrawable;

/**
 * Main Activity
 *
 * @author fengwx
 */
public class MainActivity extends Activity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.imageview);
        imageView.setImageDrawable(new RoundDrawable(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher)));
    }

    public void click(View v) {
    }

}
