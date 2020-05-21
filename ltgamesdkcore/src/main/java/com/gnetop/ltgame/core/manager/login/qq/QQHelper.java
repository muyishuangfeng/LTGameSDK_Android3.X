package com.gnetop.ltgame.core.manager.login.qq;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.gnetop.ltgame.core.base.BaseEntry;
import com.gnetop.ltgame.core.exception.LTGameError;
import com.gnetop.ltgame.core.exception.LTResultCode;
import com.gnetop.ltgame.core.impl.OnLoginStateListener;
import com.gnetop.ltgame.core.manager.lt.LoginRealizeManager;
import com.gnetop.ltgame.core.model.AccessToken;
import com.gnetop.ltgame.core.model.LoginResult;
import com.gnetop.ltgame.core.model.QQAccessToken;
import com.gnetop.ltgame.core.model.ResultModel;
import com.gnetop.ltgame.core.model.user.QQUser;
import com.gnetop.ltgame.core.platform.Target;
import com.google.gson.Gson;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

import java.lang.ref.WeakReference;

class QQHelper {

    private static final String TAG = QQHelper.class.getSimpleName();
    private String mTencentID;
    private String type;
    private boolean mIsLoginOut;
    private int mLoginTarget;
    private WeakReference<Activity> mActivityRef;
    private OnLoginStateListener mListener;
    private Tencent mTencent;
    private LoginUIListener mLoginListener;


    QQHelper(Activity activity, String mTencentID, String type, boolean mIsLoginOut,
             OnLoginStateListener mListener) {
        this.mActivityRef = new WeakReference<>(activity);
        this.mTencentID = mTencentID;
        this.mIsLoginOut = mIsLoginOut;
        this.mLoginTarget = Target.LOGIN_QQ;
        this.mListener = mListener;
        this.type = type;
        mTencent = Tencent.createInstance(mTencentID, activity);
    }


    /**
     * 登录
     */
    void loginAction() {
        if (mTencent != null) {
            if (mIsLoginOut) {
                mTencent.logout(mActivityRef.get());
                login();
            } else {
                login();
            }
        }
    }

