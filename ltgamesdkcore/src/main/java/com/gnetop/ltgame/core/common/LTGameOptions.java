package com.gnetop.ltgame.core.common;


import android.content.Context;

import com.gnetop.ltgame.core.util.FileUtil;

import java.io.File;

/**
 * 配置项
 */
public class LTGameOptions {
    //缓存
    private static final String SHARE_CACHE_DIR_NAME = "LT_SDK_CATCH";
    //是否debug
    private boolean debug;
    //appID
    private String wxAppId;
    //Google客户端ID
    private String googleClientID;
    //乐推AppID
    private String ltAppId;
    //广告ID（唯一）
    private String adID;
    //包名
    private String packageID;
    //是否沙盒测试
    private int mPayTest;
    //公钥
    private String mPublicKey;
    //是否是测试服务器
    private String mISServerTest;
    //QQ平台的AppID
    private String qqAppId;
    //Facebook平台的AppID
    private String fbAppID;
    private String wxSecretKey;
    //缓存
    private String cacheDir;
    //是否支持邮箱登录
    private boolean isEmailEnable;
    //是否支持Facebook登录
    private boolean isFBEnable;
    //是否支持Google支付
    private boolean isGPEnable;
    //是否支持Google登录
    private boolean isGoogleEnable;
    //是否支持QQ登录
    private boolean isQQEnable;
    //是否支持微信登录
    private boolean isWeChatEnable;
    //是否支持游客登录
    private boolean isGuestEnable;
    //是否支持OneStore支付
    private boolean isOneStoreEnable;
    private long tokenExpiresHours;
    private boolean wxOnlyAuthCode;
    //国内还是国外
    private String mCountryModel;
    private String mAgreementUrl;//用户协议
    private String mPrivacyUrl;//隐私政策

    public long getTokenExpiresHoursMs() {
        if (tokenExpiresHours <= 0) {
            return 0;
        }
        return tokenExpiresHours * 60 * 60 * 1000;
    }

    public long getTokenExpiresHours() {
        if (tokenExpiresHours <= 0) {
            return 0;
        }
        return tokenExpiresHours;

    }

    public String getCountryModel() {
        return mCountryModel;
    }

    public void setCountryModel(String mCountryModel) {
        this.mCountryModel = mCountryModel;
    }

    public boolean isEmailEnable() {
        return isEmailEnable;
    }

    public void setEmailEnable(boolean emailEnable) {
        isEmailEnable = emailEnable;
    }

    public boolean isFBEnable() {
        return isFBEnable;
    }

    public void setFBEnable(boolean FBEnable) {
        isFBEnable = FBEnable;
    }

    public boolean isGPEnable() {
        return isGPEnable;
    }

    public void setGPEnable(boolean GPEnable) {
        isGPEnable = GPEnable;
    }

    public boolean isGoogleEnable() {
        return isGoogleEnable;
    }

    public void setGoogleEnable(boolean googleEnable) {
        isGoogleEnable = googleEnable;
    }

    public boolean isQQEnable() {
        return isQQEnable;
    }

    public void setQQEnable(boolean QQEnable) {
        isQQEnable = QQEnable;
    }

    public boolean isWeChatEnable() {
        return isWeChatEnable;
    }

    public void setWeChatEnable(boolean weChatEnable) {
        isWeChatEnable = weChatEnable;
    }

    public boolean isGuestEnable() {
        return isGuestEnable;
    }

    public void setGuestEnable(boolean guestEnable) {
        isGuestEnable = guestEnable;
    }


    public boolean isOneStoreEnable() {
        return isOneStoreEnable;
    }

    public void setOneStoreEnable(boolean oneStoreEnable) {
        isOneStoreEnable = oneStoreEnable;
    }

    public String getISServerTest() {
        return mISServerTest;
    }

    public void setISServerTest(String mISServerTest) {
        this.mISServerTest = mISServerTest;
    }

    public boolean isWxOnlyAuthCode() {
        return wxOnlyAuthCode;
    }

