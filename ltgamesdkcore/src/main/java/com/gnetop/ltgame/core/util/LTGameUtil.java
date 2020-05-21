package com.gnetop.ltgame.core.util;

import android.text.TextUtils;
import android.util.Log;

import com.gnetop.ltgame.core.common.LTGameCommon;
import com.gnetop.ltgame.core.platform.PlatformFactory;


public class LTGameUtil {

    private static final String TAG = LTGameUtil.class.getSimpleName();

    public static void e(String tag, String msg) {
        if (LTGameCommon.getInstance().options().isDebug()) {
            Log.e(TAG + "|" + tag, msg);
        }
    }

    /**
     * 查询平台
     *
     * @param factory 平台工厂
     * @param target  目标平台
     */
    public static boolean isPlatform(PlatformFactory factory, int target) {
        return factory.getPlatformTarget() == target || factory.checkLoginPlatformTarget(target) ||
                factory.checkRechargePlatformTarget(target);
    }

    /**
     * 任意一个参数都不为空
     *
     * @param strs 可变参数
     * @return 是否为空
     */
    public static boolean isAnyEmpty(String... strs) {
        for (String str : strs) {
            if (str == null || TextUtils.isEmpty(str)) {
                return true;
            }
        }
        return false;
    }


    public static boolean isAnyEq(String dest, String... source) {
        for (String s : source) {
            if (s.equals(dest)) {
                return true;
            }
        }
        return false;
    }

}
