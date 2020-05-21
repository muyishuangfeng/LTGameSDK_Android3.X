package com.gnetop.ltgame.core.impl;


import com.gnetop.ltgame.core.exception.LTGameError;

/**
 * 自动验证登录接口
 */
public interface OnAutoCheckLoginListener {

    void onCheckedSuccess(String result);

    void onCheckedFailed(String failed);

    void onCheckedException(LTGameError ex);
}
