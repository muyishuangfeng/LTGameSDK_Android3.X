package com.gnetop.ltgame.core.manager.login.wx;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.gnetop.ltgame.core.base.BaseEntry;
import com.gnetop.ltgame.core.common.Constants;
import com.gnetop.ltgame.core.common.LTGameCommon;
import com.gnetop.ltgame.core.common.LTGameOptions;
import com.gnetop.ltgame.core.exception.LTResultCode;
import com.gnetop.ltgame.core.impl.OnLoginStateListener;
import com.gnetop.ltgame.core.impl.OnWeChatAccessTokenListener;
import com.gnetop.ltgame.core.manager.lt.LoginRealizeManager;
import com.gnetop.ltgame.core.model.AccessToken;
import com.gnetop.ltgame.core.model.AuthWXModel;
import com.gnetop.ltgame.core.model.LoginObject;
import com.gnetop.ltgame.core.model.LoginResult;
import com.gnetop.ltgame.core.model.ResultModel;
import com.gnetop.ltgame.core.model.WeChatAccessToken;
import com.gnetop.ltgame.core.model.user.WXUser;
import com.gnetop.ltgame.core.net.Api;
import com.gnetop.ltgame.core.platform.Target;
import com.gnetop.ltgame.core.util.FileUtil;
import com.gnetop.ltgame.core.util.PreferencesUtils;
import com.tencent.mm.opensdk.diffdev.DiffDevOAuthFactory;
import com.tencent.mm.opensdk.diffdev.IDiffDevOAuth;
import com.tencent.mm.opensdk.diffdev.OAuthErrCode;
import com.tencent.mm.opensdk.diffdev.OAuthListener;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

import static com.gnetop.ltgame.core.net.Api.WX_BASE_URL;

public class WxLoginHelper {


    private static final String SDK_TEST = "1";
    private static final String TAG = WxLoginHelper.class.getSimpleName();

    private int mLoginTarget;
    private IWXAPI mIWXAPI;
    private String mAppId;
    private String mSecretKey;
    private String mAuthCode;
    private WeakReference<Activity> mActivityRef;
    private LoginObject mLoginObj;
    private String mType;

    OnLoginStateListener mListener;
    private IDiffDevOAuth mDiffDevOAuth;

    WxLoginHelper(Activity act, IWXAPI iwxapi, int target, String appId, String appSecret,
                  String type,
                  LoginObject loginObj) {
        mActivityRef = new WeakReference<>(act);
        mType = type;
        mLoginObj = loginObj;
        mIWXAPI = iwxapi;
        mAppId = appId;
        mSecretKey = appSecret;
        mLoginTarget = target;
    }

    /**
     * 获取token
     */
    private WeChatAccessToken getToken() {
        return AccessToken.getWXToken(mActivityRef.get(), Constants.LT_WX_TOKEN, WeChatAccessToken.class);
    }

    /**
     * 开始登录
     */
    void requestAuthCode(OnLoginStateListener listener) {
        mListener = listener;
        // 检测本地token的机制
        WeChatAccessToken storeToken = getToken();
        if (storeToken != null && storeToken.isValid()) {
            checkAccessTokenValid(storeToken);
        } else {
            // 本地没有token, 发起请求，wxEntry将会获得code，接着获取access_token
            if (mLoginTarget == Target.LOGIN_WX_SCAN) {
                sendWxCodeAuthReq();
            } else if (mLoginTarget == Target.LOGIN_WX) {
                sendAuthReq();
            } else {
                mListener.onState(null, LoginResult.failOf(LTResultCode.STATE_WX_LOGIN_FAILED, "target 错误"));
            }
        }
    }

