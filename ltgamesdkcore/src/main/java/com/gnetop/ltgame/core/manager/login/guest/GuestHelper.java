package com.gnetop.ltgame.core.manager.login.guest;


import android.app.Activity;

import com.gnetop.ltgame.core.exception.LTResultCode;
import com.gnetop.ltgame.core.impl.OnLoginStateListener;
import com.gnetop.ltgame.core.manager.lt.LoginRealizeManager;
import com.gnetop.ltgame.core.model.LoginResult;
import com.gnetop.ltgame.core.platform.Target;

import java.lang.ref.WeakReference;


public class GuestHelper {

    private int mLoginTarget;
    private static WeakReference<Activity> mActivityRef;
    private OnLoginStateListener mListener;


    GuestHelper(Activity activity,  OnLoginStateListener listener) {
        this.mActivityRef = new WeakReference<>(activity);
        this.mListener = listener;
        this.mLoginTarget = Target.LOGIN_GUEST;
    }


    void login() {
        guestLogin();
    }


    /**
     * 游客登录
     */
    private void guestLogin() {
        LoginRealizeManager.guestLogin(mActivityRef.get(), new OnLoginStateListener() {
            @Override
            public void onState(Activity activity, LoginResult result) {
                switch (result.state) {
                    case LTResultCode.STATE_GUEST_LOGIN_SUCCESS:
                        if (result.getResultModel() != null) {
                            mListener.onState(mActivityRef.get(),
                                    LoginResult.successOf(
                                            LTResultCode.STATE_GUEST_LOGIN_SUCCESS,
                                            result.getResultModel()));
                            mActivityRef.get().finish();
                        }
                        break;
                    case LTResultCode.STATE_GUEST_LOGIN_FAILED:
                        mListener.onState(mActivityRef.get(),
                                LoginResult.failOf(
                                        LTResultCode.STATE_GUEST_LOGIN_FAILED,
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
     * 绑定账户
     */
    private void bindGuest() {
        LoginRealizeManager.bindGuest(mActivityRef.get(), new OnLoginStateListener() {
            @Override
            public void onState(Activity activity, LoginResult result) {
                switch (result.state) {
                    case LTResultCode.STATE_GUEST_BIND_SUCCESS:
                        if (result.getResultModel() != null) {
                            mListener.onState(mActivityRef.get(),
                                    LoginResult.successOf(
                                            LTResultCode.STATE_GUEST_BIND_SUCCESS,
                                            result.getResultModel()));
                            mActivityRef.get().finish();
                        }
                        break;
                    case LTResultCode.STATE_GUEST_ALREADY_BIND:
                        mListener.onState(mActivityRef.get(),
                                LoginResult.failOf(
                                        LTResultCode.STATE_GUEST_ALREADY_BIND,
                                        result.getResultModel().getMsg()));
                        mActivityRef.get().finish();
                        break;
                    case LTResultCode.STATE_GUEST_BIND_FAILED:
                        mListener.onState(mActivityRef.get(),
                                LoginResult.failOf(
                                        LTResultCode.STATE_GUEST_BIND_FAILED,
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


}
