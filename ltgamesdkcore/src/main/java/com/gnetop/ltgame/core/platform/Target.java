package com.gnetop.ltgame.core.platform;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Target {

    public static final int PLATFORM_QQ = 100; // qq 登录
    public static final int PLATFORM_WX = 101; // 微信登录
    public static final int PLATFORM_FACEBOOK = 102; // Facebook登录
    public static final int PLATFORM_GOOGLE = 103; // Google登录
    public static final int PLATFORM_GOOGLE_PLAY = 104; // Google支付
    public static final int PLATFORM_ONE_STORE = 105; // ONEStore支付
    public static final int PLATFORM_PHONE = 106; // 手机登录
    public static final int PLATFORM_WB = 107; // 微博登录
    public static final int PLATFORM_TWITTER = 108; // Twitter登录
    public static final int PLATFORM_WX_SCAN = 109; // 微信扫码登录



    public static final int LOGIN_QQ = 200; // qq 登录
    public static final int LOGIN_WX = 201; // 微信登录
    public static final int LOGIN_FACEBOOK = 202; // Facebook登录
    public static final int LOGIN_GOOGLE = 203; // Google登录
    public static final int RECHARGE_GOOGLE = 204; // Google支付
    public static final int RECHARGE_ONE_STORE = 205; // oneStore支付
    public static final int LOGIN_PHONE = 206; // 手机登录
    public static final int LOGIN_WB = 207; // 微博登录
    public static final int LOGIN_TWITTER = 208; // Twitter登录
    public static final int LOGIN_WX_SCAN = 209; // 微信扫码登录

    public static final int PLATFORM_GUEST = 210; // 游客登录
    public static final int LOGIN_GUEST = 211; // 游客登录
    public static final int PLATFORM_EMAIL = 212; // 邮箱登录
    public static final int LOGIN_EMAIL = 213; // 邮箱登录

    @IntDef({Target.RECHARGE_GOOGLE, Target.RECHARGE_ONE_STORE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface RechargeTarget {

    }

    @IntDef({Target.LOGIN_FACEBOOK, Target.LOGIN_GOOGLE, Target.LOGIN_QQ, Target.LOGIN_WX,
            LOGIN_WB, LOGIN_TWITTER, LOGIN_PHONE, LOGIN_WX_SCAN,LOGIN_GUEST,LOGIN_EMAIL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface LoginTarget {
    }

    @IntDef({Target.PLATFORM_FACEBOOK, Target.PLATFORM_GOOGLE, Target.PLATFORM_QQ, Target.PLATFORM_WX,
            Target.PLATFORM_GOOGLE_PLAY, Target.PLATFORM_ONE_STORE, PLATFORM_TWITTER, PLATFORM_WB,
            PLATFORM_PHONE, PLATFORM_WX_SCAN,PLATFORM_GUEST,PLATFORM_EMAIL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PlatformTarget {
    }


    public static String toDesc(int target) {
        String result = null;
        switch (target) {
            case Target.LOGIN_FACEBOOK:
                result = "Facebook登录";
                break;
            case Target.LOGIN_GOOGLE:
                result = "Google登录";
                break;
            case Target.LOGIN_WX:
                result = "微信登录";
                break;
            case Target.LOGIN_QQ:
                result = "QQ登录";
                break;
            case Target.LOGIN_PHONE:
                result = "手机登录";
                break;
            case Target.LOGIN_WB:
                result = "微博登录";
                break;
            case Target.LOGIN_TWITTER:
                result = "Twitter登录";
                break;
            case Target.LOGIN_WX_SCAN:
                result = "微信扫码登录";
                break;
            case Target.LOGIN_GUEST:
                result = "游客登录";
                break;
            case Target.LOGIN_EMAIL:
                result = "邮箱登录";
                break;

        }
        return result;


    }


}
