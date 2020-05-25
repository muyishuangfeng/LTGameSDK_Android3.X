package com.gnetop.ltgame.core.manager.recharge.gp;

import android.app.Activity;
import android.text.TextUtils;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchaseHistoryRecord;
import com.android.billingclient.api.PurchaseHistoryResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.gnetop.ltgame.core.common.Constants;
import com.gnetop.ltgame.core.exception.LTGameError;
import com.gnetop.ltgame.core.exception.LTResultCode;
import com.gnetop.ltgame.core.impl.OnRechargeStateListener;
import com.gnetop.ltgame.core.manager.lt.LoginRealizeManager;
import com.gnetop.ltgame.core.model.RechargeResult;
import com.gnetop.ltgame.core.platform.Target;
import com.gnetop.ltgame.core.util.PreferencesUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class GooglePlayHelper implements PurchasesUpdatedListener {
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
    private List<Purchase> mList = new ArrayList<>();

    GooglePlayHelper(Activity activity, int role_number,
                     int server_number, String goods_number, int mPayTest,
                     String sku, OnRechargeStateListener mListener) {
        this.mActivityRef = new WeakReference<>(activity);
        this.role_number = role_number;
        this.server_number = server_number;
        this.goods_number = goods_number;
        this.mSku = sku;
        this.mPayTest = mPayTest;
        this.mRechargeTarget = Target.RECHARGE_GOOGLE;
        this.mListener = mListener;
    }


    /**
     * 初始化
     */
    void init() {
        mBillingClient = BillingClient.newBuilder(mActivityRef.get()).setListener(this)
                .enablePendingPurchases()
                .build();
        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                billingResult.getDebugMessage();
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    mConsume = "1";
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

            @Override
            public void onBillingServiceDisconnected() {
            }
        });
    }


    @Override
    public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> list) {
        if (list != null && list.size() > 0) {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                for (Purchase purchase : list) {
                    mList.add(purchase);
                    uploadToServer(purchase.getPurchaseToken());
                }
            }
        } else {
            switch (billingResult.getResponseCode()) {
                case BillingClient.BillingResponseCode.SERVICE_TIMEOUT: {//服务连接超时
                    mListener.onState(mActivityRef.get(), RechargeResult.failOf("-3"));
                    break;
                }
                case BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED: {
                    mListener.onState(mActivityRef.get(), RechargeResult.failOf("-2"));
                    break;
                }
                case BillingClient.BillingResponseCode.SERVICE_DISCONNECTED: {//服务未连接
                    mListener.onState(mActivityRef.get(), RechargeResult.failOf("-1"));
                    break;
                }
                case BillingClient.BillingResponseCode.USER_CANCELED: {//取消
                    mListener.onState(mActivityRef.get(), RechargeResult.failOf("1"));
                    break;
                }
                case BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE: {//服务不可用
                    mListener.onState(mActivityRef.get(), RechargeResult.failOf("2"));
                    break;
                }
                case BillingClient.BillingResponseCode.BILLING_UNAVAILABLE: {//购买不可用
                    mListener.onState(mActivityRef.get(), RechargeResult.failOf("3"));
                    break;
                }
                case BillingClient.BillingResponseCode.ITEM_UNAVAILABLE: {//商品不存在
                    mListener.onState(mActivityRef.get(), RechargeResult.failOf("4"));
                    break;
                }
                case BillingClient.BillingResponseCode.DEVELOPER_ERROR: {//提供给 API 的无效参数
                    mListener.onState(mActivityRef.get(), RechargeResult.failOf("5"));
                    break;
                }
                case BillingClient.BillingResponseCode.ERROR: {//错误
                    mListener.onState(mActivityRef.get(), RechargeResult.failOf("6"));
                    break;
                }
                case BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED: {//未消耗掉
                    queryHistory();
                    break;
                }
                case BillingClient.BillingResponseCode.ITEM_NOT_OWNED: {//不可购买
                    mListener.onState(mActivityRef.get(), RechargeResult.failOf("8"));
                    break;
                }
            }
        }


    }

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
    private void consume(String purchaseToken, final RechargeResult result) {
        ConsumeParams consumeParams =
                ConsumeParams.newBuilder()
                        .setPurchaseToken(purchaseToken)
                        .build();
        mBillingClient.consumeAsync(consumeParams, new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(BillingResult billingResult, String s) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    mListener.onState(mActivityRef.get(), RechargeResult.successOf(result.getResultModel()));
                } else {
                    if (mList != null) {
                        for (int i = 0; i < mList.size(); i++) {
                            consume2(mList.get(i).getPurchaseToken());
                        }
                    }
                }
            }
        });

    }

    /**
     * 消耗
     */
    private void consume2(String purchaseToken) {
        ConsumeParams consumeParams =
                ConsumeParams.newBuilder()
                        .setPurchaseToken(purchaseToken)
                        .build();
        mBillingClient.consumeAsync(consumeParams, new ConsumeResponseListener() {

            @Override
            public void onConsumeResponse(BillingResult billingResult, String s) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    if (mConsume.equals("1")) {
                        recharge();
                    } else {
                        mActivityRef.get().finish();
                    }

                    if (mList != null && mList.size() > 0) {
                        mList.clear();
                    }
                }
            }
        });

    }

    /**
     * 补单操作
     */
    private void queryHistory() {
        mBillingClient.queryPurchaseHistoryAsync(BillingClient.SkuType.INAPP,
                new PurchaseHistoryResponseListener() {
                    @Override
                    public void onPurchaseHistoryResponse(BillingResult billingResult, List<PurchaseHistoryRecord> list) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                                && list != null) {
                            for (final PurchaseHistoryRecord purchase : list) {
                                consume2(purchase.getPurchaseToken());
                            }
                        } else {
                            mActivityRef.get().finish();
                        }
                    }

                });


    }

    /**
     * 获取乐推订单ID
     */
    private void getLTOrderID() {
        LoginRealizeManager.createOrder(mActivityRef.get(), role_number, server_number, goods_number,
                new OnRechargeStateListener() {

                    @Override
                    public void onState(Activity activity, RechargeResult result) {
                        if (result != null) {
                            if (result.getResultModel() != null) {
                                if (result.getResultModel().getData() != null) {
                                    if (result.getResultModel().getCode() == 0) {
                                        if (result.getResultModel().getData().getOrder_number() != null) {
                                            mOrderID = result.getResultModel().getData().getOrder_number();
                                            recharge();
                                        }
                                    } else {
                                        mListener.onState(mActivityRef.get(),
                                                RechargeResult.failOf(result.getResultModel().getMsg()));
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
    private void uploadToServer(final String purchaseToken) {
        LoginRealizeManager.googlePlay(mActivityRef.get(),
                purchaseToken, mOrderID, mPayTest, new OnRechargeStateListener() {
                    @Override
                    public void onState(Activity activity, RechargeResult result) {
                        if (result != null) {
                            if (result.getResultModel().getCode() == 200) {
                                consume(purchaseToken, result);
                                mConsume = "2";
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
}
