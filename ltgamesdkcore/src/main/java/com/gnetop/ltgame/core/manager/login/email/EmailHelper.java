package com.gnetop.ltgame.core.manager.login.email;


import android.app.Activity;
import android.text.TextUtils;

import com.gnetop.ltgame.core.common.Constants;
import com.gnetop.ltgame.core.exception.LTResultCode;
import com.gnetop.ltgame.core.impl.OnLoginStateListener;
import com.gnetop.ltgame.core.manager.lt.LoginRealizeManager;
import com.gnetop.ltgame.core.model.LoginResult;
import com.gnetop.ltgame.core.platform.Target;

import java.lang.ref.WeakReference;


public class EmailHelper {

    private int mLoginTarget;
    private WeakReference<Activity> mActivityRef;
    private OnLoginStateListener mListener;
    private String type;
    private String code;
    private String email;


    EmailHelper(Activity activity, String type, String code, String email, OnLoginStateListener listener) {
        this.mActivityRef = new WeakReference<>(activity);
        this.mListener = listener;
        this.type = type;
        this.code = code;
        this.email = email;
        this.mLoginTarget = Target.LOGIN_EMAIL;
    }


    void login() {
        if (!TextUtils.isEmpty(type)) {
            switch (type) {
                case Constants
                        .EMAIL_LOGIN: //登录
                    emailLogin();
                    break;
                case Constants
                        .EMAIL_BIND: //绑定
                    bindEmail();
                    break;
                case Constants
                        .EMAIL_GET_CODE: //获取验证码
                    getCode();
                    break;
            }
        }
    }

    /**
     * 获取验证码
     */
    public void getCode() {
        LoginRealizeManager.getEmailAuthCode(mActivityRef.get(),email, new OnLoginStateListener() {
            @Override
            public void onState(Activity activity, LoginResult result) {
                switch (result.state) {
                    case LTResultCode.STATE_EMAIL_GET_CODE_SUCCESS:
                        mListener.onState(mActivityRef.get(),
                                LoginResult.successOf(
                                        LTResultCode.STATE_EMAIL_GET_CODE_SUCCESS,
                                        result.getResultModel()));
                        mActivityRef.get().finish();
                        break;
                    case LTResultCode.STATE_EMAIL_GET_CODE_FAILED:
                        mListener.onState(mActivityRef.get(),
                                LoginResult.failOf(
                                        LTResultCode.STATE_EMAIL_GET_CODE_FAILED,
                                        result.getBaseEntry().getMsg()));
                        mActivityRef.get().finish();
                        break;
                }
            }

            @Override
            public void onLoginOut() {

            }
        });
    }


    /**
     * 邮箱登录
     */
    private void emailLogin() {
        LoginRealizeManager.emailLogin(mActivityRef.get(), email,code, new OnLoginStateListener() {
            @Override
            public void onState(Activity activity, LoginResult result) {
                switch (result.state) {
                    case LTResultCode.STATE_EMAIL_LOGIN_SUCCESS:
                        mListener.onState(mActivityRef.get(),
                                LoginResult.successOf(
                                        LTResultCode.STATE_EMAIL_LOGIN_SUCCESS,
                                        result.getResultModel()));
                        mActivityRef.get().finish();
                        break;
                    case LTResultCode.STATE_EMAIL_LOGIN_FAILED:
                        mListener.onState(mActivityRef.get(),
                                LoginResult.failOf(
                                        LTResultCode.STATE_EMAIL_LOGIN_FAILED));
                        mActivityRef.get().finish();
                        break;
                }
            }

            @Override
            public void onLoginOut() {

            }
        });
    }


    /**
     * 绑定邮箱
     */
    private void bindEmail() {
        LoginRealizeManager.bindEmail(mActivityRef.get(), email, code, new OnLoginStateListener() {
            @Override
            public void onState(Activity activity, LoginResult result) {
                switch (result.state) {
                    case LTResultCode.STATE_EMAIL_BIND_SUCCESS:
                        mListener.onState(mActivityRef.get(),
                                LoginResult.successOf(
                                        LTResultCode.STATE_EMAIL_BIND_SUCCESS,
                                        result.getResultModel()));
                        mActivityRef.get().finish();
                        break;
                    case LTResultCode.STATE_EMAIL_BIND_FAILED:
                        mListener.onState(mActivityRef.get(),
                                LoginResult.failOf(
                                        LTResultCode.STATE_EMAIL_BIND_FAILED));
                        mActivityRef.get().finish();
                        break;
                    case LTResultCode.STATE_EMAIL_ALREADY_BIND:
                        mListener.onState(mActivityRef.get(),
                                LoginResult.failOf(
                                        LTResultCode.STATE_EMAIL_ALREADY_BIND));
                        mActivityRef.get().finish();
                        break;
                }
            }

            @Override
            public void onLoginOut() {

            }
        });
    }


}
