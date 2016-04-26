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
package com.wx.android.common.web;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wx.android.common.util.DisplayUtils;
import com.wx.android.common.util.StringUtils;
import com.wx.logger.Logger;

/**
 * Web页面
 *
 * @author venshine
 */
public class WebActivity extends Activity {

    private static final String URL = "URL";
    private static final String TITLE = "TITLE";
    private static final String PROGRESS = "PROGRESS";

    private TextView mTitle;    // web title

    private ProgressBar mProgress;  // web progress

    private WebView mWebView;   // webview

    private String mUrl;    // url

    private boolean mShowTitle = true;

    private boolean mShowProgress = true;

    /**
     * Start web activity
     *
     * @param context
     * @param url
     */
    public static void start(Context context, String url) {
        start(context, url, true, true);
    }

    /**
     * Start web activity
     *
     * @param context
     * @param url
     * @param showTitle
     * @param showProgress
     */
    public static void start(Context context, String url, boolean showTitle, boolean showProgress) {
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra(URL, url);
        intent.putExtra(TITLE, showTitle);
        intent.putExtra(PROGRESS, showProgress);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(createContentView());
        getIntentData();
        initWebViewSettings();
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

        // title
        mTitle = new TextView(this);
        mTitle.setGravity(Gravity.CENTER);
        mTitle.setTextColor(Color.BLACK);

        mTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        mTitle.setSingleLine();
        mTitle.setEllipsize(TextUtils.TruncateAt.END);
        mTitle.setPadding(DisplayUtils.dip2px(this, 20), 0, DisplayUtils.dip2px(this, 20), 0);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(LinearLayout
                .LayoutParams.MATCH_PARENT, DisplayUtils.dip2px(this, 45));
        layout.addView(mTitle, titleParams);

        // progressbar
        mProgress = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        LinearLayout.LayoutParams progressParams = new LinearLayout.LayoutParams(LinearLayout
                .LayoutParams.MATCH_PARENT, DisplayUtils.dip2px(this, 3));
        layout.addView(mProgress, progressParams);

        // webview
        mWebView = new WebView(this);
        LinearLayout.LayoutParams webviewParams = new LinearLayout.LayoutParams(LinearLayout
                .LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layout.addView(mWebView, webviewParams);

        return layout;
    }

    /**
     * Get intent data
     */
    private void getIntentData() {
        mUrl = getIntent().getStringExtra(URL);
        mShowTitle = getIntent().getBooleanExtra(TITLE, true);
        mShowProgress = getIntent().getBooleanExtra(PROGRESS, true);
    }

    /**
     * Init WebView configs
     */
    private void initWebViewSettings() {
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setAllowFileAccess(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setNeedInitialFocus(true);

        mWebView.requestFocusFromTouch();
        mWebView.setWebViewClient(mWebViewClient);
        mWebView.setWebChromeClient(mWebChromeClient);
    }

    /**
     * Load url
     */
    private void loadUrl() {
        if (!StringUtils.isEmpty(mUrl)) {
            mWebView.loadUrl(mUrl);
            if (mShowTitle) {
                mTitle.setText(mUrl);
            }
        }
    }

    private WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Logger.v("WebView Page Started");
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            Logger.v("WebView Page Finished");
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            Logger.v("WebView Load Resource");
        }

        @Deprecated
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                Logger.e("ErrorCode:{}, ErrorMessage:{}", errorCode, description);
            }
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Logger.e("ErrorCode:{}, ErrorMessage:{}", error.getErrorCode(), error.getDescription());
            }
        }

        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            super.onReceivedHttpError(view, request, errorResponse);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Logger.e("HttpStatusCode:{}, HttpErrorMessage:{}", errorResponse.getStatusCode(), errorResponse
                        .getReasonPhrase());
            }
        }
    };

    private WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public void onReceivedIcon(WebView view, Bitmap icon) {
            super.onReceivedIcon(view, icon);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            if (mShowTitle) {
                mTitle.setText(title);
            }
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (mShowProgress) {
                mProgress.setVisibility(View.VISIBLE);
                mProgress.setProgress(newProgress);
                if (newProgress == 100) {
                    mProgress.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, final JsPromptResult
                result) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle("Prompt");
            builder.setMessage(message);
            final EditText et = new EditText(view.getContext());
            et.setSingleLine();
            et.setText(defaultValue);
            builder.setView(et);
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    result.confirm(et.getText().toString());
                }
            }).setNeutralButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    result.cancel();
                }
            });
            builder.setCancelable(false);
            builder.create();
            builder.show();
            return true;
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
            AlertDialog.Builder builder = new AlertDialog.Builder(WebActivity.this);
            builder.setTitle("Confirm");
            builder.setMessage(message);
            builder.setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    result.confirm();
                }
            });
            builder.setNegativeButton(android.R.string.cancel, new AlertDialog.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    result.cancel();
                }
            });
            builder.setCancelable(false);
            builder.create();
            builder.show();
            return true;
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
            AlertDialog.Builder builder = new AlertDialog.Builder(WebActivity.this);
            builder.setTitle("Alert");
            builder.setMessage(message);
            builder.setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    result.confirm();
                }
            });
            builder.setCancelable(false);
            builder.create();
            builder.show();
            return true;
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
