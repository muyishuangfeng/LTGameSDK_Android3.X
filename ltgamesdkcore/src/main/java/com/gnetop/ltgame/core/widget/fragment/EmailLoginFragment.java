package com.gnetop.ltgame.core.widget.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.gnetop.ltgame.core.R;
import com.gnetop.ltgame.core.base.BaseFragment;
import com.gnetop.ltgame.core.common.Constants;
import com.gnetop.ltgame.core.exception.LTResultCode;
import com.gnetop.ltgame.core.impl.OnLoginStateListener;
import com.gnetop.ltgame.core.manager.ui.LoginUIManager;
import com.gnetop.ltgame.core.model.LoginObject;
import com.gnetop.ltgame.core.model.LoginResult;
import com.gnetop.ltgame.core.ui.CountDownButton;
import com.gnetop.ltgame.core.ui.dialog.GeneralCenterDialog;
import com.gnetop.ltgame.core.util.RegexUtil;
import com.gnetop.ltgame.core.util.ToastUtil;


/**
 * Describe: 邮箱登录
 * Author: Heaven
 * CtrateTime: 2020-02-24 14:54
 */
public class EmailLoginFragment extends BaseFragment implements View.OnClickListener {

    LoginObject mData;
    String mAgreementUrl;
    String mPrivacyUrl;
    String googleClientID;
    String LTAppID;
    String mAdID;
    String mServerTest;
    String mFacebookID;
    String mCountryModel;
    String mQQAppID;
    String mWXAppID;
    String mWXSecret;
    boolean mIsLoginOut;
    EditText mEdtEmail, mEdtCode;
    String mEmailType;
    TextView mTxtError, mTxtCancel, mTxtSure;
    CountDownButton mBtnCode;
    private OnLoginStateListener mListener;
    GeneralCenterDialog mDialog;


    public static EmailLoginFragment newInstance(LoginObject data) {
        Bundle args = new Bundle();
        EmailLoginFragment fragment = new EmailLoginFragment();
        args.putSerializable(ARG_NUMBER, data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getFragmentId() {
        return R.layout.fragment_login_email;
    }

    @Override
    protected void initView(View view) {

        mDialog=new GeneralCenterDialog(mActivity);
        mBtnCode = view.findViewById(R.id.btn_count_down);
        mBtnCode.setOnClickListener(this);

        mTxtCancel = view.findViewById(R.id.txt_guest_cancel);
        mTxtCancel.setOnClickListener(this);

        mTxtSure = view.findViewById(R.id.txt_guest_continue);
        mTxtSure.setOnClickListener(this);

        mTxtError = view.findViewById(R.id.txt_error);
        mEdtEmail = view.findViewById(R.id.edt_email);
        mEdtCode = view.findViewById(R.id.edt_ver_code);

        mBtnCode.setOnFinishListener(new CountDownButton.OnFinishListener() {
            @Override
            public void onFinish() {
                mBtnCode.setClickable(true);
                if (mTxtError.getVisibility() == View.VISIBLE) {
                    mTxtError.setVisibility(View.INVISIBLE);
                }
            }
        });

        initData();

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
                mEmailType = mData.getEmailType();
                mQQAppID = mData.getQqAppID();
                mWXAppID = mData.getWxAppID();
                mWXSecret = mData.getAppSecret();
                mCountryModel = mData.getCountryModel();
            }
        }
    }

