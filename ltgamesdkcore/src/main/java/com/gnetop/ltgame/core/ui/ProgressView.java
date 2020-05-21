package com.gnetop.ltgame.core.ui;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.gnetop.ltgame.core.R;


public class ProgressView extends View {

    private int mDotCount = 5; // 圆点个数
    private int mDotColor = 0xFFFF9966;// 圆点颜色
    private Paint mPaint;
    private int mRingRadius = 50;// 圆环半径，单位dp
    private int mOriginalRingRadius;// 保存的原始圆环半径，单位dp
    private int mDotRadius = 7; // 小点半径，单位dp
    private int mOriginalDotRadius; // 保存的原始小点半径，单位dp
    private int mCurrentAngle = 0; // 当前旋转的角度
    private ValueAnimator mAnimator;// 旋转动画

    public ProgressView(Context context) {
        this(context, null);
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ProgressView);
        mDotColor = array.getColor(R.styleable.ProgressView_dot_color, mDotColor);
        mDotCount = array.getInt(R.styleable.ProgressView_dot_count, mDotCount);
        array.recycle();
        init();
    }


    /**
     * 初始化动画
     */
    @SuppressLint("WrongConstant")
    private void initAnimatior() {
        mAnimator = ValueAnimator.ofInt(0, 359);
        mAnimator.setDuration(4000);
        mAnimator.setRepeatCount(-1);
        mAnimator.setRepeatMode(ValueAnimator.INFINITE);
        mAnimator.setInterpolator(new LinearInterpolator());

        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurrentAngle = (int) animation.getAnimatedValue();

                invalidate();
            }
        });
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        // 重设圆环半径，防止超出视图大小
        int effectiveWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        int effectiveHeight = getHeight() - getPaddingBottom() - getPaddingTop();
        int maxRadius = Math.min(effectiveWidth / 2, effectiveHeight / 2) - mDotRadius;
        mRingRadius = mRingRadius > maxRadius ? maxRadius : mRingRadius;
        mOriginalRingRadius = mRingRadius;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 根据小球总数平均分配整个圆，得到每个小球的间隔角度
        double cellAngle = 360 / mDotCount;

        for (int i = 0; i < mDotCount; i++) {
            double ange = i * cellAngle + mCurrentAngle;
            // 根据当前角度计算小球到圆心的距离
            calculateRadiusFromProgress();
            // 根据角度绘制单个小球
            drawDot(canvas, ange * 2 * Math.PI / 360);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startAnimation();
    }

    //销毁页面时停止动画
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnimation();
    }
    /**
     * 初始化
     */
    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setColor(mDotColor);
        // 屏幕适配，转化圆环半径，小点半径
        mRingRadius = dp2px(getContext(), mRingRadius);
        mDotRadius = dp2px(getContext(), mDotRadius);
        mOriginalDotRadius = mDotRadius;
        initAnimatior();
    }


    /**
     * dp转px
     *
     * @param context
     * @param dpVal
     * @return
     */
    private int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }

    /**
     * 根据当前角度计算小球到圆心的距离
     */
    private void calculateRadiusFromProgress() {
        float fraction = 1.0f * mCurrentAngle / 180 - 1;
        fraction = Math.abs(fraction);
        mRingRadius = evaluate(fraction, mOriginalRingRadius, mOriginalRingRadius * 2 / 4);
        mDotRadius = evaluate(fraction, mOriginalDotRadius, mOriginalDotRadius * 4 / 5);
    }


    /**
     * @param fraction   当前的估值器计算值
     * @param startValue 起始值
     * @param endValue   终点值
     * @return
     */
    private Integer evaluate(float fraction, Integer startValue, Integer endValue) {
        return (int) (startValue + fraction * (endValue - startValue));
    }




    private void drawDot(Canvas canvas, double angle) {
        // 根据当前角度获取x、y坐标点
        float x = (float) (getWidth() / 2 + mRingRadius * Math.sin(angle));
        float y = (float) (getHeight() / 2 - mRingRadius * Math.cos(angle));
        if (mPaint!=null){

            // 绘制圆
            canvas.drawCircle(x, y, mDotRadius, mPaint);
        }
    }

    /**
     * 开始动画
     */
    public void startAnimation() {
        if (mAnimator != null) {
            mAnimator.start();
        }
    }

    /**
     * 停止动画
     */
    public void stopAnimation() {
        if (mAnimator != null)
            mAnimator.end();
    }


}
