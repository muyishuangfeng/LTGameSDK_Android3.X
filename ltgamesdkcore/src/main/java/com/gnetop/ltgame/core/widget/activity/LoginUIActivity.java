package com.gnetop.ltgame.core.widget.activity;


import android.os.Bundle;
import android.text.TextUtils;

import com.gnetop.ltgame.core.R;
import com.gnetop.ltgame.core.base.BaseAppActivity;
import com.gnetop.ltgame.core.common.Constants;
import com.gnetop.ltgame.core.model.LoginObject;
import com.gnetop.ltgame.core.util.PreferencesUtils;
import com.gnetop.ltgame.core.widget.fragment.AgreementFragment;
import com.gnetop.ltgame.core.widget.fragment.LoginUIFragment;

public class LoginUIActivity extends BaseAppActivity {

    String mAgreementUrl;
    String mPrivacyUrl;
    String googleClientID;
    String LTAppID;
    String mServerTest;
    String mFacebookID;
    boolean mIsLoginOut;
    String mQQAppID;
    String mWXAppID;
    String mWXSecret;
    String mCountryModel;


    @Override
    protected int getViewId() {
        return R.layout.activity_login;
    }


    /**
     * 初始化控件
     */
    @Override
    protected void initView() {
        Bundle bundle = getIntent().getBundleExtra("bundleData");
        mAgreementUrl = bundle.getString("mAgreementUrl");
        mPrivacyUrl = bundle.getString("mPrivacyUrl");
        googleClientID = bundle.getString("googleClientID");
        LTAppID = bundle.getString("LTAppID");
        mServerTest = bundle.getString("mServerTest");
        mFacebookID = bundle.getString("mFacebookID");
        mIsLoginOut = bundle.getBoolean("mIsLoginOut");
        mQQAppID = bundle.getString("mQQAppID");
        mWXAppID = bundle.getString("mWXAppID");
        mWXSecret = bundle.getString("mWXSecret");
        mCountryModel = bundle.getString("mCountryModel");

        LoginObject data = new LoginObject();
        data.setAgreementUrl(mAgreementUrl);
        data.setPrivacyUrl(mPrivacyUrl);
        data.setmGoogleClient(googleClientID);
        data.setLTAppID(LTAppID);
        data.setServerTest(mServerTest);
        data.setFBAppID(mFacebookID);
        data.setLoginOut(mIsLoginOut);
        data.setQqAppID(mQQAppID);
        data.setAppSecret(mWXSecret);
        data.setWxAppID(mWXSecret);
        data.setCountryModel(mCountryModel);


        if (!TextUtils.isEmpty(mAgreementUrl) &&
                !TextUtils.isEmpty(mPrivacyUrl)) {
            if (TextUtils.isEmpty(PreferencesUtils.getString(this,
                    Constants.USER_AGREEMENT_FLAT))) {
                if (findFragment(AgreementFragment.class) == null) {
                    addFragment(AgreementFragment.newInstance(data),
                            false,
                            true);
                }
            } else {
                if (findFragment(LoginUIFragment.class) == null) {
                    LoginUIFragment fragment = LoginUIFragment.newInstance(data);
                    addFragment(fragment,
                            false,
                            true);
                }
            }

        }

    }

    @Override
    protected void initData() {
    }


}
