package com.gnetop.ltgame.core.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.CompoundButton;

import com.gnetop.ltgame.core.R;


@SuppressLint("AppCompatCustomView")
public class CountDownButton extends CompoundButton {

    //总时长
    private long millisinfuture;

    //间隔时长
    private long countdowninterva;

    //默认背景颜色
    private int normalColor;

    //倒计时 背景颜色
    private int countDownColor;

    //是否结束
    private boolean isFinish;

    //定时器
    private CountDownTimer countDownTimer;

    private OnFinishListener listener;
    private Context mContext;

    public CountDownButton(Context context) {
        this(context, null);
    }

    public CountDownButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountDownButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CountDownButton, defStyleAttr, 0);
        //设置默认时长
        millisinfuture = (long) typedArray.getInt(R.styleable.CountDownButton_millisinfuture, 60000);
        //设置默认间隔时长
        countdowninterva = (long) typedArray.getInt(R.styleable.CountDownButton_countdowninterva, 1000);
        //设置默认背景色
        normalColor = typedArray.getColor(R.styleable.CountDownButton_normalColor, getResources().getColor(R.color.transparent));
        //设置默认倒计时 背景色
        countDownColor = typedArray.getColor(R.styleable.CountDownButton_countDownColor, getResources().getColor(R.color.transparent));
        typedArray.recycle();
        //默认为已结束状态
        isFinish = true;
        //字体居中
        setGravity(Gravity.CENTER);
        //默认文字和背景色
        normalBackground();
        //设置定时器
        countDownTimer = new CountDownTimer(millisinfuture, countdowninterva) {
            @Override
            public void onTick(long millisUntilFinished) {
                //未结束
                isFinish = false;
                setText(mContext.getString(R.string.text_resend)+"  " + (Math.round((float) millisUntilFinished / 1000) - 1) + "s");
                setTextColor(ContextCompat.getColor(mContext, R.color.text_color_black));
                setBackgroundResource(countDownColor);
            }

            @Override
            public void onFinish() {
                //结束
                isFinish = true;
                if (null != listener) {
                    listener.onFinish();
                }
                normalBackground();
            }
        };
    }

    private void normalBackground() {
        setText(R.string.text_send);
        setBackgroundResource(normalColor);
    }

    public void setOnFinishListener(OnFinishListener listener) {
        this.listener = listener;
    }

    public boolean isFinish() {
        return isFinish;
    }

    public void cancel() {
        countDownTimer.cancel();
    }

    public void start() {
        countDownTimer.start();
    }

    public interface OnFinishListener {
        void onFinish();
    }

}