    public String getWxSecretKey() {
        return wxSecretKey;
    }

    public String getWxAppId() {
        return wxAppId;
    }

    public String getGoogleClientID() {
        return googleClientID;
    }

    public String getCacheDir() {
        return cacheDir;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }


    public String getLtAppId() {
        return ltAppId;
    }


    public String getAdID() {
        return adID;
    }

    public String getPackageID() {
        return packageID;
    }

    @Override
    public String toString() {
        return "LTGameOptions{" +
                "debug=" + debug +
                ", wxAppId='" + wxAppId + '\'' +
                ", googleClientID='" + googleClientID + '\'' +
                ", ltAppId='" + ltAppId + '\'' +
                ", adID='" + adID + '\'' +
                ", packageID='" + packageID + '\'' +
                ", mPayTest=" + mPayTest +
                ", mPublicKey='" + mPublicKey + '\'' +
                ", mISServerTest=" + mISServerTest +
                ", qqAppId='" + qqAppId + '\'' +
                ", fbAppID='" + fbAppID + '\'' +
                ", wxSecretKey='" + wxSecretKey + '\'' +
                ", cacheDir='" + cacheDir + '\'' +
                ", isEmailEnable=" + isEmailEnable +
                ", isFBEnable=" + isFBEnable +
                ", isGPEnable=" + isGPEnable +
                ", isGoogleEnable=" + isGoogleEnable +
                ", isQQEnable=" + isQQEnable +
                ", isWeChatEnable=" + isWeChatEnable +
                ", mCountryModel=" + mCountryModel +
                ", isGuestEnable=" + isGuestEnable +
                ", isOneStoreEnable=" + isOneStoreEnable +
                '}';
    }


    public String getFBAppID() {
        return fbAppID;
    }

    public int getPayTest() {
        return mPayTest;
    }


    public String getPublicKey() {
        return mPublicKey;
    }


    private LTGameOptions(Builder builder) {
        if (builder.wxOnlyAuthCode == null) {
            builder.wxOnlyAuthCode = false;
        }
        if (builder.tokenExpiresHours < 0) {
            builder.tokenExpiresHours = 0;
        }
        this.debug = builder.debug;
        this.wxAppId = builder.wxAppId;
        // qq 配置
        this.qqAppId = builder.qqAppId;
        this.googleClientID = builder.googleClientID;
        //乐推
        this.ltAppId = builder.ltAppId;
        this.adID = builder.adID;
        this.mCountryModel = builder.mCountryModel;
        this.packageID = builder.packageID;
        this.mPrivacyUrl = builder.mPrivacyUrl;
        this.mAgreementUrl = builder.mAgreementUrl;
        this.fbAppID = builder.fbAppID;
        this.mPayTest = builder.mPayTest;
        this.wxSecretKey = builder.wxSecretKey;
        this.mPublicKey = builder.mPublicKey;
        this.cacheDir = builder.cacheDir;
        this.mISServerTest = builder.mISServerTest;
        this.isEmailEnable = builder.isEmailEnable;
        this.isFBEnable = builder.isFBEnable;
        this.isQQEnable = builder.isQQEnable;
        this.isGoogleEnable = builder.isGoogleEnable;
        this.isGuestEnable = builder.isGuestEnable;
        this.isWeChatEnable = builder.isWeChatEnable;
        this.isGPEnable = builder.isGPEnable;
        this.isOneStoreEnable = builder.isOneStoreEnable;
        this.tokenExpiresHours = builder.tokenExpiresHours;
        this.wxOnlyAuthCode = builder.wxOnlyAuthCode;
    }


