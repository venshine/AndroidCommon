package com.wx.android.common.util;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.CycleInterpolator;

/**
 * Animation effect, it contains alpha, translation, rotation, scale.
 *
 * @author fengwx
 */
public class AnimatorUtils {

    public static final float ALPHA_MIN = 0.0f;
    public static final float ALPHA_MAX = 1.0f;

    /**
     * alpha
     *
     * @param v
     * @param fromAlpha
     * @param toAlpha
     * @param duration
     */
    public static void alpha(View v, float fromAlpha, float toAlpha, int duration) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(v, View.ALPHA, fromAlpha, toAlpha);
        animator.setDuration(duration);
        animator.start();
    }

    /**
     * translation x
     *
     * @param v
     * @param fromX
     * @param toX
     * @param duration
     */
    public static void translationX(View v, float fromX, float toX, int duration) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(v, View.TRANSLATION_X, fromX, toX);
        animator.setDuration(duration);
        animator.start();
    }

    /**
     * translation y
     *
     * @param v
     * @param fromY
     * @param toY
     * @param duration
     */
    public static void translationY(View v, float fromY, float toY, int duration) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(v, View.TRANSLATION_Y, fromY, toY);
        animator.setDuration(duration);
        animator.start();
    }

    /**
     * rotation x
     *
     * @param v
     * @param fromX
     * @param toX
     * @param duration
     */
    public static void rotationX(View v, float fromX, float toX, int duration) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(v, View.ROTATION_X, fromX, toX);
        animator.setDuration(duration);
        animator.start();
    }

    /**
     * rotation y
     *
     * @param v
     * @param fromY
     * @param toY
     * @param duration
     */
    public static void rotationY(View v, float fromY, float toY, int duration) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(v, View.ROTATION_Y, fromY, toY);
        animator.setDuration(duration);
        animator.start();
    }

    /**
     * scale x
     *
     * @param v
     * @param fromX
     * @param toX
     * @param duration
     */
    public static void scaleX(View v, float fromX, float toX, int duration) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(v, View.SCALE_X, fromX, toX);
        animator.setDuration(duration);
        animator.start();
    }

    /**
     * scale y
     *
     * @param v
     * @param fromY
     * @param toY
     * @param duration
     */
    public static void scaleY(View v, float fromY, float toY, int duration) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(v, View.SCALE_Y, fromY, toY);
        animator.setDuration(duration);
        animator.start();
    }

    /**
     * shake x
     *
     * @param v
     */
    public static void shakeX(View v) {
        shakeX(v, 10, 1000, 5.0f);
    }

    /**
     * shake x
     *
     * @param v
     * @param offset
     * @param duration
     * @param times
     */
    public static void shakeX(View v, float offset, long duration, float times) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(v, View.TRANSLATION_X, 0, offset);
        animator.setDuration(duration);
        animator.setInterpolator(new CycleInterpolator(times));
        animator.start();
    }

    /**
     * shake y
     *
     * @param v
     */
    public static void shakeY(View v) {
        shakeY(v, 10, 1000, 5.0f);
    }

    /**
     * shake x
     *
     * @param v
     * @param offset
     * @param duration
     * @param times
     */
    public static void shakeY(View v, float offset, long duration, float times) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(v, View.TRANSLATION_Y, 0, offset);
        animator.setDuration(duration);
        animator.setInterpolator(new CycleInterpolator(times));
        animator.start();
    }

    /**
     * breath light effect
     *
     * @param v
     */
    public static void breath(View v) {
        breath(v, 0.0f, 1.0f, 1000);
    }

    /**
     * breath light effect
     *
     * @param v
     * @param fromRange
     * @param toRange
     * @param duration
     */
    public static void breath(View v, float fromRange, float toRange, long duration) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(v, View.ALPHA, fromRange, toRange);
        animator.setDuration(duration);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();
    }

}
