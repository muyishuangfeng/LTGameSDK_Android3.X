package com.gnetop.ltgame.core.widget.fragment;

import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.gnetop.ltgame.core.R;
import com.gnetop.ltgame.core.base.BaseFragment;
import com.gnetop.ltgame.core.common.Constants;
import com.gnetop.ltgame.core.model.LoginObject;
import com.gnetop.ltgame.core.util.PreferencesUtils;
import com.gnetop.ltgame.core.util.UrlUtils;


public class AgreementFragment extends BaseFragment implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {

    TextView mTxtAgreement, mTxtPrivacy;
    Button mBtnInto;
    AppCompatCheckBox mCkbAgreement, mCkbPrivacy;
    boolean isAgreement = false;
    boolean isPrivacy = false;
    String mAgreementUrl;
    String mPrivacyUrl;
    String googleClientID;
    String LTAppID;
    String mAdID;
    String mFacebookID;
    boolean mIsLoginOut;
    String mServerTest;
    String mQQAppID;
    String mWXAppID;
    String mWXSecret;
    String mCountryModel;


    public static AgreementFragment newInstance(LoginObject data) {
        Bundle args = new Bundle();
        AgreementFragment fragment = new AgreementFragment();
        args.putSerializable(ARG_NUMBER, data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getFragmentId() {
        return R.layout.fragment_agreement;
    }

    @Override
    protected void initView(View view) {
        isAgreement = false;
        isPrivacy = false;
        mTxtAgreement = view.findViewById(R.id.txt_agreement);
        mTxtAgreement.setOnClickListener(this);

        mTxtPrivacy = view.findViewById(R.id.txt_privacy);
        mTxtPrivacy.setOnClickListener(this);

        mCkbAgreement = view.findViewById(R.id.ckb_agreement);
        mCkbAgreement.setOnCheckedChangeListener(this);

        mCkbPrivacy = view.findViewById(R.id.ckb_privacy);
        mCkbPrivacy.setOnCheckedChangeListener(this);


        mBtnInto = view.findViewById(R.id.btn_into_game);
        mBtnInto.setOnClickListener(this);
    }

    @Override
    public void lazyLoadData() {
        Bundle args = getArguments();
        if (args != null) {
            LoginObject mData = (LoginObject) args.getSerializable(ARG_NUMBER);
            if (mData != null) {
                mAgreementUrl = mData.getAgreementUrl();
                mPrivacyUrl = mData.getPrivacyUrl();
                googleClientID = mData.getmGoogleClient();
                LTAppID = mData.getLTAppID();
                mAdID = mData.getmAdID();
                mFacebookID = mData.getFBAppID();
                mServerTest = mData.isServerTest();
                mIsLoginOut = mData.isLoginOut();
                mQQAppID = mData.getQqAppID();
                mWXAppID = mData.getWxAppID();
                mWXSecret = mData.getAppSecret();
                mCountryModel = mData.getCountryModel();
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_into_game) {
            if (isPrivacy && isAgreement) {
                if (TextUtils.isEmpty(PreferencesUtils.getString(mActivity,
                        Constants.USER_AGREEMENT_FLAT))) {
                    PreferencesUtils.putString(mActivity, Constants.USER_AGREEMENT_FLAT, "1");
                    login();
                }

            }
        } else if (view.getId() == R.id.txt_privacy) {
            if (!TextUtils.isEmpty(mPrivacyUrl)) {
                UrlUtils.getInstance().loadUrl(mActivity, mPrivacyUrl);
            }
        } else if (view.getId() == R.id.txt_agreement) {
            if (!TextUtils.isEmpty(mAgreementUrl)) {
                UrlUtils.getInstance().loadUrl(mActivity, mAgreementUrl);
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.ckb_agreement) {
            isAgreement = isChecked;

        } else if (buttonView.getId() == R.id.ckb_privacy) {
            isPrivacy = isChecked;
        }
        if (isPrivacy && isAgreement) {
            mBtnInto.setBackgroundResource(R.drawable.btn_blue_corner);
        } else {
            mBtnInto.setBackgroundResource(R.drawable.btn_corner);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isPrivacy = false;
        isAgreement = false;
    }

    /**
     * 登录
     */
    private void login() {
        LoginObject data = new LoginObject();
        data.setAgreementUrl(mAgreementUrl);
        data.setPrivacyUrl(mPrivacyUrl);
        data.setLTAppID(LTAppID);
        data.setmGoogleClient(googleClientID);
        data.setmAdID(mAdID);
        data.setServerTest(mServerTest);
        data.setFBAppID(mFacebookID);
        data.setLoginOut(mIsLoginOut);
        data.setQqAppID(mQQAppID);
        data.setAppSecret(mWXSecret);
        data.setWxAppID(mWXAppID);
        data.setCountryModel(mCountryModel);
        getProxyActivity().addFragment(LoginUIFragment.newInstance(data),
                false,
                true);

    }

}
