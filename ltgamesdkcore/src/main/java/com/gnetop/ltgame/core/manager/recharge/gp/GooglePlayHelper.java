package com.gnetop.ltgame.core.manager.recharge.gp;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.gnetop.ltgame.core.common.Constants;
import com.gnetop.ltgame.core.exception.LTGameError;
import com.gnetop.ltgame.core.exception.LTResultCode;
import com.gnetop.ltgame.core.impl.OnRechargeStateListener;
import com.gnetop.ltgame.core.manager.login.fb.FacebookEventManager;
import com.gnetop.ltgame.core.manager.lt.LoginRealizeManager;
import com.gnetop.ltgame.core.model.RechargeResult;
import com.gnetop.ltgame.core.platform.Target;
import com.gnetop.ltgame.core.util.PreferencesUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GooglePlayHelper {
    private static final String TAG = GooglePlayHelper.class.getSimpleName();

    //订单号
    private String mOrderID;
    //商品集合
    private WeakReference<Activity> mActivityRef;
    private int mRechargeTarget;
    private OnRechargeStateListener mListener;
    //商品
    private String mSku;
    private int role_number;//  角色编号（游戏服务器用户ID）
    private int server_number;// 服务器编号（游戏提供）
    private String goods_number;//  商品ID，游戏提供
    private BillingClient mBillingClient;
    private int mPayTest;
    private String mConsume = "0";
    //自定义参数
    private Map<String, Object> mParams;

    GooglePlayHelper(Activity activity, int role_number,
                     int server_number, String goods_number, Map<String, Object> mParams, int mPayTest,
                     String sku, OnRechargeStateListener mListener) {
        this.mActivityRef = new WeakReference<>(activity);
        this.role_number = role_number;
        this.server_number = server_number;
        this.goods_number = goods_number;
        this.mParams = mParams;
        this.mSku = sku;
        this.mPayTest = mPayTest;
        this.mRechargeTarget = Target.RECHARGE_GOOGLE;
        this.mListener = mListener;
    }

    public GooglePlayHelper(Activity activity, int mPayTest) {
        this.mActivityRef = new WeakReference<>(activity);
        this.mPayTest = mPayTest;
    }


    /**
     * 初始化
     */
    void init() {
        mBillingClient = BillingClient.newBuilder(mActivityRef.get())
                .setListener(mPurchasesUpdatedListener)
                .enablePendingPurchases()
                .build();
        if (!mBillingClient.isReady()) {
            mBillingClient.startConnection(new BillingClientStateListener() {
                @Override
                public void onBillingSetupFinished(BillingResult billingResult) {
                    if (billingResult != null) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            if (!TextUtils.isEmpty(PreferencesUtils.getString(mActivityRef.get(),
                                    Constants.USER_LT_UID_KEY))) {
                                getLTOrderID();
                            } else {
                                mListener.onState(mActivityRef.get(), RechargeResult.failOf(LTGameError.make(
                                        LTResultCode.STATE_GP_CREATE_ORDER_FAILED,
                                        "order create failed:user key is empty"
                                )));
                                mActivityRef.get().finish();
                            }
                        }
                    }

                }

                @Override
                public void onBillingServiceDisconnected() {
                }
            });
        } else {
            if (!TextUtils.isEmpty(PreferencesUtils.getString(mActivityRef.get(),
                    Constants.USER_LT_UID_KEY))) {
                getLTOrderID();
            } else {
                mListener.onState(mActivityRef.get(), RechargeResult.failOf(LTGameError.make(
                        LTResultCode.STATE_GP_CREATE_ORDER_FAILED,
                        "order create failed:user key is empty"
                )));
                mActivityRef.get().finish();
            }
        }

    }


    /**
     * 购买回调
     */
    private PurchasesUpdatedListener mPurchasesUpdatedListener = new PurchasesUpdatedListener() {
        @Override
        public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> list) {
            String debugMessage = billingResult.getDebugMessage();
            Log.e(TAG, debugMessage);
            if (list != null && list.size() > 0) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    for (Purchase purchase : list) {
                        mConsume = "2";
                        uploadToServer(purchase.getPurchaseToken(), purchase.getOrderId(),mOrderID, mPayTest);
                    }

                }
            } else {
                switch (billingResult.getResponseCode()) {
                    case BillingClient.BillingResponseCode.SERVICE_TIMEOUT: {//服务连接超时
                        mListener.onState(mActivityRef.get(), RechargeResult.failOf("-3"));
                        mActivityRef.get().finish();
                        break;
                    }
                    case BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED: {
                        mListener.onState(mActivityRef.get(), RechargeResult.failOf("-2"));
                        mActivityRef.get().finish();
                        break;
                    }
                    case BillingClient.BillingResponseCode.SERVICE_DISCONNECTED: {//服务未连接
                        mListener.onState(mActivityRef.get(), RechargeResult.failOf("-1"));
                        mActivityRef.get().finish();
                        break;
                    }
                    case BillingClient.BillingResponseCode.USER_CANCELED: {//取消
                        mListener.onState(mActivityRef.get(), RechargeResult.failOf("1"));
                        mActivityRef.get().finish();
                        break;
                    }
                    case BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE: {//服务不可用
                        mListener.onState(mActivityRef.get(), RechargeResult.failOf("2"));
                        mActivityRef.get().finish();
                        break;
                    }
                    case BillingClient.BillingResponseCode.BILLING_UNAVAILABLE: {//购买不可用
                        mListener.onState(mActivityRef.get(), RechargeResult.failOf("3"));
                        mActivityRef.get().finish();
                        break;
                    }
                    case BillingClient.BillingResponseCode.ITEM_UNAVAILABLE: {//商品不存在
                        mListener.onState(mActivityRef.get(), RechargeResult.failOf("4"));
                        mActivityRef.get().finish();
                        break;
                    }
                    case BillingClient.BillingResponseCode.DEVELOPER_ERROR: {//提供给 API 的无效参数
                        mListener.onState(mActivityRef.get(), RechargeResult.failOf("5"));
                        mActivityRef.get().finish();
                        break;
                    }
                    case BillingClient.BillingResponseCode.ERROR: {//错误
                        mListener.onState(mActivityRef.get(), RechargeResult.failOf("6"));
                        mActivityRef.get().finish();
                        break;
                    }
                    case BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED: {//未消耗掉
                        mConsume = "1";
                        queryHistory();
                        break;
                    }
                    case BillingClient.BillingResponseCode.ITEM_NOT_OWNED: {//不可购买
                        mListener.onState(mActivityRef.get(), RechargeResult.failOf("8"));
                        mActivityRef.get().finish();
                        break;
                    }
                }
            }
        }
    };

    /**
     * 购买
     */
    private void recharge() {
        if (mBillingClient.isReady()) {
            List<String> skuList = new ArrayList<>();
            skuList.add(mSku);
            SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
            params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
            mBillingClient.querySkuDetailsAsync(params.build(),
                    new SkuDetailsResponseListener() {
                        @Override
                        public void onSkuDetailsResponse(BillingResult billingResult,
                                                         List<SkuDetails> skuDetailsList) {
                            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                                    && skuDetailsList != null) {
                                for (SkuDetails skuDetails : skuDetailsList) {
                                    String sku = skuDetails.getSku();
                                    if (mSku.equals(sku)) {
                                        BillingFlowParams purchaseParams =
                                                BillingFlowParams.newBuilder()
                                                        .setSkuDetails(skuDetails)
                                                        .setObfuscatedAccountId(role_number + "")
                                                        .setObfuscatedProfileId(mOrderID)
                                                        .setDeveloperId(mOrderID)
                                                        .build();
                                        mBillingClient.launchBillingFlow(mActivityRef.get(), purchaseParams);
                                    }
                                }
                            }

                        }
                    });

        }


    }


    /**
     * 消耗
     */
    private void consume(String purchaseToken) {
        if (mBillingClient.isReady()) {
            ConsumeParams consumeParams = ConsumeParams.newBuilder()
                    .setPurchaseToken(purchaseToken)
                    .build();
            mBillingClient.consumeAsync(consumeParams, new ConsumeResponseListener() {
                @Override
                public void onConsumeResponse(BillingResult billingResult, String s) {

                }
            });
        } else {
            mBillingClient.startConnection(new BillingClientStateListener() {
                @Override
                public void onBillingSetupFinished(BillingResult billingResult) {
                    if (billingResult != null) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            ConsumeParams consumeParams = ConsumeParams.newBuilder()
                                    .setPurchaseToken(purchaseToken)
                                    .build();
                            mBillingClient.consumeAsync(consumeParams, new ConsumeResponseListener() {
                                @Override
                                public void onConsumeResponse(BillingResult billingResult, String s) {

                                }
                            });
                        }
                    }

                }

                @Override
                public void onBillingServiceDisconnected() {
                }
            });
        }


    }

    /**
     * 消耗
     */
    private void consume2(String purchaseToken) {
        if (mBillingClient.isReady()) {
            ConsumeParams consumeParams = ConsumeParams.newBuilder()
                    .setPurchaseToken(purchaseToken)
                    .build();
            mBillingClient.consumeAsync(consumeParams, new ConsumeResponseListener() {
                @Override
                public void onConsumeResponse(BillingResult billingResult, String s) {

                }
            });
            recharge();
        } else {
            mBillingClient.startConnection(new BillingClientStateListener() {
                @Override
                public void onBillingSetupFinished(BillingResult billingResult) {
                    if (billingResult != null) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            ConsumeParams consumeParams = ConsumeParams.newBuilder()
                                    .setPurchaseToken(purchaseToken)
                                    .build();
                            mBillingClient.consumeAsync(consumeParams, new ConsumeResponseListener() {
                                @Override
                                public void onConsumeResponse(BillingResult billingResult, String s) {

                                }
                            });
                        }
                    }

                }

                @Override
                public void onBillingServiceDisconnected() {
                }
            });
            recharge();
        }


    }

    /**
     * 补单操作
     */
    private void queryHistory() {
        Purchase.PurchasesResult mResult = mBillingClient.queryPurchases(BillingClient.SkuType.INAPP);
        for (int i = 0; i < mResult.getPurchasesList().size(); i++) {
            if (mResult.getPurchasesList().get(i).isAcknowledged()) {
                consume2(mResult.getPurchasesList().get(i).getPurchaseToken());
            } else {
                uploadToServer2(mResult.getPurchasesList().get(i).getPurchaseToken(),
                        mResult.getPurchasesList().get(i).getOrderId(),
                        mResult.getPurchasesList().get(i).getAccountIdentifiers().getObfuscatedProfileId(),
                        mPayTest);
            }
        }


    }

    /**
     * 获取乐推订单ID
     */
    private void getLTOrderID() {
        LoginRealizeManager.createOrder(mActivityRef.get(), role_number, server_number, goods_number,
                mParams,
                new OnRechargeStateListener() {

                    @Override
                    public void onState(Activity activity, RechargeResult result) {
                        if (result != null) {
                            if (result.getResultModel() != null) {
                                if (result.getResultModel().getData() != null) {
                                    if (result.getResultModel().getCode() == 0) {
                                        if (result.getResultModel().getData().getOrder_number() != null) {
                                            mOrderID = result.getResultModel().getData().getOrder_number();
                                            PreferencesUtils.init(mActivityRef.get());
                                            PreferencesUtils.putString(mActivityRef.get(), Constants.LT_ORDER_ID, mOrderID);
                                            recharge();
                                        }
                                    } else {
                                        mListener.onState(mActivityRef.get(),
                                                RechargeResult.failOf(result.getMsg()));
                                        mActivityRef.get().finish();
                                        activity.finish();
                                    }

                                }

                            }
                        }
                    }

                });

    }

    /**
     * 上传到服务器验证
     */
    private void uploadToServer(final String purchaseToken,String mGoogleOrder, String mOrderID, int mPayTest) {
        LoginRealizeManager.googlePlay(mActivityRef.get(),
                purchaseToken, mOrderID, mPayTest, new OnRechargeStateListener() {
                    @Override
                    public void onState(Activity activity, RechargeResult result) {
                        if (result != null) {
                            if (result.getResultModel() != null) {
                                if (result.getResultModel().getCode() == 0) {
                                    FacebookEventManager.getInstance().recharge(mActivityRef.get(),
                                            result.getResultModel().getData().getGoods_price(),
                                            result.getResultModel().getData().getGoods_price_type(),
                                            result.getResultModel().getData().getOrder_number());
                                    mListener.onState(mActivityRef.get(), RechargeResult
                                            .successOf(result.getResultModel()));
                                    consume(purchaseToken);
                                } else if (result.getResultModel().getCode() == 10500) {
                                    uploadToServer(purchaseToken,mGoogleOrder, mOrderID, mPayTest);
                                } else {
                                    LoginRealizeManager.sendGooglePlayFailed(mActivityRef.get(), mOrderID, purchaseToken,
                                            mGoogleOrder,
                                            mPayTest, result.getResultModel().getMsg(), mListener);
                                }
                            }

                        }

                    }

                });

    }

    /**
     * 上传到服务器验证
     */
    private void uploadToServer2(final String purchaseToken,String mGoogleOrder, String mOrderID, int mPayTest) {
        LoginRealizeManager.googlePlay(mActivityRef.get(),
                purchaseToken, mOrderID, mPayTest, new OnRechargeStateListener() {
                    @Override
                    public void onState(Activity activity, RechargeResult result) {
                        if (result != null) {
                            if (result.getResultModel() != null) {
                                if (result.getResultModel().getCode() == 0) {
                                    FacebookEventManager.getInstance().recharge(mActivityRef.get(),
                                            result.getResultModel().getData().getGoods_price(),
                                            result.getResultModel().getData().getGoods_price_type(),
                                            result.getResultModel().getData().getOrder_number());
                                    consume2(purchaseToken);
                                    if (mConsume.equals("1")) {
                                        recharge();
                                    }
                                } else if (result.getResultModel().getCode() == 10500) {
                                    uploadToServer2(purchaseToken,mGoogleOrder, mOrderID, mPayTest);
                                } else {
                                    LoginRealizeManager.sendGooglePlayFailed(mActivityRef.get(), mOrderID, purchaseToken,
                                            mGoogleOrder,
                                            mPayTest, result.getResultModel().getMsg(), mListener);
                                }
                            }

                        }

                    }

                });

    }

    /**
     * 上传到服务器验证
     */
    private void uploadToServer3(final String purchaseToken, String mGoogleOrder,String mOrderID, int mPayTest) {
        LoginRealizeManager.googlePlay(mActivityRef.get(),
                purchaseToken, mOrderID, mPayTest, new OnRechargeStateListener() {
                    @Override
                    public void onState(Activity activity, RechargeResult result) {
                        if (result != null) {
                            if (result.getResultModel() != null) {
                                if (result.getResultModel().getCode() == 0) {
                                    FacebookEventManager.getInstance().recharge(mActivityRef.get(),
                                            result.getResultModel().getData().getGoods_price(),
                                            result.getResultModel().getData().getGoods_price_type(),
                                            result.getResultModel().getData().getOrder_number());
                                    consume2(purchaseToken);
                                } else if (result.getResultModel().getCode() == 10500) {
                                    uploadToServer3(purchaseToken,mGoogleOrder, mOrderID, mPayTest);
                                } else {
                                    LoginRealizeManager.sendGooglePlayFailed(mActivityRef.get(), mOrderID, purchaseToken,
                                            mGoogleOrder,
                                            mPayTest, result.getResultModel().getMsg(), mListener);
                                }
                            }

                        }

                    }

                });

    }

    /**
     * 释放
     */
    void release() {
        if (mBillingClient.isReady()) {
            mBillingClient.endConnection();
        }
    }

    /**
     * 补单操作
     */
    public void addOrder() {
        mBillingClient = BillingClient.newBuilder(mActivityRef.get())
                .setListener(mPurchasesUpdatedListener)
                .enablePendingPurchases()
                .build();
        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult != null) {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        Purchase.PurchasesResult mResult = mBillingClient.queryPurchases(BillingClient.SkuType.INAPP);
                        for (int i = 0; i < mResult.getPurchasesList().size(); i++) {
                            if (mResult.getPurchasesList().get(i).isAcknowledged()) {
                                consume2(mResult.getPurchasesList().get(i).getPurchaseToken());
                            } else {
                                uploadToServer3(mResult.getPurchasesList().get(i).getPurchaseToken(),
                                        mResult.getPurchasesList().get(i).getOrderId(),
                                        mResult.getPurchasesList().get(i).getAccountIdentifiers().getObfuscatedProfileId(),
                                        mPayTest);
                            }
                        }
                    }
                }

            }

            @Override
            public void onBillingServiceDisconnected() {
            }
        });
    }


}
