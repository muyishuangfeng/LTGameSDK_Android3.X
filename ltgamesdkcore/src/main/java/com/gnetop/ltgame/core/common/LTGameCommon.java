package com.gnetop.ltgame.core.common;

import android.util.SparseArray;

import com.gnetop.ltgame.core.exception.LTGameError;
import com.gnetop.ltgame.core.exception.LTResultCode;
import com.gnetop.ltgame.core.platform.PlatformFactory;
import com.gnetop.ltgame.core.platform.Target;
import com.gnetop.ltgame.core.util.LTGameUtil;

public class LTGameCommon {


    // 配置项
    private LTGameOptions mOptions;
    //单例
    private static LTGameCommon sInstance;
    // platform factory
    private static SparseArray<PlatformFactory> mPlatformFactories;
    private static final String TAG = LTGameCommon.class.getSimpleName();

    private LTGameCommon(){}

    /**
     * 单例
     */
    public static LTGameCommon getInstance(){
        if (sInstance==null){
            synchronized (LTGameCommon.class){
                if (sInstance==null){
                    sInstance=new LTGameCommon();
                }
            }
        }
        return sInstance;
    }



    /**
     * 初始化
     */
    public  void init(LTGameOptions options) {
        mOptions = options;
        mPlatformFactories = new SparseArray<>();
        // Google平台
        if (mOptions.isGoogleEnable()) {
            addPlatform(Target.PLATFORM_GOOGLE, "com.gnetop.ltgame.core.manager.login.google.GooglePlatform$Factory");
        }
        // Facebook平台
        if (mOptions.isFBEnable()) {
            addPlatform(Target.PLATFORM_FACEBOOK, "com.gnetop.ltgame.core.manager.login.fb.FacebookPlatform$Factory");
        }
        // GooglePlay平台
        if (mOptions.isGPEnable()) {
            addPlatform(Target.PLATFORM_GOOGLE_PLAY, "com.gnetop.ltgame.core.manager.recharge.gp.GooglePlayPlatform$Factory");
        }
        // oneStore平台
        if (mOptions.isOneStoreEnable()) {
            addPlatform(Target.PLATFORM_ONE_STORE, "com.gnetop.ltgame.core.manager.recharge.one.OneStorePlatform$Factory");
        }
        // 邮箱平台
        if (mOptions.isEmailEnable()) {
            addPlatform(Target.PLATFORM_EMAIL, "com.gnetop.ltgame.core.manager.login.email.EmailPlatform$Factory");
        }
        // QQ平台
        if (mOptions.isQQEnable()) {
            addPlatform(Target.PLATFORM_QQ, "com.gnetop.ltgame.core.manager.login.qq.QQPlatform$Factory");
        }
        // 微信平台
        if (mOptions.isWeChatEnable()) {
            addPlatform(Target.PLATFORM_WX, "com.gnetop.ltgame.core.manager.login.wx.WxPlatform$Factory");
        }
        // 游客登录
        if (mOptions.isGuestEnable()) {
            addPlatform(Target.PLATFORM_GUEST, "com.gnetop.ltgame.core.manager.login.guest.GuestPlatform$Factory");
        }

    }

    /**
     * 获取配置项
     */
    public LTGameOptions options() {
        if (mOptions == null) {
            throw LTGameError.make(LTResultCode.STATE_SDK_INIT_ERROR);
        }
        return mOptions;
    }

    /**
     * 获取构建工厂
     */
    public  SparseArray<PlatformFactory> getPlatformFactories() {
        return mPlatformFactories;
    }


    /**
     * 添加 platform
     *
     * @param factory 平台工厂
     */
    private  void addPlatform(PlatformFactory factory) {
        mPlatformFactories.append(factory.getPlatformTarget(), factory);
    }

    /**
     * 添加平台
     *
     * @param target 目标平台
     */
    private  void addPlatform(int target, String factoryClazz) {
        try {
            Object instance = Class.forName(factoryClazz).newInstance();
            if (instance instanceof PlatformFactory) {
                addPlatform((PlatformFactory) instance);
                LTGameUtil.e(TAG, "注册平台 " + target + " ," + instance.getClass().getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
