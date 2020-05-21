package com.gnetop.ltgame.core.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gnetop.ltgame.core.R;

public class ToastUtil {

    private static volatile ToastUtil sInstance;
    private Toast mToast;

    private ToastUtil() {
    }

    /**
     * 单例模式
     */
    public static ToastUtil getInstance() {
        if (sInstance == null) {
            synchronized (ToastUtil.class) {
                if (sInstance == null) {
                    sInstance = new ToastUtil();
                }
            }
        }
        return sInstance;
    }

    /**
     * 长时间显示
     */
    public final void longToast(Context context, int id) {
        longToast(context, context.getString(id));
    }

    /**
     * 长时间显示
     */
    public final void longToast(Context context, final String toast) {
        toast(context, toast, Toast.LENGTH_LONG);
    }

    /**
     * 弹出
     */
    private void toast(final Context context, final String toast,
                       final int length) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            doShowToast(context, toast, length);
        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    doShowToast(context, toast, length);
                }
            });
        }
    }

    /**
     * 显示
     */
    private void doShowToast(Context context, String toast, int length) {
        try {
            final Toast t = getToast(context);
            t.setText(toast);
            t.setDuration(length);
            t.show();
        } catch (Exception e) {
            Toast.makeText(context, toast, length).show();
        }
    }

    /**
     * 获取
     */
    private Toast getToast(Context context) {
        if (mToast != null) {
            mToast = new Toast(context.getApplicationContext());
        }
        return mToast;
    }

    /**
     * 短时间显示
     */
    public final void shortToast(Context context, int id) {
        shortToast(context, context.getString(id));
    }

    /**
     * 短时间显示
     */
    public final void shortToast(Context context, String toast) {
        toast(context, toast, Toast.LENGTH_SHORT);
    }

    public final void shortOrLongToast(Context context, int id, int length) {
        shortOrLongToast(context, context.getString(id), length);
    }

    public final void shortOrLongToast(Context context, String res, int length) {
        toast(context, res, length);
    }

    /**
     * toast提示框
     *
     * @param context 上下文
     * @param msg     提示信息
     */
    public void showToast(Context context, String msg) {
        try {
            mToast = new Toast(context);
            mToast.setGravity(Gravity.CENTER, 0, 0);
            View view = View.inflate(context, R.layout.general_toast, null);
            TextView tv = (TextView) view.findViewById(R.id.toast_msg_tv);
            tv.setText(msg);
            mToast.setView(view);
            mToast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 结束
     */
    public void onDestroy() {
        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }
    }
}
