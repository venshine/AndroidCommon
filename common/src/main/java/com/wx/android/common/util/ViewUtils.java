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
package com.wx.android.common.util;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Field;

/**
 * View操作
 *
 * @author venshine
 */
public class ViewUtils {

    /**
     * Measure view's height
     *
     * @param view
     * @return
     */
    public static int measureViewHeight(View view) {
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        return view.getMeasuredHeight();
    }

    /**
     * Get TextView's height, TextView's width is full screen width
     *
     * @param textView
     * @return
     */
    public static int getTextViewHeight(TextView textView) {
        WindowManager wm =
                (WindowManager) textView.getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        int deviceWidth;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            display.getSize(size);
            deviceWidth = size.x;
        } else {
            deviceWidth = display.getWidth();
        }

        return getTextViewHeight(textView, deviceWidth);
    }

    /**
     * Get TextView's height by textview's width
     *
     * @param textView
     * @param width
     * @return
     */
    public static int getTextViewHeight(TextView textView, int width) {
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.AT_MOST);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        textView.measure(widthMeasureSpec, heightMeasureSpec);
        return textView.getMeasuredHeight();
    }

    /**
     * Find TextView by traverse view tree
     *
     * @param view
     * @return
     */
    public static TextView findTextView(View view) {
        if (view instanceof TextView) {
            return (TextView) view;
        } else {
            if (view instanceof ViewGroup) {
                return findTextView(((ViewGroup) view).getChildAt(0));
            } else {
                return null;
            }
        }
    }

    /**
     * Get GridView's horizontal spacing
     *
     * @param GridView
     * @return
     */
    @Deprecated
    private static int getGridViewHorizontalSpacing(GridView GridView) {
        int spacing = 0;
        try {
            Field field = GridView.getClass().getDeclaredField("mHorizontalSpacing");
            field.setAccessible(true);
            spacing = field.getInt(GridView);
        } catch (Exception e) {
        }
        return spacing;
    }

    /**
     * Get GridView's request horizontal spacing
     *
     * @param GridView
     * @return
     */
    private static int getGridViewRequestHorizontalSpacing(GridView GridView) {
        int spacing = 0;
        Field field = null;
        try {
            field = GridView.getClass().getDeclaredField("mRequestedHorizontalSpacing");
            field.setAccessible(true);
            spacing = field.getInt(GridView);
        } catch (Exception e) {
        }
        return spacing;
    }

    /**
     * Get GridView's height
     *
     * @param GridView
     * @return
     */
    public static int getGridViewHeight(GridView GridView) {
        if (GridView == null) {
            return 0;
        }
        ListAdapter listAdapter = GridView.getAdapter();
        if (listAdapter == null) {
            return 0;
        }
        int rows = 0;
        int columns = GridView.getNumColumns();
        int horizontalBorderHeight = getGridViewRequestHorizontalSpacing(GridView);
        if (listAdapter.getCount() % columns > 0) {
            rows = listAdapter.getCount() / columns + 1;
        } else {
            rows = listAdapter.getCount() / columns;
        }
        int totalHeight = 0;
        for (int i = 0; i < rows; i++) {
            View listItem = listAdapter.getView(i, null, GridView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        return (totalHeight + horizontalBorderHeight * (rows - 1));
    }

    /**
     * Set GridView's height
     *
     * @param GridView
     */
    public static void setGridViewHeight(GridView GridView) {
        int h = getGridViewHeight(GridView);
        if (h == 0 || GridView == null) {
            return;
        }
        ViewGroup.LayoutParams params = GridView.getLayoutParams();
        params.height = h;
        GridView.setLayoutParams(params);
    }

    /**
     * Get ListView's height
     *
     * @param ListView
     * @return
     */
    public static int getListViewHeight(ListView ListView) {
        if (ListView == null) {
            return 0;
        }

        ListAdapter listAdapter = ListView.getAdapter();
        if (listAdapter == null) {
            return 0;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, ListView);
            listItem.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            totalHeight += listItem.getMeasuredHeight();
        }

        return (totalHeight + (ListView.getDividerHeight() * (listAdapter.getCount() - 1)));
    }

    /**
     * Set ListView's height
     *
     * @param ListView
     * @param height
     */
    public static void setListViewHeight(ListView ListView, int height) {
        int h = 0;
        if (height == 0) {
            h = getListViewHeight(ListView);
            if (h == 0 || ListView == null) {
                return;
            }
        } else {
            h = height;
        }
        ViewGroup.LayoutParams params = ListView.getLayoutParams();
        params.height = h;
        ListView.setLayoutParams(params);
    }

    /**
     * Set ListView's height
     *
     * @param ListView
     */
    public static void setListViewHeight(ListView ListView) {
        setListViewHeight(ListView, 0);
    }

}