    /**
     * 回调
     */
    void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_LOGIN ||
                requestCode == Constants.REQUEST_APPBAR) {
            mLoginListener = new LoginUIListener();
            Tencent.onActivityResultData(requestCode, resultCode, data, mLoginListener);
        }
    }

    /**
     * QQ登录回调
     */
    private class LoginUIListener implements IUiListener {

        @Override
        public void onComplete(Object object) {
            if (null == object) {
                mListener.onState(null, LoginResult.failOf(LTResultCode.STATE_CODE_PARSE_ERROR,
                        TAG + "#LoginUiListener#qq token is null"));
                return;
            }
            JSONObject jsonResponse = (JSONObject) object;
            if (jsonResponse.length() == 0) {
                mListener.onState(null, LoginResult.failOf(LTResultCode.STATE_CODE_PARSE_ERROR,
                        TAG + "#LoginUiListener#qq token is null "));
            } else {
                try {
                    QQAccessToken qqToken = new Gson().fromJson(jsonResponse.toString(), QQAccessToken.class);
                    if (qqToken == null) {
                        mListener.onState(mActivityRef.get(), LoginResult.failOf(LTResultCode.STATE_QQ_GET_TOKEN_FAILED));
                        return;
                    }
                    if (qqToken.getRet() == 100030) {
                        mTencent.reAuth(mActivityRef.get(), "all", mLoginListener);
                    } else {
                        // 保存token
                        AccessToken.saveToken(getContext(), com.gnetop.ltgame.core.common.Constants.LT_QQ_TOKEN,
                                com.gnetop.ltgame.core.common.Constants.LT_QQ_TOKEN_TIME, qqToken);
                        mTencent.setAccessToken(qqToken.getAccess_token(), qqToken.getExpires_in() + "");
                        mTencent.setOpenId(qqToken.getOpenid());
                        getUserInfo();
                    }
                } catch (Exception e) {
                    LoginRealizeManager.sendException(mActivityRef.get(),
                            LTResultCode.STATE_QQ_GET_TOKEN_ERROR, "QQ_Access_Token_Exception:" + e.getMessage(),
                            e.getMessage(), mListener);
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onError(UiError uiError) {
            LoginRealizeManager.sendException(mActivityRef.get(),
                    uiError.errorCode, "QQ_Access_Token:" + uiError.errorMessage,
                    uiError.errorDetail, mListener);
            LTGameError error = LTGameError.make(LTResultCode.STATE_CODE_PARSE_ERROR,
                    TAG + uiError.errorDetail + uiError.errorMessage + uiError.errorCode);
            mListener.onState(null, LoginResult.failOf(error));
        }

        @Override
        public void onCancel() {
            mListener.onState(null, LoginResult.cancelOf());
        }
    }

    /**
     * 登录
     */
    private void login() {
        QQAccessToken qqToken = getToken();
        if (qqToken != null) {
            mTencent.setAccessToken(qqToken.getAccess_token(), String.valueOf(qqToken.getExpires_in()));
            mTencent.setOpenId(qqToken.getOpenid());
            if (mTencent.isSessionValid()) {
                getUserInfo();
            }
        } else {
            mLoginListener = new LoginUIListener();
            mTencent.login(mActivityRef.get(), "all", mLoginListener, true);
        }
    }


    /**
     * 上下文
     */
    private Context getContext() {
        return mActivityRef.get().getApplicationContext();
    }

    /**
     * 获取token
     */
    public QQAccessToken getToken() {
        return AccessToken.getQQToken(getContext(), com.gnetop.ltgame.core.common.Constants.LT_QQ_TOKEN,
                QQAccessToken.class);
    }


    // 获取用户信息
    private void getUserInfo() {
        UserInfo info = new UserInfo(getContext(), mTencent.getQQToken());
        info.getUserInfo(new IUiListener() {
            @Override
            public void onComplete(Object object) {
                QQUser qqUserInfo = new Gson().fromJson(object.toString(), QQUser.class);
                if (qqUserInfo == null) {
                    if (mListener != null) {
                        mListener.onState(null, LoginResult.failOf(LTResultCode.STATE_CODE_PARSE_ERROR,
                                "QQ Parse Error"));
                    }
                } else {
                    qqUserInfo.setOpenId(mTencent.getOpenId());
                    if (!TextUtils.isEmpty(type)) {
                        switch (type) {
                            case com.gnetop.ltgame.core.common.Constants.QQ_LOGIN://QQ登录
                                LoginRealizeManager.qqLogin(mActivityRef.get(),
                                        qqUserInfo.getOpenId(),
                                        qqUserInfo.getUserId(),
                                        qqUserInfo.getUserNickName(),
                                        mListener);
                                mActivityRef.get().finish();
                                break;
                            case com.gnetop.ltgame.core.common.Constants.QQ_BIND://QQ绑定
                                LoginRealizeManager.bindQQ(mActivityRef.get(),
                                        qqUserInfo.getOpenId(),
                                        qqUserInfo.getUserId(),
                                        qqUserInfo.getUserNickName(),
                                        mListener);
                                mActivityRef.get().finish();
                                break;

                            case com.gnetop.ltgame.core.common.Constants.QQ_UI_TOKEN: //获取token
                                BaseEntry<ResultModel> resultModelBaseEntry = new BaseEntry<>();
                                ResultModel model = new ResultModel();
                                model.setEmali(qqUserInfo.getUserId());
                                model.setId(qqUserInfo.getOpenId());
                                model.setNickName(qqUserInfo.getUserNickName());
                                resultModelBaseEntry.setData(model);
                                mListener.onState(mActivityRef.get(), LoginResult.successOf(
                                        LTResultCode.STATE_QQ_UI_TOKEN, resultModelBaseEntry));
                                break;
                        }

                    }
                }
            }

            @Override
            public void onError(UiError e) {
                LoginRealizeManager.sendException(mActivityRef.get(),
                        e.errorCode, "QQ_Get_User_Info:" + e.errorMessage,
                        e.errorDetail, mListener);
                mListener.onState(null, LoginResult
                        .failOf(LTResultCode.STATE_QQ_GET_USER_INFO_FAILED,
                                "getUserInfo#qq获取用户信息失败"));
            }

            @Override
            public void onCancel() {
                mListener.onState(null, LoginResult.cancelOf());
            }
        });
    }

}
