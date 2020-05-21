package com.gnetop.ltgame.core.manager.login.email;

import android.app.Activity;
import android.content.Context;

import com.gnetop.ltgame.core.common.LTGameCommon;
import com.gnetop.ltgame.core.common.LTGameOptions;
import com.gnetop.ltgame.core.impl.OnLoginStateListener;
import com.gnetop.ltgame.core.impl.OnRechargeStateListener;
import com.gnetop.ltgame.core.model.LoginObject;
import com.gnetop.ltgame.core.model.RechargeObject;
import com.gnetop.ltgame.core.platform.AbsPlatform;
import com.gnetop.ltgame.core.platform.IPlatform;
import com.gnetop.ltgame.core.platform.PlatformFactory;
import com.gnetop.ltgame.core.platform.Target;
import com.gnetop.ltgame.core.util.LTGameUtil;
import com.gnetop.ltgame.core.widget.activity.EmailActionActivity;


public class EmailPlatform extends AbsPlatform {



    private EmailPlatform(Context context, String appId, int target) {
        super(context, appId, target);
    }

    /**
     * 工厂
     */
    public static class Factory implements PlatformFactory {


        @Override
        public IPlatform create(Context context, int target) {
            IPlatform platform = null;
            LTGameOptions options = LTGameCommon.getInstance().options();
            if (!LTGameUtil.isAnyEmpty(options.getLtAppId())) {
                platform = new EmailPlatform(context, options.getLtAppId(),  target);
            }
            return platform;
        }

        @Override
        public int getPlatformTarget() {
            return Target.PLATFORM_EMAIL;
        }

        @Override
        public boolean checkLoginPlatformTarget(int target) {
            return target == Target.LOGIN_EMAIL;
        }

        @Override
        public boolean checkRechargePlatformTarget(int target) {
            return false;
        }
    }

    @Override
    public Class getUIKitClazz() {
        return EmailActionActivity.class;
    }



    @Override
    public void login(Activity activity, int target, LoginObject object, OnLoginStateListener listener) {
        EmailHelper mGuestHelper = new EmailHelper(activity,  object.getType(), object.getAuthCode(),
                object.getEmail(),listener);
        mGuestHelper.login();
    }

    @Override
    public void recharge(Activity activity, int target, RechargeObject object, OnRechargeStateListener listener) {

    }
}
