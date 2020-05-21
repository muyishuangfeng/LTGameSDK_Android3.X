package com.gnetop.ltgame.core.platform;

import android.content.Context;

public interface PlatformFactory {
    /**
     * 创建
     *
     * @param context 上下文
     * @param target  目标
     * @return 平台
     */
    IPlatform create(Context context, int target);

    /**
     * 获取目标平台
     *
     * @return 目标平台
     */
    int getPlatformTarget();

    /**
     * 检查登录平台
     *
     * @param target 目标
     * @return 是否
     */
    boolean checkLoginPlatformTarget(int target);

    /**
     * 检查支付平台
     *
     * @param target 目标
     * @return 是否
     */
    boolean checkRechargePlatformTarget(int target);
}
