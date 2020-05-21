package com.gnetop.ltgame.core.manager.login.wx;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.gnetop.ltgame.core.common.Constants;
import com.gnetop.ltgame.core.common.LTGameCommon;
import com.gnetop.ltgame.core.common.LTGameOptions;
import com.gnetop.ltgame.core.impl.OnLoginStateListener;
import com.gnetop.ltgame.core.impl.OnRechargeStateListener;
import com.gnetop.ltgame.core.model.LoginObject;
import com.gnetop.ltgame.core.model.LoginResult;
import com.gnetop.ltgame.core.model.RechargeObject;
import com.gnetop.ltgame.core.platform.AbsPlatform;
import com.gnetop.ltgame.core.platform.IPlatform;
import com.gnetop.ltgame.core.platform.PlatformFactory;
import com.gnetop.ltgame.core.platform.Target;
import com.gnetop.ltgame.core.util.LTGameUtil;
import com.gnetop.ltgame.core.util.PreferencesUtils;
import com.gnetop.ltgame.core.widget.activity.WeChatActionActivity;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class WxPlatform extends AbsPlatform {

    private WxLoginHelper mWeChatLoginHelper;
    private IWXAPI mWxApi;
    private String mWxSecret;
    private String mWxAppID;

    private WxPlatform(Context context, String appId, int target, String wxSecret) {
        super(context, appId, target);
        this.mWxSecret = wxSecret;
        mWxApi = WXAPIFactory.createWXAPI(context, appId, false);
        mWxApi.registerApp(appId);
    }


    public static class Factory implements PlatformFactory {
        @Override
        public IPlatform create(Context context, int target) {
            IPlatform platform = null;
            LTGameOptions opts = LTGameCommon.getInstance().options();
            if (!LTGameUtil.isAnyEmpty(opts.getWxAppId(), opts.getWxSecretKey())) {
                platform = new WxPlatform(context, opts.getWxAppId(), target, opts.getWxSecretKey());
            }
            return platform;
        }

        @Override
        public int getPlatformTarget() {
            return Target.PLATFORM_WX;
        }


        @Override
        public boolean checkLoginPlatformTarget(int loginTarget) {
            return loginTarget == Target.LOGIN_WX || loginTarget == Target.LOGIN_WX_SCAN;
        }

        @Override
        public boolean checkRechargePlatformTarget(int target) {
            return false;
        }
    }


    @Override
    public boolean checkPlatformConfig() {
        return super.checkPlatformConfig() && !TextUtils.isEmpty(mWxSecret);
    }

    @Override
    public Class getUIKitClazz() {
        return WeChatActionActivity.class;
    }

    @Override
    public boolean isInstall(Context context) {
        return mWxApi != null && mWxApi.isWXAppInstalled();
    }

    @Override
    public void recycle() {
        mWxApi.detach();
        if (mWeChatLoginHelper != null) {
            mWeChatLoginHelper.recycle();
        }
        mWxApi = null;
    }

    @Override
    public void handleIntent(Activity activity) {
        if (activity instanceof IWXAPIEventHandler && mWxApi != null) {
            mWxApi.handleIntent(activity.getIntent(), (IWXAPIEventHandler) activity);
        }
    }

    @Override
    public void onResponse(Object resp) {
        if (!(resp instanceof BaseResp)) {
            return;
        }
        BaseResp baseResp = (BaseResp) resp;
        if (baseResp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {
            // 登录
            OnLoginStateListener listener = mWeChatLoginHelper.mListener;
            if (listener == null) {
                return;
            }
            switch (baseResp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    // 用户同意  authResp.country;  authResp.lang;  authResp.state;
                    SendAuth.Resp authResp = (SendAuth.Resp) resp;
                    String authCode = authResp.code;
                    mWeChatLoginHelper.getAccessTokenByCode(authCode);
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    // 用户拒绝授权
                    // 用户取消
                    listener.onState(null, LoginResult.cancelOf());
                    break;
            }
        }
    }


    @Override
    public void login(Activity act, int target, LoginObject obj, OnLoginStateListener listener) {
        if (obj != null) {
            if (obj.getAppSecret() != null) {
                mWxSecret = obj.getAppSecret();
            } else if (!TextUtils.isEmpty(PreferencesUtils.getString(act, Constants.LT_SDK_WX_SECRET_KEY))) {
                mWxSecret = PreferencesUtils.getString(act, Constants.LT_SDK_WX_SECRET_KEY);
            }
            if (obj.getWxAppID() != null) {
                mWxAppID = obj.getWxAppID();
            } else if (!TextUtils.isEmpty(PreferencesUtils.getString(act, Constants.LT_SDK_WX_APP_ID))) {
                mWxAppID = PreferencesUtils.getString(act, Constants.LT_SDK_WX_APP_ID);
            }
            mWeChatLoginHelper = new WxLoginHelper(act, mWxApi, target, mWxAppID, mWxSecret, obj.getType(), obj);
            mWeChatLoginHelper.requestAuthCode(listener);
        }

    }

    @Override
    public void recharge(Activity activity, int target, RechargeObject object, OnRechargeStateListener listener) {

    }


}
