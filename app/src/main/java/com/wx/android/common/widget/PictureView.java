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
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.wx.android.common.util.DisplayUtils;

/**
 * 图片操作
 *
 * @author venshine
 */
public class PictureView extends ImageView {

    private static final int NONE = 0;// 初始状态
    private static final int DRAG = 1;// 拖动
    private static final int ZOOM = 2;// 缩放

    private static final float MAX_SCALE = 4f;// 最大缩放比例

    private float minScaleR;// 最小缩放比例

    private int mode = NONE;

    private Bitmap bitmap = null;
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();
    private Matrix originalMatrix = new Matrix();   // 原始状态

    private PointF prev = new PointF();
    private PointF mid = new PointF();
    private float dist = 1f;

    private int mScreenW, mScreenH;

    private GestureDetector gestureDetector;

    public PictureView(Context context) {
        super(context);
        initView();
    }

    public PictureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public PictureView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    public void initView() {
        // 屏幕宽高
        mScreenW = DisplayUtils.getScreenWidth(getContext());
        mScreenH = DisplayUtils.getScreenHeight(getContext());

        // 设置ScaleType为ScaleType.MATRIX，这一步很重要
        setScaleType(ScaleType.MATRIX);

        // 获取bitmap对象
        BitmapDrawable bd = (BitmapDrawable) getDrawable();
        if (bd != null) {
            bitmap = bd.getBitmap();
            setImageBitmap(bitmap);
            initMatrix(bitmap);
        }
        setGestureListener();
    }

    /**
     * 设置手势监听
     */
    private void setGestureListener() {
        gestureDetector = new GestureDetector(
                getContext(),
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDown(MotionEvent e) {
                        return false;
                    }

                    @Override
                    public void onLongPress(MotionEvent e) {
                        super.onLongPress(e);
                    }
                });
        gestureDetector.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {
                return false;
            }

            /**
             * 双击手势
             */
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                mid.x = e.getX();
                mid.y = e.getY();
                matrix.set(savedMatrix);
                if (isZoomChanged()) {  // 缩小
                    reset();
                } else {    // 放大
                    matrix.postScale(2f, 2f, mid.x, mid.y);
                }
                center();
                setImageMatrix(matrix);
                return true;
            }

            /**
             * 单击手势
             */
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                return false;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gestureDetector.onTouchEvent(event)) {
            return true;
        } else {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                // 主点按下
                case MotionEvent.ACTION_DOWN:
                    savedMatrix.set(matrix);
                    prev.set(event.getX(), event.getY());
                    mode = DRAG;
                    break;
                // 副点按下
                case MotionEvent.ACTION_POINTER_DOWN:
                    dist = spacing(event);
                    // 如果连续两点距离大于10，则判定为多点模式
                    if (spacing(event) > 10f) {
                        savedMatrix.set(matrix);
                        midPoint(mid, event);
                        mode = ZOOM;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                    mode = NONE;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (mode == DRAG) {
                        matrix.set(savedMatrix);
                        matrix.postTranslate(event.getX() - prev.x, event.getY()
                                - prev.y);
                    } else if (mode == ZOOM) {
                        float newDist = spacing(event);
                        if (newDist > 10f) {
                            matrix.set(savedMatrix);
                            float tScale = newDist / dist;
                            matrix.postScale(tScale, tScale, mid.x, mid.y);
                        }
                    }
                    break;
            }
            setImageMatrix(matrix);
            checkView();
            return true;
        }
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        initMatrix(bm);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        BitmapDrawable bd = (BitmapDrawable) getDrawable();
        if (bd != null) {
            initMatrix(bd.getBitmap());
        }
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        setImageDrawable(getResources().getDrawable(resId));
    }

    /**
     * 初始化矩阵
     *
     * @param bm
     */
    private void initMatrix(Bitmap bm) {
        if (bm != null) {
            bitmap = bm;
            minZoom();
            center();
            setImageMatrix(matrix);
            // 设置原始矩阵
            originalMatrix.set(matrix);
        }
    }

    /**
     * 判断缩放级别是否改变
     *
     * @return
     */
    private boolean isZoomChanged() {
        float[] values = new float[9];
        getImageMatrix().getValues(values);
        //获取当前X轴缩放级别
        float scale = values[Matrix.MSCALE_X];
        //获取模板的X轴缩放级别，两者做比较
        originalMatrix.getValues(values);
        return scale != values[Matrix.MSCALE_X];
    }

    /**
     * 重置原始矩阵
     */
    private void reset() {
        matrix.set(originalMatrix);
    }

    /**
     * 检查缩放范围
     */
    private void checkView() {
        float p[] = new float[9];
        matrix.getValues(p);
        if (mode == ZOOM) {
            if (p[0] < minScaleR) {
                matrix.setScale(minScaleR, minScaleR);
            }
            if (p[0] > MAX_SCALE) {
                matrix.set(savedMatrix);
            }
        }
        center();
    }

    /**
     * 最小缩放比例，最大为100%
     */
    private void minZoom() {
        minScaleR = Math.min(
                (float) mScreenW / (float) bitmap.getWidth(),
                (float) mScreenH / (float) bitmap.getHeight());
        if (minScaleR < 1.0) {
            matrix.postScale(minScaleR, minScaleR);
        } else {
            minScaleR = 1.0f;
        }
    }

    /**
     * 横向、纵向居中
     */
    private void center() {
        center(true, true);
    }

    /**
     * 横向、纵向居中
     */
    protected void center(boolean horizontal, boolean vertical) {
        Matrix m = new Matrix();
        m.set(matrix);
        RectF rect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
        m.mapRect(rect);

        float height = rect.height();
        float width = rect.width();

        float deltaX = 0, deltaY = 0;

        if (vertical) {
            // 图片小于屏幕大小，则居中显示。大于屏幕，上方留空则往上移，下方留空则往下移
            if (height < mScreenH) {
                deltaY = (mScreenH - height) / 2 - rect.top;
            } else if (rect.top > 0) {
                deltaY = -rect.top;
            } else if (rect.bottom < mScreenH) {
                deltaY = getHeight() - rect.bottom;
            }
        }

        if (horizontal) {
            if (width < mScreenW) {
                deltaX = (mScreenW - width) / 2 - rect.left;
            } else if (rect.left > 0) {
                deltaX = -rect.left;
            } else if (rect.right < mScreenW) {
                deltaX = mScreenW - rect.right;
            }
        }
        matrix.postTranslate(deltaX, deltaY);
    }

    /**
     * 两点的距离
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * 两点的中点
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

}
