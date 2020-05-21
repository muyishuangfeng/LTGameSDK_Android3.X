package com.gnetop.ltgame.core.model;

import java.util.Map;

/**
 * 登录参数类
 */
public class RechargeObject {
    //乐推AppID
    private String LTAppID;
    //乐推AppKey
    private String LTAppKey;
    //包名
    private String mPackageID;
    //唯一标识
    private String mAdID;
    //baseUrl
    private String baseUrl;
    private String fbAppID;
    //商品
    private String sku;
    //自定义参数
    private Map<String, Object> params;
    //oneStore公钥
    private String mOnePublicKey;
    //google公钥
    private String mGPPublicKey;
    //是否是沙盒登录
    private int payTest;
    //商品ID
    private String goodsID;
    //商品类型
    private String mGoodsType;
    //支付类型
    private String rechargeType;
    //统计
    private boolean mStats;
    private int role_number;//  角色编号（游戏服务器用户ID）
    private int server_number;// 服务器编号（游戏提供）
    private String goods_number;//  商品ID，游戏提供

    public String getFbAppID() {
        return fbAppID;
    }

    public void setFbAppID(String fbAppID) {
        this.fbAppID = fbAppID;
    }

    public String getRechargeType() {
        return rechargeType;
    }

    public void setRechargeType(String rechargeType) {
        this.rechargeType = rechargeType;
    }

    public int getRole_number() {
        return role_number;
    }

    public void setRole_number(int role_number) {
        this.role_number = role_number;
    }

    public int getServer_number() {
        return server_number;
    }

    public void setServer_number(int server_number) {
        this.server_number = server_number;
    }

    public String getGoods_number() {
        return goods_number;
    }

    public void setGoods_number(String goods_number) {
        this.goods_number = goods_number;
    }

    public String getLTAppID() {
        return LTAppID;
    }

    public void setLTAppID(String LTAppID) {
        this.LTAppID = LTAppID;
    }

    public String getLTAppKey() {
        return LTAppKey;
    }

    public void setLTAppKey(String LTAppKey) {
        this.LTAppKey = LTAppKey;
    }

    public String getPackageID() {
        return mPackageID;
    }

    public void setPackageID(String mPackageID) {
        this.mPackageID = mPackageID;
    }

    public String getAdID() {
        return mAdID;
    }

    public void setAdID(String mAdID) {
        this.mAdID = mAdID;
    }




    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public String getOnePublicKey() {
        return mOnePublicKey;
    }

    public void setOnePublicKey(String mOnePublicKey) {
        this.mOnePublicKey = mOnePublicKey;
    }

    public String getGPPublicKey() {
        return mGPPublicKey;
    }

    public void setGPPublicKey(String mGPPublicKey) {
        this.mGPPublicKey = mGPPublicKey;
    }

    public int getPayTest() {
        return payTest;
    }

    public void setPayTest(int payTest) {
        this.payTest = payTest;
    }

    public String getGoodsID() {
        return goodsID;
    }

    public void setGoodsID(String goodsID) {
        this.goodsID = goodsID;
    }

    public String getGoodsType() {
        return mGoodsType;
    }

    public void setGoodsType(String mGoodsType) {
        this.mGoodsType = mGoodsType;
    }

    public boolean getStats() {
        return mStats;
    }

    public void setStats(boolean mStats) {
        this.mStats = mStats;
    }
}
