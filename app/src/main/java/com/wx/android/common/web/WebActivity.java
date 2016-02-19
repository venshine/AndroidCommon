package com.wx.android.common.web;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.wx.android.common.util.StringUtils;

/**
 * Web Page
 *
 * @author fengwx
 */
public class WebActivity extends Activity {

    private static final String URL = "URL";

    private WebView mWebView;

    private String mUrl;

    /**
     * Start web activity
     *
     * @param context
     * @param url
     */
    public static void start(Context context, String url) {
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra(URL, url);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(createContentView());
        getIntentData();
        initWebViewConfigs();
        loadUrl();
    }

    /**
     * Create content view
     *
     * @return
     */
    private View createContentView() {
        // root layout
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        // webview
        mWebView = new WebView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout
                .LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layout.addView(mWebView, params);

        return layout;
    }

    /**
     * Get intent data
     */
    private void getIntentData() {
        mUrl = getIntent().getStringExtra(URL);
    }

    /**
     * Init WebView configs
     */
    private void initWebViewConfigs() {
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient());
    }

    /**
     * Load url
     */
    private void loadUrl() {
        if (!StringUtils.equalsNull(mUrl)) {
            mWebView.loadUrl(mUrl);
        }
    }


}
