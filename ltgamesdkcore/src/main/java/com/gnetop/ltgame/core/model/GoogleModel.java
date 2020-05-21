package com.gnetop.ltgame.core.model;

public class GoogleModel {

    /**
     * orderId : GPA.3382-6146-8197-43864
     * packageName : com.gnetop.ltgameproject
     * productId : kr.ltgames.10usd
     * purchaseTime : 1547635927364
     * purchaseState : 0
     * developerPayload : LTDD3B3989D89AA7A97A05AEA04D802724
     * purchaseToken : gnlcmnmofjacmdcmgmmompmm.AO-J1OzymU5G2VZJSlpvUvTYtWOx0CKhRAym7AtXsGrAykHN4YyGZ6hIW3gof_XzZoUPy5XqsYS4W5svOZpBszgvcYs9QldAejcwD6iYgG9o964MoCNw8BrC2gSQNgoE6TR0-hsd5S2A
     */

    private String orderId;
    private String packageName;
    private String productId;
    private long purchaseTime;
    private int purchaseState;
    private String developerPayload;
    private String purchaseToken;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public long getPurchaseTime() {
        return purchaseTime;
    }

    public void setPurchaseTime(long purchaseTime) {
        this.purchaseTime = purchaseTime;
    }

    public int getPurchaseState() {
        return purchaseState;
    }

    public void setPurchaseState(int purchaseState) {
        this.purchaseState = purchaseState;
    }

    public String getDeveloperPayload() {
        return developerPayload;
    }

    public void setDeveloperPayload(String developerPayload) {
        this.developerPayload = developerPayload;
    }

    public String getPurchaseToken() {
        return purchaseToken;
    }

    public void setPurchaseToken(String purchaseToken) {
        this.purchaseToken = purchaseToken;
    }
}