    /**
     * 扫码登录
     */
    private void sendWxCodeAuthReq() {
        if (mLoginObj == null) {
            mListener.onState(null,
                    LoginResult.failOf(LTResultCode.STATE_WX_SCAN_FAILED, "login scan code param"));
            return;
        }
        LoginObject obj = mLoginObj;
        if (mDiffDevOAuth != null) {
            mDiffDevOAuth.stopAuth();
            mDiffDevOAuth.removeAllListeners();
            mDiffDevOAuth.detach();
            mDiffDevOAuth = null;
        }
        mDiffDevOAuth = DiffDevOAuthFactory.getDiffDevOAuth();
        mDiffDevOAuth.auth(mAppId, obj.getScope(), obj.getNonceStr(), obj.getTimestamp(), obj.getSignature(), new OAuthListener() {
            @Override
            public void onAuthGotQrcode(String s, byte[] bytes) {
                try {
                    File file = FileUtil.saveWxCode2File(bytes);
                    if (FileUtil.isExist(file)) {
                        LoginResult result = LoginResult.stateOf(LoginResult.STATE_WX_CODE_RECEIVE);
                        result.wxCodePath = file.getAbsolutePath();
                        mListener.onState(null, result);
                    }
                    bytes = null;
                } catch (IOException e) {
                    e.printStackTrace();
                    mListener.onState(null,
                            LoginResult.failOf(
                                    LTResultCode.STATE_WX_SCAN_FAILED, "login scan code param" + e));
                }
            }

            @Override
            public void onQrcodeScanned() {
                LoginResult result = LoginResult.stateOf(LoginResult.STATE_WX_CODE_SCANNED);
                mListener.onState(null, result);
            }

            @Override
            public void onAuthFinish(OAuthErrCode oAuthErrCode, String authCode) {
                switch (oAuthErrCode) {
                    case WechatAuth_Err_OK:
                        if (LTGameCommon.getInstance().options().isWxOnlyAuthCode()) {
                            getAccessTokenByCode(authCode);
                        }
                        break;
                    case WechatAuth_Err_Cancel:
                        mListener.onState(null,
                                LoginResult.cancelOf());
                        break;
                    case WechatAuth_Err_NormalErr:
                        mListener.onState(null,
                                LoginResult.failOf(LTResultCode.STATE_WX_SCAN_FAILED, "微信扫码登录错误[NORMAL]"));
                        break;
                    case WechatAuth_Err_NetworkErr:
                        mListener.onState(null,
                                LoginResult.failOf(LTResultCode.STATE_WX_SCAN_FAILED, "微信扫码登录错误[NETWORK]"));
                        break;
                    case WechatAuth_Err_JsonDecodeErr:
                        mListener.onState(null,
                                LoginResult.failOf(LTResultCode.STATE_WX_SCAN_FAILED, "微信扫码登录错误[JSON]"));
                        break;

                    case WechatAuth_Err_Timeout:
                        mListener.onState(null,
                                LoginResult.failOf(LTResultCode.STATE_WX_SCAN_FAILED, "微信扫码登录错误[TIMEOUT]"));
                        break;
                    case WechatAuth_Err_Auth_Stopped:
                        mListener.onState(null,
                                LoginResult.failOf(LTResultCode.STATE_WX_SCAN_FAILED, "微信扫码登录错误[STOP]"));
                        break;
                }
            }
        });
    }

    /**
     * 发起申请
     */
    private void sendAuthReq() {
        Log.e(TAG, "本地没有token,发起登录");
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "carjob_wx_login";
        mIWXAPI.sendReq(req);
    }

    /**
     * 刷新token,当access_token失效时使用,使用refresh_token获取新的token
     *
     * @param token 用来放 refresh_token
     */
    private void refreshToken(final WeChatAccessToken token) {
        Log.e(TAG, "token失效，开始刷新token");
        LoginRealizeManager.refreshWXAccessToken(mActivityRef.get(), WX_BASE_URL, mAppId, token.getRefresh_token(),
                new OnWeChatAccessTokenListener<WeChatAccessToken>() {
                    @Override
                    public void onWeChatSuccess(WeChatAccessToken weChatAccessToken) {
                        if (weChatAccessToken.isNoError()) {
                            Log.e(TAG, "刷新token成功 token = " + weChatAccessToken);
                            AccessToken.saveToken(mActivityRef.get(), Constants.LT_WX_TOKEN,
                                    Constants.LT_WX_TOKEN_TIME, weChatAccessToken);
                            // 刷新完成，获取用户信息
                            getUserInfoByValidToken(weChatAccessToken);
                        } else {
                            Log.e(TAG, "code = " + weChatAccessToken.getErrcode() + "  ,msg = "
                                    + weChatAccessToken.getErrmsg());
                            sendAuthReq();
                        }
                    }

                    @Override
                    public void onWeChatFailed(String failed) {
                        mListener.onState(mActivityRef.get(), LoginResult.failOf(LTResultCode.STATE_WX_REFRESH_TOKEN_FAILED, failed));
                    }
                },mListener);

    }

    /**
     * 根据code获取access_token
     *
     * @param code code
     */
    void getAccessTokenByCode(String code) {
        mAuthCode = code;
        Log.e(TAG, "使用code获取access_token " + code);
        LoginRealizeManager.getWXAccessToken(mActivityRef.get(), WX_BASE_URL, mAppId, mSecretKey, code,
                new OnWeChatAccessTokenListener<WeChatAccessToken>() {
                    @Override
                    public void onWeChatSuccess(WeChatAccessToken weChatAccessToken) {
                        if (weChatAccessToken.isNoError()) {
                            AccessToken.saveToken(mActivityRef.get(), Constants.LT_WX_TOKEN,
                                    Constants.LT_WX_TOKEN_TIME, weChatAccessToken);
                            getUserInfoByValidToken(weChatAccessToken);
                        } else {
                            mListener.onState(mActivityRef.get(), LoginResult.failOf(LTResultCode.STATE_WX_ACCESS_TOKEN_FAILED,
                                    "#getAccessTokenByCode#获取access_token失败 code = "
                                            + weChatAccessToken.getErrcode() + "  msg = "
                                            + weChatAccessToken.getErrmsg()));
                        }
                    }

                    @Override
                    public void onWeChatFailed(String failed) {
                        mListener.onState(mActivityRef.get(), LoginResult.failOf(LTResultCode.STATE_WX_ACCESS_TOKEN_FAILED, failed));
                    }
                },mListener);
    }


