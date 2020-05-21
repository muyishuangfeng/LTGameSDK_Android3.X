package com.gnetop.ltgame.core.manager.lt;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.gnetop.ltgame.core.base.BaseEntry;
import com.gnetop.ltgame.core.common.Constants;
import com.gnetop.ltgame.core.common.LTGameCommon;
import com.gnetop.ltgame.core.common.LTGameOptions;
import com.gnetop.ltgame.core.exception.LTGameError;
import com.gnetop.ltgame.core.exception.LTResultCode;
import com.gnetop.ltgame.core.impl.OnLoginStateListener;
import com.gnetop.ltgame.core.impl.OnRechargeStateListener;
import com.gnetop.ltgame.core.impl.OnWeChatAccessTokenListener;
import com.gnetop.ltgame.core.manager.login.fb.FacebookEventManager;
import com.gnetop.ltgame.core.model.AuthWXModel;
import com.gnetop.ltgame.core.model.LoginResult;
import com.gnetop.ltgame.core.model.RechargeResult;
import com.gnetop.ltgame.core.model.ResultModel;
import com.gnetop.ltgame.core.model.WeChatAccessToken;
import com.gnetop.ltgame.core.model.user.WXUser;
import com.gnetop.ltgame.core.net.Api;
import com.gnetop.ltgame.core.net.exception.ExceptionHelper;
import com.gnetop.ltgame.core.util.AppUtil;
import com.gnetop.ltgame.core.util.DateUtil;
import com.gnetop.ltgame.core.util.MD5Util;
import com.gnetop.ltgame.core.util.PreferencesUtils;
import com.gnetop.ltgame.core.util.ToastUtil;
import com.google.gson.Gson;

import java.util.Map;
import java.util.WeakHashMap;

import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;

public class LoginRealizeManager {

    private static final int SDK_VERSION = 100;
    private static final String SDK_TEST = "1";


