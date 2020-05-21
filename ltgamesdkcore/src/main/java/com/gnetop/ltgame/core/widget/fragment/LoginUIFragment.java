package com.gnetop.ltgame.core.widget.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
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


public class LoginUIFragment extends BaseFragment implements View.OnClickListener {

    LinearLayout mLytGoogle, mLytFaceBook, mLytQQ, mLytWX, mLytHome, mLytAbroad;
    TextView mTxtGuest, mTxtHomeGuest;
    TextView mTxtEmail, mTxtHomeEmail;
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
    LoginObject mData;
    private OnLoginStateListener mListener;
    GeneralCenterDialog mDialog;


    public static LoginUIFragment newInstance(LoginObject data) {
        Bundle args = new Bundle();
        LoginUIFragment fragment = new LoginUIFragment();
        args.putSerializable(ARG_NUMBER, data);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected int getFragmentId() {
        return R.layout.fragment_loign;
    }

    @Override
    protected void initView(View view) {
        mDialog = new GeneralCenterDialog(mActivity);

        mLytGoogle = view.findViewById(R.id.lyt_login_google);
        mLytGoogle.setOnClickListener(this);

        mTxtGuest = view.findViewById(R.id.txt_abroad_visitor);
        mTxtGuest.setOnClickListener(this);

        mTxtEmail = view.findViewById(R.id.txt_abroad_email);
        mTxtEmail.setOnClickListener(this);

        mTxtHomeGuest = view.findViewById(R.id.txt_home_visitor);
        mTxtHomeGuest.setOnClickListener(this);

        mTxtHomeEmail = view.findViewById(R.id.txt_home_email);
        mTxtHomeEmail.setOnClickListener(this);

        mLytFaceBook = view.findViewById(R.id.lyt_login_facebook);
        mLytFaceBook.setOnClickListener(this);

        mLytWX = view.findViewById(R.id.lyt_login_wx);
        mLytWX.setOnClickListener(this);

        mLytQQ = view.findViewById(R.id.lyt_login_qq);
        mLytQQ.setOnClickListener(this);

        mLytHome = view.findViewById(R.id.lyt_home);

        mLytAbroad = view.findViewById(R.id.lyt_abroad);
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

                Log.e("TAG","===LoginUIFragment========"+mData.toString());

                if (mCountryModel != null) {
                    switch (mCountryModel) {
                        case Constants.LT_SDK_COUNTRY_ABROAD://国外
                            mLytQQ.setVisibility(View.GONE);
                            mLytWX.setVisibility(View.GONE);
                            mLytHome.setVisibility(View.GONE);
                            mLytGoogle.setVisibility(View.VISIBLE);
                            mLytFaceBook.setVisibility(View.VISIBLE);
                            mLytAbroad.setVisibility(View.VISIBLE);

                            break;
                        case Constants.LT_SDK_COUNTRY_HOME://国内
                            mLytQQ.setVisibility(View.VISIBLE);
                            mLytWX.setVisibility(View.VISIBLE);
                            mLytHome.setVisibility(View.VISIBLE);
                            mLytGoogle.setVisibility(View.GONE);
                            mLytFaceBook.setVisibility(View.GONE);
                            mLytAbroad.setVisibility(View.GONE);

                            break;
                    }
                }

            }
        }
        initData();

    }

    @Override
    public void onClick(View view) {
        int resID = view.getId();
        if (resID == R.id.lyt_login_facebook) {//facebook
            LoginUIManager.getInstance().getFBInfo(mActivity, mData, mListener);
        } else if (resID == R.id.lyt_login_google) {//google
            LoginUIManager.getInstance().getGoogleInfo(mActivity, mData, mListener);
        } else if (resID == R.id.txt_abroad_visitor || resID == R.id.txt_home_visitor) {//游客登录
            if (!TextUtils.isEmpty(PreferencesUtils.getString(mActivity, Constants.USER_BIND_FLAG)) &&
                    TextUtils.equals(PreferencesUtils.getString(mActivity, Constants.USER_BIND_FLAG), "YES")) {//游客登录过
                LoginUIManager.getInstance().guestLogin(mActivity, mData, mListener);
            } else {
                guestLogin();
            }
        } else if (resID == R.id.txt_abroad_email || resID == R.id.txt_home_email) {//邮箱登录
            emailLogin();
        } else if (resID == R.id.lyt_login_qq) {//QQ登录
            LoginUIManager.getInstance().getQQInfo(mActivity, mData, mListener);
        } else if (resID == R.id.lyt_login_wx) {//微信登录
            LoginUIManager.getInstance().getWXInfo(mActivity, mData, mListener);
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
                    case LTResultCode.STATE_GOOGLE_UI_TOKEN: //google获取信息
                        if (result.getResultModel() != null) {
                            showDialog(getResources().getString(R.string.text_loading));
                            LoginUIManager.getInstance().googleLogin(
                                    mActivity,
                                    result.getResultModel().getData().getId(),
                                    result.getResultModel().getData().getEmali(),
                                    result.getResultModel().getData().getNickName(),
                                    result.getResultModel().getData().getAccessToken(),
                                    mListener);
                        }

                        break;
                    case LTResultCode.STATE_FB_UI_TOKEN: //Facebook获取信息
                        if (result.getResultModel() != null) {
                            showDialog(getResources().getString(R.string.text_loading));
                            LoginUIManager.getInstance().fbLogin(mActivity,
                                    result.getResultModel().getData().getId(),
                                    result.getResultModel().getData().getEmali(),
                                    result.getResultModel().getData().getNickName(),
                                    result.getResultModel().getData().getAccessToken(),
                                    mListener);
                        }

                        break;
                    case LTResultCode.STATE_WX_UI_TOKEN: //微信获取信息
                        if (result.getResultModel() != null) {
                            showDialog(getResources().getString(R.string.text_loading));
                            LoginUIManager.getInstance().wxLogin(mActivity,
                                    result.getResultModel().getData().getId(),
                                    result.getResultModel().getData().getEmali(),
                                    result.getResultModel().getData().getNickName(),
                                    mListener);
                        }

                        break;
                    case LTResultCode.STATE_QQ_UI_TOKEN: //QQ获取信息
                        if (result.getResultModel() != null) {
                            showDialog(getResources().getString(R.string.text_loading));
                            LoginUIManager.getInstance().qqLogin(mActivity,
                                    result.getResultModel().getData().getId(),
                                    result.getResultModel().getData().getEmali(),
                                    result.getResultModel().getData().getNickName(),
                                    mListener);
                        }

                        break;
                    case LTResultCode.STATE_GOOGLE_LOGIN_FAILED: //google登录失败
                        LoginUIManager.getInstance().setResultFailed(activity,
                                LTResultCode.STATE_GOOGLE_LOGIN_FAILED,
                                result.getResultModel().getMsg());
                        dismissDialog();
                        break;
                    case LTResultCode.STATE_FB_LOGIN_FAILED: //Facebook登录失败
                        LoginUIManager.getInstance().setResultFailed(activity,
                                LTResultCode.STATE_FB_LOGIN_FAILED,
                                result.getResultModel().getMsg());
                        dismissDialog();
                        break;
                    case LTResultCode.STATE_QQ_LOGIN_FAILED: //QQ登录失败
                        LoginUIManager.getInstance().setResultFailed(activity,
                                LTResultCode.STATE_QQ_LOGIN_FAILED,
                                result.getResultModel().getMsg());
                        dismissDialog();
                        break;
                    case LTResultCode.STATE_WX_LOGIN_FAILED: //微信登录失败
                        LoginUIManager.getInstance().setResultFailed(activity,
                                LTResultCode.STATE_WX_LOGIN_FAILED,
                                result.getResultModel().getMsg());
                        dismissDialog();
                        break;
                    case LTResultCode.STATE_GOOGLE_LOGIN_SUCCESS: //google登录成功
                        if (result.getResultModel() != null) {
                            LoginUIManager.getInstance().setResultSuccess(activity,
                                    LTResultCode.STATE_GOOGLE_LOGIN_SUCCESS,
                                    result.getResultModel());
                            dismissDialog();
                            getProxyActivity().finish();

                        }
                        break;
                    case LTResultCode.STATE_FB_LOGIN_SUCCESS: //facebook登录成功
                        if (result.getResultModel() != null) {
                            LoginUIManager.getInstance().setResultSuccess(activity,
                                    LTResultCode.STATE_FB_LOGIN_SUCCESS,
                                    result.getResultModel());
                            dismissDialog();
                            getProxyActivity().finish();

                        }
                        break;
                    case LTResultCode.STATE_QQ_LOGIN_SUCCESS: //QQ登录成功
                        if (result.getResultModel() != null) {
                            LoginUIManager.getInstance().setResultSuccess(activity,
                                    LTResultCode.STATE_QQ_LOGIN_SUCCESS,
                                    result.getResultModel());
                            dismissDialog();
                            getProxyActivity().finish();

                        }
                        break;
                    case LTResultCode.STATE_WX_LOGIN_SUCCESS: //微信登录成功
                        if (result.getResultModel() != null) {
                            LoginUIManager.getInstance().setResultSuccess(activity,
                                    LTResultCode.STATE_WX_LOGIN_SUCCESS,
                                    result.getResultModel());
                            dismissDialog();
                            getProxyActivity().finish();

                        }
                        break;
                    case LTResultCode.STATE_GUEST_LOGIN_SUCCESS:
                        if (result.getResultModel() != null) {
                            PreferencesUtils.init(activity);
                            PreferencesUtils.putString(activity,
                                    Constants.USER_GUEST_FLAG, "YES");
                            PreferencesUtils.putString(activity,
                                    Constants.USER_BIND_FLAG, "YES");
                            guestTurn();
                        }
                        break;
                    case LTResultCode.STATE_GUEST_LOGIN_FAILED:
                        Log.e("TAG", "STATE_GUEST_LOGIN_FAILED==");
                        break;
                }
            }

            @Override
            public void onLoginOut() {

            }
        };
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
        data.setWxAppID(mWXSecret);
        ;
        data.setCountryModel(mCountryModel);
        data.setBind(false);
        getProxyActivity().addFragment(LoginFailedFragment.newInstance(data),
                false,
                true);
    }

    /**
     * 邮箱登录
     */
    private void emailLogin() {
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
        data.setWxAppID(mWXSecret);
        data.setCountryModel(mCountryModel);
        data.setEmailType(Constants.EMAIL_LOGIN_JUMP);
        EmailLoginFragment fragment = EmailLoginFragment.newInstance(data);
        getProxyActivity().addFragment(fragment,
                false,
                true);

    }

    /**
     * 游客登录
     */
    private void guestLogin() {
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
        data.setWxAppID(mWXSecret);
        data.setCountryModel(mCountryModel);
        GuestFragment fragment = GuestFragment.newInstance(data);
        getProxyActivity().addFragment(fragment,
                false,
                true);
    }

    /**
     * 游客转正
     */
    private void guestTurn() {
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
        data.setWxAppID(mWXSecret);
        data.setCountryModel(mCountryModel);
        GuestTurnFragment fragment = GuestTurnFragment.newInstance(data);
        getProxyActivity().addFragment(fragment,
                false,
                true);
    }


}
