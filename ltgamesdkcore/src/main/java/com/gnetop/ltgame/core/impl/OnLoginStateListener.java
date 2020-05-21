package com.gnetop.ltgame.core.impl;

import android.app.Activity;

import com.gnetop.ltgame.core.model.LoginResult;

/**
 * 登录状态接口
 */
public interface OnLoginStateListener {

    void onState(Activity activity, LoginResult result);

    void onLoginOut();

}
