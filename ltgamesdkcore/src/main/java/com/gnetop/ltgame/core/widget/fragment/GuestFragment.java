package com.gnetop.ltgame.core.widget.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gnetop.ltgame.core.R;
import com.gnetop.ltgame.core.base.BaseFragment;
import com.gnetop.ltgame.core.common.Constants;
import com.gnetop.ltgame.core.exception.LTResultCode;
import com.gnetop.ltgame.core.impl.OnLoginStateListener;
import com.gnetop.ltgame.core.manager.ui.LoginUIManager;
import com.gnetop.ltgame.core.model.LoginObject;
import com.gnetop.ltgame.core.model.LoginResult;
import com.gnetop.ltgame.core.ui.dialog.GeneralCenterDialog;
import com.gnetop.ltgame.core.util.PreferencesUtils;


public class GuestFragment extends BaseFragment implements View.OnClickListener {

    String mAgreementUrl;
    String mPrivacyUrl;
    String googleClientID;
    String LTAppID;
    String mAdID;
    String mServerTest;
    String mFacebookID;
    String mQQAppID;
    String mWXAppID;
    String mWXSecret;
    String mCountryModel;
    boolean mIsLoginOut;
    Button mBtnSure, mBtnCancel;
    TextView mTxtTitle, mTxtTips, mTxtContent;
    RelativeLayout mRytBottom;
    LoginObject mData;
    private OnLoginStateListener mOnLoginListener;
    GeneralCenterDialog mDialog;


    public static GuestFragment newInstance(LoginObject data) {
        Bundle args = new Bundle();
        GuestFragment fragment = new GuestFragment();
        args.putSerializable(ARG_NUMBER, data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getFragmentId() {
        return R.layout.fragment_guest;
    }


    @Override
    protected void initView(View view) {
        mDialog = new GeneralCenterDialog(mActivity);
        mBtnCancel = view.findViewById(R.id.btn_guest_cancel);
        mBtnCancel.setOnClickListener(this);

        mBtnSure = view.findViewById(R.id.btn_guest_continue);
        mBtnSure.setOnClickListener(this);

        mTxtTitle = view.findViewById(R.id.txt_guest_title);
        mTxtTips = view.findViewById(R.id.txt_guest_tips);

        mTxtContent = view.findViewById(R.id.txt_guest_content);
        mRytBottom = view.findViewById(R.id.ryt_guest_bottom);
    }

    @Override
    public void lazyLoadData() {
        super.lazyLoadData();
        Bundle args = getArguments();
        if (args != null) {
            mData = (LoginObject) args.getSerializable(ARG_NUMBER);
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
        initData();

    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_guest_cancel) {//取消
            backLogin();
        } else if (view.getId() == R.id.btn_guest_continue) {//继续
            LoginUIManager.getInstance().guestLogin(mActivity, mData, mOnLoginListener);
            showDialog(getResources().getString(R.string.text_loading));
        }

    }

    /**
     * 返回
     */
    private void backLogin() {
        LoginObject data = new LoginObject();
        data.setPrivacyUrl(mPrivacyUrl);
        data.setAgreementUrl(mAgreementUrl);
        data.setLTAppID(LTAppID);
        data.setmGoogleClient(googleClientID);
        data.setmAdID(mAdID);
        data.setFBAppID(mFacebookID);
        data.setServerTest(mServerTest);
        data.setLoginOut(mIsLoginOut);
        data.setQqAppID(mQQAppID);
        data.setAppSecret(mWXSecret);
        data.setWxAppID(mWXAppID);
        data.setCountryModel(mCountryModel);
        getProxyActivity().addFragment(LoginUIFragment.newInstance(data),
                false, true);
        if (mActivity.getSupportFragmentManager().getBackStackEntryCount() > 0) {
            pop();
        }

    }


    /**
     * 登录失败
     */
    private void loginFailed() {
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
        data.setBind(false);
        getProxyActivity().addFragment(LoginFailedFragment.newInstance(data),
                false,
                true);
        if (mActivity.getSupportFragmentManager().getBackStackEntryCount() > 0) {
            pop();
        }
    }


    /**
     * 显示对话框
     */
    private void showDialog(String content) {
        if (mDialog != null && !mDialog.isShowing()) {
            mDialog.setContent(content);
            mDialog.setCancelable(false);
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.show();
        }
    }

    /**
     * 隐藏对话框
     */
    private void dismissDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismissDialog(mActivity);
        }

    }

    /**
     * 初始化数据
     */
    private void initData() {
        mOnLoginListener = new OnLoginStateListener() {
            @Override
            public void onState(Activity activity, LoginResult result) {
                switch (result.state) {
                    case LTResultCode.STATE_GUEST_LOGIN_SUCCESS:
                        if (result.getResultModel() != null) {
                            LoginUIManager.getInstance().setResultSuccess(activity,
                                    LTResultCode.STATE_GUEST_LOGIN_SUCCESS,
                                    result.getResultModel());
                            PreferencesUtils.init(activity);
                            PreferencesUtils.putString(activity,
                                    Constants.USER_GUEST_FLAG,"YES");
                            PreferencesUtils.putString(activity,
                                    Constants.USER_BIND_FLAG,"YES");
                            dismissDialog();
                            getProxyActivity().finish();

                        }
                        break;
                    case LTResultCode.STATE_GUEST_LOGIN_FAILED:
                        LoginUIManager.getInstance().setResultFailed(activity,
                                LTResultCode.STATE_GUEST_LOGIN_FAILED,
                                result.getResultModel().getMsg());
                        dismissDialog();
                        break;
                }
            }

            @Override
            public void onLoginOut() {

            }
        };
    }
}
