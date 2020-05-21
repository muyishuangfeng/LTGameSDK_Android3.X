package com.gnetop.ltgame.core.impl;

import android.app.Activity;

import com.gnetop.ltgame.core.model.RechargeResult;

/**
 * 支付状态接口
 */
public interface OnRechargeStateListener {

    void onState(Activity activity, RechargeResult result);
}
