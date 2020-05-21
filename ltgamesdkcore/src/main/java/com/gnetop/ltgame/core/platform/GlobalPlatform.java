package com.gnetop.ltgame.core.platform;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.util.SparseArray;

import com.gnetop.ltgame.core.base.BaseActionActivity;
import com.gnetop.ltgame.core.common.LTGameCommon;
import com.gnetop.ltgame.core.manager.LoginManager;
import com.gnetop.ltgame.core.manager.RechargeManager;
import com.gnetop.ltgame.core.util.LTGameUtil;


/**
 * 静态持有 platform, 在流程结束后会回收所有资源
 */
public class GlobalPlatform {

    public static final int INVALID_PARAM = -1;
    //触发操作类型（登录）
    public static final int ACTION_TYPE_LOGIN = 0;
    //触发操作类型（支付）
    public static final int ACTION_TYPE_RECHARGE = 1;

    public static final String KEY_SHARE_MEDIA_OBJ = "KEY_SHARE_MEDIA_OBJ"; // media obj key
    //触发操作类型
    public static final String KEY_ACTION_TYPE = "KEY_ACTION_TYPE";

    public static final String KEY_LOGIN_TARGET = "KEY_LOGIN_TARGET"; // login target

    private static IPlatform sIPlatform;

    /**
     * 使用 target 创建对应的 platform
     */
    public static IPlatform newPlatformByTarget(Context context, int target) {
        if (LTGameCommon.getInstance().options() == null) {
            throw new IllegalArgumentException(Target.toDesc(target) + " LTGameSdk.init() request");
        }
        IPlatform platform = newPlatformByTargetUseFactory(context, target);
        if (platform == null) {
            throw new IllegalArgumentException(Target.toDesc(target) + "  创建platform失败，请检查参数 "
                    + LTGameCommon.getInstance().options().toString());
        }
        return platform;
    }

    /**
     * 保存platform
     */
    public static void savePlatform(IPlatform platform) {
        sIPlatform = platform;
    }

    /**
     * 使用配置的工厂创建 platform
     */
    private static IPlatform newPlatformByTargetUseFactory(Context context, int target) {
        PlatformFactory platformFactory = null;
        Log.e("TAG", "===使用配置的工厂创建");
        SparseArray<PlatformFactory> factories = LTGameCommon.getInstance().getPlatformFactories();
        for (int i = 0; i < factories.size(); i++) {
            PlatformFactory factory = factories.valueAt(i);
            Log.e("TAG", factories.size() + "===" + factory.getPlatformTarget());
            if (LTGameUtil.isPlatform(factory, target)) {
                platformFactory = factory;
                break;
            }
        }
        if (platformFactory != null) {
            return platformFactory.create(context, target);
        }
        return null;
    }

    /**
     * 获取当前持有的 platform
     */
    public static IPlatform getCurrentPlatform() {
        return sIPlatform;
    }

    /**
     * 释放资源
     */
    public static void release(Activity activity) {
        if (sIPlatform != null) {
            sIPlatform.recycle();
            sIPlatform = null;
        }
        if (activity != null) {
            if (activity instanceof BaseActionActivity) {
                ((BaseActionActivity) activity).checkFinish();
            }
        }
    }

    /**
     * 触发操作
     */
    public static void dispatchAction(Activity activity, int actionType) {
        if (actionType == -1) {
            return;
        }
        switch (actionType) {
            case GlobalPlatform.ACTION_TYPE_LOGIN:
                LoginManager.actionLogin(activity);
                break;
            case GlobalPlatform.ACTION_TYPE_RECHARGE:
                RechargeManager.actionRecharge(activity);
                break;

        }
    }


}
