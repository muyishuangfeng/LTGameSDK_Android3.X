package com.gnetop.ltgame.core.manager.recharge.gp;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.gnetop.ltgame.core.base.BaseEntry;
import com.gnetop.ltgame.core.common.Constants;
import com.gnetop.ltgame.core.exception.LTGameError;
import com.gnetop.ltgame.core.exception.LTResultCode;
import com.gnetop.ltgame.core.impl.OnRechargeStateListener;
import com.gnetop.ltgame.core.manager.login.fb.FacebookEventManager;
import com.gnetop.ltgame.core.manager.lt.LoginRealizeManager;
import com.gnetop.ltgame.core.model.GoogleModel;
import com.gnetop.ltgame.core.model.RechargeResult;
import com.gnetop.ltgame.core.model.ResultModel;
import com.gnetop.ltgame.core.platform.Target;
import com.gnetop.ltgame.core.util.PreferencesUtils;
import com.gnetop.ltgame.core.util.gp.IabHelper;
import com.gnetop.ltgame.core.util.gp.IabResult;
import com.gnetop.ltgame.core.util.gp.Inventory;
import com.gnetop.ltgame.core.util.gp.Purchase;
import com.google.gson.Gson;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class GooglePlayHelper {

    private static IabHelper mHelper;
    private static boolean mSetupDone = false;
    //订单号
    private String mOrderID;
    //商品集合
    private List<String> mGoodsList = new ArrayList<>();
    private WeakReference<Activity> mActivityRef;
    //公钥
    private String mPublicKey;
    private int mRechargeTarget;
    private OnRechargeStateListener mListener;
    //商品
    private String mSku;
    private int role_number;//  角色编号（游戏服务器用户ID）
    private int server_number;// 服务器编号（游戏提供）
    private String goods_number;//  商品ID，游戏提供
    //是否是沙盒账号
    private int mPayTest;


    GooglePlayHelper(Activity activity, String mPublicKey, int role_number,
                     int server_number, String goods_number, int mPayTest,
                     String sku, OnRechargeStateListener mListener) {
        this.mActivityRef = new WeakReference<>(activity);
        this.mPublicKey = mPublicKey;
        this.role_number = role_number;
        this.server_number = server_number;
        this.goods_number = goods_number;
        this.mSku = sku;
        this.mPayTest = mPayTest;
        this.mRechargeTarget = Target.RECHARGE_GOOGLE;
        this.mListener = mListener;
    }

    public GooglePlayHelper(Activity activity, String mPublicKey) {
        this.mActivityRef = new WeakReference<>(activity);
        this.mPublicKey = mPublicKey;
        this.mRechargeTarget = Target.RECHARGE_GOOGLE;
    }


    /**
     * 补单操作
     */
    public void queryOrder() {
        //创建谷歌帮助类
        mHelper = new IabHelper(mActivityRef.get(), mPublicKey);
        mHelper.enableDebugLogging(true);
        if (mHelper != null) {
            mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                @Override
                public void onIabSetupFinished(IabResult result) {
                    if (result.isSuccess()) {
                        try {
                            mHelper.queryInventoryAsync(true, null, null,
                                    new IabHelper.QueryInventoryFinishedListener() {
                                        @Override
                                        public void onQueryInventoryFinished(IabResult result, Inventory inv) {
                                            if (result.isSuccess()) {
                                                if (inv.getAllPurchases() != null) {
                                                    if (inv.getAllPurchases().size() > 0) {
                                                        for (int i = 0; i < inv.getAllPurchases().size(); i++) {
                                                            if (inv.getAllPurchases().get(i).getToken() != null && inv.getAllPurchases().get(i).getDeveloperPayload() != null) {
                                                                uploadToServer3(inv.getAllPurchases().get(i), inv.getAllPurchases().get(i).getToken(), inv.getAllPurchases().get(i).getDeveloperPayload());
                                                            }

                                                        }
                                                    }

                                                }

                                            }
                                        }

                                    });
                        } catch (IabHelper.IabAsyncInProgressException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }


    /**
     * 初始化
     */
    void init() {
        //创建谷歌帮助类
        mHelper = new IabHelper(mActivityRef.get(), mPublicKey);
        mHelper.enableDebugLogging(true);
        if (mHelper != null) {
            mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                @Override
                public void onIabSetupFinished(IabResult result) {
                    if (!result.isSuccess()) {
                        mSetupDone = false;
                    } else {
                        mSetupDone = true;
                        try {
                            mHelper.queryInventoryAsync(true, null, null,
                                    new IabHelper.QueryInventoryFinishedListener() {
                                        @Override
                                        public void onQueryInventoryFinished(IabResult result, Inventory inv) {
                                            if (result.isSuccess()) {
                                                if (inv.getAllPurchases() != null) {
                                                    if (inv.getAllPurchases().size() > 0) {
                                                        mGoodsList = getGoodsList(inv.getAllPurchases());
                                                        for (int i = 0; i < inv.getAllPurchases().size(); i++) {
                                                            consumeProduct(inv.getAllPurchases().get(i));
                                                        }
                                                    } else {
                                                        recharge();
                                                    }

                                                }

                                            }
                                        }

                                    });
                        } catch (IabHelper.IabAsyncInProgressException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    /**
     * 消费掉商品
     */
    private void consumeProduct(Purchase purchase) {
        try {
            mHelper.consumeAsync(purchase, new IabHelper.OnConsumeFinishedListener() {
                @Override
                public void onConsumeFinished(Purchase purchase, IabResult result) {
                    if (purchase.getToken() != null && purchase.getDeveloperPayload() != null) {
                        uploadToServer2(purchase.getToken(), purchase.getDeveloperPayload());
                    }
                }
            });
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }

    /**
     * 消费掉商品
     */
    private void consumeProduct2(Purchase purchase) {
        try {
            mHelper.consumeAsync(purchase, new IabHelper.OnConsumeFinishedListener() {
                @Override
                public void onConsumeFinished(Purchase purchase, IabResult result) {
                }
            });
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }

    /**
     * 购买
     */
    private void recharge() {
        if (mSetupDone) {
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
        } else {
            if (!TextUtils.isEmpty(mPublicKey)) {
                //创建谷歌帮助类
                mHelper = new IabHelper(mActivityRef.get(), mPublicKey);
                mHelper.enableDebugLogging(true);
                mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                    @Override
                    public void onIabSetupFinished(IabResult result) {
                        if (result.isFailure()) {
                            mSetupDone = false;
                        }
                        if (result.isSuccess()) {
                            mSetupDone = true;
                        }
                    }
                });
            }

        }
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
                                            try {
                                                if (mHelper == null) return;
                                                mHelper.queryInventoryAsync(true, mGoodsList, mGoodsList,
                                                        new IabHelper.QueryInventoryFinishedListener() {
                                                            @Override
                                                            public void onQueryInventoryFinished(IabResult result, Inventory inv) {
                                                                if (result != null) {
                                                                    if (result.isSuccess() && inv.hasPurchase(mSku)) {
                                                                        //消费, 并下一步, 这里Demo里面我没做提示,将购买了,但是没消费掉的商品直接消费掉, 正常应该
                                                                        //给用户一个提示,存在未完成的支付订单,是否完成支付
                                                                        consumeProduct(inv.getPurchase(mSku));
                                                                    } else {
                                                                        getProduct(LTResultCode.GP_SELF_REQUEST_CODE, mSku);
                                                                    }
                                                                }
                                                            }

                                                        });
                                            } catch (IabHelper.IabAsyncInProgressException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    } else {
                                        mListener.onState(mActivityRef.get(), RechargeResult.failOf(result.getResultModel().getMsg()));
                                        mActivityRef.get().finish();
                                    }

                                }

                            }
                        }
                    }

                });
    }


    /**
     * 产品获取
     *
     * @param REQUEST_CODE 请求码
     * @param SKU          产品唯一id, 填写你自己添加的商品id
     */
    private void getProduct(int REQUEST_CODE, final String SKU) {
        if (!TextUtils.isEmpty(mOrderID)) {
            try {
                mHelper.launchPurchaseFlow(mActivityRef.get(), SKU, REQUEST_CODE, new IabHelper.OnIabPurchaseFinishedListener() {
                    @Override
                    public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
                        if (result.isFailure()) {
                            return;
                        }
                        if (purchase.getSku().equals(SKU)) {
                            //购买成功，调用消耗
                            consumeProduct(purchase);
                        }
                    }
                }, mOrderID);
            } catch (IabHelper.IabAsyncInProgressException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 回调
     *
     * @param requestCode 请求码
     * @param resultCode  结果码
     * @param data        数据
     */
    void onActivityResult(int requestCode, int resultCode, Intent data) {
        //将回调交给帮助类来处理, 否则会出现支付正在进行的错误
        if (mHelper == null) return;
        mHelper.handleActivityResult(requestCode, resultCode, data);
        if (requestCode == LTResultCode.GP_SELF_REQUEST_CODE) {
            int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
            //订单信息
            String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
            String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");
            switch (responseCode) {
                case 0: {//成功
                    if (!TextUtils.isEmpty(purchaseData)) {
                        GoogleModel googleModel = new Gson().fromJson(purchaseData, GoogleModel.class);
                        Map<String, Object> params = new WeakHashMap<>();
                        params.put("purchase_token", googleModel.getPurchaseToken());
                        params.put("lt_order_id", mOrderID);
                        uploadToServer(googleModel.getPurchaseToken(), mSku);
                    }
                }
                break;
                case 1: {//取消
                    mListener.onState(mActivityRef.get(), RechargeResult.failOf(
                            LTResultCode.STATE_GP_RESPONSE_RESULT_USER_CANCELED, "GP Cancel"));
                }
                break;
                case 2: {//网络异常
                    mListener.onState(mActivityRef.get(), RechargeResult.failOf(
                            LTResultCode.STATE_GP_RESPONSE_RESULT_SERVICE_UNAVAILABLE, "GP NetWork Error"));
                }
                break;
                case 3: {//不支持购买
                    mListener.onState(mActivityRef.get(), RechargeResult.failOf(
                            LTResultCode.STATE_GP_RESPONSE_RESULT_BILLING_UNAVAILABLE, "GP Billing UnAvailable"));
                }
                break;
                case 4: {//商品不可购买
                    mListener.onState(mActivityRef.get(), RechargeResult.failOf(
                            LTResultCode.STATE_GP_RESPONSE_RESULT_ITEM_UNAVAILABLE, "GP Item UnAvailable"));
                }
                break;
                case 5: {//提供给 API 的无效参数
                    mListener.onState(mActivityRef.get(), RechargeResult.failOf(
                            LTResultCode.STATE_GP_RESPONSE_RESULT_DEVELOPER_ERROR, "GP Developer Error"));
                }
                break;
                case 6: {//错误
                    mListener.onState(mActivityRef.get(), RechargeResult.failOf(
                            LTResultCode.STATE_GP_RESPONSE_RESULT_ERROR, "GP Error"));
                }
                break;
                case 7: {//未消耗掉
                    mListener.onState(mActivityRef.get(), RechargeResult.failOf(
                            LTResultCode.STATE_GP_RESPONSE_RESULT_ITEM_ALREADY_OWNED, "GP Item Already owen"));
                }
                break;
                case 8: {//不可购买
                    mListener.onState(mActivityRef.get(), RechargeResult.failOf(
                            LTResultCode.STATE_GP_RESPONSE_RESULT_ITEM_NOT_OWNED, "GP Item Not Owen"));
                }
                break;
            }
        }

    }

    /**
     * 上传到服务器验证
     */
    private void uploadToServer(final String purchaseToken, final String productID) {
        LoginRealizeManager.googlePlay(mActivityRef.get(),
                purchaseToken, mOrderID, mPayTest, new OnRechargeStateListener() {

                    @Override
                    public void onState(Activity activity, RechargeResult result) {
                        if (result != null) {
                            if (result.getResultModel() != null) {
                                if (result.getResultModel().getCode() == 0) {
                                    FacebookEventManager.getInstance().recharge(activity,
                                            result.getResultModel().getData().getGoods_price(),
                                            result.getResultModel().getData().getGoods_price_type(),
                                            result.getResultModel().getData().getOrder_number());
                                    BaseEntry<ResultModel> entry = new BaseEntry<>();
                                    ResultModel resultModel = new ResultModel();
                                    resultModel.setOrder_number(result.getResultModel().getData().getOrder_number());
                                    resultModel.setGoods_price_type(result.getResultModel().getData().getGoods_price_type());
                                    resultModel.setGoods_price(result.getResultModel().getData().getGoods_price());
                                    entry.setData(resultModel);
                                    mListener.onState(mActivityRef.get(), RechargeResult.successOf(entry));
                                    if (mHelper == null) {
                                        mHelper = new IabHelper(mActivityRef.get(), mPublicKey);
                                        mHelper.enableDebugLogging(true);
                                        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                                            @Override
                                            public void onIabSetupFinished(IabResult result) {
                                                if (result.isSuccess()) {
                                                    try {
                                                        mHelper.queryInventoryAsync(true, mGoodsList, mGoodsList,
                                                                new IabHelper.QueryInventoryFinishedListener() {
                                                                    @Override
                                                                    public void onQueryInventoryFinished(IabResult result, Inventory inv) {
                                                                        if (result != null) {
                                                                            if (result.isSuccess() && inv.hasPurchase(productID)) {
                                                                                //消费, 并下一步, 这里Demo里面我没做提示,将购买了,但是没消费掉的商品直接消费掉, 正常应该
                                                                                //给用户一个提示,存在未完成的支付订单,是否完成支付
                                                                                consumeProduct2(inv.getPurchase(productID));
                                                                            }
                                                                        }
                                                                    }

                                                                });
                                                    } catch (IabHelper.IabAsyncInProgressException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                        });
                                    }

                                }
                            }

                        }

                    }

                });
    }

    /**
     * 补单
     */
    private void uploadToServer2(final String purchaseToken, final String productID) {
        LoginRealizeManager.googlePlay(mActivityRef.get(),
                purchaseToken, mOrderID, mPayTest, new OnRechargeStateListener() {

                    @Override
                    public void onState(Activity activity, RechargeResult result) {
                        if (result != null) {
                            if (result.getResultModel().getCode() == 0) {
                                recharge();
                            }
                        }

                    }

                });
    }

    /**
     * 释放资源
     */
    void release() {
        if (mHelper != null) {
            mHelper.disposeWhenFinished();
        }
        mHelper = null;
    }

    /**
     * 获取商品集合
     */
    private List<String> getGoodsList(
            List<Purchase> mList) {
        mGoodsList = new ArrayList<>();
        for (int i = 0; i < mList.size(); i++) {
            mGoodsList.add(mList.get(i).getSku());
        }
        return mGoodsList;
    }


    /**
     * 上传到服务器验证
     */
    private void uploadToServer3(final Purchase purchase, final String purchaseToken, String mOrderID) {
        LoginRealizeManager.googlePlay(mActivityRef.get(),
                purchaseToken, mOrderID, mPayTest, new OnRechargeStateListener() {
                    @Override
                    public void onState(Activity activity, RechargeResult result) {
                        if (result != null) {
                            if (result.getResultModel().getCode() == 200) {
                                consumeProduct2(purchase);
                            }
                        }

                    }

                });
    }
}
