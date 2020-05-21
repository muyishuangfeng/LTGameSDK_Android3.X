package com.gnetop.ltgame.core.util;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v7.widget.AppCompatImageView;

public class AnimationUtils {

    public static void changeUI(Context context, AppCompatImageView mImgLeft, AppCompatImageView mImgRight) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(mImgLeft, "translationX", 1, dip2px(context, 60), 1);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mImgRight, "translationX", 1, -dip2px(context, 60), 1);
        animator2.setRepeatCount(ValueAnimator.INFINITE);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator, animator2);
        animatorSet.setDuration(2000);
        animatorSet.start();
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    private static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


}