    /**
     * Builder
     */
    public static class Builder {
        //国内还是国外
        private String mCountryModel;
        //是否支持邮箱登录
        private boolean isEmailEnable;
        //是否支持Facebook登录
        private boolean isFBEnable;
        //是否支持Google支付
        private boolean isGPEnable;
        //是否支持Google登录
        private boolean isGoogleEnable;
        //是否支持QQ登录
        private boolean isQQEnable;
        //是否支持微信登录
        private boolean isWeChatEnable;
        //是否支持游客登录
        private boolean isGuestEnable;
        //是否支持OneStore支付
        private boolean isOneStoreEnable;
        // 调试配置
        private boolean debug;
        //缓存
        private String cacheDir;
        // 微信配置
        private String wxAppId;
        private Boolean wxOnlyAuthCode;
        // qq 配置
        private String qqAppId;
        // google配置
        private String googleClientID;
        private String mAgreementUrl;//用户协议
        private String mPrivacyUrl;//隐私政策
        // token 失效时间，默认立刻失效
        private int tokenExpiresHours = -1;

        //乐推AppID
        private String ltAppId;
        //广告ID（唯一）
        private String adID;
        //包名
        private String packageID;
        //facebook  AppID
        private String fbAppID;
        //是否沙盒测试
        private int mPayTest;
        //公钥
        private String mPublicKey;
        //是否是测试服务器:0是，1，不是
        private String mISServerTest;
        private String wxSecretKey;
        private Context context;


        public Builder(Context context) {
            this.context = context;
        }


        public Builder setFBiD(String facebookAppID) {
            this.fbAppID = facebookAppID;
            this.isFBEnable = true;
            return this;
        }

        public Builder emailEnable() {
            this.isEmailEnable = true;
            return this;
        }


        public Builder setQQ(String qqAppId) {
            this.qqAppId = qqAppId;
            this.isQQEnable = true;
            return this;
        }

        public Builder setWX(String wxAppId, String wxSecretKey) {
            this.wxAppId = wxAppId;
            this.wxSecretKey = wxSecretKey;
            this.isWeChatEnable = true;
            return this;
        }


        public Builder wx(String wxAppId, String wxSecretKey, boolean wxOnlyAuthCode) {
            this.wxOnlyAuthCode = wxOnlyAuthCode;
            this.wxSecretKey = wxSecretKey;
            this.wxAppId = wxAppId;
            this.isWeChatEnable = true;
            return this;
        }

        public Builder setGoogle(String googleClientID) {
            this.googleClientID = googleClientID;
            this.isGoogleEnable = true;
            return this;
        }

        public Builder setTokenTime(int time) {
            this.tokenExpiresHours = time;
            return this;
        }

        public Builder setCountryModel(String countryModel) {
            this.mCountryModel = countryModel;
            return this;
        }

        public Builder appID(String ltAppId) {
            this.ltAppId = ltAppId;
            return this;
        }

        public Builder setAgreementUrl(String mAgreementUrl) {
            this.mAgreementUrl = mAgreementUrl;
            return this;
        }

        public Builder setPrivacyUrl(String mPrivacyUrl) {
            this.mPrivacyUrl = mPrivacyUrl;
            return this;
        }


        public Builder setAdID(String adID) {
            this.adID = adID;
            return this;
        }

        public Builder packageID(String packageID) {
            this.packageID = packageID;
            return this;
        }

        public Builder setOneStore(String publicKey) {
            this.mPublicKey = publicKey;
            this.isOneStoreEnable = true;
            return this;
        }

        public Builder setGuest() {
            this.isGuestEnable = true;
            return this;
        }

        public Builder setGP() {
            this.isGPEnable = true;
            return this;
        }


        public Builder payTest(int payTest) {
            this.mPayTest = payTest;
            return this;
        }


        public Builder debug(boolean debug) {
            this.debug = debug;
            return this;
        }


        public Builder isServerTest(String mISServerTest) {
            this.mISServerTest = mISServerTest;
            return this;
        }


        public LTGameOptions build() {
            File storageDir = new File(context.getExternalCacheDir(), SHARE_CACHE_DIR_NAME);
            if (!FileUtil.isExist(storageDir)) {
                storageDir.mkdirs();
            }
            this.cacheDir = storageDir.getAbsolutePath();
            return new LTGameOptions(this);
        }
    }
}
