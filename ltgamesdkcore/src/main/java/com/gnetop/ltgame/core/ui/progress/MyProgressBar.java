package com.gnetop.ltgame.core.ui.progress;

import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Window;
import android.widget.TextView;

import com.gnetop.ltgame.core.R;
import com.gnetop.ltgame.core.util.LTGameSDKLog;


/**
 * @author mahaiyun
 * @ClassName: MyProgressBar
 * @Description: 加载进度条
 * @date
 */
public class MyProgressBar {

    /**
     * GeneralUtil 实例
     */
    private static MyProgressBar myProgressBar = null;

    /**
     * 单例模式
     */
    public static MyProgressBar getInstance() {
        if (myProgressBar == null) {
            synchronized (MyProgressBar.class) {
                if (myProgressBar == null) {
                    myProgressBar = new MyProgressBar();
                }
            }
        }
        return myProgressBar;
    }

    /**
     * 请求数据进度条对话框
     */
    private AlertDialog dialogprogress = null;

    public void showProgress(Context context) {
        dismissProgressDialog();
        dialogprogress = new AlertDialog.Builder(context).create();
        dialogprogress.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialogprogress.show();
        dialogprogress.setCancelable(true);
        Window window = dialogprogress.getWindow();
        window.setContentView(R.layout.general_progress);
        setMsg(window, "");
    }

    public void showProgress(Context context, String msg) {
        dismissProgressDialog();
        dialogprogress = new AlertDialog.Builder(context).create();
        dialogprogress.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialogprogress.show();
        dialogprogress.setCancelable(true);
        Window window = dialogprogress.getWindow();
        window.setContentView(R.layout.general_progress);
        setMsg(window, msg);
    }

    public void showProgress(Context context, boolean cancelable) {
        dismissProgressDialog();
        dialogprogress = new AlertDialog.Builder(context).create();
        dialogprogress.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialogprogress.show();
        dialogprogress.setCancelable(cancelable);
        Window window = dialogprogress.getWindow();
        window.setContentView(R.layout.general_progress);
        setMsg(window, "");
    }

    public void showProgress(Context context, String msg, boolean cancelable) {
        dismissProgressDialog();
        dialogprogress = new AlertDialog.Builder(context).create();
        dialogprogress.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialogprogress.show();
        dialogprogress.setCancelable(cancelable);
        Window window = dialogprogress.getWindow();
        window.setContentView(R.layout.general_progress);
        setMsg(window, msg);
    }

    private void setMsg(Window window, String msg) {
        msg = TextUtils.isEmpty(msg) ? "Loading" : msg;
        TextView textView = window.findViewById(R.id.msg_txt);
        textView.setText(msg);
    }

    public void dismissProgressDialog() {
        try {
            if (dialogprogress != null && dialogprogress.isShowing()) {
                dialogprogress.dismiss();
                dialogprogress = null;
            }
        } catch (Exception e) {
            LTGameSDKLog.loge(e.toString());
        }
    }
}
