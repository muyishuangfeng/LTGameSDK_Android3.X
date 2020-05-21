package com.gnetop.ltgame.core.manager.login.fb;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.gnetop.ltgame.core.base.BaseActionActivity;
import com.gnetop.ltgame.core.common.Constants;
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
import com.gnetop.ltgame.core.util.PreferencesUtils;
import com.gnetop.ltgame.core.widget.activity.FacebookActionActivity;


public class FacebookPlatform extends AbsPlatform {

    private FacebookLoginHelper mLoginHelper;


    private FacebookPlatform(Context context, String appId, int target) {
        super(context, appId, target);
    }


    public static class Factory implements PlatformFactory {

        @Override
        public IPlatform create(Context context, int target) {
            IPlatform platform = null;
            LTGameOptions options = LTGameCommon.getInstance().options();
            if (!LTGameUtil.isAnyEmpty(options.getLtAppId())) {
                platform = new FacebookPlatform(context, options.getLtAppId(), target);
            }
            return platform;
        }

        @Override
        public int getPlatformTarget() {
            return Target.PLATFORM_FACEBOOK;
        }

        @Override
        public boolean checkLoginPlatformTarget(int target) {
            return target == Target.LOGIN_FACEBOOK;
        }

        @Override
        public boolean checkRechargePlatformTarget(int target) {
            return false;
        }
    }

    @Override
    public Class getUIKitClazz() {
        return FacebookActionActivity.class;
    }

    @Override
    public void onActivityResult(BaseActionActivity activity, int requestCode, int resultCode, Intent data) {
        super.onActivityResult(activity, requestCode, resultCode, data);
        mLoginHelper.setOnActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void login(Activity activity, int target, LoginObject object, OnLoginStateListener listener) {
        mLoginHelper = new FacebookLoginHelper(activity, object.getType(), listener, target);
        String mAppID = "";
        if (!TextUtils.isEmpty(object.getFBAppID())) {
            mAppID = object.getFBAppID();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(activity, Constants.LT_SDK_FB_APP_ID))) {
            mAppID = PreferencesUtils.getString(activity, Constants.LT_SDK_FB_APP_ID);
        }
        if (!TextUtils.isEmpty(mAppID)) {
            mLoginHelper.login(mAppID, activity);
        }

    }

    @Override
    public void recharge(Activity activity, int target, RechargeObject object, OnRechargeStateListener listener) {

    }
}