    /**
     * 获取服务器时间
     */
    public static void getTime(final Context context) {
        LTGameOptions options = LTGameCommon.getInstance().options();
        String mLtAppID = "";
        String mADID = "";
        String baseUrl = "";
        String mServerTest = "";
        if (!TextUtils.isEmpty(options.getLtAppId())) {
            mLtAppID = options.getLtAppId();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_APP_ID))) {
            mLtAppID = PreferencesUtils.getString(context, Constants.LT_SDK_APP_ID);
        }
        if (!TextUtils.isEmpty(options.getAdID())) {
            mADID = options.getAdID();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_DEVICE_ADID))) {
            mADID = PreferencesUtils.getString(context, Constants.LT_SDK_DEVICE_ADID);
        }
        if (!TextUtils.isEmpty(options.getISServerTest())) {
            mServerTest = options.getISServerTest();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_SERVER_TEST_TAG))) {
            mServerTest = PreferencesUtils.getString(context, Constants.LT_SDK_SERVER_TEST_TAG);
        }

        if (!TextUtils.isEmpty(mLtAppID) &&
                !TextUtils.isEmpty(mADID)) {
            long LTTime = System.currentTimeMillis() / 1000L;
            Map<String, Object> map = new WeakHashMap<>();
            map.put("dpt", 2);
            map.put("vid", SDK_VERSION);
            map.put("udid", mADID);
            map.put("adid", mADID);
            map.put("bid", AppUtil.getPackageName(context));
            if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.USER_LT_UID_KEY))) {
                map.put("ukey", PreferencesUtils.getString(context, Constants.USER_LT_UID_KEY));
            } else {
                map.put("ukey", "");
            }
            WeakHashMap<String, Object> params = new WeakHashMap<>();
            map.put("data", params);

            if (mServerTest.equals(Constants.LT_SERVER_TEST)) {
                baseUrl = Api.TEST_SERVER_URL + SDK_TEST + Api.TEST_SERVER_DOMAIN;
            } else if (mServerTest.equals(Constants.LT_SERVER_OFFICIAL)) {
                baseUrl = Api.FORMAL_SERVER_URL + mLtAppID + Api.FORMAL_SERVER_DOMAIN;
            }
            String LTToken = "";

            String finalMServerTest = mServerTest;
            Api.getInstance((Activity) context, baseUrl)
                    .getTime(AppUtil.getLanguage(), LTToken, (int) LTTime, mLtAppID, map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BaseEntry<ResultModel>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(BaseEntry<ResultModel> result) {
                            if (result != null) {
                                if (result.getCode() == 0) {
                                    long mServerTime = DateUtil.getTimeStamp(result.getData().getTime());
                                    long mInterval = DateUtil.getSystemTimeStamp() - result.getData().getTime();
                                    PreferencesUtils.putLong(Constants.LT_SDK_TIME_STAMP, mInterval);
                                    if (finalMServerTest.equals(Constants.LT_SERVER_TEST)) {
                                        Log.e("LT_SDK_TAG", "====GetServerTime:======"
                                                + result.getData().getTime() + "====" + mInterval);
                                    }
                                } else {
                                    Log.e("LT_SDK_TAG", "====GetServerTime:===FAILED===");
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {
                        }
                    });
        }
    }

    /**
     * Google登录
     *
     * @param bindID       google返回的ID
     * @param account      google的账户
     * @param userName     google返回的昵称
     * @param access_token google返回的token
     * @param mListener    接口回调
     */
    public static void googleLogin(final Context context, String bindID,
                                   String account, String userName, String access_token,
                                   final OnLoginStateListener mListener) {
        LTGameOptions options = LTGameCommon.getInstance().options();
        String mLtAppID = "";
        String mADID = "";
        String baseUrl = "";
        String mServerTest = "";
        if (!TextUtils.isEmpty(options.getLtAppId())) {
            mLtAppID = options.getLtAppId();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_APP_ID))) {
            mLtAppID = PreferencesUtils.getString(context, Constants.LT_SDK_APP_ID);
        }
        if (!TextUtils.isEmpty(options.getAdID())) {
            mADID = options.getAdID();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_DEVICE_ADID))) {
            mADID = PreferencesUtils.getString(context, Constants.LT_SDK_DEVICE_ADID);
        }
        if (!TextUtils.isEmpty(options.getISServerTest())) {
            mServerTest = options.getISServerTest();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_SERVER_TEST_TAG))) {
            mServerTest = PreferencesUtils.getString(context, Constants.LT_SDK_SERVER_TEST_TAG);
        }

        if (!TextUtils.isEmpty(mLtAppID) &&
                !TextUtils.isEmpty(mADID) &&
                !TextUtils.isEmpty(account) &&
                !TextUtils.isEmpty(bindID) &&
                !TextUtils.isEmpty(userName) &&
                !TextUtils.isEmpty(access_token)) {
            long LTTime = System.currentTimeMillis() / 1000L;
            Map<String, Object> map = new WeakHashMap<>();
            map.put("dpt", 2);
            map.put("vid", SDK_VERSION);
            map.put("udid", mADID);
            map.put("adid", mADID);
            map.put("bid", AppUtil.getPackageName(context));
            if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.USER_LT_UID_KEY))) {
                map.put("ukey", PreferencesUtils.getString(context, Constants.USER_LT_UID_KEY));
            } else {
                map.put("ukey", "");
            }
            WeakHashMap<String, Object> params = new WeakHashMap<>();
            params.put("bind_id", bindID);
            params.put("account", account);
            params.put("username", userName);
            params.put("access_token", access_token);
            map.put("data", params);
            if (mServerTest.equals(Constants.LT_SERVER_TEST)) {
                baseUrl = Api.TEST_SERVER_URL + SDK_TEST + Api.TEST_SERVER_DOMAIN;
            } else if (mServerTest.equals(Constants.LT_SERVER_OFFICIAL)) {
                baseUrl = Api.FORMAL_SERVER_URL + mLtAppID + Api.FORMAL_SERVER_DOMAIN;
            }
            String mLtToken = MD5Util.md5Decode(
                    (DateUtil.getTimeStamp(DateUtil.getSystemTimeStamp() -
                            PreferencesUtils.getLong(context, Constants.LT_SDK_TIME_STAMP)))
                            + mADID + mADID + AppUtil.getPackageName(context) + 123).substring(3, 28);

            Api.getInstance((Activity) context, baseUrl)
                    .googleLogin(AppUtil.getLanguage(), mLtToken, (int) LTTime, mLtAppID, map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BaseEntry<ResultModel>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(BaseEntry<ResultModel> result) {
                            if (result != null) {
                                if (result.getCode() == 0) {

                                    //用户的Key
                                    if (!TextUtils.isEmpty(result.getData().getUkey())) {
                                        PreferencesUtils.putString(context, Constants.USER_LT_UID_KEY,
                                                result.getData().getUkey());
                                    }
                                    //用户ID
                                    if (result.getData().getUser_id() != 0) {
                                        PreferencesUtils.putInt(context, Constants.USER_LT_UID,
                                                result.getData().getUser_id());
                                    }

                                    if (mListener != null) {
                                        BaseEntry<ResultModel> resultModelBaseEntry = new BaseEntry<>();
                                        ResultModel model = new ResultModel();
                                        model.setUkey(result.getData().getUkey());
                                        model.setUser_id(result.getData().getUser_id());
                                        resultModelBaseEntry.setData(model);
                                        mListener.onState((Activity) context, LoginResult.successOf(
                                                LTResultCode.STATE_GOOGLE_LOGIN_SUCCESS,
                                                resultModelBaseEntry));
                                    }


                                    if (result.getData().getIs_register() == 1) {
                                        FacebookEventManager.getInstance().register(context, 3);
                                    }

                                } else {
                                    sendException(context, result.getCode(), result.getMsg(),
                                            "Google Login", mListener);
                                    if (mListener != null) {
                                        mListener.onState((Activity) context, LoginResult.failOf(
                                                LTResultCode.STATE_GOOGLE_LOGIN_FAILED,
                                                result.getMsg()));
                                    }
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            mListener.onState((Activity) context,
                                    LoginResult.failOf(ExceptionHelper.handleException(e)));
                        }

                        @Override
                        public void onComplete() {
                        }
                    });
        } else {
            mListener.onState((Activity) context,
                    LoginResult.failOf(LTResultCode.STATE_CODE_PARAMETERS_ERROR,
                            LTResultCode.STATE_CODE_PARAMETERS_FAILED));
        }
    }


    /**
     * facebook登录
     *
     * @param bindID       facebook返回的id
     * @param account      facebook的账号
     * @param userName     facebook返回的昵称
     * @param access_token facebook返回的token
     * @param mListener    接口回调
     */
    public static void facebookLogin(final Context context, String bindID,
                                     String account, String userName, String access_token,
                                     final OnLoginStateListener mListener) {
        LTGameOptions options = LTGameCommon.getInstance().options();
        String mLtAppID = "";
        String mADID = "";
        String baseUrl = "";
        String mServerTest = "";
        if (!TextUtils.isEmpty(options.getLtAppId())) {
            mLtAppID = options.getLtAppId();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_APP_ID))) {
            mLtAppID = PreferencesUtils.getString(context, Constants.LT_SDK_APP_ID);
        }
        if (!TextUtils.isEmpty(options.getAdID())) {
            mADID = options.getAdID();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_DEVICE_ADID))) {
            mADID = PreferencesUtils.getString(context, Constants.LT_SDK_DEVICE_ADID);
        }
        if (!TextUtils.isEmpty(options.getISServerTest())) {
            mServerTest = options.getISServerTest();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_SERVER_TEST_TAG))) {
            mServerTest = PreferencesUtils.getString(context, Constants.LT_SDK_SERVER_TEST_TAG);
        }
        if (!TextUtils.isEmpty(mLtAppID) &&
                !TextUtils.isEmpty(mADID) &&
                !TextUtils.isEmpty(account) &&
                !TextUtils.isEmpty(bindID) &&
                !TextUtils.isEmpty(userName) &&
                !TextUtils.isEmpty(access_token)) {
            long LTTime = System.currentTimeMillis() / 1000L;

            Map<String, Object> map = new WeakHashMap<>();
            map.put("dpt", 2);
            map.put("vid", SDK_VERSION);
            map.put("udid", mADID);
            map.put("adid", mADID);
            map.put("bid", AppUtil.getPackageName(context));
            if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.USER_LT_UID_KEY))) {
                map.put("ukey", PreferencesUtils.getString(context, Constants.USER_LT_UID_KEY));
            } else {
                map.put("ukey", "");
            }
            WeakHashMap<String, Object> params = new WeakHashMap<>();
            params.put("bind_id", bindID);
            params.put("account", account);
            params.put("username", userName);
            params.put("access_token", access_token);
            map.put("data", params);

            if (mServerTest.equals(Constants.LT_SERVER_TEST)) {
                baseUrl = Api.TEST_SERVER_URL + SDK_TEST + Api.TEST_SERVER_DOMAIN;
            } else if (mServerTest.equals(Constants.LT_SERVER_OFFICIAL)) {
                baseUrl = Api.FORMAL_SERVER_URL + mLtAppID + Api.FORMAL_SERVER_DOMAIN;
            }

            String mLtToken = MD5Util.md5Decode(
                    (DateUtil.getTimeStamp(DateUtil.getSystemTimeStamp() -
                            PreferencesUtils.getLong(context, Constants.LT_SDK_TIME_STAMP)))
                            + mADID + mADID + AppUtil.getPackageName(context) + 123).substring(3, 28);

            Api.getInstance((Activity) context, baseUrl)
                    .faceBookLogin(AppUtil.getLanguage(), mLtToken, (int) LTTime, mLtAppID, map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BaseEntry<ResultModel>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(BaseEntry<ResultModel> result) {
                            if (result != null) {
                                if (result.getCode() == 0) {
                                    if (mListener != null) {
                                        BaseEntry<ResultModel> resultModelBaseEntry = new BaseEntry<>();
                                        ResultModel model = new ResultModel();
                                        model.setUkey(result.getData().getUkey());
                                        model.setUser_id(result.getData().getUser_id());
                                        resultModelBaseEntry.setData(model);
                                        mListener.onState((Activity) context, LoginResult.successOf(
                                                LTResultCode.STATE_FB_LOGIN_SUCCESS,
                                                resultModelBaseEntry));
                                    }
                                    //用户的Key
                                    if (!TextUtils.isEmpty(result.getData().getUkey())) {
                                        PreferencesUtils.putString(context, Constants.USER_LT_UID_KEY,
                                                result.getData().getUkey());
                                    }
                                    //用户ID
                                    if (result.getData().getUser_id() != 0) {
                                        PreferencesUtils.putInt(context, Constants.USER_LT_UID,
                                                result.getData().getUser_id());
                                    }

                                    if (result.getData().getIs_register() == 1) {
                                        FacebookEventManager.getInstance().register(context, 3);
                                    }

                                } else {
                                    sendException(context, result.getCode(), "Facebook_Login:" + result.getMsg(),
                                            result.getMsg(), mListener);
                                    if (mListener != null) {
                                        mListener.onState((Activity) context,
                                                LoginResult.failOf(LTResultCode.STATE_FB_LOGIN_FAILED,
                                                        result.getMsg()));
                                    }
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            mListener.onState((Activity) context,
                                    LoginResult.failOf(ExceptionHelper.handleException(e)));
                        }

                        @Override
                        public void onComplete() {
                        }
                    });
        } else {
            mListener.onState((Activity) context,
                    LoginResult.failOf(LTResultCode.STATE_CODE_PARAMETERS_ERROR,
                            LTResultCode.STATE_CODE_PARAMETERS_FAILED));
        }


    }

    /**
     * 游客登录
     */
    public static void guestLogin(Activity context, OnLoginStateListener mListener) {
        LTGameOptions options = LTGameCommon.getInstance().options();
        String mLtAppID = "";
        String mADID = "";
        String baseUrl = "";
        String mServerTest = "";
        if (!TextUtils.isEmpty(options.getLtAppId())) {
            mLtAppID = options.getLtAppId();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_APP_ID))) {
            mLtAppID = PreferencesUtils.getString(context, Constants.LT_SDK_APP_ID);
        }
        if (!TextUtils.isEmpty(options.getAdID())) {
            mADID = options.getAdID();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_DEVICE_ADID))) {
            mADID = PreferencesUtils.getString(context, Constants.LT_SDK_DEVICE_ADID);
        }
        if (!TextUtils.isEmpty(options.getISServerTest())) {
            mServerTest = options.getISServerTest();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_SERVER_TEST_TAG))) {
            mServerTest = PreferencesUtils.getString(context, Constants.LT_SDK_SERVER_TEST_TAG);
        }

        if (!TextUtils.isEmpty(mLtAppID) &&
                !TextUtils.isEmpty(mADID)) {
            long LTTime = System.currentTimeMillis() / 1000L;
            Map<String, Object> map = new WeakHashMap<>();
            map.put("dpt", 2);
            map.put("vid", SDK_VERSION);
            map.put("udid", mADID);
            map.put("adid", mADID);
            map.put("bid", AppUtil.getPackageName(context));
            if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.USER_LT_UID_KEY))) {
                map.put("ukey", PreferencesUtils.getString(context, Constants.USER_LT_UID_KEY));
            } else {
                map.put("ukey", "");
            }
            WeakHashMap<String, Object> params = new WeakHashMap<>();
            map.put("data", params);

            if (mServerTest.equals(Constants.LT_SERVER_TEST)) {
                baseUrl = Api.TEST_SERVER_URL + SDK_TEST + Api.TEST_SERVER_DOMAIN;
            } else if (mServerTest.equals(Constants.LT_SERVER_OFFICIAL)) {
                baseUrl = Api.FORMAL_SERVER_URL + mLtAppID + Api.FORMAL_SERVER_DOMAIN;
            }
            String mLtToken = MD5Util.md5Decode(
                    (DateUtil.getTimeStamp(DateUtil.getSystemTimeStamp() -
                            PreferencesUtils.getLong(context, Constants.LT_SDK_TIME_STAMP)))
                            + mADID + mADID + AppUtil.getPackageName(context) + 123).substring(3, 28);

            Api.getInstance((Activity) context, baseUrl)
                    .guestLogin(AppUtil.getLanguage(), mLtToken, (int) LTTime, mLtAppID, map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BaseEntry<ResultModel>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(BaseEntry<ResultModel> result) {
                            if (result != null) {
                                if (result.getCode() == 0) {
                                    if (mListener != null) {
                                        BaseEntry<ResultModel> resultModelBaseEntry = new BaseEntry<>();
                                        ResultModel model = new ResultModel();
                                        model.setUkey(result.getData().getUkey());
                                        model.setUser_id(result.getData().getUser_id());
                                        resultModelBaseEntry.setData(model);
                                        mListener.onState((Activity) context, LoginResult.successOf(
                                                LTResultCode.STATE_GUEST_LOGIN_SUCCESS,
                                                resultModelBaseEntry));
                                    }
                                    //用户的Key
                                    if (!TextUtils.isEmpty(result.getData().getUkey())) {
                                        PreferencesUtils.putString(context, Constants.USER_LT_UID_KEY,
                                                result.getData().getUkey());
                                    }
                                    //用户ID
                                    if (result.getData().getUser_id() != 0) {
                                        PreferencesUtils.putInt(context, Constants.USER_LT_UID,
                                                result.getData().getUser_id());
                                    }

                                    if (result.getData().getIs_register() == 1) {
                                        FacebookEventManager.getInstance().register(context, 3);
                                    }


                                } else {
                                    sendException(context, result.getCode(), "Guest_Login:" + result.getMsg(),
                                            result.getMsg(), mListener);
                                    if (mListener != null) {
                                        mListener.onState((Activity) context,
                                                LoginResult.failOf(
                                                        LTResultCode.STATE_GUEST_LOGIN_FAILED,
                                                        result.getMsg()));
                                    }
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            if (mListener != null) {
                                mListener.onState((Activity) context,
                                        LoginResult.failOf(ExceptionHelper.handleException(e)));
                            }
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            mListener.onState((Activity) context,
                    LoginResult.failOf(LTResultCode.STATE_CODE_PARAMETERS_ERROR,
                            LTResultCode.STATE_CODE_PARAMETERS_FAILED));
        }
    }


    /**
     * 邮箱登录
     */
    public static void emailLogin(final Context context, String email, String code,
                                  final OnLoginStateListener mListener) {
        LTGameOptions options = LTGameCommon.getInstance().options();
        String mLtAppID = "";
        String mADID = "";
        String baseUrl = "";
        String mServerTest = "";
        if (!TextUtils.isEmpty(options.getLtAppId())) {
            mLtAppID = options.getLtAppId();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_APP_ID))) {
            mLtAppID = PreferencesUtils.getString(context, Constants.LT_SDK_APP_ID);
        }
        if (!TextUtils.isEmpty(options.getAdID())) {
            mADID = options.getAdID();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_DEVICE_ADID))) {
            mADID = PreferencesUtils.getString(context, Constants.LT_SDK_DEVICE_ADID);
        }
        if (!TextUtils.isEmpty(options.getISServerTest())) {
            mServerTest = options.getISServerTest();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_SERVER_TEST_TAG))) {
            mServerTest = PreferencesUtils.getString(context, Constants.LT_SDK_SERVER_TEST_TAG);
        }
        if (!TextUtils.isEmpty(mLtAppID) &&
                !TextUtils.isEmpty(mADID) &&
                !TextUtils.isEmpty(email) &&
                !TextUtils.isEmpty(code)) {
            long LTTime = System.currentTimeMillis() / 1000L;
            Map<String, Object> map = new WeakHashMap<>();
            map.put("dpt", 2);
            map.put("vid", SDK_VERSION);
            map.put("udid", mADID);
            map.put("adid", mADID);
            map.put("bid", AppUtil.getPackageName(context));
            if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.USER_LT_UID_KEY))) {
                map.put("ukey", PreferencesUtils.getString(context, Constants.USER_LT_UID_KEY));
            } else {
                map.put("ukey", "");
            }
            WeakHashMap<String, Object> params = new WeakHashMap<>();
            params.put("email", email);
            params.put("code", code);
            map.put("data", params);

            if (mServerTest.equals(Constants.LT_SERVER_TEST)) {
                baseUrl = Api.TEST_SERVER_URL + SDK_TEST + Api.TEST_SERVER_DOMAIN;
            } else if (mServerTest.equals(Constants.LT_SERVER_OFFICIAL)) {
                baseUrl = Api.FORMAL_SERVER_URL + mLtAppID + Api.FORMAL_SERVER_DOMAIN;
            }

            String mLtToken = MD5Util.md5Decode(
                    (DateUtil.getTimeStamp(DateUtil.getSystemTimeStamp() -
                            PreferencesUtils.getLong(context, Constants.LT_SDK_TIME_STAMP)))
                            + mADID + mADID + AppUtil.getPackageName(context) + 123).substring(3, 28);

            Api.getInstance((Activity) context, baseUrl)
                    .emailLogin(AppUtil.getLanguage(), mLtToken, (int) LTTime, mLtAppID, map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BaseEntry<ResultModel>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(BaseEntry<ResultModel> result) {
                            if (result != null) {
                                if (result.getCode() == 0) {
                                    if (mListener != null) {
                                        BaseEntry<ResultModel> resultModelBaseEntry = new BaseEntry<>();
                                        ResultModel model = new ResultModel();
                                        model.setUkey(result.getData().getUkey());
                                        model.setUser_id(result.getData().getUser_id());
                                        resultModelBaseEntry.setData(model);
                                        mListener.onState((Activity) context, LoginResult.successOf(
                                                LTResultCode.STATE_EMAIL_LOGIN_SUCCESS,
                                                resultModelBaseEntry));
                                    }
                                    //用户的Key
                                    if (!TextUtils.isEmpty(result.getData().getUkey())) {
                                        PreferencesUtils.putString(context, Constants.USER_LT_UID_KEY,
                                                result.getData().getUkey());
                                    }
                                    //用户ID
                                    if (result.getData().getUser_id() != 0) {
                                        PreferencesUtils.putInt(context, Constants.USER_LT_UID,
                                                result.getData().getUser_id());
                                    }

                                    if (result.getData().getIs_register() == 1) {
                                        FacebookEventManager.getInstance().register(context, 3);
                                    }


                                } else {
                                    sendException(context, result.getCode(), "Email_Login:" + result.getMsg(),
                                            result.getMsg(), mListener);
                                    if (mListener != null) {
                                        mListener.onState((Activity) context, LoginResult.failOf(
                                                LTResultCode.STATE_EMAIL_LOGIN_FAILED,
                                                result.getMsg()));
                                    }
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            mListener.onState((Activity) context,
                                    LoginResult.failOf(ExceptionHelper.handleException(e)));
                        }

                        @Override
                        public void onComplete() {
                        }
                    });
        } else {
            mListener.onState((Activity) context,
                    LoginResult.failOf(LTResultCode.STATE_CODE_PARAMETERS_ERROR,
                            LTResultCode.STATE_CODE_PARAMETERS_FAILED));
        }
    }

    /**
     * QQ登录
     * <p>
     *
     * @param bindID    QQ返回的id
     * @param account   qq返回的账号
     * @param userName  qq返回的昵称
     * @param mListener 接口回调
     */
    public static void qqLogin(final Activity context, String bindID, String account,
                               String userName, final OnLoginStateListener mListener) {
        LTGameOptions options = LTGameCommon.getInstance().options();
        String mLtAppID = "";
        String mADID = "";
        String baseUrl = "";
        String mServerTest = "";
        if (!TextUtils.isEmpty(options.getLtAppId())) {
            mLtAppID = options.getLtAppId();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_APP_ID))) {
            mLtAppID = PreferencesUtils.getString(context, Constants.LT_SDK_APP_ID);
        }
        if (!TextUtils.isEmpty(options.getAdID())) {
            mADID = options.getAdID();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_DEVICE_ADID))) {
            mADID = PreferencesUtils.getString(context, Constants.LT_SDK_DEVICE_ADID);
        }
        if (!TextUtils.isEmpty(options.getISServerTest())) {
            mServerTest = options.getISServerTest();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_SERVER_TEST_TAG))) {
            mServerTest = PreferencesUtils.getString(context, Constants.LT_SDK_SERVER_TEST_TAG);
        }
        if (!TextUtils.isEmpty(mLtAppID) &&
                !TextUtils.isEmpty(mADID) &&
                !TextUtils.isEmpty(bindID) &&
                !TextUtils.isEmpty(account) &&
                !TextUtils.isEmpty(userName) &&
                !TextUtils.isEmpty(options.getAdID())) {
            long LTTime = System.currentTimeMillis() / 1000L;
            Map<String, Object> map = new WeakHashMap<>();
            map.put("dpt", 2);
            map.put("vid", SDK_VERSION);
            map.put("udid", mADID);
            map.put("adid", mADID);
            map.put("bid", AppUtil.getPackageName(context));
            if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.USER_LT_UID_KEY))) {
                map.put("ukey", PreferencesUtils.getString(context, Constants.USER_LT_UID_KEY));
            } else {
                map.put("ukey", "");
            }
            WeakHashMap<String, Object> params = new WeakHashMap<>();
            params.put("bind_id", bindID);
            params.put("account", account);
            params.put("username", userName);
            map.put("data", params);

            if (mServerTest.equals(Constants.LT_SERVER_TEST)) {
                baseUrl = Api.TEST_SERVER_URL + SDK_TEST + Api.TEST_SERVER_DOMAIN;
            } else if (mServerTest.equals(Constants.LT_SERVER_OFFICIAL)) {
                baseUrl = Api.FORMAL_SERVER_URL + mLtAppID + Api.FORMAL_SERVER_DOMAIN;
            }
            String mLtToken = MD5Util.md5Decode(
                    (DateUtil.getTimeStamp(DateUtil.getSystemTimeStamp() -
                            PreferencesUtils.getLong(context, Constants.LT_SDK_TIME_STAMP)))
                            + mADID + mADID + AppUtil.getPackageName(context) + 123).substring(3, 28);

            Api.getInstance(context, baseUrl)
                    .qqLogin(AppUtil.getLanguage(), mLtToken, (int) LTTime, mLtAppID, map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BaseEntry<ResultModel>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(BaseEntry<ResultModel> result) {
                            if (result != null) {
                                if (result.getCode() == 0) {
                                    if (mListener != null) {
                                        BaseEntry<ResultModel> resultModelBaseEntry = new BaseEntry<>();
                                        ResultModel model = new ResultModel();
                                        model.setUkey(result.getData().getUkey());
                                        model.setUser_id(result.getData().getUser_id());
                                        resultModelBaseEntry.setData(model);
                                        mListener.onState((Activity) context, LoginResult.successOf(
                                                LTResultCode.STATE_QQ_LOGIN_SUCCESS,
                                                resultModelBaseEntry));
                                    }
                                    //用户的Key
                                    if (!TextUtils.isEmpty(result.getData().getUkey())) {
                                        PreferencesUtils.putString(context, Constants.USER_LT_UID_KEY,
                                                result.getData().getUkey());
                                    }
                                    //用户ID
                                    if (result.getData().getUser_id() != 0) {
                                        PreferencesUtils.putInt(context, Constants.USER_LT_UID,
                                                result.getData().getUser_id());
                                    }

                                    if (result.getData().getIs_register() == 1) {
                                        FacebookEventManager.getInstance().register(context, 3);
                                    }


                                } else {
                                    sendException(context, result.getCode(), "QQ_Login:" + result.getMsg(),
                                            result.getMsg(), mListener);
                                    if (mListener != null) {
                                        mListener.onState((Activity) context, LoginResult.failOf(
                                                LTResultCode.STATE_QQ_LOGIN_FAILED,
                                                result.getMsg()));
                                    }
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            mListener.onState((Activity) context,
                                    LoginResult.failOf(ExceptionHelper.handleException(e)));
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            mListener.onState((Activity) context,
                    LoginResult.failOf(LTResultCode.STATE_CODE_PARAMETERS_ERROR,
                            LTResultCode.STATE_CODE_PARAMETERS_FAILED));
        }

    }

    /**
     * 微信登录
     * <p>
     *
     * @param bindID    微信返回的id
     * @param account   微信返回的账号
     * @param userName  微信返回的昵称
     * @param mListener 接口回调
     */
    public static void weChatLogin(final Activity context, String baseUrl, String bindID,
                                   String account, String userName,
                                   final OnLoginStateListener mListener) {
        LTGameOptions options = LTGameCommon.getInstance().options();
        String mLtAppID = "";
        String mADID = "";
        if (!TextUtils.isEmpty(options.getLtAppId())) {
            mLtAppID = options.getLtAppId();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_APP_ID))) {
            mLtAppID = PreferencesUtils.getString(context, Constants.LT_SDK_APP_ID);
        }
        if (!TextUtils.isEmpty(options.getAdID())) {
            mADID = options.getAdID();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_DEVICE_ADID))) {
            mADID = PreferencesUtils.getString(context, Constants.LT_SDK_DEVICE_ADID);
        }
        if (!TextUtils.isEmpty(mLtAppID) &&
                !TextUtils.isEmpty(mADID) &&
                !TextUtils.isEmpty(baseUrl) &&
                !TextUtils.isEmpty(bindID) &&
                !TextUtils.isEmpty(account) &&
                !TextUtils.isEmpty(userName)) {
            long LTTime = System.currentTimeMillis() / 1000L;
            Map<String, Object> map = new WeakHashMap<>();
            map.put("dpt", 2);
            map.put("vid", SDK_VERSION);
            map.put("udid", mADID);
            map.put("adid", mADID);
            map.put("bid", AppUtil.getPackageName(context));
            if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.USER_LT_UID_KEY))) {
                map.put("ukey", PreferencesUtils.getString(context, Constants.USER_LT_UID_KEY));
            } else {
                map.put("ukey", "");
            }
            WeakHashMap<String, Object> params = new WeakHashMap<>();
            params.put("bind_id", bindID);
            params.put("account", account);
            params.put("username", userName);
            map.put("data", params);

            String mLtToken = MD5Util.md5Decode(
                    (DateUtil.getTimeStamp(DateUtil.getSystemTimeStamp() -
                            PreferencesUtils.getLong(context, Constants.LT_SDK_TIME_STAMP)))
                            + mADID + mADID + AppUtil.getPackageName(context) + 123).substring(3, 28);

            Api.getInstance(context, baseUrl)
                    .weChatLogin(AppUtil.getLanguage(), mLtToken, (int) LTTime, mLtAppID, map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BaseEntry<ResultModel>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(BaseEntry<ResultModel> result) {
                            if (result != null) {
                                if (result.getCode() == 0) {
                                    if (mListener != null) {
                                        BaseEntry<ResultModel> resultModelBaseEntry = new BaseEntry<>();
                                        ResultModel model = new ResultModel();
                                        model.setUkey(result.getData().getUkey());
                                        model.setUser_id(result.getData().getUser_id());
                                        resultModelBaseEntry.setData(model);
                                        mListener.onState((Activity) context, LoginResult.successOf(
                                                LTResultCode.STATE_WX_LOGIN_SUCCESS,
                                                resultModelBaseEntry));
                                    }
                                    //用户的Key
                                    if (!TextUtils.isEmpty(result.getData().getUkey())) {
                                        PreferencesUtils.putString(context, Constants.USER_LT_UID_KEY,
                                                result.getData().getUkey());
                                    }
                                    //用户ID
                                    if (result.getData().getUser_id() != 0) {
                                        PreferencesUtils.putInt(context, Constants.USER_LT_UID,
                                                result.getData().getUser_id());
                                    }

                                    if (result.getData().getIs_register() == 1) {
                                        FacebookEventManager.getInstance().register(context, 3);
                                    }
                                } else {
                                    sendException(context, result.getCode(), "weChat_Login:" + result.getMsg(),
                                            result.getMsg(), mListener);
                                    if (mListener != null) {
                                        mListener.onState((Activity) context, LoginResult.failOf(
                                                LTResultCode.STATE_WX_LOGIN_FAILED,
                                                result.getMsg()));
                                    }
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            mListener.onState((Activity) context,
                                    LoginResult.failOf(ExceptionHelper.handleException(e)));
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            mListener.onState((Activity) context,
                    LoginResult.failOf(LTResultCode.STATE_CODE_PARAMETERS_ERROR,
                            LTResultCode.STATE_CODE_PARAMETERS_FAILED));
        }


    }

    /**
     * 创建订单
     *
     * @param context       上下文
     * @param role_number   角色编号（游戏服务器用户ID）
     * @param server_number 服务器编号（游戏提供）
     * @param goods_number  商品ID，游戏提供
     * @param mListener     接口
     */
    public static void createOrder(final Activity context, int role_number, int server_number,
                                   String goods_number,
                                   final OnRechargeStateListener mListener) {
        LTGameOptions options = LTGameCommon.getInstance().options();
        String mLtAppID = "";
        String mADID = "";
        String baseUrl = "";
        String mServerTest = "";
        if (!TextUtils.isEmpty(options.getLtAppId())) {
            mLtAppID = options.getLtAppId();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_APP_ID))) {
            mLtAppID = PreferencesUtils.getString(context, Constants.LT_SDK_APP_ID);
        }
        if (!TextUtils.isEmpty(options.getAdID())) {
            mADID = options.getAdID();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_DEVICE_ADID))) {
            mADID = PreferencesUtils.getString(context, Constants.LT_SDK_DEVICE_ADID);
        }
        if (!TextUtils.isEmpty(options.getISServerTest())) {
            mServerTest = options.getISServerTest();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_SERVER_TEST_TAG))) {
            mServerTest = PreferencesUtils.getString(context, Constants.LT_SDK_SERVER_TEST_TAG);
        }

        if (!TextUtils.isEmpty(mLtAppID) &&
                !TextUtils.isEmpty(mADID) &&
                !TextUtils.isEmpty(goods_number) &&
                !TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.USER_LT_UID_KEY)) &&
                role_number != 0 &&
                server_number != 0) {
            long LTTime = System.currentTimeMillis() / 1000L;
            Map<String, Object> map = new WeakHashMap<>();
            map.put("dpt", 2);
            map.put("vid", SDK_VERSION);
            map.put("udid", options.getAdID());
            map.put("adid", options.getAdID());
            map.put("bid", AppUtil.getPackageName(context));
            map.put("ukey", PreferencesUtils.getString(context, Constants.USER_LT_UID_KEY));
            WeakHashMap<String, Object> params = new WeakHashMap<>();
            params.put("role_number", role_number);
            params.put("server_number", server_number);
            params.put("goods_number", goods_number);
            map.put("data", params);
            if (mServerTest.equals(Constants.LT_SERVER_TEST)) {
                baseUrl = Api.TEST_SERVER_URL + SDK_TEST + Api.TEST_SERVER_DOMAIN;
            } else if (mServerTest.equals(Constants.LT_SERVER_OFFICIAL)) {
                baseUrl = Api.FORMAL_SERVER_URL + mLtAppID + Api.FORMAL_SERVER_DOMAIN;
            }


            String finalMADID = mADID;
            String finalMLtAppID = mLtAppID;
            String finalBaseUrl = baseUrl;
            Api.getInstance((Activity) context, baseUrl)
                    .getTime(AppUtil.getLanguage(), "", (int) LTTime, mLtAppID, map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .flatMap(new Function<BaseEntry<ResultModel>, ObservableSource<BaseEntry<ResultModel>>>() {
                        @Override
                        public ObservableSource<BaseEntry<ResultModel>> apply(BaseEntry<ResultModel> resultModelBaseEntry) throws Exception {
                            if (resultModelBaseEntry.getCode() == 0) {
                                String mLTToken = MD5Util.md5Decode(
                                        DateUtil.getTimeStamp(resultModelBaseEntry.getData().getTime())
                                                + finalMADID + finalMADID +
                                                AppUtil.getPackageName(context) + 123).substring(3, 28);
                                return Api.getInstance((Activity) context, finalBaseUrl)
                                        .createOrder(AppUtil.getLanguage(), mLTToken, (int) LTTime, finalMLtAppID, map);
                            }
                            return null;
                        }
                    }).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BaseEntry<ResultModel>>() {


                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(BaseEntry<ResultModel> result) {
                            if (result != null) {
                                if (result.getCode() == 0) {
                                    if (!TextUtils.isEmpty(result.getData().getOrder_number())) {
                                        mListener.onState(context, RechargeResult.successOf(
                                                LTResultCode.STATE_GP_CREATE_ORDER_SUCCESS, result));
                                    }
                                } else {
                                    sendRechargeException(context, result.getCode(), "Create_Order:" + result.getMsg(),
                                            result.getMsg(), mListener);
                                    mListener.onState(context, RechargeResult.failOf(LTGameError.make(
                                            LTResultCode.STATE_GP_CREATE_ORDER_FAILED, result.getMsg()
                                    )));
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            mListener.onState(context, RechargeResult.failOf(ExceptionHelper.handleException(e)));
                        }

                        @Override
                        public void onComplete() {

                        }

                    });

        } else {
            mListener.onState(context,
                    RechargeResult.failOf(LTResultCode.STATE_CODE_PARAMETERS_ERROR,
                            LTResultCode.STATE_CODE_PARAMETERS_FAILED));
        }


    }


    /**
     * google支付
     */
    public static void googlePlay(final Context context, String purchaseToken, String orderID,
                                  int mPayTest, final OnRechargeStateListener mListener) {
        LTGameOptions options = LTGameCommon.getInstance().options();
        String mLtAppID = "";
        String mADID = "";
        String baseUrl = "";
        String mServerTest = "";
        if (!TextUtils.isEmpty(options.getLtAppId())) {
            mLtAppID = options.getLtAppId();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_APP_ID))) {
            mLtAppID = PreferencesUtils.getString(context, Constants.LT_SDK_APP_ID);
        }
        if (!TextUtils.isEmpty(options.getAdID())) {
            mADID = options.getAdID();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_DEVICE_ADID))) {
            mADID = PreferencesUtils.getString(context, Constants.LT_SDK_DEVICE_ADID);
        }
        if (!TextUtils.isEmpty(options.getISServerTest())) {
            mServerTest = options.getISServerTest();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_SERVER_TEST_TAG))) {
            mServerTest = PreferencesUtils.getString(context, Constants.LT_SDK_SERVER_TEST_TAG);
        }
        if (!TextUtils.isEmpty(purchaseToken) &&
                !TextUtils.isEmpty(orderID) &&
                !TextUtils.isEmpty(mLtAppID) &&
                !TextUtils.isEmpty(mADID)) {
            long LTTime = System.currentTimeMillis() / 1000L;
            String LTToken = "1";
            Map<String, Object> map = new WeakHashMap<>();
            map.put("dpt", 2);
            map.put("vid", SDK_VERSION);
            map.put("udid", mADID);
            map.put("adid", mADID);
            map.put("bid", AppUtil.getPackageName(context));
            map.put("ukey", PreferencesUtils.getString(context, Constants.USER_LT_UID_KEY));
            WeakHashMap<String, Object> params = new WeakHashMap<>();
            params.put("order_number", orderID);
            params.put("is_test", mPayTest);
            params.put("token", purchaseToken);
            map.put("data", params);
            if (mServerTest.equals(Constants.LT_SERVER_TEST)) {
                baseUrl = Api.TEST_SERVER_URL + SDK_TEST + Api.TEST_SERVER_DOMAIN;
            } else if (mServerTest.equals(Constants.LT_SERVER_OFFICIAL)) {
                baseUrl = Api.FORMAL_SERVER_URL + mLtAppID + Api.FORMAL_SERVER_DOMAIN;
            }

            String finalMADID = mADID;
            String finalMLtAppID = mLtAppID;
            String finalBaseUrl = baseUrl;
            Api.getInstance((Activity) context, baseUrl)
                    .getTime(AppUtil.getLanguage(), "", (int) LTTime, mLtAppID, map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .flatMap(new Function<BaseEntry<ResultModel>, ObservableSource<BaseEntry<ResultModel>>>() {
                        @Override
                        public ObservableSource<BaseEntry<ResultModel>> apply(BaseEntry<ResultModel> resultModelBaseEntry) throws Exception {
                            if (resultModelBaseEntry.getCode() == 0) {
                                String mLTToken = MD5Util.md5Decode(
                                        DateUtil.getTimeStamp(resultModelBaseEntry.getData().getTime())
                                                + finalMADID + finalMADID +
                                                AppUtil.getPackageName(context) + 123).substring(3, 28);
                                return Api.getInstance((Activity) context, finalBaseUrl)
                                        .googlePlay(AppUtil.getLanguage(), mLTToken, (int) LTTime, finalMLtAppID, map);
                            }
                            return null;
                        }
                    }).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BaseEntry<ResultModel>>() {


                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(BaseEntry<ResultModel> result) {
                            if (result != null) {
                                if (result.getCode() == 0) {
                                    mListener.onState((Activity) context, RechargeResult.successOf(result));
                                } else {
                                    sendRechargeException(context, result.getCode(), "GP_PLAY:" + result.getMsg(),
                                            result.getMsg(), mListener);
                                    mListener.onState((Activity) context, RechargeResult.failOf(
                                            LTResultCode.STATE_GP_RESPONSE_RESULT_FAILED, result.getMsg()));
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            mListener.onState((Activity) context, RechargeResult.failOf(ExceptionHelper.handleException(e)));
                        }

                        @Override
                        public void onComplete() {

                        }

                    });

        } else {
            mListener.onState((Activity) context,
                    RechargeResult.failOf(LTResultCode.STATE_CODE_PARAMETERS_ERROR,
                            LTResultCode.STATE_CODE_PARAMETERS_FAILED));
        }


    }

    /**
     * oneStore支付
     */
    public static void oneStorePlay(final Context context, String purchaseToken, String orderID,
                                    int mPayTest, final OnRechargeStateListener mListener) {
        LTGameOptions options = LTGameCommon.getInstance().options();
        String mLtAppID = "";
        String mADID = "";
        String baseUrl = "";
        String mServerTest = "";
        if (!TextUtils.isEmpty(options.getLtAppId())) {
            mLtAppID = options.getLtAppId();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_APP_ID))) {
            mLtAppID = PreferencesUtils.getString(context, Constants.LT_SDK_APP_ID);
        }
        if (!TextUtils.isEmpty(options.getAdID())) {
            mADID = options.getAdID();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_DEVICE_ADID))) {
            mADID = PreferencesUtils.getString(context, Constants.LT_SDK_DEVICE_ADID);
        }
        if (!TextUtils.isEmpty(options.getISServerTest())) {
            mServerTest = options.getISServerTest();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_SERVER_TEST_TAG))) {
            mServerTest = PreferencesUtils.getString(context, Constants.LT_SDK_SERVER_TEST_TAG);
        }

        if (!TextUtils.isEmpty(mLtAppID) &&
                !TextUtils.isEmpty(mADID) &&
                !TextUtils.isEmpty(orderID) &&
                !TextUtils.isEmpty(purchaseToken) &&
                mPayTest != -1) {
            long LTTime = System.currentTimeMillis() / 1000L;
            String LTToken = "";
            Map<String, Object> params = new WeakHashMap<>();
            params.put("purchase_id", purchaseToken);
            params.put("lt_order_id", orderID);
            params.put("pay_test", mPayTest);
            params.put("platform", 2);
            params.put("adid", mADID);
            params.put("gps_adid", mADID);
            params.put("platform_id", AppUtil.getPackageName(context));

            String json = new Gson().toJson(params);//要传递的json
            final RequestBody requestBody = RequestBody.create(okhttp3.MediaType
                    .parse("application/json; charset=utf-8"), json);

            if (mServerTest.equals(Constants.LT_SERVER_TEST)) {
                baseUrl = Api.TEST_SERVER_URL + SDK_TEST + Api.TEST_SERVER_DOMAIN;
            } else if (mServerTest.equals(Constants.LT_SERVER_OFFICIAL)) {
                baseUrl = Api.FORMAL_SERVER_URL + mLtAppID + Api.FORMAL_SERVER_DOMAIN;
            }

            Api.getInstance((Activity) context, baseUrl)
                    .oneStorePlay(mLtAppID, LTToken, (int) LTTime, AppUtil.getLanguage(), requestBody)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BaseEntry<ResultModel>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(BaseEntry<ResultModel> result) {
                            if (result != null) {
                                if (result.getCode() == 0) {
                                    mListener.onState((Activity) context, RechargeResult.successOf(result));
                                } else {
                                    sendRechargeException(context, result.getCode(), "ONE_STORE_PLAY:" + result.getMsg(),
                                            result.getMsg(), mListener);
                                    mListener.onState((Activity) context, RechargeResult.failOf(
                                            LTResultCode.STATE_ONE_STORE_PLAY_FAILED, result.getMsg()));
                                }

                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            mListener.onState((Activity) context,
                                    RechargeResult.failOf(ExceptionHelper.handleException(e)));
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            mListener.onState((Activity) context,
                    RechargeResult.failOf(LTResultCode.STATE_CODE_PARAMETERS_ERROR,
                            LTResultCode.STATE_CODE_PARAMETERS_FAILED));
        }
    }


    /**
     * 获取邮箱验证码
     *
     * @param mListener 接口回调
     */
    public static void getEmailAuthCode(final Activity context, String mEmail,
                                        final OnLoginStateListener mListener) {
        LTGameOptions options = LTGameCommon.getInstance().options();
        String mLtAppID = "";
        String mADID = "";
        String baseUrl = "";
        String mServerTest = "";
        if (!TextUtils.isEmpty(options.getLtAppId())) {
            mLtAppID = options.getLtAppId();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_APP_ID))) {
            mLtAppID = PreferencesUtils.getString(context, Constants.LT_SDK_APP_ID);
        }
        if (!TextUtils.isEmpty(options.getAdID())) {
            mADID = options.getAdID();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_DEVICE_ADID))) {
            mADID = PreferencesUtils.getString(context, Constants.LT_SDK_DEVICE_ADID);
        }
        if (!TextUtils.isEmpty(options.getISServerTest())) {
            mServerTest = options.getISServerTest();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_SERVER_TEST_TAG))) {
            mServerTest = PreferencesUtils.getString(context, Constants.LT_SDK_SERVER_TEST_TAG);
        }


        if (!TextUtils.isEmpty(mLtAppID) &&
                !TextUtils.isEmpty(mEmail) &&
                !TextUtils.isEmpty(mADID)) {
            long LTTime = System.currentTimeMillis() / 1000L;
            Map<String, Object> map = new WeakHashMap<>();
            map.put("dpt", 2);
            map.put("vid", SDK_VERSION);
            map.put("udid", mADID);
            map.put("adid", mADID);
            map.put("bid", AppUtil.getPackageName(context));
            if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.USER_LT_UID_KEY))) {
                map.put("ukey", PreferencesUtils.getString(context, Constants.USER_LT_UID_KEY));
            } else {
                map.put("ukey", "");
            }
            WeakHashMap<String, Object> params = new WeakHashMap<>();
            params.put("email", mEmail);
            map.put("data", params);
            if (mServerTest.equals(Constants.LT_SERVER_TEST)) {
                baseUrl = Api.TEST_SERVER_URL + SDK_TEST + Api.TEST_SERVER_DOMAIN;
            } else if (mServerTest.equals(Constants.LT_SERVER_OFFICIAL)) {
                baseUrl = Api.FORMAL_SERVER_URL + mLtAppID + Api.FORMAL_SERVER_DOMAIN;
            }

            String mLtToken = MD5Util.md5Decode(
                    (DateUtil.getTimeStamp(DateUtil.getSystemTimeStamp() -
                            PreferencesUtils.getLong(context, Constants.LT_SDK_TIME_STAMP)))
                            + mADID + mADID + AppUtil.getPackageName(context) + 123).substring(3, 28);

            Api.getInstance(context, baseUrl)
                    .getAuthCode(AppUtil.getLanguage(), mLtToken, (int) LTTime, mLtAppID, map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BaseEntry>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(BaseEntry result) {
                            if (result != null) {
                                if (result.getCode() == 0) {
                                    if (mListener != null) {
                                        mListener.onState(context, LoginResult.successBaseEntryOf(
                                                LTResultCode.STATE_EMAIL_GET_CODE_SUCCESS,
                                                result));
                                    }
                                    ToastUtil.getInstance().shortToast(context, result.getMsg());
                                } else {
                                    sendException(context, result.getCode(), "Get_Email_Code:" + result.getMsg(),
                                            result.getMsg(), mListener);
                                    if (mListener != null) {
                                        mListener.onState((Activity) context, LoginResult.failOf(
                                                LTResultCode.STATE_EMAIL_GET_CODE_FAILED,
                                                result.getMsg()));
                                    }
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            mListener.onState((Activity) context,
                                    LoginResult.failOf(ExceptionHelper.handleException(e)));
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            mListener.onState((Activity) context,
                    LoginResult.failOf(LTResultCode.STATE_CODE_PARAMETERS_ERROR,
                            LTResultCode.STATE_CODE_PARAMETERS_FAILED));
        }


    }


    /**
     * 获取微信AccessToken
     * <p>
     */
    public static void getWXAccessToken(Context context, String baseUrl, String appid,
                                        String secret, String code,
                                        OnWeChatAccessTokenListener<WeChatAccessToken> mListener,
                                        OnLoginStateListener mStateListener) {
        if (!TextUtils.isEmpty(baseUrl) &&
                !TextUtils.isEmpty(appid) &&
                !TextUtils.isEmpty(secret) &&
                !TextUtils.isEmpty(code)) {

            Api.getInstance((Activity) context, baseUrl)
                    .getWXAccessToken(appid, secret, code, "authorization_code")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<WeChatAccessToken>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(WeChatAccessToken result) {
                            if (result != null) {
                                if (result.isNoError()) {
                                    if (mListener != null) {
                                        mListener.onWeChatSuccess(result);
                                    }
                                } else {
                                    sendException(context, (int) result.getErrcode(),
                                            "WX_Get_Access_Token:" + result.getErrmsg(),
                                            result.getErrmsg(), mStateListener);
                                    if (mListener != null) {
                                        mListener.onWeChatFailed("WeChat get AccessToken error");
                                    }
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }

    /**
     * 刷新微信AccessToken
     * <p>
     */
    public static void refreshWXAccessToken(Context context, String baseUrl, String appid,
                                            String refresh_token,
                                            OnWeChatAccessTokenListener<WeChatAccessToken> mListener,
                                            OnLoginStateListener mStateListener) {
        if (!TextUtils.isEmpty(baseUrl) &&
                !TextUtils.isEmpty(appid) &&
                !TextUtils.isEmpty(refresh_token)) {
            Api.getInstance((Activity) context, baseUrl)
                    .refreshWXAccessToken(appid, "refresh_token", refresh_token)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<WeChatAccessToken>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(WeChatAccessToken result) {
                            if (result != null) {
                                if (result.isNoError()) {
                                    if (mListener != null) {
                                        mListener.onWeChatSuccess(result);
                                    }
                                } else {
                                    sendException(context, (int) result.getErrcode(),
                                            "WX_Access_Token:" + result.getErrmsg(),
                                            result.getErrmsg(), mStateListener);
                                    if (mListener != null) {
                                        mListener.onWeChatFailed("WeChat get AccessToken error");
                                    }
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }

    /**
     * 验证微信AccessToken
     * <p>
     */
    public static void authAccessToken(Context context, String baseUrl, String access_token, String openID,
                                       OnWeChatAccessTokenListener<AuthWXModel> mListener,
                                       OnLoginStateListener mStateListener) {
        if (!TextUtils.isEmpty(access_token) &&
                !TextUtils.isEmpty(openID) &&
                !TextUtils.isEmpty(baseUrl)) {
            Api.getInstance((Activity) context, baseUrl)
                    .authToken(access_token, openID)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<AuthWXModel>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(AuthWXModel result) {
                            if (result != null) {
                                if (result.isNoError()) {
                                    if (mListener != null) {
                                        mListener.onWeChatSuccess(result);
                                    }
                                } else {
                                    sendException(context, (int) result.getErrcode(),
                                            "WX_Auth_Access_Token:" + result.getErrmsg(),
                                            result.getErrmsg(), mStateListener);
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }

    /**
     * 获取微信信息
     */
    public static void getWXInfo(Context context, String baseUrl, String access_token, String openID,
                                 OnWeChatAccessTokenListener<WXUser> mListener,
                                 OnLoginStateListener mStateListener) {
        if (!TextUtils.isEmpty(access_token) &&
                !TextUtils.isEmpty(openID) &&
                !TextUtils.isEmpty(baseUrl)) {
            Api.getInstance((Activity) context, baseUrl)
                    .getWXInfo(access_token, openID)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<WXUser>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(WXUser result) {
                            if (result != null) {
                                if (result.isNoError()) {
                                    if (mListener != null) {
                                        mListener.onWeChatSuccess(result);
                                    }
                                } else {
                                    sendException(context, (int) result.getErrcode(),
                                            "WX_Get_User_Info:" + result.getErrmsg(),
                                            result.getErrmsg(), mStateListener);
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }


    /**
     * 绑定Google
     *
     * @param bindID       google返回的ID
     * @param account      google的账户
     * @param userName     google返回的昵称
     * @param access_token google返回的token
     * @param mListener    接口回调
     */
    public static void bindGoogle(final Context context, String bindID,
                                  String account, String userName, String access_token,
                                  final OnLoginStateListener mListener) {
        LTGameOptions options = LTGameCommon.getInstance().options();
        String mLtAppID = "";
        String mADID = "";
        String baseUrl = "";
        String mServerTest = "";
        if (!TextUtils.isEmpty(options.getLtAppId())) {
            mLtAppID = options.getLtAppId();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_APP_ID))) {
            mLtAppID = PreferencesUtils.getString(context, Constants.LT_SDK_APP_ID);
        }
        if (!TextUtils.isEmpty(options.getAdID())) {
            mADID = options.getAdID();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_DEVICE_ADID))) {
            mADID = PreferencesUtils.getString(context, Constants.LT_SDK_DEVICE_ADID);
        }
        if (!TextUtils.isEmpty(options.getISServerTest())) {
            mServerTest = options.getISServerTest();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_SERVER_TEST_TAG))) {
            mServerTest = PreferencesUtils.getString(context, Constants.LT_SDK_SERVER_TEST_TAG);
        }

        if (!TextUtils.isEmpty(mLtAppID) &&
                !TextUtils.isEmpty(mADID) &&
                !TextUtils.isEmpty(bindID) &&
                !TextUtils.isEmpty(account) &&
                !TextUtils.isEmpty(userName) &&
                !TextUtils.isEmpty(access_token) &&
                !TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.USER_LT_UID_KEY))) {
            long LTTime = System.currentTimeMillis() / 1000L;
            Map<String, Object> map = new WeakHashMap<>();
            map.put("dpt", 2);
            map.put("vid", SDK_VERSION);
            map.put("udid", mADID);
            map.put("adid", mADID);
            map.put("bid", AppUtil.getPackageName(context));
            map.put("ukey", PreferencesUtils.getString(context, Constants.USER_LT_UID_KEY));
            WeakHashMap<String, Object> params = new WeakHashMap<>();
            params.put("bind_id", bindID);
            params.put("account", account);
            params.put("username", userName);
            params.put("access_token", access_token);
            map.put("data", params);
            if (mServerTest.equals(Constants.LT_SERVER_TEST)) {
                baseUrl = Api.TEST_SERVER_URL + SDK_TEST + Api.TEST_SERVER_DOMAIN;
            } else if (mServerTest.equals(Constants.LT_SERVER_OFFICIAL)) {
                baseUrl = Api.FORMAL_SERVER_URL + mLtAppID + Api.FORMAL_SERVER_DOMAIN;
            }
            String mLtToken = MD5Util.md5Decode(
                    (DateUtil.getTimeStamp(DateUtil.getSystemTimeStamp() -
                            PreferencesUtils.getLong(context, Constants.LT_SDK_TIME_STAMP)))
                            + mADID + mADID + AppUtil.getPackageName(context) + 123).substring(3, 28);

            Api.getInstance((Activity) context, baseUrl)
                    .bindGoogle(AppUtil.getLanguage(), mLtToken, (int) LTTime, mLtAppID, map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BaseEntry<ResultModel>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(BaseEntry<ResultModel> result) {
                            if (result != null) {
                                if (result.getCode() == 0) {
                                    if (result.getData().getIs_bind_third() == 0) {
                                        if (mListener != null) {
                                            BaseEntry<ResultModel> resultModelBaseEntry = new BaseEntry<>();
                                            ResultModel model = new ResultModel();
                                            model.setUser_id(result.getData().getUser_id());
                                            resultModelBaseEntry.setData(model);
                                            mListener.onState((Activity) context, LoginResult.successOf(
                                                    LTResultCode.STATE_GOOGLE_BIND_SUCCESS,
                                                    resultModelBaseEntry));
                                        }
                                        //用户ID
                                        if (result.getData().getUser_id() != 0) {
                                            PreferencesUtils.putInt(context, Constants.USER_LT_UID,
                                                    result.getData().getUser_id());
                                        }
                                    } else {
                                        mListener.onState((Activity) context,
                                                LoginResult.failOf(LTResultCode.STATE_GOOGLE_ALREADY_BIND,
                                                        result.getMsg()));
                                    }

                                } else {
                                    sendException(context, result.getCode(), "Bind_Google:" + result.getMsg(),
                                            result.getMsg(), mListener);
                                    if (mListener != null) {
                                        mListener.onState((Activity) context, LoginResult.failOf(
                                                LTResultCode.STATE_GOOGLE_BIND_FAILED,
                                                result.getMsg()));
                                    }
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            mListener.onState((Activity) context,
                                    LoginResult.failOf(ExceptionHelper.handleException(e)));
                        }

                        @Override
                        public void onComplete() {
                        }
                    });
        } else {
            mListener.onState((Activity) context,
                    LoginResult.failOf(LTResultCode.STATE_CODE_PARAMETERS_ERROR,
                            LTResultCode.STATE_CODE_PARAMETERS_FAILED));
        }

    }

    /**
     * 绑定Facebook
     *
     * @param bindID       Facebook返回的ID
     * @param account      Facebook的账户
     * @param userName     Facebook返回的昵称
     * @param access_token Facebook返回的token
     * @param mListener    接口回调
     */
    public static void bindFB(final Context context, String bindID,
                              String account, String userName, String access_token,
                              final OnLoginStateListener mListener) {
        LTGameOptions options = LTGameCommon.getInstance().options();
        String mLtAppID = "";
        String mADID = "";
        String baseUrl = "";
        String mServerTest = "";
        if (!TextUtils.isEmpty(options.getLtAppId())) {
            mLtAppID = options.getLtAppId();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_APP_ID))) {
            mLtAppID = PreferencesUtils.getString(context, Constants.LT_SDK_APP_ID);
        }
        if (!TextUtils.isEmpty(options.getAdID())) {
            mADID = options.getAdID();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_DEVICE_ADID))) {
            mADID = PreferencesUtils.getString(context, Constants.LT_SDK_DEVICE_ADID);
        }
        if (!TextUtils.isEmpty(options.getISServerTest())) {
            mServerTest = options.getISServerTest();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_SERVER_TEST_TAG))) {
            mServerTest = PreferencesUtils.getString(context, Constants.LT_SDK_SERVER_TEST_TAG);
        }


        if (!TextUtils.isEmpty(mLtAppID) &&
                !TextUtils.isEmpty(mADID) &&
                !TextUtils.isEmpty(bindID) &&
                !TextUtils.isEmpty(account) &&
                !TextUtils.isEmpty(userName) &&
                !TextUtils.isEmpty(access_token) &&
                !TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.USER_LT_UID_KEY))) {
            long LTTime = System.currentTimeMillis() / 1000L;
            Map<String, Object> map = new WeakHashMap<>();
            map.put("dpt", 2);
            map.put("vid", SDK_VERSION);
            map.put("udid", mADID);
            map.put("adid", mADID);
            map.put("bid", AppUtil.getPackageName(context));
            map.put("ukey", PreferencesUtils.getString(context, Constants.USER_LT_UID_KEY));
            WeakHashMap<String, Object> params = new WeakHashMap<>();
            params.put("bind_id", bindID);
            params.put("account", account);
            params.put("username", userName);
            params.put("access_token", access_token);
            map.put("data", params);
            if (mServerTest.equals(Constants.LT_SERVER_TEST)) {
                baseUrl = Api.TEST_SERVER_URL + SDK_TEST + Api.TEST_SERVER_DOMAIN;
            } else if (mServerTest.equals(Constants.LT_SERVER_OFFICIAL)) {
                baseUrl = Api.FORMAL_SERVER_URL + mLtAppID + Api.FORMAL_SERVER_DOMAIN;
            }

            String mLtToken = MD5Util.md5Decode(
                    (DateUtil.getTimeStamp(DateUtil.getSystemTimeStamp() -
                            PreferencesUtils.getLong(context, Constants.LT_SDK_TIME_STAMP)))
                            + mADID + mADID + AppUtil.getPackageName(context) + 123).substring(3, 28);

            Api.getInstance((Activity) context, baseUrl)
                    .bindFB(AppUtil.getLanguage(), mLtToken, (int) LTTime, mLtAppID, map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BaseEntry<ResultModel>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(BaseEntry<ResultModel> result) {
                            if (result != null) {
                                if (result.getCode() == 0) {
                                    if (result.getData().getIs_bind_third() == 0) {
                                        if (mListener != null) {
                                            BaseEntry<ResultModel> resultModelBaseEntry = new BaseEntry<>();
                                            ResultModel model = new ResultModel();
                                            model.setUser_id(result.getData().getUser_id());
                                            resultModelBaseEntry.setData(model);
                                            mListener.onState((Activity) context, LoginResult.successOf(
                                                    LTResultCode.STATE_FB_BIND_SUCCESS,
                                                    resultModelBaseEntry));
                                        }
                                        //用户ID
                                        if (result.getData().getUser_id() != 0) {
                                            PreferencesUtils.putInt(context, Constants.USER_LT_UID,
                                                    result.getData().getUser_id());
                                        }
                                    } else {
                                        mListener.onState((Activity) context,
                                                LoginResult.failOf(
                                                        LTGameError.make(LTResultCode.STATE_FB_ALREADY_BIND)));
                                    }


                                } else {
                                    sendException(context, result.getCode(), "Bind_Facebook:" + result.getMsg(),
                                            result.getMsg(), mListener);
                                    if (mListener != null) {
                                        mListener.onState((Activity) context, LoginResult.failOf(
                                                LTResultCode.STATE_FB_BIND_FAILED,
                                                result.getMsg()));
                                    }
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            mListener.onState((Activity) context,
                                    LoginResult.failOf(ExceptionHelper.handleException(e)));
                        }

                        @Override
                        public void onComplete() {
                        }
                    });
        } else {
            mListener.onState((Activity) context,
                    LoginResult.failOf(LTResultCode.STATE_CODE_PARAMETERS_ERROR,
                            LTResultCode.STATE_CODE_PARAMETERS_FAILED));
        }


    }

    /**
     * 绑定游客
     *
     * @param mListener 接口回调
     */
    public static void bindGuest(final Context context,
                                 final OnLoginStateListener mListener) {
        LTGameOptions options = LTGameCommon.getInstance().options();
        String mLtAppID = "";
        String mADID = "";
        String baseUrl = "";
        String mServerTest = "";
        if (!TextUtils.isEmpty(options.getLtAppId())) {
            mLtAppID = options.getLtAppId();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_APP_ID))) {
            mLtAppID = PreferencesUtils.getString(context, Constants.LT_SDK_APP_ID);
        }
        if (!TextUtils.isEmpty(options.getAdID())) {
            mADID = options.getAdID();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_DEVICE_ADID))) {
            mADID = PreferencesUtils.getString(context, Constants.LT_SDK_DEVICE_ADID);
        }
        if (!TextUtils.isEmpty(options.getISServerTest())) {
            mServerTest = options.getISServerTest();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_SERVER_TEST_TAG))) {
            mServerTest = PreferencesUtils.getString(context, Constants.LT_SDK_SERVER_TEST_TAG);
        }


        if (!TextUtils.isEmpty(mLtAppID) &&
                !TextUtils.isEmpty(mADID) &&
                !TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.USER_LT_UID_KEY))) {
            long LTTime = System.currentTimeMillis() / 1000L;
            Map<String, Object> map = new WeakHashMap<>();
            map.put("dpt", 2);
            map.put("vid", SDK_VERSION);
            map.put("udid", mADID);
            map.put("adid", mADID);
            map.put("bid", AppUtil.getPackageName(context));
            map.put("ukey", PreferencesUtils.getString(context, Constants.USER_LT_UID_KEY));
            WeakHashMap<String, Object> params = new WeakHashMap<>();
            map.put("data", params);
            if (mServerTest.equals(Constants.LT_SERVER_TEST)) {
                baseUrl = Api.TEST_SERVER_URL + SDK_TEST + Api.TEST_SERVER_DOMAIN;
            } else if (mServerTest.equals(Constants.LT_SERVER_OFFICIAL)) {
                baseUrl = Api.FORMAL_SERVER_URL + mLtAppID + Api.FORMAL_SERVER_DOMAIN;
            }

            String mLtToken = MD5Util.md5Decode(
                    (DateUtil.getTimeStamp(DateUtil.getSystemTimeStamp() -
                            PreferencesUtils.getLong(context, Constants.LT_SDK_TIME_STAMP)))
                            + mADID + mADID + AppUtil.getPackageName(context) + 123).substring(3, 28);

            Api.getInstance((Activity) context, baseUrl)
                    .bindGuest(AppUtil.getLanguage(), mLtToken, (int) LTTime, mLtAppID, map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BaseEntry<ResultModel>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(BaseEntry<ResultModel> result) {
                            if (result != null) {
                                if (result.getCode() == 0) {
                                    if (result.getData().getIs_bind_visitor() == 0) {
                                        if (mListener != null) {
                                            BaseEntry<ResultModel> resultModelBaseEntry = new BaseEntry<>();
                                            ResultModel model = new ResultModel();
                                            model.setUser_id(result.getData().getUser_id());
                                            resultModelBaseEntry.setData(model);
                                            mListener.onState((Activity) context, LoginResult.successOf(
                                                    LTResultCode.STATE_GUEST_BIND_SUCCESS,
                                                    resultModelBaseEntry));
                                        }
                                        //用户ID
                                        if (result.getData().getUser_id() != 0) {
                                            PreferencesUtils.putInt(context, Constants.USER_LT_UID,
                                                    result.getData().getUser_id());
                                        }
                                    } else {
                                        mListener.onState((Activity) context,
                                                LoginResult.failOf(
                                                        LTGameError.make(LTResultCode.STATE_GUEST_ALREADY_BIND)));
                                    }

                                } else {
                                    sendException(context, result.getCode(), "Bind_Guest:" + result.getMsg(),
                                            result.getMsg(), mListener);
                                    if (mListener != null) {
                                        mListener.onState((Activity) context, LoginResult.failOf(
                                                LTGameError.make(LTResultCode.STATE_GUEST_BIND_FAILED,
                                                        result.getMsg())));
                                    }
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            mListener.onState((Activity) context,
                                    LoginResult.failOf(ExceptionHelper.handleException(e)));
                        }

                        @Override
                        public void onComplete() {
                        }
                    });
        } else {
            mListener.onState((Activity) context,
                    LoginResult.failOf(LTResultCode.STATE_CODE_PARAMETERS_ERROR,
                            LTResultCode.STATE_CODE_PARAMETERS_FAILED));
        }

    }

    /**
     * 绑定邮箱
     *
     * @param email     邮箱
     * @param code      验证码
     * @param mListener 接口回调
     */
    public static void bindEmail(final Context context, String email,
                                 String code,
                                 final OnLoginStateListener mListener) {
        LTGameOptions options = LTGameCommon.getInstance().options();
        String mLtAppID = "";
        String mADID = "";
        String baseUrl = "";
        String mServerTest = "";
        if (!TextUtils.isEmpty(options.getLtAppId())) {
            mLtAppID = options.getLtAppId();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_APP_ID))) {
            mLtAppID = PreferencesUtils.getString(context, Constants.LT_SDK_APP_ID);
        }
        if (!TextUtils.isEmpty(options.getAdID())) {
            mADID = options.getAdID();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_DEVICE_ADID))) {
            mADID = PreferencesUtils.getString(context, Constants.LT_SDK_DEVICE_ADID);
        }
        if (!TextUtils.isEmpty(options.getISServerTest())) {
            mServerTest = options.getISServerTest();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_SERVER_TEST_TAG))) {
            mServerTest = PreferencesUtils.getString(context, Constants.LT_SDK_SERVER_TEST_TAG);
        }


        if (!TextUtils.isEmpty(mLtAppID) &&
                !TextUtils.isEmpty(mADID) &&
                !TextUtils.isEmpty(email) &&
                !TextUtils.isEmpty(code) &&
                !TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.USER_LT_UID_KEY))) {
            long LTTime = System.currentTimeMillis() / 1000L;
            Map<String, Object> map = new WeakHashMap<>();
            map.put("dpt", 2);
            map.put("vid", SDK_VERSION);
            map.put("udid", mADID);
            map.put("adid", mADID);
            map.put("bid", AppUtil.getPackageName(context));
            map.put("ukey", PreferencesUtils.getString(context, Constants.USER_LT_UID_KEY));
            WeakHashMap<String, Object> params = new WeakHashMap<>();
            params.put("email", email);
            params.put("code", code);
            map.put("data", params);
            if (mServerTest.equals(Constants.LT_SERVER_TEST)) {
                baseUrl = Api.TEST_SERVER_URL + SDK_TEST + Api.TEST_SERVER_DOMAIN;
            } else if (mServerTest.equals(Constants.LT_SERVER_OFFICIAL)) {
                baseUrl = Api.FORMAL_SERVER_URL + mLtAppID + Api.FORMAL_SERVER_DOMAIN;
            }

            String mLtToken = MD5Util.md5Decode(
                    (DateUtil.getTimeStamp(DateUtil.getSystemTimeStamp() -
                            PreferencesUtils.getLong(context, Constants.LT_SDK_TIME_STAMP)))
                            + mADID + mADID + AppUtil.getPackageName(context) + 123).substring(3, 28);

            Api.getInstance((Activity) context, baseUrl)
                    .bindEmail(AppUtil.getLanguage(), mLtToken, (int) LTTime, mLtAppID, map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BaseEntry<ResultModel>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(BaseEntry<ResultModel> result) {
                            if (result != null) {
                                if (result.getCode() == 0) {
                                    if (result.getData().getIs_bind_email() == 0) {
                                        if (mListener != null) {
                                            BaseEntry<ResultModel> resultModelBaseEntry = new BaseEntry<>();
                                            ResultModel model = new ResultModel();
                                            model.setUser_id(result.getData().getUser_id());
                                            resultModelBaseEntry.setData(model);
                                            mListener.onState((Activity) context, LoginResult.successOf(
                                                    LTResultCode.STATE_EMAIL_BIND_SUCCESS,
                                                    resultModelBaseEntry));
                                        }
                                        //用户ID
                                        if (result.getData().getUser_id() != 0) {
                                            PreferencesUtils.putInt(context, Constants.USER_LT_UID,
                                                    result.getData().getUser_id());
                                        }
                                    } else {
                                        mListener.onState((Activity) context,
                                                LoginResult.failOf(LTResultCode.STATE_EMAIL_ALREADY_BIND,
                                                        "Email already bind"));
                                    }


                                } else {
                                    sendException(context, result.getCode(), "Bind_Email:" + result.getMsg(),
                                            result.getMsg(), mListener);
                                    if (mListener != null) {
                                        mListener.onState((Activity) context, LoginResult.failOf(
                                                LTResultCode.STATE_EMAIL_BIND_FAILED,
                                                result.getMsg()));
                                    }
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            mListener.onState((Activity) context,
                                    LoginResult.failOf(ExceptionHelper.handleException(e)));
                        }

                        @Override
                        public void onComplete() {
                        }
                    });
        } else {
            mListener.onState((Activity) context,
                    LoginResult.failOf(LTResultCode.STATE_CODE_PARAMETERS_ERROR,
                            LTResultCode.STATE_CODE_PARAMETERS_FAILED));
        }


    }

    /**
     * 绑定微信
     *
     * @param bindID    微信返回的ID
     * @param account   微信的账户
     * @param userName  微信返回的昵称
     * @param mListener 接口回调
     */
    public static void bindWX(final Context context, String baseUrl, String bindID,
                              String account, String userName,
                              final OnLoginStateListener mListener) {
        LTGameOptions options = LTGameCommon.getInstance().options();
        String mLtAppID = "";
        String mADID = "";
        if (!TextUtils.isEmpty(options.getLtAppId())) {
            mLtAppID = options.getLtAppId();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_APP_ID))) {
            mLtAppID = PreferencesUtils.getString(context, Constants.LT_SDK_APP_ID);
        }
        if (!TextUtils.isEmpty(options.getAdID())) {
            mADID = options.getAdID();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_DEVICE_ADID))) {
            mADID = PreferencesUtils.getString(context, Constants.LT_SDK_DEVICE_ADID);
        }


        if (!TextUtils.isEmpty(mLtAppID) &&
                !TextUtils.isEmpty(mADID) &&
                !TextUtils.isEmpty(baseUrl) &&
                !TextUtils.isEmpty(bindID) &&
                !TextUtils.isEmpty(account) &&
                !TextUtils.isEmpty(userName) &&
                !TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.USER_LT_UID_KEY))) {
            long LTTime = System.currentTimeMillis() / 1000L;
            Map<String, Object> map = new WeakHashMap<>();
            map.put("dpt", 2);
            map.put("vid", SDK_VERSION);
            map.put("udid", mADID);
            map.put("adid", mADID);
            map.put("bid", AppUtil.getPackageName(context));
            map.put("ukey", PreferencesUtils.getString(context, Constants.USER_LT_UID_KEY));
            WeakHashMap<String, Object> params = new WeakHashMap<>();
            params.put("bind_id", bindID);
            params.put("account", account);
            params.put("username", userName);
            map.put("data", params);

            String mLtToken = MD5Util.md5Decode(
                    (DateUtil.getTimeStamp(DateUtil.getSystemTimeStamp() -
                            PreferencesUtils.getLong(context, Constants.LT_SDK_TIME_STAMP)))
                            + mADID + mADID + AppUtil.getPackageName(context) + 123).substring(3, 28);

            Api.getInstance((Activity) context, baseUrl)
                    .bindWX(AppUtil.getLanguage(), mLtToken, (int) LTTime, mLtAppID, map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BaseEntry<ResultModel>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(BaseEntry<ResultModel> result) {
                            if (result != null) {
                                if (result.getCode() == 0) {
                                    if (result.getData().getIs_bind_third() == 0) {
                                        if (mListener != null) {
                                            BaseEntry<ResultModel> resultModelBaseEntry = new BaseEntry<>();
                                            ResultModel model = new ResultModel();
                                            model.setUser_id(result.getData().getUser_id());
                                            resultModelBaseEntry.setData(model);
                                            mListener.onState((Activity) context, LoginResult.successOf(
                                                    LTResultCode.STATE_WX_BIND_SUCCESS,
                                                    resultModelBaseEntry));
                                        }
                                        //用户ID
                                        if (result.getData().getUser_id() != 0) {
                                            PreferencesUtils.putInt(context, Constants.USER_LT_UID,
                                                    result.getData().getUser_id());
                                        }
                                    } else {
                                        mListener.onState((Activity) context,
                                                LoginResult.failOf(
                                                        LTGameError.make(LTResultCode.STATE_WX_ALREADY_BIND)));
                                    }

                                } else {
                                    sendException(context, result.getCode(), "Bind_WeChat:" + result.getMsg(),
                                            result.getMsg(), mListener);
                                    if (mListener != null) {
                                        mListener.onState((Activity) context, LoginResult.failOf(
                                                LTGameError.make(LTResultCode.STATE_WX_BIND_FAILED,
                                                        result.getMsg())));
                                    }
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            mListener.onState((Activity) context,
                                    LoginResult.failOf(ExceptionHelper.handleException(e)));
                        }

                        @Override
                        public void onComplete() {
                        }
                    });
        } else {
            mListener.onState((Activity) context,
                    LoginResult.failOf(LTResultCode.STATE_CODE_PARAMETERS_ERROR,
                            LTResultCode.STATE_CODE_PARAMETERS_FAILED));
        }


    }

    /**
     * 绑定QQ
     *
     * @param bindID    QQ返回的ID
     * @param account   QQ的账户
     * @param userName  QQ返回的昵称
     * @param mListener 接口回调
     */
    public static void bindQQ(final Context context, String bindID,
                              String account, String userName,
                              final OnLoginStateListener mListener) {
        LTGameOptions options = LTGameCommon.getInstance().options();
        String mLtAppID = "";
        String mADID = "";
        String baseUrl = "";
        String mServerTest = "";
        if (!TextUtils.isEmpty(options.getLtAppId())) {
            mLtAppID = options.getLtAppId();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_APP_ID))) {
            mLtAppID = PreferencesUtils.getString(context, Constants.LT_SDK_APP_ID);
        }
        if (!TextUtils.isEmpty(options.getAdID())) {
            mADID = options.getAdID();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_DEVICE_ADID))) {
            mADID = PreferencesUtils.getString(context, Constants.LT_SDK_DEVICE_ADID);
        }
        if (!TextUtils.isEmpty(options.getISServerTest())) {
            mServerTest = options.getISServerTest();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_SERVER_TEST_TAG))) {
            mServerTest = PreferencesUtils.getString(context, Constants.LT_SDK_SERVER_TEST_TAG);
        }


        if (!TextUtils.isEmpty(mLtAppID) &&
                !TextUtils.isEmpty(mADID) &&
                !TextUtils.isEmpty(bindID) &&
                !TextUtils.isEmpty(account) &&
                !TextUtils.isEmpty(userName) &&
                !TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.USER_LT_UID_KEY))) {
            long LTTime = System.currentTimeMillis() / 1000L;
            Map<String, Object> map = new WeakHashMap<>();
            map.put("dpt", 2);
            map.put("vid", SDK_VERSION);
            map.put("udid", mADID);
            map.put("adid", mADID);
            map.put("bid", AppUtil.getPackageName(context));
            map.put("ukey", PreferencesUtils.getString(context, Constants.USER_LT_UID_KEY));
            WeakHashMap<String, Object> params = new WeakHashMap<>();
            params.put("bind_id", bindID);
            params.put("account", account);
            params.put("username", userName);
            map.put("data", params);
            if (mServerTest.equals(Constants.LT_SERVER_TEST)) {
                baseUrl = Api.TEST_SERVER_URL + SDK_TEST + Api.TEST_SERVER_DOMAIN;
            } else if (mServerTest.equals(Constants.LT_SERVER_OFFICIAL)) {
                baseUrl = Api.FORMAL_SERVER_URL + mLtAppID + Api.FORMAL_SERVER_DOMAIN;
            }
            String mLtToken = MD5Util.md5Decode(
                    (DateUtil.getTimeStamp(DateUtil.getSystemTimeStamp() -
                            PreferencesUtils.getLong(context, Constants.LT_SDK_TIME_STAMP)))
                            + mADID + mADID + AppUtil.getPackageName(context) + 123).substring(3, 28);


            Api.getInstance((Activity) context, baseUrl)
                    .bindQQ(AppUtil.getLanguage(), mLtToken, (int) LTTime, mLtAppID, map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BaseEntry<ResultModel>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(BaseEntry<ResultModel> result) {
                            if (result != null) {
                                if (result.getCode() == 0) {
                                    if (result.getData().getIs_bind_third() == 0) {
                                        if (mListener != null) {
                                            BaseEntry<ResultModel> resultModelBaseEntry = new BaseEntry<>();
                                            ResultModel model = new ResultModel();
                                            model.setUser_id(result.getData().getUser_id());
                                            resultModelBaseEntry.setData(model);
                                            mListener.onState((Activity) context, LoginResult.successOf(
                                                    LTResultCode.STATE_QQ_BIND_SUCCESS,
                                                    resultModelBaseEntry));
                                        }
                                        //用户ID
                                        if (result.getData().getUser_id() != 0) {
                                            PreferencesUtils.putInt(context, Constants.USER_LT_UID,
                                                    result.getData().getUser_id());
                                        }
                                    } else {
                                        mListener.onState((Activity) context,
                                                LoginResult.failOf(
                                                        LTGameError.make(LTResultCode.STATE_QQ_ALREADY_BIND)));
                                    }


                                } else {
                                    sendException(context, result.getCode(), "Bind_QQ:" + result.getMsg(),
                                            result.getMsg(), mListener);
                                    if (mListener != null) {
                                        mListener.onState((Activity) context, LoginResult.failOf(
                                                LTGameError.make(LTResultCode.STATE_QQ_BIND_FAILED,
                                                        result.getMsg())));
                                    }
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            mListener.onState((Activity) context,
                                    LoginResult.failOf(ExceptionHelper.handleException(e)));
                        }

                        @Override
                        public void onComplete() {
                        }
                    });
        } else {
            mListener.onState((Activity) context,
                    LoginResult.failOf(LTResultCode.STATE_CODE_PARAMETERS_ERROR,
                            LTResultCode.STATE_CODE_PARAMETERS_FAILED));
        }

    }

    /**
     * 自动登录
     *
     * @param mListener 接口回调
     */
    public static void autoLogin(final Context context,
                                 final OnLoginStateListener mListener) {
        LTGameOptions options = LTGameCommon.getInstance().options();
        String mLtAppID = "";
        String mADID = "";
        String baseUrl = "";
        String mServerTest = "";
        if (!TextUtils.isEmpty(options.getLtAppId())) {
            mLtAppID = options.getLtAppId();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_APP_ID))) {
            mLtAppID = PreferencesUtils.getString(context, Constants.LT_SDK_APP_ID);
        }
        if (!TextUtils.isEmpty(options.getAdID())) {
            mADID = options.getAdID();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_DEVICE_ADID))) {
            mADID = PreferencesUtils.getString(context, Constants.LT_SDK_DEVICE_ADID);
        }
        if (!TextUtils.isEmpty(options.getISServerTest())) {
            mServerTest = options.getISServerTest();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_SERVER_TEST_TAG))) {
            mServerTest = PreferencesUtils.getString(context, Constants.LT_SDK_SERVER_TEST_TAG);
        }


        if (!TextUtils.isEmpty(mLtAppID) &&
                !TextUtils.isEmpty(mADID) &&
                !TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.USER_LT_UID_KEY))) {
            long LTTime = System.currentTimeMillis() / 1000L;
            Map<String, Object> map = new WeakHashMap<>();
            map.put("dpt", 2);
            map.put("vid", SDK_VERSION);
            map.put("udid", mADID);
            map.put("adid", mADID);
            map.put("bid", AppUtil.getPackageName(context));
            map.put("ukey", PreferencesUtils.getString(context, Constants.USER_LT_UID_KEY));
            WeakHashMap<String, Object> params = new WeakHashMap<>();
            map.put("data", params);
            if (mServerTest.equals(Constants.LT_SERVER_TEST)) {
                baseUrl = Api.TEST_SERVER_URL + SDK_TEST + Api.TEST_SERVER_DOMAIN;
            } else if (mServerTest.equals(Constants.LT_SERVER_OFFICIAL)) {
                baseUrl = Api.FORMAL_SERVER_URL + mLtAppID + Api.FORMAL_SERVER_DOMAIN;
            }

            String finalMADID = mADID;
            String finalMLtAppID = mLtAppID;
            String finalBaseUrl = baseUrl;
            Api.getInstance((Activity) context, baseUrl)
                    .getTime(AppUtil.getLanguage(), "", (int) LTTime, mLtAppID, map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .flatMap(new Function<BaseEntry<ResultModel>, ObservableSource<BaseEntry<ResultModel>>>() {
                        @Override
                        public ObservableSource<BaseEntry<ResultModel>> apply(BaseEntry<ResultModel> resultModelBaseEntry) throws Exception {
                            if (resultModelBaseEntry.getCode() == 0) {
                                String mLTToken = MD5Util.md5Decode(
                                        DateUtil.getTimeStamp(resultModelBaseEntry.getData().getTime())
                                                + finalMADID + finalMADID +
                                                AppUtil.getPackageName(context) + 123).substring(3, 28);
                                return Api.getInstance((Activity) context, finalBaseUrl)
                                        .autoLogin(AppUtil.getLanguage(), mLTToken, (int) LTTime, finalMLtAppID, map);
                            } else {
                                mListener.onState((Activity) context, LoginResult.failOf(
                                        LTResultCode.STATE_GET_SERVER_FAILED,
                                        resultModelBaseEntry.getMsg()));
                            }
                            return null;
                        }
                    }).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BaseEntry<ResultModel>>() {


                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(BaseEntry<ResultModel> result) {
                            if (result != null) {
                                if (result.getCode() == 0) {
                                    if (mListener != null) {
                                        BaseEntry<ResultModel> resultModelBaseEntry = new BaseEntry<>();
                                        ResultModel model = new ResultModel();
                                        model.setUser_id(result.getData().getUser_id());
                                        resultModelBaseEntry.setData(model);
                                        mListener.onState((Activity) context, LoginResult.successOf(
                                                LTResultCode.STATE_AUTO_LOGIN_SUCCESS,
                                                resultModelBaseEntry));
                                    }
                                    //用户ID
                                    if (result.getData().getUser_id() != 0) {
                                        PreferencesUtils.putInt(context, Constants.USER_LT_UID,
                                                result.getData().getUser_id());
                                    }


                                } else {
                                    sendException(context, result.getCode(), "Auto_Login:" + result.getMsg(),
                                            result.getMsg(), mListener);
                                    if (mListener != null) {
                                        mListener.onState((Activity) context, LoginResult.failOf(
                                                LTGameError.make(LTResultCode.STATE_AUTO_LOGIN_FAILED,
                                                        result.getMsg())));
                                    }
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            mListener.onState((Activity) context,
                                    LoginResult.failOf(ExceptionHelper.handleException(e)));
                        }

                        @Override
                        public void onComplete() {

                        }

                    });

        } else {
            mListener.onState((Activity) context,
                    LoginResult.failOf(LTResultCode.STATE_CODE_PARAMETERS_ERROR,
                            LTResultCode.STATE_CODE_PARAMETERS_FAILED));
        }

    }


    /**
     * 绑定游戏角色
     *
     * @param context          上下文
     * @param role_number      角色编号（游戏服务器用户ID）
     * @param role_name        角色名称
     * @param role_sex         角色性别
     * @param role_level       角色等级
     * @param role_create_time 角色创建时间
     * @param server_number    服务器编号
     * @param mListener        接口回调
     */
    public static void bindRole(final Context context, int role_number,
                                String role_name, String role_sex,
                                String role_level, long role_create_time,
                                int server_number,
                                OnLoginStateListener mListener) {
        LTGameOptions options = LTGameCommon.getInstance().options();
        String mLtAppID = "";
        String mADID = "";
        String baseUrl = "";
        String mServerTest = "";
        if (!TextUtils.isEmpty(options.getLtAppId())) {
            mLtAppID = options.getLtAppId();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_APP_ID))) {
            mLtAppID = PreferencesUtils.getString(context, Constants.LT_SDK_APP_ID);
        }
        if (!TextUtils.isEmpty(options.getAdID())) {
            mADID = options.getAdID();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_DEVICE_ADID))) {
            mADID = PreferencesUtils.getString(context, Constants.LT_SDK_DEVICE_ADID);
        }
        if (!TextUtils.isEmpty(options.getISServerTest())) {
            mServerTest = options.getISServerTest();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_SERVER_TEST_TAG))) {
            mServerTest = PreferencesUtils.getString(context, Constants.LT_SDK_SERVER_TEST_TAG);
        }

        if (!TextUtils.isEmpty(mLtAppID) &&
                !TextUtils.isEmpty(mADID) &&
                role_number != 0 &&
                !TextUtils.isEmpty(role_name) &&
                !TextUtils.isEmpty(role_sex) &&
                !TextUtils.isEmpty(role_level) &&
                role_create_time != 0 &&
                server_number != 0 &&
                !TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.USER_LT_UID_KEY))) {
            long LTTime = System.currentTimeMillis() / 1000L;
            Map<String, Object> map = new WeakHashMap<>();
            map.put("dpt", 2);
            map.put("vid", SDK_VERSION);
            map.put("udid", mADID);
            map.put("adid", mADID);
            map.put("bid", AppUtil.getPackageName(context));
            map.put("ukey", PreferencesUtils.getString(context, Constants.USER_LT_UID_KEY));
            WeakHashMap<String, Object> params = new WeakHashMap<>();
            params.put("role_number", role_number);
            params.put("role_name", role_name);
            params.put("role_sex", role_sex);
            params.put("role_level", role_level);
            params.put("role_create_time", role_create_time);
            params.put("server_number", server_number);
            map.put("data", params);
            if (mServerTest.equals(Constants.LT_SERVER_TEST)) {
                baseUrl = Api.TEST_SERVER_URL + SDK_TEST + Api.TEST_SERVER_DOMAIN;
            } else if (mServerTest.equals(Constants.LT_SERVER_OFFICIAL)) {
                baseUrl = Api.FORMAL_SERVER_URL + mLtAppID + Api.FORMAL_SERVER_DOMAIN;
            }

            String mLtToken = MD5Util.md5Decode(
                    (DateUtil.getTimeStamp(DateUtil.getSystemTimeStamp() -
                            PreferencesUtils.getLong(context, Constants.LT_SDK_TIME_STAMP)))
                            + mADID + mADID + AppUtil.getPackageName(context) + 123).substring(3, 28);

            Api.getInstance((Activity) context, baseUrl)
                    .bindRole(AppUtil.getLanguage(), mLtToken, (int) LTTime, mLtAppID, map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BaseEntry>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(BaseEntry result) {
                            if (result != null) {
                                if (result.getCode() == 0) {
                                    mListener.onState((Activity) context, LoginResult.successOf(
                                            LTResultCode.STATE_ROLE_UPLOAD_SUCCESS, result
                                    ));
                                } else {
                                    sendException(context, result.getCode(), "Send_Role:" + result.getMsg(), result.getMsg(),
                                            mListener);
                                    mListener.onState((Activity) context,
                                            LoginResult.failOf(LTResultCode.STATE_ROLE_UPLOAD_FAILED,
                                                    result.getMsg()));
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            mListener.onState((Activity) context,
                                    LoginResult.failOf(ExceptionHelper.handleException(e)));
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            mListener.onState((Activity) context,
                    LoginResult.failOf(LTResultCode.STATE_CODE_PARAMETERS_ERROR,
                            LTResultCode.STATE_CODE_PARAMETERS_FAILED));
        }

    }


    /**
     * 发送错误日志信息
     */
    public static void sendException(final Context context, int code, String msg, String error,
                                     final OnLoginStateListener mListener) {
        LTGameOptions options = LTGameCommon.getInstance().options();
        String mLtAppID = "";
        String mADID = "";
        String baseUrl = "";
        String mServerTest = "";
        if (!TextUtils.isEmpty(options.getLtAppId())) {
            mLtAppID = options.getLtAppId();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_APP_ID))) {
            mLtAppID = PreferencesUtils.getString(context, Constants.LT_SDK_APP_ID);
        }
        if (!TextUtils.isEmpty(options.getAdID())) {
            mADID = options.getAdID();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_DEVICE_ADID))) {
            mADID = PreferencesUtils.getString(context, Constants.LT_SDK_DEVICE_ADID);
        }
        if (!TextUtils.isEmpty(options.getISServerTest())) {
            mServerTest = options.getISServerTest();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_SERVER_TEST_TAG))) {
            mServerTest = PreferencesUtils.getString(context, Constants.LT_SDK_SERVER_TEST_TAG);
        }


        if (!TextUtils.isEmpty(options.getLtAppId()) &&
                !TextUtils.isEmpty(options.getAdID()) &&
                !TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.USER_LT_UID_KEY))) {
            long LTTime = System.currentTimeMillis() / 1000L;
            Map<String, Object> map = new WeakHashMap<>();
            map.put("dpt", 2);
            map.put("vid", SDK_VERSION);
            map.put("udid", mADID);
            map.put("adid", mADID);
            map.put("bid", AppUtil.getPackageName(context));
            map.put("ukey", PreferencesUtils.getString(context, Constants.USER_LT_UID_KEY));
            WeakHashMap<String, Object> params = new WeakHashMap<>();
            params.put("code", code);
            params.put("msg", msg);
            if (TextUtils.isEmpty(error)) {
                params.put("error", "");
            } else {
                params.put("error", error);
            }
            map.put("data", params);
            if (mServerTest.equals(Constants.LT_SERVER_TEST)) {
                baseUrl = Api.TEST_SERVER_URL + SDK_TEST + Api.TEST_SERVER_DOMAIN;
            } else if (mServerTest.equals(Constants.LT_SERVER_OFFICIAL)) {
                baseUrl = Api.FORMAL_SERVER_URL + mLtAppID + Api.FORMAL_SERVER_DOMAIN;
            }

            String finalMADID = mADID;
            String finalMLtAppID = mLtAppID;
            String finalBaseUrl = baseUrl;
            Api.getInstance((Activity) context, baseUrl)
                    .getTime(AppUtil.getLanguage(), "", (int) LTTime, mLtAppID, map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .flatMap(new Function<BaseEntry<ResultModel>, ObservableSource<BaseEntry<ResultModel>>>() {
                        @Override
                        public ObservableSource<BaseEntry<ResultModel>> apply(BaseEntry<ResultModel> resultModelBaseEntry) throws Exception {
                            if (resultModelBaseEntry.getCode() == 0) {
                                String mLTToken = MD5Util.md5Decode(
                                        DateUtil.getTimeStamp(resultModelBaseEntry.getData().getTime())
                                                + finalMADID + finalMADID +
                                                AppUtil.getPackageName(context) + 123).substring(3, 28);
                                return Api.getInstance((Activity) context, finalBaseUrl)
                                        .sendException(AppUtil.getLanguage(), mLTToken, (int) LTTime, finalMLtAppID, map);
                            } else {
                                mListener.onState((Activity) context, LoginResult.failOf(
                                        LTResultCode.STATE_GET_SERVER_FAILED,
                                        resultModelBaseEntry.getMsg()));
                            }
                            return null;
                        }
                    }).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BaseEntry<ResultModel>>() {


                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(BaseEntry<ResultModel> result) {
                            if (result != null) {
                                if (result.getCode() == 0) {
                                    if (mListener != null) {
                                        mListener.onState((Activity) context, LoginResult.successOf(
                                                LTResultCode.STATE_SEND_EXCEPTION_SUCCESS,
                                                result));
                                    }

                                } else {
                                    if (mListener != null) {
                                        mListener.onState((Activity) context, LoginResult.failOf(
                                                LTResultCode.STATE_SEND_EXCEPTION_FAILED,
                                                result.getMsg()));
                                    }
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            mListener.onState((Activity) context,
                                    LoginResult.failOf(ExceptionHelper.handleException(e)));
                        }

                        @Override
                        public void onComplete() {

                        }

                    });

        } else {
            mListener.onState((Activity) context,
                    LoginResult.failOf(LTResultCode.STATE_CODE_PARAMETERS_ERROR,
                            LTResultCode.STATE_CODE_PARAMETERS_FAILED));
        }

    }

    /**
     * 发送错误日志信息
     */
    private static void sendRechargeException(final Context context, int code, String msg, String error,
                                              final OnRechargeStateListener mListener) {
        LTGameOptions options = LTGameCommon.getInstance().options();
        String mLtAppID = "";
        String mADID = "";
        String baseUrl = "";
        String mServerTest = "";
        if (!TextUtils.isEmpty(options.getLtAppId())) {
            mLtAppID = options.getLtAppId();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_APP_ID))) {
            mLtAppID = PreferencesUtils.getString(context, Constants.LT_SDK_APP_ID);
        }
        if (!TextUtils.isEmpty(options.getAdID())) {
            mADID = options.getAdID();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_DEVICE_ADID))) {
            mADID = PreferencesUtils.getString(context, Constants.LT_SDK_DEVICE_ADID);
        }
        if (!TextUtils.isEmpty(options.getISServerTest())) {
            mServerTest = options.getISServerTest();
        } else if (!TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.LT_SDK_SERVER_TEST_TAG))) {
            mServerTest = PreferencesUtils.getString(context, Constants.LT_SDK_SERVER_TEST_TAG);
        }


        if (!TextUtils.isEmpty(options.getLtAppId()) &&
                !TextUtils.isEmpty(options.getAdID()) &&
                !TextUtils.isEmpty(PreferencesUtils.getString(context, Constants.USER_LT_UID_KEY))) {
            long LTTime = System.currentTimeMillis() / 1000L;
            Map<String, Object> map = new WeakHashMap<>();
            map.put("dpt", 2);
            map.put("vid", SDK_VERSION);
            map.put("udid", mADID);
            map.put("adid", mADID);
            map.put("bid", AppUtil.getPackageName(context));
            map.put("ukey", PreferencesUtils.getString(context, Constants.USER_LT_UID_KEY));
            WeakHashMap<String, Object> params = new WeakHashMap<>();
            params.put("code", code);
            params.put("msg", msg);
            if (TextUtils.isEmpty(error)) {
                params.put("error", "");
            } else {
                params.put("error", error);
            }
            map.put("data", params);
            if (mServerTest.equals(Constants.LT_SERVER_TEST)) {
                baseUrl = Api.TEST_SERVER_URL + SDK_TEST + Api.TEST_SERVER_DOMAIN;
            } else if (mServerTest.equals(Constants.LT_SERVER_OFFICIAL)) {
                baseUrl = Api.FORMAL_SERVER_URL + mLtAppID + Api.FORMAL_SERVER_DOMAIN;
            }

            String finalMADID = mADID;
            String finalMLtAppID = mLtAppID;
            String finalBaseUrl = baseUrl;
            Api.getInstance((Activity) context, baseUrl)
                    .getTime(AppUtil.getLanguage(), "", (int) LTTime, mLtAppID, map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .flatMap(new Function<BaseEntry<ResultModel>, ObservableSource<BaseEntry<ResultModel>>>() {
                        @Override
                        public ObservableSource<BaseEntry<ResultModel>> apply(BaseEntry<ResultModel> resultModelBaseEntry) throws Exception {
                            if (resultModelBaseEntry.getCode() == 0) {
                                String mLTToken = MD5Util.md5Decode(
                                        DateUtil.getTimeStamp(resultModelBaseEntry.getData().getTime())
                                                + finalMADID + finalMADID +
                                                AppUtil.getPackageName(context) + 123).substring(3, 28);
                                return Api.getInstance((Activity) context, finalBaseUrl)
                                        .sendException(AppUtil.getLanguage(), mLTToken, (int) LTTime, finalMLtAppID, map);
                            } else {
                                mListener.onState((Activity) context, RechargeResult.failOf(
                                        LTResultCode.STATE_GET_SERVER_FAILED,
                                        resultModelBaseEntry.getMsg()));
                            }
                            return null;
                        }
                    }).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BaseEntry<ResultModel>>() {


                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(BaseEntry<ResultModel> result) {
                            if (result != null) {
                                if (result.getCode() == 0) {
                                    if (mListener != null) {
                                        mListener.onState((Activity) context, RechargeResult.successOf(
                                                LTResultCode.STATE_SEND_EXCEPTION_SUCCESS,
                                                result));
                                    }

                                } else {
                                    if (mListener != null) {
                                        mListener.onState((Activity) context, RechargeResult.failOf(
                                                LTResultCode.STATE_SEND_EXCEPTION_FAILED,
                                                result.getMsg()));
                                    }
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            mListener.onState((Activity) context,
                                    RechargeResult.failOf(ExceptionHelper.handleException(e)));
                        }

                        @Override
                        public void onComplete() {

                        }

                    });

        }
    }

}
