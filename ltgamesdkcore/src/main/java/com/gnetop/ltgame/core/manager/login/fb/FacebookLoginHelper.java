package com.gnetop.ltgame.core.manager.login.fb;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.FacebookSdkNotInitializedException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.gnetop.ltgame.core.base.BaseEntry;
import com.gnetop.ltgame.core.common.Constants;
import com.gnetop.ltgame.core.exception.LTGameError;
import com.gnetop.ltgame.core.exception.LTResultCode;
import com.gnetop.ltgame.core.impl.OnLoginStateListener;
import com.gnetop.ltgame.core.manager.lt.LoginRealizeManager;
import com.gnetop.ltgame.core.model.ResultModel;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Arrays;

public class FacebookLoginHelper {


    private static CallbackManager mFaceBookCallBack;
    private int mLoginTarget;
    private  WeakReference<Activity> mActivityRef;
    private OnLoginStateListener mListener;
    private String type;


    FacebookLoginHelper(Activity activity, String type, OnLoginStateListener listener, int loginTarget) {
        mActivityRef = new WeakReference<>(activity);
        this.mListener = listener;
        this.type = type;
        this.mLoginTarget = loginTarget;
    }


    /**
     * 初始化
     */
    void login(String appID, Context context) {
        FacebookSdk.setApplicationId(appID);
        FacebookSdk.sdkInitialize(context);
        if (!TextUtils.isEmpty(type)) {
            if (type.equals(Constants.FB_LOGIN_OUT)) {
                loginOutAction();
            } else {
                loginAction();
            }
        }


    }


    /**
     * 设置登录结果回调
     *
     * @param requestCode 请求码
     * @param resultCode  结果码
     * @param data        数据
     */
    void setOnActivityResult(int requestCode, int resultCode, Intent data) {
        if (mFaceBookCallBack != null) {
            mFaceBookCallBack.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * 登录
     */
    private void loginAction() {
        try {
            mFaceBookCallBack = CallbackManager.Factory.create();
            LoginManager.getInstance()
                    .logInWithReadPermissions(mActivityRef.get(),
                            Arrays.asList("public_profile"));
            if (mFaceBookCallBack != null) {
                LoginManager.getInstance().registerCallback(mFaceBookCallBack,
                        new FacebookCallback<LoginResult>() {
                            @Override
                            public void onSuccess(LoginResult loginResult) {
                                if (loginResult != null) {
                                    if (loginResult.getAccessToken() != null) {
                                        getUserInfo(mActivityRef.get(), loginResult.getAccessToken(), mListener);
                                    }
                                }

                            }

                            @Override
                            public void onCancel() {
                                mListener.onState(mActivityRef.get(), com.gnetop.ltgame.core.model.LoginResult.failOf
                                        (LTGameError.make(LTResultCode.STATE_FB_CANCEL_CODE)));
                            }

                            @Override
                            public void onError(FacebookException error) {
                                mListener.onState(mActivityRef.get(), com.gnetop.ltgame.core.model.LoginResult.failOf(
                                        LTGameError.make(LTResultCode.STATE_FB_LOGIN_FAILED,
                                                error.getMessage())));

                            }
                        });
            }
        } catch (FacebookSdkNotInitializedException ex) {
            LoginRealizeManager.sendException(mActivityRef.get(),
                    LTResultCode.STATE_FB_GET_TOKEN_ERROR, "FB_Get_Token:" + ex.getMessage(),
                    ex.getMessage(), mListener);
            ex.printStackTrace();
        }
    }

    /**
     * 退出登录
     */
    private void loginOutAction() {
        LoginManager.getInstance().logOut();
        mListener.onLoginOut();
        mActivityRef.get().finish();
    }

    /**
     * 获取用户信息
     */
    private void getUserInfo(final Activity activity, AccessToken accessToken,
                             final OnLoginStateListener mListener) {
        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                if (object != null) {
                    String id = object.optString("id");   //比如:1565455221565
                    String emali = object.optString("email");  //邮箱：比如：56236545@qq.com
                    String name = object.optString("name");  //比如：Zhang San
                    if (!TextUtils.isEmpty(type)) {
                        switch (type) {
                            case Constants.FB_LOGIN: //登录
                                LoginRealizeManager.facebookLogin(activity, id, emali, name, accessToken.getToken(), mListener);
                                mActivityRef.get().finish();
                                break;
                            case Constants.FB_BIND: //绑定
                                LoginRealizeManager.bindFB(activity, id, emali, name, accessToken.getToken(), mListener);
                                mActivityRef.get().finish();
                                break;
                            case Constants.FB_UI_TOKEN://获取token
                                BaseEntry<ResultModel> resultModelBaseEntry = new BaseEntry<>();
                                ResultModel model = new ResultModel();
                                model.setEmali(emali);
                                model.setId(id);
                                model.setNickName(name);
                                model.setAccessToken(accessToken.getToken());
                                resultModelBaseEntry.setData(model);
                                mListener.onState(activity, com.gnetop.ltgame.core.model.LoginResult.successOf(
                                        LTResultCode.STATE_FB_UI_TOKEN, resultModelBaseEntry));
                                break;
                        }
                    }
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,gender,birthday,email,picture,locale,updated_time,timezone,age_range,first_name,last_name");
        request.setParameters(parameters);
        request.executeAsync();
    }


}