    /**
     * 检测token有效性
     *
     * @param token 用来拿access_token
     */
    private void checkAccessTokenValid(final WeChatAccessToken token) {
        Log.e(TAG, "本地存了token,开始检测有效性" + token.toString());
        LoginRealizeManager.authAccessToken(mActivityRef.get(), WX_BASE_URL, token.getAccess_token(), token.getOpenid(),
                new OnWeChatAccessTokenListener<AuthWXModel>() {
                    @Override
                    public void onWeChatSuccess(AuthWXModel authWXModel) {
                        // 检测是否有效
                        Log.e(TAG, "检测token结束，结果 = " + authWXModel.toString());
                        if (authWXModel.isNoError()) {
                            // access_token有效。开始获取用户信息
                            getUserInfoByValidToken(token);
                        } else {
                            // access_token失效，刷新或者获取新的
                            refreshToken(token);
                        }
                    }

                    @Override
                    public void onWeChatFailed(String failed) {
                        mListener.onState(mActivityRef.get(), LoginResult.failOf(LTResultCode.STATE_WX_CHECK_ACCESS_TOKEN_FAILED, failed));
                    }
                },mListener);

    }

    /**
     * token是ok的，获取用户信息
     *
     * @param token 用来拿access_token
     */
    private void getUserInfoByValidToken(final WeChatAccessToken token) {
        Log.e(TAG, "access_token有效，开始获取用户信息");
        LoginRealizeManager.getWXInfo(mActivityRef.get(), WX_BASE_URL, token.getAccess_token(), token.getOpenid(),
                new OnWeChatAccessTokenListener<WXUser>() {
                    @Override
                    public void onWeChatSuccess(WXUser wxUser) {
                        Log.e(TAG, "获取到用户信息" + wxUser.toString());
                        if (wxUser.isNoError()) {
                            if (!TextUtils.isEmpty(mType)) {
                                wxLogin(wxUser);
                            }

                        } else {
                            mListener.onState(mActivityRef.get(), LoginResult.failOf(LTResultCode.STATE_WX_INFO_FAILED,
                                    "#getUserInfoByValidToken#requestAuthCode code = "
                                            + wxUser.getErrcode() + " ,msg = "
                                            + wxUser.getErrmsg()));
                        }
                    }

                    @Override
                    public void onWeChatFailed(String failed) {
                        mListener.onState(mActivityRef.get(), LoginResult.failOf(LTResultCode.STATE_WX_INFO_FAILED,
                                failed));
                    }
                },mListener);

    }


    /**
     * 回收释放
     */
    public void recycle() {
        if (mDiffDevOAuth != null) {
            mDiffDevOAuth.removeAllListeners();
            mDiffDevOAuth.stopAuth();
            mDiffDevOAuth.detach();
        }
    }


    private void wxLogin(WXUser wxUser) {
        LTGameOptions options = LTGameCommon.getInstance().options();
        String mLtAppID = "";
        String baseUrl = "";
        if (!TextUtils.isEmpty(options.getLtAppId())) {
            mLtAppID = options.getLtAppId();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(mActivityRef.get(), Constants.LT_SDK_APP_ID))) {
            mLtAppID = PreferencesUtils.getString(mActivityRef.get(), Constants.LT_SDK_APP_ID);
        }
        if (options.getISServerTest().equals(Constants.LT_SERVER_TEST)) {
            baseUrl = Api.TEST_SERVER_URL + SDK_TEST + Api.TEST_SERVER_DOMAIN;
        } else if (options.getISServerTest().equals(Constants.LT_SERVER_OFFICIAL)) {
            baseUrl = Api.FORMAL_SERVER_URL + mLtAppID + Api.FORMAL_SERVER_DOMAIN;
        }
        switch (mType) {
            case Constants.WX_LOGIN://微信登录
                LoginRealizeManager.weChatLogin(mActivityRef.get(), baseUrl, wxUser.getOpenId(),
                        wxUser.getUserId(), wxUser.getUserNickName(), mListener);
                mActivityRef.get().finish();
                break;
            case Constants.WX_BIND://微信绑定
                LoginRealizeManager.bindWX(mActivityRef.get(), baseUrl, wxUser.getOpenId(),
                        wxUser.getUserId(), wxUser.getUserNickName(), mListener);
                mActivityRef.get().finish();
                break;

            case Constants.WX_UI_TOKEN: //获取token
                BaseEntry<ResultModel> resultModelBaseEntry = new BaseEntry<>();
                ResultModel model = new ResultModel();
                model.setEmali(wxUser.getUserId());
                model.setId(wxUser.getOpenId());
                model.setNickName(wxUser.getUserNickName());
                resultModelBaseEntry.setData(model);
                mListener.onState(mActivityRef.get(), LoginResult.successOf(
                        LTResultCode.STATE_WX_UI_TOKEN, resultModelBaseEntry));
                break;
        }

    }
}
