package com.gnetop.ltgame.core.util;


import com.gnetop.ltgame.core.BuildConfig;

/**
 * 日志
 *
 * @author mahaiyun
 */
public class LTGameSDKLog {

    private static final String TAG = "LTGameSDK";
    /**
     * 日志开关  true为打开，false为关闭
     */
    private static boolean IS_PRINT_LOG = true;

    /**
     * 直接输出一行日志，显示类名、方法名、行数
     */
    public static void log() {
        if (IS_PRINT_LOG && BuildConfig.DEBUG) {
            StringBuffer sb = new StringBuffer();
            StackTraceElement[] stacks = new Throwable().getStackTrace();
            sb.append("class: ").append(stacks[1].getClassName())
                    .append("; method: ").append(stacks[1].getMethodName())
                    .append("; number: ").append(stacks[1].getLineNumber());
            android.util.Log.d(TAG, sb.toString());
        }
    }

    /**
     * 日志输出
     *
     * @param msg 日志
     */
    public static void logv(String msg) {
        if (IS_PRINT_LOG && BuildConfig.DEBUG) {
            android.util.Log.v(TAG, msg);
        }
    }

    /**
     * 日志输出
     *
     * @param msg 日志
     */
    public static void logd(String msg) {
        if (IS_PRINT_LOG && BuildConfig.DEBUG) {
            android.util.Log.d(TAG, msg);
        }
    }

    /**
     * 日志输出
     *
     * @param msg 日志
     */
    public static void logi(String msg) {
        if (IS_PRINT_LOG && BuildConfig.DEBUG) {
            android.util.Log.i(TAG, msg);
        }
    }

    /**
     * 日志输出
     *
     * @param msg 日志
     */
    public static void logw(String msg) {
        if (IS_PRINT_LOG && BuildConfig.DEBUG) {
            android.util.Log.w(TAG, msg);
        }
    }

    /**
     * 日志输出
     *
     * @param msg 日志
     */
    public static void loge(String msg) {
        if (IS_PRINT_LOG && BuildConfig.DEBUG) {
            android.util.Log.e(TAG, msg);
        }
    }

    /**
     * 日志输出
     *
     * @param msg 日志
     */
    public static void logwtf(String msg) {
        if (IS_PRINT_LOG && BuildConfig.DEBUG) {
            android.util.Log.wtf(TAG, msg);
        }
    }

}
