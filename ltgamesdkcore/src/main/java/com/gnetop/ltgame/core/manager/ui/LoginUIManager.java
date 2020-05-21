package com.gnetop.ltgame.core.manager.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.gnetop.ltgame.core.base.BaseEntry;
import com.gnetop.ltgame.core.common.Constants;
import com.gnetop.ltgame.core.common.LTGameCommon;
import com.gnetop.ltgame.core.common.LTGameOptions;
import com.gnetop.ltgame.core.exception.LTResultCode;
import com.gnetop.ltgame.core.impl.OnLoginStateListener;
import com.gnetop.ltgame.core.manager.LoginManager;
import com.gnetop.ltgame.core.manager.lt.LoginRealizeManager;
import com.gnetop.ltgame.core.model.LoginObject;
import com.gnetop.ltgame.core.model.LoginResult;
import com.gnetop.ltgame.core.model.ResultModel;
import com.gnetop.ltgame.core.net.Api;
import com.gnetop.ltgame.core.platform.Target;
import com.gnetop.ltgame.core.util.PreferencesUtils;
import com.gnetop.ltgame.core.widget.activity.LoginUIActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


/**
 * 登录工具类
 */
public class LoginUIManager {


    private volatile static LoginUIManager sInstance;
    private OnLoginStateListener mListener;
    private static final String SDK_TEST = "1";

    private LoginUIManager() {
    }


    public static LoginUIManager getInstance() {
        if (sInstance == null) {
            synchronized (LoginUIManager.class) {
                if (sInstance == null) {
                    sInstance = new LoginUIManager();
                }
            }
        }
        return sInstance;
    }


