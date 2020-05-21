package com.gnetop.ltgame.core.ui.dialog;

import android.content.Context;

import com.gnetop.ltgame.core.R;


public class GeneralDialogUtil {

    /**
     * 显示对话框
     *
     * @param context 上下文
     * @param code    状态码
     */
    public static void showActionDialog(Context context, int code) {
        switch (code) {
            case 501: {//封号
                GeneralCenterDialog mDialog = new GeneralCenterDialog(context);
                mDialog.setCancelable(false);
                mDialog.setCanceledOnTouchOutside(false);
                mDialog.setContent(context.getResources().getString(R.string.text_seal_number));
                mDialog.show();
                break;
            }
            case 502: {//封设备
                GeneralCenterDialog mDialog = new GeneralCenterDialog(context);
                mDialog.setCancelable(false);
                mDialog.setCanceledOnTouchOutside(false);
                mDialog.setContent(context.getResources().getString(R.string.text_sealing_equipment));
                mDialog.show();
                break;
            }
            case 503: {//注销
                GeneralCenterDialog mDialog = new GeneralCenterDialog(context);
                mDialog.setCancelable(false);
                mDialog.setCanceledOnTouchOutside(false);
                mDialog.setContent(context.getResources().getString(R.string.text_logout));
                mDialog.show();
                break;
            }
        }
    }
}
