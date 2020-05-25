package com.gnetop.ltgame.core.manager.recharge.gp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.gnetop.ltgame.core.base.BaseActionActivity;
import com.gnetop.ltgame.core.common.LTGameCommon;
import com.gnetop.ltgame.core.common.LTGameOptions;
import com.gnetop.ltgame.core.impl.OnRechargeStateListener;
import com.gnetop.ltgame.core.model.RechargeObject;
import com.gnetop.ltgame.core.platform.AbsPlatform;
import com.gnetop.ltgame.core.platform.IPlatform;
import com.gnetop.ltgame.core.platform.PlatformFactory;
import com.gnetop.ltgame.core.platform.Target;
import com.gnetop.ltgame.core.util.LTGameUtil;
import com.gnetop.ltgame.core.widget.activity.GooglePlayActivity;


public class GooglePlayPlatform extends AbsPlatform {

    private GooglePlayHelper mHelper;


    private GooglePlayPlatform(Context context, String appId, int target) {
        super(context, appId, target);
    }

    @Override
    public void recharge(Activity activity, int target, RechargeObject object, OnRechargeStateListener listener) {
        mHelper = new GooglePlayHelper(activity, object.getRole_number(),
                object.getServer_number(), object.getGoods_number(), object.getPayTest(),
                object.getSku(),  listener);
        mHelper.init();
    }

    @Override
    public void onActivityResult(BaseActionActivity activity, int requestCode, int resultCode, Intent data) {
    }

    @Override
    public Class getUIKitClazz() {
        return GooglePlayActivity.class;
    }


    @Override
    public void recycle() {
        if (mHelper != null) {
            mHelper.release();
        }
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
                platform = new GooglePlayPlatform(context, options.getLtAppId(), target);
            }
            return platform;
        }

        @Override
        public int getPlatformTarget() {
            return Target.PLATFORM_GOOGLE_PLAY;
        }

        @Override
        public boolean checkLoginPlatformTarget(int target) {
            return false;
        }

        @Override
        public boolean checkRechargePlatformTarget(int target) {
            return target == Target.RECHARGE_GOOGLE;
        }
    }
}