    /**
     * 登录进入
     *
     * @param activity  上下文
     * @param mListener 登录接口
     */
    public void loginIn(final Activity activity, LoginObject object,
                        final OnLoginStateListener mListener) {
        if (PreferencesUtils.getInt(Constants.USER_LT_UID, 0) != 0 ||
                !TextUtils.isEmpty(PreferencesUtils.getString(activity,
                        Constants.USER_LT_UID_KEY))) {
            if (!TextUtils.isEmpty(PreferencesUtils.getString(activity,
                    Constants.USER_GUEST_FLAG))) {
                if (PreferencesUtils.getString(activity,
                        Constants.USER_GUEST_FLAG).equals("YES")) {//是否是游客
                    login(activity, object, mListener);
                } else {
                    LoginRealizeManager.autoLogin(activity, new OnLoginStateListener() {
                        @Override
                        public void onState(Activity activity, LoginResult result) {
                            switch (result.state) {
                                case LTResultCode.STATE_AUTO_LOGIN_SUCCESS: {//成功
                                    if (result.getResultModel() != null) {
                                        mListener.onState(activity, LoginResult.successOf(
                                                LTResultCode.STATE_AUTO_LOGIN_SUCCESS,
                                                result.getResultModel()));
                                    }

                                    break;
                                }
                                case LTResultCode.STATE_AUTO_LOGIN_FAILED: {//失败
                                    login(activity, object, mListener);
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onLoginOut() {

                        }
                    });

                }
            }
        } else {
            login(activity, object, mListener);
        }
    }

    /**
     * 登出
     *
     * @param activity 上下文
     */
    public void loginOut(Activity activity,
                         LoginObject result, OnLoginStateListener mListener) {
        PreferencesUtils.init(activity);
        if (!TextUtils.isEmpty(PreferencesUtils.getString(activity,
                Constants.USER_GUEST_FLAG))) {
            if (PreferencesUtils.getString(activity,
                    Constants.USER_GUEST_FLAG).equals("NO")) {//是否是游客
                PreferencesUtils.remove(Constants.USER_LT_UID);
                PreferencesUtils.remove(Constants.USER_LT_UID_KEY);
            }
        }
        com.facebook.login.LoginManager.getInstance().logOut();
        String mAppID = "";
        if (!TextUtils.isEmpty(result.getmGoogleClient())) {
            mAppID = result.getmGoogleClient();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(activity, Constants.LT_SDK_GOOGLE_CLIENT_ID))) {
            mAppID = PreferencesUtils.getString(activity, Constants.LT_SDK_GOOGLE_CLIENT_ID);
        }
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(mAppID)
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);
        mGoogleSignInClient.signOut().addOnCompleteListener(activity, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mListener.onLoginOut();
            }
        });
    }

    /**
     * 是否登录成功
     */
    private boolean isLoginStatus(Activity activity) {
        return TextUtils.isEmpty(PreferencesUtils.getString(activity,
                Constants.USER_LT_UID)) &&
                TextUtils.isEmpty(PreferencesUtils.getString(activity,
                        Constants.USER_LT_UID_KEY));
    }

    /**
     * 登录方法
     *
     * @param activity 上下文
     */
    private void login(Activity activity, LoginObject result, OnLoginStateListener listener) {
        this.mListener = listener;
        PreferencesUtils.init(activity);
        Intent intent = new Intent(activity, LoginUIActivity.class);
        Bundle bundle = new Bundle();
        String mGoogleAppID = "";
        String mFBAppID = "";
        String mPrivacyUrl = "";
        String mAgreementUrl = "";
        String mServerTest = "";
        String LTAppID = "";
        String mQQAppID = "";
        String mWXAppID = "";
        String mWXSecret = "";
        String mCountryModel = "";
        boolean mIsLoginOut = false;
        //FB的AppID
        if (!TextUtils.isEmpty(result.getFBAppID())) {
            mFBAppID = result.getFBAppID();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(activity, Constants.LT_SDK_FB_APP_ID))) {
            mFBAppID = PreferencesUtils.getString(activity, Constants.LT_SDK_FB_APP_ID);
        }
        //隐私条款
        if (!TextUtils.isEmpty(result.getPrivacyUrl())) {
            mPrivacyUrl = result.getPrivacyUrl();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(activity, Constants.LT_SDK_PROVACY_URL))) {
            mPrivacyUrl = PreferencesUtils.getString(activity, Constants.LT_SDK_PROVACY_URL);
        }
        //用户协议
        if (!TextUtils.isEmpty(result.getAgreementUrl())) {
            mAgreementUrl = result.getAgreementUrl();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(activity, Constants.LT_SDK_AGREEMENT_URL))) {
            mAgreementUrl = PreferencesUtils.getString(activity, Constants.LT_SDK_AGREEMENT_URL);
        }
        //是否是测试服
        if (!TextUtils.isEmpty(result.isServerTest())) {
            mServerTest = result.isServerTest();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(activity, Constants.LT_SDK_SERVER_TEST_TAG))) {
            mServerTest = PreferencesUtils.getString(activity, Constants.LT_SDK_SERVER_TEST_TAG);
        }
        //Google客户端ID
        if (!TextUtils.isEmpty(result.getmGoogleClient())) {
            mGoogleAppID = result.getmGoogleClient();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(activity, Constants.LT_SDK_GOOGLE_CLIENT_ID))) {
            mGoogleAppID = PreferencesUtils.getString(activity, Constants.LT_SDK_GOOGLE_CLIENT_ID);
        }
        //乐推AppID
        if (!TextUtils.isEmpty(result.getLTAppID())) {
            LTAppID = result.getLTAppID();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(activity, Constants.LT_SDK_APP_ID))) {
            LTAppID = PreferencesUtils.getString(activity, Constants.LT_SDK_APP_ID);
        }
        //QQ的AppID
        if (!TextUtils.isEmpty(result.getQqAppID())) {
            mQQAppID = result.getQqAppID();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(activity, Constants.LT_SDK_QQ_APP_ID))) {
            mQQAppID = PreferencesUtils.getString(activity, Constants.LT_SDK_QQ_APP_ID);
        }
        //微信AppID
        if (!TextUtils.isEmpty(result.getWxAppID())) {
            mWXAppID = result.getWxAppID();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(activity, Constants.LT_SDK_WX_APP_ID))) {
            mWXAppID = PreferencesUtils.getString(activity, Constants.LT_SDK_WX_APP_ID);
        }
        //微信AppSecret
        if (!TextUtils.isEmpty(result.getAppSecret())) {
            mWXSecret = result.getAppSecret();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(activity, Constants.LT_SDK_WX_SECRET_KEY))) {
            mWXSecret = PreferencesUtils.getString(activity, Constants.LT_SDK_WX_SECRET_KEY);
        }
        //国内还是国外
        if (!TextUtils.isEmpty(result.getCountryModel())) {
            mCountryModel = result.getCountryModel();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(activity, Constants.LT_SDK_COUNTRY_MODEL))) {
            mCountryModel = PreferencesUtils.getString(activity, Constants.LT_SDK_COUNTRY_MODEL);
        }
         //是否退出登录
        if (PreferencesUtils.getBoolean(activity, Constants.LT_SDK_LOGIN_OUT_TAG, false)) {
            mIsLoginOut = PreferencesUtils.getBoolean(activity, Constants.LT_SDK_LOGIN_OUT_TAG);
        }

        bundle.putString("mServerTest", mServerTest);
        bundle.putString("mFacebookID", mFBAppID);
        bundle.putString("mAgreementUrl", mAgreementUrl);
        bundle.putString("mPrivacyUrl", mPrivacyUrl);
        bundle.putString("googleClientID", mGoogleAppID);
        bundle.putString("LTAppID", LTAppID);
        bundle.putString("mQQAppID", mQQAppID);
        bundle.putString("mWXAppID", mWXAppID);
        bundle.putString("mWXSecret", mWXSecret);
        bundle.putString("mCountryModel", mCountryModel);
        bundle.putBoolean("mIsLoginOut", mIsLoginOut);
        intent.putExtra("bundleData", bundle);
        activity.startActivity(intent);
    }


    public void setResultSuccess(Activity activity, int code, BaseEntry<ResultModel> result) {
        if (mListener != null) {
            mListener.onState(activity,
                    LoginResult.successOf(code, result));
        }
    }

    public void setResultFailed(Activity activity, int code, String msg) {
        if (mListener != null) {
            mListener.onState(activity,
                    LoginResult.failOf(code, msg));
        }
    }


    /**
     * 从google获取信息
     */
    public void getGoogleInfo(Activity context,
                              LoginObject result, OnLoginStateListener mOnLoginListener) {
        LoginObject object = new LoginObject();
        PreferencesUtils.init(context);
        String mAppID = "";
        if (!TextUtils.isEmpty(result.getFBAppID())) {
            mAppID = result.getFBAppID();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_GOOGLE_CLIENT_ID))) {
            mAppID = PreferencesUtils.getString(context, Constants.LT_SDK_GOOGLE_CLIENT_ID);
        }
        object.setmGoogleClient(mAppID);
        object.setType(Constants.GOOGLE_UI_TOKEN);
        LoginManager.login(context, Target.LOGIN_GOOGLE,
                object, mOnLoginListener);
    }


    /**
     * 从Facebook获取信息
     */
    public void getFBInfo(Activity context,
                          LoginObject result, OnLoginStateListener mOnLoginListener) {
        LoginObject object = new LoginObject();
        PreferencesUtils.init(context);
        String mAppID = "";
        if (!TextUtils.isEmpty(result.getFBAppID())) {
            mAppID = result.getFBAppID();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_FB_APP_ID))) {
            mAppID = PreferencesUtils.getString(context, Constants.LT_SDK_FB_APP_ID);
        }
        object.setFBAppID(mAppID);
        object.setType(Constants.FB_UI_TOKEN);
        LoginManager.login(context, Target.LOGIN_FACEBOOK,
                object, mOnLoginListener);

    }

    /**
     * 从QQ获取信息
     */
    public void getQQInfo(Activity context,
                          LoginObject result, OnLoginStateListener mOnLoginListener) {
        LoginObject object = new LoginObject();
        PreferencesUtils.init(context);
        String mAppID = "";
        if (!TextUtils.isEmpty(result.getQqAppID())) {
            mAppID = result.getQqAppID();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_QQ_APP_ID))) {
            mAppID = PreferencesUtils.getString(context, Constants.LT_SDK_QQ_APP_ID);
        }
        object.setQqAppID(mAppID);
        object.setType(Constants.QQ_UI_TOKEN);
        LoginManager.login(context, Target.LOGIN_QQ,
                object, mOnLoginListener);
    }

    /**
     * 从微信获取信息
     */
    public void getWXInfo(Activity context,
                          LoginObject result, OnLoginStateListener mOnLoginListener) {
        LoginObject object = new LoginObject();
        PreferencesUtils.init(context);
        String mAppID = "";
        String appSecret = "";
        if (!TextUtils.isEmpty(result.getWxAppID())) {
            mAppID = result.getQqAppID();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_WX_APP_ID))) {
            mAppID = PreferencesUtils.getString(context, Constants.LT_SDK_WX_APP_ID);
        }
        if (!TextUtils.isEmpty(result.getAppSecret())) {
            appSecret = result.getAppSecret();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_WX_SECRET_KEY))) {
            appSecret = PreferencesUtils.getString(context, Constants.LT_SDK_WX_SECRET_KEY);
        }
        object.setWxAppID(mAppID);
        object.setAppSecret(appSecret);
        object.setType(Constants.WX_UI_TOKEN);
        LoginManager.login(context, Target.LOGIN_WX,
                object, mOnLoginListener);
    }


    /**
     * 游客登录
     */
    public void guestLogin(Activity context,
                           LoginObject result, OnLoginStateListener mOnLoginListener) {
        LoginObject object = new LoginObject();
        PreferencesUtils.init(context);
        String mAppID = "";
        if (!TextUtils.isEmpty(result.getLTAppID())) {
            mAppID = result.getLTAppID();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_APP_ID))) {
            mAppID = PreferencesUtils.getString(context, Constants.LT_SDK_APP_ID);
        }
        object.setLTAppID(mAppID);
        LoginManager.login(context, Target.LOGIN_GUEST,
                object, mOnLoginListener);
    }


    /**
     * 邮箱登录
     */
    public void emailLogin(Activity context,
                           LoginObject result, OnLoginStateListener mOnLoginListener) {
        LoginObject object = new LoginObject();
        PreferencesUtils.init(context);
        String mAppID = "";
        if (!TextUtils.isEmpty(result.getLTAppID())) {
            mAppID = result.getLTAppID();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_APP_ID))) {
            mAppID = PreferencesUtils.getString(context, Constants.LT_SDK_APP_ID);
        }
        object.setLTAppID(mAppID);
        object.setType(Constants.EMAIL_LOGIN);
        object.setEmail(result.getEmail());
        object.setAuthCode(result.getAuthCode());
        LoginManager.login(context, Target.LOGIN_EMAIL,
                object, mOnLoginListener);
    }

    /**
     * 绑定邮箱
     */
    public void bindEmail(Activity context,
                          LoginObject result, OnLoginStateListener mOnLoginListener) {
        LoginObject object = new LoginObject();
        PreferencesUtils.init(context);
        String mAppID = "";
        if (!TextUtils.isEmpty(result.getLTAppID())) {
            mAppID = result.getLTAppID();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_APP_ID))) {
            mAppID = PreferencesUtils.getString(context, Constants.LT_SDK_APP_ID);
        }
        object.setLTAppID(mAppID);
        object.setType(Constants.EMAIL_BIND);
        object.setEmail(result.getEmail());
        object.setAuthCode(result.getAuthCode());
        LoginManager.login(context, Target.LOGIN_EMAIL,
                object, mOnLoginListener);
    }





    /**
     * 获取验证码
     */
    public void getAuthCode(Activity activity, String mEmail, OnLoginStateListener mListener) {
        LoginRealizeManager.getEmailAuthCode(activity, mEmail, mListener);
    }

    /**
     * Google登录
     */
    public void googleLogin(Activity activity, String bindID,
                            String account, String userName, String accessToken, OnLoginStateListener mListener) {
        LoginRealizeManager.googleLogin(activity, bindID, account, userName, accessToken, mListener);
    }

    /**
     * facebook登录
     */
    public void fbLogin(Activity activity, String bindID,
                        String account, String userName, String accessToken, OnLoginStateListener mListener) {
        LoginRealizeManager.facebookLogin(activity, bindID, account, userName, accessToken, mListener);
    }

    /**
     * QQ登录
     */
    public void qqLogin(Activity activity, String bindID,
                        String account, String userName, OnLoginStateListener mListener) {
        LoginRealizeManager.qqLogin(activity, bindID, account, userName, mListener);
    }

    /**
     * 微信登录
     */
    public void wxLogin(Activity activity, String bindID,
                        String account, String userName, OnLoginStateListener mListener) {
        LTGameOptions options = LTGameCommon.getInstance().options();
        String mLtAppID = "";
        String baseUrl = "";
        if (!TextUtils.isEmpty(options.getLtAppId())) {
            mLtAppID = options.getLtAppId();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(activity, Constants.LT_SDK_APP_ID))) {
            mLtAppID = PreferencesUtils.getString(activity, Constants.LT_SDK_APP_ID);
        }
        if (options.getISServerTest().equals(Constants.LT_SERVER_TEST)) {
            baseUrl = Api.TEST_SERVER_URL + SDK_TEST + Api.TEST_SERVER_DOMAIN;
        } else if (options.getISServerTest().equals(Constants.LT_SERVER_OFFICIAL)) {
            baseUrl = Api.FORMAL_SERVER_URL + mLtAppID + Api.FORMAL_SERVER_DOMAIN;
        }
        LoginRealizeManager.weChatLogin(activity, baseUrl, bindID, account, userName, mListener);
    }

    /**
     * 绑定Google
     */
    public void googleBind(Activity activity, String bindID,
                           String account, String userName, String accessToken, OnLoginStateListener mListener) {
        LoginRealizeManager.bindGoogle(activity, bindID, account, userName, accessToken, mListener);
    }

    /**
     * 绑定facebook
     */
    public void fbBind(Activity activity, String bindID,
                       String account, String userName, String accessToken, OnLoginStateListener mListener) {
        LoginRealizeManager.bindFB(activity, bindID, account, userName, accessToken, mListener);
    }

    /**
     * 绑定QQ
     */
    public void qqBind(Activity activity, String bindID,
                       String account, String userName, OnLoginStateListener mListener) {
        LoginRealizeManager.bindQQ(activity, bindID, account, userName, mListener);
    }

    /**
     * 绑定微信
     */
    public void wxBind(Activity activity, String bindID,
                       String account, String userName, OnLoginStateListener mListener) {
        LTGameOptions options = LTGameCommon.getInstance().options();
        String mLtAppID = "";
        String baseUrl = "";
        if (!TextUtils.isEmpty(options.getLtAppId())) {
            mLtAppID = options.getLtAppId();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(activity, Constants.LT_SDK_APP_ID))) {
            mLtAppID = PreferencesUtils.getString(activity, Constants.LT_SDK_APP_ID);
        }
        if (options.getISServerTest().equals(Constants.LT_SERVER_TEST)) {
            baseUrl = Api.TEST_SERVER_URL + SDK_TEST + Api.TEST_SERVER_DOMAIN;
        } else if (options.getISServerTest().equals(Constants.LT_SERVER_OFFICIAL)) {
            baseUrl = Api.FORMAL_SERVER_URL + mLtAppID + Api.FORMAL_SERVER_DOMAIN;
        }
        LoginRealizeManager.bindWX(activity, baseUrl, bindID, account, userName, mListener);
    }


}