    @Override
    public void onClick(View view) {
        int resID = view.getId();
        if (resID == R.id.btn_count_down) {//获取验证码
            if (!TextUtils.isEmpty(mEdtEmail.getText().toString())) {
                getCode(mEdtEmail.getText().toString());
                showDialog(getResources().getString(R.string.text_loading));
            }
        } else if (resID == R.id.txt_guest_cancel) {//取消
            backLogin();
        } else if (resID == R.id.txt_guest_continue) {//登录
            if (!TextUtils.isEmpty(mEdtEmail.getText().toString()) &&
                    !TextUtils.isEmpty(mEdtCode.getText().toString()) &&
                    !TextUtils.isEmpty(mEmailType)) {
                switch (mEmailType) {
                    case Constants
                            .EMAIL_LOGIN_JUMP://登录
                        emailLogin(mEdtEmail.getText().toString(),
                                mEdtCode.getText().toString());
                        showDialog(getResources().getString(R.string.text_loading));
                        break;
                    case Constants.EMAIL_BIND_JUMP://绑定
                        bindEmail(mEdtEmail.getText().toString(),
                                mEdtCode.getText().toString());
                        showDialog(getResources().getString(R.string.text_loading));
                        break;
                }

            }
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
        mListener = new OnLoginStateListener() {
            @Override
            public void onState(Activity activity, LoginResult result) {
                switch (result.state) {
                    case LTResultCode.STATE_EMAIL_GET_CODE_SUCCESS: //获取验证码成功
                        mBtnCode.setClickable(true);
                        ToastUtil.getInstance().shortToast(mActivity,
                                result.getBaseEntry().getMsg());
                        dismissDialog();
                        break;
                    case LTResultCode.STATE_EMAIL_GET_CODE_FAILED: //获取验证码失败
                        if (mTxtError.getVisibility() == View.INVISIBLE) {
                            mTxtError.setVisibility(View.VISIBLE);
                            mTxtError.setText(result.getBaseEntry().getMsg());
                        }
                        mBtnCode.setClickable(true);
                        LoginUIManager.getInstance().setResultFailed(activity,
                                LTResultCode.STATE_EMAIL_GET_CODE_FAILED,
                                result.getResultModel().getMsg());
                        dismissDialog();
                        break;
                    case LTResultCode.STATE_EMAIL_BIND_FAILED: //绑定失败
                        if (result.getResultModel().getMsg()!=null){
                            LoginUIManager.getInstance().setResultFailed(activity,
                                    LTResultCode.STATE_EMAIL_BIND_FAILED,"BindFailed");
                        }
                        dismissDialog();
                        break;
                    case LTResultCode.STATE_EMAIL_LOGIN_FAILED: //登录失败
                        LoginUIManager.getInstance().setResultFailed(activity,
                                LTResultCode.STATE_EMAIL_LOGIN_FAILED,
                                result.getResultModel().getMsg());
                        dismissDialog();
                        break;
                    case LTResultCode.STATE_EMAIL_ALREADY_BIND: //已经绑定了邮箱
                        LoginUIManager.getInstance().setResultFailed(activity,
                                LTResultCode.STATE_EMAIL_ALREADY_BIND,
                                result.getResultModel().getMsg());
                        dismissDialog();

                        break;

                    case LTResultCode.STATE_EMAIL_BIND_SUCCESS: //绑定成功
                        if (result.getResultModel() != null) {
                            LoginUIManager.getInstance().setResultSuccess(activity,
                                    LTResultCode.STATE_EMAIL_BIND_SUCCESS,
                                    result.getResultModel());
                            dismissDialog();
                            getProxyActivity().finish();

                        }
                        break;
                    case LTResultCode.STATE_EMAIL_LOGIN_SUCCESS: //登录成功
                        if (result.getResultModel() != null) {
                            LoginUIManager.getInstance().setResultSuccess(activity,
                                    LTResultCode.STATE_EMAIL_LOGIN_SUCCESS,
                                    result.getResultModel());
                            dismissDialog();
                            getProxyActivity().finish();

                        }
                        break;
                }
            }

            @Override
            public void onLoginOut() {

            }
        };
    }


    /**
     * 已经绑定
     */
    private void loginAlreadyBind() {
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
        data.setBind(true);
        getProxyActivity().addFragment(LoginFailedFragment.newInstance(data),
                false,
                true);

    }


    /**
     * 获取验证码
     */
    private void getCode(String email) {
        mBtnCode.start();
        mBtnCode.setClickable(false);
        if (!TextUtils.isEmpty(email)) {
            if (RegexUtil.isEmail(email)) {
                LoginUIManager.getInstance().getAuthCode(mActivity, email, mListener);
            }
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
    }

    /**
     * 登录
     */
    private void emailLogin(String email, String code) {
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(code)) {
            mData.setEmail(mEdtEmail.getText().toString());
            mData.setAuthCode(mEdtCode.getText().toString());
            LoginUIManager.getInstance().emailLogin(mActivity, mData, mListener);
        }
    }

    /**
     * 绑定
     */
    private void bindEmail(String email, String code) {
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(code)) {
            mData.setEmail(mEdtEmail.getText().toString());
            mData.setAuthCode(mEdtCode.getText().toString());
            LoginUIManager.getInstance().bindEmail(mActivity, mData, mListener);
        }
    }
}

