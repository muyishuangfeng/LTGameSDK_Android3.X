package com.gnetop.ltgame.core.platform;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.gnetop.ltgame.core.base.BaseActionActivity;
import com.gnetop.ltgame.core.impl.OnLoginStateListener;
import com.gnetop.ltgame.core.model.LoginObject;

import java.util.Map;

public abstract class AbsPlatform implements IPlatform {


    protected String mAppId;
    protected String mAppName;
    protected int mTarget;
    protected String mClientID;
    protected boolean mISBaseUrl;
    protected String mAdID;
    protected String mPackageID;
    protected int mSelfRequestCode;
    protected int mPayTest;
    protected String mSku;
    protected Map<String, Object> mParams;
    protected String mPublicKey;
    protected String mProductID;
    protected String mPayType;
    protected String mPhone;
    protected String mPassword;
    protected String mLoginCode;
    protected String mQqAppID;


    public AbsPlatform(Context context, String appId,  int target) {
        mAppId = appId;
        mTarget = target;
    }
    public AbsPlatform(Context context, String appId, String appName,  int target) {
        mAppId = appId;
        mAppName = appName;
        mTarget = target;
    }


    public AbsPlatform(Context context, String appId, int payTest, int target) {
        mAppId = appId;
        mPayTest = payTest;
        mTarget = target;
    }

    public AbsPlatform(Context context, String appId,  int payTest, String publicKey,
                       int selfRequestCode, String sku, String productID, Map<String, Object> params,
                       String payType, int target) {
        mAppId = appId;
        mPayTest = payTest;
        mPublicKey = publicKey;
        mProductID = productID;
        mPayType = payType;
        mSelfRequestCode = selfRequestCode;
        mSku = sku;
        mParams = params;
        mTarget = target;
    }

    public AbsPlatform(Context context, boolean baseUrl, String appId,
                       String qqAppID, int target) {
        mAppId = appId;
        mISBaseUrl = baseUrl;
        mQqAppID = qqAppID;
        mTarget = target;
    }

    public AbsPlatform(Context context, String appId, int payTest, String publicKey,
                       int selfRequestCode, String sku, String productID, Map<String, Object> params,
                       int target) {
        mAppId = appId;
        mPayTest = payTest;
        mPublicKey = publicKey;
        mProductID = productID;
        mSelfRequestCode = selfRequestCode;
        mSku = sku;
        mParams = params;
        mTarget = target;
    }

    public AbsPlatform(Context context, String appId,  boolean baseUrl, String phone,
                       String password, String loginCode, int target) {
        mAppId = appId;
        mISBaseUrl = baseUrl;
        mLoginCode = loginCode;
        mPhone = phone;
        mPassword = password;
        mTarget = target;
    }

    public AbsPlatform(Context context, boolean baseUrl, String appId,  String clientID,
                       String adID, String packageID, int selfRequestCode, int target) {
        mISBaseUrl = baseUrl;
        mAppId = appId;
        mClientID = clientID;
        mAdID = adID;
        mPackageID = packageID;
        mSelfRequestCode = selfRequestCode;
        mTarget = target;
    }

    public AbsPlatform(Context context, boolean baseUrl, String appId,  String adID,
                       String packageID, int target) {
        mISBaseUrl = baseUrl;
        mAppId = appId;
        mAdID = adID;
        mPackageID = packageID;
        mTarget = target;
    }


    @Override
    public Class getUIKitClazz() {
        return null;
    }

    @Override
    public void handleIntent(Activity intent) {

    }

    @Override
    public void onResponse(Object response) {

    }

    @Override
    public boolean isInstall(Context context) {
        return false;
    }

    @Override
    public void login(Activity activity, int target, LoginObject object, OnLoginStateListener listener) {
        throw new UnsupportedOperationException("该平台不支持登录操作～");
    }

    @Override
    public void onActivityResult(BaseActionActivity activity, int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void recycle() {

    }

    public boolean checkPlatformConfig() {
        return !TextUtils.isEmpty(mAppId);
    }
}
