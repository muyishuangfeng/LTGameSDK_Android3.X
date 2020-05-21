package com.gnetop.ltgame.core.impl;

/**
 * 微信获取AccessToken接口
 * @param <T>
 */
public interface OnWeChatAccessTokenListener<T> {

    void onWeChatSuccess(T t);

    void onWeChatFailed(String failed);
}
