package com.gnetop.ltgame.core.manager.login.google;

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
import com.gnetop.ltgame.core.widget.activity.GoogleLoginActivity;


public class GooglePlatform extends AbsPlatform {

    private GoogleLoginHelper mGoogleHelper;

    private GooglePlatform(Context context, String appId, int target) {
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
                platform = new GooglePlatform(context, options.getLtAppId(), target);
            }
            return platform;
        }

        @Override
        public int getPlatformTarget() {
            return Target.PLATFORM_GOOGLE;
        }

        @Override
        public boolean checkLoginPlatformTarget(int target) {
            return target == Target.LOGIN_GOOGLE;
        }

        @Override
        public boolean checkRechargePlatformTarget(int target) {
            return false;
        }
    }

    @Override
    public Class getUIKitClazz() {
        return GoogleLoginActivity.class;
    }

    @Override
    public void onActivityResult(BaseActionActivity activity, int requestCode, int resultCode, Intent data) {
        super.onActivityResult(activity, requestCode, resultCode, data);
        mGoogleHelper.onActivityResult(requestCode, data);
    }


    @Override
    public void login(Activity activity, int target, LoginObject object, OnLoginStateListener listener) {
        mGoogleHelper = new GoogleLoginHelper(activity, object.getType(), listener);
        String mClientID = "";
        if (!TextUtils.isEmpty(object.getmGoogleClient())) {
            mClientID = object.getmGoogleClient();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(activity, Constants.LT_SDK_GOOGLE_CLIENT_ID))) {
            mClientID = PreferencesUtils.getString(activity, Constants.LT_SDK_GOOGLE_CLIENT_ID);
        }
        if (!TextUtils.isEmpty(mClientID)){
            mGoogleHelper.loginAction(mClientID);
        }


    }

    @Override
    public void recharge(Activity activity, int target, RechargeObject object, OnRechargeStateListener listener) {

    }
}
