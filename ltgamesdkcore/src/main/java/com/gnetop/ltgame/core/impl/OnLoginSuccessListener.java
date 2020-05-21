package com.gnetop.ltgame.core.impl;


import com.gnetop.ltgame.core.base.BaseEntry;

public interface OnLoginSuccessListener<T> {

    void onSuccess(BaseEntry<T> result);

    void onFailed(BaseEntry<T> failed);
}
